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
	maven { url = uri("https://raw.github.com/nayasis/maven-repo/mvn-repo") }
	maven { url = uri("https://jitpack.io") }
}

dependencies {

	// kotlin
	implementation( "com.github.nayasis:basica-kt:develop-SNAPSHOT" ){ isChanging = true }
//	implementation( "com.github.nayasis:basica-kt:develop-010d28a0ca-1" )
//	implementation( "com.github.nayasis:basica-kt:develop-010d28a0ca-1" )
	implementation( "org.apache.poi:poi:5.0.0" )
	implementation( "org.apache.poi:poi-ooxml:5.0.0" )

//	implementation("org.jetbrains.kotlin:kotlin-reflect")
//	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
//	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
	implementation( "io.github.microutils:kotlin-logging:1.8.3" )
//	implementation("au.com.console:kassava:2.1.0-rc.1")

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