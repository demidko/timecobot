repositories {
  mavenCentral()
  maven("https://jitpack.io")
}
plugins {
  kotlin("jvm") version "1.5.30"
  id("com.github.johnrengelman.shadow") version "7.0.0"
}
dependencies {
  implementation("org.redisson:redisson:3.16.2")
  implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.0.5")
  implementation("ch.qos.logback:logback-classic:1.2.5")
  implementation("com.github.demidko:print-utils:2021.09.03")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.0-RC1")
  testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.24")
  testImplementation("io.mockk:mockk:1.12.0")
}
tasks.compileKotlin {
  kotlinOptions.jvmTarget = "16"
  kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
}
tasks.compileTestKotlin {
  kotlinOptions.jvmTarget = "16"
  kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
}
tasks.test {
  useJUnitPlatform()
}
tasks.jar {
  isZip64 = true
  manifest.attributes("Main-Class" to "ml.demidko.timecobot.AppKt")
}
tasks.build {
  dependsOn(tasks.shadowJar)
}