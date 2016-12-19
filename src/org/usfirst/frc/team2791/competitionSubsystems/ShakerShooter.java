package org.usfirst.frc.team2791.competitionSubsystems;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerShooter;
import org.usfirst.frc.team2791.util.Constants;

public class ShakerShooter extends AbstractShakerShooter {
    private DoubleSolenoid shortPiston;
    private DoubleSolenoid longPiston;

    public ShakerShooter() {
        super();
        longPiston = new DoubleSolenoid(Constants.PCM_MODULE, Constants.LONG_PISTON_FORWARD,
                Constants.LONG_PISTON_REVERSE);
        shortPiston = new DoubleSolenoid(Constants.PCM_MODULE, Constants.SHORT_PISTON_FORWARD,
                Constants.SHORT_PISTON_REVERSE);
        leftShooterTalon = new CANTalon(Constants.SHOOTER_TALON_LEFT_PORT);
        rightShooterTalon = new CANTalon(Constants.SHOOTER_TALON_RIGHT_PORT);
        ballAidServo = new Servo(Constants.BALL_AID_SERVO_PORT);
        ballDistanceSensor = new AnalogInput(Constants.BALL_DISTANCE_SENSOR_PORT);
//        clampServo = new Servo(2);
        init();
    }

    public ShooterHeight getShooterHeight() {
        // get current shooter height by determining which solenoid are true
        if (shortPiston.get().equals(Constants.SMALL_PISTON_HIGH_STATE)
                && longPiston.get().equals(Constants.LARGE_PISTON_HIGH_STATE))
            return ShooterHeight.HIGH;
        else if (longPiston.get().equals(Constants.LARGE_PISTON_HIGH_STATE))
            return ShooterHeight.MID;
        else
            return ShooterHeight.LOW;

    }

    protected void moveShooterPistonsLow() {
        // both pistons will be set to true to get max height
        // both pistons will be set to true to get low height
    	
    	// LOW STATE = both pistons retracted
        shortPiston.set(Constants.SMALL_PISTON_HIGH_STATE); // was reverse
        // //this is short
        // one
        longPiston.set(Constants.LARGE_PISTON_HIGH_STATE);
    }

    protected void moveShooterPistonsMiddle() {
        // set shooter height to middle meaning only one piston will be true
    	// long piston extended only (But in high state?)
        shortPiston.set(Constants.SMALL_PISTON_LOW_STATE); //small piston retracted
        longPiston.set(Constants.LARGE_PISTON_HIGH_STATE); //large piston extended
    }

    protected void moveShooterPistonsHigh() {
        // set shooter height to low , set both pistons to false
    	//both pistons extended
        shortPiston.set(Constants.SMALL_PISTON_LOW_STATE);
        longPiston.set(Constants.LARGE_PISTON_LOW_STATE);
        // short needs to switch
    }

}