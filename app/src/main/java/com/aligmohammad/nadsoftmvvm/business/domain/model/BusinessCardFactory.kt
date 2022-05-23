package com.aligmohammad.nadsoftmvvm.business.domain.model

import com.aligmohammad.nadsoftmvvm.business.domain.util.DateUtil
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusinessCardFactory
@Inject
constructor(
    private val dateUtil: DateUtil
) {

    fun createSingleBusinessCard(
        id: String? = null,
        title: String,
        body: String? = null
    ): BusinessCard {
        return BusinessCard(
            id = id ?: UUID.randomUUID().toString(),
            title = title,
            body = body ?: "",
            created_at = dateUtil.getCurrentTimestamp(),
            updated_at = dateUtil.getCurrentTimestamp()
        )
    }

    fun createBusinessCardList(numBusinessCards: Int): List<BusinessCard> {
        val list: ArrayList<BusinessCard> = ArrayList()
        for (i in 0 until numBusinessCards) {
            list.add(
                createSingleBusinessCard(
                    id = UUID.randomUUID().toString(),
                    title = UUID.randomUUID().toString(),
                    body = UUID.randomUUID().toString()
                )
            )
        }
        return list
    }


}









