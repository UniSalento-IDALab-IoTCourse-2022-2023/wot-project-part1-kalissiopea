package com.polar.androidblesdk

import WsBack
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class PresActivity : AppCompatActivity() {
    private lateinit var countDownTimer: CountDownTimer
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pres)

        val aggiungi = findViewById<Button>(R.id.btnadd)

        val urls = "ws://192.168.0.105:8000"
        val myConn = Backend();


        //button register
        aggiungi.setOnClickListener {
            val giornoTxt = findViewById<EditText>(R.id.giorno)
            val oraTxt = findViewById<EditText>(R.id.ora)
            val valoreMaxTxt = findViewById<EditText>(R.id.max)
            val valoreMinTxt = findViewById<EditText>(R.id.min)

            val giorno = giornoTxt.text.toString()
            val ora = oraTxt.text.toString()
            val valoreMax = valoreMaxTxt.text.toString()
            val valoreMin = valoreMinTxt.text.toString()
            val email = intent.getStringExtra("parEmail")

            val ws = WsBack(urls, applicationContext, email.toString());

            //aggiunta nel cloud database
            val url = "http://192.168.0.105:3000/aggiungiPres"
            val req =
                "{ \"valuemax\":\"$valoreMax\", \"valuemin\":\"$valoreMin\", \"day\":\"$giorno\", \"hour\":\"$ora\", \"emailAddress\":\"$email\" }"
            Thread { myConn.post_request(url, req) }.start()

            ws.start()
            val intent = Intent(this, MainActivity::class.java)
            countDownTimer = object : CountDownTimer(15000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Avvia il conteggio alla rovescia

                    Log.d("TIMER", "Il timer è partito")
                }

                override fun onFinish() {
                    // Termina il conteggio alla rovescia e fa camminare l'immagine

                    Log.d("TIMER", "Il timer ha finito")
                    ws.sendMessage("Non misuri la pressione dal $giorno alle $ora")
                    ws.disconnect()
                }
            }

            // Avvia il contatore
            countDownTimer.start()
            intent.putExtra("parEmail", email)
            startActivity(intent)
        }
    }
}


