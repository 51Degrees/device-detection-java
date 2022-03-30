package fiftyone.devicedetection.examples.console;

import fiftyone.devicedetection.shared.testhelpers.FileUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.Objects;

public class OfflineProcessingTest {
    @Test
    public void offlineProcessingTest() throws Exception {
        OfflineProcessing.run(FileUtils.LITE_HASH_DATA_FILE_NAME,
                new FileInputStream(Objects.requireNonNull(FileUtils.getEvidenceFile())),
                System.out);
    }

}