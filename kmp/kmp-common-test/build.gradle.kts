import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("kmp-platform-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin(Deps.Tests.test))
            }
        }
    }
}