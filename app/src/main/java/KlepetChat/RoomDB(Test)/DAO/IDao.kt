//package KlepetChat.RoomDB.DAO
//
//import KlepetChat.RoomDB.Models.JWT
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.Query
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface IDao{
//
//    @Query("SELECT refreshToken FROM LocalToken WHERE phone= :phone")
//    fun getToken(phone: String): Flow<String>
//    @Insert
//    fun saveToken(localToken: JWT)
//
//    @Query("UPDATE LocalToken SET refreshToken = :refreshToken WHERE phone = :phone")
//    fun updateToken(phone: String, refreshToken: String)
//
//    @Query("DELETE From LocalToken WHERE phone= :phone")
//    fun deleteToken(phone: String)
//
//}