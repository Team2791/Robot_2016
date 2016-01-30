package org.usfirst.frc.team2791.helpers;

import org.usfirst.frc.team2791.shakerJoystick.Operator;
import org.usfirst.frc.team2791.subsystems.ShakerIntake;
import org.usfirst.frc.team2791.subsystems.ShakerShooter;

/**
 * Created by Akhil on 1/28/2016.
 */
public class OperatorHelper extends ShakerHelper {
    private Operator opJoy;
    private ShakerShooter shooter;
    private ShakerIntake intake;

    public OperatorHelper(Operator operatorJoystick) {
        this.opJoy = operatorJoystick;
        init();
    }

    protected void init() {
        shooter = new ShakerShooter();
        intake = new ShakerIntake();
    }

    public void teleopRun() {

        if (opJoy.getButtonLB())
            intake.pullBall();
        if (opJoy.getButtonRB())
            intake.pushBall();
        if (opJoy.getDpadUp())
            intake.extendIntake();
        if (opJoy.getDpadDown())
            intake.retractIntake();
        if (opJoy.getButtonA())
            shooter.setShooterLow();
        if (opJoy.getButtonB())
            shooter.setShooterMiddle();
        if (opJoy.getButtonY())
            shooter.setShooterHigh();
        if (opJoy.getDpadUp())
            autoShootHigh();
    }

    public void disableRun() {
        intake.disable();
        shooter.disable();
    }

    public void update() {
        intake.update();
        shooter.update();
    }

    public void reset() {
        intake.reset();
        shooter.reset();
    }

    private void autoShootHigh() {
    }
}
