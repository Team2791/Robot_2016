package org.usfirst.frc.team2791.robot;

import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerDriveTrain;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerIntake;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerShooter;
import org.usfirst.frc.team2791.commands.AutoLineUpShot;
import org.usfirst.frc.team2791.helpers.AutonHelper;
import org.usfirst.frc.team2791.helpers.TeleopHelper;
import org.usfirst.frc.team2791.practiceSubsystems.PracticeShakerDriveTrain;
import org.usfirst.frc.team2791.practiceSubsystems.PracticeShakerIntake;
import org.usfirst.frc.team2791.practiceSubsystems.PracticeShakerShooter;
import org.usfirst.frc.team2791.shakerJoystick.Driver;
import org.usfirst.frc.team2791.shakerJoystick.Operator;
import org.usfirst.frc.team2791.util.Constants;
import org.usfirst.frc.team2791.util.ShakerCamera;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
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
	public static AbstractShakerShooter shooter;
	public static AbstractShakerIntake intake;
	public static AbstractShakerDriveTrain driveTrain;

	// camera
	public static ShakerCamera camera;
	// other
	public static Compressor compressor;
	public Thread shooterThread;
	public Thread cameraThread;
	public Thread driveTrainThread;
	// helpers
	private TeleopHelper teleopHelper;
	private AutonHelper autonHelper;

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

		// subsystems
		driveTrain = new PracticeShakerDriveTrain();
		driveTrainThread = new Thread(driveTrain);
		driveTrainThread.start();
		intake = new PracticeShakerIntake();
		shooter = new PracticeShakerShooter();
		// shooter = PracticeShakerShooter.getInstance();

		// competition robot
		// driveTrain = ShakerDriveTrain.getInstance();
		// intake = ShakerIntake.getInstance();
		// shooter = ShakerShooter.getInstance();

		// Camera and shooter are put on their own thread to prevent
		// interference with main robot code
		shooterThread = new Thread(shooter);
		shooterThread.start();

		camera = ShakerCamera.getInstance();
		cameraThread = new Thread(camera);
		cameraThread.start();

		autonHelper = AutonHelper.getInstance();
		teleopHelper = TeleopHelper.getInstance();

		compressor = new Compressor(Constants.PCM_MODULE);

		SmartDashboard.putNumber("shooter offset", AutoLineUpShot.shootOffset);
		SmartDashboard.putBoolean("DEBUGGING MODE", debuggingMode);
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
		autonHelper.run();
		autonHelper.updateSmartDash();
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
		autonHelper.disableRun();
		alwaysUpdatedSmartDashValues();

		if (operatorJoystick.getButtonSt()){
			driveTrain.resetEncoders();
//			driveTrain.calibrateGyro();
			}

		if (operatorJoystick.getButtonSel()) {
			System.out.println("Resetting Auton step counter...");
			autonHelper.resetAutonStepCounter();
			System.out.println("Done...");
		}
		AutoLineUpShot.reset();
	}

	private void alwaysUpdatedSmartDashValues() {
		SmartDashboard.putNumber("Gyro Rate", driveTrain.getEncoderAngleRate());
		SmartDashboard.putNumber("Gyro angle", driveTrain.getAngle());
//		System.out.println("DriveTrain average velocity "+ driveTrain.getAverageVelocity()+" current distance "+driveTrain.getAvgDist()+" Current gyro Angle "+ driveTrain.getAngle() );
		debuggingMode = SmartDashboard.getBoolean("DEBUGGING MODE");
		AutoLineUpShot.shootOffset = SmartDashboard.getNumber("shooter offset");
		if (debuggingMode) {
			driveTrain.debug();
			intake.debug();
			shooter.debug();
		}
	}

	// ENUMS
	public enum GamePeriod {
		AUTONOMOUS, TELEOP, DISABLED
	}

}