import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameEnvelope<T>(val success: Boolean, val data: T?)

@Serializable
data class Game(
    val name: String,
    @SerialName("header_image")
    val headerImage: String,
)

val client = HttpClient {
    install(ContentNegotiation) {
        json(jsonFormat)
    }
}

fun getGame(appId: Int): Game? {
    return runBlocking {
        val response: Map<String, GameEnvelope<Game>> = client.get("https://store.steampowered.com/api/appdetails") {
            url {
                parameters.append("appids", appId.toString())
            }
        }.body()

        response[appId.toString()]!!.data
    }
}
