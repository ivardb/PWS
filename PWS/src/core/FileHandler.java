package core;
import java.awt.image.*;
import java.io.*;
import java.nio.file.*;

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
	
	public static String LoadParameters(String filePath) {
		//get file contents in a string
		String fileContent = null;
		try {
			fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContent;
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
	
	public static void SaveImage(BufferedImage BufImg, String savePath) {
		//save a buffered image to file
		File outputFile = new File(savePath);
		try {
			ImageIO.write(BufImg, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
