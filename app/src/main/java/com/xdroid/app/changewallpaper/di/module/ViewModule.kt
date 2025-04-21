package com.xdroid.app.changewallpaper.di.module

import com.xdroid.app.changewallpaper.utils.vm.FavoriteViewModel
import com.xdroid.app.changewallpaper.utils.vm.HomeViewModel
import com.xdroid.app.changewallpaper.utils.vm.SharedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { SharedViewModel() }
    viewModel { FavoriteViewModel(get()) }
}