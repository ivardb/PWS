package core;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Preprocessing 
{	
	private static final int IMAGE_WIDTH = 256;
	private static final int IMAGE_HEIGHT = 256;
	
	public static Tensor preproces() 
	{
		return null;
	}
	
	private static BufferedImage scale(BufferedImage input_image, double scale)
	{
		int output_img_width = (int) (input_image.getWidth() * scale);
		int output_img_height = (int) (input_image.getHeight() * scale);
		
		BufferedImage output_image = new BufferedImage(output_img_width, output_img_height, BufferedImage.TYPE_INT_BGR);
		AffineTransform scale_instance = AffineTransform.getScaleInstance(scale, scale);
		AffineTransformOp scale_op = new AffineTransformOp(scale_instance, AffineTransformOp.TYPE_BILINEAR);
		
		
		return null;
	}
}
