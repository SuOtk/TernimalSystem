package terminal.spectre.com.terminalsystem.http;

/**
 * Created by SPECTRE on 2016/7/8.
 */

public class HttpParser {
    static{
        System.loadLibrary("http-parse");
    }

    public static native String parseHttpRequest(String http);

}