package org.usfirst.frc.team2791.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.commands.AutoLineUpShot;
import org.usfirst.frc.team2791.commands.ShakerCommand;
import org.usfirst.frc.team2791.helpers.AutonHelper;
import org.usfirst.frc.team2791.helpers.TeleopHelper;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerDriveTrain;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerIntake;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerShooter;
import org.usfirst.frc.team2791.shakerJoystick.Driver;
import org.usfirst.frc.team2791.shakerJoystick.Operator;
import org.usfirst.frc.team2791.util.Constants;
import org.usfirst.frc.team2791.util.ShakerCamera;

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
    public static PracticeShakerShooter shooter;
    public static PracticeShakerIntake intake;
    public static PracticeShakerDriveTrain driveTrain;

    // camera
    public static ShakerCamera camera;
    // other
    public static Compressor compressor;
    public Thread shooterThread;
    public Thread cameraThread;
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
        driveTrain = PracticeShakerDriveTrain.getInstance();
        intake = PracticeShakerIntake.getInstance();
        shooter = PracticeShakerShooter.getInstance();
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
        super.teleopPeriodic();
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

        if (operatorJoystick.getButtonSt())
            driveTrain.calibrateGyro();

        if (operatorJoystick.getButtonSel()) {
            System.out.println("Resetting Gyro and encoders ");
            driveTrain.resetGyro();
            driveTrain.resetEncoders();
        }
        AutoLineUpShot.reset();
    }

    private void alwaysUpdatedSmartDashValues() {
        SmartDashboard.putNumber("Gyro Rate", driveTrain.getGyroRate());
        SmartDashboard.putNumber("Current gyro angle", driveTrain.getAngle());
        debuggingMode = SmartDashboard.getBoolean("DEBUGGING MODE");
        AutoLineUpShot.shootOffset = SmartDashboard.getNumber("shooter offset");
        if (debuggingMode) {
            driveTrain.debug();
            intake.debug();
            shooter.debug();
            teleopHelper.debug();
            autonHelper.debug();
        }
        intake.updateSmartDash();
        shooter.updateSmartDash();
        driveTrain.updateSmartDash();
    }

    // ENUMS
    public enum GamePeriod {
        AUTONOMOUS, TELEOP, DISABLED
    }

}