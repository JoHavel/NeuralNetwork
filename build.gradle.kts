version = "1.0-SNAPSHOT"
val serializationVersion = "0.13.0"
plugins {
    kotlin("multiplatform") version "1.3.50"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.50"
    `maven-publish`
    id("org.jetbrains.dokka") version "0.9.18"
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
    js {
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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializationVersion")
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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$serializationVersion")
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
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serialization_version")
//            }
//        }
//        val mingwTest by getting {
//        }
    }
}

tasks {

    // Thanks to https://discuss.kotlinlang.org/t/how-to-configure-dokka-for-kotlin-multiplatform/9834 for setting dokka
    dokka {
        moduleName = "neuralnetwork"
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
        impliedPlatforms = mutableListOf("Common", "JS")
        kotlinTasks {
            listOf()
        }
        val pathsCommon = kotlin.sourceSets["commonMain"].kotlin.asPath.split(";")
        pathsCommon.forEach {
            sourceRoot {
                path = it
                platforms = mutableListOf("Common")
            }
        }
        val pathsJS = kotlin.sourceSets["jsMain"].kotlin.asPath.split(";")
        pathsJS.forEach {
            sourceRoot {
                path = it
                platforms = mutableListOf("JS")
            }
        }
        val pathsJVM = kotlin.sourceSets["jvmMain"].kotlin.asPath.split(";")
        pathsJS.forEach {
            sourceRoot {
                path = it
                platforms = mutableListOf("JVM")
            }
        }
    }
}
