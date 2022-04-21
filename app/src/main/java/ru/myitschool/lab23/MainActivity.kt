package ru.myitschool.lab23

import android.R.attr.label
import android.R.attr.layout_height
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import ru.myitschool.lab23.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mInputHelper: TextInputHelper

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*binding.container.root.addView(Button(this).apply {
            setBackgroundColor(getColor(R.color.white))
            layoutParams = ViewGroup.LayoutParams(200, 200)
                //ViewGroup.LayoutParams.MATCH_PARENT,
                //ViewGroup.LayoutParams.WRAP_CONTENT)
            text = "Hello"
        })*/



        for (i in 1..5) {
            binding.container.outerLayout.addView(
                LinearLayout(this).apply {
                    //setBackgroundColor(getColor(R.color.white))
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    orientation = LinearLayout.HORIZONTAL
                    addView(
                        TextView(context).apply {
                            id = 100 * i
                            //setBackgroundColor(getColor(R.color.white))
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            text = "$i LongTextHere"
                            setTextColor(Color.GREEN)

                        }
                    )
                    addView(
                        EditText(context).apply {
                            id = i
                            setBackgroundColor(getColor(R.color.white))
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            hint = "$i T$id"
                            inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                        }
                    )
                }

            )
        }

        findViewById<TextView>(100).setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("some label", "textt")
            clipboard.setPrimaryClip(clip)
        }

        // findViewById<TextView>(1).setHint("New")

        /*val km = binding.container.km
        val m = binding.container.m
        val dm = binding.container.dm
        val sm = binding.container.sm
        val mm = binding.container.mm
        val inches = binding.container.inches
*/
        /*val str: String = intent.extras?.get("param") as String
        km.hint = str
        m.hint = "$str!"*/

        mInputHelper = TextInputHelper()
        // One or more EditTexts can be added, and TextView can be added, of course.
        //mInputHelper.addViews(km, m, dm, sm, mm, inches)

        /*km.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                // action(editable)
            }
        })*/


        /*ins.setOnClickListener {
            var km = km.getText().toString().toDouble()
            var m = m.getText().toString().toDouble()
            var dm = dm.getText().toString().toDouble()
            var sm = sm.getText().toString().toDouble()
            var mm = mm.getText().toString().toDouble()
            if (km * 1000 == m && m * 10 == dm && dm * 10 == sm && sm * 10 == mm) {
                rez.text = resources.getText(R.string.good)
                rez.setTextColor(Color.BLUE)
                answer.setImageResource(R.drawable.cool)
            } else {
                rez.text = resources.getText(R.string.bad)
                rez.setTextColor(Color.RED)
                answer.setImageResource(R.drawable.bad)
            }
            answer.setOnTouchListener({ view, motionEvent ->
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (answer.alpha > 0.1) answer.alpha -= 0.1f else answer.alpha = 1f
                }
                true
            })
        }*/


    }
}

class TextInputHelper : TextWatcher {
    private var mViewSet: ArrayList<TextView>? = null
    private val converterArray = doubleArrayOf(1000.0, 100.0, 10.0, 1.0, 0.1, 39.3700787)
    fun addViews(vararg views: TextView) {
        if (mViewSet == null) {
            mViewSet = ArrayList<TextView>(views.size - 1)
        }
        for (view in views) {
            view.addTextChangedListener(this)
            mViewSet?.add(view)
        }
        afterTextChanged(null)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //TODO("Not yet implemented")
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //TODO("Not yet implemented")
    }

    override fun afterTextChanged(p0: Editable?) {
        //TODO("Not yet implemented")
    }
}