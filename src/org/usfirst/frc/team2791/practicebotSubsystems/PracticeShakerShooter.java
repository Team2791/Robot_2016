package org.usfirst.frc.team2791.practicebotSubsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.util.Constants;

public class PracticeShakerShooter extends PracticeShakerSubsystem implements Runnable {
	private static final int updateDelayMs = 1000 / 100; // run at 100 Hz
	// private final double[] speed = {0.25, 0.5, 0.75, 1.0};
	private final double delayTimeBeforeShooting = 0.5;// time for wheels to
	// get
	// to speed
	private final double delayTimeForServo = 0.8;// time for servo to push
	private final int encoderTicks = 128 * 4;
	private boolean autoFire;
	private CANTalon leftShooterTalon;
	private CANTalon rightShooterTalon;
	private DoubleSolenoid shortPiston;
	private DoubleSolenoid longPiston;
	private Servo ballAidServo;
	private AnalogInput ballDistanceSensor;
	private double feedForward = 0.4;
	private double closeShotSetPoint = 590;
	private double farShotSetpoint = 850;
	private boolean overrideShot = false;
	private boolean prepShot = false;

	public PracticeShakerShooter() {
		leftShooterTalon = new CANTalon(PracticePorts.SHOOTER_TALON_LEFT_PORT);
		rightShooterTalon = new CANTalon(PracticePorts.SHOOTER_TALON_RIGHT_PORT);
		ballAidServo = new Servo(PracticePorts.BALL_AID_SERVO_PORT);
		longPiston = new DoubleSolenoid(PracticePorts.PCM_MODULE, PracticePorts.LONG_PISTON_FORWARD,
				PracticePorts.LONG_PISTON_REVERSE);
		shortPiston = new DoubleSolenoid(21, 0, 1);
		ballDistanceSensor = new AnalogInput(PracticePorts.BALL_DISTANCE_SENSOR_PORT);
		rightShooterTalon.setInverted(false);
		// true, false, true, false // right sensor correct,
		rightShooterTalon.reverseOutput(false);
		leftShooterTalon.reverseOutput(false);
		leftShooterTalon.reverseSensor(true);
		rightShooterTalon.reverseSensor(false);
		leftShooterTalon.configPeakOutputVoltage(+12.0f, 0);
		rightShooterTalon.configPeakOutputVoltage(+12.0f, 0);
		//
		SmartDashboard.putNumber("Shooter p", Constants.SHOOTER_P);
		SmartDashboard.putNumber("Shooter i", Constants.SHOOTER_I);
		SmartDashboard.putNumber("Shooter d", Constants.SHOOTER_D);
		Constants.SHOOTER_P = SmartDashboard.getNumber("Shooter p");
		Constants.SHOOTER_I = SmartDashboard.getNumber("Shooter i");
		Constants.SHOOTER_D = SmartDashboard.getNumber("Shooter d");
		SmartDashboard.putNumber("FeedForward", feedForward);
		SmartDashboard.putNumber("closeShotSetpoint", closeShotSetPoint);
		SmartDashboard.putNumber("farShotSetpoint", farShotSetpoint);
		leftShooterTalon.setIZone(500);
		rightShooterTalon.setIZone(500);
		leftShooterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rightShooterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		leftShooterTalon.configEncoderCodesPerRev(encoderTicks);
		rightShooterTalon.configEncoderCodesPerRev(encoderTicks);

		leftShooterTalon.changeControlMode(TalonControlMode.Speed);
		rightShooterTalon.changeControlMode(TalonControlMode.Speed);
		leftShooterTalon.enableControl();
		rightShooterTalon.enableControl();
		leftShooterTalon.enable();
		rightShooterTalon.enable();
		leftShooterTalon.configNominalOutputVoltage(0, 0);
		rightShooterTalon.configNominalOutputVoltage(0, 0);

	}

	@Override
	public void run() {
		try {
			while (true) {
				closeShotSetPoint = SmartDashboard.getNumber("closeShotSetpoint");
				farShotSetpoint = SmartDashboard.getNumber("farShotSetpoint");
				double setPoint = getShooterHeight().equals(ShooterHeight.MID) ? farShotSetpoint : closeShotSetPoint;
				if (prepShot) {
					setShooterSpeeds(setPoint, true);
				}
				if (autoFire) {

					setShooterSpeeds(setPoint, true);
					// System.out.println("my setpoint is " + setPoint);

					// System.out.println("Talons think the setpoint is " +
					// leftShooterTalon.getSetpoint() + " and "
					// + rightShooterTalon.getSetpoint());

					double whenTheWheelsStartedBeingTheRightSpeed = Timer.getFPGATimestamp();
					while (Timer.getFPGATimestamp()
							- whenTheWheelsStartedBeingTheRightSpeed < delayTimeBeforeShooting) {
						if (overrideShot)
							break;
						if (!(Math.abs(leftShooterTalon.getError()) < 50
								&& Math.abs(rightShooterTalon.getError()) < 50)) {
							// if the wheels aren't at speed reset the count
							whenTheWheelsStartedBeingTheRightSpeed = Timer.getFPGATimestamp();
						}
						Thread.sleep(10);
						setShooterSpeeds(setPoint, true);

					}
					double time = Timer.getFPGATimestamp();
					// push ball
					while (Timer.getFPGATimestamp() - time < delayTimeForServo) {
						Thread.sleep(10);
						setShooterSpeeds(setPoint, true);
						pushBall();
					}
					// reset everything
					resetServoAngle();
					stopMotors();
					overrideShot = false;
				}
				autoFire = false;
				try {
					// slows down the rate at which this method is called(so it
					// doesn't run too fast)
					Thread.sleep(updateDelayMs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setShooterSpeeds(double targetSpeed, boolean withPID) {
		if (withPID) {
			leftShooterTalon.changeControlMode(TalonControlMode.Speed);
			rightShooterTalon.changeControlMode(TalonControlMode.Speed);
			leftShooterTalon.setP(SmartDashboard.getNumber("Shooter p"));
			leftShooterTalon.setI(SmartDashboard.getNumber("Shooter i"));
			leftShooterTalon.setD(SmartDashboard.getNumber("Shooter d"));
			rightShooterTalon.setP(SmartDashboard.getNumber("Shooter p"));
			rightShooterTalon.setI(SmartDashboard.getNumber("Shooter i"));
			rightShooterTalon.setD(SmartDashboard.getNumber("Shooter d"));
			leftShooterTalon.setF(SmartDashboard.getNumber("FeedForward"));
			rightShooterTalon.setF(SmartDashboard.getNumber("FeedForward"));
			leftShooterTalon.set(targetSpeed);
			rightShooterTalon.set(targetSpeed);// these values are in rpms

		} else if (!autoFire) {

			leftShooterTalon.changeControlMode(TalonControlMode.PercentVbus);
			rightShooterTalon.changeControlMode(TalonControlMode.PercentVbus);
			leftShooterTalon.set(targetSpeed);
			rightShooterTalon.set(targetSpeed);
		}

	}

	@Override
	public void updateSmartDash() {
		// TODO Auto-generated method stub
		SmartDashboard.putBoolean("Does shooter have ball", hasBall());
		SmartDashboard.putNumber("LeftShooterSpeed", leftShooterTalon.getEncVelocity());
		SmartDashboard.putNumber("RightShooterSpeed", rightShooterTalon.getEncVelocity());
		SmartDashboard.putNumber("Left Shooter Error", leftShooterTalon.getClosedLoopError());
		SmartDashboard.putNumber("Right Shooter Error", -rightShooterTalon.getClosedLoopError());
		SmartDashboard.putString("Current shooter setpoint", getShooterHeight().toString());
		SmartDashboard.putNumber("left output voltage", leftShooterTalon.getOutputVoltage());
		SmartDashboard.putNumber("left speed", -leftShooterTalon.getEncVelocity());
		SmartDashboard.putNumber("right output voltage", rightShooterTalon.getOutputVoltage());
		SmartDashboard.putNumber("right speed", rightShooterTalon.getEncVelocity());
		SmartDashboard.putNumber("Right error", rightShooterTalon.getError());
		SmartDashboard.putNumber("Left error", leftShooterTalon.getError());

	}

	@Override
	public void reset() {
		stopMotors();
		// reset the PID on the Talons
		leftShooterTalon.reset();
		rightShooterTalon.reset();
		// TODO Auto-generated method stub

	}

	public void stopMotors() {
		leftShooterTalon.set(0);
		rightShooterTalon.set(0);
	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub
		stopMotors();
		SmartDashboard.putNumber("right speed", rightShooterTalon.getSpeed());
		SmartDashboard.putNumber("left speed", leftShooterTalon.getSpeed());
	}

	public boolean hasBall() {
		// System.out.println("The ball distance check sensor is: " +
		// ballDistanceSensor.getVoltage());
		return ballDistanceSensor.getVoltage() > 0.263;
	}

	public void pushBall() {
		// will be used to push ball toward the shooter
		ballAidServo.set(0.5);
		// System.out.println("Im being told to push the ball");

	}

	public void resetServoAngle() {
		// bring servo back to original position
		ballAidServo.set(1);
		// System.out.println("Im being told to Reset!!");
	}

	public void autoFire() {
		autoFire = true;
	}

	public ShooterHeight getShooterHeight() {
		// get current shooter height by determining which solenoid are true
		if (shortPiston.get().equals(PracticeConstants.SMALL_PISTON_HIGH_STATE)
				&& longPiston.get().equals(PracticeConstants.LARGE_PISTON_HIGH_STATE)) {
			// System.out.println("I think that the shooter is in the high
			// pos");
			return ShooterHeight.HIGH;
		} else if (longPiston.get().equals(PracticeConstants.LARGE_PISTON_HIGH_STATE)) {
			// System.out.println("I think that the shooter is in the mid pos");
			return ShooterHeight.MID;
		} else {

			// System.out.println("I think that the shooter is in the low pos");
			return ShooterHeight.LOW;
		}
	}

	public void setShooterLow() {
		// both pistons will be set to true to get max height
		shortPiston.set(PracticeConstants.SMALL_PISTON_LOW_STATE); // was
																	// reverse
		// //this is short
		// one
		longPiston.set(PracticeConstants.LARGE_PISTON_LOW_STATE);

	}

	public void setShooterMiddle() {
		// set shooter height to middle meaning only one piston will be true
		shortPiston.set(PracticeConstants.SMALL_PISTON_LOW_STATE);
		longPiston.set(PracticeConstants.LARGE_PISTON_HIGH_STATE);
	}

	public void setShooterHigh() {
		// set shooter height to low , set both pistons to false
		shortPiston.set(PracticeConstants.SMALL_PISTON_HIGH_STATE);
		longPiston.set(PracticeConstants.LARGE_PISTON_HIGH_STATE);
		// short needs to switch
	}

	public void overrideAutoShot() {
		overrideShot = true;
	}

	public boolean getIfAutoFire() {
		return autoFire;

	}

	public enum ShooterHeight {
		LOW, MID, HIGH
	}

}