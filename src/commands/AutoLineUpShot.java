package commands;

import static org.usfirst.frc.team2791.robot.Robot.camera;
import static org.usfirst.frc.team2791.robot.Robot.driveTrain;
import static org.usfirst.frc.team2791.robot.Robot.operatorJoystick;
import static org.usfirst.frc.team2791.robot.Robot.shooter;

import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerShooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoLineUpShot {

	private static int autoLineUpCounter = 0;
	private static double angleWhenAutoLineUp;
	private static double target = 0;
	private static boolean autoLineUpInProgress = false;
	private static boolean autoFireOnce = false;
	public static double shootOffset = 2.5;
	private static int frames_used = 0;

	public static void run() {
		SmartDashboard.putNumber("Auto Line Up step: ", autoLineUpCounter);
		SmartDashboard.putBoolean("Has Target", camera.getTarget()!=null);
		switch (autoLineUpCounter) {// auto lineup and fire
		default:
		case 0:
			// camera.displayTargettingImageToDash(true);
			if (camera.getTarget() != null) {
				autoLineUpCounter++;
				angleWhenAutoLineUp = driveTrain.getAngle();// set the last
															// known
				target = angleWhenAutoLineUp + camera.getTarget().ThetaDifference + shootOffset;
				shootOffset = (int) SmartDashboard.getNumber("shooter offset");
				System.out.println("my target is " + target + " current angle is " + driveTrain.getAngle()
						+ "the shooter offset is " + shootOffset);
				autoLineUpInProgress = true;
				frames_used = 1;
			}

			// angle when
			break;
		case 1:// use the angle of the target and the angle of driveTrain just
				// before this
				// to find and lineup with the target
				// System.out.println("Current angle is " +
				// driveTrain.getAngle());

			if (driveTrain.setAngle(target, 0.4)) {
				System.out.println("I got somewhere!");
				System.out.println("I'm trying to get to " + target + " I got to " + driveTrain.getAngle()
						+ "\n angle-target= " + (driveTrain.getAngle() - target));
				autoLineUpCounter = 2;
			}

			// camera.displayTargetdisplayTargettingImageToDashtingImageToDash(true);
			break;

		case 2:
			// here we check if our current angele is good enough
			// if now we reset out target using the latest camera image
			// and try to drive to it
			
			driveTrain.setAngle(target, 0.4); // keep the drivetrain engaged

			double camera_error = camera.getTarget().ThetaDifference + shootOffset;
			System.out.println("Double check camera error: " + camera_error);
			// if there is little error shoot
			if (Math.abs(camera_error) < 1.0) {
				System.out.println("spinning up now!");
				autoLineUpCounter++;
			} else {
				// too much error so we're goign to drive again
				target = driveTrain.getAngle() + camera.getTarget().ThetaDifference + shootOffset;
				frames_used++;
				autoLineUpCounter = 1;

			}
			// camera.displayTargettingImageToDash(true);
			break;

		case 3:
			if (camera.getTarget() != null) {
				
				driveTrain.setAngle(target, 0.4);
				if (!autoFireOnce) {
					shooter.autoFire();
					autoFireOnce = true;
				} else if (!shooter.getIfAutoFire()) {
					System.out.println("done shooting");
					autoLineUpCounter++;
				}
			}
			else {
				System.out.println("I lost the target and am quitting.");
				autoLineUpCounter++;
			}

			break;
		case 4:
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
