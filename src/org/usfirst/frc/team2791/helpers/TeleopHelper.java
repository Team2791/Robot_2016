package org.usfirst.frc.team2791.helpers;

import static org.usfirst.frc.team2791.robot.Robot.driveTrain;
import static org.usfirst.frc.team2791.robot.Robot.driverJoystick;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//import org.usfirst.frc.team2791.subsystems.ShakerIntake;

/**
 * Created by Akhil on 2/14/2016.
 */
public class TeleopHelper extends ShakerHelper {
	private static TeleopHelper teleop;

	private TeleopHelper() {}

	public static TeleopHelper getInstance() {
		if (teleop == null)
			teleop = new TeleopHelper();
		return teleop;
	}

	public void run() {
		// just in case something weird happen in auto
		driverRun();// runs the driver controls
		sharedRun();
	}

	private void driverRun() {
		// Read a value from the smart dashboard and chose what control scheme
		// to use for the
		// drive train
		if (driverJoystick.getButtonRB()) {
			driveTrain.setToggledLeftRight(0.35+driverJoystick.getAxisLeftX()/3, 0.35-driverJoystick.getAxisLeftX()/3);
		} else {
			switch (getDriveType()) {
			case TANK:
				driveTrain.setToggledLeftRight(driverJoystick.getAxisLeftY(), -driverJoystick.getAxisRightY());
				break;
			default:
			case GTA:
				driveTrain.setToggledLeftRight(driverJoystick.getGtaDriveLeft(), driverJoystick.getGtaDriveRight());
				break;
			case ARCADE:
				driveTrain.setToggledLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisRightX());
				break;
			case SINGLE_ARCADE:
				driveTrain.setToggledLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisLeftX());
				break;
			}
		}

		if (driveTrain.isUsingPID() && Math.abs(driverJoystick.getGtaDriveLeft()) > 0.2) {
			System.out.println("driver exiting PID");
			driveTrain.doneUsingPID();
		}
	}

	private void operatorRun() {}

	private void sharedRun() {}

	public void configureAutoShot() {}

	public void disableRun() {
		// runs disable methods of subsystems that fall under the driver
		driveTrain.disable();
	}

	public void updateSmartDash() {
		driveTrain.updateSmartDash();
		SmartDashboard.putString("Current Driver Input:", getDriveType().toString());
		SmartDashboard.putNumber("turning value", driverJoystick.getAxisLeftX());
	}

	public void reset() {	}

	@Override
	public void debug() {
		driveTrain.debug();
	}

	public DriveType getDriveType() {
		// reads data of the smart dashboard and converts to enum DriveType
		String driverInputType = "GTA";
		switch (driverInputType) {
		default:
		case "GTA":
			return DriveType.GTA;
		case "ARCADE":
			return DriveType.ARCADE;
		case "TANK":
			return DriveType.TANK;
		case "SINGLE_ARCADE":
			return DriveType.SINGLE_ARCADE;
		}
	}

	public enum DriveType {
		TANK, ARCADE, GTA, SINGLE_ARCADE
	}

}
/***************
 * old driver auto lineup code
 ***************/
// // THIS IS UNTESTED!!!!
// private static void driverAutoLineUp() {
// cameraLineUp = true;
// if (!runOnlyOnce) {
// driveTrain.resetEncoders();
// if (camera.getTarget() != null)
// target = driveTrain.getAngle()
// + camera.getTarget().ThetaDifference;
// runOnlyOnce = true;
// }
//
// double driverThrottle = driverJoystick.getAxisRT() -
// driverJoystick.getAxisLT();
// // Exit the autoline up after the
// if (driveTrain.setAngleWithDriving(target, 0.7, driverThrottle) ||
// driverJoystick.getDpadUp()) {
// SmartDashboard.putBoolean("Done Lining Up", true);
// cameraLineUp = false;
// runOnlyOnce = false;
// target = 0;
//
// } else
// SmartDashboard.putBoolean("Done Lining Up", false);
//
// }