package eu.stefanwimmer128.semver

import java.lang.IllegalArgumentException

data class SemVer(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: List<String> = listOf(),
    val buildMetadata: List<String> = listOf()
): Comparable<SemVer> {
    constructor(
        major: Int,
        minor: Int,
        patch: Int,
        preRelease: String,
        buildMetadata: String = ""
    ): this(
        major,
        minor,
        patch,
        REGEX_IDENTIFIER.findAll(preRelease).map(MatchResult::value).toList(),
        REGEX_IDENTIFIER.findAll(buildMetadata).map(MatchResult::value).toList()
    )
    
    init {
        require(major >= 0) {
            "major version must be non-negative"
        }
        require(minor >= 0) {
            "minor version must be non-negative"
        }
        require(patch >= 0) {
            "patch version must be non-negative"
        }
        
        preRelease.forEach {
            require(it.matches(REGEX_IDENTIFIER)) {
                "malformed preRelease key [$it]"
            }
        }
        
        buildMetadata.forEach {
            require(it.matches(REGEX_IDENTIFIER)) {
                "malformed buildMetadata key [$it]"
            }
        }
    }
    
    fun increase(level: String) =
        when(level.toLowerCase()) {
            "major" -> copy(major = major + 1, minor = 0, patch = 0)
            "minor" -> copy(minor = minor + 1, patch = 0)
            "patch" -> copy(patch = patch + 1)
            else -> throw IllegalArgumentException("malformed semver version increase identifier")
        }
    
    override fun toString() = buildString {
        append("$major.$minor.$patch")
        
        if (preRelease.isNotEmpty())
            append("-${preRelease.joinToString(".")}")
        
        if (buildMetadata.isNotEmpty())
            append("+${buildMetadata.joinToString(".")}")
    }
    
    override fun compareTo(other: SemVer): Int {
        return when {
            major < other.major -> -1
            major > other.major -> 1
            else -> when {
                minor < other.minor -> -1
                minor > other.minor -> 1
                else -> when {
                    patch < other.patch -> -1
                    patch > other.patch -> 1
                    else -> when {
                        toString() < other.toString() -> -1
                        toString() > other.toString() -> 1
                        else -> 0
                    }
                }
            }
        }
    }
    
    companion object {
        @JvmField
        val REGEX_DIGIT = Regex("""(0|[1-9]\d*)""")
        @JvmField
        val REGEX_IDENTIFIER = Regex("""[A-z0-9\-]+""")
        @JvmField
        val REGEX_IDENTIFIERS = Regex("""$REGEX_IDENTIFIER(\.$REGEX_IDENTIFIER)*""")
        @JvmField
        val REGEX_SEMVER = Regex("""$REGEX_DIGIT.$REGEX_DIGIT.$REGEX_DIGIT(-$REGEX_IDENTIFIERS)?(\+$REGEX_IDENTIFIERS)""")
        
        fun build(block: Builder.() -> Unit) =
            Builder().apply(block).build()
        
        @JvmStatic
        fun parse(version: String): SemVer =
            (REGEX_SEMVER.matchEntire(version) ?: throw IllegalArgumentException("malformed semver version string [$version]")).let {
                SemVer(
                    it.groupValues[1].toInt(),
                    it.groupValues[2].toInt(),
                    it.groupValues[3].toInt(),
                    mutableListOf<String>().apply {
                        it.groupValues[4].let {
                            if (it.isNotEmpty())
                                addAll(it.substring(1).split("."))
                        }
                    },
                    mutableListOf<String>().apply {
                        it.groupValues[6].let {
                            if (it.isNotEmpty())
                                addAll(it.substring(1).split("."))
                        }
                    }
                )
            }
    }
    
    data class Builder(
        var major: Int? = null,
        var minor: Int? = null,
        var patch: Int? = null,
        val preRelease: MutableList<String> = mutableListOf(),
        val buildMetadata: MutableList<String> = mutableListOf()
    ) {
        fun major(major: Int) = also {
            it.major = major
        }
        
        fun minor(minor: Int) = also {
            it.minor = minor
        }
        
        fun patch(patch: Int) = also {
            it.patch = patch
        }
        
        fun preRelease(preRelease: String) = also {
            it.preRelease.add(preRelease)
        }
        
        fun buildMetadata(buildMetadata: String) = also {
            it.buildMetadata.add(buildMetadata)
        }
        
        fun build() =
            SemVer(major as Int, minor as Int, patch as Int, preRelease.toList(), buildMetadata.toList())
    }
}
