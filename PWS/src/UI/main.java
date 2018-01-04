package UI;

import core.*;
import exceptions.DimensionException;
import exceptions.NumericalException;

public class main {

	public static void main(String[] args) 
	{
		//first multidimensional test
		try
		{
			NeuronLayer neuron_layer1 = new NeuronLayer(3, new int[] {2,2}, new int[] {1,1});
			NeuronLayer neuron_layer2 = new NeuronLayer(2, new int[] {2,2}, new int[] {2,2});
			NeuronLayer neuron_layer3 = new NeuronLayer(1, new int[] {}, new int[] {});
			
			Tensor[][] layer1_weights = new Tensor[][] {
				new Tensor[] {new Tensor(new float[] {1,1,1,1},2,2), new Tensor(new float[] {1,1,1,1},2,2)},
				new Tensor[] {new Tensor(new float[] {1,1,1,1},2,2), new Tensor(new float[] {1,1,1,1},2,2)},
				new Tensor[] {new Tensor(new float[] {1,1,1,1},2,2), new Tensor(new float[] {1,1,1,1},2,2)}
			};
			KernelLayer kernel_layer1 = new KernelLayer(layer1_weights);
			
			Tensor[][] layer2_weights = new Tensor[][] {
				new Tensor[] {new Tensor(new float[] {1})},
				new Tensor[] {new Tensor(new float[] {1})}
			};
			KernelLayer kernel_layer2 = new KernelLayer(layer2_weights);
			
			ConvolutionalNeuralNetwork conv_net = new ConvolutionalNeuralNetwork(neuron_layer1);
			conv_net.addKernelLayer(kernel_layer1);
			conv_net.addNeuronLayer(neuron_layer2);
			conv_net.addKernelLayer(kernel_layer2);
			conv_net.addNeuronLayer(neuron_layer3);
			
			Tensor[] input = new Tensor[] {
					new Tensor(new float[] {1,1,1,1},2,2),
					new Tensor(new float[] {1,1,1,1},2,2),
					new Tensor(new float[] {1,1,1,1},2,2)
			};
			conv_net.process(input);
			System.out.println(conv_net.neuron_layers.get(1).neuron_data[0].getSerializedData()[0]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//test code which determines the language (using the custom testing class)
		/*try
		{
			//create neuron layers
			NeuronLayer neuron_layer1 = new NeuronLayer(26, new int[] {}, new int[] {});
			NeuronLayer neuron_layer2 = new NeuronLayer(10, new int[] {}, new int[] {});
			NeuronLayer neuron_layer3 = new NeuronLayer(4, new int[] {}, new int[] {});
			
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
				layer2_weights[i] = new Tensor[4];
				for(int j = 0; j < 4; j++)
				{
					layer2_weights[i][j] = new Tensor(-3.0f, 3.0f);
				}
			}
			KernelLayer kernel_layer2 = new KernelLayer(layer2_weights);
			
			//create a convolutional neural net
			ConvolutionalNeuralNetwork conv_net = new ConvolutionalNeuralNetwork(neuron_layer1);
			
			//add all the layers to the net
			conv_net.addKernelLayer(kernel_layer1);
			conv_net.addNeuronLayer(neuron_layer2);
			conv_net.addKernelLayer(kernel_layer2);
			conv_net.addNeuronLayer(neuron_layer3);
			
			ConvolutionalNeuralNetwork.fromString(ConvolutionalNeuralNetwork.toString(conv_net));
			
			//run an initial performance test on the network with randomized weights
			/*Tensor[][] pair;
			float cost = 0.0f;
			for(int i = 0; i < 100; i++)
			{
				pair = Testing.getRandomClassifiedPair(1000, false);
				conv_net.process(pair[0]);
				cost += Testing.computeCost(conv_net.neuron_layers.get(2).neuron_data, pair[1]);
				//System.out.println(Testing.computeCost(conv_net.neuron_layers.get(2).neuron_data, pair[1]));
			}
			System.out.println(cost/100);
			
			//train with 100 examples
			for(int i = 0; i < 10000; i++)
			{
				pair = Testing.getRandomClassifiedPair(1000, false);
				conv_net.backpropagate(pair[0], pair[1], 0.01f);
			}
			
			//run another performance test on the trained network
			cost = 0.0f;
			for(int i = 0; i < 100; i++)
			{
				pair = Testing.getRandomClassifiedPair(1000, false);
				conv_net.process(pair[0]);
				cost += Testing.computeCost(conv_net.neuron_layers.get(2).neuron_data, pair[1]);
				//System.out.println(Testing.computeCost(conv_net.neuron_layers.get(2).neuron_data, pair[1]));
			}
			System.out.println(cost/100);
			
			int count = 0;
			int index;
			float val;
			for(int i = 0; i < 10000; i++)
			{
				val = 0;
				index = 0;
				pair = Testing.getRandomClassifiedPair(1000, false);
				conv_net.process(pair[0]);
				for(int j = 0; j < 4; j++)
				{
					if(conv_net.neuron_layers.get(2).neuron_data[j].getSerializedData()[0] > val)
					{
						val = conv_net.neuron_layers.get(2).neuron_data[j].getSerializedData()[0];
						index = j;
					}
				}
				if(pair[1][index].getSerializedData()[0] == 10)
				{
					count++;
				}
			}
			System.out.println(count);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}*/
	}

}
