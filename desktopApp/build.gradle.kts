import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "21"
            }
        }
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(projects.composeApp)
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.hemanvibes.app.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "HeManVibes"
            packageVersion = "1.0.0"
            description = "HeMan Vibes - Next-Gen Cross-Platform Music Player"
            copyright = "© 2026 Heman Subedi. All rights reserved."
            vendor = "HeMan Vibes"

            windows {
                menuGroup = "HeMan Vibes"
                upgradeUuid = "68c227bf-463e-4b68-b34e-09f482d8d80c"
                iconFile.set(project.file("../assets/icon.ico"))
            }

            macOS {
                bundleID = "com.hemanvibes.app"
                iconFile.set(project.file("../assets/icon.icns"))
            }

            linux {
                iconFile.set(project.file("../assets/icon.png"))
            }
        }
    }
}
