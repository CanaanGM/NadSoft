package com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database

import androidx.room.*
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.model.BusinessCardCacheEntity

const val BUSINESS_CARD_ORDER_ASC: String = ""
const val BUSINESS_CARD_ORDER_DESC: String = "-"
const val BUSINESS_CARD_FILTER_TITLE = "title"
const val BUSINESS_CARD_FILTER_DATE_CREATED = "created_at"

const val ORDER_BY_ASC_DATE_UPDATED = BUSINESS_CARD_ORDER_ASC + BUSINESS_CARD_FILTER_DATE_CREATED
const val ORDER_BY_DESC_DATE_UPDATED = BUSINESS_CARD_ORDER_DESC + BUSINESS_CARD_FILTER_DATE_CREATED
const val ORDER_BY_ASC_TITLE = BUSINESS_CARD_ORDER_ASC + BUSINESS_CARD_FILTER_TITLE
const val ORDER_BY_DESC_TITLE = BUSINESS_CARD_ORDER_DESC + BUSINESS_CARD_FILTER_TITLE

const val BUSINESS_CARD_PAGINATION_PAGE_SIZE = 30

@Dao
interface BusinessCardDao {

    @Insert
    suspend fun insertBusinessCard(businesscard: BusinessCardCacheEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBusinessCards(businesscards: List<BusinessCardCacheEntity>): LongArray

    @Query("SELECT * FROM businesscards WHERE id = :id")
    suspend fun searchBusinessCardById(id: String): BusinessCardCacheEntity?

    @Query("DELETE FROM businesscards WHERE id IN (:ids)")
    suspend fun deleteBusinessCards(ids: List<String>): Int

    @Query("DELETE FROM businesscards")
    suspend fun deleteAllBusinessCards()

    @Query("SELECT * FROM businesscards")
    suspend fun getAllBusinessCards(): List<BusinessCardCacheEntity>

    @Query("""
        UPDATE businesscards 
        SET 
        title = :title, 
        body = :body,
        updated_at = :updated_at
        WHERE id = :primaryKey
        """)
    suspend fun updateBusinessCard(
        primaryKey: String,
        title: String,
        body: String?,
        updated_at: String
    ): Int

    @Query("DELETE FROM businesscards WHERE id = :primaryKey")
    suspend fun deleteBusinessCard(primaryKey: String): Int

    @Query("SELECT * FROM businesscards")
    suspend fun searchBusinessCards(): List<BusinessCardCacheEntity>

    @Query("""
        SELECT * FROM businesscards 
        WHERE title LIKE '%' || :query || '%' 
        OR body LIKE '%' || :query || '%' 
        ORDER BY updated_at DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchBusinessCardsOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int = BUSINESS_CARD_PAGINATION_PAGE_SIZE
    ): List<BusinessCardCacheEntity>

    @Query("""
        SELECT * FROM businesscards 
        WHERE title LIKE '%' || :query || '%' 
        OR body LIKE '%' || :query || '%' 
        ORDER BY updated_at ASC LIMIT (:page * :pageSize)
        """)
    suspend fun searchBusinessCardsOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int = BUSINESS_CARD_PAGINATION_PAGE_SIZE
    ): List<BusinessCardCacheEntity>

    @Query("""
        SELECT * FROM businesscards 
        WHERE title LIKE '%' || :query || '%' 
        OR body LIKE '%' || :query || '%' 
        ORDER BY title DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchBusinessCardsOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int = BUSINESS_CARD_PAGINATION_PAGE_SIZE
    ): List<BusinessCardCacheEntity>

    @Query("""
        SELECT * FROM businesscards 
        WHERE title LIKE '%' || :query || '%' 
        OR body LIKE '%' || :query || '%' 
        ORDER BY title ASC LIMIT (:page * :pageSize)
        """)
    suspend fun searchBusinessCardsOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int = BUSINESS_CARD_PAGINATION_PAGE_SIZE
    ): List<BusinessCardCacheEntity>


    @Query("SELECT COUNT(*) FROM businesscards")
    suspend fun getNumBusinessCards(): Int
}


suspend fun BusinessCardDao.returnOrderedQuery(
    query: String,
    filterAndOrder: String,
    page: Int
): List<BusinessCardCacheEntity> {

    when{

        filterAndOrder.contains(ORDER_BY_DESC_DATE_UPDATED) ->{
            return searchBusinessCardsOrderByDateDESC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(ORDER_BY_ASC_DATE_UPDATED) ->{
            return searchBusinessCardsOrderByDateASC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(ORDER_BY_DESC_TITLE) ->{
            return searchBusinessCardsOrderByTitleDESC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(ORDER_BY_ASC_TITLE) ->{
            return searchBusinessCardsOrderByTitleASC(
                query = query,
                page = page)
        }
        else ->
            return searchBusinessCardsOrderByDateDESC(
                query = query,
                page = page
            )
    }
}












