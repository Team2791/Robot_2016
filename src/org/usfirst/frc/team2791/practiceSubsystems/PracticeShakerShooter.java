package org.usfirst.frc.team2791.practiceSubsystems;

import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerShooter;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerShooter.ShooterHeight;
import org.usfirst.frc.team2791.util.Constants;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PracticeShakerShooter extends AbstractShakerShooter {
	// shooter arm positiion pistons
	private DoubleSolenoid shortPiston;
	private DoubleSolenoid longPiston;

	public PracticeShakerShooter() {
		System.out.println("Creating an instance of the shaker shooter");
		leftShooterTalon = new CANTalon(PracticePorts.SHOOTER_TALON_LEFT_PORT);
		rightShooterTalon = new CANTalon(PracticePorts.SHOOTER_TALON_RIGHT_PORT);
		// servo
		ballAidServo = new Servo(PracticePorts.BALL_AID_SERVO_PORT);
		// shooter arm movement pistons
		longPiston = new DoubleSolenoid(PracticePorts.PCM_MODULE, PracticePorts.LONG_PISTON_FORWARD,
				PracticePorts.LONG_PISTON_REVERSE);
		shortPiston = new DoubleSolenoid(21, 0, 1);
		// analog sensor
		ballDistanceSensor = new AnalogInput(PracticePorts.BALL_DISTANCE_SENSOR_PORT);
		init();
	}
	
	public ShooterHeight getShooterHeight() {
		// get current shooter height by determining which solenoid are true
		if (shortPiston.get().equals(PracticeConstants.SMALL_PISTON_HIGH_STATE)
				&& longPiston.get().equals(PracticeConstants.LARGE_PISTON_HIGH_STATE)) {
			return ShooterHeight.HIGH;
		} else if (longPiston.get().equals(PracticeConstants.LARGE_PISTON_HIGH_STATE)) {
			return ShooterHeight.MID;
		} else {
			return ShooterHeight.LOW;
		}
	}
	
	protected void moveShooterPistonsLow() {
		shortPiston.set(PracticeConstants.SMALL_PISTON_LOW_STATE);
		longPiston.set(PracticeConstants.LARGE_PISTON_LOW_STATE);
	}
	
	protected void moveShooterPistonsMiddle() {
		shortPiston.set(PracticeConstants.SMALL_PISTON_LOW_STATE);
		longPiston.set(PracticeConstants.LARGE_PISTON_HIGH_STATE);
	}
	
	protected void moveShooterPistonsLHigh() {
		shortPiston.set(PracticeConstants.SMALL_PISTON_HIGH_STATE);
		longPiston.set(PracticeConstants.LARGE_PISTON_HIGH_STATE);
	}

}