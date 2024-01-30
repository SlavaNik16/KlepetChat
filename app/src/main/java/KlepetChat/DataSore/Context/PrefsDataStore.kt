package KlepetChat.DataSore.Context

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

abstract class PrefsDataStore(fileName: String) {
    internal val Context.dataStore: DataStore<Preferences>  by preferencesDataStore(name = fileName)
}