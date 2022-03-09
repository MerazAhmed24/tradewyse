package com.info.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.unit.Constraints
import androidx.recyclerview.widget.RecyclerView
import com.info.api.APIClient
import com.info.commons.Constants
import com.info.commons.DateTimeHelperElapsed
import com.info.interfaces.ShowHideProgressDialogInterface
import com.info.model.NotificationBundle
import com.info.model.NotificationModel
import com.info.tradewyse.R
import com.info.tradewyse.TipDetailActivity
import kotlinx.android.synthetic.main.notification_row.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Amit Gupta on 16,June,2021
 */
class NotificationAdapter(

    var notificationBundleList: ArrayList<NotificationBundle>,
    var context: Context,
    var notificationListener: NotificationListener

) : RecyclerView.Adapter<NotificationAdapter.NotificationRowViewHolder>() {

    class NotificationRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationRowViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.notification_row, parent, false)
        return NotificationRowViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationRowViewHolder, position: Int) {
        var notificationModel = notificationBundleList[position]
        if (notificationModel.read == true) {
            holder.itemView.unread_badge.visibility = View.GONE
        } else {
            holder.itemView.unread_badge.visibility = View.VISIBLE
        }
        holder.itemView.notification_text.text = notificationModel.notificationText
        holder.itemView.notification_time.text =
            DateTimeHelperElapsed.changeDateWithCheckTodayYesterday(notificationModel.createdOn) + ", " +
            DateTimeHelperElapsed.toString(
            DateTimeHelperElapsed.changeTimeToEstTime(notificationModel.createdOn), "hh:mm a") + " EST";
        /*holder.itemView.notification_time.text =
            DateTimeHelperElapsed.changeDateFormat(notificationModel.createdOn) + " EST";*/

        holder.itemView.root_row.setOnClickListener {
            if (!notificationModel.read!!) {
                notificationListener.onNotificationClick(position, "markRead")
                //markNotificationRead(notificationModel, position)
            } else {
                notificationListener.onNotificationClick(position, "action")
                //takeRequiredAction(notificationModel, position)
            }
        }

        holder.itemView.delete_button.setOnClickListener {
            notificationListener.onDeleteClick(position)
            //deleteNotificationById(position)
            holder.itemView.swipeRevealLayout.swipeRevealLayout.close(true);
        }
    }

    override fun getItemCount(): Int {
        return notificationBundleList.size
    }

    interface NotificationListener {
        fun onNotificationClick(position: Int, type: String)
        fun onDeleteClick(position: Int)
    }

    fun setOnNotificationListener(notificationListener: NotificationListener) {
        this.notificationListener = notificationListener
    }

}