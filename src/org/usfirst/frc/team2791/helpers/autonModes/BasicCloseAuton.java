package org.usfirst.frc.team2791.helpers.autonModes;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerIntake.IntakeState;

import static org.usfirst.frc.team2791.robot.Robot.*;

//import org.usfirst.frc.team2791.

/**
 * Created by team2791 on 3/15/2016.
 */
public class BasicCloseAuton extends AutonMode {
    private double firstDistance;
    private double turnToAngle;
    private double secondDistance;

    private Timer timer = new Timer();

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
                timer.reset();
                break;
            case 1:
                System.out.println("Starting basic close auto.");
                driveTrain.resetEncoders();
                intake.extendIntake();
                timer.reset();
                timer.start();
            case 2:
                if (timer.get() > 1.5 && intake.getIntakeState().equals(IntakeState.EXTENDED)) {
                    System.out.println("Intake down, starting my first drive.");
                    //go to next state
                    state++;
                }
                break;
            case 3:
                if (driveTrain.setDistance(firstDistance, 0, 0.4, true)) {
                    System.out.println("Finished driving, now time to raise the shooter.");
                    // if reached the distance then reset the encoders 
                    driveTrain.resetEncoders();

                    // raise the arm and continue to the next case
                    shooter.setShooterHigh();
                    timer.reset();
                    state++;
                }
                break;
            case 4:
                // allow 1s for the arm to rise
                if (timer.get() > 1)
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
                if (driveTrain.setDistance(secondDistance, 0, 0.6, false)) {
                    //after reaching the final distance fire
                    shooter.autoFire(635);
                    state++;
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