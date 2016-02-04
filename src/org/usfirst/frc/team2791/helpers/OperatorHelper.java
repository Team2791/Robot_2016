package org.usfirst.frc.team2791.helpers;

import static org.usfirst.frc.team2791.robot.Robot.*;

/**
 * Created by Akhil on 1/28/2016.
 */
public class OperatorHelper extends ShakerHelper {
    double whenShotBall = 0;
    boolean shooterIsReset = false;
    private int shooterSpeedIndex = 0;

    public OperatorHelper() {
        // init
    }

    public void teleopRun() {
        // Operator button layout
        if (operatorJoystick.getButtonB()) {
            intake.pullBall();
            shooter.shooterSpeedsWithoutPID(0.5);
        } else if (operatorJoystick.getButtonX()) {
            intake.pushBall();
            shooter.shooterSpeedsWithoutPID(0.5);
        } else {
            intake.stopMotors();
            shooter.stopMotors();
        }
        //intake operations
        if (operatorJoystick.getButtonA())
            intake.extendIntake();
        if (operatorJoystick.getButtonY())
            intake.retractIntake();
        //Shooter operations

        if (operatorJoystick.getButtonSel()) {
            shooter.stopMotors();
        }
        if (operatorJoystick.getDpadUp())
            shooter.autoFire();

        // Start button to reset to teleop start
        if (operatorJoystick.getButtonSt())
            reset();

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

//    private void autoShootHigh() {
//        intake.extendIntake();
//        shooter.setShooterHigh();
//        shooter.shooterSpeedWithPID(3);
//    }
}
