package com.example.musicapp.di


import com.example.musicapp.MusicService
import com.example.musicapp.ui.HomeFragment
import com.example.musicapp.ui.PlayerFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ViewModelModule::class, ApplicationModule::class, DatabaseModule::class]
)
interface ApplicationComponent {
    fun inject(newsListFragment: HomeFragment?)
    fun inject(playerFragment: PlayerFragment?)
    fun inject(musicService: MusicService)


}