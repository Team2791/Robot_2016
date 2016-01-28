package org.usfirst.frc.team2791.configuration;

public class Camera {
    //////////////////
    //// Vision - global targeting values
    //////////////////
    public static final String cameraPort = "cam0";// check on robotRio(web
    // interface)
    public static final int HUE_LOW = 60;
    public static final int HUE_HIGH = 100;
    public static final int SATURATION_LOW = 90;
    public static final int SATURATION_HIGH = 255;
    public static final int VALUE_LOW = 20;
    public static final int VALUE_HIGH = 255;
    public static final int CAMERA_FOV_ANGLE = 47;
    // Minimum area of particles to be considered
    public static final int minArea = 150;
    public static final int maxArea = 65535;
    // Maximum number of particles to process
    public static final int maxParticles = 8;

    // Color settings for particle filtering
    public static final int redLow = 0;
    public static final int redHigh = 100;
    public static final int greenLow = 110;
    public static final int greenHigh = 255;
    public static final int blueLow = 235;
    public static final int blueHigh = 255;
    // Minimum width the height ratio of valid targets;
    public static final int minWidHighRat = 1;//rough estimate of min must be tested
    public static final int minWidth = 80;// min width of targets
    public static final int calibrated = 0;
    public static final int exclude = 0;
    public static final int centeringDeadzone = 10;//number of pixels wide that the center should be within
}
