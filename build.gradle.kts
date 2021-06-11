import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	`maven`
	kotlin("jvm") version "1.4.32"
	kotlin("plugin.allopen") version "1.4.20"
	kotlin("plugin.noarg") version "1.4.20"
	kotlin("plugin.serialization") version "1.4.32"
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.MappedSuperclass")
	annotation("javax.persistence.Embeddable")
}

noArg {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.MappedSuperclass")
	annotation("javax.persistence.Embeddable")
	annotation("com.github.nayasis.kotlin.basica.annotation.NoArg")
	invokeInitializers = true
}

group = "com.github.nayasis"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations.all {
	resolutionStrategy.cacheChangingModulesFor(  0, "seconds" )
	resolutionStrategy.cacheDynamicVersionsFor(  5, "minutes" )
}

repositories {
	mavenLocal()
	mavenCentral()
	jcenter()
	maven { url = uri("https://jitpack.io") }
}

dependencies {

	// kotlin
	implementation( "com.github.nayasis:basica-kt:develop-SNAPSHOT" ){ isChanging = true }
	implementation( "org.apache.poi:poi:5.0.0" )
	implementation( "org.apache.poi:poi-ooxml:5.0.0" )
	implementation( "io.github.microutils:kotlin-logging:1.8.3" )

	testImplementation("ch.qos.logback:logback-classic:1.2.3")
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