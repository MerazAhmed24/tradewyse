package com.info.tradewyse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.info.CustomToast.Toasty;
import com.info.adapter.SelectableViewHolder;
import com.info.adapter.StockSearchAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.StringHelper;
import com.info.model.FooterModel;
import com.info.model.NotificationCountModel;
import com.info.model.SelectableStocks;
import com.info.model.Stocks;
import com.info.tradewyse.databinding.ActivitySearchStocksBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchStocksActivity extends BaseActivity {
    private RecyclerView listStocks;
    private EditText edtSearchStocks;
    private ImageView imgClear, backAction, searchstockiconbg;
    private TextView txtStockTitle, otherAction, cancel;
    StockSearchAdapter stockSearchAdapter;
    ArrayList<String> alreadySelectedStocks = new ArrayList<>();
    ArrayList<Stocks> stockList = new ArrayList<>();
    HashMap<String, Stocks> selectedStocksMap = new HashMap<>();
    public static final int SEARCH_REQUEST_CODE = 101;
    public static final int ADD_TIPS = 102;
    public static final int TIPS_ADDED = 103;
    public boolean multipleChoice;
    private String selectedScreen = "";
    private List<Stocks> stocksList;
    LinearLayout bottomLinearLayout;

    public Stocks selectedStock;
    ActivitySearchStocksBinding activitySearchStocksBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchStocksBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_stocks);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        if (getIntent().getStringExtra("selectedScreen") != null)
            selectedScreen = getIntent().getStringExtra("selectedScreen");
        FooterModel footerModel = null;

        if (selectedScreen.equalsIgnoreCase(Constants.HOME)) {
            footerModel = new FooterModel(true, false, false, false, false);
        } else if (selectedScreen.equalsIgnoreCase(Constants.MORE_TAB)) {
            footerModel = new FooterModel(false, false, false, false, true);
        } else if (selectedScreen.equalsIgnoreCase(Constants.NOTIFICATION)) {
            footerModel = new FooterModel(false, false, false, true, false);
        } else if (selectedScreen.equalsIgnoreCase(Constants.SERVICES)) {
            footerModel = new FooterModel(false, false, true, false, false);
        } else {
            footerModel = new FooterModel(false, false, false, false, false);
        }

        activitySearchStocksBinding.setFooterModel(footerModel);

        stockList = getIntent().getExtras().getParcelableArrayList("stocks_list");
        multipleChoice = getIntent().getExtras().getBoolean("multiple_choice");
        for (Stocks stocks : stockList) {
            alreadySelectedStocks.add(stocks.getStockName().trim());
            selectedStocksMap.put(stocks.getStockName().trim(), stocks);
        }
        initializeViews();
        getNotificationCount();
        Common.BottomTabColorChange(this,bottomLinearLayout);

    }

    public void initializeViews() {
        otherAction = findViewById(R.id.otherAction);
        cancel = findViewById(R.id.cancelScreen);
        bottomLinearLayout = findViewById(R.id.bottomView);
        backAction = findViewById(R.id.backAction);
        otherAction.setVisibility(View.VISIBLE);

        if (multipleChoice) {
            otherAction.setText("Done");
            setToolBar("Search");
            backAction.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
        } else {
            backAction.setVisibility(View.GONE);
            otherAction.setText("Next");
            otherAction.setEnabled(false);
            setToolBar(getString(R.string.make_a_tip));
            backAction.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);

        }
        listStocks = findViewById(R.id.listStocks);
        txtStockTitle = findViewById(R.id.txtStockLabel);
        searchstockiconbg = findViewById(R.id.searchstockimg);
        txtStockTitle.setVisibility(View.GONE);
        edtSearchStocks = findViewById(R.id.edtSearchStock);
        edtSearchStocks.requestFocus();

        edtSearchStocks.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        imgClear = findViewById(R.id.imgClear);
        edtSearchStocks.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtSearchStocks.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    getSearchStock(edtSearchStocks.getText().toString());
                    return false;
                }

                return true;
            }
        });


        edtSearchStocks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            private Timer timer = new Timer();
            private final long DELAY = 500; // milliseconds

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    imgClear.setImageResource(R.drawable.ic_search);
                    stocksList.clear();
                    alreadySelectedStocks.clear();
                    stockSearchAdapter = new StockSearchAdapter(new ArrayList<>(), null, new ArrayList<>(), false);
                    listStocks.setAdapter(stockSearchAdapter);
                    searchstockiconbg.setVisibility(View.VISIBLE);
                } else {
                    imgClear.setImageResource(R.drawable.ic_clear);
                }

                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       if (edtSearchStocks.getText().toString().length() > 0)
                                           getSearchStock(edtSearchStocks.getText().toString());
                                   }
                               },
                        DELAY
                );
            }
        });

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearchStocks.setText("");
                stocksList.clear();
                alreadySelectedStocks.clear();
                stockSearchAdapter = new StockSearchAdapter(new ArrayList<>(), null, new ArrayList<>(), false);
                listStocks.setAdapter(stockSearchAdapter);
                searchstockiconbg.setVisibility(View.VISIBLE);
            }
        });

        otherAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionDone();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    public static void startActivity(Context context, ArrayList<Stocks> selectedStocks, boolean multipleChoice, int requestCode, String selectedScreen) {
        Intent starter = new Intent(context, SearchStocksActivity.class);
        starter.putParcelableArrayListExtra("stocks_list", selectedStocks);
        starter.putExtra("multiple_choice", multipleChoice);
        starter.putExtra("selectedScreen", selectedScreen);
        ((AppCompatActivity) context).startActivityForResult(starter, requestCode);
    }


    ArrayList<Stocks> newSelectedStocks = new ArrayList<>();
    ArrayList<Stocks> removedStocks = new ArrayList<>();


    public void getSearchStock(String searchTerm) {
        if (StringHelper.isEmpty(searchTerm)) return;
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this);
        Call<List<Stocks>> call = apiInterface.searchStocks(searchTerm);
        showProgressDialog();
        call.enqueue(new Callback<List<Stocks>>() {
            @Override
            public void onResponse(Call<List<Stocks>> call, Response<List<Stocks>> response) {
                dismissProgressDialog();
                searchstockiconbg.setVisibility(View.GONE);
                if (response.body() != null && !response.body().isEmpty()) {
                    stocksList = response.body();
                    List<Stocks> addedStocks = new ArrayList<>();
                    List<Stocks> searchStocks = new ArrayList<>();
                    for (Stocks stocks : stocksList) {
                        if (alreadySelectedStocks.contains(stocks.getStockName().trim())) {
                            addedStocks.add(stocks);
                        } else {
                            searchStocks.add(stocks);
                        }
                    }

                    stocksList = new ArrayList<>();
                    if (addedStocks.size() > 0) {
                        Stocks stocks = new Stocks();
                        stocks.setSectionName("Added Stocks");
                        stocksList.add(stocks);
                        stocksList.addAll(addedStocks);
                    }

                    if (searchStocks.size() > 0) {
                        if (multipleChoice) {
                            Stocks stocks = new Stocks();
                            stocks.setSectionName("Searched Stocks");
                            stocksList.add(stocks);
                            txtStockTitle.setVisibility(View.GONE);
                        }
                        stocksList.addAll(searchStocks);
                    }

                    if (!multipleChoice) {
                        otherAction.setEnabled(false);
                    }
                    stockSearchAdapter = new StockSearchAdapter(alreadySelectedStocks, new SelectableViewHolder.OnStocksSelectedListener() {
                        @Override
                        public void onStocksSelected(SelectableStocks item) {
                            if (item.isSelected()) {
                                if (multipleChoice) {
                                    if (!alreadySelectedStocks.contains(item.getStockName().trim()))
                                        newSelectedStocks.add(item);
                                } else {
                                    newSelectedStocks.clear();
                                    newSelectedStocks.add(item);
                                    otherAction.setEnabled(true);
                                }

                            } else {
                                newSelectedStocks.remove(item);
                                if (!item.isSelected() && !StringHelper.isEmpty(item.getStockName())) {
                                    removedStocks.add(item);
                                }

                                if (!multipleChoice) {
                                    otherAction.setEnabled(false);
                                }
                            }
                        }
                    }, stocksList, multipleChoice);
                    listStocks.setLayoutManager(new LinearLayoutManager(SearchStocksActivity.this));
                    listStocks.setAdapter(stockSearchAdapter);
                } /*else {
                    Toasty.info(SearchStocksActivity.this, "No result found for your search",
                            Toast.LENGTH_SHORT, true).show();
                } */
            }

            @Override
            public void onFailure(Call<List<Stocks>> call, Throwable t) {
                dismissProgressDialog();
                Log.d(Constants.ON_FAILURE_TAG, "SearchStockActivity getSearchStock: onFailure");
            }
        });
    }

    public void onBackPress(View v) {
        //handleBackPress();
        onBackPressed();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //actionDone();
    }

    public void actionDone() {
        if (multipleChoice) {
            if (newSelectedStocks.size() > 0) {
                addNewStocks(newSelectedStocks);
            } else {
                deleteStocks();
            }
        } else {
            handleBackPress();
        }
    }


    public void handleBackPress() {
        if (stockSearchAdapter != null) {

            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra("selected_stocks", newSelectedStocks);
            resultIntent.putParcelableArrayListExtra("removed_stocks", removedStocks);
            if (newSelectedStocks != null && newSelectedStocks.size() > 0 || (removedStocks != null && removedStocks.size() > 0)) {
                setResult(RESULT_OK, resultIntent);
            } else {
                setResult(RESULT_CANCELED);
            }
        }
        finish();
    }


    public void addNewStocks(ArrayList<Stocks> stockList) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, SearchStocksActivity.this);
        final ProgressDialog progressDialog = new ProgressDialog(SearchStocksActivity.this, R.style.style_progress_dialog);
        progressDialog.setMessage("Adding selected stocks....");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        HashMap<String, ArrayList<Stocks>> stocksMap = new HashMap<>();
        stocksMap.put("stocks", stockList);
        Call<List<Stocks>> call = apiInterface.addMultipleStock(stocksMap);
        call.enqueue(new Callback<List<Stocks>>() {
            @Override
            public void onResponse(Call<List<Stocks>> call, Response<List<Stocks>> response) {
                progressDialog.dismiss();
                if (response.body() != null && response.body().size() > 0) {
                    deleteStocks();
                } else {

                    Toast.makeText(SearchStocksActivity.this, "Something went wrong,please try again later..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Stocks>> call, Throwable t) {
                progressDialog.dismiss();
                Throwable t1 = t;
                Log.d("SearchStockActivity", "AddStockonResponse: " + t.fillInStackTrace());
                Toast.makeText(SearchStocksActivity.this, "Failed,Something went wrong,please try again later..", Toast.LENGTH_SHORT).show();


            }
        });
    }

    public void deleteStocks() {
        if (removedStocks.size() == 0) {
            handleBackPress();
            return;
        }
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, SearchStocksActivity.this);
        final ProgressDialog progressDialog = new ProgressDialog(SearchStocksActivity.this, R.style.style_progress_dialog);
        progressDialog.setMessage("Deleting stocks....");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        ArrayList<String> symbols = new ArrayList<>();
        for (Stocks s : removedStocks) {
            if (selectedStocksMap.containsKey(s.getStockName().trim())) {
                symbols.add(selectedStocksMap.get(s.getStockName().trim()).getId());
            }
        }
        String symbolsString = TextUtils.join(",", symbols);
        Log.e("toDelete", symbolsString + " d");

        Call<List<Stocks>> call = apiInterface.deleteMultipleStock(symbolsString);
        call.enqueue(new Callback<List<Stocks>>() {
            @Override
            public void onResponse(Call<List<Stocks>> call, Response<List<Stocks>> response) {
                progressDialog.dismiss();
                if (response.body() != null && response.body().size() > 0) {
                    handleBackPress();
                } else {
                    Toast.makeText(SearchStocksActivity.this, "Something went wrong,please try again later..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Stocks>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SearchStocksActivity.this, "Failed,Something went wrong,please try again later..", Toast.LENGTH_SHORT).show();
                Log.d(Constants.ON_FAILURE_TAG, "SearchStocksActivity deleteStocks: onFailure");

            }
        });
    }

    public void homeTabClicked(View v) {
        DashBoardActivity.CallDashboardActivity(this, false);
        finish();
    }

    public void chatTabClicked(View v) {
        ChatActivity.starChatActivity(this,false);
        //TabbedChatActivity.CallTabbedChatActivity(SearchStocksActivity.this, true);
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
