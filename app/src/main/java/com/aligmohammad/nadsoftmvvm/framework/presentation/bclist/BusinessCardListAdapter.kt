package com.aligmohammad.nadsoftmvvm.framework.presentation.bclist

import android.view.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.aligmohammad.nadsoftmvvm.R
import com.aligmohammad.nadsoftmvvm.business.domain.model.BusinessCard
import com.aligmohammad.nadsoftmvvm.business.domain.util.DateUtil
import com.aligmohammad.nadsoftmvvm.framework.presentation.common.changeColor
import com.aligmohammad.nadsoftmvvm.util.printLogD
import kotlinx.android.synthetic.main.layout_business_card_list_item.view.*
import java.lang.IndexOutOfBoundsException


class BusinessCardListAdapter(
    private val interaction: Interaction? = null,
    private val lifecycleOwner: LifecycleOwner,
    private val selectedBusinessCards: LiveData<ArrayList<BusinessCard>>,
    private val dateUtil: DateUtil
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BusinessCard>() {

        override fun areItemsTheSame(oldItem: BusinessCard, newItem: BusinessCard): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BusinessCard, newItem: BusinessCard): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return BusinessCardViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_business_card_list_item,
                parent,
                false
            ),
            interaction,
            lifecycleOwner,
            selectedBusinessCards,
            dateUtil
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BusinessCardViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<BusinessCard>) {
        val commitCallback = Runnable {
            // if process died must restore list position
            // very annoying
            interaction?.restoreListPosition()
        }
        printLogD("listadapter", "size: ${list.size}")
        differ.submitList(list, commitCallback)
    }

    fun getBusinessCard(index: Int): BusinessCard? {
        return try{
            differ.currentList[index]
        }catch (e: IndexOutOfBoundsException){
            e.printStackTrace()
            null
        }
    }

    class BusinessCardViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?,
        private val lifecycleOwner: LifecycleOwner,
        private val selectedBusinessCards: LiveData<ArrayList<BusinessCard>>,
        private val dateUtil: DateUtil
    ) : RecyclerView.ViewHolder(itemView)
    {


        private val COLOR_GREY = R.color.app_background_color
        private val COLOR_PRIMARY = R.color.colorPrimary
        private lateinit var businesscard: BusinessCard

        fun bind(item: BusinessCard) = with(itemView) {
            setOnClickListener {
                interaction?.onItemSelected(adapterPosition, businesscard)
            }
            setOnLongClickListener {
                interaction?.activateMultiSelectionMode()
                interaction?.onItemSelected(adapterPosition, businesscard)
                true
            }
            businesscard = item
            businesscard_title.text = item.title
            businesscard_timestamp.text = dateUtil.removeTimeFromDateString(item.updated_at)

            selectedBusinessCards.observe(lifecycleOwner, Observer { businesscards ->

                if(businesscards != null){
                    if(businesscards.contains(businesscard)){
                        changeColor(
                            newColor = COLOR_GREY
                        )
                    }
                    else{
                        changeColor(
                            newColor = COLOR_PRIMARY
                        )
                    }
                }else{
                    changeColor(
                        newColor = COLOR_PRIMARY
                    )
                }
            })
        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: BusinessCard)

        fun restoreListPosition()

        fun isMultiSelectionModeEnabled(): Boolean

        fun activateMultiSelectionMode()

        fun isBusinessCardSelected(businesscard: BusinessCard): Boolean
    }

}













