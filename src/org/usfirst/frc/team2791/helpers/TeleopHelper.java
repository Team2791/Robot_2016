package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerIntake;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerShooter;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerShooter.ShooterHeight;
import org.usfirst.frc.team2791.util.Toggle;

import commands.AutoLineUpShot;

import static org.usfirst.frc.team2791.robot.Robot.*;

//import org.usfirst.frc.team2791.subsystems.ShakerIntake;

/**
 * Created by Akhil on 2/14/2016.
 */
public class TeleopHelper extends ShakerHelper {
	private SendableChooser driveTypeChooser;
	private Toggle clawToggle;
	private Toggle extendIntakeToggle;
	private Toggle useArmAttachmentToggle;
	private Toggle cameraServoPosToggle;
	private double angleHoldPos;
	private boolean angleHoldOnlyOnce = false;

	public TeleopHelper() {
		// init
		// smartdashboard drop down menu
		driveTypeChooser = new SendableChooser();
		SmartDashboard.putData("Drive Chooser", driveTypeChooser);
		driveTypeChooser.addObject("Tank Drive", "TANK");
		driveTypeChooser.addObject("Arcade Drive", "ARCADE");
		driveTypeChooser.addDefault("GTA Drive", "GTA");
		driveTypeChooser.addObject("Single Arcade", "SINGLE_ARCADE");

		// toggles, to prevent sending a subsystem a value too many times
		clawToggle = new Toggle(false);
		extendIntakeToggle = new Toggle(false);
		useArmAttachmentToggle = new Toggle(false);
		cameraServoPosToggle = new Toggle(false);
	}

	public void run() {
		// runs the three subsystems controls
		operatorRun();// runs the operator controls
		driverRun();// runs the driver controls
		sharedRun();// runs the subsystems that are shared by both
	}

	private void driverRun() {
		// Reads the current drive type to chooser what layout should be used
		if (!driverJoystick.getDpadUp()) {
			if (!commands.AutoLineUpShot.isRunning())
				switch (getDriveType()) {
				case TANK:
					driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisRightY());
					break;
				default:
				case GTA:
					driveTrain.setLeftRight(driverJoystick.getGtaDriveLeft(), driverJoystick.getGtaDriveRight());
					break;
				case ARCADE:
					driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisRightX());
					break;
				case SINGLE_ARCADE:
					driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisLeftX());
					break;
				}
			// gear switching, defaults to high gear
			if (driverJoystick.getButtonB())
				driveTrain.setHighGear();
			else
				driveTrain.setLowGear();
		}
		// intake extension toggle
		extendIntakeToggle.giveToggleInput(driverJoystick.getButtonA() || operatorJoystick.getDpadLeft());
		if (extendIntakeToggle.getToggleOutput())// Extend intake
			intake.extendIntake();
		else// Retract intake
			intake.retractIntake();
		clawToggle.giveToggleInput(driverJoystick.getButtonX());
		claw.set(clawToggle.get());
		if (driverJoystick.getDpadUp()) {
			if (!angleHoldOnlyOnce) {
				angleHoldPos = driveTrain.getAngle();
			}
			driveTrain.setAngle(angleHoldPos, 0.4);
			driveTrain.setLeftRight(driverJoystick.getAxisRT(), driverJoystick.getAxisRT());
			
		} else
			angleHoldOnlyOnce = false;
	}

	private void operatorRun() {
		// Operator button layout
		if (operatorJoystick.getButtonB()) {
			// Run intake inward with assistance of the shooter wheel
			shooter.setShooterSpeeds(-0.85, false);
			intake.pullBall();
		} else if (operatorJoystick.getButtonX()) {
			// Run reverse if button pressed
			shooter.setShooterSpeeds(0.85, false);
			intake.pushBall();
		} else {
			// else run the manual controls, if it is autofiring this will do
			// nothing
			shooter.setShooterSpeeds(operatorJoystick.getAxisRT() - operatorJoystick.getAxisLT(), false);
			intake.stopMotors();
		}
		// autofire shooter
		if (operatorJoystick.getButtonLB())
			shooter.autoFire();// does complete shot

		if (intake.getIntakeState().equals(PracticeShakerIntake.IntakeState.EXTENDED)) {
			// check if the intake is up before doing anything
			// set shooter to spot accordingly
			if (operatorJoystick.getDpadUp()) {
				shooter.setShooterHigh();
				camera.setCameraValues(1, 1);
			}
			if (operatorJoystick.getDpadRight()) {
				shooter.setShooterMiddle();
				camera.setCameraValues(1, 1);
			}
			if (operatorJoystick.getDpadDown()) {
				camera.setCameraValuesAutomatic();
				shooter.setShooterLow();
			}
		}

		if (operatorJoystick.getButtonRB()) {// actuation of servo arm
			if (shooter.getIfAutoFire())// if is currently autofiring will
				// override the auto fire
				shooter.overrideAutoShot();
			else
				shooter.pushBall();
		} else if (!shooter.getIfAutoFire())// this just brings the servo back
			// to its place if none of the
			// previous cases apply
			shooter.resetServoAngle();

		if (shooter.getShooterHeight().equals(ShooterHeight.LOW)) {
			if (operatorJoystick.getButtonSel())
				camera.cameraUp();
			else
				camera.cameraDown();
		} else {
			camera.cameraUp();
		}

		if (shooter.getIfAutoFire() || commands.AutoLineUpShot.isRunning())
			compressor.stop();
		else
			compressor.start();

		if (operatorJoystick.getButtonA() || commands.AutoLineUpShot.isRunning()) {
			// if operator hits start begin
			if (operatorJoystick.getButtonSt())
				commands.AutoLineUpShot.reset();
			else {
				commands.AutoLineUpShot.run();
			}
		}

	}

	private void sharedRun() {
		// arm attachment
		useArmAttachmentToggle.giveToggleInput(driverJoystick.getButtonY() || operatorJoystick.getButtonY());
		if (useArmAttachmentToggle.getToggleOutput())
			intake.setArmAttachmentDown();
		else
			intake.setArmAttachmentUp();

	}

	@Override
	public void disableRun() {
		// runs disable methods of subsystems that fall under the driver
		driveTrain.disable();
		AutoLineUpShot.reset();
	}

	@Override
	public void updateSmartDash() {
		intake.updateSmartDash();
		shooter.updateSmartDash();
		driveTrain.updateSmartDash();
		SmartDashboard.putString("Current Driver Input:", getDriveType().toString());
		SmartDashboard.putBoolean("Is Gyro calibrating: ", driveTrain.isGyroCalibrating());
		SmartDashboard.putNumber("turning value", driverJoystick.getAxisLeftX());
	}

	@Override
	public void reset() {
		shooter.reset();
		intake.reset();
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
