package fiftyone.devicedetection.shared.testhelpers;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class FileUtilsTest {

    Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * location of "." is the same as user.dir
     **/
    @Test()
    public void testUserDir() throws IOException {
        File f = new File("./");
        logger.info(". is {}", f.getCanonicalPath());
        File g = new File(System.getProperty("user.dir"));
        logger.info("user.dir is {}", g.getCanonicalPath());
        assertEquals(". and user dir should be the same", f.getCanonicalPath(), g.getCanonicalPath());
    }


}