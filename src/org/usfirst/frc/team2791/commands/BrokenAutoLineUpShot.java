package org.usfirst.frc.team2791.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static org.usfirst.frc.team2791.robot.Robot.*;

import org.usfirst.frc.team2791.util.ShakerCamera.ParticleReport;

public class BrokenAutoLineUpShot {
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
			if (driveTrain.setAngle(target, angleMaxOutput) && shooter.shooterAtSpeed()) {
				// for debugging
				shooter.autoFire();
				System.out.println("I'm trying to get to " + target + " I got to " + driveTrain.getAngle()
						+ "\n    angle-target= " + (driveTrain.getAngle() - target));
				autoLineUpCounter = 30;
			}
			//
			break;

		case 30:
			// keep the same angle until we are done shooting
			if (driveTrain.setAngle(target, angleMaxOutput)&&shooter.shooterAtSpeed()) {
				if (!shooter.getIfAutoFire()) {
					// only run once the shot is finished
					System.out.println("done shooting");
					// if done running go to the next step
					autoLineUpCounter = 40;
				}
			}
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