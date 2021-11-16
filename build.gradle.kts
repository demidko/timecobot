repositories {
  mavenCentral()
  maven("https://jitpack.io")
}
plugins {
  kotlin("jvm") version "1.5.31"
  id("com.github.johnrengelman.shadow") version "7.1.0"
}
dependencies {
  implementation("com.github.demidko:aot:2021.10.11")
  implementation("com.github.demidko:channelstorage:2021.10.20")
  implementation("com.github.demidko:time:2021.10.09")
  implementation("com.github.demidko:tokenizer:2021.10.20")
  implementation("ch.qos.logback:logback-classic:1.2.6")
  implementation("com.github.demidko:print-utils:2021.09.03")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
  testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
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
  minHeapSize = "1024m"
  maxHeapSize = "2048m"
  useJUnitPlatform()
}
tasks.jar {
  manifest.attributes("Main-Class" to "ml.demidko.timecobot.AppKt")
}
tasks.shadowJar {
  isZip64 = true
  minimize()
}
tasks.build {
  dependsOn(tasks.shadowJar)
}