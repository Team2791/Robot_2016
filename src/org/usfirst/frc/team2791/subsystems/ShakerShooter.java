package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.Ports;

public class ShakerShooter extends ShakerSubsystem {
	private CANTalon leftShooterTalon;
	private CANTalon rightShooterTalon;
	private ShooterHeight shooterState;
	private Solenoid firstLevelSolenoid;
	private Solenoid secondLevelSolenoid;
	private boolean robotHasBall;
	private Servo ballAidServo;

	public ShakerShooter() {
		leftShooterTalon = new CANTalon(Ports.SHOOTER_TALON_LEFT_PORT);
		rightShooterTalon = new CANTalon(Ports.SHOOTER_TALON_RIGHT_PORT);
		leftShooterTalon.setInverted(true);
		firstLevelSolenoid = new Solenoid(Ports.PCM_MODULE, Ports.SHOOTER_PISTON_CHANNEL_FIRST_LEVEL);
		secondLevelSolenoid = new Solenoid(Ports.PCM_MODULE, Ports.SHOOTER_PISTON_CHANNEL_SECOND_LEVEL);
		robotHasBall = false;
		ballAidServo = new Servo(Ports.BALL_AID_SERVO_PORT);
	}

	public void run() {
		leftShooterTalon.set(Constants.SHOOTER_SPEED);
		rightShooterTalon.set(Constants.SHOOTER_SPEED);
	}

	public void run(double syncedSpeed) {
		leftShooterTalon.set(syncedSpeed);
		rightShooterTalon.set(syncedSpeed);
	}

	public void run(double leftSpeed, double rightSpeed) {
		leftShooterTalon.set(leftSpeed);
		rightShooterTalon.set(rightSpeed);
	}

	public void disable() {
		stopMotors();
	}

	public void reset() {
		stopMotors();
		setShooterLow();
	}

	public void update() {
		SmartDashboard.putString("Shooter Height: ", getShooterHeight().toString());
	}

	public ShooterHeight getShooterHeight() {
		refreshShooterHeight();
		return shooterState;
	}

	public void refreshShooterHeight() {
		if (firstLevelSolenoid.get() && secondLevelSolenoid.get())
			shooterState = ShooterHeight.HIGH;
		else if (firstLevelSolenoid.get())
			shooterState = ShooterHeight.MID;
		else
			shooterState = ShooterHeight.LOW;

	}

	public void setShooterLow() {
		shooterState = ShooterHeight.LOW;
		firstLevelSolenoid.set(Constants.SHOOTER_LOW_STATE);
		secondLevelSolenoid.set(Constants.SHOOTER_LOW_STATE);
	}

	public void setShooterMiddle() {
		shooterState = ShooterHeight.MID;
		firstLevelSolenoid.set(Constants.SHOOTER_HIGH_STATE);
		secondLevelSolenoid.set(Constants.SHOOTER_LOW_STATE);
	}

	public void setShooterHigh() {
		shooterState = ShooterHeight.HIGH;
		firstLevelSolenoid.set(Constants.SHOOTER_HIGH_STATE);
		secondLevelSolenoid.set(Constants.SHOOTER_HIGH_STATE);
	}

	public boolean hasBall() {
		return robotHasBall;
	}

	public void pushBall() {
		ballAidServo.setAngle(Constants.SERVO_PUSH_ANGLE);
	}

	public void resetServoAngle() {
		ballAidServo.setAngle(Constants.SERVO_DEFAULT_ANGLE);
	}

	public void stopMotors() {
		leftShooterTalon.set(0.0);
		rightShooterTalon.set(0.0);
	}

	public enum ShooterHeight {
		LOW, MID, HIGH
	}

}
