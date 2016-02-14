package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.subsystems.ShakerIntake.IntakeState;
import org.usfirst.frc.team2791.util.Toggle;

import static org.usfirst.frc.team2791.robot.Robot.*;

public class OperatorHelper extends ShakerHelper {
    private double whenShotBall = 0;
    private boolean shooterIsReset = false;
    private int shooterSpeedIndex = 0;
    private Toggle shooterPIDToggle;
    private int shooterLevel = 0;

    public OperatorHelper() {
        // init
        shooterPIDToggle = new Toggle(false);
        switch (shooter.getShooterHeight()) {
            case LOW:
                shooterLevel = 0;
            case MID:
                shooterLevel = 1;
            case HIGH:
                shooterLevel = 2;
        }
    }

    @Override
    public void teleopRun() {
        // Operator button layout
        if (operatorJoystick.getButtonB()) {
            // Run intake inward with assistance of the shooter wheel
            shooter.setShooterSpeeds(-1, false);
            intake.pullBall();
        } else if (operatorJoystick.getButtonX()) {
            // Run reverse if button pressed
            shooter.setShooterSpeeds(1, false);
            intake.pushBall();
        } else {
            // else run the manual controls
            shooter.setShooterSpeeds(operatorJoystick.getAxisRT() - operatorJoystick.getAxisLT(),
                    shooterPIDToggle.get());
            intake.stopMotors();
        }

        // autofire shooter
        if (operatorJoystick.getButtonA())
            shooter.autoFire(1.0);// currently only runs the servo back and
        // forth
        if (operatorJoystick.getButtonRB())
            shooterLevel++;
        if (operatorJoystick.getButtonLB())
            shooterLevel--;
        shooterLevel = shooterLevel > 2 ? 2 : shooterLevel;
        shooterLevel = shooterLevel < 0 ? 0 : shooterLevel;

        if (intake.getIntakeState().equals(IntakeState.EXTENDED)) {
            if (operatorJoystick.getDpadUp())
                shooter.setShooterHigh();
            if (operatorJoystick.getDpadRight())
                shooter.setShooterMiddle();
            if (operatorJoystick.getDpadDown())
                shooter.setShooterLow();
        }
        // else{
        // intake.extendIntake();
        // }
        // toggle arm attachments
        // if (intake.getIntakeState().equals(IntakeState.EXTENDED))
        // switch (shooterLevel) {
        // case 0:
        // shooter.setShooterLow();
        // break;
        // case 1:
        // shooter.setShooterMiddle();
        // break;
        // // case 2:
        // // shooter.setShooterHigh();
        // // break;
        // }

        // Start button to reset to teleop start
        if (operatorJoystick.getButtonSt())
            reset();
        // toggle the pid
        shooterPIDToggle.giveToggleInput(operatorJoystick.getButtonSel());

    }

    @Override
    public void disableRun() {
        intake.disable();
        shooter.disable();
    }

    @Override
    public void updateSmartDash() {
        intake.updateSmartDash();
        shooter.updateSmartDash();
        SmartDashboard.putBoolean("Shooter PID", shooterPIDToggle.getToggleOutput());
        SmartDashboard.putNumber("Shooter Height SetPoint", shooterLevel);
    }

    @Override
    public void reset() {
        shooterSpeedIndex = 0;
        shooter.reset();
        intake.reset();
    }

}
