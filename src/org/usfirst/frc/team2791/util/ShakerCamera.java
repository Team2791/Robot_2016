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

import java.awt.Point;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Akhil on 2/22/2016.
 */
public class ShakerCamera implements Runnable {

	private final double CAMERA_WIDTH_DEGREES = 53;
	private int CAMERA_WIDTH_PIXELS = 720;
	private int CAMERA_HEIGHT_PIXELS = 480;
	private NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
	private NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0, 0, 1, 1);
	private double AREA_MINIMUM = 7;
	private Image frame;
	private Image binaryFrame;
	private int imaqError;
	private Image filteredImage;
	private Image particleBinaryFrame;
	private StructuringElement box;
	private USBCamera cam;
	private Servo cameraServo;
	private boolean displayTargettingToDash = false;
	private int cameraExposure = 1;
	private int cameraBrightness = 1;
	private boolean cameraAutoSettings = true;
	private boolean cameraServoUp = false;
	private boolean cameraValsOnlyOnce = false;
	private TreeMap<Double, Double> rangeTable;
	private double rangeOffset = 0.0;
	private double targetHeightIn = 0;
	private double cameraHeightIn = 0;
	private double cameraPitchDeg = 45;

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
		cam.setSize(CAMERA_WIDTH_PIXELS, CAMERA_HEIGHT_PIXELS);
		cam.updateSettings();
		box = new NIVision.StructuringElement(6, 4, 1);
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
		// rangeTable.put(, );//DISTANCE, rpm
	}

	public void run() {
		while (true) {
			try {
				cam.getImage(frame);
				if (frame != null) {
					// if should display the modified image to the
					// smartdashboard
					if (SmartDashboard.getBoolean("display targetting")) {
						measureAndGetParticles();
						CameraServer.getInstance().setImage(particleBinaryFrame);
					} else if (SmartDashboard.getBoolean("Debug Image") || commands.AutoLineUpShot.isRunning()) {
						measureAndGetParticles();
						NIVision.imaqDrawLineOnImage(binaryFrame, binaryFrame, NIVision.DrawMode.DRAW_VALUE,
								new NIVision.Point(CAMERA_WIDTH_PIXELS / 2, 0),
								new NIVision.Point(CAMERA_WIDTH_PIXELS / 2, CAMERA_HEIGHT_PIXELS), 125f);
						CameraServer.getInstance().setImage(binaryFrame);
					}

					else
						CameraServer.getInstance().setImage(frame);
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

	public double getRange() {// make sure to only call this if we know a target
								// exists
		double range = (targetHeightIn - cameraHeightIn) / Math
				.tan(getNormalizedCenterOfMass(getTarget().CenterOfMassY) * (CAMERA_WIDTH_DEGREES / 2) + cameraPitchDeg)
				* (Math.PI / 180);
		return range;

	}

	public ParticleReport getTarget() {
		ArrayList<ParticleReport> reports = measureAndGetParticles();
		if (reports.size() == 0) {
			System.out.println("The camera reports are empty");
			return null;
		}
		int targetLoc = 0;
		double maxPercentArea = 0;
		int counter = 0;
		for (ParticleReport par : reports) {
			if (maxPercentArea < par.PercentAreaToImageArea) {
				maxPercentArea = par.PercentAreaToImageArea;
				targetLoc = counter;
			}
			counter++;
		}
		return reports.get(targetLoc);
	}

	private ArrayList<ParticleReport> measureAndGetParticles() {
		// does a bunch of measurements on the image and its particles
		String output = "";
		// particle information is stored into the arraylist particles
		ArrayList<ParticleReport> particles = new ArrayList<ParticleReport>();
		// create and apply an hsl threshold on the current fame
		NIVision.imaqColorThreshold(binaryFrame, frame, 255, NIVision.ColorMode.HSL,
				new Range((int) SmartDashboard.getNumber("H min"), (int) SmartDashboard.getNumber("H max")),
				new Range((int) SmartDashboard.getNumber("S min"), (int) SmartDashboard.getNumber("S max")),
				new Range((int) SmartDashboard.getNumber("L min"), (int) SmartDashboard.getNumber("L max")));
		// set the lower threshold on area in the criteria filter
		criteria[0].lower = 0.3f;
		// use particle filter to remove unwanted particles
		imaqError = NIVision.imaqParticleFilter4(particleBinaryFrame, binaryFrame, criteria, filterOptions, null);
		// System.out.println("I just did a filter to remove noise");
		// count the number of viable particles
		int numParticles = NIVision.imaqCountParticles(particleBinaryFrame, 1);
		// System.out.println("I just counted the number of particles "+
		// numParticles );
		// checks to make sure there is at least one particle

		if (numParticles > 0) {
			// creates an ouput string with all the data that has been collected
			output += "The number of particles: " + numParticles;
			// Measure each of the particles
			// System.out.println("Measuring the particles");
			for (int particleIndex = 0; particleIndex < numParticles; particleIndex++) {
				// System.out.println("im about to do convex hull");
				// NIVision.imaqConvexHull(particleBinaryFrame, binaryFrame,0);
				// System.out.println("i did the convex hull");
				// iterates through each particle
				// creates a particle report and then adds it to the arraylist
				ParticleReport par = new ParticleReport();
				particles.add(par);

				// System.out.println("im about to measure;");
				// finds how much of the particle covers the frame
				par.PercentAreaToImageArea = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
						NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
				// System.out.println("I did the first are measure;");
				// adds the value to the output line with all its information
				output += "PercentAreaToImageArea: " + par.PercentAreaToImageArea + "\n";
				// measures the area of the particle and adds to the output
				par.Area = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
						NIVision.MeasurementType.MT_AREA);
				// System.out.println("I did the second are calculation");
				output += "Area: " + par.Area + "\n";
				// System.out.println("measured the area");
				// measures the upper width of the particle
				par.BoundingRectTop = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
						NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
				output += "BoundingRectTop: " + par.BoundingRectTop + "\n";
				// measures the left height of the particle
				par.BoundingRectLeft = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
						NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
				output += "BoundingRectLeft: " + par.BoundingRectTop + "\n";
				// measures the bottom width of the particle
				par.BoundingRectBottom = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
						NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
				output += "BoundingRectBottom: " + par.BoundingRectTop + "\n";
				// measures the bottom height of the particle
				par.BoundingRectRight = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
						NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
				output += "BoundingRectRight: " + par.BoundingRectTop + "\n";
				// System.out.println("Measured the bounding boxes");
				par.CenterOfMassX = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
						NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
				// System.out.println("measured the center of mass");
				// finds the center of mass in x coordinates of the particle
				// par.CenterOfMassX =
				// NIVision.imaqMeasureParticle(particleBinaryFrame,
				// particleIndex, 0,
				// NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
				// par.CenterOfMassX = (par.BoundingRectLeft +
				// par.BoundingRectRight)/2;
				// par.CenterOfMassX /= 2;
				// // finds the center of mass in the y direction of the paricle
				// par.CenterOfMassY =
				// NIVision.imaqMeasureParticle(particleBinaryFrame,
				// particleIndex, 0,
				// NIVision.MeasurementType.MT_FIRST_PIXEL_Y) +
				// par.BoundingRectLeft;
				// par.CenterOfMassY /= 2;
				// output += "Center of Mass Y: " + par.CenterOfMassY + "\n";
				// using center of mass calculate the distance between the
				// two points ..theoretically......
				output += "Normalized center of mass x " + getNormalizedCenterOfMass(par.CenterOfMassX);
				output += "Normalized center of mass y " + getNormalizedCenterOfMass(par.CenterOfMassY);
				double angleFromMiddle = CAMERA_WIDTH_DEGREES * getNormalizedCenterOfMass(par.CenterOfMassX);
				par.ThetaDifference = angleFromMiddle / 2;
				output += "Theta diff: " + par.ThetaDifference;
				// System.out.println("Measured the theata diff");
				SmartDashboard.putNumber("Theta diff", par.ThetaDifference);
				SmartDashboard.putNumber("center of mass x", par.CenterOfMassX);
				SmartDashboard.putNumber("Boudnding rect top", par.BoundingRectTop);
				SmartDashboard.putNumber("Normalized center of mass x", getNormalizedCenterOfMass(par.CenterOfMassX));

				// creates a rectangle to cover the target
				NIVision.Rect r = new NIVision.Rect((int) par.BoundingRectTop, (int) par.BoundingRectLeft,
						Math.abs((int) (par.BoundingRectTop - par.BoundingRectBottom)),
						Math.abs((int) (par.BoundingRectLeft - par.BoundingRectRight)));
				// draws the rectangle on the binary image
				NIVision.imaqDrawShapeOnImage(binaryFrame, binaryFrame, r, NIVision.DrawMode.DRAW_VALUE,
						NIVision.ShapeMode.SHAPE_RECT, 125f);

			}

		}
		SmartDashboard.putString("Image output:", output);
		return particles;
	}

	public void cameraUp() {
		// bring servo arm up
		cameraServo.set(0.4);
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

	public void displayTargettingImageToDash(boolean value) {
		// set whether or not the brinary image should be displayed to the
		// smartdashboard
		// this is ususally a little slower
		displayTargettingToDash = value;
	}

	public double getNormalizedCenterOfMass(double currentCenterInPixels) {
		return ((2 * currentCenterInPixels) / CAMERA_WIDTH_PIXELS) - 1;
	}

	public static class ParticleReport {
		// a class just to hold values of the particles
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
