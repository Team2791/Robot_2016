package org.usfirst.frc.team2791.abstractSubsystems;

public abstract class ShakerSubsystem implements Runnable {

    protected static final int updateDelayMs = 1000 / 100; // subsystems run at 100 Hz

    // Runnable impliments a run method which will contain

    // This method should update the smartdashboard with any
    // variables we want to track
    abstract public void updateSmartDash();

    // This method is called in disabled. It should make the robot safe.
    abstract public void disable();

    // This method should print lots of data and outputs lots to the
    // smart dashboard to help with debuggin.
    abstract public void debug();
}
