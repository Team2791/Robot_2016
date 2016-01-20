package subsystems;

import shakerJoystick.Driver;
import shakerJoystick.Operator;
import shakerJoystick.ShakerJoystick;

public interface Subsystems {
	
	  void init();

	  void initTeleop();

	  void initDisabled();

	  void initAutonomous();

	  void runTeleop();

	  void runDisabled();

	  void runAutonomous();

	  void reset();

}
