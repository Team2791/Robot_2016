package org.usfirst.frc.team2791.util;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;

public class ShakerAccelerometer extends BuiltInAccelerometer {
	public ShakerAccelerometer() {

	}

	public double getAngleOffGround() {
		return Math.asin(Math.toRadians(getZ()-1));

	}
}
