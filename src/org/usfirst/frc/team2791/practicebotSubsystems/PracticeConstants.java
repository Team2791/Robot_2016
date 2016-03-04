package org.usfirst.frc.team2791.practicebotSubsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class PracticeConstants {
    // ROBOCLOCK
    public static final double CODE_EXECUTION_PERIOD = 0.02;

    // JOYSTICK PORTS
    public static final int JOYSTICK_DRIVER_PORT = 0;
    public static final int JOYSTICK_OPERATOR_PORT = 1;

    // JOYSTICK CONSTANTS
    public static final double DEADZONE = 0.08;
    public static final double AXIS_SCALE = 1.0;

    // ROBOT DRIVE CONSTANTS
    public static final double FULL_SPEED_SAFETY_MODE = 0.50;
    // sets max speed percentage during safety mode
    public static final DoubleSolenoid.Value DRIVE_LOW_GEAR = DoubleSolenoid.Value.kReverse;
    // this is the solenoid value for low gear
    public static final DoubleSolenoid.Value DRIVE_HIGH_GEAR = DoubleSolenoid.Value.kForward;
    // this is the solenoid value for high gear

    // SHOOTER CONTANTS
    public static final double MAX_SHOOTER_SPEED = 1.0;
    public static final DoubleSolenoid.Value SMALL_PISTON_HIGH_STATE = DoubleSolenoid.Value.kForward;
    public static final DoubleSolenoid.Value SMALL_PISTON_LOW_STATE = DoubleSolenoid.Value.kReverse;
    public static final DoubleSolenoid.Value LARGE_PISTON_HIGH_STATE = DoubleSolenoid.Value.kReverse;
    public static final DoubleSolenoid.Value LARGE_PISTON_LOW_STATE = DoubleSolenoid.Value.kForward;
    public static final int SERVO_PUSH_ANGLE = 70;
    public static final int SERVO_DEFAULT_ANGLE = 0;
    public static final double THRESHOLD_BALL_DISTANCE = 0.250;

    // INTAKE CONSTANTS
    public static final double INTAKE_SPEED = 1.0;
    public static final DoubleSolenoid.Value INTAKE_RECTRACTED_VALUE = DoubleSolenoid.Value.kReverse;
    public static final DoubleSolenoid.Value INTAKE_EXTENDED_VALUE = DoubleSolenoid.Value.kForward;
    public static final DoubleSolenoid.Value INTAKE_ARM_UP_VALUE = DoubleSolenoid.Value.kReverse;
    public static final DoubleSolenoid.Value INTAKE_ARM_DOWN_VALUE = DoubleSolenoid.Value.kForward;
    //CLAW CONSTANTS
    public static final DoubleSolenoid.Value CLAW_RETRACTED_VALUE = DoubleSolenoid.Value.kForward;
    public static final DoubleSolenoid.Value CLAW_EXTENDED_VALUE = DoubleSolenoid.Value.kReverse;

}
