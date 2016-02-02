package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.PID;
import org.usfirst.frc.team2791.configuration.Ports;
import org.usfirst.frc.team2791.util.BasicPID;

public class ShakerShooter extends ShakerSubsystem {
	private static final int updateDelayMs = 1000 / 100; // run at 100 Hz
	private final double[] speed = { 0.25, 0.5, 0.75, 1.0 };
	private CANTalon leftShooterTalon;
	private CANTalon rightShooterTalon;
	private Solenoid firstLevelSolenoid;
	private Solenoid secondLevelSolenoid;
	private Servo ballAidServo;
	private BasicPID rightShooterPID;
	private BasicPID leftShooterPID;
	private boolean usePID = false;
	private double leftShooterOutput;
	private double rightShooterOutput;
	private Encoder rightShooterEncoder;
	private Encoder leftShooterEncoder;
	private AnalogInput ballCheckingSensor;

	public ShakerShooter() {
		// init
		leftShooterTalon = new CANTalon(Ports.SHOOTER_TALON_LEFT_PORT);
		rightShooterTalon = new CANTalon(Ports.SHOOTER_TALON_RIGHT_PORT);
		leftShooterTalon.setInverted(true);
		firstLevelSolenoid = new Solenoid(Ports.PCM_MODULE, Ports.SHOOTER_PISTON_CHANNEL_FIRST_LEVEL);
		secondLevelSolenoid = new Solenoid(Ports.PCM_MODULE, Ports.SHOOTER_PISTON_CHANNEL_SECOND_LEVEL);
		ballAidServo = new Servo(Ports.BALL_AID_SERVO_PORT);
		rightShooterPID = new BasicPID(PID.SHOOTER_P, PID.SHOOTER_I, PID.SHOOTER_D);
		rightShooterPID.setMaxOutput(Constants.MAX_SHOOTER_SPEED);
		leftShooterPID = new BasicPID(PID.SHOOTER_P, PID.SHOOTER_I, PID.SHOOTER_D);
		leftShooterPID.setMaxOutput(Constants.MAX_SHOOTER_SPEED);
		ballCheckingSensor = new AnalogInput(Ports.BALL_DISTANCE_SENSOR_PORT);
	}

	public void run() {
		while (true) {
			try {
				if (usePID) {// if given value to go to
					leftShooterOutput = rightShooterPID.updateAndGetOutput(leftShooterEncoder.getRate());
					rightShooterOutput = rightShooterPID.updateAndGetOutput(rightShooterEncoder.getRate());
					SmartDashboard.putNumber("Right Shooter Output", rightShooterOutput);
					SmartDashboard.putNumber("Left Shooter Output", leftShooterOutput);
					//setShoterSpeeds(leftShooterOutput, rightShooterOutput);
					//usePID = hasBall();
				}
				// delay to prevent it from running to fast
				Thread.sleep(updateDelayMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void shooterSpeedsWithoutPID(double syncedSpeed) {
		// this shouldn't be used except for testing or for practice bot....
		leftShooterTalon.set(syncedSpeed);
		rightShooterTalon.set(syncedSpeed);
	}

	private void setShoterSpeeds(double left, double right) {
		// private method to set speeds of the shooter wheels
		leftShooterTalon.set(left);
		rightShooterTalon.set(right);
	}

	public void shooterSpeedWithPID(int shooterSpeedIndex) {
		// starts the pid loop
		usePID = true;
		rightShooterPID.setSetPoint(speed[shooterSpeedIndex]);
		leftShooterPID.setSetPoint(speed[shooterSpeedIndex]);
		rightShooterPID.reset();
		leftShooterPID.reset();
	}

	public void shooterSpeedWithoutPID(int shooterSpeedIndex) {

		shooterSpeedsWithoutPID(speed[shooterSpeedIndex]);

	}

	public void disable() {
		// disable code --stops wheels
		stopMotors();
	}

	public void reset() {
		// bring robot back to starting configuration --maybe put one button on
		// dash to reset robot pos
		stopMotors();
		// setShooterLow();
	}

	public void updateSmartDash() {
		// SmartDashboard.putString("Shooter Height: ", getShooterHeight().toString());
		SmartDashboard.putNumber("Ball Distance Sensor", ballCheckingSensor.getVoltage());

	}

	public ShooterHeight getShooterHeight() {
		// get current shooter height by determining which solenoid are true
		if (firstLevelSolenoid.get() && secondLevelSolenoid.get())
			return ShooterHeight.HIGH;
		else if (firstLevelSolenoid.get())
			return ShooterHeight.MID;
		else
			return ShooterHeight.LOW;

	}

	public void setShooterLow() {
		// set shooter height to low , set both pistons to false
		firstLevelSolenoid.set(Constants.SHOOTER_LOW_STATE);
		secondLevelSolenoid.set(Constants.SHOOTER_LOW_STATE);
	}

	public void setShooterMiddle() {
		// set shooter height to middle meaning only one piston will be true
		firstLevelSolenoid.set(Constants.SHOOTER_HIGH_STATE);
		secondLevelSolenoid.set(Constants.SHOOTER_LOW_STATE);
	}

	public void setShooterHigh() {
		// both pistons will be set to true to get max height
		firstLevelSolenoid.set(Constants.SHOOTER_HIGH_STATE);
		secondLevelSolenoid.set(Constants.SHOOTER_HIGH_STATE);
	}

	public boolean hasBall() {
		// this boolean will be determined by possible sensors
		// will be used for auto firing
		if (ballCheckingSensor.getAverageVoltage() < Constants.THRESHOLD_BALL_DISTANCE)
			return true;
		return false;
	}

	public void pushBall() {
		// will be used to push ball toward the shooter
		ballAidServo.setAngle(Constants.SERVO_PUSH_ANGLE);
	}

	public void resetServoAngle() {
		// bring servo back to original position
		ballAidServo.setAngle(Constants.SERVO_DEFAULT_ANGLE);
	}

	public void stopMotors() {
		// bring both motors to stop
		leftShooterTalon.set(0.0);
		rightShooterTalon.set(0.0);
	}

	public enum ShooterHeight {
		LOW, MID, HIGH
	}

}
