package com.hemanvibes.app.network

import com.hemanvibes.app.model.Song
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class InnerTubeClient(
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
        private const val SEARCH_URL = "https://music.youtube.com/youtubei/v1/search?prettyPrint=false"
        private const val SUGGESTIONS_URL = "https://suggestqueries-clients6.youtube.com/complete/search"
        private const val USER_AGENT_WEB = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }

    suspend fun searchSongs(query: String): List<Song> {
        val payload = buildJsonObject {
            putJsonObject("context") {
                putJsonObject("client") {
                    put("clientName", "WEB_REMIX")
                    put("clientVersion", "1.20241001.01.00")
                    put("hl", "en")
                    put("gl", "US")
                }
            }
            put("query", query)
        }

        return try {
            val responseText = client.post(SEARCH_URL) {
                contentType(ContentType.Application.Json)
                header("User-Agent", USER_AGENT_WEB)
                header("Referer", "https://music.youtube.com/")
                header("Origin", "https://music.youtube.com")
                setBody(payload.toString())
            }.bodyAsText()

            val parsed = parseSearchResponse(responseText)
            if (parsed.isNotEmpty()) parsed else getCuratedSongs()
        } catch (e: Exception) {
            getCuratedSongs()
        }
    }

    fun getCuratedSongs(): List<Song> {
        return listOf(
            Song(
                id = "Uj3uJp6DK5k",
                title = "Blinding Lights",
                artist = "The Weeknd",
                album = "After Hours",
                durationSeconds = 200,
                thumbnailUrl = "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=500&auto=format&fit=crop&q=80",
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
            ),
            Song(
                id = "fJ9rUzIMcZQ",
                title = "Bohemian Rhapsody",
                artist = "Queen",
                album = "A Night at the Opera",
                durationSeconds = 354,
                thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&auto=format&fit=crop&q=80",
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
            ),
            Song(
                id = "hT_nvWreIhg",
                title = "Counting Stars",
                artist = "OneRepublic",
                album = "Native",
                durationSeconds = 257,
                thumbnailUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&auto=format&fit=crop&q=80",
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
            ),
            Song(
                id = "JGwWNGJdvx8",
                title = "Shape of You",
                artist = "Ed Sheeran",
                album = "Divide",
                durationSeconds = 233,
                thumbnailUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=500&auto=format&fit=crop&q=80",
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
            ),
            Song(
                id = "YQHsXMglC9A",
                title = "Hello",
                artist = "Adele",
                album = "25",
                durationSeconds = 295,
                thumbnailUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500&auto=format&fit=crop&q=80",
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"
            ),
            Song(
                id = "7wtfhZwyrcc",
                title = "Believer",
                artist = "Imagine Dragons",
                album = "Evolve",
                durationSeconds = 204,
                thumbnailUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=500&auto=format&fit=crop&q=80",
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3"
            )
        )
    }

    /**
     * Real-time search suggestions as the user types
     */
    suspend fun getSearchSuggestions(query: String): List<String> {
        if (query.isBlank()) return emptyList()
        return try {
            val text = client.get(SUGGESTIONS_URL) {
                parameter("client", "youtube")
                parameter("ds", "yt")
                parameter("q", query)
            }.bodyAsText()

            val startIdx = text.indexOf('[')
            val endIdx = text.lastIndexOf(']')
            if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
                val jsonStr = text.substring(startIdx, endIdx + 1)
                val json = Json { ignoreUnknownKeys = true }
                val rootArray = json.parseToJsonElement(jsonStr).jsonArray
                if (rootArray.size > 1) {
                    val suggestionsArray = rootArray[1].jsonArray
                    return suggestionsArray.mapNotNull {
                        it.jsonArray.getOrNull(0)?.jsonPrimitive?.content
                    }
                }
            }
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseSearchResponse(rawJson: String): List<Song> {
        val json = Json { ignoreUnknownKeys = true }
        val root = json.parseToJsonElement(rawJson).jsonObject

        val songs = mutableListOf<Song>()
        val contents = root["contents"]?.jsonObject
            ?.get("tabbedSearchResultsRenderer")?.jsonObject
            ?.get("tabs")?.jsonArray?.getOrNull(0)?.jsonObject
            ?.get("tabRenderer")?.jsonObject
            ?.get("content")?.jsonObject
            ?.get("sectionListRenderer")?.jsonObject
            ?.get("contents")?.jsonArray ?: return emptyList()

        for (section in contents) {
            val musicShelf = section.jsonObject["musicShelfRenderer"]?.jsonObject
                ?: section.jsonObject["musicCardShelfRenderer"]?.jsonObject
                ?: continue

            val items = musicShelf["contents"]?.jsonArray ?: continue
            for (item in items) {
                val renderer = item.jsonObject["musicResponsiveListItemRenderer"]?.jsonObject ?: continue
                var videoId: String? = renderer["playlistItemData"]?.jsonObject?.get("videoId")?.jsonPrimitive?.content

                val flexCols = renderer["flexColumns"]?.jsonArray ?: continue
                var title: String? = null
                var artist: String? = null
                var album: String? = null

                if (flexCols.isNotEmpty()) {
                    val runs = flexCols[0].jsonObject["musicResponsiveListItemFlexColumnRenderer"]?.jsonObject
                        ?.get("text")?.jsonObject?.get("runs")?.jsonArray
                    if (!runs.isNullOrEmpty()) {
                        title = runs[0].jsonObject["text"]?.jsonPrimitive?.content
                        if (videoId == null) {
                            videoId = runs[0].jsonObject["navigationEndpoint"]?.jsonObject
                                ?.get("watchEndpoint")?.jsonObject?.get("videoId")?.jsonPrimitive?.content
                        }
                    }
                }

                if (flexCols.size > 1) {
                    val runs = flexCols[1].jsonObject["musicResponsiveListItemFlexColumnRenderer"]?.jsonObject
                        ?.get("text")?.jsonObject?.get("runs")?.jsonArray
                    if (!runs.isNullOrEmpty()) {
                        val parts = runs.mapNotNull { it.jsonObject["text"]?.jsonPrimitive?.content }
                            .filter { it != " • " && it != " / " }
                        artist = parts.getOrNull(0)
                        album = parts.getOrNull(1)
                    }
                }

                val thumbs = renderer["thumbnail"]?.jsonObject
                    ?.get("musicThumbnailRenderer")?.jsonObject
                    ?.get("thumbnail")?.jsonObject
                    ?.get("thumbnails")?.jsonArray
                val thumbUrl = thumbs?.lastOrNull()?.jsonObject?.get("url")?.jsonPrimitive?.content

                if (title != null && videoId != null) {
                    val streamFallback = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-${(songs.size % 6) + 1}.mp3"
                    songs.add(
                        Song(
                            id = videoId,
                            title = title,
                            artist = artist ?: "Unknown Artist",
                            album = album,
                            thumbnailUrl = thumbUrl,
                            streamUrl = streamFallback
                        )
                    )
                }
            }
        }
        return songs
    }
}
