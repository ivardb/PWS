package core;
import java.awt.image.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FileHandler {

	public static void SaveParameters(String filePath, String fileContent) {
		// Writing content
				
		try {
			FileWriter fWriter = new FileWriter(filePath);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write(fileContent);
			bWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Done");
	}
	
	public static BufferedImage LoadImage(String imgPath,  int width, int height) {
		//create a buffered image from a given file
		
		File img = new File(imgPath);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		try {
			image = ImageIO.read(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}
