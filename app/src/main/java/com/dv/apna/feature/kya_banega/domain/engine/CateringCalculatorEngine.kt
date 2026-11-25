package com.dv.apna.feature.kya_banega.domain.engine

import com.dv.apna.feature.kya_banega.domain.model.*
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt

object CateringCalculatorEngine {

    fun calculate(
        guestConfig: GuestConfig,
        selectedDishes: List<CateringDish>,
        isHindi: Boolean = true
    ): CateringCalculationResult {
        val effectiveGuests = guestConfig.effectiveGuests.coerceAtLeast(1f)

        // 1. Calculate dish-wise cooked quantities and individual raw breakdowns
        val dishRequirements = selectedDishes.map { dish ->
            val totalCooked = dish.cookedServingPerPerson * effectiveGuests
            val formattedCooked = formatCookedQuantity(
                amount = totalCooked,
                unit = dish.servingUnit,
                isHindi = isHindi,
                displayUnit = dish.displayUnit(isHindi)
            )

            val rawBreakdown = dish.ingredients.map { ing ->
                val amount = ing.perPersonGrams * effectiveGuests
                val cost = (amount / 1000f) * ing.pricePerKgOrLitre
                CalculatedIngredientItem(
                    name = ing.name,
                    category = ing.category,
                    totalAmount = amount,
                    unit = ing.unit,
                    formattedQuantity = formatRawAmount(amount, ing.unit, isHindi),
                    estimatedCost = cost,
                    nameEnglish = ing.nameEnglish
                )
            }

            CalculatedDishRequirement(
                dish = dish,
                totalCookedAmount = totalCooked,
                formattedCooked = formattedCooked,
                rawBreakdown = rawBreakdown
            )
        }

        // 2. Aggregate raw items across all selected dishes
        val itemMap = mutableMapOf<String, AggregatedItem>()

        for (dish in selectedDishes) {
            for (ing in dish.ingredients) {
                val existing = itemMap[ing.name]
                val amount = ing.perPersonGrams * effectiveGuests
                if (existing != null) {
                    existing.totalAmount += amount
                } else {
                    itemMap[ing.name] = AggregatedItem(
                        name = ing.name,
                        nameEnglish = ing.nameEnglish,
                        category = ing.category,
                        unit = ing.unit,
                        totalAmount = amount,
                        pricePerKg = ing.pricePerKgOrLitre
                    )
                }
            }
        }

        // 3. Group by Grocery Category
        val categoryOrder = listOf(
            IngredientCategory.KIRANA,
            IngredientCategory.SABZI_MANDI,
            IngredientCategory.DAIRY,
            IngredientCategory.MASALA_DRY
        )

        var totalBaseCost = 0f

        val groceryGroups = categoryOrder.map { category ->
            val itemsForCategory = itemMap.values
                .filter { it.category == category }
                .sortedByDescending { it.totalAmount * it.pricePerKg }
                .map { agg ->
                    val roundedAmount = applyMarketRounding(agg.totalAmount, agg.unit)
                    val cost = (roundedAmount / 1000f) * agg.pricePerKg
                    totalBaseCost += cost

                    CalculatedIngredientItem(
                        name = agg.name,
                        category = agg.category,
                        totalAmount = roundedAmount,
                        unit = agg.unit,
                        formattedQuantity = formatRawAmount(roundedAmount, agg.unit, isHindi),
                        estimatedCost = cost,
                        nameEnglish = agg.nameEnglish
                    )
                }

            val categoryCost = itemsForCategory.sumOf { it.estimatedCost.toDouble() }.toFloat()

            GroceryCategoryGroup(
                category = category,
                items = itemsForCategory,
                totalCategoryCost = categoryCost
            )
        }.filter { it.items.isNotEmpty() }

        // 4. Compute realistic budget estimates (market range)
        val minCost = (totalBaseCost * 0.95f).roundToInt().coerceAtLeast(0)
        val maxCost = (totalBaseCost * 1.15f).roundToInt().coerceAtLeast(0)

        val costPerPlateMin = (minCost / effectiveGuests).roundToInt()
        val costPerPlateMax = (maxCost / effectiveGuests).roundToInt()

        return CateringCalculationResult(
            guestConfig = guestConfig,
            selectedDishes = selectedDishes,
            dishRequirements = dishRequirements,
            groceryGroups = groceryGroups,
            estimatedCostMin = minCost,
            estimatedCostMax = maxCost,
            costPerPlateMin = costPerPlateMin,
            costPerPlateMax = costPerPlateMax
        )
    }

    fun formatCookedQuantity(
        amount: Float,
        unit: String,
        isHindi: Boolean = true,
        displayUnit: String = unit
    ): String {
        return when (unit) {
            "ग्राम" -> {
                if (amount >= 1000f) {
                    val kg = amount / 1000f
                    if (isHindi) {
                        String.format(Locale.US, "%.1f कि.ग्रा. तैयार", kg)
                    } else {
                        String.format(Locale.US, "%.1f kg ready", kg)
                    }
                } else {
                    if (isHindi) "${amount.roundToInt()} ग्राम तैयार" else "${amount.roundToInt()} g ready"
                }
            }
            "मिली" -> {
                if (amount >= 1000f) {
                    val ltr = amount / 1000f
                    if (isHindi) {
                        String.format(Locale.US, "%.1f लीटर तैयार", ltr)
                    } else {
                        String.format(Locale.US, "%.1f L ready", ltr)
                    }
                } else {
                    if (isHindi) "${amount.roundToInt()} मि.ली. तैयार" else "${amount.roundToInt()} ml ready"
                }
            }
            else -> {
                "${amount.roundToInt()} $displayUnit"
            }
        }
    }

    fun formatRawAmount(amount: Float, unit: String, isHindi: Boolean = true): String {
        return when (unit) {
            "g" -> {
                if (amount >= 1000f) {
                    val kg = amount / 1000f
                    val kgUnit = if (isHindi) "कि.ग्रा." else "kg"
                    if (kg % 1f == 0f) {
                        "${kg.toInt()} $kgUnit"
                    } else {
                        String.format(Locale.US, "%.1f %s", kg, kgUnit)
                    }
                } else {
                    if (isHindi) "${amount.roundToInt()} ग्राम" else "${amount.roundToInt()} g"
                }
            }
            "ml" -> {
                if (amount >= 1000f) {
                    val ltr = amount / 1000f
                    val ltrUnit = if (isHindi) "लीटर" else "L"
                    if (ltr % 1f == 0f) {
                        "${ltr.toInt()} $ltrUnit"
                    } else {
                        String.format(Locale.US, "%.1f %s", ltr, ltrUnit)
                    }
                } else {
                    if (isHindi) "${amount.roundToInt()} मि.ली." else "${amount.roundToInt()} ml"
                }
            }
            "pcs" -> {
                if (isHindi) "${amount.roundToInt()} पीस" else "${amount.roundToInt()} pcs"
            }
            else -> {
                "${amount.roundToInt()} $unit"
            }
        }
    }

    private fun applyMarketRounding(amount: Float, unit: String): Float {
        return when (unit) {
            "g" -> {
                if (amount < 100f) {
                    ceil(amount / 25.0).toFloat() * 25f
                } else if (amount < 500f) {
                    ceil(amount / 50.0).toFloat() * 50f
                } else if (amount < 2000f) {
                    ceil(amount / 100.0).toFloat() * 100f
                } else {
                    ceil(amount / 250.0).toFloat() * 250f
                }
            }
            "ml" -> {
                if (amount < 500f) {
                    ceil(amount / 50.0).toFloat() * 50f
                } else {
                    ceil(amount / 250.0).toFloat() * 250f
                }
            }
            "pcs" -> {
                ceil(amount)
            }
            else -> amount
        }
    }

    private class AggregatedItem(
        val name: String,
        val nameEnglish: String,
        val category: IngredientCategory,
        val unit: String,
        var totalAmount: Float,
        val pricePerKg: Float
    )
}
