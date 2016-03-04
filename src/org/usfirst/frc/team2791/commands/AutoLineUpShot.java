package org.usfirst.frc.team2791.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static org.usfirst.frc.team2791.robot.Robot.*;

public class AutoLineUpShot {
    //to correct any curving of the shot leftward or right ward
    public static double shootOffset = 2.5;
    //this is the counter that decides what stop to run in the auto lineup process
    private static int autoLineUpCounter = 0;
    //target angle during the entire process
    private static double target = 0;
    //this variable is used to notify other classes and prevent them from taking action
    private static boolean autoLineUpInProgress = false;
    //this is to stop the sending auto fire multiple times
    private static boolean autoFireOnce = false;
    //just to count how many frames we used to lineup
    private static int frames_used = 0;

    public static void run() {
        //Put dashboard values
        SmartDashboard.putNumber("Auto Line Up step: ", autoLineUpCounter);
        SmartDashboard.putBoolean("Has Target", camera.getTarget() != null);
        switch (autoLineUpCounter) {
            default:
            case 0:
                //only run if there is a target available
                if (camera.getTarget() != null) {
                    //go to next step after this
                    autoLineUpCounter++;
                    //prep the shot, runs the shooter wheels to setpoint
                    //saves time in firing
                    shooter.prepShot();
                    //the target angle == current angle + targetAngleDiff + offset
                    target = driveTrain.getAngle() + camera.getTarget().ThetaDifference + shootOffset;
                    //Print out the values for debugging
                    System.out.println("my target is " + target + " current angle is " + driveTrain.getAngle()
                            + "the shooter offset is " + shootOffset);
                    //tell the other subsystems that we are currently autofiring
                    autoLineUpInProgress = true;
                    //we used one frame so far
                    frames_used = 1;
                }
                break;
            case 1:
                //set the drive train to the target angle, will return true when reached there
                if (driveTrain.setAngle(target, 0.4)) {
                    //for debugging
                    System.out.println("I got somewhere!");
                    System.out.println("I'm trying to get to " + target + " I got to " + driveTrain.getAngle()
                            + "\n angle-target= " + (driveTrain.getAngle() - target));
                    //if the target angle is reached then continue to the next step
                    autoLineUpCounter = 2;
                }
                break;
            case 2:
                // here we check if our current angele is good enough
                // if now we reset out target using the latest camera image
                // and try to drive to it
                driveTrain.setAngle(target, 0.4); // keep the drivetrain engaged
                //double check that we are close to the target angle
                double camera_error = camera.getTarget().ThetaDifference + shootOffset;
                System.out.println("Double check camera error: " + camera_error);
                // if error is minimal shoot
                if (Math.abs(camera_error) < 1.0) {
                    //go to the next step
                    autoLineUpCounter++;
                } else {
                    // too much error so we're goign to drive again
                    target = driveTrain.getAngle() + camera.getTarget().ThetaDifference + shootOffset;
                    //we need another frame to check retry
                    frames_used++;
                    //go back a step
                    autoLineUpCounter = 1;

                }
                break;
            case 3:
                //only fire if we still have the target
                if (camera.getTarget() != null) {
                    //keep the drive train engaged
                    driveTrain.setAngle(target, 0.4);
                    //to prevent autofiring multiple times
                    if (!autoFireOnce) {
                        shooter.autoFire();
                        autoFireOnce = true;
                    } else if (!shooter.getIfAutoFire()) {
                        //only run once the shot is finished
                        System.out.println("done shooting");
                        //if done running go to the next step
                        autoLineUpCounter++;
                    }
                } else {
                    //if we lost the target during the process then continue and dont fire
                    System.out.println("I lost the target and am quitting.");
                    autoLineUpCounter++;
                }
                break;
            case 4:
                //reset everything
                System.out.println("Finished auto line up and resetting.");
                System.out.println("I took " + frames_used + " frames to shoot");
                reset();
                break;
        }
    }

    public static void overrideAutoLineUp() {
        autoLineUpCounter = 3;
    }

    public static void reset() {
        autoLineUpInProgress = false;
        autoLineUpCounter = 0;
        autoFireOnce = false;
    }

    public static boolean isRunning() {
        return autoLineUpInProgress;
    }
}
