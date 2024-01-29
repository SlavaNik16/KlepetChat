package KlepetChat.RoomDB.Context

import KlepetChat.RoomDB.DAO.IDao
import KlepetChat.RoomDB.Models.JWT
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(
    entities = [
        JWT::class
    ],
    version = 1
)
abstract class KlepetDB : RoomDatabase() {
    abstract fun getDao() : IDao
    companion object{
        fun getDB(context: Context): KlepetDB {
            return Room.databaseBuilder(
                context.applicationContext,
                KlepetDB::class.java,
                "Klepet_local_token.db"
            ).build()
        }
    }
}