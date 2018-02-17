package UI;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import core.*;
import exceptions.DimensionException;
import exceptions.NumericalException;

public class main {

	public static final int COST_RUN_COUNT = 100;
	public static final int TRAINING_RUN_COUNT = 1500;
	public static final int BATCH_COUNT = 40;
	public static final float LEARNING_RATE = 0.01f;
	
	public static void main(String[] args) 
	{	
		CharacterFrequencyRecognition AI = new CharacterFrequencyRecognition();
		AI.initializeAI(4);
		AI.UI();
	}

}
