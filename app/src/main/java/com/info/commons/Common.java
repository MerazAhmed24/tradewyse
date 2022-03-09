package com.info.commons;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.info.CircleTransform.CircleTransform;
import com.info.ComplexPreference.ComplexPreferences;
import com.info.adapter.ChatActivityAdapter;
import com.info.api.APIClient;
import com.info.api.ApiInterface;
import com.info.interfaces.ImageDownloadResponse;
import com.info.logger.Logger;
import com.info.model.BasilPrivateChatModel;
import com.info.model.ChatModel;
import com.info.model.FirestoreAuthentication;
import com.info.model.FollowUserModel;
import com.info.model.PublicChatModel;
import com.info.model.SectorNews;
import com.info.model.TipResponse;
import com.info.model.Tips;
import com.info.model.userServiceResponse.ServiceSubscriptionPlan;
import com.info.tradewyse.DashBoardActivity;
import com.info.tradewyse.LoginActivity;
import com.info.tradewyse.MemeMeActivity;
import com.info.tradewyse.R;
import com.info.tradewyse.SubscriptionActivity;
import com.info.tradewyse.TipDetailActivity;
import com.info.tradewyse.TradwyseApplication;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Amit on 5/27/2019.
 */

public class Common {

    private static boolean isKeyboardOpen = false;
    private static MutableLiveData<HashMap<String, Boolean>> liveData = new MutableLiveData<>();

    public static LiveData<HashMap<String, Boolean>> getFollowersData() {
        return liveData;
    }

    public static void setFollowersData(String userName, Boolean isFollow) {
        HashMap<String, Boolean> newFollowersMap = liveData.getValue();
        if (newFollowersMap == null) {
            newFollowersMap = new HashMap<>();
        }
        newFollowersMap.put(userName, isFollow);
        liveData.postValue(newFollowersMap);
    }

    public static void setFollowersData(List<FollowUserModel> followList) {
        if (followList != null && followList.size() > 0 && liveData != null) {
            HashMap<String, Boolean> newFollowersMap = liveData.getValue();
            if (newFollowersMap == null) {
                newFollowersMap = new HashMap<>();
            }
            for (FollowUserModel followUserModel : followList) {
                newFollowersMap.put(followUserModel.getFollowUserName(), followUserModel.getLoginUserFollow());
            }
            liveData.postValue(newFollowersMap);
        }
    }

    public static void observeFollowers(Context context, TextView txtFollowBtn, String userName) {
        getFollowersData().observe((AppCompatActivity) context, stringStringHashMap -> {

            if (stringStringHashMap.containsKey(userName) && stringStringHashMap.get(userName)) {
                txtFollowBtn.setText(context.getResources().getString(R.string.Following));
                txtFollowBtn.setBackgroundResource(R.drawable.dark_follow_btn_bg);
                //txtFollowBtn.setBackgroundResource(R.drawable.bg_white_rounded_border);
            } else {
                txtFollowBtn.setText(context.getResources().getString(R.string.Follow));
                txtFollowBtn.setBackgroundResource(R.drawable.follow_btn_bg_transplant);
                //   txtFollowBtn.setBackgroundResource(R.drawable.bg_white_rounded_border);
            }

        });
    }

    public static void setFollowText(Context context, TextView txtFollowBtn, Boolean isFollow) {
        if (isFollow) {
            txtFollowBtn.setText(context.getResources().getString(R.string.Following));
            //  txtFollowBtn.setBackgroundResource(R.drawable.bg_white_rounded_border);
            txtFollowBtn.setBackgroundResource(R.drawable.dark_follow_btn_bg);
        } else {
            txtFollowBtn.setText(context.getResources().getString(R.string.Follow));
            // txtFollowBtn.setBackgroundResource(R.drawable.bg_white_rounded_border);
            txtFollowBtn.setBackgroundResource(R.drawable.follow_btn_bg_transplant);
        }
    }

    public static void changeLikeBtnBg(Context context, SimpleDraweeView ivLike, Boolean likeStatus) {
        if (likeStatus) {
            ivLike.setBackgroundResource(R.drawable.ic_like_fill);
        } else {
            ivLike.setBackgroundResource(R.drawable.ic_like);
        }
    }


    public static HashMap<String, String> updatedFollowItemList = new HashMap<>();

    /**
     * @param context context
     * @param fontId  fontid
     * @return Typeface
     */
    public static Typeface getTypeface(Context context, int fontId) {
        Typeface typeface = null;
        switch (fontId) {
            case 1:
                //  typeface = Typeface.createFromAsset(context.getAssets(), "JosefinSans-Bold.ttf");
                typeface = Typeface.createFromAsset(context.getAssets(), "sfregular.ttf"); // san seriff
                break;
            case 2:
                typeface = Typeface.createFromAsset(context.getAssets(), "sfbold.ttf");

                break;
            case 3:
                typeface = Typeface.createFromAsset(context.getAssets(), "sfmedium.ttf");
                //  typeface = Typeface.createFromAsset(context.getAssets(), "HelveticaNeue-Regular.ttf");
                break;
            default:
                //  typeface = Typeface.createFromAsset(context.getAssets(), "JosefinSans-Bold.ttf");
                break;
        }
        return typeface;
    }

    public static int getScreenHeightWidth(Context context, int heightWidth) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        if (heightWidth == Constants.DEVICE_HEIGHT) {
            return height;
        }
        if (heightWidth == Constants.DEVICE_WIDTH) {
            return width;
        }

        return height;
    }


/*      NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat decimalFormat =  (DecimalFormat)nf;
        decimalFormat.applyPattern("0.00");*/


    public static String formatDouble(double value) {

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat decimalFormat = (DecimalFormat) nf;
        decimalFormat.applyPattern("0.00");
        return decimalFormat.format(value);
    }

    public static double formatDouble2(double value) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat decimalFormat = (DecimalFormat) nf;
        decimalFormat.applyPattern("0.00");
        return Double.parseDouble(decimalFormat.format(value));
    }


    public static String formatFloat(float value) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat decimalFormat = (DecimalFormat) nf;
        decimalFormat.applyPattern("0.00");
        return decimalFormat.format(value);
    }

    public static String calculatedSuggestion(Double currentPrice, SectorNews sectorNews) {
        String tenDayVal = "0.0";
        String fiftyDayVal = "0.0";
        String twoHundredDayVal = "0.0";
        String currentRSI = "0.0";
        String fiveDayAgoRSI = "0.0";
        double rsiCompareValueMax = 80;
        double rsiCompareValueMin = 20;
        //need to optimize this code.
        // Condition of RSI check.
        if (sectorNews.getCurrentRsi() != null && (sectorNews.getCurrentRsi().equalsIgnoreCase("null") || sectorNews.getCurrentRsi().equalsIgnoreCase("-") ||
                sectorNews.getCurrentRsi().equalsIgnoreCase("") || sectorNews.getCurrentRsi().equalsIgnoreCase("--"))) {
            currentRSI = "0.0";
        } else {
            currentRSI = sectorNews.getCurrentRsi();
        }
        if (sectorNews.getCurrentRsi() != null && (sectorNews.getFiveDayAgoRsi().equalsIgnoreCase("null") || sectorNews.getFiveDayAgoRsi().equalsIgnoreCase("-") ||
                sectorNews.getFiveDayAgoRsi().equalsIgnoreCase("") || sectorNews.getFiveDayAgoRsi
                ().equalsIgnoreCase("--"))) {
            fiveDayAgoRSI = "0.0";
        } else {
            fiveDayAgoRSI = sectorNews.getFiveDayAgoRsi();
        }


        // if RSI not belong to above range condition then below is sma calculation logic.
        //====== if any string or symbol comes in response value then set it to zero.....
        if (sectorNews.getAvg10days().equalsIgnoreCase("null") || sectorNews.getAvg10days().equalsIgnoreCase("-") ||
                sectorNews.getAvg10days().equalsIgnoreCase("") || sectorNews.getAvg10days().equalsIgnoreCase("--")) {
            tenDayVal = "0.0";
            return getRSIStatusWhenSMA_Is_Avoid(currentRSI, fiveDayAgoRSI, rsiCompareValueMax, rsiCompareValueMin, sectorNews);
        } else {
            tenDayVal = sectorNews.getAvg10days();
        }
        if (sectorNews.getAvg50days().equalsIgnoreCase("null") || sectorNews.getAvg50days().equalsIgnoreCase("-") ||
                sectorNews.getAvg50days().equalsIgnoreCase("") || sectorNews.getAvg50days().equalsIgnoreCase("--")) {
            fiftyDayVal = "0.0";
            return getRSIStatusWhenSMA_Is_Avoid(currentRSI, fiveDayAgoRSI, rsiCompareValueMax, rsiCompareValueMin, sectorNews);
        } else {
            fiftyDayVal = sectorNews.getAvg50days();
        }
        if (sectorNews.getAvg200days().equalsIgnoreCase("null") || sectorNews.getAvg200days().equalsIgnoreCase("-") ||
                sectorNews.getAvg200days().equalsIgnoreCase("") || sectorNews.getAvg200days().equalsIgnoreCase("--")) {
            twoHundredDayVal = "0.0";
            return getRSIStatusWhenSMA_Is_Avoid(currentRSI, fiveDayAgoRSI, rsiCompareValueMax, rsiCompareValueMin, sectorNews);
        } else {
            twoHundredDayVal = sectorNews.getAvg200days();
        }
        boolean is10Lower = Double.parseDouble(tenDayVal) < currentPrice;
        boolean is50Lower = Double.parseDouble(fiftyDayVal) < currentPrice;
        boolean is200Lower = Double.parseDouble(twoHundredDayVal) < currentPrice;
        // changed logic which pankaj shared.
        if ((is10Lower && is50Lower && is200Lower) || (!is10Lower && !is50Lower && !is200Lower)) {
            return getRSIStatusWhenSMA_Is_Avoid(currentRSI, fiveDayAgoRSI, rsiCompareValueMax, rsiCompareValueMin, sectorNews);
        } else if ((!is10Lower && !is50Lower && is200Lower) || (!is10Lower && is50Lower && !is200Lower) || (!is10Lower && is50Lower && is200Lower)) {
            return "Sell";
        } else if ((is10Lower && is50Lower && !is200Lower) || (is10Lower && !is50Lower && is200Lower) || (is10Lower && !is50Lower && !is200Lower)) {
            return "Buy";
        }
        //}
        return "Avoid";
    }


    public static boolean checkValueISInvalidOrNull(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.equalsIgnoreCase("-") ||
                value.equalsIgnoreCase("") || value.equalsIgnoreCase("--")) {
            return true;
        } else {
            return false;
        }
    }


    public static String getRSIStatusWhenSMA_Is_Avoid(String strCurrentRSI, String strfiveDayRSI,
                                                      double rsiCompareValueMax, double rsiCompareValueMin, SectorNews sectorNews) {
        String status = "Avoid";
        double currentRSI, fiveDayRSIDouble;
        if (strCurrentRSI == null) {
            return status;
        } else {
            currentRSI = Double.parseDouble(strCurrentRSI);
        }
        if (strfiveDayRSI == null) {
            return status;
        } else {
            fiveDayRSIDouble = Double.parseDouble(strfiveDayRSI);
        }

        if (currentRSI < rsiCompareValueMin || currentRSI > rsiCompareValueMax) {
            if (!checkValueISInvalidOrNull(sectorNews.getCurrentRsi()) && currentRSI < rsiCompareValueMin) {
                return "Buy";
            } else {
                if (!checkValueISInvalidOrNull(sectorNews.getCurrentRsi()) && currentRSI > rsiCompareValueMax) {
                    return "Sell";
                }
            }
        } else if (Math.abs(currentRSI - fiveDayRSIDouble) <= 3) {
            status = "Avoid";
        } else if (fiveDayRSIDouble < currentRSI) {
            status = "Buy";
        } else if (fiveDayRSIDouble > currentRSI) {
            status = "Sell";
        }
        return status;
    }


    public static double getDounbleValuefromStringforPredectionValue(String stringValue) {
        if (stringValue.equalsIgnoreCase("--") || stringValue.equalsIgnoreCase("-") ||
                stringValue.equalsIgnoreCase("") || stringValue.equalsIgnoreCase("null")) {
            return 0.0;
        } else {
            return Double.parseDouble(stringValue);
        }
    }

    public static boolean checkStringIsDouble(String stringValue) {
        if (stringValue.equalsIgnoreCase("--") || stringValue.equalsIgnoreCase("-") ||
                stringValue.equalsIgnoreCase("") || stringValue.equalsIgnoreCase("null") ||
                stringValue == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void setProfileImage(SimpleDraweeView profileImage, String userId) {
        String url = getProfileImageUrl(userId);
        Logger.debug("UserProfileImage", url);
        Uri uri = Uri.parse(url);
        ImageRequest request = ImageRequest.fromUri(uri);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(profileImage.getController()).build();
        profileImage.setController(controller);
    }

    @NotNull
    public static String getProfileImageUrl(String userId) {
        return APIClient.BASE_SERVER_URL + "appUser/getImageByAppUserId?appUserId=" + userId;
    }

    public static void setCharRoomImage(SimpleDraweeView chatRoomImage, String imageUrl) {
        //String url = APIClient.BASE_SERVER_URL + "appUser/getImageByAppUserId?appUserId=" + userId;
        Logger.debug("ChatRoomUrl", imageUrl);
        Uri uri = Uri.parse(imageUrl);
        ImageRequest request = ImageRequest.fromUri(uri);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(chatRoomImage.getController()).build();
        chatRoomImage.setController(controller);
    }

    public static void setProfileImageByUserName(SimpleDraweeView profileImage, String username) {
        String url = APIClient.BASE_SERVER_URL + "appUser/getUserImageByUserName?appUserName=" + username;
        Logger.debug("UserProfileImage", url);
        Uri uri = Uri.parse(url);
        ImageRequest request = ImageRequest.fromUri(uri);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(profileImage.getController()).build();
        profileImage.setController(controller);
    }

    public static void loadBitmapByPicasso(Context pContext, Bitmap pBitmap, ImageView pImageView) {
        try {
            Uri uri = Uri.fromFile(File.createTempFile("temp_file_name", ".jpg", pContext.getCacheDir()));
            OutputStream outputStream = pContext.getContentResolver().openOutputStream(uri);
            pBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            Picasso.get().load(uri).transform(new CircleTransform()).into(pImageView);
        } catch (Exception e) {
            Log.e("LoadBitmapByPicasso", e.getMessage());
        }
    }

    public static void evictImage(String userId) {
        String url = APIClient.BASE_SERVER_URL + "appUser/getImageByAppUserId?appUserId=" + userId;
        Uri uri = Uri.parse(url);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromCache(uri);
        imagePipeline.clearCaches();
    }

    public static void clearCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
    }

    public static void downloadImage(String userName, String imageName, Context context, ImageDownloadResponse imageDownloadResponse) {
        Log.e("username", userName);
        if (context == null) {
            return;
        }
        if (StringHelper.isEmpty(imageName)) {
            imageName = userName;
        }
        try {
            File profileFile = FIleHelper.createNewTempFile(context, imageName + ".jpg");
            Log.e("exists", profileFile.exists() + " Length" + profileFile.length());
            if (profileFile.exists() && profileFile.length() > 0) {

                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", profileFile);
                if (uri != null) {
                    imageDownloadResponse.onImageDownload(uri);
                }
            } else {
                ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
                Call<ResponseBody> call = apiInterface.getProfileImage(userName);
                String finalImageName = imageName;
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                new AsyncTask<Void, Void, Uri>() {
                                    @Override
                                    protected Uri doInBackground(Void... voids) {
                                        File file = FIleHelper.writeResponseBodyToDisk(response.body(), context, finalImageName + "", "jpg");
                                        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                                        return uri;
                                    }

                                    @Override
                                    protected void onPostExecute(Uri uri) {
                                        super.onPostExecute(uri);
                                        if (uri != null) {
                                            imageDownloadResponse.onImageDownload(uri);
                                        }
                                    }
                                }.execute();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(Constants.ON_FAILURE_TAG, "Common downloadImage: onFailure");
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void displayImage(SimpleDraweeView simpleDraweeView, Uri uri) {
        simpleDraweeView.setImageURI(uri);
    }

    public static String getFlagOptionById(int selectedOption, Context context) {
        String strSelectedValue = "";
        switch (selectedOption) {
            case 1:
                strSelectedValue = context.getResources().getString(R.string.flag_Offensive);
                break;
            case 2:
                strSelectedValue = context.getResources().getString(R.string.flag_Irrelavent);
                break;
            case 3:
                strSelectedValue = context.getResources().getString(R.string.flag_Other);
                break;
            default:
                strSelectedValue = context.getResources().getString(R.string.flag_Other);
                break;

        }
        return strSelectedValue;
    }


    public static void showMessage(Context context, String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showMessageWithCenterText(Context context, String message, String title) {
        Dialog dialog = new Dialog(context, R.style.MyAlertDialogStyle2);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtTitle = dialog.findViewById(R.id.title);
        TextView txtMsg = dialog.findViewById(R.id.msg);
        TextView btnOk = dialog.findViewById(R.id.btn_ok);

        txtTitle.setText(title);
        txtMsg.setText(message);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showMessageWithStartAlignText(Context context, String message, String title) {
        Dialog dialog = new Dialog(context, R.style.MyAlertDialogStyle2);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtTitle = dialog.findViewById(R.id.title);
        TextView txtMsg = dialog.findViewById(R.id.msg);
        TextView btnOk = dialog.findViewById(R.id.btn_ok);

        txtTitle.setText(title);
        txtMsg.setText(message);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void becomeMemberDialog(Context context, String message, String title, ServiceSubscriptionPlan serviceSubscriptionPlan) {
        Dialog dialog = new Dialog(context, R.style.MyAlertDialogStyle2);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_become_member_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtTitle = dialog.findViewById(R.id.title);
        TextView txtMsg = dialog.findViewById(R.id.msg);
        TextView btnClose = dialog.findViewById(R.id.btn_close);
        TextView btnNext = dialog.findViewById(R.id.btn_next);

        txtTitle.setText(title);
        txtMsg.setText(message);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnNext.setOnClickListener(v -> {

            Intent intent = SubscriptionActivity.Companion.newIntent(context);
            intent.putExtra(Constants.FROM, Constants.FROM_PROFILE_TABBED_ACTIVITY);
            intent.putExtra("serviceSubscriptionPlan", serviceSubscriptionPlan);
            context.startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();

    }


    public static void mentorServiceDialog(Context context, String title, String message) {
        Dialog dialog = new Dialog(context, R.style.MyAlertDialogStyle2);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_become_member_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtTitle = dialog.findViewById(R.id.title);
        TextView txtMsg = dialog.findViewById(R.id.msg);
        TextView btnClose = dialog.findViewById(R.id.btn_close);
        TextView btnNext = dialog.findViewById(R.id.btn_next);


        btnClose.setText("Later");
        btnNext.setText("Start");

        txtTitle.setText(title);
        txtMsg.setText(message);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        // old url https://mentor.tradetipsapp.com:8080/


        btnNext.setOnClickListener(v -> {
/*
            context.startActivity(new Intent(context, WebViewActivity.class)
                    .putExtra("title", "Mentors Login")
                    .putExtra("url", "http://mentor.tradetipsapp.com/"));
*/
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mentor.tradetipsapp.com/services"));
            context.startActivity(browserIntent);
            dialog.dismiss();
        });

        dialog.show();

    }


    public static void showOfflineMemeDialog(Context context, String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Meme me", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Intent intent = new Intent(context, MemeMeActivity.class);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static void showConfirmDialogOld(Context context, int position, TipResponse response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm!");
        builder.setMessage(context.getResources().getString(R.string.deleteConfirmmsg));
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "delete tip confirm", Toast.LENGTH_SHORT).show();
                /*if(context instanceof DashBoardActivity ){
                   TipsAdapter.deleteTipConfirmed();
                }else if( context instanceof TipDetailActivity){
                    //method of the detail activity.
                }*/
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(context, "You've changed your mind to delete all records", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public static void showConfirmDialog(Context context, int position, TipResponse response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setMessage(context.getResources().getString(R.string.deleteConfirmmsg));
        builder.setTitle("Confirm!");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(context, "Yes click", Toast.LENGTH_SHORT).show();
                if (context instanceof DashBoardActivity) {
                    //  TipsAdapter.deleteTipConfirmed();
                } else if (context instanceof TipDetailActivity) {
                    //method of the detail activity.
                }
                dialog.dismiss();
                // finishActivity();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(context, "You've changed your mind to delete all records", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "No click", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
        builder.show();
        // AlertDialog dialog = builder.create();
        //dialog.show();
    }

    public static void showMessageWithFinishActivity(Context context, String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                ((AppCompatActivity) context).finish();
                // ((AppCompatActivity)context).overridePendingTransition(R.anim.nothing,R.anim.nothing);

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showMessageWithRedirectToDashBoard(Context context, String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                context.startActivity(new Intent(context, DashBoardActivity.class));
                ((AppCompatActivity) context).finishAffinity();
                // ((AppCompatActivity)context).overridePendingTransition(R.anim.nothing,R.anim.nothing);

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void disableInteraction(AppCompatActivity appCompatActivity) {
        if (appCompatActivity != null && !appCompatActivity.isFinishing()) {
            appCompatActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    public static void enableInteraction(AppCompatActivity appCompatActivity) {
        if (appCompatActivity != null && !appCompatActivity.isFinishing()) {
            appCompatActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    public static void changeDrawableColor(Context context, PreFixEditText preFixEditText, int newColor) {

        Drawable drawableLeft = preFixEditText.getCompoundDrawables()[0].mutate();
        drawableLeft.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
    }

    public static void downloadImage(SimpleDraweeView imgMedia, String imagePath, Context context) {
        File tipFile = null;
        try {
            tipFile = FIleHelper.createNewTempFile(context, imagePath + ".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("exists", tipFile.exists() + " Length" + tipFile.length());
        if (tipFile.exists() && tipFile.length() > 0) {

            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", tipFile);
            if (uri != null) {
                displayImage(imgMedia, uri);
            }
        } else {
            ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, context);
            Call<ResponseBody> call = apiInterface.getTipsImage(imagePath);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            new AsyncTask<Void, Void, Uri>() {
                                @Override
                                protected Uri doInBackground(Void... voids) {
                                    File file = FIleHelper.writeResponseBodyToDisk(response.body(), context, imagePath, "gif");
                                    Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                                    return uri;
                                }

                                @Override
                                protected void onPostExecute(Uri uri) {
                                    super.onPostExecute(uri);
                                    // imageProgress.setVisibility(View.GONE);
                                    imgMedia.setVisibility(View.VISIBLE);
                                    if (uri != null) {
                                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                                .setUri(uri)
                                                .setAutoPlayAnimations(true)
                                                .build();
                                        imgMedia.setController(controller);
                                    }

                                }
                            }.execute();
                        }
                    } else {
                        //  imageProgress.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // imageProgress.setVisibility(View.GONE);
                    Log.d(Constants.ON_FAILURE_TAG, "Common downloadImage: onFailure");
                }
            });
        }

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    public static float round(float value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (float) Math.round(value * scale) / scale;
    }


    public static void setEntryExitColor(Context context, double entryPrice, double exitPrice, double stopPrice,
                                         double currentPrice, double createTipPrice, String suggestion,
                                         TextView txtEntry, TextView txtExit, TextView txtStop) {

        Log.d("stockprice===", currentPrice + "===");

        txtStop.setTextColor(context.getResources().getColor(R.color.text_color_black));
        if (isTipPerforming(entryPrice, exitPrice, currentPrice, createTipPrice, suggestion)) {
            txtEntry.setTextColor(context.getResources().getColor(R.color.text_color_green));
            txtExit.setTextColor(context.getResources().getColor(R.color.text_color_green));
        } else {
            txtEntry.setTextColor(context.getResources().getColor(R.color.text_color_black));
            txtExit.setTextColor(context.getResources().getColor(R.color.text_color_black));
            // set stop color.
            if (isLoosingOnStopPrice(entryPrice, exitPrice, stopPrice, currentPrice, createTipPrice, suggestion)) {
                txtStop.setTextColor(context.getResources().getColor(R.color.text_color_red));
            }
        }
    }

    public static boolean isTipPerforming(double entryPrice, double exitPrice, double currentPrice, double createTipPrice, String suggestion) {
        if (suggestion.equalsIgnoreCase("Buy") && entryPrice <= currentPrice) {
            return true;
        } else if (entryPrice >= currentPrice && suggestion.equalsIgnoreCase("Sell")) {
            return true;
        }
        return false;
    }

    public static boolean isLoosingOnStopPrice(double entryPrice, double exitPrice, double stopPrice, double currentPrice, double createTipPrice, String suggestion) {
        if (suggestion.equalsIgnoreCase("Buy") && currentPrice < createTipPrice && currentPrice <= stopPrice) {
            return true;
        } else if (suggestion.equalsIgnoreCase("Sell") && createTipPrice < currentPrice && currentPrice >= stopPrice) {
            return true;
        }
        return false;
    }

    public static void logout(Context context) {

        TradWyseSession.getSharedInstance(context).setOpenMentorDialogCount(0);
        TradWyseSession.getSharedInstance(context).clearSession();
        TradWyseSession.getSharedInstance(context).setLoginAccessToken("");
        TradWyseSession.getSharedInstance(context).setIsLoggedIn(false);
        TradWyseSession.getSharedInstance(context).setMACDServicePurchased(false);
        ComplexPreferences AddBannerPref = ComplexPreferences.getComplexPreferences(context, Constants.ADD_BANNER_PREF, MODE_PRIVATE);
        AddBannerPref.clear();
        AddBannerPref.commit();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((AppCompatActivity) context).finish();
        ((AppCompatActivity) context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void prepareShareDeepLinkData(String username, Tips tips, Context context, Bitmap imageUrl) {
        double change = tips.getStockPrice();
        Task<ShortDynamicLink> shortDynamicLinkTask = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(Uri.parse(Constants.link + "?" + Constants.TIP_ID + "=" + tips.getId()))
                .setDomainUriPrefix(Constants.domainUriPrefix + "?" + Constants.TIP_ID + "=" + tips.getId())
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder(Constants.androidPackageName).setMinimumVersion(Constants.androidMinVersion).build())
                .setIosParameters(new DynamicLink.IosParameters.Builder(Constants.iOSPackageName).setAppStoreId(Constants.iOSAppStoreId).setMinimumVersion(Constants.iOSMinVersion).build())
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            String currentPrice = StringHelper.getAmount(Math.abs(change), "--");
                            currentPrice = currentPrice.contains("$") ? currentPrice.replace("$", "") : currentPrice;
                            String dataToShare = "";
                            if (tips.getAppUser().getTwitterId() == null) {
                                dataToShare = String.format("$ %s\nCurrent price: $%.2f\nSuggested entry: $%.2f\nExit: $%.2f\nStop: $%.2f\nStock tip by %s on TradeTips\n%s", tips.getStockName(), Converter.parseStringToFloat(currentPrice), Converter.parseStringToFloat(tips.getEntryPointAsItis()), Converter.parseStringToFloat(tips.getExitPointAsString()), Converter.parseStringToFloat(tips.getStopPointAsString()), tips.getAppUser().getUserName(), shortLink + "\n");
                            } else {
                                dataToShare = String.format("$ %s\nCurrent price: $%.2f\nSuggested entry: $%.2f\nExit: $%.2f\nStop: $%.2f\nStock tip by %s on TradeTips\n%s", tips.getStockName(), Converter.parseStringToFloat(currentPrice), Converter.parseStringToFloat(tips.getEntryPointAsItis()), Converter.parseStringToFloat(tips.getExitPointAsString()), Converter.parseStringToFloat(tips.getStopPointAsString()), tips.getAppUser().getUserName(), shortLink + "\n\n" + "#" + tips.getAppUser().getTwitterId());
                            }

                            //Toast.makeText(context, dataToShare, Toast.LENGTH_SHORT).show();
                            Logger.debug("TipDetailActivity", dataToShare);

                            if (imageUrl != null) {

                                //Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), BitmapFactory.decodeFile(fileUri), null, null));
                                // use intent to share image
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("image/*");
                                share.putExtra(Intent.EXTRA_TEXT, dataToShare);
                                share.putExtra(Intent.EXTRA_STREAM, getImageUri(context, imageUrl));
                                context.startActivity(Intent.createChooser(share, "Share Tip"));

                            } else {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, dataToShare);
                                sendIntent.setType("text/plain");

                                Intent shareIntent = Intent.createChooser(sendIntent, "Share Tip");
                                context.startActivity(shareIntent);
                            }

                            /*Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, dataToShare);
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            context.startActivity(shareIntent);*/

                        } else {

                            String currentPrice = StringHelper.getAmount(Math.abs(change), "--");
                            currentPrice = currentPrice.contains("$") ? currentPrice.replace("$", "") : currentPrice;

                            String dataToShare = String.format("$%s Current price: $%.2f Suggested entry: $%.2f exit: $%.2f and stop: $%.2f Stock tip by %s on TradeTips", tips.getStockName(), Converter.parseStringToFloat(currentPrice), Converter.parseStringToFloat(tips.getEntryPointAsItis()), Converter.parseStringToFloat(tips.getExitPointAsString()), Converter.parseStringToFloat(tips.getStopPointAsString()), tips.getAppUser().getUserName());

                            //Toast.makeText(context, dataToShare, Toast.LENGTH_SHORT).show();
                            Logger.debug("TipDetailActivity", dataToShare);

                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, dataToShare);
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            context.startActivity(shareIntent);
                        }
                    }
                });
    }

    public static Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public static void prepareServiceShareData2(ServiceSubscriptionPlan serviceSubscriptionPlan, Context context) {
        double change = serviceSubscriptionPlan.getPrice();

        Task<ShortDynamicLink> shortDynamicLinkTask = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(Uri.parse(Constants.link + "?" + Constants.SERVICE_ID + "=" + serviceSubscriptionPlan.getId()))
                .setDomainUriPrefix(Constants.domainUriPrefix + "?" + Constants.SERVICE_ID + "=" + serviceSubscriptionPlan.getId())
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder(Constants.androidPackageName).setMinimumVersion(Constants.androidMinVersion).build())
                .setIosParameters(new DynamicLink.IosParameters.Builder(Constants.iOSPackageName).setAppStoreId(Constants.iOSAppStoreId).setMinimumVersion(Constants.iOSMinVersion).build())
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            String currentPrice = StringHelper.getAmount(Math.abs(change), "--");
                            currentPrice = currentPrice.contains("$") ? currentPrice.replace("$", "") : currentPrice;

                            @SuppressLint("DefaultLocale") String dataToShare = String.format("Service Name: %s\nService Description: %s\nService Price: $%.2f\n%s", serviceSubscriptionPlan.getTitle(), serviceSubscriptionPlan.getDescriptionOne(), Converter.parseStringToFloat(currentPrice), shortLink);
                            //Toast.makeText(context, dataToShare, Toast.LENGTH_SHORT).show();
                            Logger.debug("TipDetailActivity", dataToShare);

                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, dataToShare);
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            context.startActivity(shareIntent);
                        } else {

                            String currentPrice = StringHelper.getAmount(Math.abs(change), "--");
                            currentPrice = currentPrice.contains("$") ? currentPrice.replace("$", "") : currentPrice;

                            @SuppressLint("DefaultLocale") String dataToShare = String.format("Service Name: %s\nService Description: %s\nService Price: $%.2f\n%s", serviceSubscriptionPlan.getTitle(), serviceSubscriptionPlan.getDescriptionOne(), Converter.parseStringToFloat(currentPrice));

                            Logger.debug("TipDetailActivity", dataToShare);

                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, dataToShare);
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            context.startActivity(shareIntent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Logger.debug("Common_Exception", "=" + e.getMessage());
                    }
                });

    }


    public static void prepareServiceShareData(ServiceSubscriptionPlan serviceSubscriptionPlan, Context context) {
        double change = serviceSubscriptionPlan.getPrice();

        Task<ShortDynamicLink> shortDynamicLinkTask = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(Uri.parse(Constants.link + "?" + Constants.SERVICE_ID + "=" + serviceSubscriptionPlan.getId()))
                .setDomainUriPrefix(Constants.domainUriPrefix + "?" + Constants.SERVICE_ID + "=" + serviceSubscriptionPlan.getId())
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder(Constants.androidPackageName).setMinimumVersion(Constants.androidMinVersion).build())
                .setIosParameters(new DynamicLink.IosParameters.Builder(Constants.iOSPackageName).setAppStoreId(Constants.iOSAppStoreId).setMinimumVersion(Constants.iOSMinVersion).build())
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            String currentPrice = StringHelper.getAmount(Math.abs(change), "--");
                            currentPrice = currentPrice.contains("$") ? currentPrice.replace("$", "") : currentPrice;
                            @SuppressLint("DefaultLocale") String dataToShare = String.format("Service Name: %s\nService Description: %s\nService Price: $%.2f\n%s", serviceSubscriptionPlan.getTitle(), serviceSubscriptionPlan.getDescriptionOne(), Converter.parseStringToFloat(currentPrice), shortLink);
                            //Toast.makeText(context, dataToShare, Toast.LENGTH_SHORT).show();
                            Logger.debug("TipDetailActivity", dataToShare);

                            if (serviceSubscriptionPlan.getImageDetails() != null) {

                                Picasso.get().load(APIClient.BASE_SERVER_URL_IMAGE + serviceSubscriptionPlan.getImageDetails()).into(new Target() {
                                    String fileUri = null;
                                    File mydir = null;

                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        try {

                                            mydir = new File(Environment.getExternalStorageDirectory() + "/treadtips");
                                            if (!mydir.exists()) {
                                                mydir.mkdirs();
                                            }

                                            fileUri = mydir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
                                            FileOutputStream outputStream = new FileOutputStream(fileUri);

                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                            outputStream.flush();
                                            outputStream.close();
                                        } catch (IOException e) {
                                            Logger.debug("Error==", e.getMessage());
                                            e.printStackTrace();

                                        }
                                        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "IMG_" + Calendar.getInstance().getTime(), null);
                                        //String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
                                        Uri imageUri = Uri.parse(path);
                                        // Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), BitmapFactory.decodeFile(fileUri), null, null));
                                        // use intent to share image
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("image/*");
                                        share.putExtra(Intent.EXTRA_TEXT, dataToShare);
                                        share.putExtra(Intent.EXTRA_STREAM, imageUri);
                                        context.startActivity(Intent.createChooser(share, "Share Service"));
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    }
                                });

                            } else {
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("text/*");
                                share.putExtra(Intent.EXTRA_TEXT, dataToShare);
                                context.startActivity(Intent.createChooser(share, "Share"));
                            }

                        } else {

                            String currentPrice = StringHelper.getAmount(Math.abs(change), "--");
                            currentPrice = currentPrice.contains("$") ? currentPrice.replace("$", "") : currentPrice;

                            @SuppressLint("DefaultLocale") String dataToShare = String.format("Service Name: %s\nService Description: %s\nService Price: $%.2f\n%s", serviceSubscriptionPlan.getTitle(), serviceSubscriptionPlan.getDescriptionOne(), Converter.parseStringToFloat(currentPrice));

                            Logger.debug("TipDetailActivity", dataToShare);

                            if (serviceSubscriptionPlan.getImageDetails() != null) {

                                Picasso.get().load(APIClient.BASE_SERVER_URL_IMAGE + serviceSubscriptionPlan.getImageDetails()).into(new Target() {

                                    String fileUri = null;
                                    File mydir = null;

                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        try {

                                            mydir = new File(Environment.getExternalStorageDirectory() + "/treadtips");
                                            if (!mydir.exists()) {
                                                mydir.mkdirs();
                                            }

                                            fileUri = mydir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
                                            FileOutputStream outputStream = new FileOutputStream(fileUri);

                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                            outputStream.flush();
                                            outputStream.close();
                                        } catch (IOException e) {
                                            Logger.debug("Error==", e.getMessage());
                                            e.printStackTrace();
                                        }

                                        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), BitmapFactory.decodeFile(fileUri), null, null));
                                        // use intent to share image
                                        Bitmap bit = null;
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("image/*");
                                        share.putExtra(Intent.EXTRA_TEXT, dataToShare);
                                        share.putExtra(Intent.EXTRA_STREAM, bit);
                                        context.startActivity(Intent.createChooser(share, "Share Service"));
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    }
                                });

                            } else {
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("text/*");
                                share.putExtra(Intent.EXTRA_TEXT, dataToShare);
                                context.startActivity(Intent.createChooser(share, "Share"));
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Logger.debug("Common_Exception", "=" + e.getMessage());
                    }
                });

    }

    public static void subscriptionDeepLink(Context context) {

        Task<ShortDynamicLink> shortDynamicLinkTask = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(Uri.parse(Constants.link + "?" + Constants.GOTO_SUBSCRIPTION_PAGE + "=" + Constants.GOTO_SUBSCRIPTION_PAGE))
                .setDomainUriPrefix(Constants.domainUriPrefix + "?" + Constants.GOTO_SUBSCRIPTION_PAGE + "=" + Constants.GOTO_SUBSCRIPTION_PAGE)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder(Constants.androidPackageName).setMinimumVersion(Constants.androidMinVersion).build())
                .setIosParameters(new DynamicLink.IosParameters.Builder(Constants.iOSPackageName).setAppStoreId(Constants.iOSAppStoreId).setMinimumVersion(Constants.iOSMinVersion).build())
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Logger.debug("Commons class", shortLink.toString());
                        } else {
                            Toast.makeText(context, "Unable to generate Short Link", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Logger.debug("Common_Exception", "=" + e.getMessage());
                    }
                });

    }

    public static boolean isValidEmail(String target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getDeviceId(Context context) {
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }


    public static String getVersionName(Context context) {
        String version = "";
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    context.getPackageName(), 0);

            version = info.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public static String getDeviceModel() {
        return Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
    }

    public static String removeLastChars(String str, int chars) {
        return str.substring(0, str.length() - chars);
    }


    public static Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    public static String getFileBase(Context context) {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + context.getString(R.string.app_name);
    }

    public static boolean isKeyboardOpen(RelativeLayout contentView) {
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        contentView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = contentView.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        Log.d("TAG", "keypadHeight = " + keypadHeight);

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            isKeyboardOpen = true;
                        } else {
                            // keyboard is closed
                            isKeyboardOpen = false;
                        }
                    }
                });
        return isKeyboardOpen;
    }

    /**
     * Gets custom tab builder.
     *
     * @param context the context
     * @return the custom tab builder
     */
    public static CustomTabsIntent.Builder getCustomTabBuilder(Context context) {
        final CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.black));
        return builder;

    }

    public static List<String> extractUrls(String input) {
        List<String> result = new ArrayList<String>();

        String[] words = input.split("\\s+");
        Pattern pattern = Patterns.WEB_URL;
        for (String word : words) {
            if (pattern.matcher(word).find()) {
                if (!word.toLowerCase().contains("http://") && !word.toLowerCase().contains("https://")) {
                    word = "http://" + word;
                }
                result.add(word);
            }
        }

        return result;
    }

    public static TextView createLink(TextView targetTextView, String completeString,
                                      String partToClick, ClickableSpan clickableAction) {

        SpannableString spannableString = new SpannableString(completeString);

        // make sure the String is exist, if it doesn't exist
        // it will throw IndexOutOfBoundException
        int startPosition = completeString.indexOf(partToClick);
        int endPosition = completeString.lastIndexOf(partToClick) + partToClick.length();

        spannableString.setSpan(clickableAction, startPosition, endPosition,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        targetTextView.setText(spannableString);
        targetTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return targetTextView;
    }

    public static void makeLinkOfChatReplyCount(Context activity, TextView textView, String completeText, ChatActivityAdapter.OnClickListener onClickListener, ChatModel messageModel, int position, boolean b) {
        if (completeText.contains("|")) {
            int startIndex = completeText.indexOf("|");
            String partToClick = completeText.substring(startIndex + 1);
            createLink(textView, completeText, partToClick,
                    new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            onClickListener.onReplyMsgClick(messageModel, position, true);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            // this is where you set link color, underline, typeface etc.
                            int linkColor = ContextCompat.getColor(activity, R.color.blue);
                            ds.setColor(linkColor);
                            ds.setUnderlineText(false);
                        }
                    });
        }
    }

    public static boolean shouldStockLayoutVisible(Activity activity) {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateTime = simpleDateFormat.format(currentTime);
        String prevSystemDate = TradWyseSession.getSharedInstance(activity).getSystemTime();
        if (!prevSystemDate.equalsIgnoreCase(currentDateTime) || prevSystemDate == null) {
            TradWyseSession.getSharedInstance(activity).setSystemTime(currentDateTime);
            return true;

        } else {
            return false;
        }
    }

    public static boolean checkIfPublicChatGroupIsAvailable(Activity activity) {
        if (TradwyseApplication.publicChatModel != null && TradwyseApplication.basilPrivateChatModel != null) {

            return true;
            //addMessageUpdateChangeListener();
        } else {
            Common.getFirestoreAuthToken(activity);
        }
        return false;
    }

    public static void getFirestoreAuthToken(Activity activity) {
        ApiInterface apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, activity);
        Call<FirestoreAuthentication> call = apiInterface.getFirestoreAuth();
        call.enqueue(new Callback<FirestoreAuthentication>() {
            @Override
            public void onResponse(Call<FirestoreAuthentication> call, Response<FirestoreAuthentication> response) {
                if (response != null && response.body() != null) {
                    FirestoreAuthentication firestoreAuthentication = response.body();
                    ComplexPreferences mComplexPreferences = ComplexPreferences.getComplexPreferences(activity, Constants.FIRESTORE_AUTH_PREF, MODE_PRIVATE);
                    mComplexPreferences.putObject(Constants.FIRESTORE_AUTH_PREF_OBJ, firestoreAuthentication);
                    mComplexPreferences.commit();
                    signInWithCustomToken(firestoreAuthentication.getAuthToken(), activity);
                } else {
                    Log.d(Constants.ON_FAILURE_TAG, "Dashboard getFirestoreAuthToken: No response ");
                }
            }

            @Override
            public void onFailure(Call<FirestoreAuthentication> call, Throwable t) {
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getFirestoreAuthToken: onFailure ", t.fillInStackTrace());
            }
        });
    }

    private static void signInWithCustomToken(String FCMID, Activity activity) {
        FirebaseAuth.getInstance().signInWithCustomToken(FCMID).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkIfPublicChatGroupIsAvailableHere(activity);
                    checkIfBasilPrivateChatGroupIsAvailableHere();
                } else {
                    Toast.makeText(activity, "Unable to signInWithCustomToken", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static void checkIfPublicChatGroupIsAvailableHere(Activity activity) {
        TradwyseApplication.getFirestoreDb().collection(Constants.OPEN_GROUP)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.PRODUCTION_GROUP_ID)) {
                            TradwyseApplication.publicChatModel = new PublicChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            checkIfPublicChatGroupIsAvailable(activity);
                            break;
                        } else if (!Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.DEVELOPMENT_GROUP_ID)) {
                            TradwyseApplication.publicChatModel = new PublicChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION));
                            checkIfPublicChatGroupIsAvailable(activity);
                            break;
                        }
                    }
                } else {
                    //Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private static void checkIfBasilPrivateChatGroupIsAvailableHere() {
        TradwyseApplication.getFirestoreDb().collection(Constants.BASIL_PRIVATE_GROUP)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.PRODUCTION_BASIL_PRIVATE_GROUP_ID)) {
                            TradwyseApplication.basilPrivateChatModel = new BasilPrivateChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION), document.getString(Constants.GROUP_CODE));
                            break;

                        } else if (!Constants.IS_PRODUCTION && document.getId().equalsIgnoreCase(Constants.DEVELOPMENT_BASIL_PRIVATE_GROUP_ID)) {
                            TradwyseApplication.basilPrivateChatModel = new BasilPrivateChatModel(document.getId(), document.getString(Constants.GROUP_IMAGE), document.getString(Constants.GROUP_TITLE), document.getString(Constants.GROUP_DESCRIPTION), document.getString(Constants.GROUP_CODE));
                            break;
                        }
                    }
                } else {
                    //Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }
    public static void BottomTabColorChange(Context context,View bottomView)
    {
        if (Constants.IS_PRODUCTION == false)
        {
            bottomView.setBackgroundColor(ContextCompat.getColor(context, R.color.black));

        }

    }

}

