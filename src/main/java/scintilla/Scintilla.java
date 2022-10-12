package scintilla;

public class Scintilla {
    // block initialisation of multiple WebServers
    private Scintilla() { }
    private static final WebServer INSTANCE = new WebServer();

    public static void initialize() {
        INSTANCE.initialize();
    }

    public static void start() {
        INSTANCE.finalize();
        String httpPrefix = (Environment.isSecure() ? "https://" : "http://");
        String ipAddress = "127.0.0.1";
        int port = Environment.getPort();
        PlatformUtils.openBrowserAtPath(httpPrefix + ipAddress + ":" + port + "/app/");
    }
}
