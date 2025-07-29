package team2.kakigowhere.data.api

import android.util.Base64
import java.lang.reflect.Type
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement

class Base64Deserializer : JsonDeserializer<ByteArray> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ByteArray {
        return Base64.decode(json.asString, Base64.DEFAULT)
    }
}