package KlepetChat.Activities.Data

import java.util.UUID

class Constants {
    companion object {
        const val KEY_PROFILE_VIEW = "profile_view"
        const val KEY_USER_ROLE = "user_role"
        const val KEY_IS_OPEN_GROUP = "is_open_group"
        const val KEY_CHAT_PEOPLE = "chat_people"
        const val KEY_USER_PHONE_OTHER = "user_phone_other"
        const val KEY_USER_PHONE = "user_phone"
        const val KEY_CHAT_ID = "chat_id"
        const val KEY_CHAT_NAME = "chat_name"
        const val KEY_IMAGE_URL = "image_url"

        const val KEY_TAG_MOON = "moon"
        const val KEY_TAG_SUN = "sun"
        const val KEY_TAG_SEARCH = "search"
        const val KEY_TAG_SEARCHOFF = "search_off"

        val GUID_NULL: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
    }

}