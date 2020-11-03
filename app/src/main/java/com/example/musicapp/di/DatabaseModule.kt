package com.example.musicapp.di


import android.app.Application
import androidx.room.Room
import com.example.musicapp.data.roomdb.DATABASE_NAME
import com.example.musicapp.data.roomdb.MusicDao
import com.example.musicapp.data.roomdb.MusicDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
abstract class DatabaseModule {

    companion object {

        @DatabaseInfo
        private val mDBName =
            DATABASE_NAME


        @Singleton
        @Provides
        fun provideDatabase(mContext: Application): MusicDb {
            return Room.databaseBuilder(
                mContext,
                MusicDb::class.java,
                mDBName
            ).fallbackToDestructiveMigration().build()
        }


        @Singleton
        @Provides
        fun provideNoteDao(db: MusicDb): MusicDao {
            return db.musicDao()
        }

    }

    /**
     * [fallbackToDestructiveMigration] :: when we increase the version number of database we have to tell room
     * how to migrate to the new schema  and if don't do this and increase version number our app will actually crash
     *  and we get illegalStateException.
     * By using [fallbackToDestructiveMigration] we can avoid the above exception because it will delete the database and
     *  all its' tables and create the fresh database from the scratch
     * */

}
