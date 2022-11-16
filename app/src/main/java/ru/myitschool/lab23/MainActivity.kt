package ru.myitschool.lab23



import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import ru.myitschool.lab23.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ExpensesViewModel

    private var date = ""
    private var type = "Income"
    private val bud = 0.0
    private val cat: List<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ExpensesViewModel::class.java)
        //viewModel.setBudget(0.0);

        binding.addFab.setOnClickListener { view ->
            showAddExpenseDialog()
        }
    }


    private fun showAddExpenseDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val layoutInflater = layoutInflater
        val dialogView: View = layoutInflater.inflate(R.layout.add_expense_dialog, null)
        dialogBuilder.setView(dialogView)
        val amount = dialogView.findViewById<EditText>(R.id.expense_amount_edit_text)
        val type_spinner = dialogView.findViewById<Spinner>(R.id.type_spinner)
        val chip_group_categories = dialogView.findViewById<ChipGroup>(R.id.chip_group_categories)
        val choose_date = dialogView.findViewById<TextView>(R.id.choose_date)
        val add = dialogView.findViewById<Button>(R.id.add_button)
        val b: AlertDialog = dialogBuilder.create()
        b.show()
        for (i in cat.indices) {
            val inflater = LayoutInflater.from(this)
            val newChip =
                inflater.inflate(R.layout.layout_chip_entry, chip_group_categories, false) as Chip
            newChip.setText(cat.get(i))
            newChip.isCloseIconVisible = false
            chip_group_categories.addView(newChip)
        }
        val types = arrayOf("Income", "Expenses")
        val arrayAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(applicationContext, android.R.layout.simple_spinner_item, types)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        type_spinner.adapter = arrayAdapter
        add.setOnClickListener { view: View? ->
            val a = amount.text.toString()
            if (TextUtils.isEmpty(a)) {
                amount.error = "Enter amount of money!"
            } else {
                val expense = Expense()
                expense.amount = a.toDouble()
                /*for (i in 0 until chip_group_categories.childCount) {
                    val chip = chip_group_categories.getChildAt(i) as Chip
                    if (chip.isChecked || chip.isSelected) {
                        expense.category = chip.text.toString()
                    }
                }*/
                expense.category = "Food"
                expense.date = "11.11.2022"//choose_date.text.toString()
                val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.UK)
                val date: String = sdf.format(System.currentTimeMillis())
                expense.date = date
                expense.type = type
                // id
                val sdf1 = SimpleDateFormat("yyyyMMddHHmmss", Locale.UK)
                val id = // UUID.randomUUID().toString() +
                    sdf1.format(System.currentTimeMillis())//  + expense.category
                viewModel.addExpense(id, expense)
                if (type.equals("Income"))
                    viewModel.setBudget(bud + a.toDouble());
                else
                    viewModel.setBudget(bud - a.toDouble());


                /*databaseReference.child(id.uppercase()).setValue(expense)
                if (type.equals("Income")) FirebaseDatabase.getInstance()
                    .getReference("user/" + user.getUid()).child("budget")
                    .setValue(bud + a.toDouble()) else FirebaseDatabase.getInstance()
                    .getReference("user/" + user.getUid()).child("budget")
                    .setValue(bud - a.toDouble())*/
                b.dismiss()
            }
        }
        choose_date.setOnClickListener { view: View? ->
            val c: Calendar = Calendar.getInstance()
            val mYear: Int = c.get(Calendar.YEAR)
            val mMonth: Int = c.get(Calendar.MONTH)
            val mDay: Int = c.get(Calendar.DAY_OF_MONTH)
            val dataPickerDialog =
                DatePickerDialog(
                    this@MainActivity,
                    { datePicker, i, i1, i2 ->
                        var i1 = i1
                        i1++
                        var x = ""
                        if (i1 < 10) x = "0"
                        date = "$i2.$i1.$i"
                        choose_date.text = "$i2.$x$i1.$i"
                    }, mYear, mMonth, mDay
                )
            dataPickerDialog.show()
        }
        type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                type = types[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

}