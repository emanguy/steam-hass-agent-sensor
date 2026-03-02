import kotlinx.serialization.json.Json
import java.io.File

val jsonFormat = Json {
    prettyPrint = true
    explicitNulls = false
    ignoreUnknownKeys = true
}


// Sensors:
//   Steam updating (t/f)
//   App name (string)
//   App image (url string)

fun main() {
    val appIdUpdateRegex = "^] AppID (\\d+) state changed : (.*)$".toRegex()
    val appUpdateLine = File("./sample_log.txt").bufferedReader().useLines { lines ->
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

    val (_, appId, commaSepStates) = appIdUpdateRegex.matchEntire(appUpdateLine)!!.groupValues
    val game = getGame(appId.toInt())
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
                sensorName = "Steam Updating",
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
