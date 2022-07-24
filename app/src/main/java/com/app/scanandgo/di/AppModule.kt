package com.app.scanandgo.di

import android.content.Context
import androidx.room.Room
import com.app.scanandgo.db.StoreDatabase
import com.app.scanandgo.feature_cart.data.CartItemDao
import com.app.scanandgo.network.ApiService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofitService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .client(okHttpClient)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(OkHttpProfilerInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideStoreDatabase(@ApplicationContext app: Context): StoreDatabase {
        return Room.databaseBuilder(
            app,
            StoreDatabase::class.java,
            "your_db_name").build()
    }

    @Provides
    @Singleton
    fun provideCartItemDao(db: StoreDatabase): CartItemDao {
        return db.getCartItemDao()
    }

}