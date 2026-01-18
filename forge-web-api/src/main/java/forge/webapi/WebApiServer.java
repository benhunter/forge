package forge.webapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;

public final class WebApiServer {
    private static final Logger LOG = Logger.getLogger(WebApiServer.class.getName());

    private WebApiServer() {
    }

    public static void main(String[] args) throws Exception {
        WebApiConfig config = WebApiConfig.fromArgs(args);
        configureLogging(config.logLevel());

        LOG.info(() -> String.format("Starting Forge Web API on %s:%d", config.bindAddress(), config.port()));

        Server server = new Server(new InetSocketAddress(config.bindAddress(), config.port()));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        if (!config.corsAllowedOrigins().isBlank()) {
            FilterHolder cors = new FilterHolder(newCorsFilter());
            cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, config.corsAllowedOrigins());
            cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,OPTIONS");
            cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Content-Type,Accept,Origin");
            cors.setInitParameter(CrossOriginFilter.PREFLIGHT_MAX_AGE_PARAM, "1800");
            context.addFilter(cors, "/*", EnumSet.of(DispatcherType.REQUEST));
        }

        context.addServlet(new ServletHolder(new HealthServlet()), "/health");
        context.addServlet(new ServletHolder(new VersionServlet()), "/version");

        server.setHandler(context);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.stop();
                LOG.info("Forge Web API stopped.");
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Failed to stop server cleanly", e);
            }
        }));

        server.start();
        server.join();
    }

    private static Filter newCorsFilter() {
        return new CrossOriginFilter();
    }

    private static void configureLogging(Level level) {
        Logger root = Logger.getLogger("");
        for (Handler handler : root.getHandlers()) {
            root.removeHandler(handler);
        }
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        root.addHandler(handler);
        root.setLevel(level);
    }

    private static final class HealthServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/plain");
            resp.getWriter().write("ok");
        }
    }

    private static final class VersionServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"version\":\"" + currentVersion() + "\"}");
        }

        private String currentVersion() {
            Package pkg = WebApiServer.class.getPackage();
            if (pkg != null && pkg.getImplementationVersion() != null) {
                return pkg.getImplementationVersion();
            }
            return "unknown";
        }
    }

    private record WebApiConfig(int port, String bindAddress, String corsAllowedOrigins, Level logLevel) {
        private static final int DEFAULT_PORT = 8080;
        private static final String DEFAULT_BIND = "0.0.0.0";
        private static final String DEFAULT_CORS = "*";
        private static final Level DEFAULT_LOG_LEVEL = Level.INFO;

        private static WebApiConfig fromArgs(String[] args) throws IOException {
            Objects.requireNonNull(args, "args");
            Path configPath = null;
            Integer port = null;
            String bindAddress = null;
            String corsAllowedOrigins = null;
            Level logLevel = null;

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg) {
                    case "--help" -> {
                        printUsage();
                        System.exit(0);
                    }
                    case "--config" -> {
                        configPath = Path.of(requireValue(args, ++i, "--config"));
                    }
                    case "--port" -> {
                        port = Integer.parseInt(requireValue(args, ++i, "--port"));
                    }
                    case "--bind" -> {
                        bindAddress = requireValue(args, ++i, "--bind");
                    }
                    case "--cors" -> {
                        corsAllowedOrigins = requireValue(args, ++i, "--cors");
                    }
                    case "--log-level" -> {
                        logLevel = parseLevel(requireValue(args, ++i, "--log-level"));
                    }
                    default -> throw new IllegalArgumentException("Unknown argument: " + arg);
                }
            }

            Properties props = new Properties();
            if (configPath != null) {
                try (InputStream in = Files.newInputStream(configPath)) {
                    props.load(in);
                }
            }

            int resolvedPort = port != null ? port : parseInt(props.getProperty("port"), DEFAULT_PORT);
            String resolvedBind = bindAddress != null ? bindAddress : props.getProperty("bindAddress", DEFAULT_BIND);
            String resolvedCors = corsAllowedOrigins != null ? corsAllowedOrigins : props.getProperty("corsAllowedOrigins", DEFAULT_CORS);
            Level resolvedLevel = logLevel != null ? logLevel : parseLevel(props.getProperty("logLevel", DEFAULT_LOG_LEVEL.getName()));

            return new WebApiConfig(resolvedPort, resolvedBind, resolvedCors, resolvedLevel);
        }

        private static String requireValue(String[] args, int index, String flag) {
            if (index >= args.length) {
                throw new IllegalArgumentException("Missing value for " + flag);
            }
            return args[index];
        }

        private static int parseInt(String value, int fallback) {
            if (value == null || value.isBlank()) {
                return fallback;
            }
            return Integer.parseInt(value.trim());
        }

        private static Level parseLevel(String value) {
            if (value == null || value.isBlank()) {
                return DEFAULT_LOG_LEVEL;
            }
            String normalized = value.trim().toUpperCase(Locale.ROOT);
            return Level.parse(normalized);
        }

        private static void printUsage() {
            System.out.println("Forge Web API server");
            System.out.println("Usage: java -jar forge-web-api.jar [options]");
            System.out.println("  --config <path>     Path to properties file");
            System.out.println("  --port <port>       Port to bind (default 8080)");
            System.out.println("  --bind <address>    Bind address (default 0.0.0.0)");
            System.out.println("  --cors <origins>    CORS allowed origins (default *)");
            System.out.println("  --log-level <lvl>   JUL log level (default INFO)");
        }
    }
}
