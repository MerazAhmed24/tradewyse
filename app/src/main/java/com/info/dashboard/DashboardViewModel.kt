package com.info.dashboard

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.info.CustomToast.Toasty
import com.info.api.APIClient
import com.info.commons.Constants
import com.info.commons.Constants.STOCK_CHANGE_PAYLOAD_IDENTIFIER
import com.info.commons.DateTimeHelper
import com.info.commons.TradWyseSession
import com.info.service.NotificationProvider
import com.info.tradewyse.DashBoardActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.*

class DashboardViewModel : ViewModel() {

    val stockChangeData = BehaviorSubject.create<StockChangeContainer>()
    val showStockDialog = PublishSubject.create<Boolean>()
    val compositeDisposable = CompositeDisposable()


    fun processIntent(context: Context, possibleIntent: Intent?) {
        possibleIntent?.let { intent ->
            if (intent.getBooleanExtra(STOCK_CHANGE_PAYLOAD_IDENTIFIER, false)) {
                NotificationProvider().removeChangedStocksNotification(context)
                compositeDisposable.add(
                    APIClient.getTradeAPIClient(
                        APIClient.BASE_SERVER_URL, context
                    )
                        .getChangedStocksForUser(TradWyseSession.getSharedInstance(context).userName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            showStockDialog.onNext(true)
                            stockChangeData.onNext(
                                StockChangeContainer(""/*AI Tip Changes - ${DateTimeHelper.formatTime(Date())}*/,
                                    response.map {
                                        StockChangeDisplayItem(it.stockSymbol, it.yesterdayResult, it.todayResult, "$${it.currentPrice}")
                                    })
                            )
                        }, { error ->
                            Log.i("TradeTips: ERROR", "${error.message}")
                        })
                )
            }
            if (intent.getBooleanExtra("fromNotificationClick", false)) {
                var adBannerId: String? = intent.extras?.getString("adbannerId")
                adBannerId.let { adBannerId ->
                    (context as DashBoardActivity).getAdBannerById(adBannerId, 0)
                }
            } else {
                if (!Constants.IS_PRODUCTION) {
                    //Toasty.error(context,"fromNotificationClick boolean value is not received").show()
                }
            }
        }
    }
}

data class StockChangeContainer(val date: String, val stockChanges: List<StockChangeCell>)
open class StockChangeCell

/**
 * Identifier to put a header at a location in the stock changed list recycler view.
 */
class StockChangeHeader : StockChangeCell()
data class StockChangeDisplayItem(val symbol: String, val previousStatus: String, val todayStatus: String, val price: String) :
    StockChangeCell()