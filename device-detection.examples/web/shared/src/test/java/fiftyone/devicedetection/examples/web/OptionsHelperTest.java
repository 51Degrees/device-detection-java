package fiftyone.devicedetection.examples.web;

import fiftyone.devicedetection.shared.testhelpers.FileUtils;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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
        assertTrue( oh.replace("CloudRequestEngine", "ResourceKey", "something"));
        assertEquals("something", oh.find("CloudRequestEngine", "ResourceKey"));
    }
}