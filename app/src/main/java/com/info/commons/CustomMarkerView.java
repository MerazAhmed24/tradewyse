package com.info.commons;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.info.model.EntryData;
import com.info.tradewyse.GraphActivity;
import com.info.tradewyse.R;

public class CustomMarkerView extends MarkerView implements IMarker {

    private TextView tvContent,tvLabelContent,tvTime;
    private MPPointF mOffset;
    String stockCurrentPrice;
    int apiSource;
    String[] datesArray;

    public CustomMarkerView(Context context, int layoutResource,int apiSource) {
        super(context, layoutResource);
        tvLabelContent=findViewById(R.id.tvLabelContent);
        tvContent =findViewById(R.id.tvContent);
        tvTime=findViewById(R.id.tvTime);
        this.apiSource=apiSource;
    }

    public CustomMarkerView(Context context, int layoutResource,int apiSource,String[] datesArray,String stockCurrentPrice) {
        this(context, layoutResource,apiSource);
        this.datesArray=datesArray;
        this.stockCurrentPrice = stockCurrentPrice;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText("$" + Common.formatFloat(e.getY()));
        int index=(int)e.getX();
        if(e.getData()!=null && e.getData() instanceof EntryData){
            EntryData entryData= (EntryData) e.getData();
            tvLabelContent.setVisibility(VISIBLE);
            if(e.getY()==Float.valueOf(stockCurrentPrice).floatValue()){
                tvLabelContent.setText("Current Price");
                tvLabelContent.setTextColor(getResources().getColor(R.color.yelloColor));
            }else{
                tvLabelContent.setText(entryData.getLineName());
                tvLabelContent.setTextColor(entryData.getLineColor());
            }
        }
       if(apiSource==GraphActivity.API_SOURCE_GRAPH_PREDICTION){

           tvTime.setText( DateTimeHelper.parseDateFloat(DateTimeHelper.parseDate(datesArray[index]).getTime()));
       }else  if(apiSource== GraphActivity.API_SOURCE_GRAPH){

           tvTime.setText(datesArray[index]);
           //tvTime.setText(DateTimeHelper.parseTimeFloat(e.getX()));
        }else{
           tvTime.setText(datesArray[index]);
            //tvTime.setText(DateTimeHelper.parseDateFloat(e.getX()));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        Log.e("markery",getY()+"");
        if(mOffset == null) {
            float markerX=getX()-(getWidth()/2);
            mOffset = new MPPointF(markerX, -getHeight());

        }
        return mOffset;
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {

        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        int saveId = canvas.save();
        // translate to the correct position and draw
        canvas.translate(posX + offset.x, posY + offset.y);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }

}
