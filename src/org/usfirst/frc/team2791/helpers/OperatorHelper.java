package org.usfirst.frc.team2791.helpers;

import org.usfirst.frc.team2791.shakerJoystick.Operator;
import org.usfirst.frc.team2791.subsystems.ShakerIntake;
import org.usfirst.frc.team2791.subsystems.OldShakerShooter;

/**
 * Created by Akhil on 1/28/2016.
 */
public class OperatorHelper extends ShakerHelper {
    private Operator opJoy;
    private OldShakerShooter shooter;
    private ShakerIntake intake;
    private int shooterSpeedIndex = 0;

    public OperatorHelper(Operator operatorJoystick) {
        //init
        this.opJoy = operatorJoystick;
//        shooter = new ShakerShooter();
        intake = new ShakerIntake();
    }

    public void teleopRun() {
        //Operator button layout
        //RB - pull ball
        //LB - push Ball
        if (opJoy.getButtonRB())
            intake.pullBall();
        else if (opJoy.getButtonLB())
            intake.pushBall();
        else intake.stopMotors();
        //DPAD up - extend intake
        //Dpad down - retract intake
        if (opJoy.getButtonA())
            intake.extendIntake();
        if (opJoy.getButtonY())
            intake.retractIntake();
        if(opJoy.getButtonX())
        	
        
        //Start button to reset to teleop start
        if(opJoy.getButtonSt())
        	reset();

        // if (opJoy.getDpadRight())
        // autoShootHigh();
    }

    public void disableRun() {
        intake.disable();
//        shooter.disable();
    }

    public void updateSmartDash() {
        intake.updateSmartDash();
//        shooter.updateSmartDash();
    }

    public void reset() {
//        shooter.reset();
        intake.reset();
    }

    private void autoShootHigh() {
        intake.extendIntake();
        shooter.setShooterHigh();
        shooter.run();
    }
}
