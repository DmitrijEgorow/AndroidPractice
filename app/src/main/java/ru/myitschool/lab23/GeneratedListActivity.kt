package ru.myitschool.lab23

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.myitschool.lab23.ui.main.MainViewModel
import java.util.*
import java.util.stream.DoubleStream
import kotlin.math.exp
import kotlin.math.sqrt
import kotlin.streams.toList

class GeneratedListActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generated_list)

        // viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        //viewModel.setGeneratedListData(DoubleStream.generate { generateNumber() }
        //    .limit(5_000).toList())


        /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, GeneratedListFragment.newInstance())
                .commitNow()
        }*/
    }
}