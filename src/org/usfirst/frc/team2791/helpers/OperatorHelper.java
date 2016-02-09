package org.usfirst.frc.team2791.helpers;

import static org.usfirst.frc.team2791.robot.Robot.*;

public class OperatorHelper extends ShakerHelper {
    private double whenShotBall = 0;
    private boolean shooterIsReset = false;
    private int shooterSpeedIndex = 0;
    private boolean useShooterPID = false;

    public OperatorHelper() {
        // init
    }

    public void teleopRun() {
        // Operator button layout
        if (operatorJoystick.getButtonB()) {
            //Run intake inward with assistance of the shooter wheel
            shooter.setShooterSpeeds(-1, false);
            intake.pullBall();
        } else if (operatorJoystick.getButtonX()) {
            //Run reverse if button pressed
            shooter.setShooterSpeeds(1, false);
            intake.pushBall();
        } else {
            //else run the manual controls
            shooter.setShooterSpeeds(operatorJoystick.getAxisRT() - operatorJoystick.getAxisLT(), useShooterPID);
            intake.stopMotors();
        }
        //Extend intake
        if (operatorJoystick.getButtonA())
            intake.extendIntake();
        //Retract intake
        if (operatorJoystick.getButtonY())
            intake.retractIntake();
        //autofire shooter
        if (operatorJoystick.getDpadUp())
            shooter.autoFire(1.0);//currently only runs the servo back and forth

        //movement of arm attachment
        if (operatorJoystick.getDpadRight())
            intake.setArmAttachmentUp();
        if (operatorJoystick.getDpadLeft())
            intake.setArmAttachmentDown();
        // Start button to reset to teleop start
        if (operatorJoystick.getButtonSt())
            reset();
        if (operatorJoystick.getButtonSel())
            useShooterPID = !useShooterPID;
    }

    public void disableRun() {
        intake.disable();
        shooter.disable();
    }

    public void updateSmartDash() {
        intake.updateSmartDash();
        shooter.updateSmartDash();
    }

    public void reset() {
        shooterSpeedIndex = 0;
        shooter.reset();
        intake.reset();
    }

}
