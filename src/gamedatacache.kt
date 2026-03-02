@file:OptIn(ExperimentalTime::class)

import java.io.File
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class CachedGame(val game: Game, val cacheTime: Instant)

class GameDataCache {
    private val backingFile = File("gamedata.json")
    private var data: MutableMap<Int, CachedGame> = mutableMapOf()
    private var dataLoaded = false

    fun cachedValueOr(appId: Int, fetchFn: () -> Game): Game {
        if (!dataLoaded) {
            data = jsonFormat.decodeFromString(backingFile.readText())
            dataLoaded = true
        }

        val maybeCachedGame = data[appId]
        if (maybeCachedGame != null && maybeCachedGame.cacheTime > (now() - 1.days)) {
            return maybeCachedGame.game
        }

        val freshGame = fetchFn()
        this.data[appId] = CachedGame(game = freshGame, cacheTime = now())
        backingFile.writeText(jsonFormat.encodeToString(data))

        return freshGame
    }
}