package subsystems;

<<<<<<< HEAD
=======
import configuration.*;
>>>>>>> branch 'master' of https://github.com/Team2791/robot_2016.git
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;
<<<<<<< HEAD
import util.Configuration;
=======
import edu.wpi.first.wpilibj.vision.AxisCamera;
import shakerJoystick.ShakerJoystick;
import util.AnalyzeCamera;
import subsystems.DriveTrainAutonHelper;
>>>>>>> branch 'master' of https://github.com/Team2791/robot_2016.git

/**
 *
 */
<<<<<<< HEAD
public class DriveTrain{
    
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
=======
public class DriveTrain implements Subsystems {
	private static Talon leftTalonA;
	private static Talon leftTalonB;
	private static Talon rightTalonA;
	private static Talon rightTalonB;
	private static shakerJoystick.Driver DriverJoystick;
	private static shakerJoystick.Operator OperatorJoystick;
	private static AxisCamera cam;
	private static DriveTrainAutonHelper DTAH;
>>>>>>> branch 'master' of https://github.com/Team2791/robot_2016.git

	public void init() {
	}

	public void init(shakerJoystick.Driver driveJoy, shakerJoystick.Operator opJoy) {
		// TODO Auto-generated method stub
<<<<<<< HEAD
		//instantiated speed controller here 
		Talon leftDriveA = new Talon(Configuration.leftDrivePortA);
		Talon leftDriveB = new Talon(Configuration.leftDrivePortB);
		Talon rightDriveA = new Talon(Configuration.rightDrivePortA);
		Talon rightDriveB = new Talon(Configuration.rightDrivePortB);
=======
		// instantiated speed controller here
		DriverJoystick = driveJoy;
		OperatorJoystick = opJoy;
		leftTalonA = new Talon(Ports.leftTalonPortA);
		leftTalonB = new Talon(Ports.leftTalonPortB);
		rightTalonA = new Talon(Ports.rightTalonPortA);
		rightTalonB = new Talon(Ports.rightTalonPortB);
>>>>>>> branch 'master' of https://github.com/Team2791/robot_2016.git
	}


	public void initTeleop() {
		// TODO Auto-generated method stub

	}


	public void initDisabled() {
		// TODO Auto-generated method stub

	}

	
	public void initAutonomous() {
<<<<<<< HEAD

		// TODO Auto-generated method stub
		
=======
		cam = new AxisCamera(Camera.cameraPort);
		DTAH = new DriveTrainAutonHelper(cam);
>>>>>>> branch 'master' of https://github.com/Team2791/robot_2016.git
	}
	
	public void shift_up(){
		
	}
	
	public void shift_down(){
		
	}
	

	public void runTeleop() {
		// TODO Auto-generated method stub
<<<<<<< HEAD
//		switch(Configuration.drive_state)
//		case AUTO:
//		
=======

>>>>>>> branch 'master' of https://github.com/Team2791/robot_2016.git
	}


	public void runDisabled() {
		// TODO Auto-generated method stub

	}

	
	public void runAutonomous() {
		// get onto courtyard first
		switch (DTAH.run()) {//dir robot should move
		case "Robot:center":break;
			// keep driving straight
		case "Robot:right":break;
			//robot move to the right
		case "Robot:left":break;
			//robot move to the left
		default:break;
		}
	}


	public void reset() {
		// TODO Auto-generated method stub

	}

}
