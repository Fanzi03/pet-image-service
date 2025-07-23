plugins {
	java
	application
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

application{
	mainClass.set("com.example.PetImageServiceApplication")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.kafka:spring-kafka")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")

	//test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.springframework.boot:spring-boot-testcontainers:3.5.3")
  //	testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.3"))
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.mockito:mockito-junit-jupiter:5.18.0")
  testImplementation("org.mockito:mockito-core:5.18.0")
  testImplementation("org.assertj:assertj-core:3.27.3")
  testImplementation("org.testcontainers:kafka")

}

tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs = listOf("-Dspring.config.import=optional:dotenv")
}
