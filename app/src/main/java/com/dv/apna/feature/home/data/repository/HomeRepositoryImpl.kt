package com.dv.apna.feature.home.data.repository

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
                imageUrl = "https://images.unsplash.com/photo-1595974482597-4b8da8879bc5?w=800&auto=format&fit=crop",
                title = if (isHi) "आज का ताज़ा मंडी भाव 🌾" else "Today's Live Mandi Rates 🌾",
                discountText = if (isHi) "खलीलाबाद व नौगढ़ मंडी रेट देखें" else "Check live mandi rates",
                link = "today_market",
                villageId = villageId
            ),
            BannerModel(
                id = "def_news",
                imageUrl = "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=800&auto=format&fit=crop",
                title = if (isHi) "महराजगंज व क्षेत्र की बड़ी खबरें 📢" else "Maharajganj & Local News 📢",
                discountText = if (isHi) "पल-पल के ताज़ा समाचार पढ़ें" else "Read local news updates",
                link = "news",
                villageId = villageId
            ),
            BannerModel(
                id = "def_business",
                imageUrl = "https://images.unsplash.com/photo-1578575437130-527eed3abbec?w=800&auto=format&fit=crop",
                title = if (isHi) "अपनी दुकान या ट्रैक्टर मुफ्त जोड़ें 🏪" else "Register Shop or Vehicle Free 🏪",
                discountText = if (isHi) "अपने गांव में नए ग्राहक पाएं" else "Reach more local customers",
                link = "add_business",
                villageId = villageId
            ),
            BannerModel(
                id = "def_buyers",
                imageUrl = "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=800&auto=format&fit=crop",
                title = if (isHi) "फसल व सब्जियां सीधे बेचें 🌱" else "Sell Crops & Veggies Directly 🌱",
                discountText = if (isHi) "बिना बिचौलिए खरीदार खोजें" else "Connect with buyers directly",
                link = "local_buyers",
                villageId = villageId
            ),
            BannerModel(
                id = "def_emergency",
                imageUrl = "https://images.unsplash.com/photo-1516549655169-df83a0774514?w=800&auto=format&fit=crop",
                title = if (isHi) "आपातकालीन सेवाएं व हेल्पलाइन 🚨" else "Emergency Helpline & Doctors 🚨",
                discountText = if (isHi) "एम्बुलेंस, पुलिस व डॉक्टर" else "Ambulance, police & doctors",
                link = "health",
                villageId = villageId
            )
        )
    }
}
