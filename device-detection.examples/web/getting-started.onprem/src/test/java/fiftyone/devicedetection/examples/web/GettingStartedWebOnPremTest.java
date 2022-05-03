package fiftyone.devicedetection.examples.web;

import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static fiftyone.devicedetection.examples.web.GettingStartedWebOnPrem.resourceBase;
import static fiftyone.pipeline.util.FileFinder.getFilePath;
import static org.junit.Assert.assertEquals;

public class GettingStartedWebOnPremTest {
    private static Server SERVER;

    @BeforeClass
    public static void startJetty() throws Exception {
        System.setProperty("TestDataFile", getFilePath("device-detection-data/51Degrees-LiteV4.1" +
                ".hash").getAbsolutePath());
        SERVER = EmbedJetty.startWebApp(getFilePath(resourceBase).getAbsolutePath(), 8081);
    }

    @Test
    public void testWebOnPrem() throws Exception {

        HttpURLConnection connection =
                (HttpURLConnection) new URL("http://localhost:8081/").openConnection();

        InputStream response = connection.getInputStream();
        try (Scanner scanner = new Scanner(response)) {
            String responseBody = scanner.useDelimiter("\\A").next();
            System.out.println(responseBody);
        }
        int code = connection.getResponseCode();
        connection.disconnect();
        assertEquals(200, code);
    }

    @AfterClass
    public static void stopJetty() throws Exception {
        SERVER.stop();
    }
}