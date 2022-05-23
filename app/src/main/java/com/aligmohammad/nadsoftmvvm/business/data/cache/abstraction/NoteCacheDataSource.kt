import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard

interface BusinessCardCacheDataSource{

    suspend fun insertBusinessCard(businesscard: BusinessCard): Long

    suspend fun deleteBusinessCard(primaryKey: String): Int

    suspend fun deleteBusinessCards(businesscards: List<BusinessCard>): Int

    suspend fun updateBusinessCard(
        primaryKey: String,
        newTitle: String,
        newBody: String?,
        timestamp: String?
    ): Int

    suspend fun searchBusinessCards(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<BusinessCard>

    suspend fun getAllBusinessCards(): List<BusinessCard>

    suspend fun searchBusinessCardById(id: String): BusinessCard?

    suspend fun getNumBusinessCards(): Int

    suspend fun insertBusinessCards(businesscards: List<BusinessCard>): LongArray
}






