package org.usfirst.frc.team2791.helpers;

import static org.usfirst.frc.team2791.robot.Robot.driveTrain;

/**
 * Created by Akhil on 1/28/2016.
 */
public class AutonHelper extends ShakerHelper {
    private int counter;

    public void run() {
        switch (counter) {
            case 0:
                if (driveTrain.driveInFeet(1.0)) {
                    counter++;
                }
            case 1:
                driveTrain.disable();

        }
    }

    @Override
    public void disableRun() {
        driveTrain.disable();
    }

    @Override
    public void updateSmartDash() {

    }

    @Override
    public void reset() {

    }
}
