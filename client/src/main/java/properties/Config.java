package properties;

public class Config {
    private static String host;
    private static int port;

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        Config.host = host;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        Config.port = port;
    }
}
