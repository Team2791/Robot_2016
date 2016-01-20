package subsystems;

import configuration.*;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import shakerJoystick.*;
import util.AnalyzeCamera;
import util.DriveTrainAutonHelper;
import util.Logger;

/**
 *
 */
public class DriveTrain implements Subsystems {
	private static Talon leftTalonA;
	private static Talon leftTalonB;
	private static Talon rightTalonA;
	private static Talon rightTalonB;
	private static Driver driveJoystick;
	private static DriveTrainAutonHelper DTAH;
	private static RobotDrive roboDrive;
	private static String name;

	public static enum DriveState {
		AUTO, MANUAL
	}

	public void init() {
	}

	public void init(shakerJoystick.Driver driveJoy, shakerJoystick.Operator opJoy) {

		// instantiated speed controller here
		driveJoystick = driveJoy;
		leftTalonA = new Talon(Ports.leftTalonPortA);
		leftTalonB = new Talon(Ports.leftTalonPortB);
		rightTalonA = new Talon(Ports.rightTalonPortA);
		rightTalonB = new Talon(Ports.rightTalonPortB);
		roboDrive = new RobotDrive(leftTalonA, rightTalonA, leftTalonB, rightTalonB);
	}

	@Override
	public void initTeleop() {
		roboDrive.stopMotor();

	}

	@Override
	public void initDisabled() {
		roboDrive.stopMotor();
	}

	@Override
	public void initAutonomous() {
		roboDrive.stopMotor();

	}

	@Override
	public void runTeleop() {
		driveManually();
	}

	@Override
	public void runDisabled() {
		roboDrive.stopMotor();
	}

	@Override
	public void runAutonomous() {
		// get onto courtyard first
		switch (DTAH.run()) {// dir robot should move
		case "Robot:center":
			break;
		// keep driving straight
		case "Robot:right":
			break;
		// robot move to the right
		case "Robot:left":
			break;
		// robot move to the left
		default:
			break;
		}
	}

	private void driveManually() {
		roboDrive.arcadeDrive(driveJoystick.getAxisLeftY(), driveJoystick.getAxisRightX());
	}

	@Override
	public void reset() {

	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		String s = "";

		s += Logger.buildLine(name, 0);
		return null;

	}

}
