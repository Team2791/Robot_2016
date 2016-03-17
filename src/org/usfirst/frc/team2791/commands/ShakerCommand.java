package org.usfirst.frc.team2791.commands;

/**
 * Created by Akhil on 3/7/2016.
 */
public abstract class ShakerCommand {// template class for commands

    static void reset() {
    }

    static void run() {
    }

    static boolean isRunning() {
        return false;
    }

    abstract public void updateSmartDash();

    abstract public void debug();
}
