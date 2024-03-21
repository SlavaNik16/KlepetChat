package KlepetChat.Hilts

import KlepetChat.DataSore.Context.DataStoreManager
import KlepetChat.WebApi.Implementations.Authentificator.AuthAuthenticator
import KlepetChat.WebApi.Implementations.Interceptor.AuthInterceptor
import KlepetChat.WebApi.Interfaces.IAuthService
import KlepetChat.WebApi.Interfaces.IChatService
import KlepetChat.WebApi.Interfaces.IHubService
import KlepetChat.WebApi.Interfaces.IImageService
import KlepetChat.WebApi.Interfaces.IMessageService
import KlepetChat.WebApi.Interfaces.ITokenService
import KlepetChat.WebApi.Interfaces.IUserService
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
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

    companion object {
        val URL_BASE = "http://klepetapi.somee.com/"
        val URL_IMG = "http://upload-soft.photolab.me/"
        val URL_SIGNALR = "http://klepetapi.somee.com/ch"
    }

    @Singleton
    @Provides
    fun providesDataStoreManager(@ApplicationContext context: Context): DataStoreManager =
        DataStoreManager(context)

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
    fun providesRetrofitBuilder(): Retrofit.Builder {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        var client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun providesIAuthService(retrofit: Retrofit.Builder): IAuthService =
        retrofit
            .build()
            .create(IAuthService::class.java)


    @Singleton
    @Provides
    fun providesIUserService(okHttpClient: OkHttpClient, retrofit: Retrofit.Builder): IUserService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(IUserService::class.java)

    @Singleton
    @Provides
    fun providesITokenService(
        okHttpClient: OkHttpClient,
        retrofit: Retrofit.Builder,
    ): ITokenService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(ITokenService::class.java)

    @Singleton
    @Provides
    fun providesIChatService(okHttpClient: OkHttpClient, retrofit: Retrofit.Builder): IChatService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(IChatService::class.java)

    @Singleton
    @Provides
    fun providesIMessageService(
        okHttpClient: OkHttpClient,
        retrofit: Retrofit.Builder,
    ): IMessageService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(IMessageService::class.java)

    @Singleton
    @Provides
    fun providesIImageService(): IImageService {
        return Retrofit.Builder()
            .baseUrl(URL_IMG)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IImageService::class.java)
    }

    @Singleton
    @Provides
    fun providesIHubService(okHttpClient: OkHttpClient, retrofit: Retrofit.Builder): IHubService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(IHubService::class.java)


    @Singleton
    @Provides
    fun providesHubConnection(): HubConnection {
        return HubConnectionBuilder
            .create(URL_SIGNALR)
            .build()
    }
}