package shakerJoystick;

import configuration.Constants;

public class Driver {
	ShakerJoystick driverJoystick;
	public Driver(){
		driverJoystick = new ShakerJoystick(Constants.JOYSTICK_DRIVER_PORT);
	}
	//place driver button layout here
}
