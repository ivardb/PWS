package core;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {

	public static void SaveDataFile() {
		// File Content
		String fileContent = "test1.test2.test3";
		// File path
		String filePath = "D:\\test.txt";
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
	
}
