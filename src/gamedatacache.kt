import kotlinx.serialization.Serializable
import java.io.File
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

@Serializable
data class CachedGame(val game: Game, val cacheTime: Instant)

class GameDataCache(private val backingFile: File, private val nowFunc: () -> Instant = ::now) {
    private var data: MutableMap<Int, CachedGame> = mutableMapOf()
    private var dataLoaded = false

    fun cachedValueOr(appId: Int, fetchFn: () -> Game): Game {
        if (!dataLoaded && backingFile.exists()) {
            data = jsonFormat.decodeFromString(backingFile.readText())
            dataLoaded = true
        }

        val maybeCachedGame = data[appId]
        if (maybeCachedGame != null && maybeCachedGame.cacheTime > (nowFunc() - 1.days)) {
            return maybeCachedGame.game
        }

        val freshGame = fetchFn()
        this.data[appId] = CachedGame(game = freshGame, cacheTime = nowFunc())
        this.clearOldGames()
        backingFile.writeText(jsonFormat.encodeToString(data))

        return freshGame
    }

    private fun clearOldGames() {
        val expiryTime = nowFunc() - 1.days
        data.entries.removeIf { it.value.cacheTime < expiryTime }
    }
}