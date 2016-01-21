package subsystems;

import configuration.*;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import shakerJoystick.ShakerJoystick;
import util.AnalyzeCamera;
import util.DriveTrainAutonHelper;

/**
 *
 */
public class DriveTrain implements Subsystems {
	private static Talon leftTalonA;
	private static Talon leftTalonB;
	private static Talon rightTalonA;
	private static Talon rightTalonB;
	private static shakerJoystick.Driver DriverJoystick;
	private static shakerJoystick.Operator OperatorJoystick;
	private static AxisCamera cam;
	private static DriveTrainAutonHelper DTAH;

	public void init() {
	}

	public void init(shakerJoystick.Driver driveJoy, shakerJoystick.Operator opJoy) {
		// TODO Auto-generated method stub
		// instantiated speed controller here
		DriverJoystick = driveJoy;
		OperatorJoystick = opJoy;
		leftTalonA = new Talon(Ports.leftTalonPortA);
		leftTalonB = new Talon(Ports.leftTalonPortB);
		rightTalonA = new Talon(Ports.rightTalonPortA);
		rightTalonB = new Talon(Ports.rightTalonPortB);
	}

	public void initTeleop() {
		// TODO Auto-generated method stub

	}

	public void initDisabled() {
		// TODO Auto-generated method stub

	}

	public void initAutonomous() {
		cam = new AxisCamera(Camera.cameraPort);
		DTAH = new DriveTrainAutonHelper(cam);
	}

	public void shift_up() {

	}

	public void shift_down() {

	}

	public void runTeleop() {
	}

	public void runDisabled() {
		// TODO Auto-generated method stub

	}

	public void runAutonomous() {
		// get onto courtyard first
		switch (DTAH.run()) {// dir robot should move
		case "Robot:center":
			System.out.println("Center");
			break;
		// keep driving straight
		case "Robot:right":
			System.out.println("move right");
			break;
		// robot move to the right
		case "Robot:left":
			System.out.println("move left");
			break;
		// robot move to the left
		default:	
			break;
		}
	}

	public void reset() {
		// TODO Auto-generated method stub

	}

}
