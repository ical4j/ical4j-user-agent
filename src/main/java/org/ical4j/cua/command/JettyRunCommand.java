package org.ical4j.cua.command;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.ical4j.cua.ICalendarUserAgentServlet;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(name = "jetty", description = "Start Jetty service for calendar user agent")
public class JettyRunCommand implements Runnable{

    @Override
    public void run() {
        Server server = new Server(8003);
        Connector connector = new ServerConnector(server);
        server.addConnector(connector);

        // Create a ServletContextHandler with contextPath.
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/cua");

        // Add the Servlet implementing the cart functionality to the context.
        ServletHolder servletHolder = context.addServlet(ICalendarUserAgentServlet.class, "/");

        // Link the context to the server.
        server.setHandler(context);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Shutting down the application...");
                server.stop();
                System.out.println("Done, exit.");
            } catch (Exception e) {
                LoggerFactory.getLogger(JettyRunCommand.class.getName()).error("Unexpected error", e);
            }
        }));

        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
