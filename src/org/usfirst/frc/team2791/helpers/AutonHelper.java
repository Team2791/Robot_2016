package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.helpers.autonModes.AutonMode;
import org.usfirst.frc.team2791.helpers.autonModes.DriveStraightAutomaticLineup;
import org.usfirst.frc.team2791.util.Constants;

import static org.usfirst.frc.team2791.robot.Robot.*;
/**
 * Created by Akhil on 1/28/2016.
 */
public class AutonHelper extends ShakerHelper {
	private static AutonHelper auton;
	private int counter = 0;
	private double setPoint = 0;

	private AutonMode overallAuto;
	private AutonHelper() {
		SmartDashboard.putNumber("Stat Angle P", Constants.STATIONARY_ANGLE_P);
		SmartDashboard.putNumber("Stat Angle I", Constants.STATIONARY_ANGLE_I);
		SmartDashboard.putNumber("Stat Angle D", Constants.STATIONARY_ANGLE_D);
		SmartDashboard.putNumber("Angle P", Constants.DRIVE_ANGLE_P);
		SmartDashboard.putNumber("Angle I", Constants.DRIVE_ANGLE_I);
		SmartDashboard.putNumber("Angle D", Constants.DRIVE_ANGLE_D);
		SmartDashboard.putNumber("DISTANCE P", Constants.DRIVE_DISTANCE_P);
		SmartDashboard.putNumber("DISTANCE I", Constants.DRIVE_DISTANCE_I);
		SmartDashboard.putNumber("Distance D", Constants.DRIVE_DISTANCE_D);
		SmartDashboard.putNumber("Angle setpoint", setPoint);
		SmartDashboard.putBoolean("Use Gyro", false);
		SmartDashboard.putNumber("max speed", 0.5);
		SmartDashboard.putNumber("pid distance travel", 1.0);
		SmartDashboard.putNumber("Auton step counter", counter);
		// This is a low bar auton
		// overallAuto = new BasicCloseAuton(20.6, 60, 7.9);

		// Drive straight auton
		// overallAuto = new DriveStraightAuton(15);

		// we used this on Defense 2
		
		SmartDashboard.putNumber("Auton Distance", 13.05);
		SmartDashboard.putNumber("Auton Angle",30);
		// we used this on Defense 3
		// overallAuto = new DriveStraightAutomaticLineup(13.75,5);

		// we used this on Defense 4
		// overallAuto = new DriveStraightAutomaticLineup(13.75,-5);

		// we used this on Defense 5
		// overallAuto = new DriveStraightAutomaticLineup(13.75,-15);

		// this is for testing purposes only..
		// overallAuto = new DriveStraightAutomaticLineup(.5, 5);
	}

	public static AutonHelper getInstance() {
		if (auton == null)
			auton = new AutonHelper();
		return auton;
	}

	private static void retuneStationaryAnglePID() {
		driveTrain.setAngle(SmartDashboard.getNumber("Angle setpoint"), SmartDashboard.getNumber("max speed"));
	}

	private static void retuneDistancePID() {
		driveTrain.setDistance(SmartDashboard.getNumber("pid distance travel"),
				SmartDashboard.getNumber("Angle setpoint"), SmartDashboard.getNumber("max speed"),
				SmartDashboard.getBoolean("Use Gyro"));
	}

	public void run() {
//		 retuneDistancePID();
//		 retuneStationaryAnglePID();
		 driveTrain.updateSmartDash();
		switch (counter) {
		case 0:
			//start the auton.. basically run the init method
			camera.setCameraValues(1, 1);
			overallAuto = new DriveStraightAutomaticLineup(SmartDashboard.getNumber("Auton Distance"), SmartDashboard.getNumber("Auton Angle"));
			overallAuto.start();
			counter++;
		case 1:
			//run the auton...
			overallAuto.run();
			if (overallAuto.getCompleted()) {
				//if completed go to the next case...
				counter++;
			}
			break;
		case 2:
			//we are done with auton...so do nothing
			break;
		}
	}

	public void disableRun() {
		driveTrain.disable();
		counter = 0;
	}

	public void updateSmartDash() {
		driveTrain.updateSmartDash();
		intake.updateSmartDash();
	}

	public void reset() {

	}

	public void debug() {

	}

	public void resetAutonStepCounter() {
		counter = 0;
	}

}