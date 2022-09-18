package ru.myitschool.lab23.ui.main

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

class MainViewModel(
    private val mean: Double = 0.0,
    private val variance: Double = 1.0
) : ViewModel() {
    init {
        Log.d("Tests","init")
    }

    private val generatedListData: MutableLiveData<List<Double>> by lazy {
        MutableLiveData()
    }

    fun setGeneratedListData(form: List<Double>){
        this.generatedListData.value = form
    }

    fun getGeneratedListData(): LiveData<List<Double>> = generatedListData




}
