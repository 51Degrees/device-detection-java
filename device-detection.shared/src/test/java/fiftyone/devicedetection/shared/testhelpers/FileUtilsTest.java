package fiftyone.devicedetection.shared.testhelpers;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static fiftyone.devicedetection.shared.testhelpers.FileUtils.*;
import static org.junit.Assert.*;

public class FileUtilsTest {

    Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * non existent
     **/
    @Test(expected = Exception.class)
    public void testGetFilePathNonExist() {
        getFilePath("xyzw");
    }

    /**
     * exists
     **/
    @Test()
    public void testGetFilePathExist() {
        File f = getFilePath(UA_FILE_NAME);
        logger.info(f.getAbsolutePath());
        Assert.assertTrue(UA_FILE_NAME.endsWith(f.getName()));
    }

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