package com.example.stockscreener.stock

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockscreener.viewmodel.StockViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.stockscreener.spacing_250
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.components.XAxis
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


@Composable
fun StockChartScreen(viewModel: StockViewModel) {
    val stockPrices by viewModel.stockPricesChart.collectAsState()

    StockLineChart(stockPrices)
}

@Composable
fun StockLineChart(stockPrices: List<Pair<String, Float>>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(spacing_250),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false
                xAxis.setDrawGridLines(false)
                setBackgroundColor(Color.White.toArgb())
            }
        },
        update = { chart ->
            val entries = stockPrices.mapIndexed { index, (_, price) ->
                Entry(index.toFloat(), price)
            }

            val dataSet = LineDataSet(entries, "Stock Prices").apply {
                color = Color.Blue.toArgb()
                valueTextColor = Color.Black.toArgb()
                setDrawCircles(true)
                setDrawValues(false)
                lineWidth = 2f
                setCircleColor(Color.Red.toArgb())
            }

            val lineData = LineData(dataSet)
            chart.data = lineData
            chart.invalidate()
        }
    )
}
