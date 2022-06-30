import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("multiplatform") version "1.7.0"
    id("org.jetbrains.dokka") version "1.7.0"
    id("maven-publish")
    signing
    id("nebula.release") version "16.0.0"
}

allprojects {
    apply { plugin("maven-publish") }
}

description = "Kommons Debug is a Kotlin Multiplatform Library to support print debugging"
group = "com.bkahlert.kommons"

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        browser {
            testTask {
                testLogging.showStandardStreams = true
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs()
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-test:0.3.6")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.slf4j:slf4j-api:1.7.36")
                implementation(kotlin("reflect"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.slf4j:slf4j-simple:1.7.36")
            }
        }
        val jsMain by getting {
            dependencies {
                @Suppress("SpellCheckingInspection")
                implementation(npm("xregexp", "5.1.0"))
                implementation(npm("grapheme-splitter", "1.0.4"))
            }
        }
        val jsTest by getting

        all {
            languageSettings.apply {
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.experimental.ExperimentalTypeInference")
                progressiveMode = true // false by default
            }
        }
        jvmMain.languageSettings.apply {
            optIn("kotlin.reflect.jvm.ExperimentalReflectionOnLambdas")
        }
    }
}

tasks {
    withType<Test>().configureEach {
        testLogging {
            events = setOf(
                TestLogEvent.SKIPPED,
                TestLogEvent.FAILED,
                TestLogEvent.STANDARD_OUT,
                TestLogEvent.STANDARD_ERROR
            )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }
}

val dokkaOutputDir = buildDir.resolve("dokka")
tasks.dokkaHtml.configure {
    outputDirectory.set(dokkaOutputDir)
    dokkaSourceSets {
        configureEach {
            displayName.set(
                when (platform.get()) {
                    org.jetbrains.dokka.Platform.jvm -> "jvm"
                    org.jetbrains.dokka.Platform.js -> "js"
                    org.jetbrains.dokka.Platform.native -> "native"
                    org.jetbrains.dokka.Platform.common -> "common"
                }
            )
        }
    }
}
val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") { delete(dokkaOutputDir) }
val javadocJar = tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(deleteDokkaOutputDir) // TODO add jsGenerateExternalsIntegrated
    from(tasks.dokkaHtml.map { it.outputs })
}

publishing {
    repositories {
        @Suppress("SpellCheckingInspection")
        maven {
            name = "OSSRH"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/bkahlert/kommons-debug")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        withType<MavenPublication>().configureEach {
            artifact(javadocJar)
            pom {
                name.set("Kommons")
                description.set(project.description)
                url.set("https://github.com/bkahlert/kommons-debug")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/bkahlert/kommons-debug/blob/master/LICENSE")
                    }
                }
                scm {
                    url.set("https://github.com/bkahlert/kommons-debug")
                    connection.set("scm:git:https://github.com/bkahlert/kommons-debug.git")
                    developerConnection.set("scm:git:https://github.com/bkahlert/kommons-debug.git")
                }
                issueManagement {
                    url.set("https://github.com/bkahlert/kommons-debug/issues")
                    system.set("GitHub")
                }
                ciManagement {
                    url.set("https://github.com/bkahlert/kommons-debug/issues")
                    system.set("GitHub")
                }
                developers {
                    developer {
                        id.set("bkahlert")
                        name.set("Björn Kahlert")
                        email.set("mail@bkahlert.com")
                        url.set("https://bkahlert.com")
                        timezone.set("Europe/Berlin")
                    }
                }
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}

// getting rid of missing dependency declarations
val signingTasks = tasks.filter { it.name.startsWith("sign") }
listOf(
    tasks.getByName("publishKotlinMultiplatformPublicationToMavenLocal"),
    tasks.getByName("publishJsPublicationToMavenLocal"),
    tasks.getByName("publishJvmPublicationToMavenLocal"),
).forEach {
    it.dependsOn(signingTasks)
}
