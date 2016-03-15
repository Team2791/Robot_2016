package org.usfirst.frc.team2791.helpers.autonModes;

import static org.usfirst.frc.team2791.robot.Robot.*;
//import org.usfirst.frc.team2791.

/**
 * Created by team2791 on 3/15/2016.
 */
public class BasicCloseAuton extends AutonMode {
    private double firstDistance;
    private double turnToAngle;
    private double secondDistance;

    public BasicCloseAuton(double firstTravelDistance, double turnAngle, double secondTravelDistance) {
        this.firstDistance = firstTravelDistance;
        this.turnToAngle = turnAngle;
        this.secondDistance = secondTravelDistance;
    }

    public void run() {
        switch (state) {
            case 0:
                driveTrain.disable();
                shooter.stopMotors();
                break;
            case 1:
                driveTrain.resetEncoders();
                intake.extendIntake();
            case 2:
                if (intake.getIntakeState().equals(IntakeState.EXTENDED))
                    state++;
                break;
            case 3:
                if (driveTrain.driveInFeet(firstDistance, 0, 0.6)) {
                    //if reached the distance then reset the encoders
                    driveTrain.resetEncoders();
                    //then continue to the next case
                    state++;
                }
                break;
            case 4:
                //raise arm before turning to allow time for the arm to rise
                shooter.setShooterHigh();
                //continue to the next case
                state++;
                break;
            case 5:
                if (driveTrain.setAngle(turnToAngle, 0.6)) {
                    //if reached the angle target then reset encoders
                    driveTrain.resetEncoders();
                    //continue to the next case
                    state++;
                }
                break;
            case 6:
                if (driveTrain.driveInFeet(secondDistance, 0, 0.6)) {
                    //after reaching the final distance fire
                    shooter.autoFire();
                }
                break;
            case 7:
                if (!shooter.getIfAutoFire()) {
                    //if the shooter is done firing reset
                    System.out.println("I am done with the basic close auton");
                    state = 0;
                }
                break;
        }
    }
}