package core;
import java.awt.image.*;
import java.io.*;
import java.nio.file.*;
import javax.imageio.ImageIO;

public class FileHandler {

	public static void saveParameters(String file_path, String file_content) {
		// Writing content
				
		try {
			FileWriter f_writer = new FileWriter(file_path);
			BufferedWriter b_writer = new BufferedWriter(f_writer);
			b_writer.write(file_content);
			b_writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static String loadParameters(String file_path) {
		//get file contents in a string
		String file_content = null;
		try {
			file_content = new String(Files.readAllBytes(Paths.get(file_path)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file_content;
	}
	
	public static BufferedImage loadImage(String img_path,  int width, int height) {
		//create a buffered image from a given file
		
		File img = new File(img_path);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		try {
			image = ImageIO.read(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static void saveImage(BufferedImage buf_img, String save_path) {
		//save a buffered image to file
		File output_file = new File(save_path);
		try {
			ImageIO.write(buf_img, "png", output_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
