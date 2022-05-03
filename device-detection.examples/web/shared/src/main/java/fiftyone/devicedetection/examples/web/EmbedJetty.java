package fiftyone.devicedetection.examples.web;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.Servlet;
import java.util.Map;
import java.util.Scanner;

public class EmbedJetty {
    public static void runWebApp(String resourceBase, int port) throws Exception {
        Server server = startWebApp(resourceBase, port);
        System.out.format("Browse to http://localhost:%d using a 'private' window in your browser\n" +
                "Hit enter to stop server:", port);
        new Scanner(System.in).nextLine();
        server.stop();
    }

    public static Server startWebApp(String resourceBase, int port) throws Exception {
        Server server = new Server(port);
        Connector connector = new ServerConnector(server);
        server.addConnector(connector);

        // Create a WebAppContext.
        WebAppContext context = new WebAppContext();
        // Configure the path of the packaged web application (file or directory).
        context.setResourceBase(resourceBase);
        context.setDescriptor("WEB-INF/web.xml");

        // Link the context to the server.
        server.setHandler(context);

        server.start();
        return server;
    }

    public static void runServlet(String contextPath, int port, Class<? extends Servlet> servlet,
                                  Map<String, String> initParams) throws Exception {
        Server server = startServlet(contextPath, port, servlet, initParams);
        System.out.format("Browse to http://localhost:%d using a 'private' window in your browser\n" +
                "Hit enter to stop server:", port);
        new Scanner(System.in).nextLine();
        server.stop();
    }

    public static Server startServlet(String contextPath, int port,
                                      Class<? extends Servlet> servlet,
                                      Map<String, String> initParams) throws Exception {
        Server server = new Server(port);
        Connector connector = new ServerConnector(server);
        server.addConnector(connector);

        // Create a ServletContextHandler with contextPath.
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setSessionHandler(new SessionHandler());
        context.setContextPath("/");

        // Add the Servlet to the context.
        ServletHolder servletHolder = context.addServlet(servlet, contextPath);
        // Configure the Servlet with init-parameters.
        servletHolder.setInitParameters(initParams);

        // Link the context to the server.
        server.setHandler(context);

        server.start();
        return server;
    }
}
