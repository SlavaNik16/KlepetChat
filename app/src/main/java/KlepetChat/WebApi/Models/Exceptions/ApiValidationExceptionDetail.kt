package KlepetChat.WebApi.Models.Exceptions

import com.google.gson.annotations.SerializedName

data class ApiValidationExceptionDetail(
    @SerializedName("errors")
    val errors: MutableList<InvalidateItemModel>,
)