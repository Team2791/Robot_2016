package util;

public class RoboException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RoboException() {}

    public RoboException(String message) {
        super(message);
    }

    public RoboException(Throwable cause) {
        super(cause);
    }

    public RoboException(String message, Object o) {
        super(message + Util.newline + o.toString());
    }
}