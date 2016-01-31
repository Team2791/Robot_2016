package org.usfirst.frc.team2791.util;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.vision.AxisCamera;

public class ShakerCamera extends AxisCamera {
    private final double cameraAngleWidth = 47 / 2;
    private int cameraPixelHeight = 240;
    private double halfCamHtFt;
    private int halfCamHtPx;
    private double targetHeightFt;
    private double targetHeightPx;
    private double distance;
    private NetworkTable netTable;

    public ShakerCamera() {
        super("cam0");
        halfCamHtPx = 120;
        targetHeightFt = 1.16667;


    }

    public void update() {// send next camera image
        netTable = NetworkTable.getTable("GRIP/myContoursReport");
        Double[] height = netTable.getNumberArray("height", new Double[0]);
        halfCamHtFt = (height[0] * halfCamHtFt) / halfCamHtPx;
        distance = halfCamHtFt / (Math.tan(Math.toDegrees(cameraAngleWidth)));
    }

    public double getDistance() {
        return distance;
    }


}
