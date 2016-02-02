package org.usfirst.frc.team2791.shakerJoystick;

import org.usfirst.frc.team2791.configuration.Constants;

public class Driver extends OverriddenJoystick {
    public Driver() {
        super(Constants.JOYSTICK_DRIVER_PORT);
    }

    public double getGtaDriveLeft() {
        double combined = super.getAxisRightX() + super.getAxisRT() - super.getAxisLT();
        if (combined > 1.0) return 1.0;
        else if (combined < -1.0) return -1.0;
        return combined;

    }

    public double getGtaDriveRight() {
        double combined = -super.getAxisRightX() + super.getAxisRT() - super.getAxisLT();
        if (combined > 1.0) return 1.0;
        else if (combined < -1.0) return -1.0;
        return combined;
    }


    //place driver button layout here
}
