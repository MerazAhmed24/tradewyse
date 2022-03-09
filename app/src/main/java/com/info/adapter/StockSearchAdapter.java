package com.info.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.info.commons.Constants;
import com.info.commons.StringHelper;
import com.info.model.SelectableStocks;
import com.info.model.Stocks;
import com.info.tradewyse.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StockSearchAdapter extends RecyclerView.Adapter implements SelectableViewHolder.OnStocksSelectedListener {

    private List<SelectableStocks> mValues;
    private List<Stocks> stocksList;
    private boolean isMultiSelectionEnabled = false;
    SelectableViewHolder.OnStocksSelectedListener listener;
    ArrayList<String> stocks_list;

    //ArrayList<String> newStocksAdded=new ArrayList<>();
    public StockSearchAdapter(ArrayList<String> stocks_list, SelectableViewHolder.OnStocksSelectedListener listener,
                              List<Stocks> stocksList, boolean isMultiSelectionEnabled) {
        this.listener = listener;
        this.isMultiSelectionEnabled = isMultiSelectionEnabled;
        this.stocks_list = stocks_list;
        this.stocksList = stocksList;
        mValues = new ArrayList<>();
        for (Stocks stocks : stocksList) {
            mValues.add(new SelectableStocks(stocks, false));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         if (viewType == Constants.STOCK_VIEW) {
            View stocksView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_stocks_search, parent, false);

            return new SelectableViewHolder(stocksView, this);

        }
        else {
            View sectionView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_section, parent, false);

            return new SectionViewHolder(sectionView);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SelectableStocks selectableStocks = mValues.get(position);
        if (viewHolder instanceof SelectableViewHolder) {
            SelectableViewHolder holder = (SelectableViewHolder) viewHolder;

            String stockName = selectableStocks.getStockName();
            holder.txtStockName.setText(stockName);
            holder.txtStockCompany.setText(selectableStocks.getCompanyName());

            if (isMultiSelectionEnabled) {
                TypedValue value = new TypedValue();
                holder.txtStockName.getContext().getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorMultiple, value, true);
            } else {
                TypedValue value = new TypedValue();
                holder.txtStockName.getContext().getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorSingle, value, true);
            }
            holder.mStocks = selectableStocks;
            holder.setChecked(holder.mStocks.isSelected());
            if (stocks_list.contains(stockName.trim())) {
                holder.setChecked(true);
            }
            holder.setClickable(true);
        } else {
            SectionViewHolder sectionViewHolder = (SectionViewHolder) viewHolder;
            sectionViewHolder.txtStockName.setText(selectableStocks.getSectionName());
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public ArrayList<Stocks> getSelectedStocks() {

        ArrayList<Stocks> selectedShopCategorys = new ArrayList<>();
        for (SelectableStocks selectableStocks : mValues) {
            if (selectableStocks.isSelected()) {
                selectedShopCategorys.add(selectableStocks);
            }
        }
        return selectedShopCategorys;
    }

    public ArrayList<Stocks> getRemovedStocks() {

        ArrayList<Stocks> removedStocks = new ArrayList<>();
        for (SelectableStocks selectableStocks : mValues) {
            if (!selectableStocks.isSelected() && !StringHelper.isEmpty(selectableStocks.getStockName())) {
                removedStocks.add(selectableStocks);
            }
        }
        return removedStocks;
    }

    @Override
    public int getItemViewType(int position) {
        if (!StringHelper.isEmpty(mValues.get(position).getSectionName())){
            return Constants.SECTION_VIEW;
        }
        else
        {
            return Constants.STOCK_VIEW;
        }
    }


    @Override
    public void onStocksSelected(SelectableStocks selectableStocks) {
        if (!isMultiSelectionEnabled) {
            for (SelectableStocks stocks : mValues) {
               if(!StringHelper.isEmpty(stocks.getStockName())){
                   if (stocks.getStockName().equals(selectableStocks.getStockName())
                           && selectableStocks.isSelected()) {
                       stocks.setSelected(true);
                   } else {
                       stocks.setSelected(false);
                   }
               }

            }
            notifyDataSetChanged();
        }
        listener.onStocksSelected(selectableStocks);
        String selectedStockName = selectableStocks.getStockName().trim();
       if(!isMultiSelectionEnabled){
           stocks_list.clear();
       }else{
           if (selectableStocks.isSelected()) {
               if (!stocks_list.contains(selectedStockName)) {
                   stocks_list.add(selectedStockName);
               }

           } else {
               stocks_list.remove(selectedStockName);
           }
           resetStockSearchList();
       }


    }

    public void resetStockSearchList() {
        List<Stocks> addedStocks = new ArrayList<>();
        List<Stocks> searchStocks = new ArrayList<>();
        for (Stocks stocks : stocksList) {
            if (!StringHelper.isEmpty(stocks.getStockName())) {
                if (stocks_list.contains(stocks.getStockName().trim())) {
                    addedStocks.add(stocks);
                } else {
                    searchStocks.add(stocks);
                }
            }
        }

        stocksList = new ArrayList<>();
        if (addedStocks.size() > 0) {
            Collections.sort(addedStocks, new Comparator<Stocks>() {
                @Override
                public int compare(Stocks o1, Stocks o2) {
                    return o1.getStockName().compareTo(o2.getStockName());
                }
            });
            Stocks stocks = new Stocks();
            stocks.setSectionName("Added Stocks");
            stocksList.add(stocks);
            stocksList.addAll(addedStocks);
        }

        if (searchStocks.size() > 0) {
            Collections.sort(searchStocks, new Comparator<Stocks>() {
                @Override
                public int compare(Stocks o1, Stocks o2) {
                    return o1.getStockName().compareTo(o2.getStockName());
                }
            });
            Stocks stocks = new Stocks();
            stocks.setSectionName("Searched Stocks");
            stocksList.add(stocks);
            stocksList.addAll(searchStocks);
        }

        mValues = new ArrayList<>();
        for (Stocks stocks : stocksList) {
            mValues.add(new SelectableStocks(stocks, false));
        }

        notifyDataSetChanged();
    }

}