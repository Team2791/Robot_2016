package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerShooter;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerShooter.ShooterHeight;
import org.usfirst.frc.team2791.commands.AutoLineUpShot;
import org.usfirst.frc.team2791.util.Toggle;

import static org.usfirst.frc.team2791.robot.Robot.*;

//import org.usfirst.frc.team2791.subsystems.ShakerIntake;

/**
 * Created by Akhil on 2/14/2016.
 */
public class TeleopHelper extends ShakerHelper {
	private static TeleopHelper teleop;
	private static boolean cameraLineUp = false;
	private SendableChooser driveTypeChooser;
	private Toggle useArmAttachmentToggle;
	private boolean holdIntakeDown = false;

	private TeleopHelper() {
		// init
		// smartdashboard drop down menu
		driveTypeChooser = new SendableChooser();
		SmartDashboard.putData("Drive Chooser", driveTypeChooser);
		driveTypeChooser.addObject("Tank Drive", "TANK");
		driveTypeChooser.addObject("Arcade Drive", "ARCADE");
		driveTypeChooser.addDefault("GTA Drive", "GTA");
		driveTypeChooser.addObject("Single Arcade", "SINGLE_ARCADE");
		SmartDashboard.putNumber("Shooter Speeds Setpoint range table", 0);

		// toggles, to prevent sending a subsystem a value too many times
		// this is sort of like a light switch
		useArmAttachmentToggle = new Toggle(false);
	}

	public static TeleopHelper getInstance() {
		if (teleop == null)
			teleop = new TeleopHelper();
		return teleop;
	}

	public void run() {
		// just in case something weird happen in auto
		shooter.setAutonShotMode(false);
		configureAutoShot();
		operatorRun();// runs the operator controls
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
			AutoLineUpShot.reset();
		}

		// TODO: rework this method and the drive train methods to do PID in the
		// run loop

	}

	private void operatorRun() {
		// Operator button layout
		if (operatorJoystick.getButtonB()) {
			// Run intake inward with assistance of the shooter wheel
			shooter.setShooterSpeeds(-0.7, false);
			intake.pullBall();
			holdIntakeDown = true;
		} else if (operatorJoystick.getButtonX()) {
			// Run reverse if button pressed
			shooter.setShooterSpeeds(0.7, false);
			intake.pushBall();

		} else if (!AutoLineUpShot.isRunning() && !shooter.getIfAutoFire()) {
			shooter.setShooterSpeeds(operatorJoystick.getAxisRT() - operatorJoystick.getAxisLT(), false);
			intake.stopMotors();
		}
		if (operatorJoystick.getDpadLeft())
			holdIntakeDown = false;
		if (operatorJoystick.getButtonRS()) {
			shooter.prepShot();
		}
		if (operatorJoystick.getButtonA()) {
			shooter.autoFire();
		}

		if (operatorJoystick.getButtonLS()) {
			if (camera.isCameraManual())
				camera.setCameraValuesAutomatic();
			else
				camera.setCameraValues(1, 1);
		}
		if (operatorJoystick.getDpadUp()) {
			intake.extendIntake();
			shooter.delayedShooterPosition(ShooterHeight.HIGH);
			holdIntakeDown = false;
			// camera.setCameraValues(1, 1);
		}
		if (operatorJoystick.getDpadRight()) {
			intake.extendIntake();
			shooter.delayedShooterPosition(ShooterHeight.MID);
			// camera.setCameraValues(1, 1);
			holdIntakeDown = true;
		}
		if (operatorJoystick.getDpadDown()) {
			intake.extendIntake();
			// camera.setCameraValuesAutomatic();
			shooter.delayedShooterPosition(ShooterHeight.LOW);
			holdIntakeDown = false;
		}

		if (operatorJoystick.getButtonRB()) {
			if (shooter.getIfAutoFire())// if is currently autofiring will
				// override the auto fire
				shooter.overrideAutoShot();
			else
				shooter.pushBall();
		} else if (!shooter.getIfAutoFire())// this just brings the servo back
			// to its place if none of the
			// previous cases apply
			shooter.resetServoAngle();

		if (shooter.getIfAutoFire() || 
			shooter.getIfPreppingShot() || 
			AutoLineUpShot.isRunning()|| 
			DriverStation.getInstance().isBrownedOut())

			compressor.stop();
		else
			compressor.start();

		if ((operatorJoystick.getButtonLB() || driverJoystick.getDpadRight() || AutoLineUpShot.isRunning())
				&& !cameraLineUp) {
			AutoLineUpShot.run();
		}

		if (operatorJoystick.getButtonSt() || operatorJoystick.getDpadDown() || driverJoystick.getButtonSel()) {
			shooter.resetShooterAutoStuff();
			AutoLineUpShot.reset();
		}

	}

	private void sharedRun() {
		if (!shooter.getIfPreppingShot())
			if (operatorJoystick.getButtonSel()) {
				intake.extendIntake();
				useArmAttachmentToggle.setManual(true);
			} else if (driverJoystick.getButtonA() || operatorJoystick.getButtonB()
					|| AbstractShakerShooter.delayedArmMove || operatorJoystick.getDpadLeft() || holdIntakeDown) {
				// this runs if intaking ball too
				intake.extendIntake();
			} else
				// Retract intake
				intake.retractIntake();

		// arm attachment
		useArmAttachmentToggle.giveToggleInput(driverJoystick.getButtonY() || operatorJoystick.getButtonY());
		if (useArmAttachmentToggle.getToggleOutput())
			intake.setArmAttachmentDown();
		else
			intake.setArmAttachmentUp();

	}

	public void configureAutoShot() {
		// this is a driver auto line up without shooting
		if (driverJoystick.getDpadLeft()) {
			AutoLineUpShot.setUseMultipleFrames(false);
			AutoLineUpShot.setShootAfterAligned(false);
			AutoLineUpShot.run();
		}
		if (operatorJoystick.getButtonLB() || driverJoystick.getDpadRight()) {
			AutoLineUpShot.setUseMultipleFrames(true);
			AutoLineUpShot.setShootAfterAligned(true);
			AutoLineUpShot.run();
		}
	}

	public void disableRun() {
		// runs disable methods of subsystems that fall under the driver
		driveTrain.disable();
		shooter.disable();
		intake.disable();
		AutoLineUpShot.reset();
	}

	public void updateSmartDash() {
		intake.updateSmartDash();
		shooter.updateSmartDash();
		driveTrain.updateSmartDash();
		SmartDashboard.putString("Current Driver Input:", getDriveType().toString());
		SmartDashboard.putNumber("turning value", driverJoystick.getAxisLeftX());
	}

	public void reset() {
		shooter.reset();
		intake.reset();
	}

	@Override
	public void debug() {
		driveTrain.debug();
		intake.debug();
		shooter.debug();
	}

	public DriveType getDriveType() {
		// reads data of the smart dashboard and converts to enum DriveType
		String driverInputType = (String) driveTypeChooser.getSelected();
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