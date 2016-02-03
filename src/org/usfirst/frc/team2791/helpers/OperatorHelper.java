package org.usfirst.frc.team2791.helpers;

import static org.usfirst.frc.team2791.robot.Robot.*;

/**
 * Created by Akhil on 1/28/2016.
 */
public class OperatorHelper extends ShakerHelper {
    private int shooterSpeedIndex = 0;

    public OperatorHelper() {
        // init
    }

    public void teleopRun() {
        // Operator button layout
        // RB - pull ball
        // LB - push Ball
        if (operatorJoystick.getAxisRT() > 0.5)
            intake.pullBall();
        else if (operatorJoystick.getAxisLT() > 0.5)
            intake.pushBall();
        else
            intake.stopMotors();
        // DPAD up - extend intake
        // Dpad down - retract intake
        if (operatorJoystick.getButtonA())
            intake.extendIntake();
        if (operatorJoystick.getButtonY())
            intake.retractIntake();
        if (operatorJoystick.getButtonLB())
            shooterSpeedIndex = shooterSpeedIndex == 0 ? 0 : shooterSpeedIndex--;
        if (operatorJoystick.getButtonRB())
            shooterSpeedIndex = shooterSpeedIndex == 3 ? 3 : shooterSpeedIndex++;
        if (operatorJoystick.getButtonB()) {
            shooter.shooterSpeedsWithoutPID(shooterSpeedIndex);
            shooter.shooterSpeedWithPID(shooterSpeedIndex);
        }
        if (operatorJoystick.getButtonSel()) {
            shooterSpeedIndex = 0;
            shooter.stopMotors();
        }

        // Start button to reset to teleop start
        if (operatorJoystick.getButtonSt())
            reset();

        // if (opJoy.getDpadRight())
        // autoShootHigh();
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

    private void autoShootHigh() {
        intake.extendIntake();
        shooter.setShooterHigh();
        shooter.shooterSpeedWithPID(3);
    }
}
