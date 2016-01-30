package org.usfirst.frc.team2791.util;

import edu.wpi.first.wpilibj.vision.AxisCamera;

//values that are still needed are
//conversion factor = (targetWidInFt(known)*targetwidinPix(known))/targetWidInPix(for current frame)
//target width is 1.667ft
//center of mass from network tables.
public class ShakerCamera extends AxisCamera {
    private final double cameraAngleWidth = 47 / 2;
    private double centerOfMassX;
    private int cameraPixelWidth = 480;
    private double camToTarget;
    private double targetToCenter;
    private double targetToEdge;
    private double hypotenuse;
    private double targetHeight;
    private double cameraHeight;
    private double conversionFactor;//this has to be found out looking at

    public ShakerCamera() {
        super("cam0");
        centerOfMassX = 0;//get this from network tables
        doNecessaryCalculations();


    }

    private void doNecessaryCalculations() {
        hypotenuse = (inFeet(cameraPixelWidth) / 2) / (Math.toDegrees(Math.sin(Math.toRadians(cameraAngleWidth / 2))));
        camToTarget = Math.sqrt(Math.pow(inFeet(hypotenuse), 2.0) - (Math.pow(inFeet(cameraPixelWidth), 2.0) / 4));
        targetToCenter = inFeet(cameraPixelWidth / 2) - centerOfMassX;

    }

    public void update() {//send next camera image

    }

    public double inFeet(double value) {
        return value * conversionFactor;
    }

    public void getRobotToTarget() {
        Math.sqrt(camToTarget * camToTarget + Math.pow(targetHeight + cameraHeight, 2.0));
    }

}
