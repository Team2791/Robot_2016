package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.commands.AutoLineUpShot;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerShooter;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerShooter.ShooterHeight;
import org.usfirst.frc.team2791.util.Toggle;

import static org.usfirst.frc.team2791.robot.Robot.*;

//import org.usfirst.frc.team2791.subsystems.ShakerIntake;

/**
 * Created by Akhil on 2/14/2016.
 */
public class TeleopHelper extends ShakerHelper {
    private static TeleopHelper teleop;
    private SendableChooser driveTypeChooser;
    private Toggle useArmAttachmentToggle;

    private TeleopHelper() {
        // init
        // smartdashboard drop down menu
        driveTypeChooser = new SendableChooser();
        SmartDashboard.putData("Drive Chooser", driveTypeChooser);
        driveTypeChooser.addObject("Tank Drive", "TANK");
        driveTypeChooser.addObject("Arcade Drive", "ARCADE");
        driveTypeChooser.addDefault("GTA Drive", "GTA");
        driveTypeChooser.addObject("Single Arcade", "SINGLE_ARCADE");
        SmartDashboard.putNumber("Shooter Speeds Setpoint range table", 0);

        // toggles, to prevent sending a subsystem a value too many times
        // this is sort of like a light switch
        useArmAttachmentToggle = new Toggle(false);
        camera.setCameraValues(1, 1);
    }

    public static TeleopHelper getInstance() {
        if (teleop == null)
            teleop = new TeleopHelper();
        return teleop;
    }

    public void run() {
        // runs the three subsystems controls
        operatorRun();// runs the operator controls
        driverRun();// runs the driver controls
        sharedRun();// runs the subsystems that are shared by both
    }

    private void driverRun() {
        if (!AutoLineUpShot.isRunning() || (AutoLineUpShot.isRunning() && !AutoLineUpShot.getIfShooting())) {
            switch (getDriveType()) {
                case TANK:
                    driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisRightY());
                    break;
                default:
                case GTA:
                    driveTrain.setLeftRight(driverJoystick.getGtaDriveLeft(), driverJoystick.getGtaDriveRight());
                    break;
                case ARCADE:
                    driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisRightX());
                    break;
                case SINGLE_ARCADE:
                    driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisLeftX());
                    break;
            }
            if (driverJoystick.getDpadDown() && !AutoLineUpShot.isRunning()) {
                AutoLineUpShot.run();
                AutoLineUpShot.onlyLineup();
            }
            if (driverJoystick.getButtonB())
                driveTrain.setHighGear();
            else if (driverJoystick.getButtonX())
                driveTrain.setLowGear();
            else
                driveTrain.autoShift(!(driverJoystick.getGtaDriveLeft() == driverJoystick.getGtaDriveRight()));
        }
    }

    private void operatorRun() {
        // Operator button layout
        if (operatorJoystick.getButtonB()) {
            // Run intake inward with assistance of the shooter wheel
            shooter.setShooterSpeeds(-0.85, false);
            intake.pullBall();
        } else if (operatorJoystick.getButtonX()) {
            // Run reverse if button pressed
            shooter.setShooterSpeeds(0.85, false);
            intake.pushBall();

        } else if (!AutoLineUpShot.isRunning() && !shooter.getIfAutoFire()) {
            // else run the manual controls, if it is autofiring this will do
            // nothing
//			shooter.setShooterSpeeds(SmartDashboard.getNumber("Shooter Speeds Setpoint range table"), true);
            shooter.setShooterSpeeds(operatorJoystick.getAxisRT() -
                    operatorJoystick.getAxisLT(), false);
            intake.stopMotors();
//			if(shooter.hasBall()||operatorJoystick.getButtonSel())
//				camera.setCameraValues(1, 1);
        }

        if (operatorJoystick.getButtonRS()) {
            shooter.prepShot();
        }
        if (operatorJoystick.getButtonA()) {
            shooter.autoFire();
        }

        // if the intake is up first set the intake down
        // then run the delayed shooter movement that waits one second
        // before moving the arm
        // this allows time for the intake to go down to prevent collision

        if (operatorJoystick.getDpadUp()) {
            useArmAttachmentToggle.setManual(false);
            intake.extendIntake();
            shooter.delayedShooterPosition(ShooterHeight.HIGH);
            // camera.setCameraValues(1, 1);
        }
        if (operatorJoystick.getDpadRight()) {
            intake.extendIntake();
            useArmAttachmentToggle.setManual(true);
            shooter.delayedShooterPosition(ShooterHeight.MID);

        }
        if (operatorJoystick.getDpadDown()) {
            intake.extendIntake();
            useArmAttachmentToggle.setManual(false);
            camera.setCameraValuesAutomatic();
            shooter.delayedShooterPosition(ShooterHeight.LOW);
        }


        if (operatorJoystick.getButtonRB()) {// actuation of servo arm for
            // shooter
            if (shooter.getIfAutoFire())// if is currently autofiring will
                // override the auto fire
                shooter.overrideAutoShot();
            else
                shooter.pushBall();
        } else if (!shooter.getIfAutoFire())// this just brings the servo back
            // to its place if none of the
            // previous cases apply
            shooter.resetServoAngle();

        if (shooter.getShooterHeight().equals(ShooterHeight.LOW)
                && (operatorJoystick.getButtonSel() || useArmAttachmentToggle.get())) {
            camera.cameraDown();
        } else {
            camera.cameraUp();
        }

        if (shooter.getIfAutoFire() || AutoLineUpShot.isRunning())
            compressor.stop();
        else
            compressor.start();

        if (operatorJoystick.getButtonLB() || AutoLineUpShot.isRunning()) {
            // if operator hits start begin
            if (operatorJoystick.getButtonSt()) {
                shooter.reset();
                AutoLineUpShot.reset();
            } else {
                if (operatorJoystick.getButtonLS())
                    AutoLineUpShot.shooterWithExtraJucice();
                AutoLineUpShot.run();
            }
        }

    }

    private void sharedRun() {
        // intake extension toggle
        if (!shooter.getIfPreppingShot())
            if (driverJoystick.getButtonA() || operatorJoystick.getDpadLeft() || operatorJoystick.getButtonB()
                    || PracticeShakerShooter.delayedArmMove || operatorJoystick.getButtonSel())
                // this runs if intakeing ball too
                intake.extendIntake();
            else// Retract intake
                intake.retractIntake();
        // arm attachment
        useArmAttachmentToggle.giveToggleInput(driverJoystick.getButtonY() || operatorJoystick.getButtonY());
        if (useArmAttachmentToggle.getToggleOutput())
            intake.setArmAttachmentDown();
        else
            intake.setArmAttachmentUp();

    }

    public void disableRun() {
        // runs disable methods of subsystems that fall under the driver
        driveTrain.disable();
        shooter.disable();
        intake.disable();
        AutoLineUpShot.reset();
    }

    public void updateSmartDash() {
        intake.updateSmartDash();
        shooter.updateSmartDash();
        driveTrain.updateSmartDash();
        SmartDashboard.putString("Current Driver Input:", getDriveType().toString());
        SmartDashboard.putNumber("turning value", driverJoystick.getAxisLeftX());
    }

    public void reset() {
        shooter.reset();
        intake.reset();
    }

    public DriveType getDriveType() {
        // reads data of the smart dashboard and converts to enum DriveType
        String driverInputType = (String) driveTypeChooser.getSelected();
        switch (driverInputType) {
            default:
            case "GTA":
                return DriveType.GTA;
            case "ARCADE":
                return DriveType.ARCADE;
            case "TANK":
                return DriveType.TANK;
            case "SINGLE_ARCADE":
                return DriveType.SINGLE_ARCADE;
        }
    }

    public enum DriveType {
        TANK, ARCADE, GTA, SINGLE_ARCADE
    }

}
