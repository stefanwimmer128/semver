import eu.stefanwimmer128.semver.SemVer;
import org.junit.*;

import java.util.Arrays;

public class TestSemVerJava {
    @Test
    public void test() {
        SemVer semver = new SemVer.Builder()
            .major(1)
            .minor(0)
            .patch(0)
            .preRelease("alpha.0")
            .buildMetadata("devBuild")
            .build();
        Assert.assertEquals(semver, SemVer.parse("1.0.0-alpha.0+devBuild"));
        Assert.assertEquals(semver, new SemVer(1, 0, 0, "alpha.0", "devBuild"));
        Assert.assertEquals(semver, new SemVer.Builder(1, 0, 0, Arrays.asList("alpha", "0"), Arrays.asList("devBuild")).build());
    }
}
