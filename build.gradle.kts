import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	`java`
	`maven-publish`
	kotlin("jvm") version "1.8.10"
	kotlin("plugin.noarg") version "1.8.10"
}

noArg {
	annotation("com.github.nayasis.kotlin.basica.annotation.NoArg")
	invokeInitializers = true
}

group = "com.github.nayasis"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations.all {
	resolutionStrategy.cacheChangingModulesFor(0, "seconds")
	resolutionStrategy.cacheDynamicVersionsFor(  5, "minutes" )
}

java {
	registerFeature("support") {
		usingSourceSet(sourceSets["main"])
	}
	withJavadocJar()
	withSourcesJar()
}

repositories {
	mavenLocal()
	mavenCentral()
	maven { url = uri("https://jitpack.io") }
}

dependencies {

	// kotlin
	implementation("com.github.nayasis:basica-kt:0.2.18")
	implementation("org.apache.poi:poi:5.2.2")
	implementation("org.apache.poi:poi-ooxml:5.2.2")
	implementation("com.opencsv:opencsv:5.7.1")
	implementation("io.github.microutils:kotlin-logging:3.0.5")

	testImplementation("ch.qos.logback:logback-classic:1.3.5")
	testImplementation("org.apache.logging.log4j:log4j-core:2.19.0")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.3.1")

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