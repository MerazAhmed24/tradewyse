package com.info.tradewyse;

import static com.info.tradewyse.R.color.white;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.tabs.TabLayout;
import com.info.adapter.TipsAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.Converter;
import com.info.commons.CustomMarkerView;
import com.info.commons.DateTimeHelper;
import com.info.commons.DateTimeHelperElapsed;
import com.info.commons.SlideToActView;
import com.info.commons.StringHelper;
import com.info.model.FooterModel;
import com.info.model.GraphMaxMin;
import com.info.model.NotificationCountModel;
import com.info.model.Quote;
import com.info.model.SectorNews;
import com.info.model.StockChartModel;
import com.info.model.StockHistory;
import com.info.model.Stocks;
import com.info.model.TipsSection;
import com.info.tradewyse.databinding.ActivityGraphBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphActivity extends BaseActivity {
    public static final int API_SOURCE_GRAPH_PREDICTION = 3;
    public static final int API_SOURCE_GRAPH = 1;
    public static final int API_SOURCE_HISTORY = 2;
    public static final int chartDifferenceInterval = 5;
    LineChart chart;
    Stocks stock;
    LinearLayout bottomlinearLayout;
    HashMap<Float, String> dummyTime = new HashMap<>();
    TextView txtStockCompany, txtNYTime, txtPriceValue, txtHigh, txtLow;
    TabLayout daysTabLayout;
    List<StockHistory> stockHistoryList = new ArrayList<>();
    List<StockChartModel> stockOneDayList = new ArrayList<>();
    Button btnPrediction;
    Context context;

    // AI tip detail views.
    private RelativeLayout tipRootLayout;
    private TextView txtStatusBuySellAvoid;
    private TextView txtDateTime;
    private TextView txtStockName;
    private TextView txtOverallStatus;
    private TextView txtStockCompanyName;
    private TextView txtFirstValue;
    private TextView txtSecondValue;
    private TextView txtThirdValue;
    private ProgressBar progress;
    private CardView rotationCard;
    private ImageView rotation;
    ActivityGraphBinding graphBinding;
    private TextView textViewGetPrediction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        graphBinding = DataBindingUtil.setContentView(this, R.layout.activity_graph);
        FooterModel footerModel = new FooterModel(true, false, false, false, false);
        graphBinding.setFooterModel(footerModel);
        getNotificationCount();
        setToolBar("");
        context = this;
        stock = getIntent().getExtras().getParcelable("stock");
        callGetStockDetail(stock.getStockName());
        initializeView();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Common.BottomTabColorChange(GraphActivity.this,bottomlinearLayout);
            }
        }, 500);
        //  setAITipDetail(stock.getStockName());
        rotationCard = findViewById(R.id.rotationCard);
        bottomlinearLayout = findViewById(R.id.bottomView);
        rotation = findViewById(R.id.rotation);
        final SlideToActView slide = findViewById(R.id.event_slider);
        slide.setSliderIcon(R.drawable.ic_arrow_circle);
        slide.setOnSlideCompleteListener(view -> {
            slide.setText("Running Prediction");
            rotationCard.setVisibility(View.VISIBLE);
            Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            rotation.startAnimation(rotate);
            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    GraphStockPrediction.start(GraphActivity.this, stock);
                    rotationCard.setVisibility(View.GONE);
                    slide.resetSlider();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            slide.setText("   Swipe Right For 5 Day");
                        }
                    }, 1000);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

        });
    }


    public void callGetStockDetail(final String stockName) {
        // check network is available or not.
        if (!Common.isNetworkAvailable(context)) {
            Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                    context.getResources().getString(R.string.JustLetYouKnow));
            return;
        }
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_URL_TRADEWYSE, this);
        Call<Quote> call = apiInterface.getStockDetailData(stockName, Constants.IEX_TOKEN);
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.body() != null) {
                    Quote quote = response.body();
                    stock.setStockPrice(quote.getLatestPrice());
                    stock.setStockChange(quote.getChange());
                    stock.setHigh(quote.getHigh());
                    stock.setLow(quote.getLow());
                    setValues();
                }
            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "GraphActivity callGetStockDetail: onFailure");
            }
        });


    }

    public void initializeView() {
        setToolBar(stock.getStockName());

        daysTabLayout = findViewById(R.id.daysTabLayout);
        txtStockCompany = findViewById(R.id.txtStockCompany);
        txtNYTime = findViewById(R.id.txtNYTime);
        txtPriceValue = findViewById(R.id.txtPriceValue);
        txtHigh = findViewById(R.id.txtHigh);
        txtLow = findViewById(R.id.txtLow);
        chart = findViewById(R.id.chart);

        btnPrediction = findViewById(R.id.btnPrediction);

        progress = findViewById(R.id.progress);
        tipRootLayout = findViewById(R.id.rootLayout);
        txtDateTime = findViewById(R.id.txtDateTime);
        txtStockName = findViewById(R.id.txtStockName);
        txtOverallStatus = findViewById(R.id.txtOverallStatus);  //very positive, very negative etc.
        txtStockCompanyName = findViewById(R.id.txtStockCompanyName);
        txtFirstValue = findViewById(R.id.txtFirstValue);
        txtSecondValue = findViewById(R.id.txtSecondValue);
        txtThirdValue = findViewById(R.id.txtThirdValue);
        txtStatusBuySellAvoid = findViewById(R.id.txtStatusBuySellAvoid);
        textViewGetPrediction = findViewById(R.id.textViewGetPrediction);

        setValues();
        getStockChartData(stock.getStockName());
        daysTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int p = tab.getPosition();
                if (p == 0) {
                    if (stockOneDayList.size() == 0) {
                        getStockChartData(stock.getStockName());
                    } else {
                        setStockChart(stockOneDayList, API_SOURCE_GRAPH);
                    }
                } else {
                    if (stockHistoryList.size() == 0) {
                        getStockHistoryData(stock.getStockName());
                    } else {
                        setStockHistoryChart(p);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        btnPrediction.setOnClickListener(view -> {
            GraphStockPrediction.start(this, stock);
        });

        textViewGetPrediction.setOnClickListener(v -> {
            GraphStockPrediction.start(GraphActivity.this, stock);
        });

    }

    public void setValues() {
        txtNYTime.setText("NY Time " + DateTimeHelper.getNYTime());
        txtPriceValue.setText(Common.formatDouble(stock.getStockPrice()));
        txtStockCompany.setText(stock.getCompanyName());
    }

    public static void start(Context context, Stocks stocks) {
        Intent starter = new Intent(context, GraphActivity.class);
        starter.putExtra("stock", stocks);
        context.startActivity(starter);
    }


    public void getStockChartData(String stockName) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_URL_TRADEWYSE, GraphActivity.this);
        Call<List<StockChartModel>> call = apiInterface.getStockChartData(stockName.trim(), Constants.IEX_TOKEN);
        progress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<StockChartModel>>() {
            @Override
            public void onResponse(Call<List<StockChartModel>> call, Response<List<StockChartModel>> response) {
                progress.setVisibility(View.GONE);
                if (response.body() != null) {
                    chart.setVisibility(View.VISIBLE);
                    stockOneDayList = response.body();
                    try {
                        setStockChart(stockOneDayList, API_SOURCE_GRAPH);
                    } catch (Exception e) {

                    }
                }

            }

            @Override
            public void onFailure(Call<List<StockChartModel>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Log.d(Constants.ON_FAILURE_TAG, "GraphActivity getStockChartData: onFailure");
            }
        });
    }


    public void getStockHistoryData(String stockName) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, GraphActivity.this);
        Call<List<StockHistory>> call = apiInterface.getStockHistoryData(stockName.trim());
        progress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<StockHistory>>() {
            @Override
            public void onResponse(Call<List<StockHistory>> call, Response<List<StockHistory>> response) {
                progress.setVisibility(View.GONE);
                if (response.body() != null) {
                    stockHistoryList = response.body();
                    try {
                        setStockHistoryChart(daysTabLayout.getSelectedTabPosition());
                    } catch (Exception e) {

                    }
                } else {
                    chart.clear();
                    chart.invalidate();
                    Common.showMessage(GraphActivity.this, getString(R.string.NoHistoricalDataErrorMsg), getString(R.string.JustLetYouKnow));
                }
            }

            @Override
            public void onFailure(Call<List<StockHistory>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                chart.clear();
                chart.invalidate();
                Common.showMessage(GraphActivity.this, getString(R.string.NoHistoricalDataErrorMsg), getString(R.string.JustLetYouKnow));
                Log.d(Constants.ON_FAILURE_TAG, "GraphActivity getStockHistoryData: onFailure");
            }
        });
    }

    private void setStockHistoryChart(int index) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date currentDate = cal.getTime();
        switch (index) {
            case 0:
                //setStockChart(stockOneDayList, API_SOURCE_GRAPH);
                getStockChartData(stock.getStockName());
                break;
            case 1:
                cal = DateTimeHelper.workingDaysBack(cal, 5);
                break;
            case 2:
                cal.add(Calendar.MONTH, -1);
                break;
            case 3:
                cal.add(Calendar.MONTH, -3);
                break;
            case 4:
                cal.add(Calendar.MONTH, -6);
                break;
            case 5:
                cal.add(Calendar.YEAR, -1);
                break;
        }

        Date endDate = cal.getTime();
        Log.e("current", currentDate + "");
        Log.e("end", endDate + "");
        List<StockChartModel> stockChartData = new ArrayList<>();
        int size = stockHistoryList.size();
        /* if(isFiveDay&&stockHistoryList.size()>=5){
            size=5;
        }*/
        for (int i = 0; i < size; i++) {
            StockHistory stockHistory = stockHistoryList.get(i);

            Date stockDate = DateTimeHelper.parseDate(stockHistory.getDate());
            if (DateTimeHelper.between(stockDate, currentDate, endDate)) {
                Log.e("stockHistory", stockHistory.getDate());
                float close = stockHistory.getClose();
                StockChartModel stockChartModel = new StockChartModel();
                stockChartModel.setAverage(close);
                stockChartModel.setClose(close);
                stockChartModel.setDate(stockHistory.getDate());
                stockChartModel.setHigh(close);

                stockChartModel.setLow(close);
                stockChartData.add(stockChartModel);
            }
        }
        StockChartModel currentStockChartModel = new StockChartModel();
        currentStockChartModel.setAverage(stock.getStockPrice());
        currentStockChartModel.setClose(stock.getStockPrice());
        String today = DateTimeHelperElapsed.toString(System.currentTimeMillis(), "yyyy-MM-dd");
        currentStockChartModel.setDate(today);
        currentStockChartModel.setHigh(stock.getStockPrice());
        currentStockChartModel.setLow(stock.getStockPrice());
        stockChartData.add(currentStockChartModel);

        setStockChart(stockChartData, API_SOURCE_HISTORY);
    }

    public void setStockChart(List<StockChartModel> stockChartData, int apiSource) {
        ArrayList<Entry> entries = new ArrayList<>();
        double high = 0;
        double low = 0;
        chart.setNoDataText(context.getResources().getString(R.string.no_data_chart_text));
        LineDataSet dataSet;


        String[] datesList = new String[stockChartData.size()];
        //String[] datesList = new String [] {"09:30 am", "11:07 am", "12:44 pm", "02:21 pm", "02:59 pm"};

        for (int i = 0; i < stockChartData.size(); i++) {
            StockChartModel stockChartModel = stockChartData.get(i);

            if (apiSource == API_SOURCE_GRAPH) {

                float timeX = DateTimeHelper.parseTimeResponse(stockChartModel.getMinute().toLowerCase().trim()).getTime();
                datesList[i] = DateTimeHelper.parseTimeFloat(timeX);
            } else {
                float dateX = DateTimeHelper.parseDate(stockChartModel.getDate().toLowerCase().trim()).getTime();
                datesList[i] = DateTimeHelper.parseDateFloat(dateX);
            }

            double amountY = Common.formatDouble2(stockChartModel.getClose());

            if (amountY > 0) {
                entries.add(new Entry(i, (float) amountY));
                if (high <= amountY) {
                    high = amountY;
                }

                if (amountY <= low || low == 0) {
                    if (amountY != 0)
                        low = amountY;
                }
            }
        }

        txtLow.setText("LO: " + Common.formatDouble(low));
        txtHigh.setText("HI: " + Common.formatDouble(high));
        if (entries.size() == 0) {
            chart.clear();
            chart.invalidate();
            return;
        }


        dataSet = new LineDataSet(entries, "");
        dataSet.setColor(ContextCompat.getColor(this, R.color.text_color_white));
        dataSet.setLineWidth(1);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.text_color_white));

        dataSet.setCircleRadius(6.5f);
        dataSet.setCircleHoleRadius(3.5f);
        dataSet.setCircleColor(Color.parseColor("#ffffff"));
        dataSet.setCircleHoleColor(Color.parseColor("#039BE5"));

        //dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setTextSize(12f);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setAvoidFirstLastClipping(false);

        chart.getAxisRight().setTextColor(ContextCompat.getColor(this, R.color.text_color_white)); // left y-axis
        chart.getAxisRight().setAxisLineWidth(.5F);
        chart.getAxisRight().setGranularityEnabled(true);
        chart.getAxisRight().setGranularity(.5f);
        chart.getAxisRight().setLabelCount(chartDifferenceInterval, true);
        chart.getXAxis().setTextColor(ContextCompat.getColor(this, R.color.text_color_white));
        chart.getXAxis().setAxisLineWidth(.5F);
        chart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.text_color_white));
        chart.getXAxis().setTextSize(12f);
        chart.getAxisLeft().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.getAxisRight().setAxisMinimum((float) low);
        chart.getAxisRight().setAxisMaximum((float) high);

        // Controlling X axis
        XAxis xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value


        double finalLow = low;
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (datesList != null) {
                    if (datesList.length >= (int) value && value > 0) {
                        try {
                            return datesList[(int) value];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            //chart.setVisibility(View.INVISIBLE);
                            e.printStackTrace();
                        }
                        return datesList[0];
                    } else
                        return datesList[0];
                } else
                   return datesList[0];
            }
        };

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

        xAxis.setValueFormatter(formatter);
        //chart.getAxisRight().setValueFormatter(formatterY);

        CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout, apiSource, datesList, Common.formatDouble(stock.getStockPrice()));

        // set the marker to the chart
        chart.setMarker(mv);

        chart.setExtraOffsets(45F, 0, 10F, 10F);

        // Setting Data
        LineData data = new LineData(dataSet);
        chart.setData(data);
        chart.animateX(250);
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

        GraphMaxMin graphMaxMin = Interval.getInterval(high, low);

        chart.getAxisRight().setLabelCount(graphMaxMin.getAxisLabelCount() + 1, true);

        chart.getAxisRight().setAxisMinimum((float) graphMaxMin.getAxisMinimum());

        chart.getAxisRight().setAxisMaximum((float) graphMaxMin.getAxisMaximum());


        chart.getAxisLeft().setLabelCount(graphMaxMin.getAxisLabelCount() + 1, true);

        chart.getAxisLeft().setAxisMinimum((float) graphMaxMin.getAxisMinimum());

        chart.getAxisLeft().setAxisMaximum((float) graphMaxMin.getAxisMaximum());
        xAxis.setYOffset(10);
        chart.invalidate();
        chart.highlightValue(null);
    }


    /*
     * show AI tip data for this stock.
     *
     * */


    String sectorNewsStatus = "";


    public void homeTabClicked(View v) {
        DashBoardActivity.CallDashboardActivity(this, false);
        finish();
    }

    public void chatTabClicked(View v) {
        ChatActivity.starChatActivity(this,false);
        //TabbedChatActivity.CallTabbedChatActivity(this, true);
        finish();
    }

    public void servicesTabClicked(View v) {
        ServicesActivity.CallServicesActivity(this);
        finish();
    }

    public void notificationTabClicked(View v) {
        NotificationActivity.starNotificationActivity(this);
        finish();
    }

    public void moreTabClicked(View v) {
        MoreTabActivity.startMoreTabActivity(this);
        finish();
    }

    private void getNotificationCount() {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<NotificationCountModel> call = apiInterface.getCountOfAllUnreadNotificationActivityDetails();
        call.enqueue(new Callback<NotificationCountModel>() {
            @Override
            public void onResponse(Call<NotificationCountModel> call, Response<NotificationCountModel> response) {
                if (response != null && response.body() != null) {
                    NotificationCountModel notificationCountModel = response.body();
                    if (notificationCountModel.getUnReadCount() > 0) {
                        (findViewById(R.id.nav_unread_badge)).setVisibility(View.VISIBLE);
                    } else {
                        (findViewById(R.id.nav_unread_badge)).setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationCountModel> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getUserProfile: onFailure", t.fillInStackTrace());
            }
        });
    }

}
