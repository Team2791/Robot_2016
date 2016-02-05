package org.usfirst.frc.team2791.util;

import org.jetbrains.annotations.NotNull;

public class RoboException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RoboException() {
    }

    public RoboException(String message) {
        super(message);
    }

    public RoboException(Throwable cause) {
        super(cause);
    }

    public RoboException(String message, @NotNull Object o) {
        super(message + Util.newline + o.toString());
    }
}