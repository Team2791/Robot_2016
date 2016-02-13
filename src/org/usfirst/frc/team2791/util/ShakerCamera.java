package org.usfirst.frc.team2791.util;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

//public class ShakerCamera extends AxisCamera {
public class ShakerCamera {
	private final double cameraAngleWidth = 47 / 2;
	private int cameraPixelHeight = 240;
	private double halfCamHtFt;
	private int halfCamHtPx;
	private double targetHeightFt;
	private double targetHeightPx;
	private double distance;
	private NetworkTable netTable;
	private CameraServer camServer;

	public ShakerCamera() {
		// super("cam0");
		camServer = CameraServer.getInstance();
		camServer.setQuality(50);
		camServer.startAutomaticCapture("cam0");
		halfCamHtPx = 120;
		targetHeightFt = 1.16667;
	}

	public void update() {

	}

	public double getDistance() {// send next camera image
		netTable = NetworkTable.getTable("GRIP/myContoursReport");
		Double[] height = netTable.getNumberArray("height", new Double[0]);
		halfCamHtFt = (height[0] * halfCamHtFt) / halfCamHtPx;
		return distance = halfCamHtFt / (Math.tan(Math.toDegrees(cameraAngleWidth)));
	}

}
