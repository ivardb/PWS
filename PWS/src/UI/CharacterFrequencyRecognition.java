package UI;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import core.ConvolutionalNeuralNetwork;
import core.FileHandler;
import core.KernelLayer;
import core.NeuronLayer;
import core.Tensor;

/*This is an AI which can recognize multiple languages by the frequency of the characters in a string.
 * It is equipped with an UI, but can also be used from within other classes.*/
public class CharacterFrequencyRecognition {
	
	private static final String allowed_chars_string = "abcdefghijklmnopqrstuvwxyz";
	private static String[] allowed_chars = allowed_chars_string.split("");
	
	public static ConvolutionalNeuralNetwork conv_net;
	
	public static String getRandomSubstring(String str, int length)
	{
		if(length >= str.length())
		{
			return str;
		}
		else
		{
			int start_index = Math.floorMod(ThreadLocalRandom.current().nextInt(), str.length()-length);
			return str.substring(start_index,start_index+length);
		}
	}
	
	public static float[] computeFrequencies(String str)
	{		
		float[] frequencies = new float[26];
		int processed_length = 0;
		for(int i = 0; i < allowed_chars.length; i++)
		{
			frequencies[i] = str.length() - str.replace(allowed_chars[i], "").length();
			processed_length += frequencies[i];
		}
		for(int i = 0; i < allowed_chars.length; i++)
		{
			frequencies[i] /= processed_length;
		}
		
		return frequencies;
	}

	public CharacterFrequencyRecognition()
	{
	}
	
	public static boolean initializeAI(String path)
	{
		try
		{
			String network_string = FileHandler.readFromFile(path);
			conv_net = ConvolutionalNeuralNetwork.fromString(network_string);
		} catch(Exception e)
		{
			return false;
		}
		
		return true;
	}
	
	public static boolean initializeAI(int language_count)
	{
		//standard AI is initialized. 26 neurons in the first layer, 10 in the second and a variable number in the third (output)
		try
		{
			//create neuron layers
			NeuronLayer neuron_layer1 = new NeuronLayer(26, new int[] {}, new int[] {});
			NeuronLayer neuron_layer2 = new NeuronLayer(10, new int[] {}, new int[] {});
			NeuronLayer neuron_layer3 = new NeuronLayer(language_count, new int[] {}, new int[] {});
			
			//create kernel layers with all values randomly between -1 and 1
			Tensor[][] layer1_weights = new Tensor[26][];
			for(int i = 0; i < 26; i++)
			{
				layer1_weights[i] = new Tensor[10];
				for(int j = 0; j < 10; j++)
				{
					layer1_weights[i][j] = new Tensor(-3.0f, 3.0f);
				}
			}
			KernelLayer kernel_layer1 = new KernelLayer(layer1_weights);
			
			
			Tensor[][] layer2_weights = new Tensor[10][];
			for(int i = 0; i < 10; i++)
			{
				layer2_weights[i] = new Tensor[language_count];
				for(int j = 0; j < language_count; j++)
				{
					layer2_weights[i][j] = new Tensor(-3.0f, 3.0f);
				}
			}
			KernelLayer kernel_layer2 = new KernelLayer(layer2_weights);
			
			//create a convolutional neural net
			conv_net = new ConvolutionalNeuralNetwork(neuron_layer1);
			
			//add all the layers to the net
			conv_net.addKernelLayer(kernel_layer1);
			conv_net.addNeuronLayer(neuron_layer2);
			conv_net.addKernelLayer(kernel_layer2);
			conv_net.addNeuronLayer(neuron_layer3);
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static float computeCost(String input, int desired_output) throws Exception
	{
		//this function takes in a single (input, output) pair and computes the cost(error)
		//for each output node, the difference between the true output and the desired output is taken
		//and squared. The cost is half the sum of these squares.
		//squares are used for two reasons: the sign of the difference doesn't matter ( being off by -4 is the same as being off by 4)
		//and the further away from the output, the higher the derivative of the cost, which means
		//the quantity minimized using backpropagation is better represented.
		
		float[] frequencies = computeFrequencies(input);
		
		//create the input tensor array
		//the tensors are of dimension zero (numbers), so no size data has to be provided, just a single float
		Tensor[] input_tensors = new Tensor[26];
		for(int i = 0 ; i < 26; i++)
		{
			input_tensors[i] = new Tensor(new float[] {frequencies[i]});
		}
		
		//run the input through the network
		conv_net.process(input_tensors);
		
		//run over all outputs in order to compute the sum
		float cost = 0.0f;
		for(int i = 0; i < conv_net.neuron_out_count; i++)
		{
			if(i == desired_output)
			{
				cost += 0.5*Math.pow(10-conv_net.neuron_layers.get(conv_net.neuron_layer_count-1).neuron_data[i].getSerializedData()[0], 2);
			}
			else
			{
				cost += 0.5*Math.pow(conv_net.neuron_layers.get(conv_net.neuron_layer_count-1).neuron_data[i].getSerializedData()[0], 2);
			}
		}
		
		return cost;
	}

	public static int compute(String input) throws Exception
	{
		//computes the language for a single given input.
		float[] frequencies = computeFrequencies(input);
	
		//create the input tensor array
		//the tensors are of dimension zero (numbers), so no size data has to be provided, just a single float
		Tensor[] input_tensors = new Tensor[26];
		for(int i = 0 ; i < 26; i++)
		{
			input_tensors[i] = new Tensor(new float[] {frequencies[i]});
		}
		
		//run the input through the network
		conv_net.process(input_tensors);
		
		//set the first output node as the one with the highest value
		int index = 0;
		float max_val = conv_net.neuron_layers.get(conv_net.neuron_layer_count-1).neuron_data[0].getSerializedData()[0];
		
		//iterate over the others to compute the true highest value
		for(int i = 1; i < conv_net.neuron_out_count; i++)
		{
			//System.out.println(conv_net.neuron_layers.get(conv_net.neuron_layer_count-1).neuron_data[i].getSerializedData()[0]);
			if(conv_net.neuron_layers.get(conv_net.neuron_layer_count-1).neuron_data[i].getSerializedData()[0] >  max_val)
			{
				index = i;
				max_val = conv_net.neuron_layers.get(conv_net.neuron_layer_count-1).neuron_data[i].getSerializedData()[0];
			}
		}
		return index;
	}
	
	public static boolean isCorrect(String input, int desired_output) throws Exception
	{
		//runs a single input through the network and checks whether the output node with the highest value is the desired one
		
		return compute(input) == desired_output;
	}

	public static void train(String[] inputs, int[] desired_outputs, float learning_rate) throws Exception
	{
		//trains a single batch
		
		if(inputs.length != desired_outputs.length)
			throw new Exception("inputs and outputs must be the same length");
		
		//load into tensors
		Tensor[][] input_tensors = new Tensor[inputs.length][];
		Tensor[][] output_tensors = new Tensor[desired_outputs.length][];
		float[] frequencies;
		for(int i = 0; i < inputs.length; i++)
		{
			frequencies = computeFrequencies(inputs[i]);
			input_tensors[i] = new Tensor[26];
			for(int j = 0; j < 26; j++)
			{
				input_tensors[i][j] = new Tensor(new float[] {frequencies[j]});
			}
			
			output_tensors[i] = new Tensor[conv_net.neuron_out_count];
			for(int j = 0; j < conv_net.neuron_out_count; j++)
			{
				 output_tensors[i][j] = new Tensor(new float[] {j == desired_outputs[i] ? 10.0f: 0.0f});
			}
		}
		
		//train
		conv_net.backpropagate(input_tensors, output_tensors, learning_rate);
	}

	public static void UI()
	{
		Scanner scanner = new Scanner(System.in);
		
		while(true)
		{
			System.out.print("Welcome to the language recognition network!\nWould you like to\n\t(1) change the network\n\t(2) train the network\n\t(3) use the network\n:");
			
			//UI vars
			int ans1 = scanner.nextInt();
			int ans2, ans3;
			String ans4;
			boolean resolved;
			
			//training vars
			String[] training_paths= new String[conv_net.neuron_out_count]; 
			int sample_size, batch_size, cycle_count;
			float learning_rate;
			
			//other
			int test_run_count;
			
			if(ans1 == 1)
			{
				resolved = false;
				while(!resolved)
				{
					System.out.print("Would you like to\n\t(1) reinitialize to the standard network\n\t(2) load a custom network\n\t(3) save the current network\n\t(4) go back to the main menu\n:");
					ans2 = scanner.nextInt();
					scanner.nextInt();
					
					if(ans2 == 1)
					{
						System.out.print("\nFor how many languages would you like the default network to be setup: ");
						ans3 = scanner.nextInt();
						scanner.nextLine();
						if(ans3 > 0)
						{
							System.out.print("\nYou are about to reinitialize the network for "+ans3+" languages. Are you sure(y/n):");
							ans4 = scanner.nextLine(); 
							if(ans4.equals("y"))
							{
								initializeAI(ans3);
								System.out.println("Network reinitialized. Returning to main menu...\n\n");
								training_paths = new String[conv_net.neuron_layer_count];
							}
						}
						resolved = true;
					}
					else if(ans2 == 2)
					{
						System.out.print("Please enter the path to your custom network file: ");
						ans4 = scanner.nextLine();
						System.out.print("\nYou are about to reinitialize the network. Are you sure (y/n): ");
						ans4 = scanner.nextLine(); 
						if(ans4.equals("y"))
						{
							try 
							{
								initializeAI(ans4);
								training_paths = new String[conv_net.neuron_layer_count];
							}
							catch(Exception e)
							{
								System.out.println("There was a problem loading the file. Returning to main menu...\n\n");
							}
						}
						resolved = true;
					}
					else if(ans2 == 3)
					{
						System.out.print("Please enter the filepath: ");
						ans4 = scanner.nextLine();
						try
						{
							core.FileHandler.writeToFile(ans4, ConvolutionalNeuralNetwork.toString(conv_net));
							System.out.println("Network data saved to: "+ans4);
						}
						catch(Exception e)
						{
							System.out.println("An exception occurred during saving. Returning to main menu...\n\n");
						}
						resolved = true;
					}
					else if(ans2 == 4)
					{
						resolved = true;
					}
				}
			}
			else if(ans1 == 2)
			{
				resolved = false;
				while(!resolved)
				{
					scanner.nextLine();
					System.out.print("Are you sure you want to commence the training procedure(y/n)");
					if(scanner.nextLine().equals("y"))
					{
						//obtaining paths to files which contain a bunch of text in a certain language
						System.out.println("The network will be trained to recognize "+conv_net.neuron_out_count+" possible languages.");
						for(int i = 0; i < conv_net.neuron_out_count; i++)
						{
							System.out.print("Please supply a location for the training data for language "+i+":");
							training_paths[i] = scanner.nextLine();
						}
						
						//loading all files as single strings
						String[] training_strings = new String[conv_net.neuron_out_count];
						try
						{
							for(int i = 0; i < conv_net.neuron_out_count; i++)
							{
								training_strings[i] = FileHandler.readFromFile(training_paths[i]);
							}
						}
						catch(Exception e)
						{
							System.out.print("An error occurred during file loading. Returning to main menu...\n\n");
							break;
						}
						
						System.out.print("How many characters would you like the random samples to be: ");
						sample_size = scanner.nextInt();
						scanner.nextLine();
						
						System.out.print("How large would you like the batches to be: ");
						batch_size = scanner.nextInt();
						scanner.nextLine();
						
						System.out.print("How many training cycles would you like: ");
						cycle_count = scanner.nextInt();
						scanner.nextLine();
						
						System.out.print("What learning rate would you like to use: ");
						learning_rate = (float)scanner.nextDouble();
						scanner.nextLine();
						
						System.out.print("You are about to commence training with the following parameters:\n"
								+ "\t sample size: "+sample_size
								+"\n\t batch size: "+batch_size
								+"\n\t cycle count: "+cycle_count
								+"\n\t learning rate: "+learning_rate
								+"\nThe traning process might take a while and cannot be interrupted. Are you sure you want to start traning (y/n): ");
						if(scanner.nextLine().equals("y"))
						{
							long start_time = System.nanoTime();
							System.out.println("training started...");
							
							//the actual training
							String[] inputs;
							int[] desired_outputs;
							int dots = 0;
							int dot_count = 50;
							for(int i = 0; i < cycle_count; i++)
							{
								//create inputs and outputs
								inputs = new String[batch_size];
								desired_outputs = new int[batch_size];
								for(int j = 0; j < batch_size; j++)
								{
									desired_outputs[j] = ThreadLocalRandom.current().nextInt(0,conv_net.neuron_out_count);
									inputs[j] = getRandomSubstring(training_strings[desired_outputs[j]],sample_size);
								}
								
								//train batch
								try {
									train(inputs, desired_outputs, learning_rate);
								} catch (Exception e) {
									System.out.println("Something went wrong during the training process. Returning to main menu...\n\n");
								}
								
								//print progress dots
								if((i*dot_count)/cycle_count > dots)
								{
									dots++;
									System.out.print(".");
								}
							}
							
							System.out.println("\nTraining finished in "+((float)(System.nanoTime()-start_time))/10e9+" seconds.");
						}
					}
					resolved = true;
				}
			}
			else if(ans1 ==3)
			{
				resolved = false;
				while(!resolved)
				{
					System.out.print("\nWould you like to"
							+"\n\t(1) recognize the language of a text file or string"
							+ "\n\t(2) run a network test."
							+"\n\t(3) go back.\n:");
					ans2 = scanner.nextInt();
					scanner.nextLine();
					
					if(ans2 == 1)
					{
						System.out.print("\nWould you like to"
								+"\n\t(1) provide a string"
								+"\n\t(2) provide a .txt file");
						ans3 = scanner.nextInt();
						scanner.nextLine();
						
						//load the string of which the language must be recognized
						ans4 = "";
						if(ans3 == 1)
						{
							System.out.print("Enter string: ");
							ans4 = scanner.nextLine();
						}
						else if(ans3 == 2)
						{
							System.out.print("Enter file path: ");
							ans4 = scanner.nextLine();
							try
							{
								ans4 = FileHandler.readFromFile(ans4);
							}
							catch(Exception e)
							{
								System.out.println("Something went wrong when loading the file. Returning to main menu");
							}
						}
						
						//recognize the language
						if(ans2 == 1 || ans2 == 3)
						{
							try {
								System.out.println("I think this piece of text was written in language No."+(1+compute(ans4)));
							} catch (Exception e) {
								System.out.println("Something went wrong during the AI computation. Returning to main menu...\n\n");
							}
						}
					}
					else if(ans2 == 2)
					{
						for(int i = 0; i < conv_net.neuron_out_count; i++)
						{
							System.out.print("Please supply a location for the test data for language "+i+":");
							training_paths[i] = scanner.nextLine();
						}
						
						//loading all files as single strings
						String[] training_strings = new String[conv_net.neuron_out_count];
						try
						{
							for(int i = 0; i < conv_net.neuron_out_count; i++)
							{
								training_strings[i] = FileHandler.readFromFile(training_paths[i]);
							}
						}
						catch(Exception e)
						{
							System.out.print("An error occurred during file loading. Returning to main menu...\n\n");
							break;
						}
						
						System.out.print("How many test runs would you like to do: ");
						test_run_count = scanner.nextInt();
						scanner.nextLine();
						
						System.out.print("How large would you like the samples to be: ");
						sample_size = scanner.nextInt();
						scanner.nextLine();
						
						System.out.print("You have chosen to perform a full network test with "+test_run_count+" runs of sample size "+sample_size
								+". This process might take a while and cannot be interrupted. Are you sure you want to start the test run (y/n): ");
						if(scanner.nextLine().equals("y"))
						{
							System.out.println("Commencing test");
							long start_time = System.nanoTime();
							
							float cost = 0.0f;
							int count = 0;
							int num;
							try
							{
								for(int i = 0; i < test_run_count; i++)
								{
									num = ThreadLocalRandom.current().nextInt(0,conv_net.neuron_out_count);
									cost += computeCost(getRandomSubstring(training_strings[num], sample_size),num);
									count += isCorrect(getRandomSubstring(training_strings[num],sample_size),num) ? 1 : 0;
								}
								System.out.println("Test completed in "+(float)(System.nanoTime()-start_time)/10e9+" seconds. \n"
										+ "The average cost was "+cost/test_run_count
										+"\nOf the "+test_run_count+" test runs, "+count+" yielded a correct result."
												+ "\nThis is "+100*(float)count/test_run_count+"%\n\n");
							}
							catch(Exception e)
							{
								System.out.println("Something went wrong during testing. Returning to main menu...\n\n");
							}
						}
						
					}
					else if(ans2 == 3)
					{
						resolved = true;
					}
				}
			}
		}
	}
}




