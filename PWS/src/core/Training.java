package core;

import java.awt.image.*;
import java.util.Random;

public class Training {
	
	private int number_of_testsubjects = 50; 
	private int number_of_pictures = 15; //the amount of pictures per person
	private String image_path = "F:\\Fotos_AI\\gt_db";
	Random random = new Random();
	
	public void Train(String path_map, int amount_of_runs) {
		int subject = random.nextInt(number_of_testsubjects) + 1;
		int train_subject;
		for(int i = 0; i<amount_of_runs; i++) {
			if(random.nextInt(2) == 1) {
				train_subject = subject;
			} else {
				train_subject = -1;
			}
			String path = getNextPath(Integer.toString(train_subject));
			Tensor loaded_tensor = loadTrainingset(path);
			
			//insert code for running a tensor through ai and backpropagation
			
			if(subject == number_of_testsubjects) {
				subject = 1;
			} else {
				subject++;
			}
		}
	}
	
	private Tensor loadTrainingset(String path) {
		BufferedImage loaded_image = FileHandler.loadImage(path);
		Tensor loaded_tensor = Preprocessing.preproces(loaded_image);
		return loaded_tensor;
	}
	
	private String getNextPath(String subject) {
		String picture_string;
		if(subject == "" || subject == "-1") {
			subject = "s" + random.nextInt(50) + 1;					
		} else if(subject.substring(0,1) != "s") {
			subject = "s" + subject;
		}
		int picture = random.nextInt(number_of_pictures) + 1;
		if(picture < 10) {
			picture_string = "0" + picture;
		} else {
			picture_string = Integer.toString(picture);
		}
		String output = image_path + "\\" + subject + "\\" + picture_string;
		return output;
	}
	
	public void setImagePath(String path) {
		image_path = path;
	}
	
	public void setNumberOfTestsubjects(int number) {
		number_of_testsubjects = number;
	}
	
	public void setNumberOfPictures(int number) {
		number_of_pictures = number;
	}
	
}
