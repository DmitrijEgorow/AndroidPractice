package ru.myitschool.lab23

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginEnd
import ru.myitschool.lab23.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mInputHelper: TextInputHelper

    private var lower = 0
    private var upper = 0

    private val editTextTags = arrayOf(
        "et_inch",
        "et_yard",
        "et_foot",
        "et_mile",
        "et_yottametre",
        "et_zettametre",
        "et_exametre",
        "et_petametre",
        "et_terametre",
        "et_gigametre",
        "et_megametre",
        "et_kilometre",
        "et_hectometre",
        "et_decametre",
        "et_metre",
        "et_decimetre",
        "et_centimetre",
        "et_millimetre",
        "et_micrometre",
        "et_nanometre",
        "et_picometre",
        "et_femtometre",
        "et_attometre",
        "et_zeptometre",
        "et_yoctometre"
    )
    private val editTextView = arrayOf<EditText?>(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )

    private lateinit var textViewContents: Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textViewContents = resources.getStringArray(R.array.text_view_captions)
        Log.d("Tests", textViewContents.toList().map { v -> "\"$v\"" }.toString())

        lower =
            if (intent.extras?.get("lower") != null) intent.extras?.get("lower") as Int else 0
        upper =
            if (intent.extras?.get("upper") != null) intent.extras?.get("upper") as Int else
                textViewContents.size - 1

        mInputHelper = TextInputHelper(context = this, lower, upper)


        for (i in lower..upper) {
            val mEditText = EditText(this).apply {
                id = 300 * i + 1
                tag = "${editTextTags[i]}"
                // setBackgroundColor(getColor(R.color.white))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                hint = "${mInputHelper.converterArray[i]} ${textViewContents[i]}"
                inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
                textSize = 12F
                addTextChangedListener(mInputHelper)
            }
            editTextView[i] = mEditText
            binding.container.outerLayout.addView(
                LinearLayout(this).apply {
                    //setBackgroundColor(getColor(R.color.white))
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(32, 0, 32, 0)
                    addView(
                        TextView(context).apply {
                            id = 100 * i + 1
                            //setBackgroundColor(getColor(R.color.white))
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            setPadding(0, 0, 32, 0)
                            text = textViewContents[i]
                            setTextColor(Color.GREEN)
                            setOnClickListener {
                                val clipboard: ClipboardManager =
                                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                                val clip = ClipData.newPlainText(
                                    "some label",
                                    editTextView[i]?.text.toString()
                                )
                                clipboard.setPrimaryClip(clip)
                            }
                            textSize = 12F
                        }
                    )
                    addView(
                        mEditText
                    )
                }

            )
        }

        mInputHelper.addViewTags(editTextView)

        /*findViewById<TextView>(100).setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("some label", "textt")
            clipboard.setPrimaryClip(clip)
        }*/

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

    // https://freakycoder.com/android-notes-66-how-to-use-textwatcher-for-more-than-one-edittext-e190b7ae1070
    class TextInputHelper(context: Activity, private var lower: Int, private var upper: Int) :
        TextWatcher {
        private var mViewSet: ArrayList<TextView>? = null
        internal val converterArray = doubleArrayOf(
            39.37007874015748031,
            1.093613298337707787,
            3.280839895013123360,
            0.000621371192237330,
            1e-10,
            1e-9,
            1e-8,
            1e-7,
            1e-6,
            1e-5,
            1e-4,
            1e-3,
            1e-2,
            1e-1,
            1.0,
            10.0,
            100.0,
            1000.0,
            10000.0,
            100000.0,
            1000000.0,
            10000000.0,
            100000000.0,
            1000000000.0,
            10000000000.0
        )
        private var context: Activity? = context

        private var tagsArray = arrayOf<EditText?>()

        fun addViews(vararg views: TextView) {
            if (mViewSet == null) {
                mViewSet = ArrayList<TextView>(views.size - 1)
            }
            for (view in views) {
                view.addTextChangedListener(this)
                mViewSet?.add(view)
            }
            //afterTextChanged(null)
        }

        fun addViewTags(arr: Array<EditText?>) {
            tagsArray = arr.clone()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //TODO("Not yet implemented")
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //TODO("Not yet implemented")
        }

        override fun afterTextChanged(editable: Editable?) {
            //TODO("Not yet implemented")

            if (editable != null && !editable.toString().equals("")) {
                // Checking editable.hashCode() to understand which edittext is using right now
                // Log.d("Tests", editable.toString())
                for (i in lower..upper) {

                    val v = tagsArray[i]// context?.findViewById<TextView>(300 * i + 1)
                    if (v != null) {
                        val txt: String = v.text.toString()
                        // Log.d("Tests", "${editable.toString()}@${txt}@")
                        // val r = txt.equals(editable.toString())
                        if (txt.hashCode() == editable.toString().hashCode()) {
                            val value: BigDecimal = try {
                                BigDecimal(editable.toString())
                                    .divide(BigDecimal(converterArray[i]),
                                    8, RoundingMode.HALF_UP)
                                    .add(BigDecimal(1e-8))
                                // editable.toString().toDouble() / converterArray[i]
                            } catch (e: NumberFormatException) {
                                BigDecimal.ZERO
                            }
                            Log.d("Tests", "$value")
                            for (j in lower..upper) {
                                if (j != i) {
                                    val v1 = tagsArray[j] // context?.findViewById<TextView>(j)
                                    if (v1 != null) {
                                        v1.removeTextChangedListener(this)
                                        // v1.setText("${value * converterArray[j]}")
                                        v1.setText(
                                            "${
                                                value
                                                    .multiply(BigDecimal(converterArray[j]))
                                            }"
                                        )
                                        v1.addTextChangedListener(this)
                                    }
                                }
                            }
                            break
                        }
                    }
                }

                /*if (editText.editText!!.text.hashCode() === editable.hashCode()) {
                    // This is just an example, your magic will be here!
                    val value = editable.toString()
                    editText.editText!!.removeTextChangedListener(this)
                    editText.editText!!.setText(value)
                    editText.editText!!.addTextChangedListener(this)
                }*/
            }/* else if (editText2.editText!!.text.hashCode() === editable!!.hashCode()) {
            // This is just an example, your magic will be here!
            val value = editable!!.toString()
            *//*editText2.editText!!.removeTextChangedListener(this)
            editText2.editText!!.setText(value)
            editText2.editText!!.addTextChangedListener(this)*//*
        }*/
        }
    }
}

