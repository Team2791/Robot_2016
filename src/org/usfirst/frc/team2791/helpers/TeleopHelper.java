package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.subsystems.ShakerIntake;
import org.usfirst.frc.team2791.util.Toggle;

import static org.usfirst.frc.team2791.robot.Robot.*;

/**
 * Created by Akhil on 2/14/2016.
 */
public class TeleopHelper extends ShakerHelper {
    private Toggle shooterPIDToggle;
    private SendableChooser driveTypeChooser;
    private Toggle clawToggle;
    private Toggle extendIntakeToggle;
    private Toggle useArmAttachmentToggle;

    public TeleopHelper() {
        // init
        shooterPIDToggle = new Toggle(false);
        useArmAttachmentToggle = new Toggle(false);
        driveTypeChooser = new SendableChooser();
        SmartDashboard.putData("Drive Chooser", driveTypeChooser);
        driveTypeChooser.addDefault("Tank Drive", "TANK");
        driveTypeChooser.addObject("Arcade Drive", "ARCADE");
        driveTypeChooser.addObject("GTA Drive", "GTA");
        driveTypeChooser.addObject("Single Arcade", "SINGLE_ARCADE");
        clawToggle = new Toggle(false);
        extendIntakeToggle = new Toggle(true);
        useArmAttachmentToggle = new Toggle(false);
    }

    public void teleopRun() {
        operatorRun();
        driverRun();
        sharedRun();

    }

    private void driverRun() {
        // Reads the current drive type to chooser what layout should be used
        // Tank: Left Stick (Y Dir) and Right Stick (Y Dir)
        // GTA: Left Trigger, Right Trigger, Right Stick (Y Dir)
        // Arcade: Left Stick (Y Dir), Right Stick (X Dir)
        switch (getDriveType()) {
            default:
            case TANK:
                driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisRightY());
                break;
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
        // Driver button layout
        // RB HIGH GEAR
        // LB LOW GEAR
        if (driverJoystick.getButtonB())
            driveTrain.setLowGear();
        else
            driveTrain.setHighGear();
        // resets all subsystems under the driver
        if (driverJoystick.getButtonSel())
            driveTrain.reset();
        clawToggle.giveToggleInput(driverJoystick.getButtonX());
        claw.set(clawToggle.get());

        // intake extension toggle
        extendIntakeToggle.giveToggleInput(driverJoystick.getButtonA());
        if (extendIntakeToggle.getToggleOutput())
            // Extend intake
            intake.extendIntake();
        else
            // Retract intake
            intake.retractIntake();

    }

    private void operatorRun() {
        // Operator button layout
        if (operatorJoystick.getButtonB()) {
            // Run intake inward with assistance of the shooter wheel
            shooter.setShooterSpeeds(-1200, true);
            intake.pullBall();
        } else if (operatorJoystick.getButtonX()) {
            // Run reverse if button pressed
            shooter.setShooterSpeeds(1200, true);
            intake.pushBall();
        } else {
            // else run the manual controls
            shooter.setShooterSpeeds(0, false);
//                    shooterPIDToggle.get());
            intake.stopMotors();
        }

        // autofire shooter
        if (operatorJoystick.getButtonA())
            shooter.autoFire(1.0);// currently only runs the servo back and
        // forth


        if (intake.getIntakeState().equals(ShakerIntake.IntakeState.EXTENDED)) {
            if (operatorJoystick.getDpadUp())
                shooter.setShooterHigh();
            if (operatorJoystick.getDpadRight())
                shooter.setShooterMiddle();
            if (operatorJoystick.getDpadDown())
                shooter.setShooterLow();
        }
        if (intake.getIntakeState().equals(ShakerIntake.IntakeState.RETRACTED)) {
            if (operatorJoystick.getDpadUp()) intake.extendIntake();
            if (operatorJoystick.getDpadRight()) intake.extendIntake();
            if (operatorJoystick.getDpadDown()) intake.extendIntake();
        }
//        useArmAttachmentToggle.giveToggleInput(driverJoystick.getButtonY());
//        if (useArmAttachmentToggle.getToggleOutput())
//            intake.setArmAttachmentDown();
//        else
//            intake.setArmAttachmentUp();
//

        // Start button to reset to teleop start
        if (operatorJoystick.getButtonSt())
            reset();
        // toggle the pid
        shooterPIDToggle.giveToggleInput(operatorJoystick.getButtonSel());

//        shooter.setShooterSpeeds(1, true);
    }

    private void sharedRun() {
        // arm attachment
        useArmAttachmentToggle.giveToggleInput(driverJoystick.getButtonY());
        if (useArmAttachmentToggle.getToggleOutput())
            intake.setArmAttachmentDown();
        else
            intake.setArmAttachmentUp();
    }

    @Override
    public void disableRun() {
        // runs disable methods of subsystems that fall under the driver
        driveTrain.disable();
    }

    @Override
    public void updateSmartDash() {
        intake.updateSmartDash();
        shooter.updateSmartDash();
        SmartDashboard.putBoolean("Shooter PID", shooterPIDToggle.getToggleOutput());
        // updateSmartDash the smartDashboard values of subsystems
        driveTrain.updateSmartDash();
        SmartDashboard.putString("Current Driver Input:", getDriveType().toString());

    }

    @Override
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
