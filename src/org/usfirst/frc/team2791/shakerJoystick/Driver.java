package org.usfirst.frc.team2791.shakerJoystick;

import org.usfirst.frc.team2791.util.Constants;

public class Driver extends OverriddenJoystick {
    public Driver() {
        super(Constants.JOYSTICK_DRIVER_PORT);
    }

    public double getGtaDriveLeft() {
        // Does the math to get Gta Drive Type on left motor
        double leftAxis;
        if (super.getAxisLeftX() < 0)
            leftAxis = -Math.pow(super.getAxisLeftX(), 2);
        else
            leftAxis = Math.pow(super.getAxisLeftX(), 2);
        double combined = leftAxis + super.getAxisRT() - super.getAxisLT();
        if (combined > 1.0)
            return 1.0;
        else if (combined < -1.0)
            return -1.0;
        return combined;

    }

    public double getGtaDriveRight() {
        // Does the math to get Gta Drive Type on right motor
        double leftAxis;
        if (super.getAxisLeftX() < 0)
            leftAxis = -Math.pow(super.getAxisLeftX(), 2);
        else
            leftAxis = Math.pow(super.getAxisLeftX(), 2);
        double combined = -leftAxis + super.getAxisRT() - super.getAxisLT();
        if (combined > 1.0)
            return 1.0;
        else if (combined < -1.0)
            return -1.0;
        return combined;
    }

    // place driver button layout here
}
