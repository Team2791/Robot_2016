package org.usfirst.frc.team2791.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.util.ShakerCamera.ParticleReport;

import static org.usfirst.frc.team2791.robot.Robot.*;

public class AutoLineUpShot {
    private static final double decreasePercentage = 0.8;
    private static final double angleMaxAtLowError = 0.4;
    private static final double defaultMaxOut = 1;
    // to correct any curving of the shot leftward or right ward
    public static double shootOffset = 0.0;
    // this is the counter that decides what stop to run in the auto lineup
    // process
    private static int autoLineUpCounter = 0;
    // target angle during the entire process
    private static double target = 0;
    private static double angleMaxOutput = defaultMaxOut;
    // this variable is used to notify other classes and prevent them from
    // taking action
    private static boolean autoLineUpInProgress = false;
    // just to count how many frames we used to lineup
    private static int frames_used = 0;
    private static long frameID;
    private static double timeForErrorCheck;
    private static ParticleReport currentTarget;
    private static boolean morePowerInShot = false;
    private static boolean shoot = true;

    public static void run() {
        // Put dashboard values
        SmartDashboard.putNumber("Auto Line Up step: ", autoLineUpCounter);
        currentTarget = camera.getTarget();
        // SmartDashboard.putBoolean("Has Target", currentTarget != null);
        switch (autoLineUpCounter) {
            default:
            case 0:
                // only run if there is a target available
                if (currentTarget != null) {
                    driveTrain.resetEncoders();
                    // go to next step after this
                    autoLineUpCounter = 10;
                    // prep the shot, runs the shooter wheels to setpoint
                    // saves time in firing
                    shooter.prepShot();
                    // the target angle == current angle + targetAngleDiff + offset
                    target = driveTrain.getAngle() + currentTarget.ThetaDifference + shootOffset;
                    frameID = camera.getCurrentFrameID();
                    // Print out the values for debugging
                    System.out.println("my target is " + target + " current angle is " + driveTrain.getAngle()
                            + "the shooter offset is " + shootOffset);
                    // tell the other subsystems that we are currently autofiring
                    autoLineUpInProgress = true;
                    // we used one frame so far
                    frames_used = 1;

                }
                break;
            case 10:
                // set the drive train to the target angle, will return true when
                // reached there
                if (driveTrain.setAngle(target, angleMaxOutput)) {
                    // for debugging
                    System.out.println("I'm trying to get to " + target + " I got to " + driveTrain.getAngle()
                            + "\n    angle-target= " + (driveTrain.getAngle() - target));
                    // if the target angle is reached then continue to the
                    // next
                    // step
                    autoLineUpCounter = 11;
                    // get the current frame ID so we make sure the one we
                    // use
                    // for double checking is different
                    frameID = camera.getCurrentFrameID();
                    if (angleMaxOutput < angleMaxAtLowError)
                        angleMaxOutput = angleMaxAtLowError;
                    else
                        angleMaxOutput *= decreasePercentage;
                    System.out.println("made turn with frameID " + frameID);
                    timeForErrorCheck = Timer.getFPGATimestamp();
                }
                break;
            case 11:
                if (driveTrain.setAngle(target, angleMaxOutput))
                    if (Timer.getFPGATimestamp() - timeForErrorCheck < 0.3)
                        autoLineUpCounter = 20;
                break;
            case 20:
                // here we check if our current angele is good enough
                // if now we reset out target using the latest camera image
                // and try to drive to it
                if (driveTrain.setAngle(target, angleMaxOutput)) { // keep the drivetrain
                    // engaged
                    // double check that we are close to the target angle
                    if (!(currentTarget == null)) {
                        if (!(frameID == camera.getCurrentFrameID())) {
                            // if(true){
                            frameID = camera.getCurrentFrameID();
                            System.out.println("Final* checking with frameID " + frameID);
                            frames_used++;
                            double camera_error = currentTarget.ThetaDifference + shootOffset;
                            System.out.println("Double check camera error: " + camera_error);
                            // if error is minimal shoot
                            if (Math.abs(camera_error) < 0.75 && shooter.shooterAtSpeed()) {
                                // go to the next step
                                // shoot whenever ready
                                System.out
                                        .println("I've found a good angle and am going to hold it while the shooter spins up.");

                                if (morePowerInShot)
                                    shooter.autoFireWithExtraJuice();
                                else
                                    shooter.autoFire();
                                autoLineUpCounter = 30;
                            } else {
                                if (!shooter.shooterAtSpeed())
                                    System.out.println("I am waiting on the shooter wheels");

                                if (!(Math.abs(camera_error) < 0.75))
                                    System.out.println("I am waiting on camera error");
                                // too much error so we're going to drive again
                                target = driveTrain.getAngle() + currentTarget.ThetaDifference + shootOffset;
                                // we need another frame to check retry
                                frames_used++;
                                // go back a step
                                autoLineUpCounter = 10;
                            }

                        } else {
                            System.out.println("My check frame is the same as my turn frame. so I'm waiting.");
                            try {// wait a bit for a new frame
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        System.out.println("We lost the image and are quitting");
                        // turn off the shooter
                        shooter.resetShooterAutoStuff();
                        autoLineUpCounter = 40;
                    }
                }
                break;
            case 30:
                if (shoot) {
                    // keep the same angle until we are done shooting
                    if (driveTrain.setAngle(target, angleMaxOutput)) {
                        if (!shooter.getIfAutoFire()) {
                            // only run once the shot is finished
                            System.out.println("done shooting");
                            // if done running go to the next step
                            autoLineUpCounter = 40;
                        }
                    }
                } else
                    autoLineUpCounter = 40;
                break;

            // if (currentTarget != null) {
            // // keep the drive train engaged
            //
            // // to prevent autofiring multiple times
            // if (!autoFireOnce) {
            //
            // autoFireOnce = true;
            // } else
            // }
            // } else {
            // // if we lost the target during the process then continue and
            // // dont fire
            // System.out.println("I lost the target and am quitting.");
            // autoLineUpCounter++;
            // }
            // break;

            case 40:
                // reset everything
                System.out.println("Finished auto line up and resetting.");
                System.out.println("I took " + frames_used + " frames to shoot");
                reset();
                break;
        }

    }

    public static void overrideAutoLineUp() {
        autoLineUpCounter = 30;
    }

    public static void reset() {
        angleMaxOutput = defaultMaxOut;
        autoLineUpInProgress = false;
        autoLineUpCounter = 0;
        morePowerInShot = false;
        shoot = true;
    }

    public static void shooterWithExtraJucice() {
        morePowerInShot = true;
    }

    public static void onlyLineup() {
        shoot = false;
    }

    public static boolean getIfShooting() {
        return shoot;
    }

    public static boolean isRunning() {
        return autoLineUpInProgress;
    }
}