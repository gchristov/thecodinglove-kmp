plugins {
    kotlin("js") version "1.7.20-RC"
}

kotlin {
    js(IR) {
        nodejs()
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(projects.moduleb)
            }
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
}