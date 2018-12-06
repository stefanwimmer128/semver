# SemVer

SemVer data class for kotlin

## How to use?

``` groovy
repositories {
    jcenter()
}

dependencies {
    compile("eu.stefanwimmer.semver:semver:$version")
}
```

``` kt
val version = SemVer.parse("1.0.0-alpha.0+devBuild")
```
