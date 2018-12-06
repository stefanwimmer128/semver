import eu.stefanwimmer128.semver.SemVer
import org.junit.Assert
import org.junit.Test

class TestSemVerKt {
    @Test
    fun test_SemVer() {
        Assert.assertEquals(SemVer.parse("1.0.0-alpha.0+devBuild"), SemVer(1, 0, 0, "alpha.0", "devBuild"))
        Assert.assertEquals(SemVer.parse("1.0.0-alpha.0+devBuild"), SemVer(1, 0, 0, listOf("alpha", "0"), listOf("devBuild")))
        Assert.assertEquals(SemVer.parse("1.0.0-alpha.0+devBuild"), SemVer.build {
            major = 1
            minor = 0
            patch = 0
            
            preRelease.addAll(listOf("alpha", "0"))
            buildMetadata.add("devBuild")
        })
        Assert.assertEquals(SemVer(1, 2, 3).increase("major"), SemVer(2, 0, 0))
        Assert.assertEquals(SemVer(1, 2, 3).increase("minor"), SemVer(1, 3, 0))
        Assert.assertEquals(SemVer(1, 2, 3).increase("patch"), SemVer(1, 2, 4))
        Assert.assertTrue("1.0.0 < 2.0.0", SemVer(1, 0, 0) < SemVer(2, 0, 0))
        Assert.assertTrue("2.0.0 > 1.0.0", SemVer(2, 0, 0) > SemVer(1, 0, 0))
        Assert.assertTrue("0.1.0 < 0.2.0", SemVer(0, 1, 0) < SemVer(0, 2, 0))
        Assert.assertTrue("0.2.0 > 0.1.0", SemVer(0, 2, 0) > SemVer(0, 1, 0))
        Assert.assertTrue("0.0.1 < 0.0.2", SemVer(0, 0, 1) < SemVer(0, 0, 2))
        Assert.assertTrue("0.0.2 > 0.0.1", SemVer(0, 0, 2) > SemVer(0, 0, 1))
        Assert.assertTrue("0.0.0-alpha < 0.0.0-beta", SemVer(0, 0, 0, "alpha") < SemVer(0, 0, 0, "beta"))
        Assert.assertTrue("0.0.0-beta > 0.0.0-alpha", SemVer(0, 0, 0, "beta") > SemVer(0, 0, 0, "alpha"))
    }
}
