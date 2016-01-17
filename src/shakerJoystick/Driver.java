package shakerJoystick;

public class Driver {
	ShakerJoystick driverJoystick;
	public Driver(){
		driverJoystick = new ShakerJoystick(Configuration.Joystick.DriverPort);
	}
	//place driver button layout here
}
