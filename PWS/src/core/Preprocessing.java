package core;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

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
		
		byte[] pixels = ((DataBufferByte)input_image.getRaster().getDataBuffer()).getData();
		
		//convert the image to three tensors (red, green and blue)
		Tensor[] output = new Tensor[3];
		float[] tmp = new float[pixels.length/3];
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < tmp.length; j++)
			{
				tmp[j] = (float)pixels[3*j+i];
			}
			output[i] = new Tensor(tmp, input_image.getWidth(), input_image.getHeight());
		}
		
		return output;
	}
	
	//scales the picture
	private static BufferedImage scale(BufferedImage input_image, double scale)			
	{
		int new_width = (int)(input_image.getWidth()*scale);
		int new_height = (int)(input_image.getHeight()*scale);
		
		Image tmp = input_image.getScaledInstance(new_width, new_height, Image.SCALE_SMOOTH);
		BufferedImage new_img = new BufferedImage(new_width, new_height, BufferedImage.TYPE_3BYTE_BGR);
		
		Graphics2D g2d = new_img.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		
		return new_img;
	}
	
	//crops the picture
	private static BufferedImage crop(BufferedImage input_image, int start_x, int start_y, int width, int height) 
	{
		Image tmp = input_image.getSubimage(start_x, start_y, width, height);
		BufferedImage output_image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		
		Graphics2D g2d = output_image.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		
		return output_image;
	}
}
