package com.info.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.info.ComplexPreference.ComplexPreferences;
import com.info.api.APIClient;
import com.info.commons.Common;
import com.info.commons.Constants;
import com.info.commons.TradWyseSession;
import com.info.interfaces.AddSubscribeSuggestedServiceCallback;
import com.info.model.userServiceResponse.ServiceSubscriptionPlan;
import com.info.tradewyse.CheckoutActivity;
import com.info.tradewyse.ProfileTabbedActivity;
import com.info.tradewyse.R;
import com.info.tradewyse.ServiceDetailsScreen;
import com.info.tradewyse.ServicesActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shuhart.stickyheader.StickyAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ServiceAdapter extends StickyAdapter<RecyclerView.ViewHolder, RecyclerView.ViewHolder> {
    private final Context context;
    private final Context activityContext;
    private final List<ServiceSubscriptionPlan> userServiceList;
    private String userName;
    private String isMentor;
    private TradWyseSession tradWyseSession;
    boolean fromLoggedInProfile = true;
    boolean isFromHomePageService = false;
    private AddSubscribeSuggestedServiceCallback addSubscribeSuggestedServiceCallback;

    private static int TYPE_TITLE = 1;
    private static int TYPE_SERVICE_ITEM = 2;
    private String selectedScreen = "";
    private boolean isFilterList;


    public ServiceAdapter(Context activityContext, Context context, List<ServiceSubscriptionPlan> userServiceList,
                          String userName, String isMentor, AddSubscribeSuggestedServiceCallback addSubscribeSuggestedServiceCallback,
                          boolean fromLoggedInProfile, boolean isFromHomePageService, String selectedScreen,
                          boolean isFilterList) {
        this.activityContext = activityContext;
        this.context = context;
        this.userServiceList = userServiceList;
        this.userName = userName;
        this.isMentor = isMentor;
        this.addSubscribeSuggestedServiceCallback = addSubscribeSuggestedServiceCallback;
        tradWyseSession = TradWyseSession.getSharedInstance(context);
        this.fromLoggedInProfile = fromLoggedInProfile;
        this.isFromHomePageService = isFromHomePageService;
        this.selectedScreen = selectedScreen;
        this.isFilterList = isFilterList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;

        if (viewType == TYPE_TITLE) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_service_title, parent,
                    false);
            return new ServiceAdapter.TitleViewHolder(itemView);

        } else {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_services, parent, false);
            return new ServiceAdapter.ServiceHolder(itemView);
        }

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (userServiceList.get(position).getServiceType() != null && userServiceList.get(position).getServiceType().equals(Constants.SERVICE_LIST)) {
            ServiceHolder holder = (ServiceHolder) viewHolder;

            // If filter is applied then hide the title.
            if (isFilterList) {
                holder.txtServiceType.setVisibility(View.GONE);
            }

            if (fromLoggedInProfile) {
                holder.llRoot.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                //holder.txtServiceType.setVisibility(View.VISIBLE);
            } else {
                holder.llRoot.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                //holder.txtServiceType.setVisibility(View.GONE);
            }

            if (isMentor != null && isMentor.equalsIgnoreCase("true")) {

                ServiceSubscriptionPlan serviceResponse = userServiceList.get(position);
                holder.txtServiceType.setText("" + serviceResponse.getServiceTypeMaster().getServiceType());
                holder.txtTitle.setText("" + serviceResponse.getTitle());
                holder.txtDescription.setText("" + serviceResponse.getDescriptionOne());

                double roundOff = Math.round(serviceResponse.getPrice() * 100.0) / 100.0;

                if (roundOff % 1 == 0) {
                    int price = (int) roundOff;
                    holder.txtPrice.setText("Price\n" + (price > 0 ? ("$" + price) : "Free"));
                } else {
                    holder.txtPrice.setText("Price\n" + (roundOff > 0 ? ("$" + roundOff) : "Free"));
                }

                // MACD Tips service always free for mentor only.
                if (serviceResponse.getServiceTypeMaster().getServiceType().equalsIgnoreCase("MACDTips")) {
                    holder.txtPrice.setText("Price\n" + "Free");
                }

                holder.llShare.setVisibility(View.VISIBLE);

                holder.txtSeeMore.setText(context.getResources().getString(R.string.see_more));
                holder.txtSeeMore.setVisibility(View.VISIBLE);

                if (serviceResponse.getImageDetails() != null) {
                    Picasso.get().load(APIClient.BASE_SERVER_URL_IMAGE + serviceResponse.getImageDetails()).placeholder(R.drawable.placeholder).into(holder.imgService);
                } else  if (serviceResponse.getThumbnailImageUrl() != null) {
                    Picasso.get().load(APIClient.BASE_SERVER_URL_IMAGE + serviceResponse.getThumbnailImageUrl()).placeholder(R.drawable.placeholder).into(holder.imgService);
                } else {
                    Picasso.get().load(APIClient.BASE_SERVER_URL_IMAGE + serviceResponse.getImageDetails()).placeholder(R.drawable.placeholder).into(holder.imgService);
                }

                holder.txtSeeMore.setOnClickListener(v -> {

                    if (holder.txtSeeMore.getText().toString().equals("Purchase")) {

                        if (tradWyseSession.isSubscribedMember()) {

                            context.startActivity(new Intent(context, CheckoutActivity.class)
                                    .putExtra(Constants.FROM, Constants.FROM_SERVICE_LIST)
                                    .putExtra("id", serviceResponse.getId())
                                    .putExtra("description", serviceResponse.getDescriptionOne())
                                    .putExtra("price", serviceResponse.getPrice() + ""));
                        } else {
                            Common.becomeMemberDialog(context, context.getString(R.string.just_letting_you_know_description), context.getString(R.string.just_letting_you_know), serviceResponse);
                        }
                    } else if (holder.txtSeeMore.getText().toString().equals(context.getResources().getString(R.string.see_more))) {
                        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(context, "serviceSubscriptionPlanModel", context.MODE_PRIVATE);
                        complexPreferences.putObject("serviceSubscriptionPlanModelObject", serviceResponse);
                        complexPreferences.commit();

                        if (isFromHomePageService) {
                            context.startActivity(new Intent(context, ServiceDetailsScreen.class)
                                    .putExtra("selectedScreen", selectedScreen)
                                    .putExtra("serviceSubscriptionPlanId", serviceResponse.getId()));
                        } else {
                            ((ProfileTabbedActivity) context).startActivityForResult(new Intent(context, ServiceDetailsScreen.class)
                                            .putExtra("selectedScreen", selectedScreen)
                                            .putExtra("serviceSubscriptionPlanId", serviceResponse.getId())
                                    , ProfileTabbedActivity.REQUEST_CODE_FOR_SERVICE_DETAILS);
                        }
                    }
                });


                holder.llRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(context, "serviceSubscriptionPlanModel", context.MODE_PRIVATE);
                        complexPreferences.putObject("serviceSubscriptionPlanModelObject", serviceResponse);
                        complexPreferences.commit();
                        if (isFromHomePageService) {
                            context.startActivity(new Intent(context, ServiceDetailsScreen.class)
                                    .putExtra("selectedScreen", selectedScreen)
                                    .putExtra("serviceSubscriptionPlanId", serviceResponse.getId()));
                        } else {
                            ((ProfileTabbedActivity) context).startActivityForResult(new Intent(context, ServiceDetailsScreen.class)
                                            .putExtra("selectedScreen", selectedScreen)
                                            .putExtra("serviceSubscriptionPlanId", serviceResponse.getId())
                                    , ProfileTabbedActivity.REQUEST_CODE_FOR_SERVICE_DETAILS);
                        }
                    }
                });

                holder.llShare.setOnClickListener(v -> requestStoragePermission(serviceResponse));

            }

            else {

                ServiceSubscriptionPlan serviceSubscriptionPlan = userServiceList.get(position);
                holder.txtServiceType.setText("" + serviceSubscriptionPlan.getServiceTypeMaster().getServiceType());
                holder.txtTitle.setText("" + serviceSubscriptionPlan.getTitle());
                holder.txtDescription.setText("" + serviceSubscriptionPlan.getDescriptionOne());
                //holder.txtPrice.setText("Price\n" + (serviceSubscriptionPlan.getPrice() > 0 ? ("$" + serviceSubscriptionPlan.getPrice()) : "Free"));
                String userName = TradWyseSession.getSharedInstance(context).getUserName();


                double roundOff = Math.round(serviceSubscriptionPlan.getPrice() * 100.0) / 100.0;

                if (roundOff % 1 == 0) {
                    int price = (int) roundOff;
                    holder.txtPrice.setText("Price\n" + (price > 0 ? ("$" + price) : "Free"));
                } else {
                    holder.txtPrice.setText("Price\n" + (roundOff > 0 ? ("$" + roundOff) : "Free"));
                }


                holder.txtSeeMore.setVisibility(View.VISIBLE);
                holder.llShare.setVisibility(View.VISIBLE);
                holder.txtSeeMore.setText(context.getResources().getString(R.string.see_more));


                if (serviceSubscriptionPlan.getImageDetails() != null) {
                    Picasso.get().load(APIClient.BASE_SERVER_URL_IMAGE + serviceSubscriptionPlan.getImageDetails()).placeholder(R.drawable.placeholder).into(holder.imgService);
                } else  if (serviceSubscriptionPlan.getThumbnailImageUrl() != null) {
                    Picasso.get().load(APIClient.BASE_SERVER_URL_IMAGE + serviceSubscriptionPlan.getThumbnailImageUrl()).placeholder(R.drawable.placeholder).into(holder.imgService);
                } else {
                    Picasso.get().load(APIClient.BASE_SERVER_URL_IMAGE + serviceSubscriptionPlan.getImageDetails()).placeholder(R.drawable.placeholder).into(holder.imgService);
                }

                holder.llShare.setOnClickListener(v -> requestStoragePermission(serviceSubscriptionPlan));


                holder.txtSeeMore.setOnClickListener(v -> {
                    if (holder.txtSeeMore.getText().toString().equals("Purchase")) {
                        if (tradWyseSession.isSubscribedMember()) {
                            context.startActivity(new Intent(context, CheckoutActivity.class)
                                    .putExtra(Constants.FROM, Constants.FROM_SERVICE_LIST)
                                    .putExtra("id", serviceSubscriptionPlan.getId())
                                    .putExtra("description", serviceSubscriptionPlan.getDescriptionOne())
                                    .putExtra("price", serviceSubscriptionPlan.getPrice() + ""));
                        } else {
                            Common.becomeMemberDialog(context, context.getString(R.string.just_letting_you_know_description), context.getString(R.string.just_letting_you_know), serviceSubscriptionPlan);
                        }
                    } else if (holder.txtSeeMore.getText().toString().equals(context.getResources().getString(R.string.see_more))) {
                        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(context, "serviceSubscriptionPlanModel", context.MODE_PRIVATE);
                        complexPreferences.putObject("serviceSubscriptionPlanModelObject", serviceSubscriptionPlan);
                        complexPreferences.commit();
                        if (isFromHomePageService) {
                            context.startActivity(new Intent(context, ServiceDetailsScreen.class)
                                    .putExtra("selectedScreen", selectedScreen)
                                    .putExtra("serviceSubscriptionPlanId", serviceSubscriptionPlan.getId()));
                        } else {
                            ((ProfileTabbedActivity) context).startActivityForResult(new Intent(context, ServiceDetailsScreen.class)
                                            .putExtra("selectedScreen", selectedScreen)
                                            .putExtra("serviceSubscriptionPlanId", serviceSubscriptionPlan.getId())
                                    , ProfileTabbedActivity.REQUEST_CODE_FOR_SERVICE_DETAILS);
                        }
                    } else {
                        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(context, "serviceSubscriptionPlanModel", context.MODE_PRIVATE);
                        complexPreferences.putObject("serviceSubscriptionPlanModelObject", serviceSubscriptionPlan);
                        complexPreferences.commit();
                        if (isFromHomePageService) {
                            context.startActivity(new Intent(context, ServiceDetailsScreen.class)
                                    .putExtra("selectedScreen", selectedScreen)
                                    .putExtra("serviceSubscriptionPlanId", serviceSubscriptionPlan.getId()));
                        } else {
                            ((ProfileTabbedActivity) context).startActivityForResult(new Intent(context, ServiceDetailsScreen.class)
                                            .putExtra("selectedScreen", selectedScreen)
                                            .putExtra("serviceSubscriptionPlanId", serviceSubscriptionPlan.getId())
                                    , ProfileTabbedActivity.REQUEST_CODE_FOR_SERVICE_DETAILS);
                        }
                    }
                });


                holder.llRoot.setOnClickListener(v -> {

                    if (isFromHomePageService) {
                        context.startActivity(new Intent(context, ServiceDetailsScreen.class)
                                .putExtra("selectedScreen", selectedScreen)
                                .putExtra("serviceSubscriptionPlanId", serviceSubscriptionPlan.getId()));
                    } else {
                        ((ProfileTabbedActivity) context).startActivityForResult(new Intent(context, ServiceDetailsScreen.class)
                                .putExtra("selectedScreen", selectedScreen)
                                .putExtra("serviceSubscriptionPlanId", serviceSubscriptionPlan.getId()), ProfileTabbedActivity.REQUEST_CODE_FOR_SERVICE_DETAILS);
                    }

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(context, "serviceSubscriptionPlanModel", context.MODE_PRIVATE);
                    complexPreferences.putObject("serviceSubscriptionPlanModelObject", serviceSubscriptionPlan);
                    complexPreferences.commit();
                });

            }

        } else if (userServiceList.get(position).getServiceType() != null && userServiceList.get(position).getServiceType().equals(Constants.SERVICE_HEADER)) {
            TitleViewHolder titleViewHolder = (TitleViewHolder) viewHolder;
            titleViewHolder.txtServiceTitle.setText(userServiceList.get(position).getServiceHeaderTitle());

            if (isFromHomePageService) {
                titleViewHolder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg_india));

            } else {
                if (((ProfileTabbedActivity) activityContext).fromLoggedInProfile) {
                    titleViewHolder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg_india));
                    //titleViewHolder.txtServiceTitle.setBackgroundColor(context.getResources().getColor(R.color.color_other_profile_bg));
                } else {
                    titleViewHolder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg_india));
                    //titleViewHolder.txtServiceTitle.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg));
                }
            }
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (userServiceList.get(position).getServiceType() != null && userServiceList.get(position).getServiceType().equals(Constants.SERVICE_HEADER)) {
            return TYPE_TITLE;
        } else if (userServiceList.get(position).getServiceType() != null && userServiceList.get(position).getServiceType().equals(Constants.SERVICE_LIST)) {
            return TYPE_SERVICE_ITEM;
        } else {
            return -1;
        }

    }

    @Override
    public int getItemCount() {

        return userServiceList.size();
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        if (itemPosition >= userServiceList.size()) {
            return -1;
        }
        return userServiceList.get(itemPosition).getSection();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return createViewHolder(parent, TYPE_TITLE);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int headerPosition) {
        if (headerPosition != -1) {
            Log.d("header_pos=", "==" + headerPosition);
            TitleViewHolder titleViewHolder = (TitleViewHolder) viewHolder;
            titleViewHolder.txtServiceTitle.setText(userServiceList.get(headerPosition).getServiceHeaderTitle());

            if (isFromHomePageService) {
                titleViewHolder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg_india));

            } else {
                if (((ProfileTabbedActivity) activityContext).fromLoggedInProfile) {
                    titleViewHolder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg_india));
                } else {
                    titleViewHolder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.color_app_dark_bg_india));
                }
            }
        }
    }

    static class ServiceHolder extends RecyclerView.ViewHolder {
        MaterialTextView txtServiceType;
        AppCompatImageView imgService;
        MaterialTextView txtTitle;
        MaterialTextView txtDescription;
        TextView txtSeeMore;
        MaterialTextView txtPrice;
        LinearLayout llRoot;
        LinearLayout llShare;
        LinearLayout llDivider;
        MaterialTextView txtDividerLabel;

        public ServiceHolder(@NonNull View itemView) {
            super(itemView);

            imgService = itemView.findViewById(R.id.img_service);
            txtServiceType = itemView.findViewById(R.id.service_type);
            txtTitle = itemView.findViewById(R.id.title);
            txtDescription = itemView.findViewById(R.id.description);
            txtSeeMore = itemView.findViewById(R.id.btn_Subscribe);
            txtPrice = itemView.findViewById(R.id.txt_price);
            llRoot = itemView.findViewById(R.id.ll_root);
            llShare = itemView.findViewById(R.id.ll_share);
            llDivider = itemView.findViewById(R.id.ll_divider);
            txtDividerLabel = itemView.findViewById(R.id.txt_divider_label);
        }

    }

    public static class TitleViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView txtServiceTitle;
        private RelativeLayout linearLayout;

        public TitleViewHolder(@NonNull View view) {
            super(view);
            txtServiceTitle = view.findViewById(R.id.txt_service_title);
            linearLayout = view.findViewById(R.id.background);
        }
    }


    public void requestStoragePermission(ServiceSubscriptionPlan serviceSubscriptionPlan) {
        Dexter.withContext(context)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Common.prepareServiceShareData(serviceSubscriptionPlan, context);

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
                        Toast.makeText(context, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.style_progress_dialog);
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
        if (isFromHomePageService) {
            ((ServicesActivity) context).startActivityForResult(intent, 101);
        } else {
            ((ProfileTabbedActivity) context).startActivityForResult(intent, 101);
        }
    }

}
