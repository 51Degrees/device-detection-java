/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2019 51 Degrees Mobile Experts Limited, 5 Charlotte Close,
 * Caversham, Reading, Berkshire, United Kingdom RG4 7BY.
 *
 * This Original Work is licensed under the European Union Public Licence (EUPL)
 * v.1.2 and is subject to its terms as set out below.
 *
 * If a copy of the EUPL was not distributed with this file, You can obtain
 * one at https://opensource.org/licenses/EUPL-1.2.
 *
 * The 'Compatible Licences' set out in the Appendix to the EUPL (as may be
 * amended by the European Commission) shall be deemed incompatible for
 * the purposes of the Work and the provisions of the compatibility
 * clause in Article 5 of the EUPL shall not apply.
 *
 * If using the Work as, or as part of, a network application, by
 * including the attribution notice(s) required under Article 5 of the EUPL
 * in the end user terms of the application under an appropriate heading,
 * such notice(s) shall fulfill the requirements of that article.
 * ********************************************************************* */

package fiftyone.pipeline.web.examples.shared;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/*
 * Create and start embedded Tomcat server for Servlet Web Examples
 */
public class EmbedTomcat {

    public static Tomcat tomcat;

    /**
     * Utility to start Tomcat with the servlet supplied
     * @param servletName a name
     * @param urlPattern a mapping for the addresses the servlet will respond to
     * @param servlet an instantiated servlet
     * @return a server context
     */
    public static Context startTomcat(String servletName, String urlPattern,
                                      HttpServlet servlet, int port,
                                      Map<String, String> initParameters) throws LifecycleException, IOException {
        tomcat = new Tomcat();
        tomcat.setBaseDir(Files.createTempDirectory("tomcat-").toFile().getAbsolutePath());
        tomcat.setPort(port);

        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);

        Wrapper wrapper = tomcat.addServlet(contextPath, servletName, servlet);
        if (Objects.nonNull(initParameters )) {
            for (String key: initParameters.keySet()) {
                wrapper.addInitParameter(key, initParameters.get(key));
            }
        }
        context.addServletMappingDecoded(urlPattern, servletName);

        //initialize webapp classloader
        if (Integer.parseInt(System.getProperty("java.version").split("\\.")[0]) <= 9) {
            context.setParentClassLoader(Thread.currentThread().getContextClassLoader());
            ((StandardContext) context).setDelegate(true);
        }

        tomcat.start();
        return context;
    }

    /**
     * Utility to shut Tomcat down
     */
    public static void stopTomcat() {
        try {
            tomcat.stop();
            tomcat.destroy();
        } catch (LifecycleException e) {
            // expected error on stopping
        }
    }

    /**
     * Utility to run a servlet in Tomcat at the server root address - wait and then shut down
     * @param pathname path to the webapp folder from project root
     * @param workingDir where Tomcat should put its temp files etc
     * @param port the port to listen on
     */
    public static void runWebApp(String pathname, String workingDir, int port) throws LifecycleException {
        System.setProperty("logback.configurationFile", "./logback.xml");
        String contextPath = "";
        String webappDir = new File(pathname).getAbsolutePath();

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(workingDir);
        tomcat.setPort(port);

        tomcat.addWebapp(contextPath, webappDir);

        tomcat.start();
        System.out.format("Browse to http://localhost:%d using a 'private' window in your browser\n" +
                "Hit enter to stop tomcat:", port);
        new Scanner(System.in).nextLine();
        tomcat.stop();
    }

    /**
     * Utility to run a servlet in Tomcat - wait and then shut down
     * @param servlet a servlet
     * @param workingDir where Tomcat should put its temp files etc
     * @param urlPattern where servlet should be releative to server root
     * @param port the port to listen on
     */
    public static void runServlet(HttpServlet servlet, String workingDir, String urlPattern, int port) throws LifecycleException {
        System.setProperty("logback.configurationFile", "./logback.xml");
        tomcat = new Tomcat();
        tomcat.setBaseDir(workingDir);
        tomcat.setPort(port);

        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);

        tomcat.addServlet(contextPath, servlet.getClass().getSimpleName(), servlet);
        context.addServletMappingDecoded(urlPattern, servlet.getClass().getSimpleName());

        //initialize webapp classloader
        if (Integer.parseInt(System.getProperty("java.version").split("\\.")[0]) <= 9) {
            context.setParentClassLoader(Thread.currentThread().getContextClassLoader());
            ((StandardContext) context).setDelegate(true);
        }

        tomcat.start();
        System.out.format("Browse to http://localhost:%d using a 'private' window in your browser\n" +
                "Hit enter to stop tomcat:", port);
        new Scanner(System.in).nextLine();
        tomcat.stop();
    }

}
