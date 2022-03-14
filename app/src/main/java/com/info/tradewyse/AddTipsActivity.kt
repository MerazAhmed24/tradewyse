package com.info.tradewyse

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar
import com.info.api.APIClient
import com.info.commons.*
import com.info.interfaces.CustomTextWatcher
import com.info.interfaces.PhotoOptionSelectListener
import com.info.model.Quote
import com.info.model.Stocks
import com.info.model.Tips
import com.info.model.User
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_add_tips.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Builder
import okhttp3.MultipartBody.Companion.FORM
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

var ENTRY_LEVEL = 0
var EXIT_LEVEL = 1
var STOP_LEVEL = 2

class AddTipsActivity : BaseActivity() {
    var entryValue: Float = 0f
    var exitValue: Float = 0f
    var stopValue: Float = 0f
    var stockPrice = 0f
    private var photoUtility: PhotoUtility? = null
    lateinit var stocks: Stocks
    lateinit var context: Context;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tips)
        stocks = intent.getParcelableExtra("stocks")!!
        context = applicationContext;
        setToolBarAddTip(getString(R.string.make_a_tip))
        otherAction.visibility = View.VISIBLE
        otherAction.text = "Post Tip"
        takePhoto.setOnClickListener {
            takePhoto()
        }

        txtEntryPoint.setChangeListener(object : CustomTextWatcher() {
            override fun onTextChange(value: Float) {
                entryValue = value
                exitValue = entryValue
                stopValue = entryValue
                txtExitPoint.setText("0.00")
                txtStopPoint.setText("0.00")
                setEntryPointTextColor()
            }
        })

        otherAction.setOnClickListener {
            if (validate()) {
                addTips()
            }

        }

        callGetStockDetail()

        edtTipComment.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                hideSeekBar(rlSetEntry, seekBarEntry, txtSetEntry, txtEntryPoint)
                hideSeekBar(rlSetExit, seekBarExit, txtSetExit, txtExitPoint)
                hideSeekBar(rlSetStop, seekBarStop, txtSetStop, txtStopPoint)
            }
        }
    }


    private fun setStockValue() {

        stockPrice = stocks.stockPrice.toFloat()
        entryValue = stockPrice
        txtStockName.text = stocks.stockName
        txtStockCompany.text = stocks.companyName
        txtCurrentValue.text = "" + Common.formatFloat(stockPrice)

        val change = Common.formatDouble(Math.abs(stocks.stockChange))
        txtChange.text = change

        if (stocks.stockChange < 0.0) {
            txtChange.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_price_low, 0)
            txtChange.setTextColor(ContextCompat.getColor(this, R.color.text_color_red))
        } else {
            txtChange.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_price_high, 0)
            txtChange.setTextColor(ContextCompat.getColor(this, R.color.green_light))
        }

        txtEntryPoint.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            //setFocusAnimation(hasFocus, rlSetEntry, seekBarEntry, txtSetEntry, ENTRY_LEVEL)
            if (hasFocus)
                rlSetEntry.performClick()
        }
        var doneAction = TextView.OnEditorActionListener { textView, actionId, event ->
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                edtTipComment.requestFocus()
                hideKeyboard()
            }
            return@OnEditorActionListener false;
        }
        txtEntryPoint.setOnEditorActionListener(doneAction)

        txtExitPoint.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            // setFocusAnimation(hasFocus, rlSetExit, seekBarExit, txtSetExit, EXIT_LEVEL)
            if (hasFocus) rlSetExit.performClick()
        }
        txtExitPoint.setOnEditorActionListener(doneAction)


        txtStopPoint.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            //setFocusAnimation(hasFocus, rlSetStop, seekBarStop, txtSetStop, STOP_LEVEL)
            if (hasFocus) rlSetStop.performClick()
        }
        txtStopPoint.setOnEditorActionListener(doneAction)
        //txtEntryPoint.requestFocus()
        setValueSelector(seekBarEntry, ENTRY_LEVEL)
        txtEntryPoint.setTextColor(ContextCompat.getColor(this, R.color.text_color_white))

        rlSetEntry.setOnClickListener {
            if (seekBarEntry.visibility == View.GONE) {
                setFocusAnimation(true, rlSetEntry, seekBarEntry, txtSetEntry, ENTRY_LEVEL)
                setFocusAnimation(false, rlSetExit, seekBarExit, txtSetExit, EXIT_LEVEL)
                setFocusAnimation(false, rlSetStop, seekBarStop, txtSetStop, STOP_LEVEL)
                txtStopPoint.clearFocus()
                txtExitPoint.clearFocus()
            }
        }

        rlSetExit.setOnClickListener {
            if (seekBarExit.visibility == View.GONE) {
                setFocusAnimation(false, rlSetEntry, seekBarEntry, txtSetEntry, ENTRY_LEVEL)
                setFocusAnimation(true, rlSetExit, seekBarExit, txtSetExit, EXIT_LEVEL)
                setFocusAnimation(false, rlSetStop, seekBarStop, txtSetStop, STOP_LEVEL)
                txtEntryPoint.clearFocus()
                txtStopPoint.clearFocus()
            }

        }

        rlSetStop.setOnClickListener {
            if (seekBarStop.visibility == View.GONE) {
                setFocusAnimation(false, rlSetEntry, seekBarEntry, txtSetEntry, ENTRY_LEVEL)
                setFocusAnimation(false, rlSetExit, seekBarExit, txtSetExit, EXIT_LEVEL)
                setFocusAnimation(true, rlSetStop, seekBarStop, txtSetStop, STOP_LEVEL)
                txtEntryPoint.clearFocus()
                txtExitPoint.clearFocus()
            }

        }
        var decimalDigitsInputFilter = DecimalDigitsInputFilter(10, 2)
        txtEntryPoint.filters = arrayOf(decimalDigitsInputFilter)
        txtExitPoint.filters = arrayOf(decimalDigitsInputFilter)
        txtStopPoint.filters = arrayOf(decimalDigitsInputFilter)
        setFocusAnimation(true, rlSetEntry, seekBarEntry, txtSetEntry, ENTRY_LEVEL)
    }

    private fun hideSeekBar(rl: RelativeLayout, seekBar: CrystalSeekbar, txtTitle: TextView, editText: PreFixEditText) {
        txtTitle.setTextColor(ContextCompat.getColor(this, R.color.text_color_grey))
        rl.background = ContextCompat.getDrawable(this, R.drawable.rounded_corner)
        seekBar.visibility = View.GONE
        editText.formatValue()
    }

    private fun setFocusAnimation(hasFocus: Boolean, rl: RelativeLayout, seekBar: CrystalSeekbar, txtTitle: TextView, level: Int) {
        if (hasFocus) {
            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.interpolator = DecelerateInterpolator() //add this
            fadeIn.duration = 500
            rl.animation = fadeIn
            seekBar.visibility = View.VISIBLE
            rl.background = ContextCompat.getDrawable(this, R.drawable.value_selector_gradient)
            txtTitle.setTextColor(ContextCompat.getColor(this, R.color.text_color_white))
            setValueSelector(seekBar, level)
            scrollView.smoothScrollTo(0, rl.top)

        } else {
            val fadeOut = AlphaAnimation(0f, 1f)
            fadeOut.interpolator = DecelerateInterpolator() //add this
            fadeOut.duration = 500
            rl.animation = fadeOut
            txtTitle.setTextColor(ContextCompat.getColor(this, R.color.text_color_grey))
            rl.background = ContextCompat.getDrawable(this, R.drawable.rounded_corner)
            seekBar.visibility = View.GONE

            when (level) {
                ENTRY_LEVEL -> {
                    if (entryValue == 0F) {
                        entryValue = stockPrice
                        setEntryPointTextColor()
                    }
                    txtEntryPoint.formatValue()
                }

                EXIT_LEVEL -> {
                    setExitPointTextColor()
                    txtExitPoint.formatValue()
                }

                STOP_LEVEL -> {
                    setStopPointTextColor()
                    txtStopPoint.formatValue()
                }
            }
        }
    }


    private fun setValueSelector(seekBar: CrystalSeekbar, level: Int) {
        var max = 0f
        var min = 0f
        var selectedValue = 0f
        when (level) {
            ENTRY_LEVEL -> {
                var percentage = (20 * entryValue) / 100
                max = entryValue + percentage
                min = entryValue - percentage
                selectedValue = entryValue
                txtExitPoint.setText("0.00")
                txtStopPoint.setText("0.00")
                txtExitPoint.setTextColor(ContextCompat.getColor(this, R.color.text_color_grey))
                txtStopPoint.setTextColor(ContextCompat.getColor(this, R.color.text_color_grey))
                exitValue = entryValue;
                stopValue = entryValue
            }

            EXIT_LEVEL -> {
                var percentage = (20 * entryValue) / 100
                max = entryValue + percentage
                min = entryValue - percentage
                selectedValue = exitValue
                stopValue = entryValue
                txtStopPoint.setText("0.00")
                txtStopPoint.setTextColor(ContextCompat.getColor(this, R.color.text_color_grey))
            }

            STOP_LEVEL -> {
                var percentage = (20 * entryValue) / 100
                max = entryValue + percentage
                min = entryValue - percentage
                selectedValue = stopValue
                if (Converter.parseStringToFloat(txtExitPoint.value) == 0F)
                    txtExitPoint.setText(Common.formatFloat(exitValue))
                setExitPointTextColor()
            }
        }

        if (selectedValue > min && selectedValue < max) {
            seekBar.minStartValue = selectedValue
        }

        seekBar.maxValue = max
        seekBar.minValue = min
        seekBar.steps = 0.01f
        var changeListener = OnSeekbarChangeListener {
            hideKeyboard()
            when (level) {
                ENTRY_LEVEL -> {
                    entryValue = Common.formatFloat(it.toFloat()).toFloat()
                    exitValue = entryValue
                    stopValue = entryValue
                    txtEntryPoint.setText(Common.formatFloat(entryValue))
                }

                EXIT_LEVEL -> {
                    exitValue = Common.formatFloat(it.toFloat()).toFloat()
                    txtExitPoint.setText(Common.formatFloat(exitValue))
                    txtStopPoint.setText("0.00")
                    setExitPointTextColor()
                }

                STOP_LEVEL -> {
                    stopValue = Common.formatFloat(it.toFloat()).toFloat()
                    txtStopPoint.setText(Common.formatFloat(stopValue))
                    setStopPointTextColor()
                    setExitPointTextColor()
                }
            }
        }



        seekBar.setOnSeekbarChangeListener(changeListener)

        seekBar.apply()
    }

    private fun setEntryPointTextColor() {
        var entryValue = Converter.parseStringToFloat(txtEntryPoint.value)
        var color: Int
        when {
            stockPrice == 0f -> color = ContextCompat.getColor(this@AddTipsActivity, R.color.text_color_grey)
            entryValue == stockPrice -> color = ContextCompat.getColor(this@AddTipsActivity, R.color.text_color_white)
            entryValue < stockPrice -> color = ContextCompat.getColor(this@AddTipsActivity, R.color.text_color_red)
            else -> color = ContextCompat.getColor(this@AddTipsActivity, R.color.green_light)
        }
        txtEntryPoint.setTextColor(color)
        Common.changeDrawableColor(this, txtEntryPoint, color)
    }

    private fun setExitPointTextColor() {
        var entryValue = Converter.parseStringToFloat(txtEntryPoint.value)
        var exitValue = Converter.parseStringToFloat(txtExitPoint.value)
        var color: Int
        when {
            exitValue == 0f -> color = ContextCompat.getColor(this, R.color.text_color_grey)
            exitValue == entryValue -> color = ContextCompat.getColor(this, R.color.text_color_white)
            exitValue < entryValue -> color = ContextCompat.getColor(this, R.color.text_color_red)
            else -> color = ContextCompat.getColor(this, R.color.green_light)
        }
        txtExitPoint.setTextColor(color)
        Common.changeDrawableColor(this, txtExitPoint, color)
    }

    private fun setStopPointTextColor() {
        var entryValue = Converter.parseStringToFloat(txtEntryPoint.value)
        var stopValue = Converter.parseStringToFloat(txtStopPoint.value)
        var color: Int
        when {
            stopValue == 0f -> color = ContextCompat.getColor(this, R.color.text_color_grey)
            stopValue == entryValue -> color = ContextCompat.getColor(this, R.color.text_color_white)
            stopValue < entryValue -> color = ContextCompat.getColor(this, R.color.text_color_red)
            else -> color = ContextCompat.getColor(this, R.color.green_light)
        }
        txtStopPoint.setTextColor(color)
        Common.changeDrawableColor(this, txtStopPoint, color)
    }

    private fun validate(): Boolean {

        entryValue = Converter.parseStringToFloat(txtEntryPoint.value)
        exitValue = Converter.parseStringToFloat(txtExitPoint.value)
        stopValue = Converter.parseStringToFloat(txtStopPoint.value)

        var isValidate = true
        if (entryValue == 0f) {
            isValidate = false
            Toast.makeText(this, "Select Entry price", Toast.LENGTH_LONG).show()
            return isValidate
        }

        if (exitValue == 0f) {
            isValidate = false
            Toast.makeText(this, "Select Exit price", Toast.LENGTH_LONG).show()
            return isValidate
        }

        if (stopValue == 0f) {
            isValidate = false
            Toast.makeText(this, "Select Stop price", Toast.LENGTH_LONG).show()
            return isValidate
        }


        if (entryValue == exitValue) {
            isValidate = false
            Toast.makeText(this, "Entry and Exit prices can not be the same.", Toast.LENGTH_LONG).show()
            return isValidate
        }
        if (exitValue == stopValue) {
            isValidate = false
            Toast.makeText(this, "Exit and Stop prices can not be the same.", Toast.LENGTH_LONG).show()
            return isValidate
        }
        var lowerRangeSP = Common.formatDouble(stockPrice * 0.5)
        var upperRangeSP = Common.formatDouble(stockPrice * 1.5)
        if (!vaidateStockPriceRange(stockPrice, entryValue)) {
            //var msg = getString(R.string.validateTipPiceMessage, lowerRangeSP, upperRangeSP)
            Common.showMessage(this,
                    "Only 50% of the current stock price is allowed. Range for the selected stock is " + lowerRangeSP + " to " + upperRangeSP,
                    getString(R.string.TipRuleTitle));

            isValidate = false;
        }
        if (!vaidateStockPriceRange(stockPrice, exitValue)) {
            //var msg = getString(R.string.validateTipPiceMessage, lowerRangeSP, upperRangeSP)
            Common.showMessage(this,
                    "Only 50% of the current stock price is allowed. Range for the selected stock is " + lowerRangeSP + " to " + upperRangeSP,
                    getString(R.string.TipRuleTitle));
            isValidate = false;
        }
        if (!vaidateStockPriceRange(stockPrice, stopValue)) {
            //var msg = getString(R.string.validateTipPiceMessage, lowerRangeSP, upperRangeSP);
            Common.showMessage(this,
                    "Only 50% of the current stock price is allowed. Range for the selected stock is " + lowerRangeSP + " to " + upperRangeSP,
                    getString(R.string.TipRuleTitle));
            isValidate = false;
        }

        return isValidate
    }

    private fun vaidateStockPriceRange(stockPrice: Float, inputNo: Float): Boolean {
        var lowerRangeSP = stockPrice * .5
        var upperRangeSP = stockPrice * 1.5
        if (inputNo in lowerRangeSP..upperRangeSP) {  // equivalent of 1 <= i && i <= 4
            print(" in range");
            return true
        } else {
            print("not in range");
            return false
        }
    }

    private fun callGetStockDetail() {
        // check network is available or not.


        // check network is available or not.
        if (!Common.isNetworkAvailable(context)) {
            Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                    context.getResources().getString(R.string.JustLetYouKnow))
            return
        }
        val apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_URL_TRADEWYSE, this)
        val call = apiInterface.getStockDetailData(stocks.stockName, Constants.IEX_TOKEN)
        showProgressDialog()
        call.enqueue(object : Callback<Quote> {
            override fun onResponse(call: Call<Quote>, response: Response<Quote>) {
                dismissProgressDialog()
                if (response.body() != null) {
                    var quote = response.body()
                    stocks.stockPrice = quote!!.latestPrice
                    stocks.stockChange = quote.change
                    stocks.high = quote.high
                    stocks.low = quote.low
                    setStockValue()
                }

            }

            override fun onFailure(call: Call<Quote>, t: Throwable) {
                dismissProgressDialog()
                Toast.makeText(this@AddTipsActivity, "Failed,Something went wrong,please try again later..", Toast.LENGTH_SHORT).show()
                Log.d(Constants.ON_FAILURE_TAG, "Dashboard getUserProfile: onFailure");
            }
        })
    }

    private fun addTips() {
        val builder = Builder()
        builder.setType(FORM)

        photoUtility?.let {
            it.getSelectedFilePath()?.let {
                var f = File(it)
                if (f != null && f.exists()) {

                    if (FIleHelper.isFileJpgOrJpegOrPng(FIleHelper.getMimeType(this, photoUtility!!.getSelectedFileUri()!!))) {
                        Log.e("StockImage", f.length().toString() + " length");
                        try {
                            f = Compressor(this).compressToFile(f)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        Log.e("StockImage", f.length().toString() + " length");
                    }


                    val requestFile = RequestBody.create(it.toMediaTypeOrNull(), f)
                    builder.addPart(createFormData("imageDetails", f.name, requestFile))
                }
            }
        }

        var stockSuggestion: String
        if (entryValue < exitValue) {
            stockSuggestion = "Buy"
        } else if (entryValue > exitValue) {
            stockSuggestion = "Sell"
        } else {
            stockSuggestion = "Avoid"
        }

        builder.addFormDataPart("tipComment", edtTipComment.text.toString())
        builder.addFormDataPart("entryPoint", txtEntryPoint.value.toString())
        builder.addFormDataPart("exitPoint", txtExitPoint.value.toString())
        builder.addFormDataPart("stopPoint", txtStopPoint.value.toString())
        builder.addFormDataPart("stockName", stocks.stockName.trim())
        builder.addFormDataPart("status", "Approved")
        builder.addFormDataPart("timeFrame", "15 days")
        builder.addFormDataPart("stockSuggestion", stockSuggestion)
        builder.addFormDataPart("createTipPrice", stockPrice.toString())

        // check network is available or not.
        if (!Common.isNetworkAvailable(context)) {
            Common.showOfflineMemeDialog(context, context.getResources().getString(R.string.memeMsg),
                    context.getResources().getString(R.string.JustLetYouKnow))
            return
        }
        val finalRequestBody = builder.build()
        val apiInterface = APIClient.getTradeAPIClient(APIClient.BASE_SERVER_URL, this@AddTipsActivity)
        showProgressDialog()
        val call = apiInterface.addTip(finalRequestBody)
        call.enqueue(object : Callback<Tips> {
            override fun onResponse(call: Call<Tips>, response: Response<Tips>) {
                dismissProgressDialog()

                if (response.body() != null) {
                    Toast.makeText(this@AddTipsActivity, "Stock tip added successfully", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    var tips = response.body();
                    tips!!.companyName = stocks.companyName
                    tips!!.stockPrice = stocks.stockPrice
                    var user = User();
                    user.userName = tradWyseSession.userName
                    user.id = tradWyseSession.userId
                    user.image = tradWyseSession.userImage
                    user.setfName(tradWyseSession.firstName)
                    user.setlName(tradWyseSession.lastName)
                    tips?.let {
                        it.appUser = user
                    }
                    resultIntent.putExtra("tips", tips)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()

                } else {
                    Toast.makeText(this@AddTipsActivity, "Something went wrong,please try again later..", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Tips>, t: Throwable) {
                dismissProgressDialog()
                Toast.makeText(this@AddTipsActivity, "Failed,Something went wrong,please try again later..", Toast.LENGTH_SHORT).show()
                Log.d(Constants.ON_FAILURE_TAG, "AddTips addTip: onFailure");
            }
        })
    }


    companion object {

        fun start(context: Context, stocks: Stocks, requestCode: Int) {
            val starter = Intent(context, AddTipsActivity::class.java)
            starter.putExtra("stocks", stocks)
            (context as AppCompatActivity).startActivityForResult(starter, requestCode)
        }
    }

    private fun takePhoto() {
        this.photoUtility = PhotoUtility.Builder(this)
                .setImageView(tipPhoto)
                .setOutPutFile(FIleHelper.createNewFile(this, FIleHelper.createFileName("profile")))
                .build()
        photoUtility!!.setPhotoOptionSelectListener(object : PhotoOptionSelectListener {
            override fun requestPermissions(permissions: Array<out String>, requestCode: Int) {
                ActivityCompat.requestPermissions(this@AddTipsActivity, permissions, requestCode)
            }

            override fun startActivityForResult(intent: Intent?, requestCode: Int) {
                ActivityCompat.startActivityForResult(this@AddTipsActivity, intent!!, requestCode, null)
            }

        })
        photoUtility!!.requestPermissionsCameraAndStorage()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        photoUtility!!.setResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        photoUtility!!.setPermissionResult(requestCode, grantResults)
    }

}
