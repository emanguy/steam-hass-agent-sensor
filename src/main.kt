import io.github.cdimascio.dotenv.Dotenv
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.div

val jsonFormat = Json {
    explicitNulls = false
    ignoreUnknownKeys = true
}

fun main() {
    val dotenv = Dotenv.configure()
        .ignoreIfMissing()
        .load()
    val homeDir = Path(System.getProperty("user.home"))
    val steamLogFile = dotenv["STEAM_CONTENT_LOG"]?.let { Path(it) }
        ?: (homeDir / ".var/app/com.valvesoftware.Steam/data/Steam/logs/content_log.txt")

    val appIdUpdateRegex = "^] AppID (\\d+) state changed : (.*)$".toRegex()
    val appUpdateLine = steamLogFile.bufferedReader().useLines { lines ->
        lines
            .map { line ->
                line.dropWhile { it != ']' }
            }
            .findLast { it.matches(appIdUpdateRegex) }
    }

    if (appUpdateLine == null) {
        println(jsonFormat.encodeToString(defaultSensors()))
        return
    }

    val gameCache = GameDataCache(File("./game_cache.json"))

    val (_, appId, commaSepStates) = appIdUpdateRegex.matchEntire(appUpdateLine)!!.groupValues
    val game = gameCache.cachedValueOr(appId.toInt()) { getGame(appId.toInt()) }
    val updateState = determineUpdateState(commaSepStates.split(","))

    val sensorSet = sensorsUpdating(game.name, game.headerImage, updateState)
    println(jsonFormat.encodeToString(sensorSet))
}

fun determineUpdateState(states: List<String>): UpdateState {
    return when {
        states.contains("Fully Installed") -> UpdateState.UPDATED
        states.contains("Running Update") -> UpdateState.UPDATING
        else -> UpdateState.IDLE
    }
}

const val SCHEDULE = "*/30 * * * * *"

fun defaultSensors(): SensorSet {
    return SensorSet(
        schedule = SCHEDULE,
        sensors = listOf(
            Sensor(
                sensorName = "Steam Update State",
                sensorIcon = "mdi:steam",
                sensorState = UpdateState.IDLE.stringState,
            ),
            Sensor(
                sensorName = "App Name",
                sensorIcon = "mdi:controller",
                sensorState = "N/A",
            ),
            Sensor(
                sensorName = "App Image",
                sensorIcon = "mdi:image",
                sensorState = "",
            )
        )
    )
}

fun sensorsUpdating(appName: String, appImageUrl: String, updateState: UpdateState): SensorSet {
    return SensorSet(
        schedule = SCHEDULE,
        sensors = listOf(
            Sensor(
                sensorName = "Steam Update State",
                sensorIcon = "mdi:steam",
                sensorState = updateState.stringState,
            ),
            Sensor(
                sensorName = "App Name",
                sensorIcon = "mdi:controller",
                sensorState = appName,
            ),
            Sensor(
                sensorName = "App Image",
                sensorIcon = "mdi:image",
                sensorState = appImageUrl,
            ),
        )
    )
}
