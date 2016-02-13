package org.usfirst.frc.team2791.configuration;

public class Ports {
	// ANALOG
	public static final int BALL_DISTANCE_SENSOR_PORT = 3;

	// DIO
	public static final int LEFT_DRIVE_ENCODER_PORT_A = 2;
	public static final int LEFT_DRIVE_ENCODER_PORT_B = 3;
	public static final int RIGHT_DRIVE_ENCOODER_PORT_A = 0;
	public static final int RIGHT_DRIVE_ENCODER_PORT_B = 1;

	// PWM PORTS
	public static final int DRIVE_TALON_LEFT_PORT_FRONT = 1;
	public static final int DRIVE_TALON_LEFT_PORT_BACK = 2;
	public static final int DRIVE_TALON_RIGHT_PORT_FRONT = 0;
	public static final int DRIVE_TALON_RIGHT_PORT_BACK = 3;
	public static final int INTAKE_TALON_LEFT_PORT = 8;
	public static final int INTAKE_TALON_RIGHT_PORT = 9;
	public static final int BALL_AID_SERVO_PORT = 4;

	// PCM PORTS

	public static final int DRIVE_PISTON_FORWARD = 0;
	public static final int DRIVE_PISTON_REVERSE = 1;
	public static final int INTAKE_PISTON_CHANNEL_FORWARD = 2;
	public static final int INTAKE_PISTON_CHANNEL_REVERSE = 3;
	public static final int SHOOTER_PISTON_CHANNEL_FIRST_LEVEL_FORWARD = 4;
	public static final int SHOOTER_PISTON_CHANNEL_FIRST_LEVEL_REVERSE = 5;
	// public static final int SHOOTER_PISTON_CHANNEL_SECOND_LEVEL_CHANNEL = 0;
	// //Relay port
	public static final int SHOOTER_PISTON_CHANNEL_SECOND_LEVEL_FORWARD = 6;
	public static final int SHOOTER_PISTON_CHANNEL_SECOND_LEVEL_REVERSE = 7;
	public static final int INTAKE_ARM_CHANNEL_FORWARD = 6;
	public static final int INTAKE_ARM_CHANNEL_REVERSE = 7;
	public static final int CLAW_LEVEL_ONE_SOLENOID_PORT = 8;
	public static final int CLAW_LEVEL_TWO_SOLENOID_PORT = 9;
	// CAN Ports
	public static final int PCM_MODULE = 20;
	public static final int SECOND_PCM_MODULE = 21;
	public static final int SHOOTER_TALON_RIGHT_PORT = 10;
	public static final int SHOOTER_TALON_LEFT_PORT = 11;
}
