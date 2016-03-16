package org.usfirst.frc.team2791.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import static org.usfirst.frc.team2791.robot.Robot.*;

import org.usfirst.frc.team2791.util.ShakerCamera.ParticleReport;

public class AutoLineUpShot {
	// to correct any curving of the shot leftward or right ward
	public static double shootOffset = 0.0;
	// this is the counter that decides what stop to run in the auto lineup
	// process
	private static int autoLineUpCounter = 0;
	// target angle during the entire process
	private static double target = 0;
	private static double angleMaxOutput = 0.5;
	// this variable is used to notify other classes and prevent them from
	// taking action
	private static boolean addShooterPower = false;
	private static boolean autoLineUpInProgress = false;
	// this is to stop the sending auto fire multiple times
	private static boolean autoFireOnce = false;
	// just to count how many frames we used to lineup
	private static int frames_used = 0;
	private static long frameID;
	private static int timeForErrorCheck;
	private static ParticleReport currentTarget;
	
	private static boolean useMultipleFrames = false;
	private static boolean shootAfterAligned = false;
	
	public static void setUseMultipleFrames(boolean value) {
    	useMultipleFrames = value;
    }
    
    public static void setShootAfterAligned(boolean value) {
    	shootAfterAligned = value;
    }
    
    public static boolean setUseMultipleFrames() {
    	return useMultipleFrames;
    }
    
    public static boolean setShootAfterAligned() {
    	return shootAfterAligned;
    }
    

	public static void run() {
		// Put dashboard values
		driveTrain.usePID();
		SmartDashboard.putNumber("Auto Line Up step: ", autoLineUpCounter);
		currentTarget = camera.getTarget();
		// SmartDashboard.putBoolean("Has Target", currentTarget != null);
		switch (autoLineUpCounter) {
		default:
		case 0:
			// only run if there is a target available
			if (currentTarget != null) {
				driveTrain.resetEncoders();
				// prep the shot, runs the shooter wheels to setpoint
				// saves time in firing
				
				// the useMultipleFrames is there because we always fire if we're using multiple frames
				if(shootAfterAligned || useMultipleFrames) 
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
				

				// go to next step after this
				if(useMultipleFrames) {
					if(shootAfterAligned) {
						autoLineUpCounter = 40; // this is the double check and shoot case
					} else {
						System.out.println("We have no code to line up with multiple frame and not shoot. Shooting anyway.");
						autoLineUpCounter = 40;
					}	
				} else {
					if(shootAfterAligned) {
						autoLineUpCounter = 10;	// use 1 frame and shoot
					} else {
						autoLineUpCounter = 15; // use 1 frame and don't shoot
					}
				}	

			}
			break;
			
		case 10: // this is the single line up and shoot case
			// set the drive train to the target angle, will return true when
			// reached there
			if (driveTrain.setAngle(target, angleMaxOutput, true) && shooter.shooterAtSpeed()) {
				// for debugging
				shooter.autoFire();
				System.out.println("I'm trying to get to " + target + " I got to " + driveTrain.getAngle()
						+ "\n    angle-target= " + (driveTrain.getAngle() - target));
				autoLineUpCounter = 30;
			}
			//
			break;

		case 15: // this is the single line up and don't shoot case
			if (driveTrain.setAngle(target, angleMaxOutput, true)) {
				// for debugging
				System.out.println("I'm trying to get to " + target + " I got to " + driveTrain.getAngle()
						+ "\n    angle-target= " + (driveTrain.getAngle() - target));
				autoLineUpCounter = 40;
			}
			
			break;
			
		case 20:
            // here we check if our current angele is good enough
            // if now we reset out target using the latest camera image
            // and try to drive to it
            if (driveTrain.setAngle(target, angleMaxOutput, true)) { // keep the drivetrain
                // engaged
                // double check that we are close to the target angle
                if (!(currentTarget == null)) {
                    if (!(frameID == camera.getCurrentFrameID())) {
                    	// if we got a new frame process it
                    	currentTarget = camera.getTarget();
                    	
                        System.out.println("Final* checking with frameID " + frameID);
                        frameID = camera.getCurrentFrameID();
                        frames_used++;
                        
                        double camera_error = currentTarget.ThetaDifference + shootOffset;
                        System.out.println("Double check camera error: " + camera_error);
                        // if error is minimal shoot
                        if (Math.abs(camera_error) < 0.75 && shooter.shooterAtSpeed()) {
                            // go to the next step
                            // shoot whenever ready
                            System.out.println("I've found a good angle and am going to hold it while the shooter spins up.");
                            shooter.autoFire();
                            // we should be firing when the auto fire method is called
                            // the state we jump to will hold our angle until the auto fire method
                            // is completed
                            autoLineUpCounter = 30;
                        } else {
                            if (!shooter.shooterAtSpeed())
                                System.out.println("I am waiting on the shooter wheels");

                            if (!(Math.abs(camera_error) < 0.75))
                                System.out.println("I am waiting on camera error");
                            
                            // too much error so we're going to drive again
                            // update the target and the setAngle in the if statement in the top of
                            // this method will move us
                            target = driveTrain.getAngle() + currentTarget.ThetaDifference + shootOffset;
                        }
                    } else {
                        System.out.println("My check frame is the same as my turn frame. so I'm waiting.");
                    }
                } else {
                    System.out.println("We lost the image and are quitting");
                    // turn off the shooter
                    shooter.resetShooterAutoStuff();
                    autoLineUpCounter = 40;
                }
            } // endif driveTrain.setAngle
            break;
			
		case 30:
			// keep the same angle until we are done shooting
			if (driveTrain.setAngle(target, angleMaxOutput) && shooter.shooterAtSpeed()) {
				if (!shooter.getIfAutoFire()) {
					// only run once the shot is finished
					System.out.println("done shooting");
					// if done running go to the next step
					autoLineUpCounter = 40;
				}
			}
			break;
			
		case 40:
			// reset everything
			System.out.println("Finished auto line up and resetting.");
			// the fames_used + 1 is to include the check frame
			System.out.println("I took " + (frames_used + 1) + " frames to shoot");
			reset();
			break;
		}

	}

	public static void overrideAutoLineUp() {
		autoLineUpCounter = 30;
	}

	public static void reset() {
		autoLineUpInProgress = false;
		autoLineUpCounter = 0;
		autoFireOnce = false;
		shooter.resetShooterAutoStuff();
		driveTrain.doneUsingPID();
	}

	public static void addSomeShooterPower() {
		addShooterPower = true;
	}

	public static boolean isRunning() {
		return autoLineUpInProgress;
	}
}