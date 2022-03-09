package com.info.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.info.dao.AppDatabase
import com.info.dao.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StockViewModel : ViewModel() {

    fun getStockPrice(stockName:String,context: Context):MutableLiveData<StockPrice>{
       var mutableStockLiveData=MutableLiveData<StockPrice>()
        viewModelScope.launch(Dispatchers.IO) {
            var dao = AppDatabase.getAppDataBase(context).stockPriceDao()
           var stockPrice= dao.getStockPriceSingle(stockName)
           mutableStockLiveData.postValue(stockPrice)
        }
        return mutableStockLiveData;
    }

    fun insert(stockPrice: StockPrice,context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            DataRepository.insertPrice(stockPrice,context)
        }
    }
}