package com.info.model;

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.info.model.userServiceResponse.ServiceSubscription
import com.info.model.userServiceResponse.ServiceSubscriptionPlan
import kotlinx.android.parcel.Parcelize

@Parcelize
class MyServices :Parcelable{

    @SerializedName("serviceSubscriptionMyMentorPlans")
    @Expose
    var serviceSubscriptionMyMentorPlans:List<ServiceSubscriptionPlan?>?=null

    @SerializedName("myServiceSubscriptionPlans")
    @Expose
    var myServiceSubscriptionPlans:List<ServiceSubscriptionPlan?>?=null

}
