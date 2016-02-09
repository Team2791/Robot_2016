package org.usfirst.frc.team2791.helpers;

import static org.usfirst.frc.team2791.robot.Robot.*;

public class OperatorHelper extends ShakerHelper {
    private double whenShotBall = 0;
    private boolean shooterIsReset = false;
    private int shooterSpeedIndex = 0;

    public OperatorHelper() {
        // init
    }

    public void teleopRun() {
        // Operator button layout
        //Run intake inward with assistance of the shooter wheel   	

        if (operatorJoystick.getButtonB()) {
        	shooter.setShooterSpeeds(-1, false);
            intake.pullBall();
        }
        //Run reverse if button pressed
        else if (operatorJoystick.getButtonX()) {
        	shooter.setShooterSpeeds(1, false);
            intake.pushBall();
        }
        //else stop
        else {
            shooter.setShooterSpeeds(operatorJoystick.getAxisRT() - operatorJoystick.getAxisLT(),operatorJoystick.getButtonRB());
            intake.stopMotors();
        }
        //Extend intake
        if (operatorJoystick.getButtonA())
            intake.extendIntake();
        //Retract intake
        if (operatorJoystick.getButtonY())
            intake.retractIntake();
        //Run shooters without pid only meant for testing
        System.out.println(operatorJoystick.getAxisRT() - operatorJoystick.getAxisLT());
        //run servo motor to push ball into spinning wheels (meant as a manual mode) else reset servo back to 0 position
//        if (operatorJoystick.getDpadDown())
//            shooter.pushBall();
//        else
//            shooter.resetServoAngle();
        //Fail safe meant to stop motors
        if (operatorJoystick.getButtonSel()) {
            shooter.stopMotors();
        }
        //autofire shooter
        if (operatorJoystick.getDpadUp())
            shooter.autoFire(1.0);
        //movement of arm attachment
        if (operatorJoystick.getDpadRight())
            intake.setArmAttachmentUp();
        if (operatorJoystick.getDpadLeft())
            intake.setArmAttachmentDown();
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

}
