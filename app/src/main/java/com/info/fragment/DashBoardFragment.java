package com.info.fragment;

import static com.info.adapter.TipsAdapter.SCROLLED_POSITION;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.info.adapter.StocksAdapter;
import com.info.adapter.TipsAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.EndlessRecyclerViewScrollListener;
import com.info.commons.OnSwipeTouchListener;
import com.info.commons.OnlyVerticalSwipeRefreshLayout;
import com.info.commons.StringHelper;
import com.info.commons.TradWyseSession;
import com.info.interfaces.ImageDownloadResponse;
import com.info.logger.Logger;
import com.info.model.BuyStock;
import com.info.model.Quote;
import com.info.model.SectorNews;
import com.info.model.SellStock;
import com.info.model.ServiceSubscrptionForMACD;
import com.info.model.StockPrice;
import com.info.model.StockViewModel;
import com.info.model.Stocks;
import com.info.model.TipOuterResponse;
import com.info.model.TipResponse;
import com.info.model.Tips;
import com.info.model.TipsSection;
import com.info.model.User;
import com.info.tradewyse.DashBoardActivity;
import com.info.tradewyse.R;
import com.info.tradewyse.TipDetailActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardFragment extends BaseFragment implements TipsAdapter.CallTipDetailEventListener {

    private static final String TAG = "DashBoardFragment";
    RecyclerView stockList, tipsList;
    RelativeLayout swipeView;
    LinearLayout llStocks, llNoStocks, filterTipOption;
    TextView txtSwipe;
    LinearLayoutManager stockLayoutManger, tipsLayoutManager;
    StocksAdapter stocksAdapter;
    TipsAdapter tipsAdapter;
    boolean swipeUp = true;
    OnlyVerticalSwipeRefreshLayout swipeRefreshLayout;
    // Handler handler = new Handler();
    //private Runnable periodicUpdate;
    TextView txtTipText;
    private static final int PAGE_LIMIT = 15;
    public static final int START_FROM = 0;
    EndlessRecyclerViewScrollListener scrollListener;
    ProgressBar progress;
    boolean isFirstTime = false;
    boolean isUSMarketOpen = true;
    TradWyseSession tradWyseSession;
    StockViewModel stockViewModel;
    ImageView imgFilterIcon;
    int filter = TipsAdapter.ALL_TIP;
    DashBoardActivity dashBoardActivity;
    ArrayList<BuyStock> buyStockArrayList = new ArrayList<>();
    ArrayList<SellStock> sellStockArrayList = new ArrayList<>();
    private String buyGif = "", sellGif = "";
    List<String> stocksName = new ArrayList<>();
    private TextView textViewNoData;
    ArrayList<TipsSection> tipsSections = new ArrayList<>();

    public DashBoardFragment() {
        // Required empty public constructor
    }

    public static DashBoardFragment newInstance(String param1, String param2) {
        DashBoardFragment fragment = new DashBoardFragment();
        return fragment;
    }

    @Override
    public void onAttach(@androidx.annotation.NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DashBoardActivity)
            dashBoardActivity = (DashBoardActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dash_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tradWyseSession = TradWyseSession.getSharedInstance(getContext());
        stockViewModel = new ViewModelProvider(getActivity()).get(StockViewModel.class);
        filterTipOption = view.findViewById(R.id.filterTipOption);
        txtTipText = view.findViewById(R.id.txtTipText);
        llNoStocks = view.findViewById(R.id.llNoStocks);
        llStocks = view.findViewById(R.id.llStocks);
        stockList = view.findViewById(R.id.stockList);
        tipsList = view.findViewById(R.id.tipsList);
        swipeView = view.findViewById(R.id.swipeView);
        txtSwipe = view.findViewById(R.id.txtSwipe);
        progress = view.findViewById(R.id.progress);
        imgFilterIcon = view.findViewById(R.id.imgFilterIcon);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        textViewNoData = view.findViewById(R.id.textViewNoData);
        initializeViews();

        tipsList.setNestedScrollingEnabled(false);

        isFirstTime = true;
        showDefaultStocks();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            isFirstTime = true;
            //imgFilterIcon.setVisibility(View.VISIBLE);
        });

        if (!Common.isNetworkAvailable(getActivity())) {
            textViewNoData.setVisibility(View.VISIBLE);
            tipsList.setVisibility(View.GONE);
            return;
        } else {
            textViewNoData.setVisibility(View.GONE);
            tipsList.setVisibility(View.VISIBLE);
        }

        filterTipOption.setOnClickListener(view1 -> showFiltersDialog());
        Logger.debug("DashboardFragment", "Chat Token:- " + tradWyseSession.getAccessTokenStreamIO());
    }

    public void swipeListEvent() {

        LinearLayout.LayoutParams wrapContentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0F);
        LinearLayout.LayoutParams matchParentLayoutParmas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1F);
        if (stockLayoutManger.getOrientation() == LinearLayoutManager.VERTICAL) {
            stockLayoutManger.setOrientation(LinearLayoutManager.HORIZONTAL);
            tipsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            llStocks.setLayoutParams(wrapContentLayoutParams);
            tipsList.setLayoutParams(matchParentLayoutParmas);

            int height = getResources().getDimensionPixelSize(R.dimen.swipe_refresh_layout_height);
            LinearLayout.LayoutParams wrapContentSRL = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height, 0F);
            swipeRefreshLayout.setLayoutParams(wrapContentSRL);
        } else {
            stockLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
            tipsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            llStocks.setLayoutParams(matchParentLayoutParmas);
            tipsList.setLayoutParams(wrapContentLayoutParams);
            LinearLayout.LayoutParams matchContentSRL = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0F);
            swipeRefreshLayout.setLayoutParams(matchContentSRL);
        }
        if (stocksAdapter != null)
            stocksAdapter.notifyItemRangeChanged(0, stocksAdapter.getItemCount());
        if (tipsAdapter != null)
            tipsAdapter.notifyItemRangeChanged(0, tipsAdapter.getItemCount());
    }

    public void initializeViews() {
        stockLayoutManger = new LinearLayoutManager(getActivity());
        stockList.setLayoutManager(stockLayoutManger);
        tipsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        tipsAdapter = new TipsAdapter(new ArrayList<>(), stocksAdapter, getActivity(), tipsLayoutManager, this, buyStockArrayList, sellStockArrayList, buyGif, sellGif, new ArrayList<>(), filter, Constants.HOME);
        tipsList.setLayoutManager(tipsLayoutManager);
        tipsList.setAdapter(tipsAdapter);
        tipsLayoutManager.scrollToPosition(0);

        txtSwipe.setOnClickListener(view -> {
            swipeUp = !swipeUp;
            swipeListEvent();
        });


        swipeView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeTop() {
                if (swipeUp) {
                    swipeListEvent();
                }
                swipeUp = false;
                super.onSwipeTop();
            }

            @Override
            public void onSwipeBottom() {
                if (!swipeUp) {
                    swipeListEvent();
                }
                swipeUp = true;
                super.onSwipeBottom();
            }
        });
        scrollListener = new EndlessRecyclerViewScrollListener(tipsLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                int nextPage = page * PAGE_LIMIT;
                Log.e("nextpage", nextPage + "");
                if (filter != TipsAdapter.AI_TRADE_TIP) {
                    getAllTips(true, nextPage);
                }
            }
        };
        tipsList.addOnScrollListener(scrollListener);
    }

    public void refreshStockList() {
        try {
            stocksAdapter = null;
            imgFilterIcon.setVisibility(View.GONE);
            tipsAdapter = null;
            filter = TipsAdapter.ALL_TIP;
            showDefaultStocks();
        } catch (Exception e) {
            Log.e("RefreshStockListExe", e.toString());
        }

    }

    // Top Stocks list
    ArrayList<Stocks> stockListData = new ArrayList<>();

    public void showDefaultStocks() {

        if (!Common.isNetworkAvailable(getActivity())) {
            Common.showOfflineMemeDialog(getActivity(), getActivity().getResources().getString(R.string.memeMsg),
                    getActivity().getResources().getString(R.string.JustLetYouKnow));
            llNoStocks.setVisibility(View.VISIBLE);
            llStocks.setVisibility(View.GONE);

            return;
        }

        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, getActivity());
        TradWyseSession tradWyseSession = TradWyseSession.getSharedInstance(getContext());
        //.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        Call<List<Stocks>> call = apiInterface.getAllStockOfUser(tradWyseSession.getUserId());
        call.enqueue(new Callback<List<Stocks>>() {
            @Override
            public void onResponse(Call<List<Stocks>> call, Response<List<Stocks>> response) {
                progress.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (getActivity() == null) {
                    return;
                }
                if (response.body() != null) {
                    stockListData = (ArrayList<Stocks>) response.body();
                    Collections.sort(stockListData, new Comparator<Stocks>() {
                        @Override
                        public int compare(Stocks o1, Stocks o2) {
                            if (o1.getCreatedOn() < (o2.getCreatedOn()))
                                return 1;
                            else if (o1.getCreatedOn() == (o2.getCreatedOn())) // it's equals
                                return 0;
                            else
                                return -1;
                        }
                    });

                    if (stocksAdapter == null || stocksAdapter.getItemCount() == 0) {
                        if (getActivity() != null) {
                            stocksAdapter = new StocksAdapter(stockListData, getActivity(), stockLayoutManger, stockViewModel);
                            stockList.setAdapter(stocksAdapter);
                            llStocks.setVisibility(View.VISIBLE);
                            llNoStocks.setVisibility(View.GONE);
                        }

                    } else {
                        if (getActivity() != null) {
                            stocksAdapter.addMoreStocks(stockListData, new ArrayList<Stocks>());
                            stocksAdapter = new StocksAdapter(stocksAdapter.getStocksFromAdapter(), getActivity(), stockLayoutManger, stockViewModel);
                            stockList.setAdapter(stocksAdapter);

                        }
                    }

                    stocksAdapter.SetOnClickListener(new StocksAdapter.OnClickListener() {
                        @Override
                        public void onDeleteListener(int position) {
                            if (stockListData.size() == 0) {
                                llNoStocks.setVisibility(View.VISIBLE);
                                llStocks.setVisibility(View.GONE);
                            } else {
                                llStocks.setVisibility(View.VISIBLE);
                                llNoStocks.setVisibility(View.GONE);
                            }
                        }
                    });

                    if (stockListData.size() > 0) {
                        for (int i = 0; i < stockListData.size(); i++) {
                            stocksName.add(stockListData.get(i).getStockName());
                        }
                    }
                    else {
                        llNoStocks.setVisibility(View.VISIBLE);
                        llStocks.setVisibility(View.GONE);
                    }
                    getStockDetail(stockListData);
                    setDefaultTips();

                }
                getAllTips(true, START_FROM);
            }

            @Override
            public void onFailure(Call<List<Stocks>> call, Throwable t) {
                llNoStocks.setVisibility(View.VISIBLE);
                llStocks.setVisibility(View.GONE);
                if (getActivity() == null) {
                    return;
                }
//                progress.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Log.d(Constants.ON_FAILURE_TAG, "DashboardFragment showDefaultStocks: onFailure");
                getAllTips(true, START_FROM);
            }
        });
    }

    public void getStockDetail(final ArrayList<Stocks> selectedStocks) {
        for (int i = 0; i < selectedStocks.size(); i++) {
            Stocks stocks = selectedStocks.get(i);
            callGetStockDetail(stocks.getStockName().trim());
        }
    }

    // Get all the stock details
    public void callGetStockDetail(final String stockName) {
        if (StringHelper.isEmpty(stockName)) {
            return;
        }
        Log.e("StockName", stockName + " NAME");
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_URL_TRADEWYSE, getActivity());
        Call<Quote> call = apiInterface.getStockDetailData(stockName, Constants.IEX_TOKEN);
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.body() != null) {
                    try {
                        Quote quote = response.body();
                        isUSMarketOpen = quote.isUSMarketOpen();
                        double newStockPrice = Common.formatDouble2(quote.getLatestPrice());
                        StockPrice stockPrice1 = new StockPrice(stockName, quote.getCompanyName(), newStockPrice, quote.getChange());
                        stockViewModel.insert(stockPrice1, getContext());
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                        Log.e("StockName", stockName + ": Exe " + e.toString());
                    }
                } else {
                    Log.e("StockName", stockName + ": Null");
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.v("StockName", stockName + ": onFailure" + t.getMessage());
            }
        });
    }


    // Get AI tip after getting all tip details
    int itemReceiveCount = 0;

    public void setDefaultTips() {

        // check network is available or not.
        if (!Common.isNetworkAvailable(getActivity())) {
            Common.showOfflineMemeDialog(getActivity(), getActivity().getResources().getString(R.string.memeMsg),
                    getActivity().getResources().getString(R.string.JustLetYouKnow));
            return;
        }
        List<Stocks> stocksList = stocksAdapter.getStocksFromAdapter();
        tipsAdapter = null;
        scrollListener.resetState();
        scrollListener.setFirstTime(false);
        itemReceiveCount = 0;
        for (Stocks stock : stocksList) {
            ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, getActivity());
            Call<List<SectorNews>> call = apiInterface.getSectorNewsSentiment(stock.getStockName().trim());
            //progress.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
            call.enqueue(new Callback<List<SectorNews>>() {
                @Override
                public void onResponse(Call<List<SectorNews>> call, Response<List<SectorNews>> response) {

                    itemReceiveCount += 1;
                    if (response.body() != null && response.body().size() > 0) {
                        ArrayList<SectorNews> tipsArrayList = (ArrayList<SectorNews>) response.body();
                        TipsSection tipsSection = new TipsSection();
                        tipsSection.setSectorNewsList(tipsArrayList);
                        tipsSection.setTipsType(TipsAdapter.AI_TRADE_TIP);

                        if (tipsAdapter == null || tipsAdapter.getItemCount() == 0) {
                            if (filter != TipsAdapter.ALL_TIP) {
                                ArrayList<TipsSection> tipsSections = new ArrayList<>();
                                tipsSections.add(tipsSection);
                                tipsAdapter = new TipsAdapter(tipsSections, stocksAdapter, getActivity(), tipsLayoutManager, DashBoardFragment.this, buyStockArrayList, sellStockArrayList, buyGif, sellGif, new ArrayList<>(), filter, Constants.HOME);
                                tipsList.setLayoutManager(tipsLayoutManager);
                                tipsList.setAdapter(tipsAdapter);
                                tipsLayoutManager.scrollToPosition(0);
                                getStockForTipsDetail(tipsSection);
                            } else {
                                tipsSections.add(tipsSection);
                                tipsAdapter = new TipsAdapter(tipsSections, stocksAdapter, getActivity(), tipsLayoutManager, DashBoardFragment.this, buyStockArrayList, sellStockArrayList, buyGif, sellGif, new ArrayList<>(), filter, Constants.HOME);
                                tipsList.setLayoutManager(tipsLayoutManager);
                                tipsList.setAdapter(tipsAdapter);
                                tipsLayoutManager.scrollToPosition(0);
                                getStockForTipsDetail(tipsSection);
                                //getAllTips(true, START_FROM);
                            }
                        } else {
                            tipsSections.add(tipsSection);
                            tipsAdapter.addMoreTips(tipsSection);
                            getStockForTipsDetail(tipsSection);
                        }

                    }
                    progress.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (itemReceiveCount >= stocksList.size()) {
                        if (tipsAdapter == null || tipsAdapter.getItemCount() == 0) {
                            tipsAdapter = new TipsAdapter(new ArrayList<>(), stocksAdapter, getActivity(), tipsLayoutManager, DashBoardFragment.this, buyStockArrayList, sellStockArrayList, buyGif, sellGif, new ArrayList<>(), filter, Constants.HOME);
                            tipsList.setLayoutManager(tipsLayoutManager);
                            tipsList.setAdapter(tipsAdapter);
                            tipsLayoutManager.scrollToPosition(0);
                        }
                        tipsAdapter.sortAITip();
                    }
                }

                @Override
                public void onFailure(Call<List<SectorNews>> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    itemReceiveCount += 1;
                    if (itemReceiveCount >= stocksList.size()) {
                        if (tipsAdapter == null || tipsAdapter.getItemCount() == 0) {
                            tipsAdapter = new TipsAdapter(new ArrayList<>(), stocksAdapter, getActivity(), tipsLayoutManager, DashBoardFragment.this, buyStockArrayList, sellStockArrayList, buyGif, sellGif, new ArrayList<>(), filter, Constants.HOME);
                            tipsList.setLayoutManager(tipsLayoutManager);
                            tipsList.setAdapter(tipsAdapter);
                            tipsLayoutManager.scrollToPosition(0);
                        }

                        tipsAdapter.sortAITip();
                    }
                    Log.d(Constants.ON_FAILURE_TAG, "News onFailure" + stock.getStockName());
                }
            });
        }
        //fff
    }

    public void getStockForTipsDetail(TipsSection tipsSection) {

        if (tipsSection.getTipsType() == TipsAdapter.AI_TRADE_TIP) {
            // if(tipsSection.getSectorNewsList().size()>0){
            callGetStockDetail(tipsSection.getSectorNewsList().get(0).getStockName());
            //  }
        } else if (tipsSection.getTipsType() == TipsAdapter.MENTOR_TIP) {
            callGetStockDetail(tipsSection.getTips().getTip().getStockName().trim());
            User user = tipsSection.getTips().getTip().getAppUser();
            Common.downloadImage(user.getUserName(), user.getImage(), getContext(), new ImageDownloadResponse() {
                @Override
                public void onImageDownload(@NotNull Uri uri) {
                    tipsAdapter.setUserImage(uri, user.getUserName());
                }
            });
        }
    }


    public void getAllTips(boolean showProgress, int offset) {
        if (!Common.isNetworkAvailable(getActivity())) {
            Common.showOfflineMemeDialog(getActivity(), getActivity().getResources().getString(R.string.memeMsg),
                    getActivity().getResources().getString(R.string.JustLetYouKnow));
            return;
        }
        if (offset == START_FROM) {
            scrollListener.resetState();
            scrollListener.setFirstTime(false);
        }

        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, getActivity());
        Call<TipOuterResponse> call = apiInterface.getAllTipsOfUserWithPagination(offset, PAGE_LIMIT);
        if (!isFirstTime) {
            //progress.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
            Common.disableInteraction((AppCompatActivity) getActivity());
        }

        call.enqueue(new Callback<TipOuterResponse>() {
            @Override
            public void onResponse(Call<TipOuterResponse> call, Response<TipOuterResponse> response) {
                isFirstTime = false;
                Common.enableInteraction((AppCompatActivity) getActivity());
                if (response.body() != null) {

                    ArrayList<TipsSection> tipsSectionsLocal = new ArrayList<>();
                    TipOuterResponse tipOuterResponse = response.body();
                    ArrayList<TipsSection> tipsSectionsAfterMacD = new ArrayList<>();
                    if (tipOuterResponse != null) {

                        scrollListener.hasMore(tipOuterResponse.getTipCrossoverResponse().size(), PAGE_LIMIT);

                        ArrayList<TipResponse> tipResponseArrayList = new ArrayList<>();
                        tipResponseArrayList = (ArrayList<TipResponse>) tipOuterResponse.getTipCrossoverResponse();

                        ArrayList<TipResponse> tipCrossoverResponseAfterMacD = new ArrayList<>();
                        tipCrossoverResponseAfterMacD = (ArrayList<TipResponse>) tipOuterResponse.getTipCrossoverResponseAfterMacD();

                        buyStockArrayList = (ArrayList<BuyStock>) tipOuterResponse.getBuyStock();
                        sellStockArrayList = (ArrayList<SellStock>) tipOuterResponse.getSellStock();

                        buyGif = tipOuterResponse.getBuyMacDGif();
                        sellGif = tipOuterResponse.getSellMacDGif();

                        if (tipCrossoverResponseAfterMacD != null) {
                            for (int i = 0; i < tipCrossoverResponseAfterMacD.size(); i++) {
                                TipsSection tipsSection = new TipsSection();
                                tipsSection.setTips(tipCrossoverResponseAfterMacD.get(i));
                                tipsSection.setTipsType(TipsAdapter.MENTOR_TIP);
                                tipsSectionsAfterMacD.add(tipsSection);

                                // Get stocks data of all the tips that are created after MACD.
                                callGetStockDetail(tipsSection.getTips().getTip().getStockName());
                            }
                        }

                        /*if (filter == MENTOR_TIP)
                            tipsSections.clear();*/

                        // If any tip added after macd then these particular tips should not show after macd tips list on UI.
                        for (int i = 0; i < tipResponseArrayList.size(); i++) {
                            TipsSection tipsSection = new TipsSection();
                            boolean isExist = false;
                            for (int j = 0; j < tipsSectionsAfterMacD.size(); j++) {
                                if (tipsSectionsAfterMacD.get(j).getTips().getTip().getId().equalsIgnoreCase(tipResponseArrayList.get(i).getTip().getId())) {
                                    isExist = true;
                                }
                            }
                            if (!isExist) {
                                tipsSection.setTips(tipResponseArrayList.get(i));
                                tipsSection.setTipsType(TipsAdapter.MENTOR_TIP);

                                tipsSections.add(tipsSection);
                                Log.e("ListSize", "" + tipsSections.size());
                            }
                        }

                        tipsSectionsLocal = new ArrayList<>();
                        tipsSectionsLocal.addAll(tipsSections);


                    }
                    itemReceiveCount = 0;

                    tipsAdapter = new TipsAdapter(tipsSections, stocksAdapter, getActivity(), tipsLayoutManager, DashBoardFragment.this, buyStockArrayList, sellStockArrayList, buyGif, sellGif, tipsSectionsAfterMacD, filter, Constants.HOME);
                    tipsList.setLayoutManager(tipsLayoutManager);
                    tipsList.setAdapter(tipsAdapter);
                    if (tipsAdapter == null || tipsAdapter.getItemCount() == 0) {
                        tipsLayoutManager.scrollToPosition(0);
                        ArrayList<TipsSection> allTips = tipsAdapter.getTipsFromAdapter();
                        getStockForTipsDetail(allTips);
                    } else {
                        tipsLayoutManager.scrollToPosition(SCROLLED_POSITION);
                        ArrayList<TipsSection> allTips = tipsAdapter.getTipsFromAdapter();
                        getStockForTipsDetail(allTips);
                    }

                } else {
                    tipsAdapter.hideMacdCards();
                    tipsAdapter.notifyDataSetChanged();
                }
                progress.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<TipOuterResponse> call, Throwable t) {
                isFirstTime = false;
                tipsAdapter.hideMacdCards();
                tipsAdapter.notifyDataSetChanged();
                progress.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Common.enableInteraction((AppCompatActivity) getActivity());
                Log.d(Constants.ON_FAILURE_TAG, "DashboardFragment getAllTips: onFailure");

                textViewNoData.setVisibility(View.VISIBLE);
                tipsList.setVisibility(View.GONE);
            }
        });
    }

    public TipsAdapter getTipsAdapter() {
        return tipsAdapter;
    }

    // This method is to refresh the stock price.
    public void refreshAllStockPrice() {
        Set<String> stocksName = new HashSet<>();
        if (stocksAdapter != null) {
            List<Stocks> stocksList = stocksAdapter.getStocksFromAdapter();
            if (stocksList != null && stocksList.size() > 0) {
                for (Stocks stocks : stocksList) {
                    stocksName.add(stocks.getStockName());
                }
            }
        }

        if (tipsAdapter != null) {
            List<TipsSection> tipsSectionList = tipsAdapter.getTipsFromAdapter(TipsAdapter.MENTOR_TIP);
            if (tipsSectionList != null && tipsSectionList.size() > 0) {
                for (TipsSection tipsSection : tipsSectionList) {
                    if (tipsSection.getSectorNewsList() != null && tipsSection.getSectorNewsList().size() > 0) {
                        stocksName.add(tipsSection.getSectorNewsList().get(0).getStockName());
                    }

                }
            }
        }
        Iterator<String> iterator = stocksName.iterator();
        while (iterator.hasNext()) {
            callGetStockDetail(iterator.next().trim());
        }
    }


    public void getStockForTipsDetail(final ArrayList<TipsSection> defaultTips) {
        Set<String> stocksNameSet = new HashSet<>();
        for (int i = 0; i < defaultTips.size(); i++) {
            TipsSection tipsSection = defaultTips.get(i);
            if (tipsSection.getTipsType() == TipsAdapter.AI_TRADE_TIP) {
                // if(tipsSection.getSectorNewsList().size()>0){
                stocksNameSet.add(tipsSection.getSectorNewsList().get(0).getStockName());

                //  }
            } else if (tipsSection.getTipsType() == TipsAdapter.MENTOR_TIP) {
                stocksNameSet.add(tipsSection.getTips().getTip().getStockName().trim());
                User user = tipsSection.getTips().getTip().getAppUser();
                int finalI = i;
                Common.downloadImage(user.getUserName(), user.getImage(), getContext(), new ImageDownloadResponse() {
                    @Override
                    public void onImageDownload(@NotNull Uri uri) {
                        tipsAdapter.setUserImage(uri, user.getUserName());
                    }
                });
            }
        }

        for (String stockName : stocksNameSet) {
            callGetStockDetail(stockName);
        }
    }


    public void addNewTips(Tips tips) {

        getAllTips(true, START_FROM);
    }


    public ArrayList<Stocks> getStockList() {
        if (stocksAdapter != null) {
            ArrayList<Stocks> stockList = stocksAdapter.getStocksFromAdapter();
            return stockList;
        }
        else
        {
            llNoStocks.setVisibility(View.VISIBLE);
        }
        return new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        callGetServiceSubscriptionPlanForMacd();
    }


    public boolean isUSMarketOpen() {
        return isUSMarketOpen;
    }

    private void showFiltersDialog() {
        DashBoardFragment activity = this;
        try {
            BottomSheetDialog filterOption = new BottomSheetDialog(getActivity());
            View sheetView = activity.getLayoutInflater().inflate(R.layout.filter_option, null);
            TextView filterAITips = sheetView.findViewById(R.id.filterAITips);
            TextView filterMentorTips = sheetView.findViewById(R.id.filterMentorTips);
            TextView optionCancel = sheetView.findViewById(R.id.optionCancel);
            TextView filterResetTips = sheetView.findViewById(R.id.filterResetTips);
            filterOption.setContentView(sheetView);
            if (filter != TipsAdapter.ALL_TIP) {
                filterResetTips.setVisibility(View.VISIBLE);
            } else {
                filterResetTips.setVisibility(View.GONE);
            }

            filterAITips.setOnClickListener(view -> {
                imgFilterIcon.setVisibility(View.VISIBLE);
                imgFilterIcon.setImageResource(R.drawable.filter_ai_tip);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imgFilterIcon.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.text_color_white));
                }
                filter = TipsAdapter.AI_TRADE_TIP;
                tipsAdapter = null;
                setDefaultTips();
                filterOption.cancel();
            });
            filterMentorTips.setOnClickListener((view) -> {
                imgFilterIcon.setVisibility(View.VISIBLE);
                imgFilterIcon.setImageResource(R.drawable.profile_icon_mentor_filter);
                tipsAdapter = null;
                tipsSections.clear();
                filter = TipsAdapter.MENTOR_TIP;
                getAllTips(true, START_FROM);
                filterOption.cancel();
            });

            filterResetTips.setOnClickListener(view -> {
                imgFilterIcon.setVisibility(View.GONE);
                tipsAdapter = null;
                filter = TipsAdapter.ALL_TIP;
                tipsSections.clear();
                setDefaultTips();
                getAllTips(true, START_FROM);
                filterOption.cancel();
            });

            optionCancel.setOnClickListener(view -> filterOption.cancel());
            filterOption.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void callGetServiceSubscriptionPlanForMacd() {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, getActivity());
        Call<ServiceSubscrptionForMACD> call = apiInterface.callGetServiceSubscriptionPlanForMacd();
        call.enqueue(new Callback<ServiceSubscrptionForMACD>() {
            @Override
            public void onResponse(Call<ServiceSubscrptionForMACD> call, Response<ServiceSubscrptionForMACD> response) {
                try {
                    if (response.body() != null) {
                        ServiceSubscrptionForMACD serviceSubscrptionForMACD = response.body();
                        if (serviceSubscrptionForMACD.getStatus().equalsIgnoreCase("true")) {
                            tradWyseSession.setMACDServicePurchased(true);
                            tradWyseSession.setSubscribedMember(true);
                        } else {
                            tradWyseSession.setMACDServicePurchased(false);
                        }
                    } else {
                        tradWyseSession.setMACDServicePurchased(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ServiceSubscrptionForMACD> call, Throwable t) {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d("ActivityResult", "Detail AR fragment");
        if (requestCode == Constants.TIP_DETAIL_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                Object tipPosition = data.getExtras().get("TipPosition");
                boolean isFromAfterMacd = data.getBooleanExtra("isFromAfterMacd", false);
                if (tipPosition == null) {
                    return;
                }
                int position = (Integer) tipPosition;

                if (data.hasExtra("TipDeleted") && data.getExtras().get("TipDeleted").equals("true")) {
                    removeAt(position);
                } else {

                    TipResponse tip = null;
                    if (isFromAfterMacd) {
                        if (tradWyseSession.getIsMentor().equalsIgnoreCase("true"))
                            tip = tipsAdapter.getTipAdapterDataAfterMacd().get(position - 1).getTips();
                        else
                            tip = tipsAdapter.getTipAdapterDataAfterMacd().get(position).getTips();
                    } else {
                        tip = tipsAdapter.getTipAdapterData().get(position).getTips();
                    }
                    if (tip == null) {
                        tip = new TipResponse();
                    }
                    if (data.hasExtra("like_count")) {
                        int likeCount = data.getExtras().getInt("like_count");
                        boolean likeStatus = data.getExtras().getBoolean("like_status");
                        tip.setLikeCount(likeCount);
                        tip.setUserLikeStatus(likeStatus);
                    }

                    if (data.hasExtra("pin_count")) {
                        int pinCount = data.getExtras().getInt("pin_count");
                        boolean pinStatus = data.getExtras().getBoolean("pin_status");
                        tip.setPinCount(pinCount);
                        tip.setUserPinStatus(pinStatus);
                    }

                    if (data.hasExtra("comment_count")) {
                        int comment_count = data.getExtras().getInt("comment_count");
                        tip.setCommentCount(comment_count);
                    }
                    if (isFromAfterMacd)
                        tipsAdapter.getAdapterAfterMacd().notifyItemChanged(position);
                    else
                        tipsAdapter.notifyItemChanged(position);
                }
            }
        }
    }


    @Override
    public void onTipDetailCallEvent(Context context, TipResponse tips, boolean fromComment, int indexPosition, boolean isFromAfterMacd) {
        Intent starter = new Intent(context, TipDetailActivity.class);
        starter.putExtra("tips", tips.getTip());
        starter.putExtra("comment_count", tips.getCommentCount());
        starter.putExtra("like_count", tips.getLikeCount());
        starter.putExtra("pin_count", tips.getPinCount());
        starter.putExtra("hide_status", tips.isUserHideStatus());
        starter.putExtra("like_status", tips.isUserLikeStatus());
        starter.putExtra("pin_status", tips.isUserPinStatus());
        starter.putExtra("fromComment", fromComment);
        starter.putExtra("TipPosition", indexPosition);
        starter.putExtra("selectedScreen", Constants.HOME);
        starter.putExtra("isFromAfterMacd", isFromAfterMacd);
        startActivityForResult(starter, Constants.TIP_DETAIL_REQUEST_CODE);
    }

    public void removeAt(int position) {
        tipsAdapter.getTipAdapterData().remove(position);
        tipsAdapter.notifyItemRemoved(position);
        tipsAdapter.notifyItemRangeChanged(position, tipsAdapter.getTipAdapterData().size());

    }

}
