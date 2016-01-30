package org.usfirst.frc.team2791.helpers;

import org.usfirst.frc.team2791.shakerJoystick.Driver;
import org.usfirst.frc.team2791.subsystems.ShakerDriveTrain;

/**
 * Created by Akhil on 1/28/2016.
 */
public class DriveHelper extends ShakerHelper {
	// Joystick inits
	private static Driver driverJoystick;
	// Subsystem inits
	private static ShakerDriveTrain driveTrain;

	public DriveHelper(Driver driveJoy, ShakerDriveTrain shakerDrive) {
		driverJoystick = driveJoy;
		driveTrain = shakerDrive;
		init();

	}

	protected void init() {

	}

	public void teleopRun() {
		switch (driveTrain.getDriveType()) {
		default:
		case TANK:
			driveTrain.setLeftRight(driverJoystick.getAxisLeftY(), driverJoystick.getAxisRightY());
			break;
		case GTA:
			driveTrain.setLeftRight(driverJoystick.getGtaDriveLeft(), driverJoystick.getGtaDriveRight());
			break;
		case ARCADE:
			driveTrain.setLeftRight(driverJoystick.getAxisLeftY(), driverJoystick.getAxisRightX());
		}
		if (driverJoystick.getButtonA()) {
			driveTrain.setHighGear();
		} else if (driverJoystick.getButtonB()) {
			driveTrain.setLowGear();
		}
	}

	public void disableRun() {
		driveTrain.disable();
	}

	public void update() {
		driveTrain.update();

	}

	public void reset() {

	}

}
