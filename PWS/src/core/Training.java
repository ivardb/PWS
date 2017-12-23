package core;

import java.awt.image.*;
import java.util.Random;

public class Training {
	
	private static int number_of_testsubjects = 50; 
	private static int number_of_pictures = 15; 				//the amount of pictures per person
	private static String image_path = "F:\\Fotos_AI\\gt_db";
	private static Random random = new Random();
	
	public static void train(int amount_of_runs) {
		int subject = random.nextInt(number_of_testsubjects) + 1;
		int train_subject;
		String str_subject;
		String str_train_subject;
		
		for(int i = 0; i<amount_of_runs; i++) {  
			if(random.nextInt(2) == 1) {			//determine if 2 pictures of the same subject will be chosen
				train_subject = subject;			// or if pictures from two different subjects are chosen
			} else {
				train_subject = -1;
			}
			if(subject < 10 && subject!=-1) {		//changes number to a proper syntax for our files
				str_subject = "0" + subject;
			} else {
				str_subject = Integer.toString(subject);
			}
			
			if(train_subject < 10 && train_subject != -1) {
				str_train_subject = "0" + train_subject;
			} else {
				str_train_subject = Integer.toString(train_subject);
			}
			
			//ik dacht dat er een foutje in jouw code gevonden had en schreef de twee onderstaande regels als vervanging
			//het bleek dat er toch geen fout in zat, maar ik heb ze als suggestie toch maar laten staan. -Maarten
		
			//strSubject = subject < 10 ? "0"+subject : Integer.toString(subject);
			//strTrain_subject = random.nextBoolean()==true ? strSubject : "";
			
			String first_path = getNextPath(str_subject);			//gets paths for our files and converts them to tensors
			String second_path = getNextPath(str_train_subject);
			Tensor[] first_tensor = loadTrainingset(first_path);
			Tensor[] second_tensor = loadTrainingset(second_path);
			
			//insert code for running a tensor through ai and back propagation
			
			if(subject == number_of_testsubjects) {
				subject = 1;
			} else {
				subject++;
			}
		}
	}
	
	private static Tensor[] loadTrainingset(String path) {							//converts a picture path to a tensor of that picture
		BufferedImage loaded_image = FileHandler.loadImage(path);
		Tensor[] loaded_tensor = Preprocessing.preprocess(loaded_image);
		return loaded_tensor;
	}
	
	private static String getNextPath(String subject) {							//determines a path for a given or random subject
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
	
	public static void setImagePath(String path) {						//setters for the different variables
		image_path = path;
	}
	
	public static void setNumberOfTestsubjects(int number) {
		number_of_testsubjects = number;
	}
	
	public static void setNumberOfPictures(int number) {
		number_of_pictures = number;
	}

}
