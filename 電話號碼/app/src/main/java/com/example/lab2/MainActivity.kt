package com.example.lab2



// 取得資源類別檔中的介面元件
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Declare your views here
    private lateinit var txtShow: TextView
    private lateinit var btnZero: Button
    private lateinit var btnOne: Button
    private lateinit var btnTwo: Button
    private lateinit var btnThree: Button
    private lateinit var btnFour: Button
    private lateinit var btnFive: Button
    private lateinit var btnSix: Button
    private lateinit var btnSeven: Button
    private lateinit var btnEight: Button
    private lateinit var btnNine: Button
    private lateinit var btnClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main) // Make sure you have a layout file named activity_main.xml

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        txtShow = findViewById(R.id.txtShow)
        btnZero = findViewById(R.id.btnZero)
        btnOne = findViewById(R.id.btnOne)
        btnTwo = findViewById(R.id.btnTwo)
        btnThree = findViewById(R.id.btnThree)
        btnFour = findViewById(R.id.btnFour)
        btnFive = findViewById(R.id.btnFive)
        btnSix = findViewById(R.id.btnSix)
        btnSeven = findViewById(R.id.btnSeven)
        btnEight = findViewById(R.id.btnEight)
        btnNine = findViewById(R.id.btnNine)
        btnClear = findViewById(R.id.btnClear)

        // Set onClick listeners
        btnZero.setOnClickListener(myListner)
        btnOne.setOnClickListener(myListner)
        btnTwo.setOnClickListener(myListner)
        btnThree.setOnClickListener(myListner)
        btnFour.setOnClickListener(myListner)
        btnFive.setOnClickListener(myListner)
        btnSix.setOnClickListener(myListner)
        btnSeven.setOnClickListener(myListner)
        btnEight.setOnClickListener(myListner)
        btnNine.setOnClickListener(myListner)
        btnClear.setOnClickListener(myListner)
    }

    private val myListner = View.OnClickListener { v ->
        val s = txtShow.text.toString()
        when (v.id) {
            R.id.btnZero -> txtShow.text = s + "0"
            R.id.btnOne -> txtShow.text = s + "1"
            R.id.btnTwo -> txtShow.text = s + "2"
            R.id.btnThree -> txtShow.text = s + "3"
            R.id.btnFour -> txtShow.text = s + "4"
            R.id.btnFive -> txtShow.text = s + "5"
            R.id.btnSix -> txtShow.text = s + "6"
            R.id.btnSeven -> txtShow.text = s + "7"
            R.id.btnEight -> txtShow.text = s + "8"
            R.id.btnNine -> txtShow.text = s + "9"
            R.id.btnClear -> txtShow.text = "電話號碼：" // Consider using string resources
        }
    }
}

