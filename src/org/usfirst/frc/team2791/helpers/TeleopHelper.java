package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerShooter;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerIntake.IntakeState;
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
	private SendableChooser driveTypeChooser;
	private Toggle useArmAttachmentToggle;
	private double angleSetPointForLineUp;
	private static boolean cameraLineUp = false;
	private static boolean runOnlyOnce = false;
	private static double target;
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
		camera.setCameraValues(1, 1);
	}

	public static TeleopHelper getInstance() {
		if (teleop == null)
			teleop = new TeleopHelper();
		return teleop;
	}

	public void run() {
		// just in case something weird happen in auto
		shooter.setAutonShotMode(false);
		// runs the three subsystems controls
		operatorRun();// runs the operator controls
		driverRun();// runs the driver controls
		sharedRun();// runs the subsystems that are shared by both
	}

	private void driverRun() {

		// Read a value from the smart dashboard and chose what control scheme to use for the
		// drive train
		switch (getDriveType()) {
		case TANK:
			driveTrain.setToggledLeftRight(driverJoystick.getAxisLeftY(),
					-driverJoystick.getAxisRightY());
			break;
		default:
		case GTA:

			break;
		case ARCADE:
			driveTrain.setToggledLeftRight(-driverJoystick.getAxisLeftY(),
					-driverJoystick.getAxisRightX());
			break;
		case SINGLE_ARCADE:
			driveTrain.setToggledLeftRight(-driverJoystick.getAxisLeftY(),
					-driverJoystick.getAxisLeftX());
			break;
		}

		if (driveTrain.isUsingPID() && Math.abs(driverJoystick.getGtaDriveLeft()) > 0.2) {
			System.out.println("driver exiting PID");
			driveTrain.doneUsingPID();
			AutoLineUpShot.reset();
		}

		// TODO: rework this method and the drive train methods to do PID in the run loop 
//		if (driverJoystick.getDpadLeft() || cameraLineUp) {
//			driverAutoLineUp();
//		}
		
		// Let the driver hold B to set high gear, hold X to set low gear and othewise auto shit
		if (driverJoystick.getButtonB())
			driveTrain.setHighGear();
		else if (driverJoystick.getButtonX())
			driveTrain.setLowGear();
		else
			driveTrain.autoShift(shooter.equals(ShooterHeight.LOW));
	}

	private void operatorRun() {
		// Operator button layout
		if (operatorJoystick.getButtonB()) {
			// Run intake inward with assistance of the shooter wheel
			shooter.setShooterSpeeds(-0.4, false);
			intake.pullBall();
			holdIntakeDown = true;
		} else if (operatorJoystick.getButtonX()) {
			// Run reverse if button pressed
			shooter.setShooterSpeeds(0.4, false);
			intake.pushBall();

		} else if (!AutoLineUpShot.isRunning() && !shooter.getIfAutoFire()) {
			// else run the manual controls, if it is autofiring this will do
			// nothing
			// shooter.setShooterSpeeds(SmartDashboard.getNumber("Shooter Speeds Setpoint range table"),
			// false);
			shooter.setShooterSpeeds(operatorJoystick.getAxisRT()
					- operatorJoystick.getAxisLT(), false);
			intake.stopMotors();
			// if(shooter.hasBall()||operatorJoystick.getButtonSel())
			// camera.setCameraValues(1, 1);
		}
		if (shooter.hasBall() && operatorJoystick.getDpadDown()
				|| operatorJoystick.getDpadLeft()
				|| operatorJoystick.getDpadRight())
			holdIntakeDown = false;
		if (operatorJoystick.getButtonRS()) {
			shooter.prepShot();
		}
		if (operatorJoystick.getButtonA()) {
			shooter.autoFire();
		}

		// if the intake is up first set the intake down
		// then run the delayed shooter movement that waits one second
		// before moving the arm
		// this allows time for the intake to go down to prevent collision
		if (holdIntakeDown
				&& intake.getIntakeState().equals(IntakeState.EXTENDED)) {
			if (operatorJoystick.getDpadUp()) {
				// holdIntakeDown = false;
				useArmAttachmentToggle.setManual(false);
				intake.extendIntake();
				shooter.setShooterHigh();
				// camera.setCameraValues(1, 1);
			}
			if (operatorJoystick.getDpadRight()) {
				intake.extendIntake();
				useArmAttachmentToggle.setManual(true);
				shooter.setShooterMiddle();
				camera.setCameraValues(1, 1);
				// holdIntakeDown = false;

			}
			if (operatorJoystick.getDpadDown()) {
				intake.extendIntake();
				useArmAttachmentToggle.setManual(false);
				// camera.setCameraValuesAutomatic();
				shooter.delayedShooterPosition(ShooterHeight.LOW);
				// holdIntakeDown = false;
			}

		} else {
			if (operatorJoystick.getDpadUp()) {
				holdIntakeDown = true;
				useArmAttachmentToggle.setManual(false);
				intake.extendIntake();
				shooter.delayedShooterPosition(ShooterHeight.HIGH);
				// camera.setCameraValues(1, 1);
			}
			if (operatorJoystick.getDpadRight()) {
				intake.extendIntake();
				useArmAttachmentToggle.setManual(true);
				shooter.delayedShooterPosition(ShooterHeight.MID);
				// camera.setCameraValues(1, 1);
				holdIntakeDown = true;

			}
			if (operatorJoystick.getDpadDown()) {
				intake.extendIntake();
				useArmAttachmentToggle.setManual(false);
				// camera.setCameraValuesAutomatic();
				shooter.delayedShooterPosition(ShooterHeight.LOW);
				holdIntakeDown = true;
			}
		}
		if (operatorJoystick.getButtonRB()) {// actuation of servo arm for
			// shooter
			if (shooter.getIfAutoFire())// if is currently autofiring will
				// override the auto fire
				shooter.overrideAutoShot();
			else
				shooter.pushBall();
		} else if (!shooter.getIfAutoFire())// this just brings the servo back
			// to its place if none of the
			// previous cases apply
			shooter.resetServoAngle();

		if (shooter.getShooterHeight().equals(ShooterHeight.LOW)
				&& (operatorJoystick.getButtonSel() || useArmAttachmentToggle
						.get())) {
			camera.cameraDown();
		} else {
			camera.cameraUp();
		}

		if (shooter.getIfAutoFire() || AutoLineUpShot.isRunning())
			compressor.stop();
		else
			compressor.start();

		if ((operatorJoystick.getButtonLB() || driverJoystick.getDpadRight() || AutoLineUpShot
				.isRunning()) && !cameraLineUp) {
			// if operator hits start begin
			if (operatorJoystick.getButtonSt()) {
				shooter.reset();
				AutoLineUpShot.reset();
			} else {
				// if (operatorJoystick.getButtonLS())
				// AutoLineUpShot.shooterWithExtraJucice();
				AutoLineUpShot.run();
			}
		}

	}

	private void sharedRun() {
		if (operatorJoystick.getButtonLS())
			holdIntakeDown = false;
		// intake extension toggle
		if (!shooter.getIfPreppingShot())
			if (driverJoystick.getButtonA() || operatorJoystick.getDpadLeft()
					|| operatorJoystick.getButtonB()
					|| AbstractShakerShooter.delayedArmMove
					|| operatorJoystick.getButtonSel() || holdIntakeDown) {
				// this runs if intakeing ball too
				intake.extendIntake();
			} else
				// Retract intake
				intake.retractIntake();
		// arm attachment
		useArmAttachmentToggle.giveToggleInput(driverJoystick.getButtonY()
				|| operatorJoystick.getButtonY());
		if (useArmAttachmentToggle.getToggleOutput())
			intake.setArmAttachmentDown();
		else
			intake.setArmAttachmentUp();

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
		SmartDashboard.putString("Current Driver Input:", getDriveType()
				.toString());
		SmartDashboard
				.putNumber("turning value", driverJoystick.getAxisLeftX());
	}

	public void reset() {
		shooter.reset();
		intake.reset();
	}

//	// THIS IS UNTESTED!!!!
//	private static void driverAutoLineUp() {
//		cameraLineUp = true;
//		if (!runOnlyOnce) {
//			driveTrain.resetEncoders();
//			if (camera.getTarget() != null)
//				target = driveTrain.getAngle()
//						+ camera.getTarget().ThetaDifference;
//			runOnlyOnce = true;
//		}
//		
//		double driverThrottle = driverJoystick.getAxisRT() - driverJoystick.getAxisLT();
//		// Exit the autoline up after the 
//		if (driveTrain.setAngleWithDriving(target, 0.7, driverThrottle) || driverJoystick.getDpadUp()) {
//			SmartDashboard.putBoolean("Done Lining Up", true);
//			cameraLineUp = false;
//			runOnlyOnce = false;
//			target = 0;
//
//		} else
//			SmartDashboard.putBoolean("Done Lining Up", false);
//
//	}

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
