package shakerJoystick;

import configuration.Constants;
import subsystems.DriveTrain;

public class Driver extends OverriddenJoystick {
    private DriveTrain.driveType driveMode;

    public Driver(DriveTrain.driveType drt) {
        super(Constants.JOYSTICK_DRIVER_PORT);
        this.driveMode = drt;
    }


    //place driver button layout here
}
