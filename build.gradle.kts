plugins {
    //trick: for the same plugin versions in all submodules
    alias(libs.plugins.multiplatform.library).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.compose).apply(false)
}