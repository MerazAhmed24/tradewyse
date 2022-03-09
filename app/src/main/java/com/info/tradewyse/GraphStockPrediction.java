package com.info.tradewyse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.info.adapter.TipsAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.ClaimsXAxisValueFormatter;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.Converter;
import com.info.commons.CustomMarkerView;
import com.info.commons.CustomProgressBarDialog;
import com.info.commons.DateTimeHelper;
import com.info.commons.DateTimeHelperElapsed;
import com.info.interfaces.OnNotificationReceived;
import com.info.commons.StringHelper;
import com.info.logger.Logger;
import com.info.model.EntryData;
import com.info.model.FooterModel;
import com.info.model.NotificationCountModel;
import com.info.model.Quote;
import com.info.model.SectorNews;
import com.info.model.Stocks;
import com.info.model.TipsSection;
import com.info.model.stockPrediction.StockPrediction;
import com.info.model.stockPrediction.StockPredictionResponse;
import com.info.tradewyse.databinding.ActivityGraphStockPredictionBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphStockPrediction extends BaseActivity implements OnNotificationReceived {
    private static final String TAG = "GraphStockPrediction";
    LineChart chart;
    Stocks stock;
    String stockName;
    HashMap<Float, String> dummyTime = new HashMap<>();
    TextView txtStockCompany, txtNYTime, txtPriceValue, txtHigh, txtLow;
    Context context;
    String stockCurrentPrice;
    LinearLayout bottomlinearLayout;
    boolean isFromNotificationClick = false;

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

    ActivityGraphStockPredictionBinding graphStockPredictionBinding;
    private CustomProgressBarDialog customProgressBarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_stock_prediction);
        graphStockPredictionBinding = DataBindingUtil.setContentView(this, R.layout.activity_graph_stock_prediction);
        FooterModel footerModel = new FooterModel(true, false, false, false, false);
        graphStockPredictionBinding.setFooterModel(footerModel);
        getNotificationCount();
        stock = getIntent().getExtras().getParcelable("stock");
        stockName = getIntent().getStringExtra("StockName");
        isFromNotificationClick = getIntent().getBooleanExtra("fromNotificationClick", false);
        initializeViews();
        Common.BottomTabColorChange(this,bottomlinearLayout);
        context = this;

        if (stock == null && stockName != null) {
            callGetStockDetail(stockName);
        } else if (stock != null) {
            setValues();
        } else {
            Logger.debug(TAG, "Unable to find data from pending intent trying to fetch from complex pref");
            onBackPressed();
        }

    }

    public void initializeViews() {
        if (stock != null)
            setToolBar(stock.getStockName());
        else
            setToolBar(stockName);
        txtStockCompany = findViewById(R.id.txtStockCompany);
        txtNYTime = findViewById(R.id.txtNYTime);
        txtPriceValue = findViewById(R.id.txtPriceValue);
        txtHigh = findViewById(R.id.txtHigh);
        txtLow = findViewById(R.id.txtLow);
        chart = findViewById(R.id.chart);

        tipRootLayout = findViewById(R.id.rootLayout);
        txtDateTime = findViewById(R.id.txtDateTime);
        txtStockName = findViewById(R.id.txtStockName);
        txtOverallStatus = findViewById(R.id.txtOverallStatus);  //very positive, very negative etc.
        txtStockCompanyName = findViewById(R.id.txtStockCompanyName);
        txtFirstValue = findViewById(R.id.txtFirstValue);
        txtSecondValue = findViewById(R.id.txtSecondValue);
        txtThirdValue = findViewById(R.id.txtThirdValue);
        txtStatusBuySellAvoid = findViewById(R.id.txtStatusBuySellAvoid);
        bottomlinearLayout = findViewById(R.id.bottomView);


    }

    @Override
    public void onBackPressed() {
        if (isFromNotificationClick) {
            Intent mIntent = new Intent(GraphStockPrediction.this, DashBoardActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
        } else {
            finishActivity();
        }
    }

    public void onBackPress(View v) {
        //handleBackPress();
        if (isFromNotificationClick) {
            Intent mIntent = new Intent(GraphStockPrediction.this, DashBoardActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
        } else
            onBackPressed();

    }

    public void setValues() {
        stockCurrentPrice = Common.formatDouble(stock.getStockPrice());
        txtNYTime.setText("NY Time " + DateTimeHelper.getNYTime());
        txtPriceValue.setText(Common.formatDouble(stock.getStockPrice()));
        txtStockCompany.setText(stock.getCompanyName());
        setToolBar(stockName);
        setAITipDetail(stockName);
        getPredictionStockHistoryData(stockName);
    }

    public void callGetStockDetail(final String stockName) {
        // check network is available or not.
        if (!Common.isNetworkAvailable(context)) {
            Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                    context.getResources().getString(R.string.JustLetYouKnow));
            return;
        }
        //showProgressDialog();
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_URL_TRADEWYSE, this);
        Call<Quote> call = apiInterface.getStockDetailData(stockName, Constants.IEX_TOKEN);
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.body() != null) {
                    //dismissProgressDialog();
                    Quote quote = response.body();
                    stock = new Stocks();
                    stock.setStockPrice(quote.getLatestPrice());
                    stock.setStockChange(quote.getChange());
                    stock.setHigh(quote.getHigh());
                    stock.setLow(quote.getLow());
                    stock.setCompanyName(quote.getCompanyName());
                    setValues();

                } else {
                    Toast.makeText(GraphStockPrediction.this, "Something went wrong please try again later", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "GraphActivity callGetStockDetail: onFailure");
            }
        });


    }

    public void getPredictionStockHistoryData(String stockName) {

        Log.d("stockName==", "==" + stockName);


        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL_PREDICTION, GraphStockPrediction.this);
        Call<StockPredictionResponse> call = apiInterface.getStockPredictionData(stockName.trim());
        showProgressDialog();
        call.enqueue(new Callback<StockPredictionResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<StockPredictionResponse> call, Response<StockPredictionResponse> response) {
                dismissProgressDialog();
                if (response.body() != null) {
                    //  List<StockPrediction> stockPredictionList = response.body();
                    StockPredictionResponse stockPredictionResponse = response.body();
                    if (stockPredictionResponse.getStatus() || (stockPredictionResponse.getStockPrediction() != null && stockPredictionResponse.getStockPrediction().size() > 0)) {
                        if (stockPredictionResponse.getStockPrediction() != null && stockPredictionResponse.getStockPrediction().size() > 0) {
                            setStockPredictionChart(stockPredictionResponse.getStockPrediction().get(stockPredictionResponse.getStockPrediction().size() - 1));
                        } else if (!stockPredictionResponse.isSubscribed()) {
                            Intent intent = SubscriptionActivity.Companion.newIntent(getApplicationContext());
                            intent.putExtra(Constants.FROM, Constants.FROM_GRAPH_STOCK_PREDICTION);
                            startActivity(intent);
                            GraphStockPrediction.this.finish();
                        } else {
                            Common.showMessageWithFinishActivity(context, getResources().getString(R.string.prediction_error_message), getResources().getString(R.string.we_are_making_prediction));
                        }
                    } else {
                        if (!stockPredictionResponse.isSubscribed()) {
                            Intent intent = SubscriptionActivity.Companion.newIntent(getApplicationContext());
                            intent.putExtra(Constants.FROM, Constants.FROM_GRAPH_STOCK_PREDICTION);
                            startActivity(intent);
                            GraphStockPrediction.this.finish();
                        } else {

                            Common.showMessageWithFinishActivity(context, getResources().getString(R.string.prediction_error_message), getResources().getString(R.string.we_are_making_prediction));
                        }
                    }
                } else {
                    chart.clear();
                    chart.invalidate();
                    Common.showMessageWithFinishActivity(GraphStockPrediction.this, getString(R.string.InternalServerError), getString(R.string.messageAlert));
                }
            }

            @Override
            public void onFailure(Call<StockPredictionResponse> call, Throwable t) {
                dismissProgressDialog();
                chart.clear();
                chart.invalidate();
                Common.showMessageWithFinishActivity(GraphStockPrediction.this, getString(R.string.InternalServerError), getString(R.string.messageAlert));
                Log.d(Constants.ON_FAILURE_TAG, "GraphStockPredection getPredection: onFailure" + t.getMessage());
            }

        });

    }

    public void showMessage(Context context, String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                if (isFromNotificationClick) {
                    Intent mIntent = new Intent(GraphStockPrediction.this, DashBoardActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mIntent);
                } else {
                    finishActivity();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void finishActivity() {
        GraphStockPrediction.this.finish();
    }

    public void setStockPredictionChart(StockPrediction stockPrediction) {

        String[] datesList = stockPrediction.getDates().split(",");

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        //xAxis.setCenterAxisLabels(true);
        xAxis.setTextSize(27);


        List<ILineDataSet> lineDataSetList = new ArrayList<ILineDataSet>();
        List<LegendEntry> entries = new ArrayList<>();
        LegendEntry entry = new LegendEntry();
        float high = 0, low = 0;

        if (!StringHelper.isEmpty(stockPrediction.getNormalizedPrice())) {
            String[] normalizedPrice = stockPrediction.getNormalizedPrice().split(",");
            LineDataSet normalizedPriceDataSet = getDataSet(datesList, normalizedPrice, Constants.NORMALIZED_PRICE_COLOR, "Technical Model");
            lineDataSetList.add(normalizedPriceDataSet);
            high = getHigh(high, normalizedPrice);
            low = getLow(low, normalizedPrice);
            entry = new LegendEntry();
            entry.label = "Technical Model";
            entry.formColor = setGraphColor(Constants.NORMALIZED_PRICE_COLOR);

            entries.add(entry);
        }


        if (!StringHelper.isEmpty(stockPrediction.getVxxaffected())) {
            String[] vxxAffected = stockPrediction.getVxxaffected().split(",");
            LineDataSet vxxAffectedDataSet = getDataSet(datesList, vxxAffected, Constants.VXX_AFFECTED_COLOR, "VXX Affected");
            lineDataSetList.add(vxxAffectedDataSet);
            high = getHigh(high, vxxAffected);
            low = getLow(low, vxxAffected);
            entry = new LegendEntry();
            entry.label = "VXX Affected";
            entry.formColor = setGraphColor(Constants.VXX_AFFECTED_COLOR);
            entries.add(entry);
        }

        if (!StringHelper.isEmpty(stockPrediction.getNewsAffected())) {
            String[] newsAffected = stockPrediction.getNewsAffected().split(",");
            LineDataSet newsAffectedDataSet = getDataSet(datesList, newsAffected, Constants.NEWS_AFFECTED_COLOR, "News Affected");
            lineDataSetList.add(newsAffectedDataSet);
            high = getHigh(high, newsAffected);
            low = getLow(low, newsAffected);
            entry = new LegendEntry();
            entry.label = "News Affected";
            entry.formColor = setGraphColor(Constants.NEWS_AFFECTED_COLOR);
            entries.add(entry);
        }


        chart.getAxisRight().setAxisMinimum(low);
        chart.getAxisRight().setAxisMaximum(high);
        txtLow.setText("LO: " + Common.formatDouble(low));
        txtHigh.setText("HI: " + Common.formatDouble(high));
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setTextSize(12f);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setAvoidFirstLastClipping(false);

        chart.getAxisRight().setTextColor(ContextCompat.getColor(this, R.color.text_color_white)); // left y-axis
        chart.getAxisRight().setAxisLineWidth(.5F);
        chart.getAxisRight().setGranularityEnabled(true);
        chart.getAxisRight().setGranularity(.5f);
        chart.getAxisRight().setLabelCount(6, false);
        chart.getXAxis().setTextColor(ContextCompat.getColor(this, R.color.text_color_white));
        chart.getXAxis().setAxisLineWidth(.5F);
        chart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.text_color_white));
        chart.getXAxis().setTextSize(12f);
        chart.getAxisLeft().setEnabled(false);
        chart.getLegend().setEnabled(false);

        //chart.getLegend().setCustom(entries);
        chart.getAxisRight().setLabelCount(6, true);
        chart.getAxisLeft().setLabelCount(6, true);
        chart.setExtraOffsets(25F, 0, 10F, 10F);
        xAxis.setYOffset(8);

        // Controlling X axis
        //XAxis xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value
        xAxis.setLabelCount(6, true);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1);


        LimitLine ll1 = new LimitLine(1, "");
        ll1.setLineColor(getResources().getColor(R.color.text_color_white));
        ll1.setLineWidth(1f);
        ll1.enableDashedLine(8f, 8f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        ll1.setTextSize(10f);

        xAxis.removeAllLimitLines();
        xAxis.addLimitLine(ll1);


        // float firstTimeX =
        Log.e("date :", stockPrediction.getDates());
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) {
                    return "";
                }
                int index = (int) value;
                Log.e("date : " + index, DateTimeHelper.parseDateFloat(DateTimeHelper.parseDate(datesList[index].trim()).getTime()));
                return DateTimeHelper.parseDateFloat(DateTimeHelper.parseDate(datesList[index].trim()).getTime());
            }
        };

        xAxis.setValueFormatter(new ClaimsXAxisValueFormatter(datesList));

        CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout, GraphActivity.API_SOURCE_GRAPH_PREDICTION, datesList, stockCurrentPrice);
        // set the marker to the chart
        chart.setMarker(mv);
        // Setting Data
        LineData data = new LineData(lineDataSetList);
        chart.setData(data);
        chart.animateX(250);
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setDrawZeroLine(false);
        chart.getXAxis().setSpaceMin(2.5f);
        chart.invalidate();
    }

    private LineDataSet getDataSet(String[] datesList, String[] valuesArray, int color, String label) {
        ArrayList<Entry> entries = new ArrayList<>();
        if (valuesArray.length > 0) {
            valuesArray[0] = stockCurrentPrice;
        }

        for (int i = 0; i < datesList.length; i++) {
            if (DateTimeHelper.parseDate(datesList[i].trim()) != null) {
                float timeX = DateTimeHelper.parseDate(datesList[i].trim()).getTime();
                Entry entry = new Entry(i, Float.parseFloat(valuesArray[i].trim()));
                entry.setData(new EntryData(label, setGraphColor(color)));
                entries.add(entry);
            }
        }
        LineDataSet dataSet = new LineDataSet(entries, "");

        dataSet.setColor(setGraphColor(color));
        //dataSet.setGradientColor(ContextCompat.getColor(this,R.color.text_color_white),ContextCompat.getColor(this,R.color.graph_color));
        dataSet.setLineWidth(1);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.text_color_white));

        dataSet.setCircleRadius(6.5f);
        dataSet.setCircleHoleRadius(3.5f);
        dataSet.setCircleColor(Color.parseColor("#ffffff"));
        dataSet.setCircleHoleColor(Color.parseColor("#039BE5"));

        // set filled gradient.
        dataSet.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.graph_gradient);
            dataSet.setFillDrawable(drawable);
        } else {
            dataSet.setFillColor(Color.BLACK);
        }

        return dataSet;
    }

    private int setGraphColor(int colorType) {
        int color = 0;
        switch (colorType) {
            case Constants.NEWS_AFFECTED_COLOR:
                color = ContextCompat.getColor(this, R.color.newsAffectedColor);
                break;
            case Constants.VXX_AFFECTED_COLOR:
                color = ContextCompat.getColor(this, R.color.vxxAffectedColor);
                break;
            case Constants.NORMALIZED_PRICE_COLOR:
                color = ContextCompat.getColor(this, R.color.normalizedPriceColor);
                break;
        }
        return color;
    }

    int highValueIndex = 0;

    private float getHigh(float high, String[] values) {
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            float newValue = Converter.parseStringToFloat(value);
            if (newValue > high) {
                high = newValue;
                highValueIndex = i;
            }
        }
        return high;
    }

    private float getLow(float low, String[] values) {
        for (String value : values) {
            float newValue = Converter.parseStringToFloat(value);
            if (low == 0 || newValue < low) {
                low = newValue;
            }
        }
        return low;
    }

    public static void start(Context context, Stocks stock) {
        Intent starter = new Intent(context, GraphStockPrediction.class);
        starter.putExtra("stock", stock);
        starter.putExtra("StockName", stock.getStockName());
        context.startActivity(starter);
    }

    /*
     * show AI tip data for this stock.
     *
     * */
    public void setAITipDetail(String stockName) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, GraphStockPrediction.this);
        Call<List<SectorNews>> call = apiInterface.getSectorNewsSentiment(stockName);
        call.enqueue(new Callback<List<SectorNews>>() {
            @Override
            public void onResponse(Call<List<SectorNews>> call, Response<List<SectorNews>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    ArrayList<SectorNews> tipsArrayList = (ArrayList<SectorNews>) response.body();
                    TipsSection tipsSection = new TipsSection();
                    tipsSection.setSectorNewsList(tipsArrayList);
                    tipsSection.setTipsType(TipsAdapter.AI_TRADE_TIP);
                    tipRootLayout.setVisibility(View.VISIBLE);
                    showAITipDetail(tipsSection);
                } else {
                    tipRootLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<SectorNews>> call, Throwable t) {
                Log.d("failure", "onfailure");
                //progress.setVisibility(View.GONE);
                tipRootLayout.setVisibility(View.INVISIBLE);
                Log.d(Constants.ON_FAILURE_TAG, "GraphActivity setAITipDetail: onFailure");
            }
        });
    }

    String sectorNewsStatus = "";

    public void showAITipDetail(TipsSection section) {
        TipsSection tipSection = section;

        if (tipSection.getSectorNewsList().size() == 0) return;
        SectorNews sectorNews = Converter.calculateSentimentValue(tipSection.getSectorNewsList());
        double tenDayVal = Common.getDounbleValuefromStringforPredectionValue(sectorNews.getAvg10days());
        double fiftyDayVal = Common.getDounbleValuefromStringforPredectionValue(sectorNews.getAvg50days());
        double twoHundredDayVal = Common.getDounbleValuefromStringforPredectionValue(sectorNews.getAvg200days());
        txtStockName.setText(sectorNews.getStockName());
        txtStockCompanyName.setVisibility(View.VISIBLE);

        sectorNews.setStockPrice(stock.getStockPrice());
        sectorNews.setStockChange(stock.getStockChange());
        sectorNews.setCompanyName(stock.getCompanyName());
        String suggestion = Common.calculatedSuggestion(sectorNews.getStockPrice(), sectorNews);
        if (suggestion.toUpperCase().equalsIgnoreCase("avoid")) {
            txtStatusBuySellAvoid.setBackgroundResource(R.drawable.text_backgroud_black);
            txtStatusBuySellAvoid.setTextColor(ContextCompat.getColor(context, R.color.text_color_avoid));
        } else if (suggestion.toUpperCase().equalsIgnoreCase("sell")) {
            txtStatusBuySellAvoid.setBackgroundResource(R.drawable.text_backgroud_red);
            txtStatusBuySellAvoid.setTextColor(ContextCompat.getColor(context, R.color.text_color_sell));
        } else if (suggestion.toUpperCase().equalsIgnoreCase("buy")) {
            txtStatusBuySellAvoid.setBackgroundResource(R.drawable.text_backgroud_green);
            txtStatusBuySellAvoid.setTextColor(ContextCompat.getColor(context, R.color.text_color_buy));
        }

        if (suggestion.length() >= 4)
            txtStatusBuySellAvoid.setEms(4);
        else if (suggestion.length() == 3)
            txtStatusBuySellAvoid.setEms(3);
        else
            txtStatusBuySellAvoid.setEms(2);
        txtStatusBuySellAvoid.setText(suggestion);
        if (StringHelper.isEmpty(suggestion)) {
            txtStatusBuySellAvoid.setVisibility(View.GONE);
        } else {
            txtStatusBuySellAvoid.setVisibility(View.VISIBLE);
        }

        //stock sentiment and sector sentiment agar ye nahi h to kuch ni dikha  te
        //---------set stock nature like natural, positive , negative.. start


        if (sectorNews.getStockSentiment() == null || sectorNews.getStockSentiment().isEmpty()) {
            if (sectorNews.getNewsSectorSentiment() == null || sectorNews.getNewsSectorSentiment().isEmpty()) {
                sectorNewsStatus = "Neutral";
            } else {
                sectorNewsStatus = sectorNews.getNewsSectorSentiment();
            }
        } else {
            sectorNewsStatus = sectorNews.getStockSentiment();
        }
        sectorNewsStatus = sectorNews.getNewsSectorSentiment();
        if (sectorNews.getStockSentiment() != null && !sectorNews.getStockSentiment().isEmpty()) {
            sectorNewsStatus = sectorNews.getStockSentiment();
        }
        if (sectorNewsStatus == null || sectorNewsStatus.isEmpty()) {
            sectorNewsStatus = "";
        }
        if (sectorNewsStatus.equalsIgnoreCase("Negative") || sectorNewsStatus.equalsIgnoreCase("Very Negative")) {
            txtOverallStatus.setTextColor(context.getResources().getColor(R.color.text_color_red));
        } else if (sectorNewsStatus.equalsIgnoreCase("Positive") || sectorNewsStatus.equalsIgnoreCase("Very Positive")) {
            txtOverallStatus.setTextColor(context.getResources().getColor(R.color.text_color_green));
        } else {
            txtOverallStatus.setTextColor(context.getResources().getColor(R.color.text_color_black));
        }

        txtOverallStatus.setText(sectorNewsStatus);
        //---------set stock nature like natural, positive , negative.. end
        txtDateTime.setText(DateTimeHelperElapsed.formatDateForAI(sectorNews.getSmaGenerationDate()) + "  "
                + context.getResources().getString(R.string.TimeZone));
        // click to AI tip
        setAITipsDetailClick(tipRootLayout, tipSection, suggestion);
        if (sectorNews.getStockPrice() >= tenDayVal) {
            txtFirstValue.setText(">10D");
            txtFirstValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
        } else {
            txtFirstValue.setText("<10D");
            txtFirstValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
        }

        if (sectorNews.getStockPrice() >= fiftyDayVal) {
            txtSecondValue.setText(">50D");
            txtSecondValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
        } else {
            txtSecondValue.setText("<50D");
            txtSecondValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
        }
        if (sectorNews.getStockPrice() >= twoHundredDayVal) {
            txtThirdValue.setText(">200D");
            txtThirdValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
        } else {
            txtThirdValue.setText("<200D");
            txtThirdValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
        }
        // set color to grey if values are --.
        if (!Common.checkStringIsDouble(sectorNews.getAvg10days())) {
            txtFirstValue.setTextColor(context.getResources().getColor(R.color.text_color_dark_grey));
        }
        if (!Common.checkStringIsDouble(sectorNews.getAvg50days())) {
            txtSecondValue.setTextColor(context.getResources().getColor(R.color.text_color_dark_grey));
        }
        if (!Common.checkStringIsDouble(sectorNews.getAvg200days())) {
            txtThirdValue.setTextColor(context.getResources().getColor(R.color.text_color_dark_grey));
        }
    }


    public void setAITipsDetailClick(RelativeLayout tipRootLayout, final TipsSection tips, String buySellAvoidStatus) {
        tipRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AITradeTipDetailActivity.startActivity(context, tips, buySellAvoidStatus, sectorNewsStatus, stock.getStockPrice(), stock.getStockChange());
            }
        });
    }

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

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("fromNotificationClick", false)) {
            if (stock == null && stockName != null) {
                callGetStockDetail(stockName);
                customProgressBarDialog.dismiss();
            }
        }
    }


    @Override
    public void onNotificationReceived(Intent intent) {
        if (intent.getBooleanExtra("fromNotificationClick", false)) {
            if (stock == null && stockName != null) {
                callGetStockDetail(stockName);
                customProgressBarDialog.dismiss();
            }
        }
    }
   }
