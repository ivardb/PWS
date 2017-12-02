package UI;

import core.*;
import exceptions.DimensionException;
import exceptions.NumericalException;

public class main {

	public static void main(String[] args) 
	{
		//test code:
		
		/*try 
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
		}*/
		
		try 
		{
			//create neuron layers
			NeuronLayer neuron_layer1 = new NeuronLayer(3, new int[] {}, new int[] {});
			NeuronLayer neuron_layer2 = new NeuronLayer(2, new int[] {}, new int[] {});
			NeuronLayer neuron_layer3 = new NeuronLayer(2, new int[] {}, new int[] {});
			
			//create kernel layers
			Tensor[][] layer1_weights = new Tensor[][] {
				new Tensor[] {new Tensor(new float[] {1}), new Tensor(new float[] {1})},
				new Tensor[] {new Tensor(new float[] {1}), new Tensor(new float[] {1})},
				new Tensor[] {new Tensor(new float[] {1}), new Tensor(new float[] {1})}
			};
			KernelLayer kernel_layer1 = new KernelLayer(layer1_weights);
			
			
			Tensor[][] layer2_weights = new Tensor[][] {
				new Tensor[] {new Tensor(new float[] {1}), new Tensor(new float[] {1})},
				new Tensor[] {new Tensor(new float[] {1}), new Tensor(new float[] {1})}
			};
			KernelLayer kernel_layer2 = new KernelLayer(layer2_weights);
			
			//create a convolutional neural net
			ConvolutionalNeuralNetwork conv_net = new ConvolutionalNeuralNetwork(neuron_layer1);
			
			//add all the layers to the net
			conv_net.addKernelLayer(kernel_layer1);
			conv_net.addNeuronLayer(neuron_layer2);
			conv_net.addKernelLayer(kernel_layer2);
			conv_net.addNeuronLayer(neuron_layer3);
			
			Tensor[] input = new Tensor[] {
					new Tensor(new float[] {7.0f}),
					new Tensor(new float[] {2.0f}),
					new Tensor(new float[] {1.0f})
			};
			
			System.out.println(conv_net.process(input)[0].getSerializedData()[0]);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
