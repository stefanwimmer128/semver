package eu.stefanwimmer128.semver

import java.lang.IllegalArgumentException

data class SemVer @JvmOverloads constructor(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: List<String> = listOf(),
    val buildMetadata: List<String> = listOf()
): Comparable<SemVer> {
    @JvmOverloads constructor(
        major: Int,
        minor: Int,
        patch: Int,
        preRelease: String,
        buildMetadata: String = ""
    ): this(
        major,
        minor,
        patch,
        if (preRelease.isEmpty()) listOf() else preRelease.split("."),
        if (buildMetadata.isEmpty()) listOf() else buildMetadata.split(".")
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
            require(it matches REGEX_IDENTIFIER) {
                "malformed preRelease key [$it]"
            }
        }
        
        buildMetadata.forEach {
            require(it matches REGEX_IDENTIFIER) {
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
    
    override fun toString() =
        buildString {
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
                    else ->toString().let { me ->
                        other.toString().let { other ->
                            when {
                                me < other -> -1
                                me > other -> 1
                                else -> 0
                            }
                        }
                    }
                }
            }
        }
    }
    
    companion object {
        @JvmField val REGEX_DIGIT = Regex("""(0|[1-9]\d*)""")
        @JvmField val REGEX_IDENTIFIER = Regex("""[A-z0-9\-]+""")
        @JvmField val REGEX_IDENTIFIERS = Regex("""$REGEX_IDENTIFIER(\.$REGEX_IDENTIFIER)*""")
        @JvmField val REGEX_SEMVER = Regex("""$REGEX_DIGIT.$REGEX_DIGIT.$REGEX_DIGIT(-$REGEX_IDENTIFIERS)?(\+$REGEX_IDENTIFIERS)""")
        
        fun build(block: Builder.() -> Unit) =
            Builder(block).build()
        
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
    
    class Builder @JvmOverloads constructor(
        var major: Int? = null,
        var minor: Int? = null,
        var patch: Int? = null,
        var preRelease: MutableList<String> = mutableListOf(),
        var buildMetadata: MutableList<String> = mutableListOf()
    ) {
        constructor(block: Builder.() -> Unit): this() {
            block()
        }
        
        fun major(major: Int) =
            also {
                it.major = major
            }
        
        fun minor(minor: Int) =
            also {
                it.minor = minor
            }
        
        fun patch(patch: Int) =
            also {
                it.patch = patch
            }
        
        fun preRelease(vararg preRelease: String) =
            also {
                it.preRelease.addAll(preRelease.joinToString(".").split("."))
            }
        
        fun buildMetadata(vararg buildMetadata: String) =
            also {
                it.buildMetadata.addAll(buildMetadata.joinToString(".").split("."))
            }
        
        fun build() =
            SemVer(major as Int, minor as Int, patch as Int, preRelease.toList(), buildMetadata.toList())
        
        operator fun MutableList<String>.invoke(block: StringList.() -> Unit) =
            StringList(this).block()
        
        class StringList(list: MutableList<String>): MutableList<String> by list {
            operator fun String.unaryPlus() =
                add(this)
            
            operator fun String.unaryMinus() =
                remove(this)
        }
    }
}
