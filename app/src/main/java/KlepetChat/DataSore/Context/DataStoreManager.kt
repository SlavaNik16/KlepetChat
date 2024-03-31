package KlepetChat.DataSore.Context

import KlepetChat.DataSore.Interface.IUserDataStore
import KlepetChat.DataSore.Models.UserData
import KlepetChat.Hilts.dataStore
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class DataStoreManager @Inject constructor(private val context: Context) : IUserDataStore {
    override val userDataFlow: Flow<UserData> = context.dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        val phone = preferences[KEY_PHONE] ?: ""
        val accessToken = preferences[KEY_ACCESS_TOKEN] ?: ""
        val refreshToken = preferences[KEY_REFRESH_TOKEN] ?: ""
        val isFirst = preferences[KEY_REFRESH_TOKEN] ?: ""
        UserData(phone, accessToken, refreshToken)
    }

    override suspend fun SaveUserData(userData: UserData) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PHONE] = userData.phone
            preferences[KEY_ACCESS_TOKEN] = userData.accessToken
            preferences[KEY_REFRESH_TOKEN] = userData.refreshToken
        }
    }

    override suspend fun ClearUserData() {
        context.dataStore.edit { preferences ->
            preferences[KEY_PHONE] = ""
            preferences[KEY_ACCESS_TOKEN] = ""
            preferences[KEY_REFRESH_TOKEN] = ""
        }
    }

    override suspend fun UpdateTokens(accessToken: String?, refreshToken: String?) {
        context.dataStore.edit { preferences ->
            if (accessToken != null) {
                preferences[KEY_ACCESS_TOKEN] = accessToken
            }
            if (refreshToken != null) {
                preferences[KEY_REFRESH_TOKEN] = refreshToken
            }
        }
    }

    companion object {
        private val KEY_PHONE = stringPreferencesKey("key_phone")
        private val KEY_IS_FIRTST = booleanPreferencesKey("key_is_first")
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
    }
}
