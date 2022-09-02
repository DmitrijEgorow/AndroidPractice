package ru.myitschool.lab23

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.myitschool.lab23.databinding.ActivityMainBinding
import java.util.*
import kotlin.math.exp
import kotlin.math.sqrt



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.content.getRandomNum.setOnClickListener {
            val mean = binding.content.meanVal.text.toString().toDouble()
            val variance = binding.content.varianceValue.text.toString().toDouble()
            val res = exp(Random().nextGaussian() * sqrt(variance) + mean)
            binding.content.randomNumberResult.setText(res.toString())
            binding.content.getRandomNum.text = res.toString()
            binding.content.varianceValue.setTextColor(Color.GREEN)
        }
    }
}