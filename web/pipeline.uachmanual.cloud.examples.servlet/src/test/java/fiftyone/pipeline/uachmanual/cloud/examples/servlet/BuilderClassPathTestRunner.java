package fiftyone.pipeline.uachmanual.cloud.examples.servlet;

import org.glassfish.jaxb.runtime.v2.ContextFactory;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized;
import org.junit.runners.model.InitializationError;

import fiftyone.devicedetection.cloud.flowelements.DeviceDetectionCloudEngineBuilder;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngineBuilder;
import fiftyone.pipeline.engines.fiftyone.flowelements.SetHeadersElementBuilder;
import jakarta.xml.bind.JAXBContext;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class BuilderClassPathTestRunner extends Parameterized {

    static ClassLoader classLoaderWithBuidlers;

    public BuilderClassPathTestRunner(Class<?> klass) throws Throwable {
        super(loadFromCustomClassloader(klass));
    }

    private static Class<?> loadFromCustomClassloader(Class<?> clazz) throws InitializationError {
        try {
            // Only load once to support parallel tests
            if (classLoaderWithBuidlers == null) {
                classLoaderWithBuidlers = new ClassLoaderWithBuilders();
            }
            return Class.forName(clazz.getName(), true, classLoaderWithBuidlers);
        } catch (ClassNotFoundException e) {
            throw new InitializationError(e);
        }
    }


    @Override
    public void run(final RunNotifier notifier) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BuilderClassPathTestRunner.super.run(notifier);
            }
        };
        Thread thread = new Thread(runnable);
        thread.setContextClassLoader(classLoaderWithBuidlers);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    // Custom class loader.
    // Loads classes that match pattern, otherwise load from context loader
    public static class ClassLoaderWithBuilders extends URLClassLoader {

        ClassLoader parent = null;

        public ClassLoaderWithBuilders() {

            super(getClasspathUrls(), null);

            parent = Thread.currentThread()
                    .getContextClassLoader();

        }

        @Override
        public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
            return parent.loadClass(name);
        }


        private static URL[] getClasspathUrls() {
            ArrayList<URL> classpathUrls = new ArrayList<>();
            classpathUrls.add(CloudRequestEngineBuilder.class.getProtectionDomain().getCodeSource().getLocation());
            classpathUrls.add(DeviceDetectionCloudEngineBuilder.class.getProtectionDomain().getCodeSource().getLocation());
            classpathUrls.add(SetHeadersElementBuilder.class.getProtectionDomain().getCodeSource().getLocation());
            if (Integer.parseInt(System.getProperty("java.version").split("\\.")[0]) >= 9) {
                classpathUrls.add(JAXBContext.class.getProtectionDomain().getCodeSource().getLocation());
                classpathUrls.add(ContextFactory.class.getProtectionDomain().getCodeSource().getLocation());
            }
            return classpathUrls.toArray(new URL[classpathUrls.size()]);
        }
    }
}
