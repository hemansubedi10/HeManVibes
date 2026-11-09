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

            modules(
                "java.base",
                "java.desktop",
                "java.logging",
                "java.net.http",
                "java.naming",
                "java.management",
                "java.security.jgss",
                "java.instrument",
                "java.sql",
                "java.xml",
                "jdk.unsupported",
                "jdk.crypto.ec"
            )

            windows {
                menuGroup = "HeMan Vibes"
                upgradeUuid = "68c227bf-463e-4b68-b34e-09f482d8d80c"
                iconFile.set(project.file("../assets/icon.ico"))
                shortcut = true
                menu = true
                dirChooser = true
                console = true
            }

            macOS {
                bundleID = "com.hemanvibes.app"
                val icns = project.file("../assets/icon.icns")
                if (icns.exists()) {
                    iconFile.set(icns)
                }
            }

            linux {
                iconFile.set(project.file("../assets/icon.png"))
            }
        }
    }
}
