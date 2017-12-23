package core;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//work in progress
public class ConvolutionalNeuralNetwork {

	//some error codes
	public static final int CONV_NET_OK = 0;
	public static final int APPEND_ERROR = 1;
	public static final int DELETE_ERROR = 2;
	public static final int SIZE_ERROR = 3;
	public static final int DIMENSION_ERROR = 4;
	
	public ArrayList<NeuronLayer> neuron_layers;
	public ArrayList<KernelLayer> kernel_layers; 
	
	public int neuron_layer_count;
	private int kernel_layer_count;
	
	private int neuron_in_count;
	private int neuron_out_count;
	
	private int dimension_in;
	private int dimension_out;
	
	private int[] output_lengths;
	
	public ConvolutionalNeuralNetwork(NeuronLayer input_layer)
	{
		this.neuron_layers = new ArrayList<NeuronLayer>();
		this.kernel_layers = new ArrayList<KernelLayer>();
		
		this.neuron_layers.add(input_layer);
		
		this.neuron_layer_count = 1;
		this.kernel_layer_count = 0;
		
		this.dimension_in = input_layer.dimension_in;
		this.dimension_out = input_layer.dimension_out;
		
		this.output_lengths = input_layer.output_lengths;
		
		this.neuron_in_count = input_layer.neuron_count;
		this.neuron_out_count = input_layer.neuron_count;
	}
	
	//neuron layers and kernel layers (connections) have to added in alternating order, beginning and ending with a neuron layer
	public int addNeuronLayer(NeuronLayer layer)
	{
		//check if the new layer 'fits' on the network
		if(neuron_layer_count - kernel_layer_count >= 1)
		{
			//the last layer is already a kernel layer
			return APPEND_ERROR;
		}
		else if(layer.dimension_in != this.dimension_out)
		{
			//the output of the previous layer is of a different dimension than the input of the layer we want to add
			return DIMENSION_ERROR;
		}
		else if(!(Arrays.equals(this.output_lengths, layer.input_lengths) || this.dimension_in == 0) || this.neuron_out_count != layer.neuron_count)
		{
			//the output of the previous layer is of a different size than the input of the layer we want to add
			return SIZE_ERROR;
		}
		else 
		{
			this.neuron_out_count = layer.neuron_count;
			this.output_lengths = layer.output_lengths;
			
			this.dimension_out = layer.dimension_out;
			
			this.neuron_layer_count++;
			this.neuron_layers.add(layer);			
			return CONV_NET_OK;
		}
	}
	
	public int addKernelLayer(KernelLayer layer)
	{
		if(this.kernel_layer_count != this.neuron_layer_count-1)
		{
			return APPEND_ERROR;
		}
		else if(this.neuron_out_count != layer.neuron_in_count)
		{
			return SIZE_ERROR;
		}
		else if(layer.dimension != this.dimension_out)
		{
			return DIMENSION_ERROR;
		}
		else
		{
			this.neuron_out_count = layer.neuron_out_count;
			
			this.kernel_layer_count++;
			this.kernel_layers.add(layer);			
			return CONV_NET_OK;
		}
	}
	
	public int removeLastLayer()
	{
		//since we start with a neuron layer, the last layer must be a kernel layer
		if(this.kernel_layer_count == this.neuron_layer_count)
		{
			this.neuron_out_count = this.neuron_layers.get(this.neuron_layer_count-1).neuron_count;
			
			this.kernel_layers.remove(this.kernel_layer_count-1);
			this.kernel_layer_count--;			
			return CONV_NET_OK;
		}
		//delete a neuron layer
		else 
		{
			//only the first (undeletable) neuron layer is left, nothing can be deleted
			if(this.neuron_layer_count == 1)
			{
				return DELETE_ERROR;
			}
			
			this.neuron_out_count = this.kernel_layers.get(this.kernel_layer_count-1).neuron_out_count;
			this.output_lengths = this.neuron_layers.get(this.neuron_layer_count -1).input_lengths;
			
			this.neuron_layers.remove(this.neuron_layer_count-1);
			this.neuron_layer_count--;
			this.dimension_out = this.neuron_layers.get(this.neuron_layer_count - 1).dimension_out;
			return CONV_NET_OK;
		}
	}
	
	//process some given input data
	public Tensor[] process(Tensor[] input_data) throws Exception
	{
		this.neuron_layers.get(0).process(input_data);
		for(int i = 0; i < this.kernel_layer_count; i++)
		{
			Tensor[] prev_layer_data = this.neuron_layers.get(i).neuron_data;
			prev_layer_data = this.kernel_layers.get(i).process(prev_layer_data);
			this.neuron_layers.get(i+1).process(prev_layer_data);
		}
		return this.neuron_layers.get(this.neuron_layer_count-1).neuron_data;
	}
	
	//generalized backpropagation algorithm
	public void backpropagate(Tensor[] input, Tensor[] desired_output, float learning_rate) throws Exception
	{
		//fill all the neurons with their output values.
		this.process(input);
		
		//compute the delta tensors for the output neurons
		for(int i = 0; i < this.neuron_out_count; i++)
		{
			Tensor tmp = Tensor.invertMaxPooling(this.neuron_layers.get(this.neuron_layer_count-1).neuron_data[i], 
												this.neuron_layers.get(this.neuron_layer_count-1).propagation_wideners[i], 
												this.neuron_layers.get(this.neuron_layer_count-1).pooling_lengths);
			this.neuron_layers.get(this.neuron_layer_count-1).delta_tensors[i] = Tensor.Hadamard(this.neuron_layers.get(this.neuron_layer_count-1).relu_derivative[i], Tensor.subtract(tmp, desired_output[i]));
		}
		
		//compute the other delta tensors
		Tensor sum, tmp;
		for(int i = this.neuron_layer_count-2; i >= 0; i--)
		{
			for(int j = 0; j < this.neuron_layers.get(i).neuron_count; j++)
			{
				sum = new Tensor(this.neuron_layers.get(i).input_lengths);
				for(int k = 0; k < this.neuron_layers.get(i+1).neuron_count; k++)
				{
					tmp = Tensor.invertMaxPooling(this.neuron_layers.get(i+1).delta_tensors[k],
													this.neuron_layers.get(i+1).propagation_wideners[k],
													this.neuron_layers.get(i+1).pooling_lengths);
					tmp.convolveWith(Tensor.rot180(this.kernel_layers.get(i).kernels[j][k]));
					sum = Tensor.add(sum, tmp);
	
				}
				this.neuron_layers.get(i).delta_tensors[j] = Tensor.Hadamard(this.neuron_layers.get(i).relu_derivative[j], sum);
			}
		}
		
		//update the weights
		Tensor derivative;
		for(int i = 0; i < this.kernel_layer_count; i++)
		{
			for(int j = 0; j < this.kernel_layers.get(i).neuron_in_count; j++)
			{
				for(int k = 0; k < this.kernel_layers.get(i).neuron_out_count; k++)
				{
					derivative = Tensor.Hadamard(this.neuron_layers.get(i).neuron_data[j], this.neuron_layers.get(i+1).delta_tensors[k]);
					this.kernel_layers.get(i).kernels[j][k] = Tensor.add(this.kernel_layers.get(i).kernels[j][k],Tensor.scalarMult(-learning_rate, derivative));
				}
			}
		}
	}

	public String toString()
	{
		String str = "";

		/*first, a header specifying the structure. 
		Every line represents another neuron layer. 
		In the comma separated list, we first have the amount of neurons in that layer and then the dimension of the input.
		After that, the input lengths and pooling lengths follow.*/
		for(int i = 0; i < this.neuron_layer_count; i++)
		{
			str += this.neuron_layers.get(i).neuron_count+","+this.neuron_layers.get(i).dimension_in+",";
			for(int j = 0; j < this.neuron_layers.get(i).dimension_in; j++)
			{
				str += this.neuron_layers.get(i).input_lengths[j]+",";
			}
			for(int j = 0; j < this.neuron_layers.get(i).dimension_in; j++)
			{
				str += this.neuron_layers.get(i).pooling_lengths[j];
			}
			str += "\n";
		}
		
		//now, an empty line signifies the end of the header
		str += "\n";
		
		return str;
	}
}




