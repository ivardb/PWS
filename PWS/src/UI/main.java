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
			Tensor t1 = new Tensor(new float[] {1,2,3,4},2,2);
			Tensor t2 = t1.maxPool(2,2);
			Tensor t3 = Tensor.invertMaxPooling(t1, t2, 2, 2);
			System.out.println(t3.getSerializedData()[3]);
		} 
		catch (Exception e) 
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
			
			//System.out.println(conv_net.process(input)[0].getSerializedData()[0]);
			conv_net.backpropagate(input, new Tensor[] {new Tensor(new float[] {1.5f}), new Tensor(new float[] {1.5f})});
			conv_net.dumpData("convnetdata.txt");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
