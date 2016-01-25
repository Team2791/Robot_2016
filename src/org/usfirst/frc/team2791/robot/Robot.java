package org.usfirst.frc.team2791.robot;

import configuration.Camera;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import shakerJoystick.Driver;
import shakerJoystick.Operator;
import subsystems.DriveTrain;
import util.RoboClock;

public class Robot extends IterativeRobot {

    private AxisCamera cam;
    private static RoboClock disabledTimer;
    private static RoboClock autonTimer;
    private static RoboClock teleopTimer;
    private static RoboClock powerTimer;
    private static GamePeriod gamePeriod;
    private SafetyMode safetyMode;
    private Driver driverJoystick;
    private Operator operatorJoystick;
    private DriveTrain driveTrain;


    public static RoboClock getPowerTimer() {
        return powerTimer;
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
    public static RoboClock getCurrentModeTimer(){
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
    
    public void robotInit() {
        disabledTimer = new RoboClock();
        disabledTimer.setName("Disabled Timer");

        teleopTimer = new RoboClock();
        teleopTimer.setName("Teleop Timer");

        autonTimer = new RoboClock();
        autonTimer.setName("Auton Timer");

        powerTimer = new RoboClock();
        powerTimer.setName("Power timer");

        gamePeriod = GamePeriod.DISABLED;

        cam = new AxisCamera(Camera.cameraPort);
        driverJoystick = new Driver();
        operatorJoystick = new Operator();
        driveTrain.init(driverJoystick, operatorJoystick, DriveTrain.driveType.TANK);

    }

    public void disabledInit() {
        gamePeriod = GamePeriod.DISABLED;
    }

    public void autonomousInit() {
        gamePeriod = GamePeriod.AUTONOMOUS;
    }

    public void teleopInit() {
        gamePeriod = GamePeriod.TELEOP;
        if (DriverStation.getInstance().isFMSAttached()) {
            driveTrain.setSafetyMode(SafetyMode.FULL_CONTROL);
        } else {
            driveTrain.setSafetyMode(SafetyMode.SAFETY);
        }

    }

    public void disabledPeriodic() {
        super.disabledPeriodic();
    }

    public void autonomousPeriodic() {
        super.autonomousPeriodic();

    }

    public void teleopPeriodic() {
        super.teleopPeriodic();
        driveTrain.runTeleop();
    }

    public enum GamePeriod {
        AUTONOMOUS, TELEOP, DISABLED
    }

    public enum SafetyMode {
        SAFETY, FULL_CONTROL
    }


}
