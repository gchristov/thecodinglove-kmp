plugins {
    id("backend-service-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(projects.searchData)
            }
        }
    }
}