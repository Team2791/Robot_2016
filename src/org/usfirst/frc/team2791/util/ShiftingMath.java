package org.usfirst.frc.team2791.util;

public class ShiftingMath {

	private static final float DRIVETRAIN_MOTOR_FREE_SPEED = 5310;
	private static final float LOW_GEAR_REDUCTION = 25;
	private static final float HIGH_GEAR_REDUCTION = (float) 12.15;
	// wheel diamter in feet
	private static final float WHEEL_DIAMETER = (float) (8.0 / 12.0);
	private static final float WHEEL_CIRCUMFERENCE = (float) Math.PI * WHEEL_DIAMETER;
	
	// this calculation is wrong. the bottom numbers should be squared
	private static final float WHEEL_RPM = DRIVETRAIN_MOTOR_FREE_SPEED * (LOW_GEAR_REDUCTION - HIGH_GEAR_REDUCTION)
			/ (LOW_GEAR_REDUCTION * 2 - HIGH_GEAR_REDUCTION * 2);
	// private static final float SHIFT_POINT = (float) (WHEEL_RPM / 60.0 *
	// WHEEL_CIRCUMFERENCE)
	private static final float SHIFT_POINT = (float) 4.94;
	private static final double SHIFT_THRESH_PERCENTAGE = 0.08;
	
	/**
	 *  This method uses constants to find the optimal shift point
	 * @return The optimal shift point in ft/s 
	 */
	public static double getOptimalShiftPoint() {
		return SHIFT_POINT;
	}
	
	/**
	 * 
	 * @return The robot speed when we should shift from low gear to high gear
	 */
	public static double getHighToLowShiftPoint() {
		return SHIFT_POINT * (1 + SHIFT_THRESH_PERCENTAGE);
	}
	
	/**
	 * 
	 * @return The robot speed when we should shift from high gear to low gear
	 */
	public static double getLowToHighShiftPoint() {
		return SHIFT_POINT * (1 - SHIFT_THRESH_PERCENTAGE);
	}
	
	
}
