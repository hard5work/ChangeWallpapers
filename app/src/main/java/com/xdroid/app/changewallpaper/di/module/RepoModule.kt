package com.xdroid.app.changewallpaper.di.module

import com.xdroid.app.changewallpaper.data.repository.MainRepository
import org.koin.dsl.module

val repoModule = module {
    single {
        MainRepository(get())
    }
}