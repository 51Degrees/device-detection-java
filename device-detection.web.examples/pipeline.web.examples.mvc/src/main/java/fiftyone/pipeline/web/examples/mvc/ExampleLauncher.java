package fiftyone.pipeline.web.examples.mvc;

import org.apache.catalina.LifecycleException;

import static fiftyone.pipeline.web.examples.shared.EmbedTomcat.runWebApp;

public class ExampleLauncher {
    public static void main(String[] args) throws LifecycleException {
        runWebApp(
                "device-detection.web.examples/pipeline.web.examples.mvc/src/main/webapp",
                "device-detection.web.examples/pipeline.web.examples.mvc/target",
                8081);
    }
}
