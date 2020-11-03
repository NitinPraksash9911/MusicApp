package com.example.musicapp.di


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicapp.viewmodel.MusicDataViewModel
import com.example.musicapp.viewmodel.ViewModelProviderFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelProviderFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MusicDataViewModel::class)
    abstract fun bindWebViewModel(myViewModel: MusicDataViewModel): ViewModel
}