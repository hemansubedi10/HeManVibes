#!/usr/bin/env python3
"""
HeMan Vibes CLI — Global Music Engine & App Controller
Used inside agy CLI and system terminal.
"""

import sys
import os
import json
import urllib.request
import urllib.parse
import subprocess

APP_EXE = r"C:\Users\heman\HeManVibes\desktopApp\build\compose\binaries\main\app\HeManVibes\HeManVibes.exe"
PROJECT_DIR = r"C:\Users\heman\HeManVibes"

def search_music(query):
    url = "https://music.youtube.com/youtubei/v1/search?prettyPrint=false"
    headers = {
        "Content-Type": "application/json",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Referer": "https://music.youtube.com/",
        "Origin": "https://music.youtube.com",
    }
    payload = {
        "context": {
            "client": {
                "clientName": "WEB_REMIX",
                "clientVersion": "1.20241001.01.00",
                "hl": "en",
                "gl": "US"
            }
        },
        "query": query
    }
    
    req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers=headers, method='POST')
    try:
        with urllib.request.urlopen(req, timeout=10) as response:
            data = json.loads(response.read().decode('utf-8'))
    except Exception as e:
        print(f"Error querying YouTube Music: {e}")
        return []

    sections = (
        data.get("contents", {})
        .get("tabbedSearchResultsRenderer", {})
        .get("tabs", [{}])[0]
        .get("tabRenderer", {})
        .get("content", {})
        .get("sectionListRenderer", {})
        .get("contents", [])
    )
    
    tracks = []
    for section in sections:
        music_shelf = section.get("musicShelfRenderer") or section.get("musicCardShelfRenderer")
        if not music_shelf:
            continue
        items = music_shelf.get("contents", [])
        for item in items:
            renderer = item.get("musicResponsiveListItemRenderer")
            if not renderer:
                continue
            playlist_item_data = renderer.get("playlistItemData", {})
            video_id = playlist_item_data.get("videoId")
            flex_columns = renderer.get("flexColumns", [])
            title = None
            artist = None
            
            if len(flex_columns) > 0:
                runs = flex_columns[0].get("musicResponsiveListItemFlexColumnRenderer", {}).get("text", {}).get("runs", [])
                if runs:
                    title = runs[0].get("text")
                    if not video_id:
                        nav = runs[0].get("navigationEndpoint", {})
                        video_id = nav.get("watchEndpoint", {}).get("videoId")
            
            if len(flex_columns) > 1:
                runs = flex_columns[1].get("musicResponsiveListItemFlexColumnRenderer", {}).get("text", {}).get("runs", [])
                parts = [r.get("text") for r in runs if r.get("text") not in [" • ", " / "]]
                if parts:
                    artist = parts[0]
            
            thumbs = renderer.get("thumbnail", {}).get("musicThumbnailRenderer", {}).get("thumbnail", {}).get("thumbnails", [])
            thumb = thumbs[-1].get("url") if thumbs else None
            
            if title and video_id:
                tracks.append({
                    "title": title,
                    "artist": artist or "Unknown Artist",
                    "id": video_id,
                    "url": f"https://www.youtube.com/watch?v={video_id}",
                    "thumbnail": thumb
                })
    return tracks

def fetch_lyrics(track, artist=""):
    params = urllib.parse.urlencode({"track_name": track, "artist_name": artist})
    url = f"https://lrclib.net/api/get?{params}"
    req = urllib.request.Request(url, headers={"User-Agent": "HeManVibes-CLI/1.0.0"})
    try:
        with urllib.request.urlopen(req, timeout=10) as response:
            return json.loads(response.read().decode('utf-8'))
    except Exception as e:
        print(f"Error fetching lyrics: {e}")
        return None

def launch_app():
    if os.path.exists(APP_EXE):
        app_dir = os.path.dirname(APP_EXE)
        print(f"Launching HeMan Vibes Native Desktop App...")
        if sys.platform == "win32":
            ps_cmd = f'Invoke-CimMethod -ClassName Win32_Process -MethodName Create -Arguments @{{CommandLine = "{APP_EXE}"; CurrentDirectory = "{app_dir}"}} | Out-Null'
            subprocess.run(["powershell", "-NoProfile", "-Command", ps_cmd], check=False)
        else:
            subprocess.Popen([APP_EXE], cwd=app_dir)
        print("HeMan Vibes is now running on your desktop.")
    else:
        print(f"App binary not found at {APP_EXE}. Running via Gradle...")
        subprocess.Popen(["cmd.exe", "/c", "gradlew.bat", ":desktopApp:run"], cwd=PROJECT_DIR)

def print_help():
    print("""
🎵 HeMan Vibes CLI — Antigravity (agy) Music Companion

Usage:
  heman-vibes                        Launch the HeMan Vibes Desktop App
  heman-vibes run                    Launch the HeMan Vibes Desktop App
  heman-vibes search <query>         Search YouTube Music catalog from CLI
  heman-vibes lyrics <title> [artist] Fetch synced timestamped LRC lyrics
  heman-vibes build                  Compile the latest Windows distribution
  heman-vibes help                   Show this help menu
""")

def main():
    if len(sys.argv) < 2 or sys.argv[1] in ["run", "open", "start"]:
        launch_app()
    elif sys.argv[1] == "search":
        if len(sys.argv) < 3:
            print("Usage: heman-vibes search <song or artist>")
            return
        query = " ".join(sys.argv[2:])
        print(f"Searching YouTube Music for: '{query}'...")
        results = search_music(query)
        if not results:
            print("No tracks found.")
            return
        print(f"\nFound {len(results)} songs:")
        for i, r in enumerate(results[:10], 1):
            print(f" {i:2d}. {r['title']} — {r['artist']} (ID: {r['id']})")
    elif sys.argv[1] == "lyrics":
        if len(sys.argv) < 3:
            print("Usage: heman-vibes lyrics <song> [artist]")
            return
        track = sys.argv[2]
        artist = sys.argv[3] if len(sys.argv) > 3 else ""
        print(f"Fetching synced lyrics for: {track} by {artist}...")
        data = fetch_lyrics(track, artist)
        if data and data.get("syncedLyrics"):
            print("\nSynced Lyrics (LRCLIB):")
            print(data["syncedLyrics"])
        elif data and data.get("plainLyrics"):
            print("\nPlain Lyrics:")
            print(data["plainLyrics"])
        else:
            print("Lyrics not found.")
    elif sys.argv[1] == "build":
        print("Compiling HeMan Vibes desktop distribution...")
        subprocess.run(["cmd.exe", "/c", "gradlew.bat", ":desktopApp:createDistributable"], cwd=PROJECT_DIR)
    else:
        print_help()

if __name__ == "__main__":
    main()
