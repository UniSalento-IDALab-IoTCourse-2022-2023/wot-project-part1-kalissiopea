package com.polar.androidblesdk

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.Gson
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future


class PpgActivity : AppCompatActivity() {
    private lateinit var chart1: LineChart
    private lateinit var chart2: LineChart
    private lateinit var chart3: LineChart

    data class orologios(val _id: String, val heartRate: String, val ecg: String, val acc: String, val gyro: String, val magnet: String, val ppg1: String, val ppg2: String, val ppg3: String, val ppi: String, val email_address: String, val date: String, val hour: String)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        val email = intent.getStringExtra("email")
        val data = intent.getStringExtra("datae")

        val myConn = Backend();
        val url = "http://192.168.0.105:3000/vsGrafico?email_address=$email&date=$data"

        val executor = Executors.newFixedThreadPool(1)
        val future: Future<String> = executor.submit(Callable { myConn.getRequest(url) })
        val responseBody = future.get()
        Log.d("RESPONSE", responseBody)

        val arrVs = getArray(responseBody)
        Log.d("ARRAYSTRING", arrVs)

        val gson = Gson()
        val array = gson.fromJson(arrVs, Array<orologios>::class.java)
        Log.d("ARRAY", array.toString())


        chart1 = findViewById(R.id.chart1)

        chart2 = findViewById(R.id.chart2)

        chart3 = findViewById(R.id.chart3)

        array.sortBy { stringToFloat(it.hour) }
        val entries1 = array.map {
            Entry(stringToFloat(it.hour), it.ppg1.toFloat())
        }

        array.sortBy { stringToFloat(it.hour) }
        val entries2 = array.map {
            Entry(stringToFloat(it.hour), it.ppg2.toFloat())
        }

        array.sortBy { stringToFloat(it.hour) }
        val entries3 = array.map {
            Entry(stringToFloat(it.hour), it.ppg3.toFloat())
        }

//        val entries1 = arrayListOf(
//            Entry(0f, 1f),
//            Entry(1f, 4f),
//            Entry(2f, 8f),
//            Entry(3f, 6f),
//            Entry(4f, 2f),
//            Entry(5f, 9f),
//            Entry(3f, 2f)
//        )
//
//        val entries2 = arrayListOf(
//            Entry(0f, 1f),
//            Entry(1f, 4f),
//            Entry(2f, 8f),
//            Entry(3f, 6f),
//            Entry(4f, 2f),
//            Entry(5f, 9f),
//            Entry(3f, 2f)
//        )
//
//        val entries3 = arrayListOf(
//            Entry(0f, 1f),
//            Entry(1f, 4f),
//            Entry(2f, 8f),
//            Entry(3f, 6f),
//            Entry(4f, 2f),
//            Entry(5f, 9f),
//            Entry(3f, 2f)
//        )

        val dataset1 = LineDataSet(entries1, "ppg0")
        dataset1.color = Color.RED
        dataset1.valueTextColor = Color.BLACK

        val dataset2 = LineDataSet(entries2, "ppg1")
        dataset2.color = Color.RED
        dataset2.valueTextColor = Color.BLACK

        val dataset3 = LineDataSet(entries3, "ppg2")
        dataset3.color = Color.RED
        dataset3.valueTextColor = Color.BLACK

        val lineData1 = LineData(dataset1)
        chart1.data = lineData1

        val lineData2 = LineData(dataset2)
        chart2.data = lineData2

        val lineData3 = LineData(dataset3)
        chart3.data = lineData3

        chart1.invalidate()

        chart2.invalidate()

        chart3.invalidate()
    }

    fun getArray(users: String): String {
        val startIndex = users.indexOf('[')
        val endIndex = users.indexOf(']')
        val arr = users.substring(startIndex, endIndex + 1)
        return arr
    }

    fun stringToFloat(timeString: String): Float {
        val timeArray = timeString.split(":")
        val hours = timeArray[0].toFloat()
        val minutes = timeArray[1].toFloat() / 100
        val seconds = timeArray[2].toFloat() / 10000
        val totalSeconds = hours + minutes + seconds
        return totalSeconds
    }
}