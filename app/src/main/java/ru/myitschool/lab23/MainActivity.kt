package ru.myitschool.lab23

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun buttonClick(v: View) {
        val sideA: EditText = findViewById(R.id.side_a)
        val sideB: EditText = findViewById(R.id.side_b)
        val sideC: EditText = findViewById(R.id.side_c)
        val text: TextView = findViewById(R.id.solution)
        var flag = false
        val spinner: Spinner = findViewById(R.id.spinner)
        val selectedValue: String = spinner.selectedItem.toString();

        if (!flag) {
            text.text = when (selectedValue) {
                "Сумма длин ребер" -> "${
                    sideA.text.toString().toDouble() * 4 + sideB.text.toString()
                        .toDouble() * 4 + sideC.text.toString().toDouble() * 4
                }"
                "Площадь поверхности" -> "${
                    2 * ((sideA.text.toString().toDouble() * sideB.text.toString()
                        .toDouble()) + (sideB.text.toString().toDouble() * sideC.text.toString()
                        .toDouble()) + (sideA.text.toString().toDouble() * sideC.text.toString()
                        .toDouble()))
                }"
                "Длина диагонали" -> "${
                    Math.sqrt(
                        sideA.text.toString().toDouble() * sideA.text.toString().toDouble() +
                                sideB.text.toString().toDouble() * sideB.text.toString()
                            .toDouble() +
                                sideC.text.toString().toDouble() * sideC.text.toString().toDouble()
                    )
                }"
                else -> "${
                    sideA.text.toString().toDouble() * sideB.text.toString()
                        .toDouble() * sideC.text.toString().toDouble()
                }"
            }

        }

        text.setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val clip = ClipData.newPlainText(
                "some label",
                text.text.toString()
            )
            clipboard.setPrimaryClip(clip)
        }
    }
}

