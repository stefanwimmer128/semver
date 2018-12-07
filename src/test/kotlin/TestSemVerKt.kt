import eu.stefanwimmer128.semver.SemVer
import kotlin.test.*

class TestSemVerKt {
    @Test
    fun test() {
        assertEquals(SemVer.parse("1.0.0-alpha.0+devBuild"), SemVer(1, 0, 0, "alpha.0", "devBuild"))
        assertEquals(SemVer.parse("1.0.0-alpha.0+devBuild"), SemVer(1, 0, 0, listOf("alpha", "0"), listOf("devBuild")))
        assertEquals(SemVer.parse("1.0.0-alpha.0+devBuild"), SemVer.build {
            major = 1
            minor = 0
            patch = 0
            
            preRelease("alpha", "0")
            buildMetadata("devBuild")
        })
        assertEquals(SemVer(1, 2, 3).increase("major"), SemVer(2, 0, 0))
        assertEquals(SemVer(1, 2, 3).increase("minor"), SemVer(1, 3, 0))
        assertEquals(SemVer(1, 2, 3).increase("patch"), SemVer(1, 2, 4))
        assertTrue(SemVer(1, 0, 0) < SemVer(2, 0, 0), "1.0.0 < 2.0.0")
        assertTrue(SemVer(2, 0, 0) > SemVer(1, 0, 0), "2.0.0 > 1.0.0")
        assertTrue(SemVer(0, 1, 0) < SemVer(0, 2, 0), "0.1.0 < 0.2.0")
        assertTrue(SemVer(0, 2, 0) > SemVer(0, 1, 0), "0.2.0 > 0.1.0")
        assertTrue(SemVer(0, 0, 1) < SemVer(0, 0, 2), "0.0.1 < 0.0.2")
        assertTrue(SemVer(0, 0, 2) > SemVer(0, 0, 1), "0.0.2 > 0.0.1")
        assertTrue(SemVer(0, 0, 0, "alpha") < SemVer(0, 0, 0, "beta"), "0.0.0-alpha < 0.0.0-beta")
        assertTrue(SemVer(0, 0, 0, "beta") > SemVer(0, 0, 0, "alpha"), "0.0.0-beta > 0.0.0-alpha")
    }
}
