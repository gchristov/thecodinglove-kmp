plugins {
    kotlin("js") version "1.7.20-RC"
}

kotlin {
    js(IR) {
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

dependencies {
    testImplementation(kotlin("test"))
}