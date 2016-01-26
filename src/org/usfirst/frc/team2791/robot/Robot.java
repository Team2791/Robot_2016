package org.usfirst.frc.team2791.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import shakerJoystick.Driver;
import shakerJoystick.Operator;
import subsystems.ShakerDriveTrain;
import util.RoboClock;

public class Robot extends IterativeRobot {
    //timers
    private static RoboClock disabledTimer;
    private static RoboClock autonTimer;
    private static RoboClock teleopTimer;
    private static RoboClock powerTimer;
    private static GamePeriod gamePeriod;
    //joysticks
    private static Driver driverJoystick;
    private static Operator operatorJoystick;
    //subsystems
    private static ShakerDriveTrain driveTrain;
    //modes
    private SafetyMode safetyMode;

    //RoboClock stuff
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

    public void robotInit() {
        //Timer inits
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
            safetyMode = SafetyMode.FULL_CONTROL;
        } else {
            safetyMode = SafetyMode.SAFETY;

        }
    }

    public void autonomousPeriodic() {
        super.autonomousPeriodic();


    }

    public void teleopPeriodic() {
        super.teleopPeriodic();

    }

    public void disabledPeriodic() {
        super.disabledPeriodic();

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

    //ENUMS
    public enum GamePeriod {
        AUTONOMOUS, TELEOP, DISABLED
    }

    public enum SafetyMode {
        SAFETY, FULL_CONTROL
    }

    public enum DriveType {
        TANK, ARCADE, GTA
    }
}