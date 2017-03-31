package org.usfirst.frc.team2791.robot;

import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerDriveTrain;
import org.usfirst.frc.team2791.competitionSubsystems.ShakerDriveTrain;
import org.usfirst.frc.team2791.helpers.TeleopHelper;
import org.usfirst.frc.team2791.shakerJoystick.Driver;
import org.usfirst.frc.team2791.shakerJoystick.Operator;
import org.usfirst.frc.team2791.util.ADXRS453Gyro;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	public static final boolean COMPETITION_ROBOT = true;
	public static boolean debuggingMode = false;
	// Modes
	public static GamePeriod gamePeriod;
	// Joysticks
	public static Driver driverJoystick;
	public static Operator operatorJoystick;

	// Subsystems
	// Competition robot subsystems
	// public static ShakerShooter shooter;
	// public static ShakerIntake intake;
	// public static ShakerDriveTrain driveTrain;
	// Practice Robot susbsystems

	public static AbstractShakerDriveTrain driveTrain;

	// camera
	// other
	public static ADXRS453Gyro gyro;
	public static Compressor compressor;
	public Thread shooterThread;
	public Thread cameraThread;
	public Thread driveTrainThread;
	// helpers
	private TeleopHelper teleopHelper;

	// MAIN ROBOT CODE
	public void robotInit() {
		// game period changed when ever game mode changes
		// (TELOP,AUTON,DISABLED,ETC.)
		System.out.println("Starting to init my systems.");
		gamePeriod = GamePeriod.DISABLED;

		// Singletons - only one instance of them is created
		// Shaker joysticks
		driverJoystick = Driver.getInstance();
		operatorJoystick = Operator.getInstance();
		driveTrain = new ShakerDriveTrain();


		driveTrainThread = new Thread(driveTrain);
		driveTrainThread.start();

		teleopHelper = TeleopHelper.getInstance();

		gyro = new ADXRS453Gyro();
		//new Thread(gyro).start();
		gyro.startThread();

	}

	public void autonomousInit() {
		gamePeriod = GamePeriod.AUTONOMOUS;

	}

	public void teleopInit() {
		gamePeriod = GamePeriod.TELEOP;
	}

	public void disabledInit() {
		gamePeriod = GamePeriod.DISABLED;
	}

	public void autonomousPeriodic() {
		super.autonomousPeriodic();
		
		alwaysUpdatedSmartDashValues();
	}

	public void teleopPeriodic() {
		teleopHelper.run();
		teleopHelper.updateSmartDash();
		alwaysUpdatedSmartDashValues();
	}

	public void disabledPeriodic() {
		super.disabledPeriodic();
		teleopHelper.disableRun();
		compressor.stop();
		
		alwaysUpdatedSmartDashValues();

		if (operatorJoystick.getButtonSt()) {
			driveTrain.resetEncoders();
			driveTrain.calibrateGyro();
			gyro.calibrate();
		}
	}

	private void alwaysUpdatedSmartDashValues() {
		SmartDashboard.putNumber("Gyro Rate", driveTrain.getEncoderAngleRate());
		if(driverJoystick.getButtonSt()){
			System.out.println("Starting calibration");
			gyro.calibrate();
			System.out.println("Calibration complete");
		}
		SmartDashboard.putNumber("Gyro angle", gyro.getAngle());
		// System.out.println("DriveTrain average velocity "+
		// driveTrain.getAverageVelocity()+" current distance
		// "+driveTrain.getAvgDist()+" Current gyro Angle "+
		// driveTrain.getAngle() );
		
		if (debuggingMode) {
			driveTrain.debug();
		}
	}

	// ENUMS
	public enum GamePeriod {
		AUTONOMOUS, TELEOP, DISABLED
	}

}