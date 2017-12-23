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
				new Tensor[] {new Tensor(new float[] {1}), new Tensor(new float[] {2})},
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
					new Tensor(new float[] {1.0f}),
					new Tensor(new float[] {1.0f}),
					new Tensor(new float[] {1.0f})
			};
			
			conv_net.backpropagate(input, new Tensor[] {new Tensor(new float[] {0.8f}), new Tensor(new float[] {1.6f})}, 0.1f);
			
			/*for(NeuronLayer nl : conv_net.neuron_layers)
			{
				for(int j = 0; j < nl.neuron_count; j++)
				{
					System.out.print("("+nl.neuron_data[j].getSerializedData()[0]+", "+nl.delta_tensors[j].getSerializedData()[0]+"), ");
				}
				System.out.println("");
			}*/
			
			for(KernelLayer kn : conv_net.kernel_layers)
			{
				for(int i = 0; i < kn.neuron_in_count; i++)
				{
					for(int j = 0; j < kn.neuron_out_count; j++)
					{
						System.out.print("("+i+", "+j+", "+kn.kernels[i][j].getSerializedData()[0]+")");
					}
					System.out.println("");
				}
				System.out.println("");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
