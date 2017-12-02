package core;

import java.awt.geom.AffineTransform;
import java.awt.image.*;

import exceptions.DimensionException;

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
			//too wide
			double scale = IMAGE_HEIGHT/height;
			input_image = scale(input_image, scale);
			width = input_image.getWidth();
			int x_begin = (width - IMAGE_WIDTH)/2;
			input_image = crop(input_image, x_begin, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
		else if(width/height < IMAGE_WIDTH / IMAGE_HEIGHT)
		{
			//too small
			double scale = IMAGE_WIDTH/width;
			input_image = scale(input_image,scale);
			height = input_image.getHeight();
			int y_begin = (height - IMAGE_WIDTH)/2;
			input_image = crop(input_image, 0, y_begin, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
		float[] pixels = ((DataBufferFloat)input_image.getRaster().getDataBuffer()).getData();
		
		return new Tensor(pixels, input_image.getWidth(), input_image.getHeight(), 3);
	}
	
	private static BufferedImage scale(BufferedImage input_image, double scale)
	{
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale); 
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		input_image = op.filter(input_image, null);
		return input_image;
	}
	
	private static BufferedImage crop(BufferedImage input_image, int start_x, int start_y, int width, int height) 
	{
		BufferedImage output_img = input_image.getSubimage(start_x, start_y, width, height);
		return output_img;
	}
}
