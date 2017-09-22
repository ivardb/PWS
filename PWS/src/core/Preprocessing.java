package core;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Preprocessing 
{	
	private static final int IMAGE_WIDTH = 256;
	private static final int IMAGE_HEIGHT = 256;
	
	public static Tensor preproces(BufferedImage input_image) 
	{
		int width = input_image.getWidth();
		int height = input_image.getHeight();
		if(width/height > IMAGE_WIDTH / IMAGE_HEIGHT)
		{
			//too broad
			double scale = IMAGE_HEIGHT/height;
			input_image = scale(input_image, scale);
			width = input_image.getWidth();
			int x_begin = (width - IMAGE_WIDTH)/2;
			input_image = crop(input_image, x_begin, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
		else
		{
			//too small
			double scale = IMAGE_WIDTH/width;
			input_image = scale(input_image,scale);
			height = input_image.getHeight();
			int y_begin = (height - IMAGE_WIDTH)/2;
			input_image = crop(input_image, 0, y_begin, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
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
	
	private static BufferedImage crop(BufferedImage input_image, int start_x, int start_y, int width, int height) 
	{
		BufferedImage output_img = input_image.getSubimage(start_x, start_y, width, height);
		return output_img;
	}
}
