package com.nothinglondon.sdkdemo.tesla

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.round

data class StockQuote(
    val symbol: String,
    val price: Double,
    val previousClose: Double,
) {
    val change: Double get() = price - previousClose
    val changePercent: Double get() = if (previousClose == 0.0) 0.0 else (change / previousClose) * 100.0
    val isUp: Boolean get() = change >= 0
}

enum class StockAsset(val displaySymbol: String, val yahooSymbol: String) {
    TESLA("TSLA", "TSLA"),
    SPACEX("SPCX", "SPCX"),
    NVIDIA("NVDA", "NVDA"),
    BITCOIN("BTC", "BTC-USD"),
    ;

    fun next(): StockAsset = entries[(ordinal + 1) % entries.size]
}

object StockFetcher {

    private const val YAHOO_CHART_TEMPLATE =
        "https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=1d&range=1d"

    suspend fun fetch(asset: StockAsset): StockQuote = withContext(Dispatchers.IO) {
        val url = YAHOO_CHART_TEMPLATE.format(asset.yahooSymbol)
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("User-Agent", "TeslaGlyphStock/1.0")
        }

        try {
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val meta = JSONObject(body)
                .getJSONObject("chart")
                .getJSONArray("result")
                .getJSONObject(0)
                .getJSONObject("meta")

            val price = meta.getDouble("regularMarketPrice")
            val previousClose = when {
                meta.has("chartPreviousClose") -> meta.getDouble("chartPreviousClose")
                meta.has("previousClose") -> meta.getDouble("previousClose")
                else -> price
            }

            StockQuote(
                symbol = asset.displaySymbol,
                price = price,
                previousClose = previousClose,
            )
        } finally {
            connection.disconnect()
        }
    }

    fun formatPrice(price: Double): String {
        return round(price).toInt().toString()
    }

    fun formatChangePercent(changePercent: Double): String {
        val rounded = round(changePercent).toInt()
        val sign = if (rounded >= 0) "+" else ""
        return "$sign$rounded%"
    }
}