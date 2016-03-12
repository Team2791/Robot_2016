package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.commands.AutoLineUpShot;
import org.usfirst.frc.team2791.commands.IntakingProcedure;
import org.usfirst.frc.team2791.commands.MoveShooterArm;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerShooter.ShooterHeight;
import org.usfirst.frc.team2791.util.Toggle;

import static org.usfirst.frc.team2791.robot.Robot.*;

/**
 * Created by Akhil on 2/14/2016.
 */
public class TeleopHelper extends ShakerHelper {
	public static Toggle useArmAttachmentToggle;
	private static TeleopHelper teleop;
	public boolean changeArmLocations = false;
	public ShooterHeight armSetpoint;
	private SendableChooser driveTypeChooser;
	private boolean cameraUp = true;

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

	public void debug() {
	}

	public void disableRun() {
		// runs disable methods of subsystems that fall under the driver
		driveTrain.disable();
		shooter.disable();
		intake.disable();
		AutoLineUpShot.reset();
	}

	private void driverRun() {
		// Reads the current drive type to chooser what layout should be used
		// if any of the pid driver controls are being used dont let the
		// triggers take control
		// this includes autoLineup Procedures
		if (!AutoLineUpShot.isRunning()) {
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

			if (driverJoystick.getButtonB())
				driveTrain.setHighGear();
			else if (driverJoystick.getButtonX())
				driveTrain.setLowGear();
			else
				driveTrain.autoShift(!(driverJoystick.getGtaDriveLeft() == driverJoystick.getGtaDriveRight()));
		}
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

	private void operatorRun() {
		// Operator button layout

		if (operatorJoystick.getButtonRS())
			shooter.cancelAutoFire();
		if (operatorJoystick.getButtonLS())
			shooter.prepShot();
		if (operatorJoystick.getButtonA())
			shooter.autoFire();

		if (operatorJoystick.getButtonRB()) {// actuation of servo arm for
			// shooter
			if (shooter.getIfAutoFire() || shooter.getIfPreppingShot())
				// if is currently autofiring will override the auto fire
				shooter.overrideAutoShot();
			else
				shooter.pushBall();
		} else if (!shooter.getIfAutoFire() && !AutoLineUpShot.isRunning())
			// this just brings the servo back
			// to its place if none of the
			// previous cases apply
			shooter.resetServoAngle();

		if (shooter.getIfAutoFire() || shooter.getIfPreppingShot() || AutoLineUpShot.isRunning())
			compressor.stop();
		else
			compressor.start();

		if (operatorJoystick.getButtonLB() || AutoLineUpShot.isRunning()) {
			// if operator hits start begin
			if (operatorJoystick.getButtonSt()) {
				shooter.reset();
				AutoLineUpShot.reset();
			} else {
				AutoLineUpShot.run();
			}
		}

		if (operatorJoystick.getButtonB() || IntakingProcedure.isRunning()) {
			IntakingProcedure.run();
			cameraUp = true;
		}
		else if (operatorJoystick.getDpadLeft()) {
			intake.extendIntake();
			cameraUp = false;
		} else {
			MoveShooterArm.run();
			if (!MoveShooterArm.isRunning()) {
				double tempIntakeSpeedSetpoint = operatorJoystick.getAxisRT() - operatorJoystick.getAxisLT();
				shooter.setToggledShooterSpeed(tempIntakeSpeedSetpoint, false);
				if (tempIntakeSpeedSetpoint > 0)
					intake.pullBall();
				else if (tempIntakeSpeedSetpoint < 0)
					intake.pullBall();
				else {
					intake.stopMotors();
					shooter.setToggledShooterSpeed(0, false);
				}
				intake.retractIntake();
				cameraUp = true;
			}
		}
		if (cameraUp)
			camera.cameraUp();
		else
			camera.cameraDown();

	}

	public void reset() {
		shooter.reset();
		intake.reset();
	}

	public void run() {
		// runs the three subsystems controls
		operatorRun();// runs the operator controls
		driverRun();// runs the driver controls
		sharedRun();// runs the subsystems that are shared by both
	}

	private void sharedRun() {
		// arm attachment
		useArmAttachmentToggle.giveToggleInput(driverJoystick.getButtonY() || operatorJoystick.getButtonY());
		if (useArmAttachmentToggle.getToggleOutput())
			intake.setArmAttachmentDown();
		else
			intake.setArmAttachmentUp();

	}

	// } else if (intake.getIntakeState().equals(IntakeState.EXTENDED) &&
	// armSetpoint != null) {
	// System.out.println("The arm setpoint is being set right
	// now!!!!!!!!!!!!!");
	// shooter.setShooterArm(armSetpoint);
	// armSetpoint = null;
	// } else if (intake.getIntakeState().equals(IntakeState.RETRACTED) &&
	// armSetpoint != null) {
	// System.out.println("I want to move the arm but i am going to wait on the
	// intake");
	// intake.extendIntake();
	// } else if (operatorJoystick.getDpadLeft()) {
	// intake.extendIntake();
	// camera.cameraDown();
	// } else if (operatorJoystick.getButtonX()) {
	// // Run reverse if button pressed
	// shooter.setToggledShooterSpeed(0.85, false);
	// intake.pushBall();
	// } else {
	// if (shooter.getShooterHeight().equals(ShooterHeight.LOW) &&
	// (operatorJoystick.getButtonSel()))
	// camera.cameraDown();
	// else
	// camera.cameraUp();
	// intake.stopMotors();
	// shooter.setToggledShooterSpeed(0, false);
	// if (!shooter.getShooterHeight().equals(ShooterHeight.MOVING) &&
	// armSetpoint == null)
	// intake.retractIntake();
	// }

	private ShooterHeight operatorWantsToMoveArmTo() {
		if (operatorJoystick.getDpadUp()) {
			useArmAttachmentToggle.setManual(false);
			intake.extendIntake();
			camera.setCameraValues(1, 1);
			return ShooterHeight.HIGH;
		} else if (operatorJoystick.getDpadRight()) {
			intake.extendIntake();
			useArmAttachmentToggle.setManual(true);
			camera.setCameraValues(1, 1);
			return (ShooterHeight.MID);
		} else if (operatorJoystick.getDpadDown()) {
			intake.extendIntake();
			useArmAttachmentToggle.setManual(false);
			camera.setCameraValuesAutomatic();
			return (ShooterHeight.LOW);
		}
		return null;
	}

	public void updateSmartDash() {

		SmartDashboard.putString("Current Driver Input:", getDriveType().toString());
	}

	public enum DriveType {
		TANK, ARCADE, GTA, SINGLE_ARCADE
	}

}
