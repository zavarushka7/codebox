package com.example.codebox.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
/* CodeboxApplication - точка входа для Hilt. Аннотация @HiltAndroidApp говорит
* "в этом приложении используй Hilt для создания объектов"*/
@HiltAndroidApp
class CodeboxApplication  : Application() {
}