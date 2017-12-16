package core;

import java.awt.image.*;

public class Training {

	public void Train(String path_map) {
		
	}
	
	private Tensor loadTrainingset(String path) {
		BufferedImage loaded_image = FileHandler.loadImage(path);
		Tensor loaded_tensor = Preprocessing.preproces(loaded_image);
		return loaded_tensor;
	}
	
	private String getNextPath() {
		
		return null;
	}
		
}
