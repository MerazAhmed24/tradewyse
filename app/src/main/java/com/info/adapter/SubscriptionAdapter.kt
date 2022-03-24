package com.info.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.info.commons.Common
import com.info.commons.Constants
import com.info.commons.StringHelper
import com.info.model.Subscription
import com.info.tradewyse.AITradeTipDetailActivity.startActivity
import com.info.tradewyse.CheckoutActivity
import com.info.tradewyse.PaymentSuccessActivity
import com.info.tradewyse.R
import kotlinx.android.synthetic.main.item_subscription.view.*
import java.util.*

class SubscriptionAdapter(var list: ArrayList<Subscription>, var context: Context) :
        RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {

    private var subscriptionListener: SubscriptionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_subscription, parent, false)
        list[0].isSelected = true;
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var name = list[position].subscriptionType
        var price = list[position].subscriptionPrice

        //var priceFloat = price.toInt() / 100

        holder.itemView.txtSubscriptionAmount.text = "$$price"

        holder.itemView.txtSubscriptionName.text = StringHelper.capitalFirstLetter("$name")

        if (list[position].planSubscribedByUser && position == 0) {
            list[0].isSelected = false;
        }

        if (list[position].isSelected) {
            holder.itemView.layoutRoot.setBackgroundResource(R.drawable.selected_subscription);
            holder.itemView.txtSubscriptionAmount.setTextColor(ContextCompat.getColor(context, R.color.color_small_text__dark_layout));
            holder.itemView.txtSubscriptionName.setTextColor(ContextCompat.getColor(context, R.color.color_small_text__dark_layout));
        } else {
            holder.itemView.layoutRoot.setBackgroundResource(R.drawable.unselected_subscription);
            holder.itemView.txtSubscriptionAmount.setTextColor(ContextCompat.getColor(context, R.color.color_text_dark_layout));
            holder.itemView.txtSubscriptionName.setTextColor(ContextCompat.getColor(context, R.color.color_text_dark_layout));
        }

        if (list[position].planSubscribedByUser) {
            //holder.itemView.layoutDisable.setBackgroundColor(ContextCompat.getColor(context, R.color.disable_color));
            holder.itemView.layoutRoot.setBackgroundTintList(context.getResources().getColorStateList(R.color.color_text_dark_layout, null));
            holder.itemView.layoutRoot.alpha = 0.5F;
            holder.itemView.txtYourCurrentPlan.visibility = View.VISIBLE;
            holder.itemView.txtYourCurrentPlan.setText("Your Current Plan")
        } else {
            holder.itemView.layoutRoot.setBackgroundTintList(null);
            //holder.itemView.layoutDisable.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
            holder.itemView.layoutRoot.alpha = 1.0F;
            holder.itemView.txtYourCurrentPlan.visibility = View.GONE;
        }

        holder.itemView.setOnClickListener {

            for (i in list.indices) {
                if (position == i) {
                    if (!list[position].planSubscribedByUser)
                        list[position].isSelected = true
                };
                else list[i].isSelected = false;
            }

            notifyDataSetChanged();

            subscriptionListener!!.onSubscriptionClick(position);
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface SubscriptionListener {
        fun onSubscriptionClick(position: Int)
    }

    fun setOnSubscriptionListener(subscriptionListener: SubscriptionListener) {
        this.subscriptionListener = subscriptionListener
    }

}
