package properties;

public class NetworkProperties {
    private static String host;
    private static int port;
    private static String login;
    private static String userName;
    private static int authCount = 0;
    private static boolean authRequestSent = false;
    private static boolean authSuccess = false;

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        NetworkProperties.host = host;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        NetworkProperties.port = port;
    }

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        NetworkProperties.login = login;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        NetworkProperties.userName = userName;
    }

    public static int getAuthCount() {
        return authCount;
    }

    public static void incAuthCount() {
        NetworkProperties.authCount++;
    }

    public static boolean isAuthRequestSent() {
        return authRequestSent;
    }

    public static void setAuthRequestSent(boolean authRequestSent) {
        NetworkProperties.authRequestSent = authRequestSent;
    }

    public static boolean isAuthSuccess() {
        return authSuccess;
    }

    public static void setAuthSuccess(boolean authSuccess) {
        NetworkProperties.authSuccess = authSuccess;
    }
}
