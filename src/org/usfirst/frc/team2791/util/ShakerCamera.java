package org.usfirst.frc.team2791.util;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.Range;
import com.ni.vision.NIVision.StructuringElement;
import com.ni.vision.VisionException;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;

import java.util.ArrayList;

/**
 * Created by Akhil on 2/22/2016.
 */
public class ShakerCamera {

    private final double CAMERA_WIDTH_DEGREES = 53;
    private final int CAMERA_WIDTH_PIXELS = 320;
    private final int CAMERA_HEIGHT_PIXELS = 240;
    private NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
    private NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0, 0, 1, 1);
    private double AREA_MINIMUM = 0.5;
    private Image frame;
    private Image binaryFrame;
    private int imaqError;
    private Image filteredImage;
    private Image particleBinaryFrame;
    private StructuringElement box;
    private USBCamera cam;
    private Servo cameraServo;
    private boolean displayTargettingToDash = false;

    public ShakerCamera(String camPort) {
        cam = new USBCamera(camPort);
        cam.startCapture();
        cameraServo = new Servo(Constants.CAMERA_SERVO_PORT);
        frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
        binaryFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
        filteredImage = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
        particleBinaryFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
        cam.setExposureManual(1);
        cam.setBrightness(0);
        cam.updateSettings();
        box = new NIVision.StructuringElement(6, 4, 1);
        criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA, AREA_MINIMUM,
                100.0, 0, 0);
        SmartDashboard.putNumber("H min", 0);
        SmartDashboard.putNumber("H max", 255);
        SmartDashboard.putNumber("S min", 0);
        SmartDashboard.putNumber("S max", 255);
        SmartDashboard.putNumber("L min", 0);
        SmartDashboard.putNumber("L max", 255);
    }

    public void update(boolean drawBasicLine) {
        try {
            cam.getImage(frame);
            if (frame != null) {
                if (drawBasicLine)// if a basic line for lineup should be drawn
                    // then
                    // draw line
                    NIVision.imaqDrawLineOnImage(frame, frame, NIVision.DrawMode.DRAW_VALUE,
                            new NIVision.Point(CAMERA_WIDTH_PIXELS / 2, 0),
                            new NIVision.Point(320, CAMERA_HEIGHT_PIXELS), 130.0f);
                // if should display the modified image to the smartdashboard
                if (displayTargettingToDash) {
                    measureAndGetParticles();
                    CameraServer.getInstance().setImage(binaryFrame);
                } else
                    CameraServer.getInstance().setImage(frame);
            }
        } catch (VisionException npe) {
            System.out.println("ERROR: " + npe);
        }

    }

    public ParticleReport getTarget() {
        ArrayList<ParticleReport> reports = measureAndGetParticles();
        if (reports.size() == 0)
            return null;
        double targetPorportion = 0.6;//the height to width ratio of the target
        int counter = 0;
        for (ParticleReport par : reports) {
            double avgHeight = par.BoundingRectRight + par.BoundingRectLeft;
            double avgWidth = par.BoundingRectTop + par.BoundingRectBottom;
            avgHeight /= 2;
            avgWidth /= 2;
            double particlePorportion = avgHeight / avgWidth;//calculate the particles porportion
            if (Math.abs(particlePorportion - 0.6) > 0.1)
                reports.remove(counter);
            counter++;
        }

        if (reports.size() > 1) {
            //if there are multiple particles it finds the one closest to the center
            double minDiff = 0;
            int minDiffLoc = 0;
            counter = 0;
            for (ParticleReport p : reports) {
                if (minDiff > Math.abs(p.CenterOfMassX - CAMERA_HEIGHT_PIXELS)) {
                    minDiff = Math.abs(p.CenterOfMassX - CAMERA_HEIGHT_PIXELS);
                    minDiffLoc = counter;
                }
                counter++;
                return reports.get(minDiffLoc);
            }
        }
        return reports.get(0);
    }

    private ArrayList<ParticleReport> measureAndGetParticles() {
        //does a bunch of measurements on the image and its particles
        String output = "";
        //particle information is stored into the arraylist particles
        ArrayList<ParticleReport> particles = new ArrayList<ParticleReport>();
        //create and apply an hsl threshold on the current fame
        NIVision.imaqColorThreshold(binaryFrame, frame, 255, NIVision.ColorMode.HSL,
                new Range((int) SmartDashboard.getNumber("H min"), (int) SmartDashboard.getNumber("H max")),
                new Range((int) SmartDashboard.getNumber("S min"), (int) SmartDashboard.getNumber("S max")),
                new Range((int) SmartDashboard.getNumber("L min"), (int) SmartDashboard.getNumber("L max")));
        //set the lower threshold on area in the criteria filter
        criteria[0].lower = 1.0f;
        //use particle filter to remove unwanted particles
        imaqError = NIVision.imaqParticleFilter4(particleBinaryFrame, binaryFrame, criteria, filterOptions, null);
        //count the number of viable particles
        int numParticles = NIVision.imaqCountParticles(particleBinaryFrame, 1);
        //checks to make sure there is at least one particle
        if (numParticles > 0) {
            //creates an ouput string with all the data that has been collected
            output += "The number of particles: " + numParticles;
            // Measure each of the particles
            for (int particleIndex = 0; particleIndex < numParticles; particleIndex++) {
                // iterates through each particle
                //creates a particle report and then adds it to the arraylist
                ParticleReport par = new ParticleReport();
                particles.add(par);
                //finds how much of the particle covers the frame
                par.PercentAreaToImageArea = NIVision.imaqMeasureParticle(particleBinaryFrame,
                        particleIndex, 0, NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
                //adds the value to the output line with all its information
                output += "PercentAreaToImageArea: " + par.PercentAreaToImageArea + "\n";
                //measures the area of the particle and adds to the output
                par.Area = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                        NIVision.MeasurementType.MT_AREA);
                output += "Area: " + par.Area + "\n";
                //measures the upper width of the particle
                par.BoundingRectTop = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                        NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
                output += "BoundingRectTop: " + par.BoundingRectTop + "\n";
                //measures the left height of the particle
                par.BoundingRectLeft = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                        NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
                output += "BoundingRectLeft: " + par.BoundingRectTop + "\n";
                //measures the bottom width of the particle
                par.BoundingRectBottom = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex,
                        0, NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
                output += "BoundingRectBottom: " + par.BoundingRectTop + "\n";
                //measures the bottom height of the particle
                par.BoundingRectRight = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                        NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
                output += "BoundingRectRight: " + par.BoundingRectTop + "\n";
                //finds the center of mass in x coordinates of the particle
                par.CenterOfMassX = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                        NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
                output += "Center of Mass X: " + par.CenterOfMassX + "\n";
                //finds the center of mass in the y direction of the paricle
                par.CenterOfMassY = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                        NIVision.MeasurementType.MT_CENTER_OF_MASS_Y);
                output += "Center of Mass Y: " + par.CenterOfMassY + "\n";
                //creates a rectangle to cover the target
                NIVision.Rect r = new NIVision.Rect((int) par.BoundingRectTop,
                        (int) par.BoundingRectLeft,
                        Math.abs((int) (par.BoundingRectTop
                                - par.BoundingRectBottom)),
                        Math.abs((int) (par.BoundingRectLeft
                                - par.BoundingRectRight)));
                //draws the rectangle on the binary image
                NIVision.imaqDrawShapeOnImage(binaryFrame, binaryFrame, r, NIVision.DrawMode.DRAW_VALUE,
                        NIVision.ShapeMode.SHAPE_RECT, 125f);
                // using center of mass calculate the distance between the
                // two points ..theoretically......
                double angleFromMiddle = (CAMERA_WIDTH_DEGREES / 2)
                        / (CAMERA_WIDTH_PIXELS / 2);
                if (par.CenterOfMassX < CAMERA_WIDTH_PIXELS / 2)
                    angleFromMiddle *= -1 * (par.BoundingRectTop);
                else
                    angleFromMiddle *= par.BoundingRectTop - (CAMERA_WIDTH_PIXELS / 2);
                par.ThetaDifference = angleFromMiddle;
                output += "Theta diff: " + par.ThetaDifference;

            }
        }
        SmartDashboard.putString("Image output:", output);
        return particles;
    }

    public void cameraUp() {
        //bring servo arm up
        cameraServo.setAngle(0.5);
    }

    public void cameraDown() {
        //bring servo arm down
        cameraServo.setAngle(0);
    }

    public void setCameraValues(int exposure, int brightness) {
        //set the exposure and the brightness for when vision targetting
        cam.setExposureManual(exposure);
        cam.setBrightness(brightness);
        cam.updateSettings();
    }

    public void setCameraValuesAutomatic() {
        //set the exposure and the brightness for when vision targetting
        cam.setExposureAuto();
        cam.setBrightness(25);
        cam.updateSettings();
    }

    public void displayTargettingImageToDash(boolean value) {
        //set whether or not the brinary image should be displayed to the smartdashboard
        //this is ususally a little slower
        displayTargettingToDash = value;
    }

    public static class ParticleReport {
        //a class just to hold values of the particles
        public static double ThetaDifference;
        public static double PercentAreaToImageArea;
        public static double Area;
        public static double BoundingRectLeft;
        public static double BoundingRectTop;
        public static double BoundingRectRight;
        public static double BoundingRectBottom;
        public static double CenterOfMassX;
        public static double CenterOfMassY;
    }
}
