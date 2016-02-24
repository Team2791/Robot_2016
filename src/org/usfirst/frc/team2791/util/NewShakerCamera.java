package org.usfirst.frc.team2791.util;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.Range;
import com.ni.vision.NIVision.StructuringElement;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;

import java.util.Comparator;

/**
 * Created by Akhil on 2/22/2016.
 */
public class NewShakerCamera {

	private NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
	private NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0, 0, 1, 1);
	// these values should be taken off of grip
	private NIVision.Range TARGET_HUE_RANGE = new NIVision.Range(0, 255); // Hue
																			// range
	private NIVision.Range TARGET_SAT_RANGE = new NIVision.Range(0, 255); // Saturation
																			// range
	private NIVision.Range TARGET_VAL_RANGE = new NIVision.Range(0, 255);// value
																			// range
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
		String output = ""; // is
							// very
		cam.getImage(frame);
		if (doTargetting) {// if targetting should be done, this is here so we
							// wont always have targetting on
			NIVision.imaqColorThreshold(binaryFrame, frame, 255, NIVision.ColorMode.HSL,
					new Range((int) SmartDashboard.getNumber("H min"), (int) SmartDashboard.getNumber("H max")),
					new Range((int) SmartDashboard.getNumber("S min"), (int) SmartDashboard.getNumber("S max")),
					new Range((int) SmartDashboard.getNumber("L min"), (int) SmartDashboard.getNumber("L max")));// color
																													// threshold,
			// returns a binary
			// image(black and
			// white), according to
			// ranges
			// all pixels that fall in the hsv range will be turned to white
			criteria[0].lower = 1.0f;// sets the min area
			// particle filtration
			imaqError = NIVision.imaqParticleFilter4(particleBinaryFrame, binaryFrame, criteria, filterOptions, null);
			int numParticles = NIVision.imaqCountParticles(particleBinaryFrame, 1);// finds
			// NIVision.IMAQdxSetAttributeEnum(, "brightness", 20);
//			for (NIVision.IMAQdxAttributeVisibility c : NIVision.IMAQdxAttributeVisibility.values())
//				System.out.println("Attributes: " + c + " " + c.getValue()); // all

			// the
			// particles
			// System.out.println("Filtered particles" + numParticles);
			if (numParticles > 0) {
				// Measure particles and sort by particle size
				for (int particleIndex = 0; particleIndex < numParticles; particleIndex++) {
					// iterates through each particle ... in future should
					// remove particle if criteria not met

					ParticleReport par = new ParticleReport();
					par.PercentAreaToImageArea = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
					output += "PercentAreaToImageArea: " + par.PercentAreaToImageArea + "\n";
					par.Area = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_AREA);
					output += "Area: " + par.Area + "\n";
					par.BoundingRectTop = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
					output += "BoundingRectTop: " + par.BoundingRectTop + "\n";
					par.BoundingRectLeft = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
					output += "BoundingRectLeft: " + par.BoundingRectTop + "\n";
					par.BoundingRectBottom = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
					output += "BoundingRectBottom: " + par.BoundingRectTop + "\n";
					par.BoundingRectRight = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
					output += "BoundingRectRight: " + par.BoundingRectTop + "\n";
					NIVision.Rect r = new NIVision.Rect((int) par.BoundingRectTop, (int) par.BoundingRectLeft,
							Math.abs((int) (par.BoundingRectTop - par.BoundingRectBottom)),
							Math.abs((int) (par.BoundingRectLeft - par.BoundingRectRight)));
					NIVision.imaqDrawShapeOnImage(binaryFrame, binaryFrame, r, NIVision.DrawMode.DRAW_VALUE,
							NIVision.ShapeMode.SHAPE_RECT, 125f);
					par.CenterOfMassX = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
					par.CenterOfMassY = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_CENTER_OF_MASS_Y);
					// using center of mass calculate the distance between the
					// two points ..theoretically......
					output += "Theta diff: "
							+ Math.atan2(480 / 2 - par.CenterOfMassY, 640 / 2 - par.CenterOfMassX) * 180 / Math.PI;

				}
			}

		}

		if (frame != null)

			if (displayTargettingToDash)
				CameraServer.getInstance().setImage(binaryFrame);
			else
				CameraServer.getInstance().setImage(frame);
		SmartDashboard.putString("Image output:", output);
	}

	public class ParticleReport implements Comparator<ParticleReport>, Comparable<ParticleReport> {
		double PercentAreaToImageArea;
		double Area;
		double BoundingRectLeft;
		double BoundingRectTop;
		double BoundingRectRight;
		double BoundingRectBottom;
		double CenterOfMassX;
		double CenterOfMassY;

		public int compareTo(ParticleReport r) {
			return (int) (r.Area - this.Area);
		}

		public int compare(ParticleReport r1, ParticleReport r2) {
			return (int) (r1.Area - r2.Area);
		}
	}

}
