import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

version = "1.0-SNAPSHOT"
plugins {
    kotlin("multiplatform") version "1.3.50"
    `maven-publish`
    id("org.jetbrains.dokka") version "0.10.0"
}

buildscript {
    val dokkaVersion = "0.9.18"
    dependencies {
        classpath("org.jetbrains.dokka:dokka-android-gradle-plugin:$dokkaVersion")
    }
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://dl.bintray.com/kyonifer/maven")
}

kotlin {
    jvm()
    js("js") {
        browser {
        }
        nodejs {
        }
    }
    // For ARM, should be changed to iosArm32 or iosArm64
    // For Linux, should be changed to e.g. linuxX64
    // For MacOS, should be changed to e.g. macosX64
    // For Windows, should be changed to e.g. mingwX64
//    mingwX64("mingw")
    sourceSets {
        val commonMain by getting {
            dependencies {
                kotlin("stdlib-common")
                implementation("com.kyonifer:koma-core-api-common:0.12")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                //implementation("com.kyonifer:koma-core-jblas:0.12")
                //implementation("com.kyonifer:koma-core-mtj:0.12")
                implementation("com.kyonifer:koma-core-ejml:0.12")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("com.kyonifer:koma-core-js:0.12")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
//        val mingwMain by getting {
//            dependencies {
//                //implementation("com.kyonifer:koma-core-api-common:0.12")
//                implementation("com.kyonifer:koma-core-cblas:0.12")
//            }
//        }
//        val mingwTest by getting {
//        }
    }
}

tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
        configuration {
            externalDocumentationLink {
                url = URL("https://example.com/docs/")
            }
        }
        multiplatform {
            val js by creating { // The same name as in Kotlin Multiplatform plugin, so the sources are fetched automatically
                includes = listOf("packages.md")
            }

            register("jvm") { // Different name, so source roots must be passed explicitly
                targets = listOf("JVM")
                platform = "jvm"
            }
        }
    }
}
