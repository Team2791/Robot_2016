package org.usfirst.frc.team2791.util;

public class RoboClock {
    private long timeWhenLastStarted;
    private long timeWhenLastPaused;
    private long totalTime;
    private long runTime;
    private long pausedTime;
    private long currentSystemTime;
    private long previousSystemTime;
    private boolean running = false;
    private String name = "";

    public RoboClock() {
        resetAndPause();
    }

    public void resetAndStart() {
        reset();
        start();
    }

    public void resetAndPause() {
        reset();
        pause();
    }

    public void start() {
        running = true;
    }

    public void pause() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    private void reset() {
        timeWhenLastPaused = 0;
        totalTime = 0;
        runTime = 0;
        pausedTime = 0;
        previousSystemTime = 0;
        timeWhenLastStarted = System.currentTimeMillis();
        currentSystemTime = System.currentTimeMillis();
    }

    private void updateSystemTimes() {
        previousSystemTime = currentSystemTime;
        currentSystemTime = System.currentTimeMillis();

        if (!running && totalTime == 0) {
            return;
        } else if (!running) {
            pausedTime += currentSystemTime - previousSystemTime;
        } else if (running) {
            runTime += currentSystemTime - previousSystemTime;
        }

        totalTime = pausedTime + runTime;
    }

    public void setName(String s) {
        name = s;
    }

    public double getPausedTime() {
        updateSystemTimes();
        return pausedTime / 1000.0;
    }

    public double getRuntime() {
        updateSystemTimes();
        return runTime / 1000.0;
    }

    public double getTotalTime() {
        updateSystemTimes();
        return totalTime / 1000.0;
    }

    public double getTimeAtLastStart() {
        updateSystemTimes();
        return timeWhenLastStarted / 1000.0;
    }

    public double getTimeAtLastPause() {
        updateSystemTimes();
        return timeWhenLastPaused / 1000.0;
    }

    public String toString() {
        updateSystemTimes();
        String s = "";

        s += Logger.buildLine(name, 0);
        if (running) {
            s += Logger.buildLine("Timer running...", 1);
            s += Logger.buildLine("Runtime: " + runTime / 1000.0, 1);
        } else {
            s += Logger.buildLine("Timer paused.", 1);
            s += Logger.buildLine("Pause time: " + pausedTime / 1000.0, 1);
        }

        return s;
    }
}