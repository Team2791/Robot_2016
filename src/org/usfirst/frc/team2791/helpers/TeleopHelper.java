package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.subsystems.ShakerIntake;
import org.usfirst.frc.team2791.util.Toggle;

import static org.usfirst.frc.team2791.robot.Robot.*;

import java.awt.Robot;

/**
 * Created by Akhil on 2/14/2016.
 */
public class TeleopHelper extends ShakerHelper {
	private boolean recalibrate = false;// for gyro
	private SendableChooser driveTypeChooser;
	private Toggle clawToggle;
	private Toggle extendIntakeToggle;
	private Toggle useArmAttachmentToggle;
	private double averageVoltage = 0;

	public TeleopHelper() {
		// init
		useArmAttachmentToggle = new Toggle(false);
		driveTypeChooser = new SendableChooser();
		SmartDashboard.putData("Drive Chooser", driveTypeChooser);
		driveTypeChooser.addDefault("Tank Drive", "TANK");
		driveTypeChooser.addObject("Arcade Drive", "ARCADE");
		driveTypeChooser.addObject("GTA Drive", "GTA");
		driveTypeChooser.addObject("Single Arcade", "SINGLE_ARCADE");
		clawToggle = new Toggle(false);
		extendIntakeToggle = new Toggle(false);
		useArmAttachmentToggle = new Toggle(false);
		averageVoltage = PDP.getVoltage();
	}

	public void run() {
		averageVoltage = 0.8 * averageVoltage + 0.2 * PDP.getVoltage();
		operatorRun();
		driverRun();
		sharedRun();
	}

	private void driverRun() {
		// Reads the current drive type to chooser what layout should be used
		// Tank: Left Stick (Y Dir) and Right Stick (Y Dir)
		// GTA: Left Trigger, Right Trigger, Right Stick (Y Dir)
		// Arcade: Left Stick (Y Dir), Right Stick (X Dir)
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

		// Driver button layout
		// RB HIGH GEAR
		// LB LOW GEAR

		if (driverJoystick.getButtonB() || averageVoltage < 11.8)
			driveTrain.setLowGear();
		else
			driveTrain.setHighGear();

		// resets all subsystems under the driver
		if (driverJoystick.getButtonSel())
			driveTrain.reset();
		clawToggle.giveToggleInput(driverJoystick.getButtonX());
		claw.set(clawToggle.get());

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
			shooter.pushBall();
			intake.pushBall();
		} else {
			// else run the manual controls
			shooter.setShooterSpeeds(operatorJoystick.getAxisRT() - operatorJoystick.getAxisLT(), false);
			intake.stopMotors();
		}
		// System.out.println("Shooter Speeds are set to:"
		// +operatorJoystick.getAxisRT());

		// autofire shooter
		if (operatorJoystick.getButtonA())
			shooter.autoFire(1.0);// does complete shot

		if (intake.getIntakeState().equals(ShakerIntake.IntakeState.EXTENDED)) {
			if (operatorJoystick.getDpadUp())
				shooter.setShooterHigh();
			if (operatorJoystick.getDpadRight())
				shooter.setShooterMiddle();
			if (operatorJoystick.getDpadDown())
				shooter.setShooterLow();
		} else if (intake.getIntakeState().equals(ShakerIntake.IntakeState.RETRACTED)) {
			//
			// if(operatorJoystick.getDpadDown() || operatorJoystick.getDpadUp()
			// || operatorJoystick.getDpadRight());
			// extendIntakeToggle.setManual(true);
		}
		if (operatorJoystick.getButtonRB())
			shooter.overrideAutoShot();
		if (operatorJoystick.getDpadLeft())
			shooter.pushBall();
		else if (!shooter.getIfAutoFire())
			shooter.resetServoAngle();
		// Start button to reset to teleop start
		if (operatorJoystick.getButtonSt())
			reset();

	}

	private void sharedRun() {
		// arm attachment
		useArmAttachmentToggle.giveToggleInput(driverJoystick.getButtonY() || operatorJoystick.getButtonY());
		// if (useArmAttachmentToggle.getToggleOutput())
		// intake.setArmAttachmentDown();
		// else
		// intake.setArmAttachmentUp();
		// intake extension toggle
		extendIntakeToggle.giveToggleInput(driverJoystick.getButtonA() || operatorJoystick.getButtonY());
		if (extendIntakeToggle.getToggleOutput())// Extend intake
			intake.extendIntake();
		else// Retract intake
			intake.retractIntake();
	}

	@Override
	public void disableRun() {
		// runs disable methods of subsystems that fall under the driver
		driveTrain.disable();

	}

	@Override
	public void updateSmartDash() {
		intake.updateSmartDash();
		shooter.updateSmartDash();
		// updateSmartDash the smartDashboard values of subsystems
		driveTrain.updateSmartDash();
		SmartDashboard.putString("Current Driver Input:", getDriveType().toString());
		SmartDashboard.putBoolean("Is Gyro calibrating: ", driveTrain.isGyroCalibrating());
		SmartDashboard.putNumber("turning value", driverJoystick.getAxisLeftX());
		SmartDashboard.putNumber("Average Voltage", averageVoltage);
		SmartDashboard.putNumber("Current Voltage", PDP.getVoltage());

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
