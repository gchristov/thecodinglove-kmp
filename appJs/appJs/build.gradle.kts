plugins {
    kotlin("js") version "1.7.10"
}

kotlin {
    js {
        binaries.executable()
        nodejs()
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":modulea"))
            }
        }
    }
}
