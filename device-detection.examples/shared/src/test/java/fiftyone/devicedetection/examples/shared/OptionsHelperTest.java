package fiftyone.devicedetection.examples.shared;

import fiftyone.devicedetection.shared.testhelpers.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionsHelperTest {

    @Test
    public void findTest() throws Exception {
        OptionsHelper oh = new OptionsHelper(FileUtils.getFilePath("51Degrees-Cloud-Test.xml"));
        assertEquals("!!YOUR_RESOURCE_KEY!!", oh.find("CloudRequestEngine", "ResourceKey"));
    }
    @Test
    public void replaceTest() throws Exception {
        OptionsHelper oh = new OptionsHelper(FileUtils.getFilePath("51Degrees-Cloud-Test.xml"));
        Assert.assertTrue( oh.replace("CloudRequestEngine", "ResourceKey", "something"));
        assertEquals("something", oh.find("CloudRequestEngine", "ResourceKey"));
    }
}