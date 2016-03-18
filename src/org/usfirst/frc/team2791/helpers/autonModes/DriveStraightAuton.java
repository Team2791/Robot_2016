package org.usfirst.frc.team2791.helpers.autonModes;

import edu.wpi.first.wpilibj.Timer;

import static org.usfirst.frc.team2791.robot.Robot.driveTrain;
import static org.usfirst.frc.team2791.robot.Robot.shooter;

/**
 * Created by team2791 on 3/17/2016.
 */
public class DriveStraightAuton extends AutonMode {
    public double firstDistance;
    private Timer timer = new Timer();

    public DriveStraightAuton(double distance) {
        firstDistance = distance;
    }

    public void run() {
        switch (state) {
            case 0:
                driveTrain.disable();
                shooter.stopMotors();
                timer.reset();
                break;
            case 1:
                System.out.println("Starting drive straight auto");
                driveTrain.resetEncoders();
                state++;
                break;
            case 2:
                if (driveTrain.setDistance(firstDistance, 0, 0.6, false)) {
                    System.out.println("Finished driving");
                    // if reached the distance then reset the encoders
                    driveTrain.resetEncoders();

                    // raise the arm and continue to the next case
//                    shooter.setShooterHigh();
                    timer.reset();
                    state++;
                }
                break;
            case 3:
                state = 0;
                System.out.print("I am done with the auton");

        }
    }
}
