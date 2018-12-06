import eu.stefanwimmer128.semver.SemVer;
import org.junit.Assert;
import org.junit.Test;

public class TestSemVerJava {
    @Test
    public void javaTest_SemVer() {
        SemVer semver = new SemVer.Builder()
            .major(1)
            .minor(0)
            .patch(0)
            .preRelease("alpha")
            .preRelease("0")
            .buildMetadata("devBuild")
            .build();
        Assert.assertEquals(semver, SemVer.parse("1.0.0-alpha.0+devBuild"));
    }
}
