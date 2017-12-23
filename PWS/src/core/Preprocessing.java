package core;

import java.awt.geom.AffineTransform;
import java.awt.image.*;

public class Preprocessing 
{	
	//constants for picture size
	private static final int IMAGE_WIDTH = 256;					
	private static final int IMAGE_HEIGHT = 256;
	
	//scales and crops the image to the right size
	public static Tensor[] preprocess(BufferedImage input_image) 		
	{
		int width = input_image.getWidth();
		int height = input_image.getHeight();
		if((double)width/height > (double)IMAGE_WIDTH / IMAGE_HEIGHT)
		{
			//too wide
			double scale = (double)IMAGE_HEIGHT/height;
			input_image = scale(input_image, scale);
			width = input_image.getWidth();
			int x_begin = (width - IMAGE_WIDTH)/2;
			input_image = crop(input_image, x_begin, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
		else if((double)width/height < (double)IMAGE_WIDTH / IMAGE_HEIGHT)
		{
			//too small
			double scale = (double)IMAGE_WIDTH/width;
			input_image = scale(input_image,scale);
			height = input_image.getHeight();
			int y_begin = (height - IMAGE_WIDTH)/2;
			input_image = crop(input_image, 0, y_begin, IMAGE_WIDTH, IMAGE_HEIGHT);
		}
		float[] pixels = ((DataBufferFloat)input_image.getRaster().getDataBuffer()).getData();
		
		//convert the image to three tensors (red, green and blue)
		Tensor[] output = new Tensor[3];
		float[] tmp = new float[pixels.length/3];
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < tmp.length; j++)
			{
				tmp[j] = pixels[3*j+i];
			}
			output[i] = new Tensor(tmp, input_image.getWidth(), input_image.getHeight());
		}
		
		return output;
	}
	
	//scales the picture
	private static BufferedImage scale(BufferedImage input_image, double scale)			
	{
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale); 
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		input_image = op.filter(input_image, null);
		return input_image;
	}
	
	//crops the picture
	private static BufferedImage crop(BufferedImage input_image, int start_x, int start_y, int width, int height) 
	{
		BufferedImage output_img = input_image.getSubimage(start_x, start_y, width, height);
		return output_img;
	}
}
