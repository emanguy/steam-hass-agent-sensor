@file:OptIn(kotlin.time.ExperimentalTime::class)

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class GameDataCacheTest {
    val nowTimeStr = "2026-01-01T12:00:00Z"
    val nowTime = Instant.parse(nowTimeStr)

    @Test
    fun `Returns cached value`() {
        val backingFileMock = mockk<File>()
        val cache = GameDataCache(
            backingFile = backingFileMock,
            nowFunc = { nowTime }
        )

        // Set up fake file
        mockkStatic(File::readText, File::writeText)
        every { backingFileMock.exists() } returns true
        every { backingFileMock.readText() } returns "{}"
        every { backingFileMock.writeText(any()) } returns Unit

        // Save the initial value
        cache.cachedValueOr(100) { Game("Super Cool Bros 2", "http://fake.url") }

        // Check that we get the cached value on the second pull
        val maybeCachedGame = cache.cachedValueOr(100) { Game("New Super Cool Bros 4D", "http://faker.url") }

        assertEquals(Game("Super Cool Bros 2", "http://fake.url"), maybeCachedGame)
        verify { backingFileMock.writeText("""{"100":{"game":{"name":"Super Cool Bros 2","header_image":"http://fake.url"},"cacheTime":"$nowTimeStr"}}""") }
    }

    @Test
    fun `Reads cache from file`() {
        val backingFileMock = mockk<File>()
        val cache = GameDataCache(
            backingFile = backingFileMock,
            nowFunc = { nowTime }
        )
        val currentCacheJson = """{
            |    "100": {
            |        "game": {
            |           "name":"Super Cool Bros 2",
            |           "header_image":"http://fake.url"
            |        },
            |        "cacheTime":"$nowTimeStr"
            |    }
            |}
        """.trimMargin()

        // Set up fake file
        mockkStatic(File::readText, File::writeText)
        every { backingFileMock.exists() } returns true
        every { backingFileMock.readText() } returns currentCacheJson
        every { backingFileMock.writeText(any()) } returns Unit

        val gameFromCache = cache.cachedValueOr(100) { Game("New Super Cool Bros 4D", "http://faker.url") }

        assertEquals(
            Game("Super Cool Bros 2", "http://fake.url"),
            gameFromCache,
        )
        verify(exactly = 0) { backingFileMock.writeText(any()) }
    }
}
