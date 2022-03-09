package com.info.dashboard

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.info.tradewyse.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_changed_stocks.*
import kotlinx.android.synthetic.main.view_stock_change_cell_list_header.view.*
import kotlinx.android.synthetic.main.view_stock_change_cell_topper.view.*
import java.util.*

class StockChangeDialogFragment : DialogFragment() {

    private lateinit var adapter: StockChangedListAdapter
    private val compositeDisposable = CompositeDisposable()

    private var viewModel: DashboardViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_changed_stocks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        close.setOnClickListener {
            dismiss()
        }
        val layoutManager = GridLayoutManager(requireActivity(), 4, GridLayoutManager.VERTICAL, false)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) {
                    4
                } else {
                    1
                }
            }
        }
        stocksList.layoutManager = layoutManager
        adapter = StockChangedListAdapter(requireActivity())
        stocksList.adapter = adapter
        viewModel?.let {
            compositeDisposable.add(it.stockChangeData.subscribe({ container ->
                if (container.stockChanges != null && !container.stockChanges.isEmpty())
                    adapter.updateItems(container.date, container.stockChanges)
                else
                    adapter.updateItems("NA", container.stockChanges)
            }, { error ->
                Toast.makeText(requireActivity(), "Could not get updated stock information ${error.message}", Toast.LENGTH_LONG).show()
            }))
        }
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null && dialog is Dialog) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            (dialog as Dialog).window?.setBackgroundDrawableResource(android.R.color.transparent)
            (dialog as Dialog).window?.setLayout(width, height)
        }
    }

}

/**
 * Grid adapter for changed stocks list.
 */
class StockChangedListAdapter(val context: Context) : RecyclerView.Adapter<StockChangedCellViewHolder>() {
    data class TextTypeHelper(val type: Int, val text: String)
    companion object {
        const val TOPPER = 0
        const val HEADER = 1
        const val STOCK_CELL = 2
    }

    private var items = mutableListOf<TextTypeHelper>()


    fun updateItems(dateText: String, list: List<StockChangeCell>) {
        val newItems = mutableListOf<TextTypeHelper>()
        if (!dateText.equals("NA")) {
            newItems.add(TextTypeHelper(TOPPER, dateText))
            newItems.add(TextTypeHelper(HEADER, context.getString(R.string.stock).toUpperCase(Locale.getDefault())))
            newItems.add(TextTypeHelper(HEADER, context.getString(R.string.yesterday).toUpperCase(Locale.getDefault())))
            newItems.add(TextTypeHelper(HEADER, context.getString(R.string.today).toUpperCase(Locale.getDefault())))
            newItems.add(TextTypeHelper(HEADER, context.getString(R.string.price).toUpperCase(Locale.getDefault())))
            list.forEach {
                if (it is StockChangeDisplayItem) {
                    newItems.add(TextTypeHelper(STOCK_CELL, it.symbol))
                    newItems.add(TextTypeHelper(STOCK_CELL, it.previousStatus.toUpperCase(Locale.getDefault())))
                    newItems.add(TextTypeHelper(STOCK_CELL, it.todayStatus.toUpperCase(Locale.getDefault())))
                    newItems.add(TextTypeHelper(STOCK_CELL, it.price))
                }
            }
        } else {
            newItems.add(TextTypeHelper(TOPPER, dateText))
        }
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockChangedCellViewHolder {
        return when (viewType) {
            TOPPER -> TopperStockChangedCellViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.view_stock_change_cell_topper, parent, false)
            )
            HEADER -> HeaderStockChangedCellViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.view_stock_change_cell_list_header, parent, false)
            )
            else -> StockContentStockChangedCellViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.view_stock_changed_cell, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: StockChangedCellViewHolder, position: Int) {
        holder.bind(items[position].text)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}

open class StockChangedCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bind(content: String) {}
}

class TopperStockChangedCellViewHolder(itemView: View) : StockChangedCellViewHolder(itemView) {
    override fun bind(content: String) {
        itemView.dateText.text = content
        if (content.equals("NA")) {
            itemView.subHeading.text =
                "There are no AI Tip signal changes for your watchlist currently.\n\n*AI Tips can change from day to day, and you may have clicked on an expired notification.\nNo problem, you’re all caught up now."
        }else{
            itemView.subHeading.text = "See which signals have changed for stocks in your watchlist."
        }
    }

}

class HeaderStockChangedCellViewHolder(itemView: View) : StockChangedCellViewHolder(itemView) {
    override fun bind(content: String) {
        itemView.text.text = content
    }
}

class StockContentStockChangedCellViewHolder(itemView: View) : StockChangedCellViewHolder(itemView) {
    override fun bind(content: String) {
        itemView.text.text = content
    }
}