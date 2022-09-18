package ru.myitschool.lab23

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.myitschool.lab23.databinding.ActivityMainBinding
import ru.myitschool.lab23.ui.main.MainViewModel
import java.util.*
import java.util.stream.DoubleStream
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.streams.toList


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.content.getRandomNums.setOnClickListener {
            val mean = binding.content.shapeParam.text.toString().toInt()
            val variance = binding.content.rateParam.text.toString().toDouble()

            viewModel.setGeneratedListData(DoubleStream.generate { generateErlang(mean, variance) }
                .limit(binding.content.sizeParam.text.toString().toLong()).toList())

            val openGeneratedListActivity = Intent(this, GeneratedListActivity::class.java)
            startActivity(openGeneratedListActivity)
        }

        /*binding.content.getRandomNum.setOnClickListener {
            val mean = binding.content.meanVal.text.toString().toDouble()
            val variance = binding.content.varianceValue.text.toString().toDouble()
            val res = exp(Random().nextGaussian() * sqrt(variance) + mean)
            binding.content.randomNumberResult.setText(res.toString())
            binding.content.getRandomNum.text = res.toString()
            binding.content.varianceValue.setTextColor(Color.GREEN)

        }*/

        /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }*/

        // binding.message.text = binding.container.javaClass.canonicalName
        // VISIBILITY
        // LAYOUT change according to resources UK lang

        /*val ch : CheckBox? = null; val tg : ToggleButton? = null;
        val bt : CompoundButton? = ch;
        val v : View? = null;

        *//*open class MyView : TextView() {

        }*//*

        Thread {
            Thread.sleep(8_000)
            runOnUiThread {
                Log.d("Tests", "invalidated")
                binding.main.maxWidth = 100
                // binding.main.invalidate()
                // binding.message.invalidate()
            }
        }.start()*/
    }

    private fun generateNumber(m : Double, v : Double) = exp(Random().nextGaussian() * sqrt(v) + m)

    private fun generateErlang(k : Int, l : Double) = - 1.0 / l *
                DoubleStream.generate { ln( Random().nextDouble() ) }
            .limit(k.toLong() - 1).sum()
}