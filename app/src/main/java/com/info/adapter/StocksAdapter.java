package com.info.adapter;

import static com.info.tradewyse.GraphActivity.API_SOURCE_GRAPH;
import static com.info.tradewyse.GraphActivity.chartDifferenceInterval;
import static com.info.tradewyse.R.color.blue;
import static com.info.tradewyse.R.color.green;
import static com.info.tradewyse.R.color.text_color_red;
import static com.info.tradewyse.R.color.white;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.DateTimeHelper;
import com.info.commons.StringHelper;
import com.info.commons.SwipeRevealLayout;
import com.info.dao.DataRepository;
import com.info.logger.Logger;
import com.info.model.GraphMaxMin;
import com.info.model.Quote;
import com.info.model.StockChartModel;
import com.info.model.StockViewModel;
import com.info.model.Stocks;
import com.info.tradewyse.DashBoardActivity;
import com.info.tradewyse.GraphActivity;
import com.info.tradewyse.Interval;
import com.info.tradewyse.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StocksAdapter extends RecyclerView.Adapter<StocksAdapter.StocksViewHolder> {

    List<Stocks> stocksList;
    static Context context;
    LinearLayoutManager linearLayoutManager;
    StockViewModel stockViewModel;
    List<StockChartModel> stockOneDayList = new ArrayList<>();
    private OnClickListener onClickListener;

    public StocksAdapter(List<Stocks> stocksList, Context context, LinearLayoutManager linearLayoutManager, StockViewModel stockViewModel) {
        this.stocksList = stocksList;
        this.context = context;
        this.linearLayoutManager = linearLayoutManager;
        this.stockViewModel = stockViewModel;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public StocksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView;
        if (viewType == 1) {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.stok_list_item, viewGroup, false);
        } else {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.stok_expand_list_item, viewGroup, false);
        }
        return new StocksViewHolder(itemView, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
            return 1;
        } else {
            return 2;
        }
    }


    @Override
    public void onBindViewHolder(final @NonNull StocksViewHolder stocksViewHolder, final int i) {
        final Stocks stocks = stocksList.get(i);

        if (stocksViewHolder.type == 1) {
            Log.v("data", "stock hor");
            stocksViewHolder.txtHorStockName.setText(stocks.getStockName());
            stocksViewHolder.txtHorStockPrice.setText(StringHelper.getAmount(stocks.getStockPrice(), "--"));
            stocksViewHolder.txtStockPriceChange.setText(Common.formatDouble(Math.abs(stocks.getStockChange())));
            DataRepository.getStockPrice(context, stocks.getStockName(), data -> {
                if (data.getStockPrice() != stocks.getStockPrice()) {
                    Log.e("stockprice", data.getStockName() + " " + data.getStockPrice() + "");
                    stocks.setStockPrice(data.getStockPrice());
                    stocks.setStockChange(data.getStockChange());
                    notifyItemChanged(stocksViewHolder.getAdapterPosition());
                }
            });
        } else if (stocksViewHolder.type == 2) {

            DataRepository.getStockPrice(context, stocks.getStockName(), data -> {
                if (data.getStockPrice() != stocks.getStockPrice()) {
                    Log.e("stockprice", data.getStockName() + " " + data.getStockPrice() + "");
                    stocks.setStockPrice(data.getStockPrice());
                    stocks.setStockChange(data.getStockChange());
                    notifyItemChanged(stocksViewHolder.getAdapterPosition());
                }
            });
            Log.v("data", "stock expand");
            stocksViewHolder.txtStockName.setText(stocks.getStockName());
            stocksViewHolder.txtStockCompanyName.setText(stocks.getCompanyName());
            //stocksViewHolder.txtStockPrice.setText(StringHelper.getAmount(stocks.getStockPrice(), "--"));
            stocksViewHolder.txtStockPrice.setText("₹"+Common.formatDouble(stocks.getStockPrice()));
            stocksViewHolder.txtStockPriceChange.setText(Common.formatDouble(Math.abs(stocks.getStockChange())));
            stocksViewHolder.delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteStocks(stocksList.get(i).getId(), i, stocksViewHolder);
                }
            });
            stocksViewHolder.swipeRevealLayout.close(true);

            //getStockChartData(stocks, stocksViewHolder.chart, stocksViewHolder.loader);
            getStockChartData(stocks, stocksViewHolder.chart, stocksViewHolder.tvDotlineBlue);

            stocksViewHolder.layout.setOnClickListener(v -> {
                if (context instanceof DashBoardActivity && ((DashBoardActivity) context).mFirebaseAnalytics != null && ((DashBoardActivity) context).tradWyseSession != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("stock_symbol", stocks.getStockName());
                    bundle.putString("user_id", ((DashBoardActivity) context).tradWyseSession.getUserId());
                    bundle.putString("user_name", ((DashBoardActivity) context).tradWyseSession.getUserName());
                    ((DashBoardActivity) context).mFirebaseAnalytics.logEvent("selected_stock", bundle);
                    Logger.debug("StockAdapter", "stock_symbol firebase analytics event logged");
                }
                GraphActivity.start(context, stocks);
            });

            //stocksViewHolder.setIsRecyclable(false);

        } else {
            Log.v("data", "last else");
        }
        if (stocks.getStockChange() < 0.0) {
            stocksViewHolder.txtStockPriceChange.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_price_low, 0);
            stocksViewHolder.txtStockPriceChange.setTextColor(context.getResources().getColor(text_color_red));
        } else {
            stocksViewHolder.txtStockPriceChange.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_price_high, 0);
            stocksViewHolder.txtStockPriceChange.setTextColor(context.getResources().getColor(R.color.text_color_green));
        }

        stocksViewHolder.relRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (context instanceof DashBoardActivity && ((DashBoardActivity) context).mFirebaseAnalytics != null && ((DashBoardActivity) context).tradWyseSession != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("stock_symbol", stocks.getStockName());
                    bundle.putString("user_id", ((DashBoardActivity) context).tradWyseSession.getUserId());
                    bundle.putString("user_name", ((DashBoardActivity) context).tradWyseSession.getUserName());
                    ((DashBoardActivity) context).mFirebaseAnalytics.logEvent("selected_stock", bundle);
                    Logger.debug("StockAdapter", "stock_symbol firebase analytics event logged");
                }
                GraphActivity.start(context, stocks);
            }
        });

    }

    @Override
    public int getItemCount() {
        return stocksList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class StocksViewHolder extends RecyclerView.ViewHolder {

        private TextView txtHorStockName, txtHorStockPrice, txtHorStockPriceChange, tvNodataChart;
        private TextView txtStockName, delete_button, txtStockCompanyName, txtStockPrice, txtStockPriceChange;
        private SwipeRevealLayout swipeRevealLayout;
        private TextView tvDotlineBlue;
        private RelativeLayout relRoot;
        private int type;
        private LineChart chart;
        private LinearLayout layout;
        LazyLoader loader;

        public StocksViewHolder(@NonNull View view, int type) {
            super(view);
            this.type = type;
            if (type == 1) {
                txtHorStockName = (TextView) view.findViewById(R.id.txtStockName);
                txtStockPriceChange = (TextView) view.findViewById(R.id.txtStockPriceChange);
                txtHorStockPrice = (TextView) view.findViewById(R.id.txtStockPrice);
                relRoot = (RelativeLayout) view.findViewById(R.id.root);

            } else if (type == 2) {
                tvDotlineBlue = (TextView) view.findViewById(R.id.tvbluedotLine);
                txtStockName = (TextView) view.findViewById(R.id.txtStockName);
                txtStockCompanyName = (TextView) view.findViewById(R.id.txtStockCompanyName);
                txtStockPrice = (TextView) view.findViewById(R.id.txtStockPrice);
                txtStockPriceChange = (TextView) view.findViewById(R.id.txtStockPriceChange);
                relRoot = (RelativeLayout) view.findViewById(R.id.root);

                delete_button = view.findViewById(R.id.delete_button);
                swipeRevealLayout = view.findViewById(R.id.swipeRevealLayout);
                chart = view.findViewById(R.id.chart);
                layout = view.findViewById(R.id.layout);
                loader = view.findViewById(R.id.loader);
            }
        }
    }


    public void addMoreStocks(List<Stocks> stocksList, List<Stocks> removedStocksList) {
        boolean b = false;
        int itemCount = 0;
        //to remove duplicate items.
        for (int i = 0; i < this.stocksList.size(); i++) {
            for (int j = 0; j < stocksList.size(); j++) {
                if (this.stocksList.get(i).getStockName().toString().trim().equals(stocksList.get(j).
                        getStockName().toString().trim())) {
                    this.stocksList.remove(i);
                    itemCount = itemCount + 1;
                    b = true;
                }
            }
        }
        if (b) {
            if (itemCount == 1)
                Toast.makeText(context, "You have selected " + itemCount + " already present stock which is added on top", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "You have selected " + itemCount + " already present stocks which are added on top", Toast.LENGTH_LONG).show();
        }

        for (int i = 0; i < this.stocksList.size(); i++) {
            for (int j = 0; j < removedStocksList.size(); j++) {
                if (this.stocksList.get(i).getStockName().toString().trim().equals(removedStocksList.get(j).
                        getStockName().toString().trim())) {
                    this.stocksList.remove(i);

                }
            }
        }

        stocksList.addAll(this.stocksList);
        this.stocksList = stocksList;
        //notifyDataSetChanged();
    }

    public ArrayList<Stocks> getStocksFromAdapter() {
        if (stocksList != null) {
            return (ArrayList<Stocks>) stocksList;
        } else {
            stocksList = new ArrayList<>();
            return (ArrayList<Stocks>) stocksList;
        }
    }

    public HashMap<String, Integer> getStockIndexMap() {
        HashMap<String, Integer> stockIndexMap = new HashMap<>();
        if (stocksList != null) {
            for (int i = 0; i < stocksList.size(); i++) {
                Stocks stocks = stocksList.get(i);
                if (!StringHelper.isEmpty(stocks.getStockName())) {
                    stockIndexMap.put(stocks.getStockName(), i);
                }
            }
        }
        return stockIndexMap;
    }

    public void deleteStocks(String stockId, final int i, final StocksViewHolder stocksViewHolder) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.style_progress_dialog);
        progressDialog.setMessage("Deleting....");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        Call<List<HashMap<String, String>>> call = apiInterface.deleteStock(stockId);
        call.enqueue(new Callback<List<HashMap<String, String>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, String>>> call, Response<List<HashMap<String, String>>> response) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    stocksList.remove(i);
                    stocksViewHolder.swipeRevealLayout.close(true);
                    notifyDataSetChanged();
                    onClickListener.onDeleteListener(i);
                }
            }

            @Override
            public void onFailure(Call<List<HashMap<String, String>>> call, Throwable t) {
                progressDialog.dismiss();
                Log.d(Constants.ON_FAILURE_TAG, "StocksAdapter deleteStocks: onFailure");
            }
        });
    }

   // public void getStockChartData(Stocks stocks, LineChart chart, LazyLoader loader)
    public void getStockChartData(Stocks stocks, LineChart chart, TextView tvDotlineBlue){
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_URL_TRADEWYSE, context);
        Call<List<StockChartModel>> call = apiInterface.getStockChartData(stocks.getStockName().trim(), Constants.IEX_TOKEN);
        //progress.setVisibility(View.VISIBLE);
        stockOneDayList.clear();
        call.enqueue(new Callback<List<StockChartModel>>() {
            @Override
            public void onResponse(Call<List<StockChartModel>> call, Response<List<StockChartModel>> response) {
                //progress.setVisibility(View.GONE);
                if (response.body() != null) {
                    stockOneDayList = response.body();
                    try {
                        List<StockChartModel> stockOneDayListFiltered = new ArrayList<>();
                        List<StockChartModel> stockOneDayListFilteredAvg = new ArrayList<>();

                        for (int i = 0; i < stockOneDayList.size(); i++) {
                            if (stockOneDayList.get(i).getAverage() > 0) {
                                stockOneDayListFilteredAvg.add(stockOneDayList.get(i));
                            }
                        }

                        if (stockOneDayListFilteredAvg.size() > 50) {
                            for (int i = 0; i < stockOneDayList.size(); i++) {
                                String min = stockOneDayList.get(i).getMinute().substring(Math.max(stockOneDayList.get(i).getMinute().length() - 2, 0));

                                if (min.equalsIgnoreCase("15") || min.equalsIgnoreCase("30") ||
                                        min.equalsIgnoreCase("45") || min.equalsIgnoreCase("59")) {
                                    stockOneDayListFiltered.add(stockOneDayList.get(i));
                                }
                            }
                        } else {
                            stockOneDayListFiltered.addAll(stockOneDayListFilteredAvg);
                        }

                       setStockChart(stockOneDayListFiltered, API_SOURCE_GRAPH, chart, stocks, tvDotlineBlue);
                        //setStockChart(stockOneDayListFiltered, API_SOURCE_GRAPH, chart, stocks);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<StockChartModel>> call, Throwable t) {
                //progress.setVisibility(View.GONE);
                Log.d(Constants.ON_FAILURE_TAG, "GraphActivity getStockChartData: onFailure");
            }
        });
    }

    public void setStockChart(List<StockChartModel> stockData, int apiSource, LineChart chart, Stocks stocks, TextView tvDotlineBlue){
    //public void setStockChart(List<StockChartModel> stockChartData, int apiSource, LineChart chart, Stocks stocks){

        List<StockChartModel> stockChartData = new ArrayList<>();
        for (int i = 0; i < stockData.size(); i++) {
            if (stockData.get(i).getAverage() > 0) {
                stockChartData.add(stockData.get(i));
            }
        }

        //loader.setVisibility(View.GONE);
        tvDotlineBlue.setVisibility(View.GONE);
        chart.setNoDataText("........................");
        chart.setNoDataTextColor(ContextCompat.getColor(context, R.color.blue_color));

        chart.setVisibility(View.VISIBLE);
        ArrayList<Entry> entries = new ArrayList<>();
        double low = 0;

        for (int i = 0; i < stockChartData.size(); i++) {
            StockChartModel stockChartModel = stockChartData.get(i);

            double amountY = Common.formatDouble2(stockChartModel.getClose());

            if (amountY > 0) {
                entries.add(new Entry(i, (float) amountY));

                if (amountY <= low || low == 0) {
                    if (amountY != 0)
                        low = amountY;
                }
            }
        }

        if (entries.size() == 0) {
            chart.clear();
            chart.invalidate();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        Drawable drawableRed = ContextCompat.getDrawable(context, R.drawable.graph_shadow_red);
        Drawable drawableGreen = ContextCompat.getDrawable(context, R.drawable.graph_shadow_green);
        XAxis xAxis = chart.getXAxis();
        if (stocks.getStockChange() < 0.0) {

            dataSet.setColor(ContextCompat.getColor(context, text_color_red));
            dataSet.setFillDrawable(drawableRed);
            dataSet.setDrawFilled(true);
            xAxis.setAxisLineColor(ContextCompat.getColor(context, text_color_red));
        } else {
            dataSet.setColor(ContextCompat.getColor(context, green));
            dataSet.setFillDrawable(drawableGreen);
            dataSet.setDrawFilled(true);
            xAxis.setAxisLineColor(ContextCompat.getColor(context, green));
        }

        dataSet.setLineWidth(1);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextColor(ContextCompat.getColor(context, R.color.text_color_white));
        dataSet.setCircleRadius(6.5f);
        dataSet.setCircleHoleRadius(3.5f);
        dataSet.setCircleColor(Color.parseColor("#ffffff"));
        dataSet.setCircleHoleColor(Color.parseColor("#039BE5"));

        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setTextSize(0f);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setAvoidFirstLastClipping(false);

        chart.getAxisRight().setTextColor(ContextCompat.getColor(context, R.color.transparent)); // left y-axis
        chart.getAxisRight().setAxisLineWidth(0F);
        chart.getAxisRight().setGranularityEnabled(true);
        chart.getAxisRight().setGranularity(.5f);
        chart.getAxisRight().setLabelCount(chartDifferenceInterval, true);
        chart.getXAxis().setTextColor(ContextCompat.getColor(context, R.color.transparent));
        chart.getXAxis().setAxisLineWidth(0F);
        chart.getLegend().setTextColor(ContextCompat.getColor(context, R.color.transparent));
        chart.getXAxis().setTextSize(0f);
        chart.getAxisLeft().setEnabled(false);
        chart.getLegend().setEnabled(false);

        // Controlling X axis
        xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        double finalLow = low;

        ValueFormatter formatterY = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                double formattedValue = Common.formatDouble2(value);
                String returnValue = String.valueOf((int) formattedValue);
                if (formattedValue <= finalLow) {
                    returnValue = "";
                }
                return returnValue;
            }
        };

        //xAxis.setValueFormatter(formatter);
        chart.getAxisRight().setValueFormatter(formatterY);

        chart.setExtraOffsets(0, 0, 0, 0);
        // Setting Data
        LineData data = new LineData(dataSet);
        chart.setData(data);
        //chart.animateX(250);
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setDrawZeroLine(false);

        xAxis.setLabelCount(chartDifferenceInterval, true);
        if (apiSource == API_SOURCE_GRAPH) {
            float start = entries.get(0).getX();
            float end = entries.get(entries.size() - 1).getX();

            float difference = end - start;
            float differenceInterval = difference / chartDifferenceInterval;
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(1);

        } else {
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(1);
        }

        xAxis.setYOffset(8);
        chart.invalidate();
        chart.highlightValue(null);

        // remove axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);
        YAxis rightAxis = chart.getAxisRight();
        //show xAxis line below the graph with dot
        rightAxis.setEnabled(false);
        DashPathEffect dashPath = new DashPathEffect(new float[]{(float) 2, 4}, (float) 1.0);
        xAxis.setAxisLineDashedLine(dashPath);
        xAxis.setAxisLineWidth((float) 1.0);

    }

    public interface OnClickListener {
        void onDeleteListener(int position);
    }

    public void SetOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
