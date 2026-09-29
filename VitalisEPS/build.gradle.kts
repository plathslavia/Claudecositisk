plugins {
    // Se declaran aquí con `apply false` para que Gradle cargue cada plugin una sola vez.
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}
