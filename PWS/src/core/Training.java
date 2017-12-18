package core;

import java.awt.image.*;
import java.util.Random;

public class Training {
	
	private int number_of_testsubjects = 50; 
	private int number_of_pictures = 15; 				//the amount of pictures per person
	private String image_path = "F:\\Fotos_AI\\gt_db";
	Random random = new Random();
	
	public void Train(int amount_of_runs) {
		int subject = random.nextInt(number_of_testsubjects) + 1;
		int train_subject;
		String strSubject;
		String strTrain_subject;
		
		for(int i = 0; i<amount_of_runs; i++) {  
			if(random.nextInt(2) == 1) {			//determine if 2 pictures of the same subject wiil be chosen
				train_subject = subject;			// or if pictures from two different subjects are chosen
			} else {
				train_subject = -1;
			}
			if(subject < 10 && subject!=-1) {		//changes number to a proper syntax for our files
				strSubject = "0" + subject;
			} else {
				strSubject = Integer.toString(subject);
			}
			
			if(train_subject < 10 && train_subject != -1) {
				strTrain_subject = "0" + train_subject;
			} else {
				strTrain_subject = Integer.toString(train_subject);
			}
			
			String first_path = getNextPath(strSubject);			//gets paths for our files and converts them to tensors
			String second_path = getNextPath(strTrain_subject);
			Tensor first_tensor = loadTrainingset(first_path);
			Tensor second_tensor = loadTrainingset(second_path);
			
			//insert code for running a tensor through ai and back propagation
			
			if(subject == number_of_testsubjects) {
				subject = 1;
			} else {
				subject++;
			}
		}
	}
	
	private Tensor loadTrainingset(String path) {							//converts a picture path to a tensor of that picture
		BufferedImage loaded_image = FileHandler.loadImage(path);
		Tensor loaded_tensor = Preprocessing.preproces(loaded_image);
		return loaded_tensor;
	}
	
	private String getNextPath(String subject) {							//determines a path for a given or random subject
		String picture_string;
		
		if(subject == "" || subject == "-1") {
			subject = "s" + random.nextInt(number_of_testsubjects) + 1;					
		} else if(subject.substring(0,1) != "s") {
			subject = "s" + subject;
		}
		
		int picture = random.nextInt(number_of_pictures) + 1;
		if(picture < 10) {													//puts picture number in proper syntax
			picture_string = "0" + picture+".jpeg";
		} else {
			picture_string = Integer.toString(picture);
		}
		
		String output = image_path + "\\" + subject + "\\" + picture_string;		//puts together the full path string
		return output;
	}
	
	public void setImagePath(String path) {						//setters for the different variables
		image_path = path;
	}
	
	public void setNumberOfTestsubjects(int number) {
		number_of_testsubjects = number;
	}
	
	public void setNumberOfPictures(int number) {
		number_of_pictures = number;
	}

}
