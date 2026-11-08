package com.hemanvibes.app.network

import com.hemanvibes.app.model.LyricLine
import com.hemanvibes.app.model.Lyrics
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class LyricsClient(
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
) {
    companion object {
        private const val LRCLIB_URL = "https://lrclib.net/api/get"
    }

    suspend fun getLyrics(trackName: String, artistName: String): Lyrics? {
        return try {
            val response = client.get(LRCLIB_URL) {
                header("User-Agent", "HeManVibes/1.0.0 (https://github.com/hemansubedi10/HeManVibes)")
                parameter("track_name", trackName)
                parameter("artist_name", artistName)
            }.bodyAsText()

            val json = Json { ignoreUnknownKeys = true }
            val root = json.parseToJsonElement(response).jsonObject

            val duration = root["duration"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val plain = root["plainLyrics"]?.jsonPrimitive?.content
            val synced = root["syncedLyrics"]?.jsonPrimitive?.content

            Lyrics(
                trackName = trackName,
                artistName = artistName,
                durationSeconds = duration,
                plainLyrics = plain,
                syncedLyrics = synced?.let { parseLrc(it) } ?: emptyList()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseLrc(lrcText: String): List<LyricLine> {
        val lines = mutableListOf<LyricLine>()
        val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2,3})\](.*)""")

        for (rawLine in lrcText.lines()) {
            val match = regex.find(rawLine.trim()) ?: continue
            val (minStr, secStr, msStr, text) = match.destructured

            val minutes = minStr.toLongOrNull() ?: 0L
            val seconds = secStr.toLongOrNull() ?: 0L
            val ms = if (msStr.length == 2) {
                (msStr.toLongOrNull() ?: 0L) * 10
            } else {
                msStr.toLongOrNull() ?: 0L
            }

            val timestampMs = (minutes * 60 * 1000) + (seconds * 1000) + ms
            lines.add(LyricLine(timestampMs, text.trim()))
        }

        return lines.sortedBy { it.timestampMs }
    }
}
