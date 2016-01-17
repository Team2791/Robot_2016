package subsystems;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import shakerJoystick.ShakerJoystick;
import Configuration.*;
import util.AnalyzeCamera;
import subsystems.DriveTrainAutonHelper;
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

	public void init(){}
	
	
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
	@Override
	public void initTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initDisabled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initAutonomous() {
		cam = new AxisCamera(Camera.cameraPort);
		DTAH = new DriveTrainAutonHelper(cam);
	}

	@Override
	public void runTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void runDisabled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void runAutonomous() {
		// get onto courtyard first
		DTAH.run();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	
}
