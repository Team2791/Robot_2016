package org.usfirst.frc.team2791.util;

import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.robot.Robot;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Logger {

    private static File logFile = null;

    private static Logger loggerInstance = null;
    private static boolean enabled = false;
    private static double lastLogTime;

    private Logger() {
        logFile = findFileHome();
    }

    public static Logger getInstance() {
        if (loggerInstance == null) {
            loggerInstance = new Logger();
            lastLogTime = 0;
        }

        return loggerInstance;
    }

    private static String getLogTimerString() {
        RoboClock power = Robot.getPowerTimer();
        RoboClock mode = Robot.getCurrentModeTimer();
        String line = "";

        if (power.getRuntime() - lastLogTime >= Constants.CODE_EXECUTION_PERIOD) {
            line += power.getTotalTime() + " (";
            line += mode == null ? "null" : "" + mode.getRuntime();
            line += ")";
            lastLogTime = power.getRuntime();
        }

        line += Util.newline;
        return line;
    }

    public static void exception(Throwable t) {
        if (enabled) {
            try {
                BufferedWriter loggerWriter = new BufferedWriter(new FileWriter(logFile.getAbsoluteFile(), true));
                PrintWriter pw = new PrintWriter(loggerWriter, true);
                pw.append(getLogTimerString()).append("\t");
                t.printStackTrace(pw);
            } catch (IOException e) {
                e.printStackTrace();
                stopLogger();
                System.out.print(getLogTimerString() + "\t");
                System.out.print(Arrays.toString(t.getStackTrace()));
            }
        } else {
            System.out.print(getLogTimerString() + "\t");
            System.out.print(Arrays.toString(t.getStackTrace()));
        }
    }

    public static void write(Object event) {
        if (enabled) {
            try {
                BufferedWriter loggerWriter = new BufferedWriter(new FileWriter(logFile.getAbsoluteFile(), true));
                PrintWriter pw = new PrintWriter(loggerWriter, true);
                pw.append(getLogTimerString()).append(event.toString()).append(Util.newline);
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
                stopLogger();
                System.out.print(getLogTimerString());
                System.out.print(event.toString());
            }
        } else {
            System.out.print(getLogTimerString());
            System.out.print(event.toString());
        }
    }

    public static String buildLine(String message, int level) {
        if (message.indexOf(Util.repeatString("\t", 2)) != 0) {
            message = Util.repeatString("\t", 2) + message;
        }
        message = message.replace(Util.newline, Util.newline + Util.repeatString("\t", 2));
        message = Util.truncateLastTerm(message, Util.newline);
        return Util.repeatString("\t", level) + message + Util.newline;
    }

    public static void startLogger() {
        loggerInstance = getInstance();
        enabled = true;
    }

    public static void stopLogger() {
        enabled = false;
    }

    private File findFileHome() {
        // logs are stored in "/home/lvuser/logs"
        SimpleDateFormat sdf = new SimpleDateFormat("EEE_MMM-dd_hh-mm-ss");
        Date runtime = new Date(System.currentTimeMillis());
        String fName = "robolog_" + sdf.format(runtime) + ".txt";
        String fDir = System.getProperty("user.home");
        return new File(fDir + "/logs/" + fName);
    }
}
