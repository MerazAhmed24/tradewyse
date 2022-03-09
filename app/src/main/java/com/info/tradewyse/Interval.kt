package com.info.tradewyse

import android.util.Log
import com.info.model.GraphMaxMin
import kotlin.math.ceil

object Interval {

    @JvmStatic
    fun getInterval(maxPrice: Double, minPrice: Double): GraphMaxMin {
        var graphMaxMin = GraphMaxMin()
        var axisMinimum = minPrice
        var axisMaximum = maxPrice
        var axisDifference = axisMaximum - axisMinimum
        var axisLabelCount = 6

        if ((maxPrice - minPrice) > 1) {
            if (axisDifference >= 6) {
                while (ceil(axisDifference / axisLabelCount) != (axisDifference / axisLabelCount) && axisLabelCount > 4) {
                    axisLabelCount -= 1
                }
            } else {

                var segmentValues = arrayOf(0, 0.5, 1)

                for (idx in (2..7).reversed()) {
                    Log.e("idx", "" + idx)
                    var interval: Double = axisDifference / idx
                    Log.e("interval", "interval " + interval + " " + interval.toInt())
                    if (segmentValues.contains(interval - interval.toInt())) {
                        axisLabelCount = idx
                        break
                    }
                }

                if (axisMinimum < minPrice - 0.5) {
                    axisMinimum += 0.5
                    axisLabelCount -= 1
                }

                if (axisMaximum > maxPrice + 0.5) {
                    axisMaximum -= 0.5
                    axisLabelCount -= 1
                }
            }

        } else {

            var difference = ((maxPrice - minPrice) * 100) / 200
            axisMinimum = minPrice - difference
            axisMaximum = maxPrice + difference
            axisLabelCount = 3
        }
        graphMaxMin.axisLabelCount = axisLabelCount
        graphMaxMin.axisMaximum=axisMaximum
        graphMaxMin.axisMinimum=axisMinimum
        return graphMaxMin;
    }
}