package KlepetChat.RoomDB.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.UUID

@Entity(tableName = "LocalToken", indices = [Index(value = ["phone"], unique = true)])
data class JWT (
    @PrimaryKey(autoGenerate = false)
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo("phone")
    val phone: String,
    @ColumnInfo("accessToken")
    val accessToken: String,
    @ColumnInfo("refreshToken")
    val refreshToken: String,
)