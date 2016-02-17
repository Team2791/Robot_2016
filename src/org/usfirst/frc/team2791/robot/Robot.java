package org.usfirst.frc.team2791.robot;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.helpers.AutonHelper;
import org.usfirst.frc.team2791.helpers.TeleopHelper;
import org.usfirst.frc.team2791.shakerJoystick.Driver;
import org.usfirst.frc.team2791.shakerJoystick.Operator;
import org.usfirst.frc.team2791.subsystems.ShakerClaw;
import org.usfirst.frc.team2791.subsystems.ShakerDriveTrain;
import org.usfirst.frc.team2791.subsystems.ShakerIntake;
import org.usfirst.frc.team2791.subsystems.ShakerShooter;
import org.usfirst.frc.team2791.util.RoboClock;
import org.usfirst.frc.team2791.util.ShakerAccelerometer;
import org.usfirst.frc.team2791.util.ShakerCamera;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

public class Robot extends IterativeRobot {
	// modes
	public static SafetyMode safetyMode;
	public static GamePeriod gamePeriod;
	// Joysticks
	public static Driver driverJoystick;
	public static Operator operatorJoystick;
	// operator subsystems
	public static ShakerShooter shooter;
	public static ShakerIntake intake;
	public static ShakerClaw claw;
	// driver subsystems
	public static ShakerDriveTrain driveTrain;
	// timers
	public static RoboClock disabledTimer;
	public static RoboClock autonTimer;
	public static RoboClock teleopTimer;
	public static RoboClock powerTimer;
	private CameraServer camServer;

	public Thread shooterThread;
	// public DigitalOutput ledDio;
	// helpers
	private TeleopHelper teleopHelper;
	private AutonHelper autonHelper;
	// other
	private Compressor compressor;
	private SendableChooser safeModeChooser;
	private ShakerCamera cam;
	private ShakerCamera cam2;
	private ShakerAccelerometer acc;
	// RoboClock stuff
	public static RoboClock getPowerTimer() {
		return powerTimer;
	}

	public static RoboClock getCurrentModeTimer() {
		switch (gamePeriod) {
		case AUTONOMOUS:
			return autonTimer;
		case TELEOP:
			return teleopTimer;
		case DISABLED:
			return disabledTimer;
		default:
			return null;
		}
	}

	// MAIN ROBOT CODE
	@Override
	public void robotInit() {
		// Timer inits
		disabledTimer = new RoboClock();
		disabledTimer.setName("Disabled Timer");

		teleopTimer = new RoboClock();
		teleopTimer.setName("Teleop Timer");

		autonTimer = new RoboClock();
		autonTimer.setName("Auton Timer");

		powerTimer = new RoboClock();
		powerTimer.setName("Power timer");

		gamePeriod = GamePeriod.DISABLED;

		driverJoystick = new Driver();
		operatorJoystick = new Operator();
		driveTrain = new ShakerDriveTrain();

		safetyMode = SafetyMode.SAFETY;

		shooter = new ShakerShooter();
		shooterThread = new Thread(shooter);
		shooterThread.start();
		intake = new ShakerIntake();
		// ledDio = new DigitalOutput(9);
		claw = new ShakerClaw();
		cam = new ShakerCamera("cam1", false);
		
//		 cam2 = new ShakerCamera("cam1", false);
		autonHelper = new AutonHelper();
		teleopHelper = new TeleopHelper();

		compressor = new Compressor();
		safeModeChooser = new SendableChooser();
		SmartDashboard.putData("Safe Mode Chooser", safeModeChooser);
		safeModeChooser.addDefault("Safe Mode", "SAFE");
		safeModeChooser.addObject("Test Mode (Partial Safety)", "TEST");
		safeModeChooser.addObject("Full Mode", "FULL");

		acc = new ShakerAccelerometer();
//		camServer = CameraServer.getInstance();
//		camServer.startAutomaticCapture("cam1");
	}

	@Override
	public void autonomousInit() {
		gamePeriod = GamePeriod.AUTONOMOUS;
		autonTimer.start();
	}

	@Override
	public void teleopInit() {
		gamePeriod = GamePeriod.TELEOP;
		if (DriverStation.getInstance().isFMSAttached()) {
			safetyMode = SafetyMode.FULL_CONTROL;
		} else {
			switch ((String) safeModeChooser.getSelected()) {
			case "FULL":
				safetyMode = SafetyMode.FULL_CONTROL;
				break;
			case "TEST":
				safetyMode = SafetyMode.TEST;
				break;
			case "SAFE":
				safetyMode = SafetyMode.SAFETY;
				break;
			}

		}

	}

	@Override
	public void disabledInit() {
		gamePeriod = GamePeriod.DISABLED;

	}

	@Override
	public void autonomousPeriodic() {
		super.autonomousPeriodic();
		autonHelper.run();
		autonHelper.updateSmartDash();
	}

	@Override
	public void teleopPeriodic() {
		super.teleopPeriodic();
		teleopHelper.run();
		cam.update();
		teleopHelper.updateSmartDash();
		compressor.start();
		SmartDashboard.putNumber("Acceleromter in x dir", acc.getX());
		SmartDashboard.putNumber("Acceleromter in y dir", acc.getY());
		SmartDashboard.putNumber("Acceleromter in z dir", acc.getZ());
		SmartDashboard.putNumber("Angle to floor ", acc.getAngleOffGround());

		// ledDio.set(true);
	}

	@Override
	public void disabledPeriodic() {
		super.disabledPeriodic();
		teleopHelper.disableRun();
		compressor.stop();
		autonHelper.disableRun();
		if(operatorJoystick.getButtonSt())
			driveTrain.calibrateGyro();
		// ledDio.set(false);
	}

	public RoboClock getTeleopTimer() {
		return teleopTimer;
	}

	public RoboClock getAutonTimer() {
		return autonTimer;
	}

	public RoboClock getDisabledTimer() {
		return disabledTimer;
	}

	// ENUMS
	public enum GamePeriod {
		AUTONOMOUS, TELEOP, DISABLED
	}

	public enum SafetyMode {
		SAFETY, TEST, FULL_CONTROL
	}

}