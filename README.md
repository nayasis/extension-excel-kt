# extension-excel-kt

Microsoft Excel reader / writer 

[![](https://jitpack.io/v/nayasis/extension-excel-kt.svg)](https://jitpack.io/#nayasis/extension-excel-kt)

## Dependency

### maven

1. add repository in **pom.xml**.

```xml
<repositories>
  <repository>
    <id>jitpack</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

2. add dependency in **pom.xml**.

```xml
<dependency>
  <groupId>com.github.nayasis</groupId>
  <artifactId>extension-excel-kt</artifactId>
  <version>x.y.z</version>
</dependency>
```

### gradle

1. add repository in **build.gradle.kts**.

```kotlin
repositories {
  maven { url = uri("https://jitpack.io") }
}
```

2. add dependency in **build.gradle.kts**.

```kotlin
dependencies {
  implementation( "com.github.nayasis:extension-excel-kt:x.y.z" )
}
```