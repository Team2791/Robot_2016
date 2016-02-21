package org.usfirst.frc.team2791.configuration;

public class Ports {
    // ANALOG
    public static final int BALL_DISTANCE_SENSOR_PORT = 0;

    // DIO
    public static final int LEFT_DRIVE_ENCODER_PORT_A = 2;
    public static final int LEFT_DRIVE_ENCODER_PORT_B = 3;
    public static final int RIGHT_DRIVE_ENCOODER_PORT_A = 0;
    public static final int RIGHT_DRIVE_ENCODER_PORT_B = 1;
    public static final int RIGHT_INTAKE_LIMIT_SWITCH = 8;
    public static final int LEFT_INTAKE_LIMIT_SWITCH = 9;
    // PWM PORTS
    public static final int DRIVE_TALON_LEFT_PORT_FRONT = 5;
    public static final int DRIVE_TALON_LEFT_PORT_BACK = 4;
    public static final int DRIVE_TALON_RIGHT_PORT_FRONT = 2;
    public static final int DRIVE_TALON_RIGHT_PORT_BACK = 3;
    public static final int INTAKE_TALON_LEFT_PORT = 6;
    public static final int INTAKE_TALON_RIGHT_PORT = 1;
    public static final int BALL_AID_SERVO_PORT = 9;

    // PCM PORTS
    // First Pcm module
    public static final int DRIVE_SHIFTING_PISTON = 6;
    public static final int INTAKE_PISTON = 7;
    public static final int LONG_PISTON_FORWARD = 2;
    public static final int LONG_PISTON_REVERSE = 3;
//    public static final int INTAKE_ARM_CHANNEL_FORWARD = 6;
//    public static final int INTAKE_ARM_CHANNEL_REVERSE = 7;
    // public static final int SHOOTER_PISTON_CHANNEL_SECOND_LEVEL_CHANNEL = 0;
    // second pcm module
    public static final int SHORT_PISTON_FORWARD = 4;
    public static final int SHORT_PISTON_REVERSE = 5;
    public static final int CLAW_HIGHER_CHANNEL = 0;
    public static final int CLAW_LOWER_CHANNEL= 1;
    // CAN Ports
    public static final int PCM_MODULE = 20;
    public static final int SHOOTER_TALON_RIGHT_PORT = 10;
    public static final int SHOOTER_TALON_LEFT_PORT = 11;
}
