import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.abstraction.BusinessCardDaoService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusinessCardCacheDataSourceImpl
@Inject
constructor(
    private val businesscardDaoService: BusinessCardDaoService
) : BusinessCardCacheDataSource {

    override suspend fun insertBusinessCard(businesscard: BusinessCard): Long {
        return businesscardDaoService.insertBusinessCard(businesscard)
    }

    override suspend fun deleteBusinessCard(primaryKey: String): Int {
        return businesscardDaoService.deleteBusinessCard(primaryKey)
    }

    override suspend fun deleteBusinessCards(businesscards: List<BusinessCard>): Int {
        return businesscardDaoService.deleteBusinessCards(businesscards)
    }

    override suspend fun updateBusinessCard(
        primaryKey: String,
        newTitle: String,
        newBody: String?,
        timestamp: String?
    ): Int {
        return businesscardDaoService.updateBusinessCard(
            primaryKey,
            newTitle,
            newBody,
            timestamp
        )
    }

    override suspend fun searchBusinessCards(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<BusinessCard> {
        return businesscardDaoService.returnOrderedQuery(
            query, filterAndOrder, page
        )
    }

    override suspend fun getAllBusinessCards(): List<BusinessCard> {
        return businesscardDaoService.getAllBusinessCards()
    }

    override suspend fun searchBusinessCardById(id: String): BusinessCard? {
        return businesscardDaoService.searchBusinessCardById(id)
    }

    override suspend fun getNumBusinessCards(): Int {
        return businesscardDaoService.getNumBusinessCards()
    }

    override suspend fun insertBusinessCards(businesscards: List<BusinessCard>): LongArray {
        return businesscardDaoService.insertBusinessCards(businesscards)
    }
}





















