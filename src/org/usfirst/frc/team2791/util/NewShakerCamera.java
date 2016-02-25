package org.usfirst.frc.team2791.util;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.Range;
import com.ni.vision.NIVision.StructuringElement;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;

/**
 * Created by Akhil on 2/22/2016.
 */
public class NewShakerCamera {

    private final double CAMERA_WIDTH_DEGREES = 53;
    private final double CAMERA_WIDTH_PIXELS = 320;
    private NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
    private NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0, 0, 1, 1);
    private double AREA_MINIMUM = 0.5; // Min area (probably best if gotten from
    // practice field or target)
    private double WID_RATIO = 2.22; // Tote long side = 26.9 / Tote height =
    // 12.1 = 2.22
    // private double HEIGHT_RATIO = 1.4; //Tote short side = 16.9 / Tote height
    // = 12.1 = 1.4
    private double SCORE_MIN = 75.0; // min rectangularity score
    private double VIEW_ANGLE = 49.4; // Angle width of camera
    private Image frame;
    private Image binaryFrame;
    private int imaqError;
    private int session;
    private Image filteredImage;
    private Image particleBinaryFrame;
    private StructuringElement box;
    private USBCamera cam;

    public NewShakerCamera(String camPort) {
        cam = new USBCamera(camPort);
        cam.startCapture();
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

    public void update(boolean doTargetting, boolean displayTargettingToDash) {// displaying
        // targetting
        // is
        // very
        cam.getImage(frame);
        if (doTargetting) {// if targetting should be done, this is here so we
            measureImage(frame);
        }

        if (frame != null)
            if (displayTargettingToDash)
                CameraServer.getInstance().setImage(binaryFrame);
            else
                CameraServer.getInstance().setImage(frame);
    }

    public void measureImage(Image image) {
        // wont always have targetting on
        String output = "";
        NIVision.imaqColorThreshold(binaryFrame, frame, 255, NIVision.ColorMode.HSL,
                new Range((int) SmartDashboard.getNumber("H min"), (int) SmartDashboard.getNumber("H max")),
                new Range((int) SmartDashboard.getNumber("S min"), (int) SmartDashboard.getNumber("S max")),
                new Range((int) SmartDashboard.getNumber("L min"), (int) SmartDashboard.getNumber("L max")));
        criteria[0].lower = 1.0f;
        imaqError = NIVision.imaqParticleFilter4(particleBinaryFrame, binaryFrame, criteria, filterOptions, null);
        int numParticles = NIVision.imaqCountParticles(particleBinaryFrame, 1);// finds
        if (numParticles > 0) {
            // Measure particles and sort by particle size
            for (int particleIndex = 0; particleIndex < numParticles; particleIndex++) {
                // iterates through each particle ... in future should
                // remove particle if criteria not met
                ParticleReport.PercentAreaToImageArea.set(NIVision.imaqMeasureParticle(particleBinaryFrame,
                        particleIndex, 0, NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA));
                output += "PercentAreaToImageArea: " + ParticleReport.PercentAreaToImageArea.getValue() + "\n";
                ParticleReport.Area.set(NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                        NIVision.MeasurementType.MT_AREA));
                output += "Area: " + ParticleReport.Area.getValue() + "\n";
                ParticleReport.BoundingRectTop.set(NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                        NIVision.MeasurementType.MT_BOUNDING_RECT_TOP));
                output += "BoundingRectTop: " + ParticleReport.BoundingRectTop + "\n";
                ParticleReport.BoundingRectLeft.set(NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                        NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT));
                output += "BoundingRectLeft: " + ParticleReport.BoundingRectTop + "\n";
                ParticleReport.BoundingRectBottom.set(NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex,
                        0, NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM));
                output += "BoundingRectBottom: " + ParticleReport.BoundingRectTop + "\n";
                ParticleReport.BoundingRectRight.set(NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                        NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT));
                output += "BoundingRectRight: " + ParticleReport.BoundingRectTop + "\n";
                NIVision.Rect r = new NIVision.Rect((int) ParticleReport.BoundingRectTop.getValue(),
                        (int) ParticleReport.BoundingRectLeft.getValue(),
                        Math.abs((int) (ParticleReport.BoundingRectTop.getValue()
                                - ParticleReport.BoundingRectBottom.getValue())),
                        Math.abs((int) (ParticleReport.BoundingRectLeft.getValue()
                                - ParticleReport.BoundingRectRight.getValue())));
                NIVision.imaqDrawShapeOnImage(binaryFrame, binaryFrame, r, NIVision.DrawMode.DRAW_VALUE,
                        NIVision.ShapeMode.SHAPE_RECT, 125f);
                ParticleReport.CenterOfMassX.set(NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                        NIVision.MeasurementType.MT_CENTER_OF_MASS_X));
                ParticleReport.CenterOfMassY.set(NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
                        NIVision.MeasurementType.MT_CENTER_OF_MASS_Y));
                // using center of mass calculate the distance between the
                // two points ..theoretically......
                double angleFromMiddle = (CAMERA_WIDTH_DEGREES / 2) * (ParticleReport.BoundingRectTop.getValue())
                        / (CAMERA_WIDTH_PIXELS / 2);
                if (ParticleReport.CenterOfMassX.getValue() < CAMERA_WIDTH_PIXELS / 2)
                    angleFromMiddle *= -1;
                ParticleReport.ThetaDifference.set(angleFromMiddle);
                output += "Theta diff: " + ParticleReport.ThetaDifference.getValue();
            }
        }
        SmartDashboard.putString("Image output:", output);
    }

    public enum ParticleReport {
        ThetaDifference, PercentAreaToImageArea, Area, BoundingRectLeft, BoundingRectTop, BoundingRectRight, BoundingRectBottom, CenterOfMassX, CenterOfMassY;

        private double value;

        public void set(double varValue) {
            this.value = varValue;
        }

        public double getValue() {
            return this.value;
        }
    }
}