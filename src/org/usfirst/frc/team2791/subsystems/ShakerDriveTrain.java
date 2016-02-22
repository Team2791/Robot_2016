package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.configuration.PID;
import org.usfirst.frc.team2791.configuration.Ports;
import org.usfirst.frc.team2791.util.BasicPID;
import org.usfirst.frc.team2791.util.ShakerGyro;
import org.usfirst.frc.team2791.util.Util;

import static org.usfirst.frc.team2791.robot.Robot.autonTimer;

public class ShakerDriveTrain extends ShakerSubsystem {
	private static BasicPID anglePID;
	private static BasicPID distancePID;
	private Talon leftTalonA;
	private Talon leftTalonB;
	private Talon rightTalonA;
	private Talon rightTalonB;
	private ShakerGyro gyro;
	private Encoder leftDriveEncoder;
	private Encoder rightDriveEncoder;
	private RobotDrive roboDrive;
	private Solenoid driveSolenoid;
	private double drivePIDOutput;
	private double anglePIDOutput;
	private double driveEncoderTicks = 128;
	private double driveTimePIDGoodTime = 0;
	private double angleTimePIDGoodTime = 0;

	public ShakerDriveTrain() {

		this.leftTalonA = new Talon(Ports.DRIVE_TALON_LEFT_PORT_FRONT);
		this.leftTalonB = new Talon(Ports.DRIVE_TALON_LEFT_PORT_BACK);
		this.rightTalonA = new Talon(Ports.DRIVE_TALON_RIGHT_PORT_FRONT);
		this.rightTalonB = new Talon(Ports.DRIVE_TALON_RIGHT_PORT_BACK);
		this.roboDrive = new RobotDrive(leftTalonA, leftTalonB, rightTalonA, rightTalonB);
		roboDrive.stopMotor();
		this.driveSolenoid = new Solenoid(Ports.PCM_MODULE, Ports.DRIVE_SHIFTING_PISTON);
		this.leftDriveEncoder = new Encoder(Ports.LEFT_DRIVE_ENCODER_PORT_A, Ports.LEFT_DRIVE_ENCODER_PORT_B);
		this.rightDriveEncoder = new Encoder(Ports.RIGHT_DRIVE_ENCOODER_PORT_A, Ports.RIGHT_DRIVE_ENCODER_PORT_B);
		leftDriveEncoder.reset();
		rightDriveEncoder.reset();
		leftDriveEncoder.setDistancePerPulse(Util.tickToFeet(driveEncoderTicks, 8));
		rightDriveEncoder.setDistancePerPulse(-Util.tickToFeet(driveEncoderTicks, 8));
		gyro = new ShakerGyro(SPI.Port.kOnboardCS1);
		(new Thread(gyro)).start();
		anglePID = new BasicPID(PID.DRIVE_ANGLE_P, PID.DRIVE_ANGLE_I, PID.DRIVE_ANGLE_D);
		distancePID = new BasicPID(PID.DRIVE_DISTANCE_P, PID.DRIVE_DISTANCE_I, PID.DRIVE_DISTANCE_D);

		anglePID.setMaxOutput(0.5);
		anglePID.setMinOutput(-0.5);
		distancePID.setInvertOutput(true);
		anglePID.setInvertOutput(true);
		anglePID.setIZone(15);
	}

	public boolean driveInFeet(double distance, double angle, double maxOutput) {
		distance *= 12;// convert distance from feet to inches
		setLowGear();
		distancePID.setSetPoint(distance);
		anglePID.setSetPoint(angle);
		distancePID.setMaxOutput(.7);
		distancePID.setMinOutput(-.7);
		anglePID.setMaxOutput(maxOutput);
		anglePID.setMinOutput(-maxOutput);
		anglePID.changeGains(PID.DRIVE_ANGLE_P, PID.DRIVE_ANGLE_I, PID.DRIVE_ANGLE_D);
		distancePID.changeGains(PID.DRIVE_DISTANCE_P, PID.DRIVE_DISTANCE_I, PID.DRIVE_DISTANCE_D);
		drivePIDOutput = distancePID.updateAndGetOutput(this.getRightDistance());
		anglePIDOutput = anglePID.updateAndGetOutput(getAngle());
		setLeftRight(drivePIDOutput + anglePIDOutput, drivePIDOutput - anglePIDOutput);
		SmartDashboard.putNumber("Left Encoder Position", getLeftDistance());
		SmartDashboard.putNumber("Right Encoder Position", getRightDistance());
		SmartDashboard.putNumber("drivePID output", drivePIDOutput);
		SmartDashboard.putNumber("drive error", distancePID.getError());
		if (!(Math.abs(distancePID.getError()) < 1) && (Math.abs(anglePID.getError()) < 2.5)) {
			driveTimePIDGoodTime = Timer.getFPGATimestamp();
			// if error is greater than the values reset the timestamp
		} else if (Timer.getFPGATimestamp() - driveTimePIDGoodTime > 0.5)
			// if the error is good for certain time then drivetrain has reached
			// the dist
			return true;
		return false;

	}

	public boolean setAngle(double angle) {
		setLowGear();
		anglePID.setSetPoint(angle);
		anglePID.setMaxOutput(0.5);
		anglePID.setMinOutput(-0.5);
		anglePID.changeGains(PID.STATIONARY_ANGLE_P, PID.STATIONARY_ANGLE_I, PID.STATIONARY_ANGLE_D);
		anglePIDOutput = anglePID.updateAndGetOutput(getAngle());

		setLeftRight(anglePIDOutput, -anglePIDOutput);
		// System.out.println("PID VALUES"+ PID.DRIVE_ANGLE_P+
		// PID.DRIVE_ANGLE_I+ PID.DRIVE_ANGLE_D);
		if (!(Math.abs(anglePID.getError()) < 2.5)) {
			driveTimePIDGoodTime = Timer.getFPGATimestamp();
			// if error is greater than the values reset the timestamp
		} else if (Timer.getFPGATimestamp() - driveTimePIDGoodTime > 0.5)
			// if the error is good for certain time then drivetrain has reached
			// the angle
			return true;
		return false;

	}

	@Override
	public void run() {
		// nothing here?
	}

	@Override
	public void reset() {
		this.disable();
		this.setLowGear();
		this.rightDriveEncoder.reset();
		this.leftDriveEncoder.reset();

		// this.setDriveType((String) driveTypeChooser.getSelected());
	}

	@Override
	public void updateSmartDash() {
		SmartDashboard.putNumber("Gear : ", isHighGear() ? 2 : 1);
		SmartDashboard.putNumber("Left Drive Encoders Rate", leftDriveEncoder.getRate());
		SmartDashboard.putNumber("Right Drive Encoders Rate", rightDriveEncoder.getRate());
		SmartDashboard.putNumber("Auton drive PID error", distancePID.getError());
		SmartDashboard.putNumber("Auton drive angle PID error", anglePID.getError());
		SmartDashboard.putNumber("Auton timer", autonTimer.getTotalTime());
		SmartDashboard.putNumber("Current gyro angle", getAngle());
		SmartDashboard.putNumber("Current angle pid error", anglePID.getError());
		SmartDashboard.putNumber("PID OUTPUT: ", anglePIDOutput);
		SmartDashboard.putNumber("Average dist", getAvgDist());
		SmartDashboard.putNumber("drivePID output", drivePIDOutput);
		SmartDashboard.putNumber("encoder left", getLeftDistance());
		SmartDashboard.putNumber("encoder right", getRightDistance());

	}

	@Override
	public void disable() {
		roboDrive.stopMotor();
	}

	public void setLeftRight(double left, double right) {
		// System.out.println("Left Output: " + left + "Right Ouput: " + right);
		roboDrive.setLeftRightMotorOutputs(left, right);
	}

	public void setArcadeDrive(double left, double right) {
		roboDrive.arcadeDrive(left, right);
	}

	public boolean isHighGear() {

		switch (getCurrentGear()) {
		case HIGH:
			return true;
		case LOW:
			return false;
		}
		return false;
	}

	private GearState getCurrentGear() {
		if (driveSolenoid.get())
			return GearState.HIGH;
		else if (!driveSolenoid.get())
			return GearState.LOW;
		else
			return GearState.LOW;
	}

	public void setHighGear() {
		driveSolenoid.set(true);

	}

	public void setLowGear() {
		driveSolenoid.set(false);

	}

	public void resetEncoders() {
		leftDriveEncoder.reset();
		rightDriveEncoder.reset();
	}

	public double getLeftDistance() {
		return leftDriveEncoder.getDistance();
	}

	public void resetGyro() {
		gyro.reset();
	}

	public double getGyroRate() {
		return gyro.getRate();
	}

	public double getRightDistance() {
		return rightDriveEncoder.getDistance();
	}

	public double getAngle() {
		return gyro.getAngle();
	}

	public double getAvgSpeed() {
		return (leftDriveEncoder.getRate() + rightDriveEncoder.getRate()) / 2;
	}

	public double getAvgDist() {
		return (leftDriveEncoder.getDistance() + rightDriveEncoder.getDistance()) / 2;
	}

	public void calibrateGyro() {
		gyro.recalibrate();
	}

	public boolean isGyroCalibrating() {
		return gyro.currentlyCalibrating();
	}

	private enum GearState {
		HIGH, LOW
	}

}
