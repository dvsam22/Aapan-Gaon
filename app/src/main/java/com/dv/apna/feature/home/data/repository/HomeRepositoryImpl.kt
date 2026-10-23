package com.dv.apna.feature.home.data.repository

import com.dv.apna.R
import com.dv.apna.core.common.Resource
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.feature.home.data.datasource.HomeDataSource
import com.dv.apna.feature.home.data.mapper.toDomain
import com.dv.apna.feature.home.domain.model.BannerModel
import com.dv.apna.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val dataSource: HomeDataSource,
    private val preferenceManager: PreferenceManager
) : HomeRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getBanners(villageId: String): Flow<Resource<List<BannerModel>>> = 
        preferenceManager.languageCode.flatMapLatest { languageCode ->
            flow {
                emit(Resource.Loading())
                try {
                    val currentLang = languageCode ?: "en"
                    val fetched = dataSource.getBanners(villageId).map { it.toDomain(currentLang) }
                    val banners = if (fetched.isNotEmpty()) fetched else getDefaultBanners(currentLang, villageId)
                    emit(Resource.Success(banners))
                } catch (e: Exception) {
                    emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
                }
            }
        }

    private fun getDefaultBanners(languageCode: String, villageId: String): List<BannerModel> {
        val isHi = languageCode == "hi"
        return listOf(
            BannerModel(
                id = "def_mandi",
                imageUrl = "",
                title = if (isHi) "आज का मंडी रेट देखें 🌾" else "Today's Mandi Rates 🌾",
                discountText = if (isHi) "खलीलाबाद व नौगढ़ मंडी" else "Khalilabad & Naugarh Mandi",
                link = "today_market",
                villageId = villageId,
                drawableRes = R.drawable.banner_mandi_rates
            ),
            BannerModel(
                id = "def_news",
                imageUrl = "",
                title = if (isHi) "जिले की ताज़ा खबरें 📢" else "District Breaking News 📢",
                discountText = if (isHi) "महराजगंज व गांव के समाचार" else "Maharajganj & Village Updates",
                link = "news",
                villageId = villageId,
                drawableRes = R.drawable.banner_local_news
            ),
            BannerModel(
                id = "def_business",
                imageUrl = "",
                title = if (isHi) "दुकान व ट्रैक्टर जोड़ें 🏪" else "Register Shop & Tractor 🏪",
                discountText = if (isHi) "अपने गांव में ग्राहक पाएं" else "Get more local customers",
                link = "add_business",
                villageId = villageId,
                drawableRes = R.drawable.banner_register_business
            ),
            BannerModel(
                id = "def_buyers",
                imageUrl = "",
                title = if (isHi) "फसल व सब्जियां बेचें 🌱" else "Sell Crops & Produce 🌱",
                discountText = if (isHi) "सीधे खरीदारों से जुड़ें" else "Connect directly with buyers",
                link = "local_buyers",
                villageId = villageId,
                drawableRes = R.drawable.banner_fresh_produce
            ),
            BannerModel(
                id = "def_emergency",
                imageUrl = "",
                title = if (isHi) "आपातकालीन हेल्पलाइन 🚨" else "Emergency & Helpline 🚨",
                discountText = if (isHi) "एम्बुलेंस, पुलिस व डॉक्टर" else "Ambulance, Police & Hospital",
                link = "health",
                villageId = villageId,
                drawableRes = R.drawable.banner_emergency_health
            )
        )
    }
}
