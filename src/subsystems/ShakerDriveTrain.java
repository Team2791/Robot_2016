package subsystems;

import configuration.Constants;
import configuration.Ports;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import org.usfirst.frc.team2791.robot.Robot.DriveType;
import org.usfirst.frc.team2791.robot.Robot.SafetyMode;

/**
 * Created by Akhil on 1/26/2016.
 */
public class ShakerDriveTrain extends ShakerSubsystem {
	private Talon leftTalonA;
	private Talon leftTalonB;
	private Talon rightTalonA;
	private Talon rightTalonB;
	private RobotDrive roboDrive;
	private DriveType driveType;
	private gear gearState;
	private SafetyMode safeMode;
	private Solenoid leftDriveSolenoid;
	private Solenoid rightDriveSolenoid;

	public ShakerDriveTrain(DriveType driverInputType) {
		init();
		this.setDriveType(driveType);
	}

	public void init() {
		this.leftTalonA = new Talon(Ports.LEFT_TALON_PORT_A);
		this.leftTalonB = new Talon(Ports.LEFT_TALON_PORT_B);
		this.rightTalonA = new Talon(Ports.RIGHT_TALON_PORT_A);
		this.rightTalonB = new Talon(Ports.RIGHT_TALON_PORT_B);
		this.roboDrive = new RobotDrive(leftTalonA, leftTalonB, rightTalonA, rightTalonB);
		this.disable();
		this.leftDriveSolenoid = new Solenoid(Ports.LEFT_DRIVE_PISTON_CHANNEL, Ports.PCM_MODULE);
		this.rightDriveSolenoid = new Solenoid(Ports.RIGHT_DRIVE_PISTON_CHANNEL, Ports.PCM_MODULE);
	}

	public void run() {
		// nothing here?
	}

	public void reset() {
		disable();
		setLowGear();
		setDriveType(DriveType.TANK);

	}

	public void update() {

	}

	public void disable() {
		roboDrive.stopMotor();
	}

	public void setLeftRight(double left, double right) {
		switch (safeMode) {
		case SAFETY:
			left *= Constants.FULL_SPEED_SAFETY_MODE;
			right *= Constants.FULL_SPEED_SAFETY_MODE;
			this.setLowGear();
			break;
		case FULL_CONTROL:
			break;
		}
		switch (driveType) {
		case TANK:
			roboDrive.setLeftRightMotorOutputs(left, right);
			break;
		case GTA:
			roboDrive.setLeftRightMotorOutputs(left, right);
			break;
		case ARCADE:
			roboDrive.arcadeDrive(left, right);
		}
	}

	public void setDriveType(DriveType driverInputType) {
		this.driveType = driverInputType;
	}

	public void setSafeMode(SafetyMode safety) {
		this.safeMode = safety;
	}

	public boolean isHighGear() {
		switch (gearState) {
		case HIGH:
			return true;
		case LOW:
			return false;
		}
		return false;
	}

	public void setHighGear() {
		if (!isHighGear()) {
			gearState = gear.HIGH;
			leftDriveSolenoid.set(Constants.DRIVE_HIGH_GEAR);
			rightDriveSolenoid.set(Constants.DRIVE_HIGH_GEAR);
		}

	}

	public void setLowGear() {
		if (isHighGear()) {
			gearState = gear.LOW;
			leftDriveSolenoid.set(Constants.DRIVE_LOW_GEAR);
			rightDriveSolenoid.set(Constants.DRIVE_LOW_GEAR);
		}
	}

	private enum gear {
		HIGH, LOW
	}
}
