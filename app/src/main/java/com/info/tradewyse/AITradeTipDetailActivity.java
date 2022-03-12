package com.info.tradewyse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Converter;
import com.info.commons.DateTimeHelperElapsed;
import com.info.commons.StringHelper;
import com.info.model.Comment;
import com.info.model.Quote;
import com.info.model.SectorNews;
import com.info.model.TipsSection;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AITradeTipDetailActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<Comment> commentList = new ArrayList<>();
    Context context;
    TextView txtCloseDetail;
    RelativeLayout newsRootLayout, detailLayout;
    TextView txtNews1, txtNews2, txtNews3, txtNews4, txtNews5, txtHeading1, txtTime1, txtHeading2, txtTime2, txtHeading3, txtTime3, txtHeading4, txtTime4,
            txtHeading5, txtTime5;
    TextView txtSubTitleDetail, txtDescriptionDetail;
    TextView txtStatus;
    TextView txtStatus1;
    TextView txtCurrentValue, txtChange, txtTenDaysValue, txtFiftyDaysValue, txtTwoHundredDaysValue;
    TextView txtNews1Detail,txtNews2Detail,txtNews3Detail,txtNews4Detail,txtNews5Detail;
    TextView txtDay, txtType;
    TextView txtStockName, txtStockCompany,txtNoOfNewSources;
    TipsSection tips;
    List<SectorNews> sectorNewsList;
    ProgressDialog progressDialog;
    String stockCompany;
    String buySellAvoidStatus;
    String sectorNewsStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_trade_tip_detail_layout);
        context = this;
        setToolBar(getResources().getString(R.string.tipDetails));
        newsRootLayout = findViewById(R.id.newsRootLayout);
        detailLayout = findViewById(R.id.detailLayout);
        txtCloseDetail = findViewById(R.id.txtCloseDetail);
        txtStatus = findViewById(R.id.txtStatus);
        txtNews1 = findViewById(R.id.txtNews1);
        txtNews2 = findViewById(R.id.txtNews2);
        txtNews3 = findViewById(R.id.txtNews3);
        txtNews4 = findViewById(R.id.txtNews4);
        txtNews5 = findViewById(R.id.txtNews5);
        context = this;

        txtNews1Detail = findViewById(R.id.txtNews1Detail);
        txtNews2Detail = findViewById(R.id.txtNews2Detail);
        txtNews3Detail = findViewById(R.id.txtNews3Detail);
        txtNews4Detail = findViewById(R.id.txtNews4Detail);
        txtNews5Detail = findViewById(R.id.txtNews5Detail);
        txtNoOfNewSources = findViewById(R.id.txtNoOfNewSources);

        txtHeading1 = findViewById(R.id.txtHeading1);
        txtTime1 = findViewById(R.id.txtTime1);
        txtHeading2 = findViewById(R.id.txtHeding2);
        txtTime2 = findViewById(R.id.txtTime2);
        txtHeading3 = findViewById(R.id.txtHeding3);
        txtTime3 = findViewById(R.id.txtTime3);
        txtHeading4 = findViewById(R.id.txtHeading4);
        txtTime4 = findViewById(R.id.txtTime4);
        txtHeading5 = findViewById(R.id.txtHeading5);
        txtTime5 = findViewById(R.id.txtTime5);
        txtType = findViewById(R.id.txtType);
        txtSubTitleDetail = findViewById(R.id.txtSubTitleDetail);
        txtDescriptionDetail = findViewById(R.id.txtDescriptionDetail);

        txtStatus1 = findViewById(R.id.txtStatus1);
        txtCurrentValue = findViewById(R.id.txtCurrentValue);
        txtChange = findViewById(R.id.txtChange);
        txtTenDaysValue = findViewById(R.id.txtTenDaysValue);
        txtFiftyDaysValue = findViewById(R.id.txtFiftyDaysValue);
        txtTwoHundredDaysValue = findViewById(R.id.txtTwoHundredDaysValue);
        txtDay = findViewById(R.id.txtDay);
        txtStockName = findViewById(R.id.txtStockName);
        txtStockCompany = findViewById(R.id.txtStockCompany);
        txtNews1.setOnClickListener(this);
        txtNews2.setOnClickListener(this);
        txtNews3.setOnClickListener(this);
        txtNews4.setOnClickListener(this);
        txtNews5.setOnClickListener(this);
        txtCloseDetail.setOnClickListener(this);

        tips = getIntent().getExtras().getParcelable("tips");
        buySellAvoidStatus = getIntent().getStringExtra("buySellAvoidStatus");
        sectorNewsStatus = getIntent().getStringExtra("sectorNewsStatus");
        change = getIntent().getDoubleExtra("stockChange",0);
        stockLatestPrice = getIntent().getDoubleExtra("stockCurrentPrice",0);
        sectorNewsList = tips.getSectorNewsList();
        stockCompany = getCompanyNameFromSectorNewsList(sectorNewsList);
        setFormData(sectorNewsList);

        if (sectorNewsList!=null && sectorNewsList.size() > 0) {

        } else {
            Toast.makeText(AITradeTipDetailActivity.this, "Something went wrong please try again later", Toast.LENGTH_SHORT).show();
        }
        SectorNews sectorNews = sectorNewsList.get(0);
        sectorNews.getSectorNews();



        String suggestion=Common.calculatedSuggestion(sectorNews.getStockPrice(),sectorNews).toUpperCase();
        suggestion = buySellAvoidStatus;
        if (suggestion.length() >= 4)
            txtStatus1.setEms(4);
        else if (suggestion.length() == 3)
            txtStatus1.setEms(3);
        else
            txtStatus1.setEms(2);
        txtStatus1.setText(buySellAvoidStatus);
        if(StringHelper.isEmpty(suggestion)){
            txtStatus1.setVisibility(View.GONE);
        }
        if (suggestion.equalsIgnoreCase("Avoid")) {
            txtStatus1.setBackgroundResource(R.drawable.text_backgroud_black);
            txtStatus1.setTextColor(ContextCompat.getColor(context, R.color.color_text_avoid));
        } else if (suggestion.equalsIgnoreCase("Sell")) {
            txtStatus1.setTextColor(ContextCompat.getColor(context, R.color.color_text_sell));
            txtStatus1.setBackgroundResource(R.drawable.text_backgroud_red);
        } else if (suggestion.equalsIgnoreCase("Buy")) {
            txtStatus1.setBackgroundResource(R.drawable.text_backgroud_green);
           txtStatus1.setTextColor(ContextCompat.getColor(context, R.color.color_text_buy));
        }

    }

    public  String getCompanyNameFromSectorNewsList(List<SectorNews> sectorNewsList){
        String companyName="";
        for(int i=0;i<sectorNewsList.size();i++){
            if(sectorNewsList.get(i).getCompanyName()!=null && sectorNewsList.get(i).getCompanyName()!=""){
                companyName = sectorNewsList.get(i).getCompanyName();
                break;
            }
        }
        return companyName;
    }
    public void showProgressDialog() {
        if (context != null) {
            progressDialog = new ProgressDialog(context, R.style.style_progress_dialog);
            progressDialog.setMessage("Loading....");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

    }

    public void dismissProgressDialog() {
        if (context != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }



    public static void startActivity(Context context, TipsSection tips,String buySellAvoidStatus,String sectorNewsStatus,double stockCurrentPrice, double stockChange) {
        Intent starter = new Intent(context, AITradeTipDetailActivity.class);
        starter.putExtra("tips", tips);
        starter.putExtra("buySellAvoidStatus", buySellAvoidStatus);
        starter.putExtra("sectorNewsStatus", sectorNewsStatus);
        starter.putExtra("stockCurrentPrice", stockCurrentPrice);
        starter.putExtra("stockChange", stockChange);
        context.startActivity(starter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtNews1:
            //   Intent i = new Intent();
                Intent intent1 = new Intent(context, NewWebViewActivity.class);
                intent1.putExtra("SectorNewsURL", sectorNewsList.get(0).getSectorNewsURL());
                startActivity(intent1);
                break;
            case R.id.txtNews2:
                Intent intent2 = new Intent(context, NewWebViewActivity.class);
                intent2.putExtra("SectorNewsURL", sectorNewsList.get(1).getSectorNewsURL());
                startActivity(intent2);
                break;
            case R.id.txtNews3:
                Intent intent3 = new Intent(context, NewWebViewActivity.class);
                intent3.putExtra("SectorNewsURL", sectorNewsList.get(2).getSectorNewsURL());
                startActivity(intent3);

                break;
            case R.id.txtNews4:
                Intent intent4 = new Intent(context, NewWebViewActivity.class);
                intent4.putExtra("SectorNewsURL", sectorNewsList.get(3).getSectorNewsURL());
                startActivity(intent4);
                break;
            case R.id.txtNews5:
                Intent intent5 = new Intent(context, NewWebViewActivity.class);
                intent5.putExtra("SectorNewsURL", sectorNewsList.get(4).getSectorNewsURL());
                startActivity(intent5);
                break;

            case R.id.txtCloseDetail: {
                break;
            }
        }
    }

    ArrayList<SectorNews> tipsArrayList = new ArrayList<>();


    public void setNewsDetail() {

        for (int i = 0; i < sectorNewsList.size(); i++) {
            switch (i) {
                case 0:
                    setNews(txtHeading1, txtNews1,txtNews1Detail,txtTime1, sectorNewsList.get(i));
                    break;
                case 1:
                    setNews(txtHeading2, txtNews2,txtNews2Detail,txtTime2, sectorNewsList.get(i));
                    break;
                case 2:
                    setNews(txtHeading3, txtNews3,txtNews3Detail,txtTime3, sectorNewsList.get(i));
                    break;
                case 3:
                    setNews(txtHeading4, txtNews4,txtNews4Detail,txtTime4, sectorNewsList.get(i));
                    break;
                case 4:
                    setNews(txtHeading5, txtNews5,txtNews5Detail,txtTime5, sectorNewsList.get(i));
                    break;
            }
        }

    }

    public void setFormData(List<SectorNews> tipsArrayList) {
       // sectorNewsList = getTopFiveNewsForSMA(sectorNewsList,sectorNewsStatus);
        setNewsDetail();
        txtStockName.setText(tipsArrayList.get(0).getStockName());
        txtStockCompany.setText(stockCompany);
        txtType.setText(tipsArrayList.get(0).getSectorName());
        double tenDayVal = Common.getDounbleValuefromStringforPredectionValue(tipsArrayList.get(0).getAvg10days());
        double fiftyDayVal = Common.getDounbleValuefromStringforPredectionValue(tipsArrayList.get(0).getAvg50days());
        double twoHundredDayVal = Common.getDounbleValuefromStringforPredectionValue(tipsArrayList.get(0).getAvg200days());
        if( sectorNewsList.get(0).getSectorName().equalsIgnoreCase("market news"))
        {
             txtType.setText( sectorNewsList.get(0).getSectorName());
        }else{
            txtType.setText( sectorNewsList.get(0).getSectorName()+ " Sector News");
        }

//+context.getResources().getString(R.string.TimeZone)

        txtDay.setText(DateTimeHelperElapsed.formatDateForAI(tipsArrayList.get(0).getSmaGenerationDate())+" "+context.getResources().getString(R.string.TimeZone));
        txtStatus1.setText(tipsArrayList.get(0).getResult());
        if (tipsArrayList.get(0).getResult().equalsIgnoreCase("Avoid")) {
            txtStatus1.setBackgroundResource(R.drawable.text_backgroud_black);
            txtStatus1.setTextColor(ContextCompat.getColor(context, R.color.color_text_avoid));
        } else if (tipsArrayList.get(0).getResult().equalsIgnoreCase("Sell")) {
            txtStatus1.setBackgroundResource(R.drawable.text_backgroud_red);
            txtStatus1.setTextColor(ContextCompat.getColor(context, R.color.color_text_sell));
        } else if (tipsArrayList.get(0).getResult().equalsIgnoreCase("Buy")) {
            txtStatus1.setBackgroundResource(R.drawable.text_backgroud_green);
            txtStatus1.setTextColor(ContextCompat.getColor(context, R.color.color_text_buy));
        }

        txtCurrentValue.setText("$" + Common.formatDouble(stockLatestPrice )+ "");
        txtChange.setText(Common.formatDouble(Math.abs(change))+ "");
        if (change < 0.0) {
            txtChange.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_price_low, 0);
            txtChange.setTextColor(ContextCompat.getColor(context, R.color.text_color_red));
        } else {
            txtChange.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_price_high, 0);
            txtChange.setTextColor(ContextCompat.getColor(context, R.color.text_color_green));
        }


        Resources res = getResources();
        String text = String.format(res.getString(R.string.sentimentBasedOnNewSources), sectorNewsList.get(0).getSectorNews() );
        txtNoOfNewSources.setText(text);





        //---------set stock nature like natural, positive , negative.. start
        String s = sectorNewsStatus;//"Neutral";
        if (s.equalsIgnoreCase("Negative") || s.equalsIgnoreCase("Very Negative")) {
            txtStatus.setTextColor(context.getResources().getColor(R.color.text_color_red));
        } else if (s.equalsIgnoreCase("Positive") || s.equalsIgnoreCase("Very Positive")) {
            txtStatus.setTextColor(context.getResources().getColor(R.color.text_color_green));
        } else {
            txtStatus.setTextColor(context.getResources().getColor(R.color.text_color_black));
        }

        txtStatus.setText(s);
        setColorOfNewsTitles();
        setDayValuesColor(tips);
    }

    private void setDayValuesColor(TipsSection tips){
        SectorNews sectorNews = Converter.calculateSentimentValue(tips.getSectorNewsList());
        double tenDayVal = Common.getDounbleValuefromStringforPredectionValue(sectorNews.getAvg10days());
        double fiftyDayVal = Common.getDounbleValuefromStringforPredectionValue(sectorNews.getAvg50days());
        double twoHundredDayVal = Common.getDounbleValuefromStringforPredectionValue(sectorNews.getAvg200days());


        //-------set color of 10 day, 50 day, 200 day values---------start
        if (sectorNews.getStockPrice() >= tenDayVal) {
            txtTenDaysValue.setText(">");
            txtTenDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
        } else {
            txtTenDaysValue.setText("<");
            txtTenDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
        }
        if (sectorNews.getStockPrice() >= fiftyDayVal) {
            txtFiftyDaysValue.setText(">");
            txtFiftyDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
        } else {
            txtFiftyDaysValue.setText("<");
            txtFiftyDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
        }
        if (sectorNews.getStockPrice() >= twoHundredDayVal) {
            txtTwoHundredDaysValue.setText(">");
            txtTwoHundredDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
        } else {
            txtTwoHundredDaysValue.setText("<");
            txtTwoHundredDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
        }
        //-------set color of 10 day, 50 day, 200 day values---------start

        //set value to label
        if (Common.checkStringIsDouble(sectorNews.getAvg10days())) {
            txtTenDaysValue.append("$" + Common.formatDouble(Double.parseDouble(sectorNews.getAvg10days())));
        } else {
            txtTenDaysValue.setText(context.getResources().getString(R.string.dashDash));
            txtTenDaysValue.setTextColor(context.getResources().getColor(R.color.color_text_dark_layout));
        }
        if (Common.checkStringIsDouble(sectorNews.getAvg50days())) {
            txtFiftyDaysValue.append("$" + Common.formatDouble(Double.parseDouble(sectorNews.getAvg50days())));
        } else {
            txtFiftyDaysValue.setText(context.getResources().getString(R.string.dashDash));
            txtFiftyDaysValue.setTextColor(context.getResources().getColor(R.color.color_text_dark_layout));
        }
        if (Common.checkStringIsDouble(sectorNews.getAvg200days())) {
            txtTwoHundredDaysValue.append("$" + Common.formatDouble(Double.parseDouble(sectorNews.getAvg200days())));
        } else {
            txtTwoHundredDaysValue.setText(context.getResources().getString(R.string.dashDash));
            txtTwoHundredDaysValue.setTextColor(context.getResources().getColor(R.color.color_text_dark_layout));
        }
    }
    private void setColorOfNewsTitles(){
        for (int i = 0; i < sectorNewsList.size(); i++) {
            switch (i) {
                case 0:
                    if(!sectorNewsList.get(0).getSectorNewsURL().isEmpty()){
                        txtNews1.setTextColor(getResources().getColor(R.color.button_dark_bg));
                    }else{
                        txtNews1.setTextColor(getResources().getColor(R.color.color_small_text__dark_layout));
                    }
                    break;
                case 1:
                    if(!sectorNewsList.get(1).getSectorNewsURL().isEmpty()){
                        txtNews1.setTextColor(getResources().getColor(R.color.button_dark_bg));
                    }else{
                        txtNews1.setTextColor(getResources().getColor(R.color.color_small_text__dark_layout));
                    }
                    break;
                case 2:
                    if(!sectorNewsList.get(2).getSectorNewsURL().isEmpty()){
                        txtNews1.setTextColor(getResources().getColor(R.color.button_dark_bg));
                    }else{
                        txtNews1.setTextColor(getResources().getColor(R.color.color_small_text__dark_layout));
                    }
                    break;
                case 3:
                    if(!sectorNewsList.get(3).getSectorNewsURL().isEmpty()){
                        txtNews1.setTextColor(getResources().getColor(R.color.button_dark_bg));
                    }else{
                        txtNews1.setTextColor(getResources().getColor(R.color.color_small_text__dark_layout));
                    }
                    break;
                case 4:
                    if(!sectorNewsList.get(4).getSectorNewsURL().isEmpty()){
                        txtNews1.setTextColor(getResources().getColor(R.color.button_dark_bg));
                    }else{
                        txtNews1.setTextColor(getResources().getColor(R.color.color_small_text__dark_layout));
                    }
                    break;

            }
        }

    }

    private void setNews(TextView txtHeading, TextView txtNews,TextView txtDetail,TextView txtTime, SectorNews sectorNews) {
        txtHeading.setText(sectorNews.getNewsSourceName());
        txtNews.setText(sectorNews.getNewsTitle());
        txtDetail.setText(sectorNews.getNewsSummary());
        txtTime.setText(DateTimeHelperElapsed.formatDateForAI(sectorNews.getNewsPublishDate())+"  "
                +context.getResources().getString(R.string.TimeZone));
        //visible the items.
        txtHeading.setVisibility(View.VISIBLE);
        txtNews.setVisibility(View.VISIBLE);
        txtDetail.setVisibility(View.VISIBLE);
        txtTime.setVisibility(View.VISIBLE);
    }

    double stockLatestPrice;
    double change;
// use to get stock letest price by using iex cloud api
    public void getStockForTipsDetail(TipsSection defaultTip) {

        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_URL_TRADEWYSE, AITradeTipDetailActivity.this);
        Call<Quote> call = apiInterface.getStockDetailData(defaultTip.getSectorNewsList().get(1).getStockName(), "pk_ce90f5bb6d66478991993248d4c1f355");
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                dismissProgressDialog();
                if (response.body() != null) {

                    stockLatestPrice = response.body().getLatestPrice();
                    change = response.body().getChange();
                    setFormData(tipsArrayList);
                }
            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                //swipeRefreshLayout.setRefreshing(false);
                dismissProgressDialog();
            }
        });

    }

    /**
     * This function I will use to get top 5 news based on sectorNews status.
     * @param newsList
     * @param status
     * @return
     */
    public  List<SectorNews> getTopFiveNewsForSMA(List<SectorNews> newsList,String status){
        ArrayList<SectorNews> sectorNewsItemsMatch = new ArrayList<>();
        ArrayList<SectorNews> sectorNewsItemsNotMatch = new ArrayList<>();
        if(status.equalsIgnoreCase("Positive") || status.equalsIgnoreCase("Very Positive")){
            for(SectorNews news: newsList){
                if(news.getNewsSectorSentiment().equalsIgnoreCase("Positive") ||
                        news.getNewsSectorSentiment().equalsIgnoreCase("Very Positive")){
                    sectorNewsItemsMatch.add(news);
                }else{
                    sectorNewsItemsNotMatch.add(news);
                }
            }
        }else if (status.equalsIgnoreCase("Negative") || status.equalsIgnoreCase("Very Negative")) {
            for (SectorNews news : newsList) {
                if (news.getNewsSectorSentiment().equalsIgnoreCase("Negative") ||
                        news.getNewsSectorSentiment().equalsIgnoreCase("Very Negative")) {
                    sectorNewsItemsMatch.add(news);
                }else{
                    sectorNewsItemsNotMatch.add(news);
                }
            }
        } else if(status.equalsIgnoreCase("Neutral") || status.equalsIgnoreCase("null")){
            for (SectorNews news : newsList) {
                return newsList;
            }
        }
        if(sectorNewsItemsMatch.size()!=newsList.size()){
            sectorNewsItemsMatch.addAll(sectorNewsItemsNotMatch);
        }
        return sectorNewsItemsMatch;
    }


}
