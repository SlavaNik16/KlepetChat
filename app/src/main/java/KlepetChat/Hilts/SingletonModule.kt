package KlepetChat.Hilts

import KlepetChat.DataSore.Context.DataStoreManager
import KlepetChat.WebApi.Implementations.Authentificator.AuthAuthenticator
import KlepetChat.WebApi.Implementations.Interceptor.AuthInterceptor
import KlepetChat.WebApi.Interfaces.IAuthService
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data_store")

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    companion object{
        private val URL_BASE = "http://klepetapi.somee.com/api/"
    }
    @Singleton
    @Provides
    fun providesDataStoreManager(@ApplicationContext context: Context): DataStoreManager = DataStoreManager(context)

    @Singleton
    @Provides
    fun providesOkHttpClient(
        authInterceptor: AuthInterceptor,
        authAuthenticator: AuthAuthenticator,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authAuthenticator)
            .build()
    }

    @Singleton
    @Provides
    fun providesAuthInterceptor(dataStoreManager: DataStoreManager): AuthInterceptor =
        AuthInterceptor(dataStoreManager)

    @Singleton
    @Provides
    fun providesAuthAuthenticator(dataStoreManager: DataStoreManager): AuthAuthenticator =
        AuthAuthenticator(dataStoreManager)

    @Singleton
    @Provides
    fun providesRetrofitBuilder(): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun providesIAuthService(retrofit: Retrofit.Builder): IAuthService =
        retrofit
            .build()
            .create(IAuthService::class.java)

}