package org.usfirst.frc.team2791.util;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.RGBValue;
import com.ni.vision.NIVision.Rect;
import com.ni.vision.NIVision.ShapeMode;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

//public class ShakerCamera extends AxisCamera {
public class ShakerCamera {
	private final double cameraAngleWidth = 47 / 2;
	private final int STARTX = 320;
	private final int STARTY = 0;
	private final int ENDX = 320;
	private final int ENDY = 480;
	private int cameraPixelHeight = 240;
	private double halfCamHtFt;
	private int halfCamHtPx;
	private double targetHeightFt;
	private double targetHeightPx;
	private double distance;
	private NetworkTable netTable;
	private NIVision.Image image;
	private int session;
	private boolean drawOnImage;

	public ShakerCamera(String cameraPort, boolean shouldDrawOnImage) {
		try {
			image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
			session = NIVision.IMAQdxOpenCamera(cameraPort,
					NIVision.IMAQdxCameraControlMode.CameraControlModeController);
			NIVision.IMAQdxConfigureGrab(this.session);
			halfCamHtPx = 120;
			targetHeightFt = 1.16667;
			this.drawOnImage = shouldDrawOnImage;
		} catch (Error e) {

		}
	}

	public void update() {
		NIVision.IMAQdxStartAcquisition(this.session);
		NIVision.IMAQdxGrab(session, image, 1);

		// System.out.println(NIVision.imaqGetImageSize(image));
		if (drawOnImage) {
			NIVision.imaqDrawLineOnImage(image, image, NIVision.DrawMode.DRAW_VALUE, new NIVision.Point(STARTX, STARTY),
					new NIVision.Point(ENDX, ENDY), 130.0f);
			NIVision.imaqDrawShapeOnImage(image, image, new Rect(STARTX, STARTY, 30, 30), DrawMode.DRAW_VALUE,
					ShapeMode.SHAPE_RECT, 125f);
		}
		CameraServer.getInstance().setImage(image);
		NIVision.imaqDrawLineOnImage(image, image, NIVision.DrawMode.DRAW_VALUE,
				new NIVision.Point(STARTX + 150, STARTY), new NIVision.Point(ENDX + 150, ENDY), 4.0f);
		// CameraServer.getInstance().setImage(image);

	}

	public double getDistance() {// send next camera image
		netTable = NetworkTable.getTable("GRIP/myContoursReport");
		Double[] height = netTable.getNumberArray("height", new Double[0]);
		halfCamHtFt = (height[0] * halfCamHtFt) / halfCamHtPx;
		return distance = halfCamHtFt / (Math.tan(Math.toDegrees(cameraAngleWidth)));
	}

}
