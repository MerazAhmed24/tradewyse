package com.info.tradewyse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.info.commons.Constants;
import com.info.commons.StringHelper;
import com.info.model.Subscription;
import com.info.model.userServiceResponse.ServiceSubscriptionPlan;

public class ServicesPaymentActivity extends BaseActivity {

    private String from = "";
    private double price;
    double promocode = 30.00;
    private double subscriptionAmount;
    private String subtotalprice;
    private double subtotalpricetotal;
    private Subscription subscription;
    private ServiceSubscriptionPlan serviceSubscriptionPlan;
    private Context context;
    private TextView tvsubsriptionprice, tvserviceprice, tvservicetype, tvmentorname;
    private TextView tvsubtotalamount, tvdicountamount, tvTotalAmount,tvTotalAmountPay;
    private Button btnContinue;

    public static void start(Context context, Subscription subscription, Boolean isFromSpecialOfferActivity, String from, ServiceSubscriptionPlan serviceSubscriptionPlan) {
        Intent starter = new Intent(context, ServicesPaymentActivity.class);
        starter.putExtra("subscription", subscription);
        starter.putExtra(Constants.FROM, from);
        starter.putExtra("isFromSpecialOfferActivity", isFromSpecialOfferActivity);
        starter.putExtra("serviceSubscriptionPlan", serviceSubscriptionPlan);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_payment);
        setToolBarAddTip("Review Purchase");
        context = getApplicationContext();

        if (getIntent() != null) {
            from = getIntent().getStringExtra(Constants.FROM);
            subscription = getIntent().getParcelableExtra("subscription");
            serviceSubscriptionPlan = getIntent().getParcelableExtra("serviceSubscriptionPlan");
        }

        tvsubsriptionprice = findViewById(R.id.tvsubsriptionprice);
        tvserviceprice = findViewById(R.id.tvserviceprice);
        tvservicetype = findViewById(R.id.tvservicetype);
        tvmentorname = findViewById(R.id.tvmentorname);
        tvsubtotalamount = findViewById(R.id.tvsubtotalamount);
        tvdicountamount = findViewById(R.id.tvdicountamount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvTotalAmountPay=findViewById(R.id.tvtotalpayamount2);
        btnContinue = findViewById(R.id.btnContinue);

        tvservicetype.setText(StringHelper.capitalFirstLetter(serviceSubscriptionPlan.getServiceTypeMaster().getServiceType()));
        tvmentorname.setText("By "+serviceSubscriptionPlan.getMentorName() + "");
        tvsubsriptionprice.setText("$"+subscription.getSubscriptionPrice());
        //convert long decimal to two digit for service price
        price = serviceSubscriptionPlan.getPrice();
        subscriptionAmount = Float.parseFloat(String.valueOf(price));
        subscriptionAmount = (Math.round(subscriptionAmount * 100.0) / 100.0);
        tvserviceprice.setText("$"+subscriptionAmount);
        //code for subtotal
        subtotalprice = subscription.getSubscriptionPrice();
        subtotalpricetotal = Double.parseDouble(subtotalprice);
        subtotalpricetotal = (Math.round(subtotalpricetotal* 100.0) / 100.0);
        subtotalpricetotal = subtotalpricetotal+subscriptionAmount;
        subtotalpricetotal = (Math.round(subtotalpricetotal* 100.0) / 100.0);
        tvsubtotalamount.setText("$"+subtotalpricetotal);
        //code for promo-code
        float discountPromoCode = (Float.parseFloat(subscription.getSubscriptionPrice()) * 20 ) / 100 ;
        tvdicountamount.setText("-$"+discountPromoCode+"");
        double promocodeAppliedAmount = subtotalpricetotal - discountPromoCode;
        promocodeAppliedAmount=(Math.round(promocodeAppliedAmount * 100.0) / 100.0);

        //code for total amount to be pay
        tvTotalAmount.setText("$"+promocodeAppliedAmount+"");
        tvTotalAmountPay.setText("$"+promocodeAppliedAmount+"");

        double finalPromocodeAppliedAmount = promocodeAppliedAmount;
        btnContinue.setOnClickListener(v-> {
            startActivity(new Intent(ServicesPaymentActivity.this, CheckoutActivity.class)
                    .putExtra("subscription", subscription)
                    .putExtra(Constants.FROM, from)
                    .putExtra("price", finalPromocodeAppliedAmount + "")
                    .putExtra("subsPrice", Float.parseFloat(subscription.getSubscriptionPrice()) - discountPromoCode + "")
                    .putExtra("servicePrice", subscriptionAmount + "")
                    .putExtra("isFromSpecialOfferActivity", false));
        });

    }
}