package UI;

import core.*;
import exceptions.DimensionException;
import exceptions.NumericalException;

public class main {

	public static void main(String[] args) 
	{
		//test code:
		
		try 
		{
			NeuronLayer layer = new NeuronLayer(1, new int[] {4,4}, new int[] {2,2});
			Tensor[] input = new Tensor[] {new Tensor(new float[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16}, 4, 4)};
			layer.process(input);
			for(float value : layer.neuron_data[0].getSerializedData())
			{
				System.out.println(value);
			}
		} 
		catch (DimensionException | NumericalException e) 
		{
			e.printStackTrace();
		}
		
		/*try 
		{
			//create neuron layers
			NeuronLayer neuron_layer1 = new NeuronLayer(3, new int[] {16,16}, new int[] {2,2});
			NeuronLayer neuron_layer2 = new NeuronLayer(5, new int[] {8,8}, new int[] {2,2});
			NeuronLayer neuron_layer3 = new NeuronLayer(5, new int[] {4,4}, new int[] {2,2});
			NeuronLayer neuron_layer4 = new NeuronLayer(5, new int[] {2,2}, new int[] {2,2});
			NeuronLayer neuron_layer5 = new NeuronLayer(5, new int[] {1,1}, new int[] {1,1});
			NeuronLayer neuron_layer6 = new NeuronLayer(9, new int[] {1,1}, new int[] {1,1});
			NeuronLayer neuron_layer7 = new NeuronLayer(9, new int[] {1,1}, new int[] {1,1});
			
			//create kernel layers
			KernelLayer kernel_layer1 = new KernelLayer(3,5);
			KernelLayer kernel_layer2 = new KernelLayer(5,5);
			KernelLayer kernel_layer3 = new KernelLayer(5,5);
			KernelLayer kernel_layer4 = new KernelLayer(5,5);
			KernelLayer kernel_layer5 = new KernelLayer(5,9);
			KernelLayer kernel_layer6 = new KernelLayer(9,9);
			
			
			//create a convolutional neural net
			ConvolutionalNeuralNetwork conv_net = new ConvolutionalNeuralNetwork(neuron_layer1);
			
			//add all the layers to the net
			conv_net.addKernelLayer(kernel_layer1);
			conv_net.addNeuronLayer(neuron_layer2);
			conv_net.addKernelLayer(kernel_layer2);
			conv_net.addNeuronLayer(neuron_layer3);
			conv_net.addKernelLayer(kernel_layer3);
			conv_net.addNeuronLayer(neuron_layer4);
			conv_net.addKernelLayer(kernel_layer4);
			conv_net.addNeuronLayer(neuron_layer5);
			conv_net.addKernelLayer(kernel_layer5);
			conv_net.addNeuronLayer(neuron_layer6);
			conv_net.addKernelLayer(kernel_layer6);
			conv_net.addNeuronLayer(neuron_layer7);
		} 
		catch (DimensionException | NumericalException e) 
		{
			e.printStackTrace();
		}*/
	}

}
