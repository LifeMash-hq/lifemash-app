import org.bmsk.lifemash.configureVerifyDetekt

configureVerifyDetekt()

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    // Target version of the generated JVM bytecode. It is used for type resolution.
    jvmTarget = JavaVersion.VERSION_17.majorVersion

    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    parallel = true
    config.setFrom(listOf(file("$rootDir/config/detekt/detekt.yml"))) // point to your custom config defining rules to run, overwriting default behavior

    reports {
        rootProject.layout.projectDirectory.dir("build/reports/test/${project.name}").asFile.mkdirs()
        html.required.set(true) // observe findings in your browser with structure and code snippets
        html.outputLocation.set(rootProject.layout.projectDirectory.file("build/reports/detekt/${project.name}.html").asFile)
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        xml.outputLocation.set(rootProject.layout.projectDirectory.file("build/reports/detekt/${project.name}.xml").asFile)
    }
}
