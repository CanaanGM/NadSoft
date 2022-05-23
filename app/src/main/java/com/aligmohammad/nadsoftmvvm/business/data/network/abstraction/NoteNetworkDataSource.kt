import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard


interface BusinessCardNetworkDataSource {

    suspend fun insertOrUpdateBusinessCard(businesscard: BusinessCard)

    suspend fun deleteBusinessCard(primaryKey: String)

    suspend fun insertDeletedBusinessCard(businesscard: BusinessCard)

    suspend fun insertDeletedBusinessCards(businesscards: List<BusinessCard>)

    suspend fun deleteDeletedBusinessCard(businesscard: BusinessCard)

    suspend fun getDeletedBusinessCards(): List<BusinessCard>

    suspend fun deleteAllBusinessCards()

    suspend fun searchBusinessCard(businesscard: BusinessCard): BusinessCard?

    suspend fun getAllBusinessCards(): List<BusinessCard>

    suspend fun insertOrUpdateBusinessCards(businesscards: List<BusinessCard>)

}
