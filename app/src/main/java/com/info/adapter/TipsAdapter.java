package com.info.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.Converter;
import com.info.commons.DateTimeHelperElapsed;
import com.info.commons.FlagReasonOptionUtility;
import com.info.commons.LikeHandler;
import com.info.commons.PinHandler;
import com.info.commons.StringHelper;
import com.info.commons.TipHideHandler;
import com.info.commons.TradWyseSession;
import com.info.dao.DataRepository;
import com.info.dao.PriceCallback;
import com.info.dialog.WhatIsMacdDialog;
import com.info.interfaces.FlagOptionSelectListener;
import com.info.interfaces.HideTipResponseListener;
import com.info.interfaces.LikeResponseListener;
import com.info.interfaces.PinResponseListener;
import com.info.model.BuyStock;
import com.info.model.FlagResponse;
import com.info.model.SectorNews;
import com.info.model.SellStock;
import com.info.model.StockPrice;
import com.info.model.TipResponse;
import com.info.model.Tips;
import com.info.model.TipsSection;
import com.info.model.User;
import com.info.tradewyse.AITradeTipDetailActivity;
import com.info.tradewyse.MacdAlertDetailsActivity;
import com.info.tradewyse.ProfileTabbedActivity;
import com.info.tradewyse.R;
import com.info.tradewyse.SearchStocksActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static int ALL_TIP = -1;
    public static int MENTOR_TIP = 0;
    public static int AI_TRADE_TIP = 1;
    ArrayList<TipsSection> tipsList = new ArrayList<>();
    public static Context context;
    LinearLayoutManager linearLayoutManager;
    TradWyseSession tradWyseSession;
    StocksAdapter stocksAdapter;
    boolean isMentor = false;
    CallTipDetailEventListener listener;
    private ArrayList<BuyStock> buyStockArrayList = new ArrayList<>();
    private ArrayList<SellStock> sellStockArrayList = new ArrayList<>();
    private ArrayList<TipsSection> tipCrossoverResponseAfterMacD = new ArrayList<>();
    private String buyGif = "", sellGif = "";
    private int filter;
    private String selectedScreen = "";
    private boolean macdCardsShouldShow = true;
    private LinearLayout layoutBuyRootLayout;
    public static int SCROLLED_POSITION = 0;
    private TipsAfterMacDAdapter tipsAfterMacdAdapter;

    public TipsAdapter(ArrayList<TipsSection> tipsList, StocksAdapter stocksAdapter, Context context,
                       LinearLayoutManager linearLayoutManager, CallTipDetailEventListener listener,
                       ArrayList<BuyStock> buyStockArrayList, ArrayList<SellStock> sellStockArrayList,
                       String buyGif, String sellGif, ArrayList<TipsSection> tipCrossoverResponseAfterMacD,
                       int filter, String selectedScreen) {
        tradWyseSession = TradWyseSession.getSharedInstance(context);
        String isMentorString = tradWyseSession.getIsMentor();
        this.listener = listener;
        if (!StringHelper.isEmpty(isMentorString) && isMentorString.equalsIgnoreCase("true")) {
            isMentor = true;
            TipsSection tipsSection = new TipsSection();
            tipsSection.setTipsType(2);
            this.tipsList.add(tipsSection);
        } else {
            TipsSection tipsSection = new TipsSection();
            tipsSection.setTipsType(2);
            this.tipsList.add(tipsSection);
        }

        if (tipsList != null && tipsList.size() > 0) {
            this.tipsList.addAll(tipsList);
        }

        if (tipCrossoverResponseAfterMacD != null && tipCrossoverResponseAfterMacD.size() > 0) {
            this.tipCrossoverResponseAfterMacD.addAll(tipCrossoverResponseAfterMacD);
        }

        this.context = context;
        this.stocksAdapter = stocksAdapter;
        this.linearLayoutManager = linearLayoutManager;
        // this.tipsList = sortAITip();
        this.buyStockArrayList = buyStockArrayList;
        this.sellStockArrayList = sellStockArrayList;
        this.buyGif = buyGif;
        this.sellGif = sellGif;
        this.filter = filter;
        this.selectedScreen = selectedScreen;
    }

    public ArrayList<TipsSection> getTipAdapterData() {
        return this.tipsList;
    }

    public ArrayList<TipsSection> getTipAdapterDataAfterMacd() {
        return this.tipCrossoverResponseAfterMacD;
    }

    public TipsAfterMacDAdapter getAdapterAfterMacd() {
        return this.tipsAfterMacdAdapter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView;

        switch (viewType) {

            case 11:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_horizontal_header, viewGroup, false);
                return new HeaderHorizontalViewHolder(itemView);

            case 12:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_vertical_header, viewGroup, false);
                return new HeaderVerticalViewHolder(itemView);

            case 1:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.item_tips_horizontal, viewGroup, false);
                return new TipsViewHolderHorizontal(itemView);

            case 2:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.item_tips_vertical, viewGroup, false);
                return new TipsViewHolderVertical(itemView);
            case 3:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.ai_tradetip_item_horizontal, viewGroup, false);
                return new AMDTipsViewHolderHorizontal(itemView);
            case 4:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.mentor_list_item, viewGroup, false);
                return new AMDTipsViewHolderVertical(itemView);
            /*case 5:
                itemView = LayoutInflater.from(context).inflate(R.layout.item_add_tips_vertical, viewGroup, false);
                return new AddTipViewModel(itemView);
            case 6:
                itemView = LayoutInflater.from(context).inflate(R.layout.item_add_tips_horizontal, viewGroup, false);
                return new AddTipViewModel(itemView);*/
        }
        itemView = LayoutInflater.from(context).inflate(R.layout.empty_view, viewGroup, false);
        return new EmptyViewModel(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder tipsViewHolder, @SuppressLint("RecyclerView") int i) {
        TipsSection tipsSection = tipsList.get(i);

        if (tipsViewHolder instanceof TipsViewHolderHorizontal) {
            Tips tips = tipsSection.getTips().getTip();
            TipsViewHolderHorizontal viewHolder = (TipsViewHolderHorizontal) tipsViewHolder;

            setTipsDetailClick(viewHolder.tipRootLayout, tipsSection.getTips(), i);
            viewHolder.txtStockName.setText(tips.getStockName());
            setFlagIconClick(viewHolder.flagIcon, tips);
            viewHolder.txtEntryPoint.setText(StringHelper.getValue(Common.formatDouble(tips.getEntryPoint()), "--"));
            viewHolder.txtExitPoint.setText(StringHelper.getValue(Common.formatDouble(tips.getExitpoint()), "--"));
            viewHolder.txtStopPoint.setText(StringHelper.getValue(Common.formatDouble(tips.getStopPoint()), "--"));
            viewHolder.txtCreateTipPrice.setText(StringHelper.getValue(Common.formatDouble(tips.getStockPrice()), "--"));

            viewHolder.txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileTabbedActivity.starProfileTabbedtActivityForResult(context, false, tips.getAppUser().getUserName(), tips.getAppUser().getId(), 104, Constants.HOME);
                }
            });
            viewHolder.imgProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileTabbedActivity.starProfileTabbedtActivityForResult(context, false, tips.getAppUser().getUserName(), tips.getAppUser().getId(), 104, Constants.HOME);
                }
            });
            // on Tip list for now hide the tip icon for always.
            viewHolder.flagIcon.setVisibility(View.GONE);
               User user = tips.getAppUser();

            DataRepository.getStockPrice(context, tips.getStockName(), data -> {
                if (data.getStockPrice() != tips.getStockPrice()) {
                    Log.e("stockprice", data.getStockName() + " " + data.getStockPrice() + "");
                    tips.setStockPrice(data.getStockPrice());
                    tips.setStockChange(data.getStockChange());
                    tips.setCompanyName(data.getCompanyName());
                    viewHolder.txtCreateTipPrice.setText(StringHelper.getValue(Common.formatDouble(tips.getStockPrice()), "--"));
//                    setChangeValue(tips.getCreateTipPrice(), tips.getStockPrice(), viewHolder.txtChangeValue, viewHolder.txtCurrentPrice);
                    notifyItemChanged(viewHolder.getAdapterPosition());
                }
            });

            viewHolder.txtName.setText(getName(user));
            setUserImage(viewHolder.imgProfile, tips);
            viewHolder.txtTimeAgo.setText(DateTimeHelperElapsed.toString(tips.getCreatedOn(), "MMM dd, hh:mm a") + "  "
                    + context.getResources().getString(R.string.TimeZone));

            String stockSuggestion;
            if (tips.getEntryPoint() < tips.getExitpoint()) {
                stockSuggestion = "Buy";
                viewHolder.txtBuySell.setBackgroundResource(R.drawable.text_backgroud_green);
                viewHolder.txtBuySell.setTextColor(Color.parseColor("#1e841c"));

            } else if (tips.getEntryPoint() > tips.getExitpoint()) {
                stockSuggestion = "Sell";
                viewHolder.txtBuySell.setBackgroundResource(R.drawable.text_backgroud_red);
                viewHolder.txtBuySell.setTextColor(Color.parseColor("#a94018"));
            } else {
                stockSuggestion = "Avoid";
                viewHolder.txtBuySell.setBackgroundResource(R.drawable.text_backgroud_black);
                viewHolder.txtBuySell.setTextColor(Color.parseColor("#707890"));
            }
            if (stockSuggestion.length() >= 4)
                viewHolder.txtBuySell.setEms(4);
            else if (stockSuggestion.length() == 3)
                viewHolder.txtBuySell.setEms(3);
            else
                viewHolder.txtBuySell.setEms(2);

            viewHolder.txtBuySell.setText(stockSuggestion);

            Common.setEntryExitColor(context, tips.getEntryPoint(), tips.getExitpoint(), tips.getStopPoint(), tips.getStockPrice(), tips.getCreateTipPrice(), tips.getStockSuggestion(), viewHolder.txtEntryPoint, viewHolder.txtExitPoint, viewHolder.txtStopPoint);

        }

        else if (tipsViewHolder instanceof TipsViewHolderVertical) {

            TipResponse tipResponse = tipsSection.getTips();
            Tips tips = tipResponse.getTip();
            TipsViewHolderVertical viewHolder = (TipsViewHolderVertical) tipsViewHolder;
            setFlagIconClick(viewHolder.flagIcon, tips);
            setTipsDetailClick(viewHolder.tipRootLayout, tipResponse, i);
            viewHolder.txtComment.setText(tips.getComment());
            viewHolder.txtStockName.setText(tips.getStockName());
            viewHolder.txtStockCompany.setText(tips.getCompanyName());

            // on Tip list for now hide the tip icon for always.
            viewHolder.flagIcon.setVisibility(View.GONE);

            viewHolder.txtLikeCount.setText(String.valueOf(tipResponse.getLikeCount()));
            viewHolder.txtPin.setText(String.valueOf(tipResponse.getPinCount()));
            viewHolder.txtNoOfComments.setText(String.valueOf(tipResponse.getCommentCount()));

            LikeHandler likeHandler = new LikeHandler(context, viewHolder.imgLike, tips.getId());
            LikeResponseListener likeResponseListener = likeResponse -> {
                if (likeResponse) {
                    tipResponse.setLikeCount(tipResponse.getLikeCount() + 1);
                    viewHolder.txtLikeCount.setText(String.valueOf(tipResponse.getLikeCount()));
                    viewHolder.imgLike.setImageResource(R.drawable.ic_like_fill);
                } else {
                    if (tipResponse.getLikeCount() > 0) {
                        tipResponse.setLikeCount(tipResponse.getLikeCount() - 1);
                        viewHolder.txtLikeCount.setText(String.valueOf(tipResponse.getLikeCount()));
                    }
                    viewHolder.imgLike.setImageResource(R.drawable.ic_like);
                }
                tipResponse.setUserLikeStatus(likeResponse);
            };
            if (tipResponse.isUserLikeStatus()) {
                viewHolder.imgLike.setImageResource(R.drawable.ic_like_fill);
            } else {
                viewHolder.imgLike.setImageResource(R.drawable.ic_like);
            }


            // Like button click event.
            viewHolder.imgLike.setOnClickListener(view -> {
                if (tipResponse.isUserLikeStatus()) {
                    likeHandler.unLikeTip(likeResponseListener);
                } else {
                    likeHandler.likeTip(likeResponseListener);
                }
            });


            viewHolder.imgComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                      listener.onTipDetailCallEvent(context, tipResponse, true, i, false);
                }
            });

            // pin response handling.
            PinHandler pinHandler = new PinHandler(context, viewHolder.imgPin, tips.getId());
            PinResponseListener pinResponseListener = pinResponse -> {
                if (pinResponse) {
                    tipResponse.setPinCount(tipResponse.getPinCount() + 1);
                    viewHolder.txtPin.setText(String.valueOf(tipResponse.getPinCount()));
                    viewHolder.imgPin.setImageResource(R.drawable.ic_pin_save);
                } else {
                    if (tipResponse.getPinCount() > 0) {
                        tipResponse.setPinCount(tipResponse.getPinCount() - 1);
                        viewHolder.txtPin.setText(String.valueOf(tipResponse.getPinCount()));
                    }
                    viewHolder.imgPin.setImageResource(R.drawable.ic_tag);
                }
                tipResponse.setUserPinStatus(pinResponse);
            };
            // Pin button click event.
            viewHolder.imgPin.setOnClickListener(view -> {
                //handle pin click response.
                if (tipResponse.isUserPinStatus()) {
                    pinHandler.unPinTip(pinResponseListener);
                } else {
                    pinHandler.pinTip(pinResponseListener);
                }
            });


            viewHolder.imgMore.setOnClickListener(view -> {
                showMoreDialog(tipResponse, i, viewHolder.tipDetail);
            });


            if (tipResponse.isUserPinStatus()) {
                viewHolder.imgPin.setImageResource(R.drawable.ic_pin_save);
            } else {
                viewHolder.imgPin.setImageResource(R.drawable.ic_tag);
            }

            DataRepository.getStockPrice(context, tips.getStockName(), data -> {
                if (data.getStockPrice() != tips.getStockPrice()) {
                    Log.e("stockprice", data.getStockName() + " " + data.getStockPrice() + "");
                    tips.setStockPrice(data.getStockPrice());
                    tips.setStockChange(data.getStockChange());
                    tips.setCompanyName(data.getCompanyName());
                    setChangeValue(tips.getCreateTipPrice(), tips.getStockPrice(), viewHolder.txtChangeValue, viewHolder.txtCurrentPrice);
                    notifyItemChanged(viewHolder.getAdapterPosition());
                }

            });
            viewHolder.txtCurrentPrice.setText(StringHelper.getAmount(tips.getStockPrice(), "--"));
            viewHolder.txtEntryPoint.setText(StringHelper.getValue(Common.formatDouble(tips.getEntryPoint()), "--"));
            viewHolder.txtExitPoint.setText(StringHelper.getValue(Common.formatDouble(tips.getExitpoint()), "--"));
            viewHolder.txtStopPoint.setText(StringHelper.getValue(Common.formatDouble(tips.getStopPoint()), "--"));
            User user = tips.getAppUser();
            setUserImage(viewHolder.imgProfile, tips);
            viewHolder.txtName.setText(getName(user));
            viewHolder.txtCreateTipPrice.setText(StringHelper.getValue(Common.formatDouble(tips.getCreateTipPrice()), "--"));
            viewHolder.txtDateTime.setText(DateTimeHelperElapsed.toString(tips.getCreatedOn(), "MMM dd, hh:mm a") + "  "
                    + context.getResources().getString(R.string.TimeZone));
            setChangeValue(tips.getCreateTipPrice(), tips.getStockPrice(), viewHolder.txtChangeValue, viewHolder.txtCurrentPrice);

            viewHolder.imgProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileTabbedActivity.starProfileTabbedtActivityForResult(context, false, tips.getAppUser().getUserName(), tips.getAppUser().getId(), 104, Constants.HOME);

                }
            });
            viewHolder.txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileTabbedActivity.starProfileTabbedtActivityForResult(context, false, tips.getAppUser().getUserName(), tips.getAppUser().getId(), 104, Constants.HOME);

                }
            });


            Log.d("stockprice===", tips.getStockPrice() + "===");

            Common.setEntryExitColor(context, tips.getEntryPoint(), tips.getExitpoint(), tips.getStopPoint(), tips.getStockPrice(), tips.getCreateTipPrice(), tips.getStockSuggestion(),
                    viewHolder.txtEntryPoint, viewHolder.txtExitPoint, viewHolder.txtStopPoint);

            String stockSuggestion;
            if (tips.getEntryPoint() < tips.getExitpoint()) {
                stockSuggestion = "Buy";
                viewHolder.txtBuySell.setBackgroundResource(R.drawable.text_backgroud_green);
                viewHolder.txtBuySell.setTextColor(Color.parseColor("#1e841c"));
            } else if (tips.getEntryPoint() > tips.getExitpoint()) {
                stockSuggestion = "Sell";
                viewHolder.txtBuySell.setBackgroundResource(R.drawable.text_backgroud_red);
                viewHolder.txtBuySell.setTextColor(Color.parseColor("#a94018"));
            } else {
                stockSuggestion = "Avoid";
                viewHolder.txtBuySell.setBackgroundResource(R.drawable.text_backgroud_black);
                viewHolder.txtBuySell.setTextColor(Color.parseColor("#707890"));
            }
            if (stockSuggestion.length() >= 4)
                viewHolder.txtBuySell.setEms(4);
            else if (stockSuggestion.length() == 3)
                viewHolder.txtBuySell.setEms(3);
            else
                viewHolder.txtBuySell.setEms(2);
            viewHolder.txtBuySell.setText(stockSuggestion);

            if (!StringHelper.isEmpty(tips.getImageDetails())) {
                // downloadImage(viewHolder.imgMedia, tips.getImageDetails(),context);
                Common.downloadImage(viewHolder.imgMedia, tips.getImageDetails(), context);
            } else {

                viewHolder.imgMedia.setVisibility(View.GONE);
            }
            viewHolder.imgMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TipDetailActivity.startActivityResult(context, tipResponse,false);
                    listener.onTipDetailCallEvent(context, tipResponse, false, i, false);
                }
            });
        }

        else if (tipsViewHolder instanceof AMDTipsViewHolderVertical) {

            List<SectorNews> newsList = tipsSection.getSectorNewsList();
            SectorNews sectorNews = Converter.calculateSentimentValue(newsList);
            double tenDayVal = Common.getDounbleValuefromStringforPredectionValue(sectorNews.getAvg10days());
            double fiftyDayVal = Common.getDounbleValuefromStringforPredectionValue(sectorNews.getAvg50days());
            double twoHundredDayVal = Common.getDounbleValuefromStringforPredectionValue(sectorNews.getAvg200days());

            AMDTipsViewHolderVertical viewHolder = (AMDTipsViewHolderVertical) tipsViewHolder;
            viewHolder.txtStockName.setText(sectorNews.getStockName());
            viewHolder.txtStockCompany.setText(sectorNews.getCompanyName());

            //---------set stock nature like natural, positive , negative.. start
            String sectorNewsStatus = "";
            if (sectorNews.getStockSentiment() == null || sectorNews.getStockSentiment().isEmpty()) {
                if (sectorNews.getNewsSectorSentiment() == null || sectorNews.getNewsSectorSentiment().isEmpty()) {
                    sectorNewsStatus = "Neutral";
                } else {
                    sectorNewsStatus = sectorNews.getNewsSectorSentiment();
                }
            } else {
                sectorNewsStatus = sectorNews.getStockSentiment();
            }

            if (sectorNewsStatus.equalsIgnoreCase("Negative") || sectorNewsStatus.equalsIgnoreCase("Very Negative")) {
                viewHolder.txtStatus.setTextColor(context.getResources().getColor(R.color.text_color_red));
            } else if (sectorNewsStatus.equalsIgnoreCase("Positive") || sectorNewsStatus.equalsIgnoreCase("Very Positive")) {
                viewHolder.txtStatus.setTextColor(context.getResources().getColor(R.color.text_color_green));
            } else {
                viewHolder.txtStatus.setTextColor(context.getResources().getColor(R.color.text_color_black));
            }
            viewHolder.txtStatus.setText(StringHelper.get(sectorNewsStatus, ""));
            //---------set stock nature like natural, positive , negative.. end


            //sector_name
            if (sectorNews.getSectorName().equalsIgnoreCase("market news")) {
                viewHolder.txtType.setText(sectorNews.getSectorName());
            } else {
                viewHolder.txtType.setText(sectorNews.getSectorName() + " Sector News");
            }
            viewHolder.txtDay.setText(DateTimeHelperElapsed.formatDateForAI(sectorNews.getSmaGenerationDate()) + "  " + context.getResources().getString(R.string.TimeZone));


            //-------set Buy Sell or Avoid status of tip---------start
            String suggestion = Common.calculatedSuggestion(sectorNews.getStockPrice(), sectorNews);
            if (suggestion.equalsIgnoreCase("Avoid")) {
                viewHolder.txtStatusBuySellAvoid.setTextColor(Color.parseColor("#707890"));
                viewHolder.txtStatusBuySellAvoid.setBackgroundResource(R.drawable.text_backgroud_black);
            } else if (suggestion.equalsIgnoreCase("Sell")) {
                viewHolder.txtStatusBuySellAvoid.setTextColor(Color.parseColor("#a94018"));
                viewHolder.txtStatusBuySellAvoid.setBackgroundResource(R.drawable.text_backgroud_red);
            } else if (suggestion.equalsIgnoreCase("Buy")) {
                viewHolder.txtStatusBuySellAvoid.setBackgroundResource(R.drawable.text_backgroud_green);
                viewHolder.txtStatusBuySellAvoid.setTextColor(Color.parseColor("#1e841c"));

            }
            if (suggestion.length() >= 4)
                viewHolder.txtStatusBuySellAvoid.setEms(4);
            else if (suggestion.length() == 3)
                viewHolder.txtStatusBuySellAvoid.setEms(3);
            else
                viewHolder.txtStatusBuySellAvoid.setEms(2);

            viewHolder.txtStatusBuySellAvoid.setText(suggestion);
            if (StringHelper.isEmpty(suggestion)) {
                viewHolder.txtStatusBuySellAvoid.setVisibility(View.GONE);
            } else {
                viewHolder.txtStatusBuySellAvoid.setVisibility(View.VISIBLE);
            }
            //-------set Buy Sell or Avoid status of tip---------end

            //-------set color of 10 day, 50 day, 200 day values---------start
            if (sectorNews.getStockPrice() >= tenDayVal) {
                viewHolder.txtTenDaysValue.setText(">");
                viewHolder.txtTenDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
            } else {
                viewHolder.txtTenDaysValue.setText("<");
                viewHolder.txtTenDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
            }
            if (sectorNews.getStockPrice() >= fiftyDayVal) {
                viewHolder.txtFiftyDaysValue.setText(">");
                viewHolder.txtFiftyDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
            } else {
                viewHolder.txtFiftyDaysValue.setText("<");
                viewHolder.txtFiftyDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
            }
            if (sectorNews.getStockPrice() >= twoHundredDayVal) {
                viewHolder.txtTwoHundredDaysValue.setText(">");
                viewHolder.txtTwoHundredDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
            } else {
                viewHolder.txtTwoHundredDaysValue.setText("<");
                viewHolder.txtTwoHundredDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
            }
            //-------set color of 10 day, 50 day, 200 day values---------start

            //set value to label
            if (Common.checkStringIsDouble(sectorNews.getAvg10days())) {
                viewHolder.txtTenDaysValue.append("$" + Common.formatDouble(Double.parseDouble(sectorNews.getAvg10days())));
            } else {
                viewHolder.txtTenDaysValue.setText(context.getResources().getString(R.string.dashDash));
                viewHolder.txtTenDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_dark_grey));
            }
            if (Common.checkStringIsDouble(sectorNews.getAvg50days())) {
                viewHolder.txtFiftyDaysValue.append("$" + Common.formatDouble(Double.parseDouble(sectorNews.getAvg50days())));
            } else {
                viewHolder.txtFiftyDaysValue.setText(context.getResources().getString(R.string.dashDash));
                viewHolder.txtFiftyDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_dark_grey));
            }
            if (Common.checkStringIsDouble(sectorNews.getAvg200days())) {
                viewHolder.txtTwoHundredDaysValue.append("$" + Common.formatDouble(Double.parseDouble(sectorNews.getAvg200days())));
            } else {
                viewHolder.txtTwoHundredDaysValue.setText(context.getResources().getString(R.string.dashDash));
                viewHolder.txtTwoHundredDaysValue.setTextColor(context.getResources().getColor(R.color.text_color_dark_grey));
            }

            viewHolder.sectorDescription.setText(sectorNews.getSectorNews() + " News sources");
            DataRepository.getStockPrice(context, sectorNews.getStockName(), new PriceCallback<StockPrice>() {
                @Override
                public void onPriceUpdate(StockPrice data) {
                    if (data.getStockPrice() != sectorNews.getStockPrice()) {
                        Log.e("stockprice", data.getStockName() + " " + data.getStockPrice() + "");
                        sectorNews.setStockPrice(data.getStockPrice());
                        sectorNews.setStockChange(data.getStockChange());
                        sectorNews.setCompanyName(data.getCompanyName());
                        notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                }
            });
            viewHolder.txtCurrentValue.setText(StringHelper.getAmount(sectorNews.getStockPrice(), "--"));
            viewHolder.txtNews1.setText(sectorNews.getNewsTitle());
            viewHolder.txtHeading1.setText(sectorNews.getNewsSourceName());
            String change = Common.formatDouble(Math.abs(sectorNews.getStockChange()));
            viewHolder.txtChange.setText(change);
            if (sectorNews.getStockChange() < 0.0) {
                viewHolder.txtChange.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_downward, 0);
                viewHolder.txtChange.setTextColor(ContextCompat.getColor(context, R.color.text_color_red));
            } else {
                viewHolder.txtChange.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_upward, 0);
                viewHolder.txtChange.setTextColor(ContextCompat.getColor(context, R.color.text_color_green));
            }
            if (newsList.size() > 1) {
                viewHolder.txtNews2.setText(newsList.get(1).getNewsTitle());
                viewHolder.txtHeding2.setText(newsList.get(1).getNewsSourceName());
                viewHolder.txtHeding2.setVisibility(View.VISIBLE);
                viewHolder.txtNews2.setVisibility(View.VISIBLE);
            } else {
                viewHolder.txtHeding2.setVisibility(View.GONE);
                viewHolder.txtNews2.setVisibility(View.GONE);
            }
            if (newsList.size() > 2) {
                viewHolder.txtNews3.setText(newsList.get(2).getNewsTitle());
                viewHolder.txtHeding3.setText(newsList.get(2).getNewsSourceName());
                viewHolder.txtHeding3.setVisibility(View.VISIBLE);
                viewHolder.txtNews3.setVisibility(View.VISIBLE);
            } else {
                viewHolder.txtHeding3.setVisibility(View.GONE);
                viewHolder.txtNews3.setVisibility(View.GONE);
            }
            // click to AI tip
            setAITipsDetailClick(viewHolder.rootLayout, tipsSection, suggestion, sectorNewsStatus, sectorNews.getStockPrice(), sectorNews.getStockChange());

        }

        else if (tipsViewHolder instanceof AMDTipsViewHolderHorizontal) {
            if (tipsSection.getSectorNewsList().size() == 0) return;
            SectorNews sectorNews = Converter.calculateSentimentValue(tipsSection.getSectorNewsList());
            double tenDayVal = Common.getDounbleValuefromStringforPredectionValue(sectorNews.getAvg10days());
            double fiftyDayVal = Common.getDounbleValuefromStringforPredectionValue(sectorNews.getAvg50days());
            double twoHundredDayVal = Common.getDounbleValuefromStringforPredectionValue(sectorNews.getAvg200days());
            AMDTipsViewHolderHorizontal viewHolder = (AMDTipsViewHolderHorizontal) tipsViewHolder;
            viewHolder.txtStockName.setText(sectorNews.getStockName());
            viewHolder.txtStockCompanyName.setVisibility(View.VISIBLE);
            DataRepository.getStockPrice(context, sectorNews.getStockName(), new PriceCallback<StockPrice>() {
                @Override
                public void onPriceUpdate(StockPrice data) {
                    if (data.getStockPrice() != sectorNews.getStockPrice()) {
                        Log.e("stockprice", data.getStockName() + " " + data.getStockPrice() + "");
                        sectorNews.setStockPrice(data.getStockPrice());
                        sectorNews.setStockChange(data.getStockChange());
                        sectorNews.setCompanyName(data.getCompanyName());
                        notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                }
            });
            String suggestion = Common.calculatedSuggestion(sectorNews.getStockPrice(), sectorNews);
            if (suggestion.equalsIgnoreCase("Avoid")) {
                viewHolder.txtStatusBuySellAvoid.setBackgroundResource(R.drawable.text_backgroud_black);
                viewHolder.txtStatusBuySellAvoid.setTextColor(Color.parseColor("#707890"));
            } else if (suggestion.equalsIgnoreCase("Sell")) {
                viewHolder.txtStatusBuySellAvoid.setBackgroundResource(R.drawable.text_backgroud_red);
                viewHolder.txtStatusBuySellAvoid.setTextColor(Color.parseColor("#a94018"));

            } else if (suggestion.equalsIgnoreCase("Buy")) {
                viewHolder.txtStatusBuySellAvoid.setTextColor(Color.parseColor("#1e841c"));
                viewHolder.txtStatusBuySellAvoid.setBackgroundResource(R.drawable.text_backgroud_green);
            }
            if (suggestion.length() >= 4)
                viewHolder.txtStatusBuySellAvoid.setEms(4);
            else if (suggestion.length() == 3)
                viewHolder.txtStatusBuySellAvoid.setEms(3);
            else
                viewHolder.txtStatusBuySellAvoid.setEms(2);
            viewHolder.txtStatusBuySellAvoid.setText(suggestion);
            if (StringHelper.isEmpty(suggestion)) {
                viewHolder.txtStatusBuySellAvoid.setVisibility(View.GONE);
            } else {
                viewHolder.txtStatusBuySellAvoid.setVisibility(View.VISIBLE);
            }


            //stock sentiment and sector sentiment agar ye nahi h to kuch ni dikha  te
            //---------set stock nature like natural, positive , negative.. start
            String sectorNewsStatus = "";

                      sectorNewsStatus = sectorNews.getNewsSectorSentiment();
            if (sectorNews.getStockSentiment() != null && !sectorNews.getStockSentiment().isEmpty()) {
                sectorNewsStatus = sectorNews.getStockSentiment();
            }
            if (sectorNewsStatus == null || sectorNewsStatus.isEmpty()) {
                sectorNewsStatus = "";
            }

            if (sectorNewsStatus.equalsIgnoreCase("Negative") || sectorNewsStatus.equalsIgnoreCase("Very Negative")) {
                viewHolder.txtOverallStatus.setTextColor(context.getResources().getColor(R.color.text_color_red));
            } else if (sectorNewsStatus.equalsIgnoreCase("Positive") || sectorNewsStatus.equalsIgnoreCase("Very Positive")) {
                viewHolder.txtOverallStatus.setTextColor(context.getResources().getColor(R.color.text_color_green));
            } else {
                viewHolder.txtOverallStatus.setTextColor(context.getResources().getColor(R.color.text_color_black));
            }

            viewHolder.txtOverallStatus.setText(sectorNewsStatus);
            //---------set stock nature like natural, positive , negative.. end

            //viewHolder.txtDateTime.setText(DateTimeHelperElapsed.formatDate(sectorNews.getSmaGenerationDate())+"  " +context.getResources().getString(R.string.TimeZone));
            viewHolder.txtDateTime.setText(DateTimeHelperElapsed.formatDateForAI(sectorNews.getSmaGenerationDate()) + "  "
                    + context.getResources().getString(R.string.TimeZone));

            // click to AI tip
            setAITipsDetailClick(viewHolder.tipRootLayout, tipsSection, suggestion, sectorNewsStatus, sectorNews.getStockPrice(), sectorNews.getStockChange());
            if (sectorNews.getStockPrice() >= tenDayVal) {
                viewHolder.txtFirstValue.setText(">10D");
                viewHolder.txtFirstValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
            } else {
                viewHolder.txtFirstValue.setText("<10D");
                viewHolder.txtFirstValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
            }

            if (sectorNews.getStockPrice() >= fiftyDayVal) {
                viewHolder.txtSecondValue.setText(">50D");
                viewHolder.txtSecondValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
            } else {
                viewHolder.txtSecondValue.setText("<50D");
                viewHolder.txtSecondValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
            }

            if (sectorNews.getStockPrice() >= twoHundredDayVal) {
                viewHolder.txtThirdValue.setText(">200D");
                viewHolder.txtThirdValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
            } else {
                viewHolder.txtThirdValue.setText("<200D");
                viewHolder.txtThirdValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
            }


            // set color to grey if values are --.
            if (!Common.checkStringIsDouble(sectorNews.getAvg10days())) {
                viewHolder.txtFirstValue.setTextColor(context.getResources().getColor(R.color.text_color_dark_grey));
            }
            if (!Common.checkStringIsDouble(sectorNews.getAvg50days())) {
                viewHolder.txtSecondValue.setTextColor(context.getResources().getColor(R.color.text_color_dark_grey));
            }
            if (!Common.checkStringIsDouble(sectorNews.getAvg200days())) {
                viewHolder.txtThirdValue.setTextColor(context.getResources().getColor(R.color.text_color_dark_grey));
            }

        }

        else if (tipsViewHolder instanceof AddTipViewModel) {
            AddTipViewModel addTipViewHolder = (AddTipViewModel) tipsViewHolder;
            addTipViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchStocksActivity.startActivity(context, new ArrayList<>(), false, SearchStocksActivity.ADD_TIPS, selectedScreen);
                }
            });
        }

        else if (tipsViewHolder instanceof HeaderHorizontalViewHolder) {
            HeaderHorizontalViewHolder headerHorizontalViewHolder = (HeaderHorizontalViewHolder) tipsViewHolder;
            headerHorizontalViewHolder.layoutMakeATip.setOnClickListener(v -> {
                SearchStocksActivity.startActivity(context, new ArrayList<>(), false, SearchStocksActivity.ADD_TIPS, selectedScreen);
            });

        }

        else if (tipsViewHolder instanceof HeaderVerticalViewHolder) {
            HeaderVerticalViewHolder headerVerticalViewHolder = (HeaderVerticalViewHolder) tipsViewHolder;
            headerVerticalViewHolder.layoutMakeATip.setOnClickListener(v -> {
                SearchStocksActivity.startActivity(context, new ArrayList<>(), false, SearchStocksActivity.ADD_TIPS, selectedScreen);
            });
        }

        SCROLLED_POSITION = i;
    }


    public String getName(User user) {
        if (user.getfName() == null || user.getfName().isEmpty()) {
            return user.getUserName();
        } else {
            return user.getfName() + " " + user.getlName();
        }
    }

    public void setTipsDetailClick(LinearLayout tipRootLayout, final TipResponse tips, int position) {
        tipRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TipDetailActivity.startActivityResult(context, tips,false);
                listener.onTipDetailCallEvent(context, tips, false, position, false);
            }
        });
    }


    public void setFlagIconClick(AppCompatImageButton flagIcon, Tips tips) {
        flagIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show dialog sheet then set flag.
                FlagReasonOptionUtility frUtility = new FlagReasonOptionUtility.Builder((AppCompatActivity) context).build();
                frUtility.showFlagOptionList();
                frUtility.setFlagOptionSelectListener(new FlagOptionSelectListener() {
                    @Override
                    public void startActivityForResult(int selectedOption, int requestCode) {
                        //  Toast.makeText(context,"selectedOption:"+selectedOption,Toast.LENGTH_LONG).show();

                        if (selectedOption == 0) return; //when user click cancel.


                        submitFlagForTip(tips.getId(), TradWyseSession.getSharedInstance(context).getUserId(), true,
                                Common.getFlagOptionById(selectedOption, context), context);

                    }
                });
            }
        });
    }

    public void submitFlagForTip(String tipId, String userId, boolean isFlag, String flagReason, Context context) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.style_progress_dialog);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<FlagResponse> call = apiInterface.addFlagForTip(tipId, userId, isFlag, flagReason);
        call.enqueue(new Callback<FlagResponse>() {
            @Override
            public void onResponse(Call<FlagResponse> call, Response<FlagResponse> response) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(String.valueOf(true))) {
                        Common.showMessage(context, context.getResources().getString(R.string.flag_success), "Success");
                    } else {
                        Common.showMessage(context, context.getResources().getString(R.string.flag_already_submitted), "Message");
                    }
                } else {
                    Toast.makeText(context, "Something went wrong,please try again later..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FlagResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, "Failed,Something went wrong,please try again later..", Toast.LENGTH_SHORT).show();
                Log.d(Constants.ON_FAILURE_TAG, "TipsAdapter submitFlagForTip: onFailure");

            }

        });
    }

    public void setAITipsDetailClick(RelativeLayout tipRootLayout, final TipsSection tips, String buySellAvoidStatus, String sectorNewsStatus, double stockPrice, double change) {
        tipRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AITradeTipDetailActivity.startActivity(context, tips, buySellAvoidStatus, sectorNewsStatus, stockPrice, change);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {

            if ((buyStockArrayList != null || sellStockArrayList != null || tipCrossoverResponseAfterMacD.size() > 0) && position == 0) {
                return 11;
            } else if (tipsList.get(position).getTipsType() == MENTOR_TIP) {
                return 1;
            } else if (tipsList.get(position).getTipsType() == AI_TRADE_TIP) {
                return 3;
            } else {
                return 6;
            }
        } else {
            if ((buyStockArrayList != null || sellStockArrayList != null || tipCrossoverResponseAfterMacD.size() > 0) && position == 0) {
                return 12;
            } else if (tipsList.get(position).getTipsType() == MENTOR_TIP) {
                return 2;
            } else if (tipsList.get(position).getTipsType() == AI_TRADE_TIP) {
                return 4;
            } else {
                return 5;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
            return tipsList.size();
        } else
            return tipsList.size();
    }

    public int addMoreTips(TipsSection tipsSection) {

        tipsList.add(tipsSection);
        sortAllTips();
        return tipsList.size() - 1;
    }



    public void sortAllTips() {
        ArrayList<TipsSection> aiTradeTip = sortAITip();
        TipsSection addTip = null;

        if (aiTradeTip != null && aiTradeTip.size() > 0 && isMentor) {
            addTip = aiTradeTip.remove(0);
        }
        ArrayList<TipsSection> mentorTip = sortTipData();
        aiTradeTip.addAll(mentorTip);


        for (TipsSection tip : aiTradeTip) {
            if (tip.getSectorNewsList() != null && tip.getSectorNewsList().size() > 0) {
                tip.setTipTime(DateTimeHelperElapsed.formatDateLong(aiTradeTip.get(0).getSectorNewsList().get(0).getSmaGenerationDate()));
            }
        }

        Collections.sort(aiTradeTip, (o1, o2) -> {
            if (o1.getTipTime() < o2.getTipTime()) {
                return -1;
            } else {
                if (o1.getTipTime() > o2.getTipTime()) {
                    return 1;
                } else {
                    return 0;
                }
            }

        });
        Collections.reverse(aiTradeTip);
        if (isMentor) aiTradeTip.add(0, addTip);
        tipsList = aiTradeTip;
        notifyDataSetChanged();
    }

    // this method is to sort data right now we are not calling it.
    private ArrayList<TipsSection> sortTipData() {

        ArrayList<TipsSection> mentorTips = getTipsFromAdapter(MENTOR_TIP);
        for (TipsSection tip : mentorTips) {
            tip.setTipTime(tip.getTips().getTip().getCreatedOn());
        }
        Collections.sort(mentorTips, new Comparator<TipsSection>() {
            @Override
            public int compare(TipsSection o1, TipsSection o2) {
                if (o1.getTipTime() < o2.getTipTime()) {
                    return -1;
                } else {
                    if (o1.getTipTime() > o2.getTipTime()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }

            }
        });
        Collections.reverse(mentorTips);
        return mentorTips;
    }

    public ArrayList<TipsSection> sortAITip() {

        ArrayList<TipsSection> tipsSections = getTipsFromAdapter(TipsAdapter.AI_TRADE_TIP);
        if (stocksAdapter != null) {
            HashMap<String, Integer> stringIntegerHashMap = stocksAdapter.getStockIndexMap();
            for (TipsSection tipsSection : tipsSections) {
                if (tipsSection.getSectorNewsList() != null && tipsSection.getSectorNewsList().size() > 0) {
                    String stockName = tipsSection.getSectorNewsList().get(0).getStockName();
                    if (stringIntegerHashMap.containsKey(stockName)) {
                        tipsSection.setStockIndex(stringIntegerHashMap.get(stockName));
                    }
                }
            }
        }
        return sortAITipData(tipsSections);
    }

    // this method is to sort data right now we are not calling it.
    private ArrayList<TipsSection> sortAITipData(ArrayList<TipsSection> tipsSectionList) {

        TipsSection firstAddSection = null;
        if (tipsSectionList.size() > 0 && isMentor)
            firstAddSection = tipsSectionList.remove(0);
        Collections.sort(tipsSectionList, new Comparator<TipsSection>() {
            @Override
            public int compare(TipsSection o1, TipsSection o2) {
                if (o1.getStockIndex() < o2.getStockIndex()) {
                    return -1;
                } else {
                    if (o1.getStockIndex() > o2.getStockIndex()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });
        Collections.reverse(tipsSectionList);
        if (firstAddSection != null)
            tipsSectionList.add(0, firstAddSection);
        return tipsSectionList;
    }

    public void hideMacdCards() {
        macdCardsShouldShow = false;
        if (layoutBuyRootLayout != null)
            layoutBuyRootLayout.setVisibility(View.GONE);
    }

    public static class TipsViewHolderHorizontal extends RecyclerView.ViewHolder {

        private LinearLayout tipRootLayout;
        private TextView txtStockName;
        private TextView txtBuySell;
        private TextView txtExitPoint;
        private TextView txtEntryPoint;
        private TextView txtStopPoint;
        private RelativeLayout profileLayout;
        private SimpleDraweeView imgProfile;
        private TextView txtName;
        private TextView txtTimeAgo;
        private AppCompatImageButton flagIcon;
        private TextView labelExit, labelEntry, txtCreateTipPrice;

        public TipsViewHolderHorizontal(View view) {
            super(view);
            tipRootLayout = view.findViewById(R.id.tipRootLayout);
            txtStockName = view.findViewById(R.id.txtStockName);
            txtBuySell = view.findViewById(R.id.txtBuySell);
            txtExitPoint = view.findViewById(R.id.txtExitPoint);
            txtCreateTipPrice = view.findViewById(R.id.txtCreateTipPrice);
            txtEntryPoint = view.findViewById(R.id.txtEntryPoint);
            profileLayout = view.findViewById(R.id.profileLayout);
            imgProfile = view.findViewById(R.id.imgProfile);
            txtName = view.findViewById(R.id.txtName);
            txtTimeAgo = view.findViewById(R.id.txtTimeAgo);
            labelEntry = view.findViewById(R.id.labelEntry);
            labelExit = view.findViewById(R.id.labelExit);
            txtStopPoint = view.findViewById(R.id.txtStopPoint);
            flagIcon = view.findViewById(R.id.flagIcon);

        }
    }

    public static class AMDTipsViewHolderHorizontal extends RecyclerView.ViewHolder {
        private RelativeLayout tipRootLayout;
        private TextView txtStatusBuySellAvoid;
        private TextView txtDateTime;
        private TextView txtStockName;
        private TextView txtOverallStatus;
        private TextView txtStockCompanyName;
        private TextView txtFirstValue;
        private TextView txtSecondValue;
        private TextView txtThirdValue;
        private RelativeLayout bottomLayout;
        private LinearLayout profileLayout;
        private ImageView imgProfile;
        private LinearLayout layout;
        private TextView txtName;
        private TextView txtTimeAgo;
        private LinearLayout dateTimeLayout;
        private RelativeLayout rootLayoutRoundedBorder;
        private LinearLayout innerLayoutFirst;
        private LinearLayout secondRowLayout;
        private TextView txtNews;
        private RelativeLayout thirdRow;


        public AMDTipsViewHolderHorizontal(View view) {
            super(view);
            tipRootLayout = view.findViewById(R.id.rootLayout);
            //  dateTimeLayout = view.findViewById(R.id.dateTimeLayout);
            txtDateTime = view.findViewById(R.id.txtDateTime);
            rootLayoutRoundedBorder = view.findViewById(R.id.rootLayoutRoundedBorder);
            innerLayoutFirst = view.findViewById(R.id.innerLayoutFirst);
            txtStockName = view.findViewById(R.id.txtStockName);
            secondRowLayout = view.findViewById(R.id.secondRowLayout);
            txtNews = view.findViewById(R.id.txtNews);
            txtOverallStatus = view.findViewById(R.id.txtOverallStatus);
            thirdRow = view.findViewById(R.id.thirdRow);
            txtStockCompanyName = view.findViewById(R.id.txtStockCompanyName);
            txtFirstValue = view.findViewById(R.id.txtFirstValue);
            txtSecondValue = view.findViewById(R.id.txtSecondValue);
            txtThirdValue = view.findViewById(R.id.txtThirdValue);
            bottomLayout = view.findViewById(R.id.bottomLayout);
            profileLayout = view.findViewById(R.id.profileLayout);
            imgProfile = view.findViewById(R.id.imgProfile);
            layout = view.findViewById(R.id.layout);
            txtName = view.findViewById(R.id.txtName);
            txtTimeAgo = view.findViewById(R.id.txtTimeAgo);
            txtStatusBuySellAvoid = view.findViewById(R.id.txtStatusBuySellAvoid);
        }
    }

    public static class AMDTipsViewHolderVertical extends RecyclerView.ViewHolder {
        private RelativeLayout rootLayout;
        private TextView sectorNews;
        private TextView sectorDescription;
        //private TextView txtDay;
        private RelativeLayout rootLayoutRoundedBorder;
        private LinearLayout firstLayout;
        private LinearLayout firstLayout11;
        private RelativeLayout firstPartLayout;
        private LinearLayout innerLayoutFirst;
        private TextView txtStockName;
        private TextView txtStockCompany;
        private TextView txtType;
        private LinearLayout innerLayoutSecond;
        private TextView txtCurrentValue;
        private TextView txtChange;
        private RelativeLayout secondBelowLayout;
        private TextView txtStatus;
        private TextView txtHeading1;
        private TextView txtTime1;
        private TextView txtNews1;
        private TextView txtHeding2;
        private TextView txtTime2;
        private TextView txtNews2;
        private TextView txtHeding3;
        private TextView txtTime3;
        private TextView txtNews3;
        private RelativeLayout secondPartLayout;
        private TextView txtPerformingstatus;
        private TextView txtTenDays;
        private TextView txtTenDaysValue;
        private TextView txtFiftyDays;
        private TextView txtFiftyDaysValue;
        private TextView txtTwoHundredDays;
        private TextView txtTwoHundredDaysValue;
        private TextView txtDay;

        private RelativeLayout bottomLayout;
        private RelativeLayout profileLayout;
        private ImageView imgProfile;
        private TextView txtName;
        private TextView txtTimeAgo;
        private TextView txtStatusBuySellAvoid;

        public AMDTipsViewHolderVertical(View view) {
            super(view);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            rootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
            sectorNews = (TextView) view.findViewById(R.id.sectorNews);
            sectorDescription = (TextView) view.findViewById(R.id.sectorDescription);
            txtDay = (TextView) view.findViewById(R.id.txtDay);
            rootLayoutRoundedBorder = (RelativeLayout) view.findViewById(R.id.rootLayoutRoundedBorder);
            firstLayout = (LinearLayout) view.findViewById(R.id.firstLayout);
            firstLayout11 = (LinearLayout) view.findViewById(R.id.firstLayout11);
            firstPartLayout = (RelativeLayout) view.findViewById(R.id.firstPartLayout);
            innerLayoutFirst = (LinearLayout) view.findViewById(R.id.innerLayoutFirst);
            txtStockName = (TextView) view.findViewById(R.id.txtStockName);
            txtStockCompany = (TextView) view.findViewById(R.id.txtStockCompany);
            txtType = (TextView) view.findViewById(R.id.txtType);
            innerLayoutSecond = (LinearLayout) view.findViewById(R.id.innerLayoutSecond);
            txtCurrentValue = (TextView) view.findViewById(R.id.txtCurrentValue);
            txtChange = (TextView) view.findViewById(R.id.txtChange);
            secondBelowLayout = (RelativeLayout) view.findViewById(R.id.secondBelowLayout);
            txtStatus = (TextView) view.findViewById(R.id.txtStatus);
            txtHeading1 = (TextView) view.findViewById(R.id.txtHeading1);
            txtTime1 = (TextView) view.findViewById(R.id.txtTime1);
            txtNews1 = (TextView) view.findViewById(R.id.txtNews1);
            txtHeding2 = (TextView) view.findViewById(R.id.txtHeding2);
            txtTime2 = (TextView) view.findViewById(R.id.txtTime2);
            txtNews2 = (TextView) view.findViewById(R.id.txtNews2);
            txtHeding3 = (TextView) view.findViewById(R.id.txtHeding3);
            txtTime3 = (TextView) view.findViewById(R.id.txtTime3);
            txtNews3 = (TextView) view.findViewById(R.id.txtNews3);
            secondPartLayout = (RelativeLayout) view.findViewById(R.id.secondPartLayout);
            //   txtPerformingstatus = (TextView) view.findViewById(R.id.txtPerformingstatus);
            //txtTenDays = (TextView) view.findViewById(R.id.txtTenDays);
            txtTenDaysValue = (TextView) view.findViewById(R.id.txtTenDaysValue);
            txtFiftyDays = (TextView) view.findViewById(R.id.txtFiftyDays);
            txtFiftyDaysValue = (TextView) view.findViewById(R.id.txtFiftyDaysValue);
            txtTwoHundredDays = (TextView) view.findViewById(R.id.txtTwoHundredDays);
            txtTwoHundredDaysValue = (TextView) view.findViewById(R.id.txtTwoHundredDaysValue);
            bottomLayout = (RelativeLayout) view.findViewById(R.id.bottomLayout);
            profileLayout = (RelativeLayout) view.findViewById(R.id.profileLayout);
            imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtTimeAgo = (TextView) view.findViewById(R.id.txtTimeAgo);
            txtStatusBuySellAvoid = (TextView) view.findViewById(R.id.txtStatusBuySellAvoid);
        }
    }

    public static class TipsViewHolderVertical extends RecyclerView.ViewHolder {

        public TextView txtDay;
        public TextView txtDateTime;
        public LinearLayout tipRootLayout;
        public TextView txtStockName;
        public TextView txtStockCompany;
        // public TextView txtStockPrice;
        public TextView txtComment;
        public TextView txtBuySell;
        public TextView labelExit;
        public TextView txtExitPoint;
        public TextView labelEntry;
        public TextView txtEntryPoint;
        public TextView txtStopPoint;
        public RelativeLayout profileLayout;
        public TextView txtName;
        public TextView txtTimeAgo, txtCreateTipPrice, txtCurrentPrice, txtChangeValue, txtNoOfComments, txtPin, txtLikeCount;
        public SimpleDraweeView imgProfile;
        private AppCompatImageButton flagIcon;
        private SimpleDraweeView imgMedia;
        private LinearLayout layoutComment;
        private ImageView imgMore, imgComment, imgPin, imgLike;
        private RelativeLayout tipDetail;

        public TipsViewHolderVertical(@NonNull View view) {
            super(view);
            txtNoOfComments = view.findViewById(R.id.txtNoOfComments);
            txtPin = view.findViewById(R.id.txtPin);
            txtLikeCount = view.findViewById(R.id.txtLikeCount);
            tipDetail = view.findViewById(R.id.tipDetail);

            imgMore = view.findViewById(R.id.imgMore);
            imgComment = view.findViewById(R.id.imgComment);
            imgPin = view.findViewById(R.id.imgPin);
            imgLike = view.findViewById(R.id.imgLike);

            txtDay = view.findViewById(R.id.txtDay);
            txtDateTime = view.findViewById(R.id.txtDateTime);
            tipRootLayout = view.findViewById(R.id.tipRootLayout);
            txtStockName = view.findViewById(R.id.txtStockName);
            txtCreateTipPrice = view.findViewById(R.id.txtCreateTipPrice);
            txtStockCompany = view.findViewById(R.id.txtStockCompany);
            //  txtStockPrice = view.findViewById(R.id.txtStockPrice);
            txtComment = view.findViewById(R.id.txtComment);
            txtBuySell = view.findViewById(R.id.txtBuySell);
            labelExit = view.findViewById(R.id.labelExit);
            txtExitPoint = view.findViewById(R.id.txtExitPoint);
            txtStopPoint = view.findViewById(R.id.txtStopPoint);
            labelEntry = view.findViewById(R.id.labelEntry);
            txtEntryPoint = view.findViewById(R.id.txtEntryPoint);
            profileLayout = view.findViewById(R.id.profileLayout);
            imgProfile = view.findViewById(R.id.imgProfile);
            txtName = view.findViewById(R.id.txtName);
            txtTimeAgo = view.findViewById(R.id.txtTimeAgo);
            txtCurrentPrice = view.findViewById(R.id.txtCurrentPrice);
            txtChangeValue = view.findViewById(R.id.txtChangeValue);
            flagIcon = view.findViewById(R.id.flagIcon);
            imgMedia = view.findViewById(R.id.imgMedia);
            layoutComment = view.findViewById(R.id.layoutComment);
        }
    }

    private class HeaderHorizontalViewHolder extends RecyclerView.ViewHolder {
        LinearLayout tipBuyRootLayoutRoot, tipSellRootLayoutRoot, layoutMakeATip;
        TextView txtBuySignals, textViewBuyStockName, txtBuyTimeAgo;
        TextView txtSellSignals, textViewSellStockName, txtSellTimeAgo;
        RecyclerView recyclerViewHorizontalTips;

        public HeaderHorizontalViewHolder(View view) {
            super(view);
            layoutBuyRootLayout = view.findViewById(R.id.layoutRootLayout);
            layoutMakeATip = view.findViewById(R.id.layoutMakeATip);
            tipBuyRootLayoutRoot = view.findViewById(R.id.tipBuyRootLayoutRoot);
            tipSellRootLayoutRoot = view.findViewById(R.id.tipSellRootLayoutRoot);
            txtBuySignals = (TextView) view.findViewById(R.id.txtBuySignals);
            textViewBuyStockName = (TextView) view.findViewById(R.id.textViewBuyStockName);
            txtSellSignals = (TextView) view.findViewById(R.id.txtSellSignals);
            textViewSellStockName = (TextView) view.findViewById(R.id.textViewSellStockName);
            txtBuyTimeAgo = (TextView) view.findViewById(R.id.txtBuyTimeAgo);
            txtSellTimeAgo = (TextView) view.findViewById(R.id.txtSellTimeAgo);
            recyclerViewHorizontalTips = view.findViewById(R.id.recyclerViewHorizontalTips);

            if (isMentor) {
                // check network is available or not.
                if (!Common.isNetworkAvailable(context)) {
                    return;
                } else if (macdCardsShouldShow) {
                    tipBuyRootLayoutRoot.setVisibility(View.VISIBLE);
                    tipSellRootLayoutRoot.setVisibility(View.VISIBLE);
                    layoutBuyRootLayout.setVisibility(View.VISIBLE);
                } else {
                    layoutBuyRootLayout.setVisibility(View.GONE);
                }


                if (isMentor) {
                    layoutMakeATip.setVisibility(View.VISIBLE);
                } else {
                    layoutMakeATip.setVisibility(View.GONE);
                }

                if (tipCrossoverResponseAfterMacD.size() > 0) {

                    recyclerViewHorizontalTips.setVisibility(View.VISIBLE);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    tipsAfterMacdAdapter = new TipsAfterMacDAdapter(tipCrossoverResponseAfterMacD, stocksAdapter, context, linearLayoutManager, listener, new ArrayList<>(), filter, selectedScreen);
                    recyclerViewHorizontalTips.setLayoutManager(linearLayoutManager);
                    recyclerViewHorizontalTips.setAdapter(tipsAfterMacdAdapter);
                } else {
                    recyclerViewHorizontalTips.setVisibility(View.GONE);
                }

                tipBuyRootLayoutRoot.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, MacdAlertDetailsActivity.class)
                            .putExtra("from", "Buy")
                            .putExtra("dataList", buyStockArrayList)
                            .putExtra("gifFile", buyGif));
                });

                tipSellRootLayoutRoot.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, MacdAlertDetailsActivity.class)
                            .putExtra("from", "Sell")
                            .putExtra("dataList", sellStockArrayList)
                            .putExtra("gifFile", sellGif));
                });

                if (sellStockArrayList.size() > 0)
                    txtSellTimeAgo.setText(DateTimeHelperElapsed.toString(sellStockArrayList.get(0).getCreatedOn(), "MMM dd, hh:mm a") + "  "
                            + context.getResources().getString(R.string.TimeZone));
                //txtSellTimeAgo.setText(Common.removeLastChars(sellStockArrayList.get(0).getCurrentdate(), 5));

                if (buyStockArrayList.size() > 0)
                    txtBuyTimeAgo.setText(DateTimeHelperElapsed.toString(buyStockArrayList.get(0).getCreatedOn(), "MMM dd, hh:mm a") + "  "
                            + context.getResources().getString(R.string.TimeZone));

                // For Buy Card
                if (buyStockArrayList.size() <= 1) {
                    txtBuySignals.setText(buyStockArrayList.size() + " Signal");
                } else {
                    txtBuySignals.setText(buyStockArrayList.size() + " Signals");
                }

                // For Sell Card
                if (sellStockArrayList.size() <= 1) {
                    txtSellSignals.setText(sellStockArrayList.size() + " Signal");
                } else {
                    txtSellSignals.setText(sellStockArrayList.size() + " Signals");
                }

                if (buyStockArrayList.size() > 0) {
                    if (buyStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("") ||
                            buyStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("buy_stock") ||
                            buyStockArrayList.get(0).getStockSymbol() == null) {

                        textViewBuyStockName.setText("No Buy MACD crossovers.");
                        txtBuySignals.setText("0 Signal");

                    } else {
                        StringBuilder buffer = new StringBuilder();
                        for (int i = 0; i < buyStockArrayList.size(); i++) {
                            if (buyStockArrayList.get(i).getStockSymbol().equalsIgnoreCase("")
                                    || buyStockArrayList.get(i).getStockSymbol() == null
                                    || buyStockArrayList.get(i).getStockSymbol().equalsIgnoreCase("null")) {
                            } else {
                                if (i > 0) {
                                    buffer.append(", ");
                                }
                                buffer.append(buyStockArrayList.get(i).getStockSymbol());
                            }
                        }
                        textViewBuyStockName.setText(buffer);
                    }

                } else {
                    textViewBuyStockName.setText("No Buy MACD crossover.");
                    txtBuySignals.setText("0 Signal");
                }

                if (sellStockArrayList.size() > 0) {

                    if (sellStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("") ||
                            sellStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("sell_stock") ||
                            sellStockArrayList.get(0).getStockSymbol() == null) {

                        textViewSellStockName.setText("No Sell MACD crossovers.");
                        txtSellSignals.setText("0 Signal");

                    } else {
                        StringBuilder buffer2 = new StringBuilder();
                        for (int i = 0; i < sellStockArrayList.size(); i++) {
                            if (sellStockArrayList.get(i).getStockSymbol().equalsIgnoreCase("")
                                    || sellStockArrayList.get(i).getStockSymbol() == null
                                    || sellStockArrayList.get(i).getStockSymbol().equalsIgnoreCase("null")) {
                            } else {
                                if (i > 0) {
                                    buffer2.append(", ");
                                }
                                buffer2.append(sellStockArrayList.get(i).getStockSymbol());
                            }
                        }
                        textViewSellStockName.setText(buffer2);
                    }
                } else {
                    textViewSellStockName.setText("No Sell MACD crossovers.");
                    txtSellSignals.setText("0 Signal");
                }


                // If filter is MENTOR_TIP then MACD cards will not show.
                if (filter == MENTOR_TIP) {
                    tipBuyRootLayoutRoot.setVisibility(View.GONE);
                    tipSellRootLayoutRoot.setVisibility(View.GONE);
                } else {
                    tipBuyRootLayoutRoot.setVisibility(View.VISIBLE);
                    tipSellRootLayoutRoot.setVisibility(View.VISIBLE);
                }

            } else if (tradWyseSession.isSubscribedMember() && tradWyseSession.isMACDServicePurchased()) {

                // check network is available or not.
                if (!Common.isNetworkAvailable(context)) {
                    return;
                } else if (macdCardsShouldShow) {
                    tipBuyRootLayoutRoot.setVisibility(View.VISIBLE);
                    tipSellRootLayoutRoot.setVisibility(View.VISIBLE);
                    layoutBuyRootLayout.setVisibility(View.VISIBLE);
                } else {
                    layoutBuyRootLayout.setVisibility(View.GONE);
                }


                if (isMentor) {
                    layoutMakeATip.setVisibility(View.VISIBLE);
                } else {
                    layoutMakeATip.setVisibility(View.GONE);
                }

                if (tipCrossoverResponseAfterMacD.size() > 0) {

                    recyclerViewHorizontalTips.setVisibility(View.VISIBLE);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    tipsAfterMacdAdapter = new TipsAfterMacDAdapter(tipCrossoverResponseAfterMacD, stocksAdapter, context, linearLayoutManager, listener, new ArrayList<>(), filter, selectedScreen);
                    recyclerViewHorizontalTips.setLayoutManager(linearLayoutManager);
                    recyclerViewHorizontalTips.setAdapter(tipsAfterMacdAdapter);
                } else {
                    recyclerViewHorizontalTips.setVisibility(View.GONE);
                }

                tipBuyRootLayoutRoot.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, MacdAlertDetailsActivity.class)
                            .putExtra("from", "Buy")
                            .putExtra("dataList", buyStockArrayList)
                            .putExtra("gifFile", buyGif));
                });

                tipSellRootLayoutRoot.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, MacdAlertDetailsActivity.class)
                            .putExtra("from", "Sell")
                            .putExtra("dataList", sellStockArrayList)
                            .putExtra("gifFile", sellGif));
                });

                if (sellStockArrayList.size() > 0)
                    txtSellTimeAgo.setText(DateTimeHelperElapsed.toString(sellStockArrayList.get(0).getCreatedOn(), "MMM dd, hh:mm a") + "  "
                            + context.getResources().getString(R.string.TimeZone));
                //txtSellTimeAgo.setText(Common.removeLastChars(sellStockArrayList.get(0).getCurrentdate(), 5));

                if (buyStockArrayList.size() > 0)
                    txtBuyTimeAgo.setText(DateTimeHelperElapsed.toString(buyStockArrayList.get(0).getCreatedOn(), "MMM dd, hh:mm a") + "  "
                            + context.getResources().getString(R.string.TimeZone));

                // For Buy Card
                if (buyStockArrayList.size() <= 1) {
                    txtBuySignals.setText(buyStockArrayList.size() + " Signal");
                } else {
                    txtBuySignals.setText(buyStockArrayList.size() + " Signals");
                }

                // For Sell Card
                if (sellStockArrayList.size() <= 1) {
                    txtSellSignals.setText(sellStockArrayList.size() + " Signal");
                } else {
                    txtSellSignals.setText(sellStockArrayList.size() + " Signals");
                }

                if (buyStockArrayList.size() > 0) {
                    if (buyStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("") ||
                            buyStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("buy_stock") ||
                            buyStockArrayList.get(0).getStockSymbol() == null) {

                        textViewBuyStockName.setText("No Buy MACD crossovers.");
                        txtBuySignals.setText("0 Signal");

                    } else {
                        StringBuilder buffer = new StringBuilder();
                        for (int i = 0; i < buyStockArrayList.size(); i++) {
                            if (buyStockArrayList.get(i).getStockSymbol().equalsIgnoreCase("")
                                    || buyStockArrayList.get(i).getStockSymbol() == null
                                    || buyStockArrayList.get(i).getStockSymbol().equalsIgnoreCase("null")) {

                            } else {
                                if (i > 0) {
                                    buffer.append(", ");
                                }
                                buffer.append(buyStockArrayList.get(i).getStockSymbol());
                            }
                        }
                        textViewBuyStockName.setText(buffer);
                    }

                } else {
                    textViewBuyStockName.setText("No Buy MACD crossovers.");
                    txtBuySignals.setText("0 Signal");
                }

                if (sellStockArrayList.size() > 0) {

                    if (sellStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("") ||
                            sellStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("sell_stock") ||
                            sellStockArrayList.get(0).getStockSymbol() == null) {

                        textViewSellStockName.setText("No Sell MACD crossovers.");
                        txtSellSignals.setText("0 Signal");

                    } else {
                        StringBuilder buffer2 = new StringBuilder();
                        for (int i = 0; i < sellStockArrayList.size(); i++) {
                            if (sellStockArrayList.get(i).getStockSymbol().equalsIgnoreCase("")
                                    || sellStockArrayList.get(i).getStockSymbol() == null
                                    || sellStockArrayList.get(i).getStockSymbol().equalsIgnoreCase("null")) {

                            } else {
                                if (i > 0) {
                                    buffer2.append(", ");
                                }
                                buffer2.append(sellStockArrayList.get(i).getStockSymbol());
                            }
                        }
                        textViewSellStockName.setText(buffer2);
                    }

                } else {
                    textViewSellStockName.setText("No Sell MACD crossovers.");
                    txtSellSignals.setText("0 Signal");
                }


                // If filter is MENTOR_TIP then MACD cards will not show.
                if (filter == MENTOR_TIP) {
                    tipBuyRootLayoutRoot.setVisibility(View.GONE);
                    tipSellRootLayoutRoot.setVisibility(View.GONE);
                } else {
                    tipBuyRootLayoutRoot.setVisibility(View.VISIBLE);
                    tipSellRootLayoutRoot.setVisibility(View.VISIBLE);
                }


            } else {
                if (tipCrossoverResponseAfterMacD.size() > 0) {
                    recyclerViewHorizontalTips.setVisibility(View.VISIBLE);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    tipsAfterMacdAdapter = new TipsAfterMacDAdapter(tipCrossoverResponseAfterMacD, stocksAdapter, context, linearLayoutManager, listener, new ArrayList<>(), filter, selectedScreen);
                    recyclerViewHorizontalTips.setLayoutManager(linearLayoutManager);
                    recyclerViewHorizontalTips.setAdapter(tipsAfterMacdAdapter);
                } else {
                    recyclerViewHorizontalTips.setVisibility(View.GONE);
                }

                if (isMentor) {
                    layoutMakeATip.setVisibility(View.VISIBLE);
                } else {
                    layoutMakeATip.setVisibility(View.GONE);
                }
            }
        }

    }

    private class HeaderVerticalViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layoutRootLayout, layoutBuyWhatIsMACD, layoutSellWhatIsMACD, layoutMakeATip;
        LinearLayout layoutBuyHeader, layoutSellHeader, layoutBuyViewDetails, layoutSellViewDetails;
        LinearLayout layoutBuyTitles, layoutSellTitles, layoutBuyBlank, layoutSellBlank;
        RelativeLayout layoutTitlesBuy, layoutTitlesSell;
        TextView txtBuySignals, txtSellSignals, txtBuyDateTime, txtSellDateTime, txtBuyTitleDetail, txtSellTitleDetail;
        TextView textViewBuyClickHere, textViewSellClickHere;
        RecyclerView recyclerViewBuyStocks, recyclerViewSellStocks, recyclerViewVerticalTips;

        public HeaderVerticalViewHolder(View view) {
            super(view);
            layoutRootLayout = view.findViewById(R.id.layoutRootLayout);
            layoutMakeATip = view.findViewById(R.id.layoutMakeATip);
            layoutBuyWhatIsMACD = view.findViewById(R.id.layoutBuyWhatIsMACD);
            layoutSellWhatIsMACD = view.findViewById(R.id.layoutSellWhatIsMACD);
            layoutBuyHeader = view.findViewById(R.id.layoutBuyHeader);
            layoutSellHeader = view.findViewById(R.id.layoutSellHeader);
            txtBuySignals = view.findViewById(R.id.txtBuySignals);
            txtSellSignals = view.findViewById(R.id.txtSellSignals);
            recyclerViewSellStocks = view.findViewById(R.id.recyclerViewSellStocks);
            recyclerViewBuyStocks = view.findViewById(R.id.recyclerViewBuyStocks);
            layoutBuyViewDetails = view.findViewById(R.id.layoutBuyViewDetails);
            layoutSellViewDetails = view.findViewById(R.id.layoutSellViewDetails);
            txtBuyDateTime = view.findViewById(R.id.txtBuyDateTime);
            txtSellDateTime = view.findViewById(R.id.txtSellDateTime);
            txtBuyTitleDetail = view.findViewById(R.id.txtBuyTitleDetail);
            txtSellTitleDetail = view.findViewById(R.id.txtSellTitleDetail);
            layoutBuyTitles = view.findViewById(R.id.layoutBuyTitles);
            layoutSellTitles = view.findViewById(R.id.layoutSellTitles);
            layoutBuyBlank = view.findViewById(R.id.layoutBuyBlank);
            layoutSellBlank = view.findViewById(R.id.layoutSellBlank);
            layoutTitlesBuy = view.findViewById(R.id.layoutTitlesBuy);
            layoutTitlesSell = view.findViewById(R.id.layoutTitlesSell);
            textViewBuyClickHere = view.findViewById(R.id.textViewBuyClickHere);
            textViewSellClickHere = view.findViewById(R.id.textViewSellClickHere);
            recyclerViewVerticalTips = view.findViewById(R.id.recyclerViewVerticalTips);

            if (isMentor) {

                // check network is available or not.
                if (!Common.isNetworkAvailable(context)) {
                    return;
                } else if (macdCardsShouldShow) {
                    layoutBuyHeader.setVisibility(View.VISIBLE);
                    layoutSellHeader.setVisibility(View.VISIBLE);
                    layoutRootLayout.setVisibility(View.VISIBLE);
                } else {
                    layoutRootLayout.setVisibility(View.GONE);
                }

                if (isMentor) {
                    layoutMakeATip.setVisibility(View.VISIBLE);
                } else {
                    layoutMakeATip.setVisibility(View.GONE);
                }

                if (tipCrossoverResponseAfterMacD.size() > 0) {

                    recyclerViewVerticalTips.setVisibility(View.VISIBLE);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    tipsAfterMacdAdapter = new TipsAfterMacDAdapter(tipCrossoverResponseAfterMacD, stocksAdapter, context, linearLayoutManager, listener, new ArrayList<>(), filter, selectedScreen);
                    recyclerViewVerticalTips.setLayoutManager(linearLayoutManager);
                    recyclerViewVerticalTips.setAdapter(tipsAfterMacdAdapter);
                } else {
                    recyclerViewVerticalTips.setVisibility(View.GONE);
                }

                // Set adapter for Buy MACD Card
                MACDBuyAdapter macdBuyAdapter = new MACDBuyAdapter(context, buyStockArrayList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                recyclerViewBuyStocks.setLayoutManager(layoutManager);
                recyclerViewBuyStocks.setAdapter(macdBuyAdapter);
                macdBuyAdapter.SetOnListClickListener(new MACDBuyAdapter.OnListClickListener() {
                    @Override
                    public void onRowClickListener() {
                        context.startActivity(new Intent(context, MacdAlertDetailsActivity.class)
                                .putExtra("from", "Buy")
                                .putExtra("dataList", buyStockArrayList)
                                .putExtra("gifFile", buyGif));
                    }
                });

                // Set adapter for Sell MACD Card
                MACDSellAdapter macdSellAdapter = new MACDSellAdapter(context, sellStockArrayList);
                RecyclerView.LayoutManager layoutSellManager = new LinearLayoutManager(context);
                recyclerViewSellStocks.setLayoutManager(layoutSellManager);
                recyclerViewSellStocks.setAdapter(macdSellAdapter);
                macdSellAdapter.SetOnListClickListener(new MACDSellAdapter.OnListClickListener() {
                    @Override
                    public void onRowClickListener() {
                        context.startActivity(new Intent(context, MacdAlertDetailsActivity.class)
                                .putExtra("from", "Sell")
                                .putExtra("dataList", sellStockArrayList)
                                .putExtra("gifFile", sellGif));
                    }
                });


                if (buyStockArrayList.size() > 4) {
                    layoutBuyViewDetails.setVisibility(View.VISIBLE);
                } else {
                    layoutBuyViewDetails.setVisibility(View.GONE);
                }

                if (sellStockArrayList.size() > 4) {
                    layoutSellViewDetails.setVisibility(View.VISIBLE);
                } else {
                    layoutSellViewDetails.setVisibility(View.GONE);
                }

                if (sellStockArrayList.size() > 0) {
                    txtSellTitleDetail.setText("S&P500 and Nasdaq100\n" +
                            "All MACD convergence crossover signals\n" + sellStockArrayList.get(0).getCrossoverDate());
                    txtSellDateTime.setText(DateTimeHelperElapsed.toString(sellStockArrayList.get(0).getCreatedOn(), "MMM dd, hh:mm a") + "  "
                            + context.getResources().getString(R.string.TimeZone));
                    if (!sellStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("sell_stock")) {
                        layoutTitlesSell.setVisibility(View.VISIBLE);
                        layoutSellBlank.setVisibility(View.GONE);
                    } else {
                        layoutTitlesSell.setVisibility(View.INVISIBLE);
                        layoutSellBlank.setVisibility(View.VISIBLE);
                    }
                } else {
                    layoutTitlesSell.setVisibility(View.INVISIBLE);
                    //txtSellTitleDetail.setText("Tracking all S&P 500 & Nasdaq 100 MACD Events List created from the crossovers of " + ".");
                    layoutSellBlank.setVisibility(View.VISIBLE);
                }

                if (buyStockArrayList.size() > 0) {
                    txtBuyTitleDetail.setText("S&P500 and Nasdaq100\n" +
                            "All MACD convergence crossover signals\n" + buyStockArrayList.get(0).getCrossoverDate());
                    txtBuyDateTime.setText(DateTimeHelperElapsed.toString(buyStockArrayList.get(0).getCreatedOn(), "MMM dd, hh:mm a") + "  "
                            + context.getResources().getString(R.string.TimeZone));
                    if (!buyStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("buy_stock")) {
                        layoutTitlesBuy.setVisibility(View.VISIBLE);
                        layoutBuyBlank.setVisibility(View.GONE);
                    } else {
                        layoutTitlesBuy.setVisibility(View.INVISIBLE);
                        layoutBuyBlank.setVisibility(View.VISIBLE);
                    }
                } else {
                    layoutTitlesBuy.setVisibility(View.INVISIBLE);
                    //txtBuyTitleDetail.setText("Tracking all S&P 500 & Nasdaq 100 MACD Events List created from the crossovers of " + ".");
                    layoutBuyBlank.setVisibility(View.VISIBLE);
                }

                // For Buy Card
                if (buyStockArrayList.size() <= 1) {
                    txtBuySignals.setText(buyStockArrayList.size() + " Signal");
                } else {
                    txtBuySignals.setText(buyStockArrayList.size() + " Signals");
                }

                // For Sell Card
                if (sellStockArrayList.size() <= 1) {
                    txtSellSignals.setText(sellStockArrayList.size() + " Signal");
                } else {
                    txtSellSignals.setText(sellStockArrayList.size() + " Signals");
                }

                if (buyStockArrayList.size() > 0) {
                    if (buyStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("") ||
                            buyStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("buy_stock") ||
                            buyStockArrayList.get(0).getStockSymbol() == null) {
                        txtBuySignals.setText("0 MACD Signal");
                    }
                } else {
                    txtBuySignals.setText("0 MACD Signal");
                }

                if (sellStockArrayList.size() > 0) {
                    if (sellStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("") ||
                            sellStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("sell_stock") ||
                            sellStockArrayList.get(0).getStockSymbol() == null) {
                        txtSellSignals.setText("0 MACD Signal");
                    }
                } else {
                    txtSellSignals.setText("0 MACD Signal");
                }

                layoutBuyWhatIsMACD.setOnClickListener(v -> {
                    showPopup();
                    //Common.showMessageWithCenterText(context, "MACD triggers technical signals when it crosses above (to buy) or below (to sell) its signal line.", "MACD");
                });

                layoutSellWhatIsMACD.setOnClickListener(v -> {
                    showPopup();
                    //Common.showMessageWithCenterText(context, "MACD triggers technical signals when it crosses above (to buy) or below (to sell) its signal line.", "MACD");
                });

                layoutBuyHeader.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, MacdAlertDetailsActivity.class)
                            .putExtra("from", "Buy")
                            .putExtra("dataList", buyStockArrayList)
                            .putExtra("gifFile", buyGif));
                });

                layoutSellHeader.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, MacdAlertDetailsActivity.class)
                            .putExtra("from", "Sell")
                            .putExtra("dataList", sellStockArrayList)
                            .putExtra("gifFile", sellGif));
                });

                // If filter is MENTOR_TIP then MACD cards will not show.
                if (filter == MENTOR_TIP) {
                    layoutBuyHeader.setVisibility(View.GONE);
                    layoutSellHeader.setVisibility(View.GONE);
                } else {
                    layoutBuyHeader.setVisibility(View.VISIBLE);
                    layoutSellHeader.setVisibility(View.VISIBLE);
                }

            }
            else if (tradWyseSession.isSubscribedMember() && tradWyseSession.isMACDServicePurchased()) {

                // check network is available or not.
                if (!Common.isNetworkAvailable(context)) {
                    return;
                } else if (macdCardsShouldShow) {
                    layoutBuyHeader.setVisibility(View.VISIBLE);
                    layoutSellHeader.setVisibility(View.VISIBLE);
                    layoutRootLayout.setVisibility(View.VISIBLE);
                } else {
                    layoutRootLayout.setVisibility(View.GONE);
                }

                if (isMentor) {
                    layoutMakeATip.setVisibility(View.VISIBLE);
                } else {
                    layoutMakeATip.setVisibility(View.GONE);
                }

                if (tipCrossoverResponseAfterMacD.size() > 0) {

                    recyclerViewVerticalTips.setVisibility(View.VISIBLE);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    tipsAfterMacdAdapter = new TipsAfterMacDAdapter(tipCrossoverResponseAfterMacD, stocksAdapter, context, linearLayoutManager, listener, new ArrayList<>(), filter, selectedScreen);
                    recyclerViewVerticalTips.setLayoutManager(linearLayoutManager);
                    recyclerViewVerticalTips.setAdapter(tipsAfterMacdAdapter);
                } else {
                    recyclerViewVerticalTips.setVisibility(View.GONE);
                }

                // Set adapter for Buy MACD Card
                MACDBuyAdapter macdBuyAdapter = new MACDBuyAdapter(context, buyStockArrayList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                recyclerViewBuyStocks.setLayoutManager(layoutManager);
                recyclerViewBuyStocks.setAdapter(macdBuyAdapter);
                macdBuyAdapter.SetOnListClickListener(new MACDBuyAdapter.OnListClickListener() {
                    @Override
                    public void onRowClickListener() {
                        context.startActivity(new Intent(context, MacdAlertDetailsActivity.class)
                                .putExtra("from", "Buy")
                                .putExtra("dataList", buyStockArrayList)
                                .putExtra("gifFile", buyGif));
                    }
                });

                // Set adapter for Sell MACD Card
                MACDSellAdapter macdSellAdapter = new MACDSellAdapter(context, sellStockArrayList);
                RecyclerView.LayoutManager layoutSellManager = new LinearLayoutManager(context);
                recyclerViewSellStocks.setLayoutManager(layoutSellManager);
                recyclerViewSellStocks.setAdapter(macdSellAdapter);
                macdSellAdapter.SetOnListClickListener(new MACDSellAdapter.OnListClickListener() {
                    @Override
                    public void onRowClickListener() {
                        context.startActivity(new Intent(context, MacdAlertDetailsActivity.class)
                                .putExtra("from", "Sell")
                                .putExtra("dataList", sellStockArrayList)
                                .putExtra("gifFile", sellGif));
                    }
                });


                if (buyStockArrayList.size() > 4) {
                    layoutBuyViewDetails.setVisibility(View.VISIBLE);
                } else {
                    layoutBuyViewDetails.setVisibility(View.GONE);
                }

                if (sellStockArrayList.size() > 4) {
                    layoutSellViewDetails.setVisibility(View.VISIBLE);
                } else {
                    layoutSellViewDetails.setVisibility(View.GONE);
                }

                if (sellStockArrayList.size() > 0) {
                    txtSellTitleDetail.setText("S&P500 and Nasdaq100\n" +
                            "All MACD convergence crossover signals\n" + sellStockArrayList.get(0).getCrossoverDate());
                    txtSellDateTime.setText(DateTimeHelperElapsed.toString(sellStockArrayList.get(0).getCreatedOn(), "MMM dd, hh:mm a") + "  "
                            + context.getResources().getString(R.string.TimeZone));
                    if (!sellStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("sell_stock")) {
                        layoutTitlesSell.setVisibility(View.VISIBLE);
                        layoutSellBlank.setVisibility(View.GONE);
                    } else {
                        layoutTitlesSell.setVisibility(View.INVISIBLE);
                        layoutSellBlank.setVisibility(View.VISIBLE);
                    }
                } else {
                    layoutTitlesSell.setVisibility(View.INVISIBLE);
                    //txtSellTitleDetail.setText("Tracking all S&P 500 & Nasdaq 100 MACD Events List created from the crossovers of " + ".");
                    layoutSellBlank.setVisibility(View.VISIBLE);
                }

                if (buyStockArrayList.size() > 0) {
                    txtBuyTitleDetail.setText("S&P500 and Nasdaq100\n" +
                            "All MACD convergence crossover signals\n" + buyStockArrayList.get(0).getCrossoverDate());
                    txtBuyDateTime.setText(DateTimeHelperElapsed.toString(buyStockArrayList.get(0).getCreatedOn(), "MMM dd, hh:mm a") + "  "
                            + context.getResources().getString(R.string.TimeZone));
                    if (!buyStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("buy_stock")) {
                        layoutTitlesBuy.setVisibility(View.VISIBLE);
                        layoutBuyBlank.setVisibility(View.GONE);
                    } else {
                        layoutTitlesBuy.setVisibility(View.INVISIBLE);
                        layoutBuyBlank.setVisibility(View.VISIBLE);
                    }
                } else {
                    layoutTitlesBuy.setVisibility(View.INVISIBLE);
                    //txtBuyTitleDetail.setText("Tracking all S&P 500 & Nasdaq 100 MACD Events List created from the crossovers of " + ".");
                    layoutBuyBlank.setVisibility(View.VISIBLE);
                }

                // For Buy Card
                if (buyStockArrayList.size() <= 1) {
                    txtBuySignals.setText(buyStockArrayList.size() + " Signal");
                } else {
                    txtBuySignals.setText(buyStockArrayList.size() + " Signals");
                }

                // For Sell Card
                if (sellStockArrayList.size() <= 1) {
                    txtSellSignals.setText(sellStockArrayList.size() + " Signal");
                } else {
                    txtSellSignals.setText(sellStockArrayList.size() + " Signals");
                }

                if (buyStockArrayList.size() > 0) {
                    if (buyStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("") ||
                            buyStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("buy_stock") ||
                            buyStockArrayList.get(0).getStockSymbol() == null) {
                        txtBuySignals.setText("0 MACD Signal");
                    }
                } else {
                    txtBuySignals.setText("0 MACD Signal");
                }

                if (sellStockArrayList.size() > 0) {
                    if (sellStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("") ||
                            sellStockArrayList.get(0).getStockSymbol().equalsIgnoreCase("sell_stock") ||
                            sellStockArrayList.get(0).getStockSymbol() == null) {
                        txtSellSignals.setText("0 MACD Signal");
                    }
                } else {
                    txtSellSignals.setText("0 MACD Signal");
                }

                layoutBuyWhatIsMACD.setOnClickListener(v -> {
                    showPopup();
                    //Common.showMessageWithCenterText(context, "MACD triggers technical signals when it crosses above (to buy) or below (to sell) its signal line.", "MACD");
                });

                layoutSellWhatIsMACD.setOnClickListener(v -> {
                    showPopup();
                    //Common.showMessageWithCenterText(context, "MACD triggers technical signals when it crosses above (to buy) or below (to sell) its signal line.", "MACD");
                });

                layoutBuyHeader.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, MacdAlertDetailsActivity.class)
                            .putExtra("from", "Buy")
                            .putExtra("dataList", buyStockArrayList)
                            .putExtra("gifFile", buyGif));
                });

                layoutSellHeader.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, MacdAlertDetailsActivity.class)
                            .putExtra("from", "Sell")
                            .putExtra("dataList", sellStockArrayList)
                            .putExtra("gifFile", sellGif));
                });

                // If filter is MENTOR_TIP then MACD cards will not show.
                if (filter == MENTOR_TIP) {
                    layoutBuyHeader.setVisibility(View.GONE);
                    layoutSellHeader.setVisibility(View.GONE);
                } else {
                    layoutBuyHeader.setVisibility(View.VISIBLE);
                    layoutSellHeader.setVisibility(View.VISIBLE);
                }

            }
            else {
                // check network is available or not.
                if (!Common.isNetworkAvailable(context)) {
                    return;
                } else if (macdCardsShouldShow) {
                    layoutRootLayout.setVisibility(View.VISIBLE);
                } else {
                    layoutRootLayout.setVisibility(View.GONE);
                }

                if (isMentor) {
                    layoutMakeATip.setVisibility(View.VISIBLE);
                } else {
                    layoutMakeATip.setVisibility(View.GONE);
                }

                if (tipCrossoverResponseAfterMacD.size() > 0) {

                    recyclerViewVerticalTips.setVisibility(View.VISIBLE);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    tipsAfterMacdAdapter = new TipsAfterMacDAdapter(tipCrossoverResponseAfterMacD, stocksAdapter, context, linearLayoutManager, listener, new ArrayList<>(), filter, selectedScreen);
                    recyclerViewVerticalTips.setLayoutManager(linearLayoutManager);
                    recyclerViewVerticalTips.setAdapter(tipsAfterMacdAdapter);
                } else {
                    recyclerViewVerticalTips.setVisibility(View.GONE);
                }

            }
        }
    }

    private void showPopup() {
        new WhatIsMacdDialog(context) {
            @Override
            public void gotItButtonClicked() {

            }

            @Override
            public void closeButtonClicked() {

            }
        };
    }

    class AddTipViewModel extends RecyclerView.ViewHolder {
        View view;

        public AddTipViewModel(View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }

    class EmptyViewModel extends RecyclerView.ViewHolder {
        View view;

        public EmptyViewModel(View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }

    public ArrayList<TipsSection> getTipsFromAdapter() {
        if (tipsList != null) {
            return tipsList;
        } else {
            tipsList = new ArrayList<>();
            return tipsList;
        }
    }

    public ArrayList<TipsSection> getTipsFromAdapter(int tipType) {
        ArrayList<TipsSection> tipList = new ArrayList<>();
        if (tipsList != null) {
            for (int i = 0; i < tipsList.size(); i++) {
                if (tipType == AI_TRADE_TIP) {
                    if (tipsList.get(i).getTipsType() == TipsAdapter.AI_TRADE_TIP || tipsList.get(i).getTipsType() == 2) {
                        tipList.add(tipsList.get(i));
                    }
                }

                if (tipType == MENTOR_TIP) {
                    if (tipsList.get(i).getTipsType() == tipType) {
                        tipList.add(tipsList.get(i));
                    }
                }

            }
            return tipList;
        } else {
            tipsList = new ArrayList<>();
            return tipsList;
        }
    }

    public void setUserImage(Uri uri, String userName) {
        for (int i = 0; i < tipsList.size(); i++) {
            TipsSection tipsSection = tipsList.get(i);
            if (tipsSection.getTipsType() == 0) {
                Tips tips = tipsSection.getTips().getTip();
                if (tips.getAppUser().getUserName().equalsIgnoreCase(userName)) {
                    tips.setUri(uri);
                    notifyItemChanged(i);
                }
            }
        }
    }

    private void setUserImage(SimpleDraweeView simpleDraweeView, Tips tips) {
        Common.setProfileImage(simpleDraweeView, tips.getUserId());
    }

    private void setChangeValue(Double createTipPrice, Double stockPrice, TextView txtChangeValue, TextView txtCurrentPrice) {
        txtCurrentPrice.setText(StringHelper.getAmount(stockPrice, "--"));
        if (createTipPrice - stockPrice > 0) {
            txtChangeValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
        } else {
            txtChangeValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
        }
        double change = stockPrice - createTipPrice;
        if (change > 0) {
            txtChangeValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
        } else {
            if (change == 0) {
                txtChangeValue.setTextColor(context.getResources().getColor(R.color.text_color_green));
            } else {
                txtChangeValue.setTextColor(context.getResources().getColor(R.color.text_color_red));
            }
        }
        // I am checking the current price to update the change value.
        if (stockPrice == 0) {
            txtChangeValue.setText("--");
        } else {
                 txtChangeValue.setText(StringHelper.currencyFormatter(change));

        }

    }

    private void showMoreDialog(TipResponse tipResponse, int position, View viewForSS) {
        try {
            String username = tipResponse.getTip().getAppUser().getUserName();
            String userId = tipResponse.getTip().getAppUser().getId();
            BottomSheetDialog moreOptionsDialog = new BottomSheetDialog(context);
            View sheetView = ((Activity) context).getLayoutInflater().inflate(R.layout.tip_more_options, null);
            TextView txtVisitProfile = sheetView.findViewById(R.id.txtVisitProfile);
            TextView txtTweetTip = sheetView.findViewById(R.id.txtTweetTip);
            TextView txtRemoveTip = sheetView.findViewById(R.id.txtRemoveTip);
            TextView txtCancel = sheetView.findViewById(R.id.optionCancel);
            moreOptionsDialog.setContentView(sheetView);
            Resources res = context.getResources();
            String text = String.format(res.getString(R.string.VisitProfile), username);
            txtVisitProfile.setText(text);
            txtVisitProfile.setOnClickListener(view -> {
                ProfileTabbedActivity.starProfileTabbedtActivity(context, false, username, userId, selectedScreen);
            });
            txtTweetTip.setOnClickListener((view) -> {
                moreOptionsDialog.cancel();
                Bitmap image = Common.screenShot(viewForSS);
                Common.prepareShareDeepLinkData(username, tipResponse.getTip(), context, image);
            });
            txtRemoveTip.setOnClickListener(view -> {
                moreOptionsDialog.cancel();
                showConfirmDialog(context, position, tipResponse);
            });
            txtCancel.setOnClickListener(view -> moreOptionsDialog.cancel());
            moreOptionsDialog.show();
        } catch (Exception e) {
        }
    }

    public void deleteTipConfirmed(int position, TipResponse tipResponse) {
        // TipHideHandler handler = new TipHideHandler();
        TipHideHandler tipHideHandler = new TipHideHandler(context, tipResponse.getTip().getId());
        HideTipResponseListener hideTipResponseListener = htResponse -> {
            if (htResponse) {
                Log.d("Item Deleted", "Item Deleted: ");
                removeAt(position);
                //notifyadapter item removed
            } else {
                Log.d("Item Deleted", "Item Deleted: ");
            }
        };

        tipHideHandler.hideTip(hideTipResponseListener);
    }

    public void removeAt(int position) {
        tipsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tipsList.size());
    }

    public void showConfirmDialog(Context context, int position, TipResponse response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.remove_tip_confirm_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        TextView cancelBtn = view.findViewById(R.id.cancel_btn);
        TextView okBtn = view.findViewById(R.id.ok_btn);
        AlertDialog alertDialog = builder.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    deleteTipConfirmed(position, response);
                    alertDialog.dismiss();
                }
            }
        });
    }

    public interface CallTipDetailEventListener {
        void onTipDetailCallEvent(Context context, TipResponse tips, boolean fromComment, int indexPosition, boolean isFromAfterMacd);
    }

    public void requestStoragePermission(String username, Tips tips, Context context) {
        Dexter.withContext(context)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Common.prepareShareDeepLinkData(username, tips, context, null);

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(context, "Something went wrong! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.style_progress_dialog);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        ((AppCompatActivity) context).startActivityForResult(intent, 101);
    }

}
