import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	`java`
	`maven-publish`
	kotlin("jvm") version "1.9.20"
	kotlin("plugin.noarg") version "1.9.20"
}

group = "com.github.nayasis"
version = "0.2.3-SNAPSHOT"
configurations.all {
	resolutionStrategy.cacheChangingModulesFor(0, "seconds")
	resolutionStrategy.cacheDynamicVersionsFor(  5, "minutes" )
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
	withJavadocJar()
	withSourcesJar()
}

repositories {
	mavenLocal()
	mavenCentral()
	maven { url = uri("https://jitpack.io") }
}

dependencies {
	implementation("com.github.nayasis:basica-kt:0.3.1")
	implementation("org.apache.poi:poi:5.2.2")
	implementation("org.apache.poi:poi-ooxml:5.2.2")
	implementation("com.opencsv:opencsv:5.7.1")
	implementation("io.github.microutils:kotlin-logging:3.0.5")

	testImplementation("ch.qos.logback:logback-classic:1.4.6")
	testImplementation("org.apache.logging.log4j:log4j-core:2.20.0")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf(
			"-Xjsr305=strict"
		)
		jvmTarget = "1.8"
	}
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])
		}
	}
}