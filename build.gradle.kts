plugins {
    kotlin("multiplatform") version "1.6.21"
}

description = "Kommons Debug is a Kotlin Multiplatform Library to support print debugging"
group = "com.bkahlert.kommons"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
            testTask {
                testLogging.showStandardStreams = true
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-assertions-core:5.3.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("reflect"))
            }
        }
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
    }
}
