package org.usfirst.frc.team2791.util;

import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * Created by Akhil on 2/24/2016.
 * This class holds all important ports and constants that are used throughout the code
 * This is rarely changed, only when ports are switched or pid's are re-tuned
 */
public class Constants {

	// JOYSTICK PORTS
	public static final int JOYSTICK_DRIVER_PORT = 0;
	public static final int JOYSTICK_OPERATOR_PORT = 1;

	// JOYSTICK CONSTANTS
	public static final double DEADZONE = 0.08;

	// SHOOTER CONTANTS
	public static final DoubleSolenoid.Value SMALL_PISTON_HIGH_STATE = DoubleSolenoid.Value.kReverse;
	public static final DoubleSolenoid.Value SMALL_PISTON_LOW_STATE = DoubleSolenoid.Value.kForward;
	public static final DoubleSolenoid.Value LARGE_PISTON_HIGH_STATE = DoubleSolenoid.Value.kReverse;
	public static final DoubleSolenoid.Value LARGE_PISTON_LOW_STATE = DoubleSolenoid.Value.kForward;

	// INTAKE CONSTANTS
	// ANALOG
	public static final int BALL_DISTANCE_SENSOR_PORT = 0;
	public static final int CLAMPING_SERVO_PORT = 1;
	// DIO
	public static final int LEFT_DRIVE_ENCODER_PORT_A = 1;
	public static final int LEFT_DRIVE_ENCODER_PORT_B = 0;
	public static final int RIGHT_DRIVE_ENCOODER_PORT_A = 9;
	public static final int RIGHT_DRIVE_ENCODER_PORT_B = 8;
	// PWM PORTS
	public static final int DRIVE_TALON_LEFT_PORT_FRONT = 5;
	public static final int DRIVE_TALON_LEFT_PORT_BACK = 4;
	public static final int DRIVE_TALON_LEFT_PORT_C = 7;
	public static final int DRIVE_TALON_RIGHT_PORT_FRONT = 2;
	public static final int DRIVE_TALON_RIGHT_PORT_BACK = 3;
	public static final int DRIVE_TALON_RIGHT_PORT_C = 0;
	public static final int INTAKE_TALON_LEFT_PORT = 6;
	public static final int INTAKE_TALON_RIGHT_PORT = 1;
	public static final int BALL_AID_SERVO_PORT = 9;
	// PCM PORTS
	// First Pcm module
	public static final int INTAKE_PISTON = 7;
	public static final int LONG_PISTON_FORWARD = 2;
	public static final int LONG_PISTON_REVERSE = 3;//changed to port 1, 3 is plugged
	public static final int SHORT_PISTON_FORWARD = 4;
	public static final int SHORT_PISTON_REVERSE = 5;
	public static final int FUN_BRIDGE_ARM_PORT = 0;
	// CAN
	public static final int PCM_MODULE = 20;
	public static final int SHOOTER_TALON_RIGHT_PORT = 10;
	public static final int SHOOTER_TALON_LEFT_PORT = 11;
	// PID VALUES
	// Shooter PID
	public static double SHOOTER_P = 0.4;
	public static double SHOOTER_I = 0.001;
	public static double SHOOTER_D = 0;
	public static double DRIVE_ANGLE_P = 0.05;
	public static double DRIVE_ANGLE_I = 0;
	public static double DRIVE_ANGLE_D = 0.000;
	public static double STATIONARY_ANGLE_P = 0.15;
	public static double STATIONARY_ANGLE_I = 1.2;
	public static double STATIONARY_ANGLE_D = 0.0;
	public static double DRIVE_DISTANCE_P = 1.3;
	public static double DRIVE_DISTANCE_I = 0.01;
	public static double DRIVE_DISTANCE_D = 0.2;

}