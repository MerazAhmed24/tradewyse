package com.info.model

data class StockChangeResponse(
        val id: String,
        val stockSymbol: String,
        val stockName: String,
        val yesterdayResult: String,
        val todayResult: String,
        val currentPrice: String,
        val userId: String,
        val createdOn: String,
        val modifiedOn: String)