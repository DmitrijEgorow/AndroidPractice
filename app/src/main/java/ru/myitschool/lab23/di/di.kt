package ru.myitschool.lab23.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.myitschool.lab23.ExpensesViewModel


val module = module {

    //factory { GeneratedListFragment() }

    /*UserDataProviderSize.values().forEach { size ->
        single(named(size)) { UserDataProvider(get(), size.size) }
    }*/
}

val viewModelModule = module {

    // viewModel { MainViewModel(get(), get()) }
    // antipattern
    single { ExpensesViewModel() }
}