package org.usfirst.frc.team2791.util;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.Range;
import com.ni.vision.NIVision.StructuringElement;
import com.ni.vision.VisionException;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;
import org.usfirst.frc.team2791.commands.AutoLineUpShot;

import java.util.ArrayList;
import java.util.TreeMap;

public class ShakerCamera implements Runnable {

    private final double CAMERA_WIDTH_DEGREES = 53;
    private final double TARGET_HT_INCHES = 88;
    private final double CAMERA_HT_INCHES = 0;
    private final double CAMERA_PITCH_DEG = 45;
    ArrayList<ParticleReport> particles;
    private int CAMERA_WIDTH_PIXELS = 720;
    private int CAMERA_HEIGHT_PIXELS = 480;
    private NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
    private NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0, 0, 1, 1);
    private Image frame;
    private Image binaryFrame;
    private Image particleBinaryFrame;
    private USBCamera cam;
    private Servo cameraServo;
    private int cameraExposure = 1;
    private int cameraBrightness = 1;
    private boolean cameraAutoSettings = true;
    private boolean cameraValsOnlyOnce = false;
    private TreeMap<Double, Double> rangeTable;
    private boolean alreadyMeasuredImage = false;
    private double rangeOffset = 0.0;

    public ShakerCamera(String camPort) {
        cam = new USBCamera(camPort);
        cam.startCapture();
        cameraServo = new Servo(Constants.CAMERA_SERVO_PORT);
        frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
        binaryFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
        Image filteredImage = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
        particleBinaryFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
        cam.setExposureManual(1);
        cam.setBrightness(0);
        cam.setSize(CAMERA_WIDTH_PIXELS, CAMERA_HEIGHT_PIXELS);
        cam.updateSettings();
        StructuringElement box = new StructuringElement(6, 4, 1);
        double AREA_MINIMUM = 7;
        criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA, AREA_MINIMUM,
                100.0, 0, 0);
        SmartDashboard.putBoolean("display targetting", false);
        SmartDashboard.getBoolean("Debug Image", false);
        SmartDashboard.putNumber("H min", 100);
        SmartDashboard.putNumber("H max", 140);
        SmartDashboard.putNumber("S min", 100);
        SmartDashboard.putNumber("S max", 255);
        SmartDashboard.putNumber("L min", 60);
        SmartDashboard.putNumber("L max", 184);
        rangeTable = new TreeMap<Double, Double>();
        // rangeTable.put(, );//distance , rpm
    }

    public void run() {
        while (true) {
            try {
                // calculate the time it takes to get the image
                double imageGetTime = Timer.getFPGATimestamp();
                cam.getImage(frame);
                SmartDashboard.putNumber("Image get Time", imageGetTime = Timer.getFPGATimestamp() - imageGetTime);
                alreadyMeasuredImage = false;
                if (frame != null) {
                    // if should display the modified image to the
                    // smartdashboard
                    if (SmartDashboard.getBoolean("display targetting")) {
                        measureAndGetParticles();
                        CameraServer.getInstance().setImage(particleBinaryFrame);
                    } else if (SmartDashboard.getBoolean("Debug Image") || AutoLineUpShot.isRunning()) {
                        double processingTime = Timer.getFPGATimestamp();
                        measureAndGetParticles();
                        processingTime = Timer.getFPGATimestamp() - processingTime;
                        NIVision.imaqDrawLineOnImage(binaryFrame, binaryFrame, NIVision.DrawMode.DRAW_VALUE,
                                new NIVision.Point(CAMERA_WIDTH_PIXELS / 2, 0),
                                new NIVision.Point(CAMERA_WIDTH_PIXELS / 2, CAMERA_HEIGHT_PIXELS), 125f);
                        NIVision.imaqOverlayText(binaryFrame, new NIVision.Point(0, 0),
                                "Processing FPS: " + 1000 / (processingTime + imageGetTime), NIVision.RGB_BLUE,
                                new NIVision.OverlayTextOptions(), "Default");
                        SmartDashboard.putNumber("FPS with processing", 1000 / (processingTime + imageGetTime));
                        CameraServer.getInstance().setImage(binaryFrame);
                    } else {
                        SmartDashboard.putNumber("FPS without processing", 1000 / (imageGetTime));
                        NIVision.imaqOverlayText(frame, new NIVision.Point(0, 0), "FPS: " + 1000 / imageGetTime,
                                NIVision.RGB_BLUE, new NIVision.OverlayTextOptions(), "Default");
                        CameraServer.getInstance().setImage(frame);

                    }
                }
                if (cameraAutoSettings && !cameraValsOnlyOnce) {
                    // set the exposure and the brightness for when vision
                    // targetting
                    cam.setExposureAuto();
                    cam.setBrightness(25);
                    cam.setSize(CAMERA_WIDTH_PIXELS, CAMERA_HEIGHT_PIXELS);
                    cam.updateSettings();
                    cameraValsOnlyOnce = true;
                } else if (!cameraValsOnlyOnce) {
                    // set the exposure and the brightness for when vision
                    // targetting
                    cam.setExposureManual(cameraExposure);
                    cam.setBrightness(cameraBrightness);
                    cam.setSize(CAMERA_WIDTH_PIXELS, CAMERA_HEIGHT_PIXELS);
                    cam.updateSettings();
                    cameraValsOnlyOnce = true;
                }
            } catch (VisionException npe) {
                System.out.println("Vision ERROR: " + npe.getMessage());
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public double getRPMForRange(double range) {
        // taken from daisycvwidget
        double lowestKey = -1;
        double lowestVal = -1;
        for (double key : rangeTable.keySet()) {
            if (range < key) {
                double highVal = rangeTable.get(key);
                if (lowestKey > 0) {
                    double slope = (range - lowestKey) / (key - lowestKey);
                    return lowestVal + slope * (highVal - lowestVal);
                } else
                    return highVal;
            }
            lowestKey = key;
            lowestVal = rangeTable.get(key);
        }
        return 850;
    }

    public double getRange() {
        // make sure to only call this if we know a target exists
        double range = (TARGET_HT_INCHES - CAMERA_HT_INCHES) / Math
                .tan(getNormalizedCenterOfMass(getTarget().CenterOfMassY) * (CAMERA_WIDTH_DEGREES / 2) + CAMERA_PITCH_DEG)
                * (Math.PI / 180);
        return range + rangeOffset;

    }

    public ParticleReport getTarget() {
        ArrayList<ParticleReport> reports = measureAndGetParticles();
        if (reports.size() == 0) {
            System.out.println("The camera reports are empty");
            return null;
        }

        int targetLoc = 0;
        if (reports.size() != 1) {
            double maxPercentArea = 0;
            int counter = 0;
            for (ParticleReport par : reports) {
                if (maxPercentArea < par.PercentAreaToImageArea) {
                    maxPercentArea = par.PercentAreaToImageArea;
                    targetLoc = counter;
                }
                counter++;
            }
        }
        ParticleReport par = reports.get(targetLoc);
        // creates a rectangle to cover the target
        NIVision.Rect r = new NIVision.Rect((int) par.BoundingRectTop, (int) par.BoundingRectLeft,
                Math.abs((int) (par.BoundingRectTop - par.BoundingRectBottom)),
                Math.abs((int) (par.BoundingRectLeft - par.BoundingRectRight)));
        // draws the rectangle on the binary image
        NIVision.imaqDrawShapeOnImage(binaryFrame, binaryFrame, r, NIVision.DrawMode.PAINT_VALUE,
                NIVision.ShapeMode.SHAPE_RECT, 10f);//highlight the choosen target in a different color
        return reports.get(targetLoc);
    }

    private ArrayList<ParticleReport> measureAndGetParticles() {
        if (!alreadyMeasuredImage) {
            // does a bunch of measurements on the image and its particles
            // particle information is stored into the arraylist particles
            particles = new ArrayList<ParticleReport>();
            // create and apply an hsl threshold on the current fame
            NIVision.imaqColorThreshold(binaryFrame, frame, 255, NIVision.ColorMode.HSL,
                    new Range((int) SmartDashboard.getNumber("H min"), (int) SmartDashboard.getNumber("H max")),
                    new Range((int) SmartDashboard.getNumber("S min"), (int) SmartDashboard.getNumber("S max")),
                    new Range((int) SmartDashboard.getNumber("L min"), (int) SmartDashboard.getNumber("L max")));
            // set the lower threshold on area in the criteria filter
            criteria[0].lower = 0.3f;
            // use particle filter to remove unwanted particles
            int imaqError = NIVision.imaqParticleFilter4(particleBinaryFrame, binaryFrame, criteria, filterOptions, null);
            // System.out.println("I just did a filter to remove noise");
            // count the number of viable particles
            int numParticles = NIVision.imaqCountParticles(particleBinaryFrame, 1);
            // System.out.println("I just counted the number of particles "+
            // numParticles );
            // checks to make sure there is at least one particle
            if (numParticles > 0) {
                // Measure each of the particles
                // first do a convex hull to fill in the pariticle
                NIVision.imaqConvexHull(binaryFrame, binaryFrame, 8);
                for (int particleIndex = 0; particleIndex < numParticles; particleIndex++) {
                    // iterates through the particles and measures them
                    ParticleReport par = new ParticleReport();
                    // adds the particle report to the arraylist
                    particles.add(par);
                    // area / total image area
                    par.PercentAreaToImageArea = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                            NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
                    // area of the particle
                    par.Area = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                            NIVision.MeasurementType.MT_AREA);
                    // Y value of the upper part of box
                    par.BoundingRectTop = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                            NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
                    // X value of the left part of box
                    par.BoundingRectLeft = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                            NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
                    // Y value of the bottom part of box
                    par.BoundingRectBottom = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                            NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
                    // X value of the right part of box
                    par.BoundingRectRight = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                            NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);

                    par.Height = Math.abs((int) (par.BoundingRectTop - par.BoundingRectBottom));
                    par.Width = Math.abs((int) (par.BoundingRectLeft - par.BoundingRectRight));
                    // measure the center of mass in the x dir
                    par.CenterOfMassX = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                            NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
                    par.CenterOfMassX = par.Width / 2;

                    // measure the center of mass in the y dir
                    par.CenterOfMassY = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                            NIVision.MeasurementType.MT_CENTER_OF_MASS_Y);
                    // calculate the angle from the middle
                    double angleFromMiddle = CAMERA_WIDTH_DEGREES * getNormalizedCenterOfMass(par.CenterOfMassX);
                    par.ThetaDifference = angleFromMiddle / 2;
                    par.Range = getRange();
                    // put the important values to the dashboard
                    SmartDashboard.putNumber("Theta diff", par.ThetaDifference);
                    SmartDashboard.putNumber("center of mass x", par.CenterOfMassX);
                    SmartDashboard.putNumber("Boudnding rect top", par.BoundingRectTop);
                    SmartDashboard.putNumber("Normalized center of mass x",
                            getNormalizedCenterOfMass(par.CenterOfMassX));
                    SmartDashboard.putNumber("Distance from target", par.Range);
                    // creates a rectangle to cover the target
                    NIVision.Rect r = new NIVision.Rect((int) par.BoundingRectTop, (int) par.BoundingRectLeft,
                            Math.abs((int) (par.BoundingRectTop - par.BoundingRectBottom)),
                            Math.abs((int) (par.BoundingRectLeft - par.BoundingRectRight)));
                    // draws the rectangle on the binary image
                    NIVision.imaqDrawShapeOnImage(binaryFrame, binaryFrame, r, NIVision.DrawMode.PAINT_VALUE,
                            NIVision.ShapeMode.SHAPE_RECT, 125f);
                }
            }
        }
        return particles;
    }

    public void cameraUp() {
        // bring servo arm up
        cameraServo.set(0.3902);
    }

    public void cameraDown() {
        // bring servo arm down
        cameraServo.set(1);
    }

    public void setCameraValues(int exposure, int brightness) {
        this.cameraBrightness = brightness;
        this.cameraExposure = exposure;
        this.cameraAutoSettings = false;
        cameraValsOnlyOnce = false;
    }

    public void setCameraValuesAutomatic() {
        this.cameraAutoSettings = true;
        this.CAMERA_WIDTH_PIXELS = 320;
        this.CAMERA_HEIGHT_PIXELS = 240;
        cameraValsOnlyOnce = false;
    }

    public double getNormalizedCenterOfMass(double currentCenterInPixels) {
        return ((2 * currentCenterInPixels) / CAMERA_WIDTH_PIXELS) - 1;
    }

    public static class ParticleReport {
        // a class just to hold values of the particles
        public double ThetaDifference;
        public double PercentAreaToImageArea;
        public double Area;
        public double BoundingRectLeft;
        public double BoundingRectTop;
        public double BoundingRectRight;
        public double BoundingRectBottom;
        public double CenterOfMassX;
        public double CenterOfMassY;
        public double Range;
        public double Height;
        public double Width;
    }
}
