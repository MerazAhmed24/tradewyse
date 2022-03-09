package com.info.commons

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.info.interfaces.FlagOptionSelectListener
import com.info.tradewyse.R
import kotlinx.android.synthetic.main.flag_reason_options.view.*
import kotlinx.android.synthetic.main.photo_option.view.*
import kotlinx.android.synthetic.main.photo_option.view.optionCancel

import android.content.Context


class FlagReasonOptionUtility{
    private var REQUEST_FLAG_OPTION = 125;

    private var REQUEST_FLAG_OFFENSIVE = 1;
    private var REQUEST_FLAG_IRRELAVENT = 2;
    private var REQUEST_FLAG_OTHER = 3;

    private val REQUEST_FLAG_OFFENSIVE_STRING = "Offensive";
    private val REQUEST_FLAG_IRRELEVANT_STRING = "Irrelevant";
    private val REQUEST_FLAG_OTHER_STRING = "Other";
    private lateinit var flagOptionSelectListener: FlagOptionSelectListener
    private var activity: AppCompatActivity;
    private constructor(builder: FlagReasonOptionUtility.Builder) {
        activity = builder.getActivity()

    }


    public fun  showFlagOptionList() {
        var selectedOption : Int = 0;
        var mBottomSheetDialog = BottomSheetDialog(activity);
        var sheetView = activity.layoutInflater.inflate(R.layout.flag_reason_options, null);
        mBottomSheetDialog.setContentView(sheetView)
        sheetView.optionOffensive.setOnClickListener {
            mBottomSheetDialog.hide()
            selectedOption = REQUEST_FLAG_OFFENSIVE;
            flagOptionSelectListener.startActivityForResult(selectedOption, REQUEST_FLAG_OPTION)
        }

        sheetView.optionIrrelavent.setOnClickListener {
            mBottomSheetDialog.hide()
            selectedOption = REQUEST_FLAG_IRRELAVENT;
            flagOptionSelectListener.startActivityForResult(selectedOption, REQUEST_FLAG_OPTION)
        }

        sheetView.optionOther.setOnClickListener {
            mBottomSheetDialog.hide()
            selectedOption =  REQUEST_FLAG_OTHER;
            flagOptionSelectListener.startActivityForResult(selectedOption, REQUEST_FLAG_OPTION)
        }
        sheetView.optionCancel.setOnClickListener {
            mBottomSheetDialog.hide()
            selectedOption =  0;
            flagOptionSelectListener.startActivityForResult(selectedOption, REQUEST_FLAG_OPTION)
        }
        mBottomSheetDialog.show()
    }


    fun setFlagOptionSelectListener(flagOptionSelectListener: FlagOptionSelectListener) {
        this.flagOptionSelectListener = flagOptionSelectListener
    }

    public fun getSelectedOptionStringByValue(number: Number, context:Context): String {

        val stringVal = when (number) {
            0 -> context.resources.getString(com.info.tradewyse.R.string.flag_Offensive)
            1 -> context.resources.getString(com.info.tradewyse.R.string.flag_Irrelavent)
            2 -> context.resources.getString(com.info.tradewyse.R.string.flag_Other)
            else -> context.resources.getString(com.info.tradewyse.R.string.flag_Other)
        }
        return stringVal;
    }

    class Builder {

        private var activity: AppCompatActivity

        constructor(activity: AppCompatActivity) {
            this.activity = activity
        }

        fun build(): FlagReasonOptionUtility {
            return FlagReasonOptionUtility(this)
        }

        fun getActivity(): AppCompatActivity {
            return activity
        }

    }
}