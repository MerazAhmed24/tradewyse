package com.info.tradewyse;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.info.adapter.StocksAdapter;
import com.info.model.Stocks;

import java.util.ArrayList;

public class SelectedStocks extends BaseActivity {
    ArrayList<Stocks> selectedStocks;
    RecyclerView stocksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_stocks);
        setToolBar("TRADEWYSE");
        initializeViews();
    }

    public void initializeViews() {
        stocksList = findViewById(R.id.stocksList);
        selectedStocks = getIntent().getExtras().getParcelableArrayList("selected_stocks");
        LinearLayoutManager stockLayoutManger = new LinearLayoutManager(this);
        StocksAdapter stocksAdapter = new StocksAdapter(selectedStocks, this, stockLayoutManger,null);
        stocksList.setLayoutManager(stockLayoutManger);
        stocksList.setAdapter(stocksAdapter);
    }

}
