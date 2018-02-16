package core;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import exceptions.DimensionException;
import exceptions.NumericalException;

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
			//the last layer is already a neuron layer
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
	
	public void computeLastLayerDeltas(Tensor[][] inputs, Tensor[][] desired_outputs) throws Exception
	{
		if(inputs.length != desired_outputs.length)
		{
			throw new Exception("Need the same number of inputs as desired outputs!");
		}
		
		Tensor[] differences = new Tensor[this.neuron_out_count];
		for(int i = 0; i < this.neuron_out_count; i++) 
		{
			differences[i] = new Tensor(this.output_lengths);
		}
		
		for(int i = 0; i < inputs.length; i++)
		{
			//set neurons to the correct values
			this.process(inputs[i]);
			
			for(int j = 0; j < this.neuron_out_count; j++) 
			{
				differences[j] = Tensor.add(differences[j], Tensor.Hadamard(Tensor.subtract(this.neuron_layers.get(this.neuron_layer_count-1).neuron_data[j], desired_outputs[i][j]),this.neuron_layers.get(this.neuron_layer_count-1).relu_derivative[j]));
			}
		}
		
		for(int i = 0; i < this.neuron_out_count; i++) 
		{
			this.neuron_layers.get(this.neuron_layer_count-1).delta_tensors[i] = Tensor.scalarMult(1.0f/inputs.length, differences[i]);
		}
	}
	
	//generalized backpropagation algorithm
	public void backpropagate(Tensor[][] inputs, Tensor[][] desired_outputs, float learning_rate) throws Exception
	{
		this.computeLastLayerDeltas(inputs, desired_outputs);
		
		//computing all other delta tensors
		Tensor sum;
		Tensor tmp = new Tensor();
		for(int i = this.neuron_layer_count-2; i >= 0; i--)
		{
			for(int j = 0; j < this.neuron_layers.get(i).neuron_count; j++)
			{
				sum = new Tensor(this.neuron_layers.get(i).output_lengths);
				for(int k = 0; k < this.neuron_layers.get(i+1).neuron_count; k++)
				{
					tmp = Tensor.invertMaxPooling(this.neuron_layers.get(i+1).delta_tensors[k], 
							this.neuron_layers.get(i+1).propagation_wideners[k], 
							this.neuron_layers.get(i+1).pooling_lengths);
					tmp.convolveWith(Tensor.rot180(this.kernel_layers.get(i).kernels[j][k]));
					sum = Tensor.add(sum, tmp);
				}
				this.neuron_layers.get(i).delta_tensors[j] = Tensor.Hadamard(sum, this.neuron_layers.get(i).relu_derivative[j]);
			}
		}
		
		//update weights
		Tensor derivative;
		for(int i = 0; i < this.kernel_layer_count; i++)
		{
			for(int j = 0; j < this.kernel_layers.get(i).neuron_in_count; j++)
			{
				for(int k = 0; k < this.kernel_layers.get(i).neuron_out_count; k++)
				{
					//compute the derivative of the error with respect to the selected kernel
					derivative = Tensor.cap(Tensor.invertMaxPooling(this.neuron_layers.get(i+1).delta_tensors[k],
							this.neuron_layers.get(i+1).propagation_wideners[k],
							this.neuron_layers.get(i+1).pooling_lengths)
							.convolveWith(this.neuron_layers.get(i).neuron_data[j], this.kernel_layers.get(i).kernel_lengths),-10.0f,10.0f);
					//update the kernel
					this.kernel_layers.get(i).kernels[j][k] = Tensor.add(this.kernel_layers.get(i).kernels[j][k], Tensor.scalarMult(-learning_rate, derivative)); 
				}
			}
		}
	}

	public static String toString(ConvolutionalNeuralNetwork net)
	{
		String str = "";
		
		for(int i = 0; i < net.kernel_layer_count; i++)
		{
			//appending a '0' to the front in order to be able to distinguish the header ending from zero-dimensional kernels later on
			str += "0";
			for(int j = 0 ; j < net.kernel_layers.get(i).dimension; j++)
			{
				str += net.kernel_layers.get(i).kernel_lengths[j]+",";
			}
			str += "\n";
		}
		
		str += "\n";
		
		/*first, a header specifying the structure. 
		Every line represents another neuron layer. 
		In the comma separated list, we first have the amount of neurons in that layer and then the dimension of the input.
		After that, the input lengths and pooling lengths follow.*/
		for(int i = 0; i < net.neuron_layer_count; i++)
		{
			str += net.neuron_layers.get(i).neuron_count+",";
			for(int j = 0; j <  net.neuron_layers.get(i).dimension_in; j++)
			{
				str += net.neuron_layers.get(i).input_lengths[j]+",";
			}
			for(int j = 0; j < net.neuron_layers.get(i).dimension_in; j++)
			{
				str += net.neuron_layers.get(i).pooling_lengths[j]+",";
			}
			str += "\n";
		}
		//now, an empty line signifies the end of the first header
		str += "\n";
		
		//adding all the weights to the string
		float[] values; 
		for(int i = 0; i < net.kernel_layer_count; i++)
		{	
			for(int j = 0; j < net.neuron_layers.get(i).neuron_count; j++)
			{
				for(int k = 0; k < net.neuron_layers.get(i+1).neuron_count; k++)
				{
					values = net.kernel_layers.get(i).kernels[j][k].getSerializedData();
					for(int l = 0; l < values.length; l++)
					{
						str += Float.toString(values[l]);
						str += ",";
					}
					str += "\n";
				}
				
			}	
		}
		return str;
	}
	
	public static ConvolutionalNeuralNetwork fromString(String parameters) throws Exception
	{
		//splitting the string into lines
		String[] lines = parameters.split("\n");
		
		//extracting kernel size data from the first header
		int header_ending_index = 0;
		while(!lines[header_ending_index].isEmpty())
			header_ending_index++;
		
		int[][] kernel_layer_lengths = new int[header_ending_index][];
		for(int i = 0 ; i <  kernel_layer_lengths.length; i++)
		{
			kernel_layer_lengths[i] = lines[i].equals("0") ? new int[] {} : Arrays.asList(lines[i].split(",")).stream().mapToInt(Integer::parseInt).toArray();
		}
		
		//determining the end of second header
		int second_header_ending_index = header_ending_index + 1;
		while(!lines[second_header_ending_index].isEmpty())
			second_header_ending_index++;
		
		//loading all neuron layers based on the second header
		NeuronLayer[] neuron_layers = new NeuronLayer[second_header_ending_index-header_ending_index-1];
		int[] values, input_lengths, pooling_lengths;
		for(int i = header_ending_index+1; i < second_header_ending_index ; i++)
		{
			//turning the comma separated list of strings into an array of integers with a lambda expression
			values = Arrays.asList(lines[i].split(",")).stream().mapToInt(Integer::parseInt).toArray();
			
			//taking out the input lengths and pooling lengths
			input_lengths = new int[(values.length-1)/2];
			pooling_lengths = new int[(values.length-1)/2];
			for(int j = 0; j < (values.length-1)/2; j++)
			{
				input_lengths[j] = values[j+1];
				pooling_lengths[j] = values[j+(values.length-1)/2+1];
			}
			
			neuron_layers[i-header_ending_index-1] = new NeuronLayer(values[0], input_lengths, pooling_lengths);
		}
		
		//the kernel layers are loaded
		KernelLayer[] kernel_layers = new KernelLayer[neuron_layers.length-1];
		int line_index = second_header_ending_index+1;
		Tensor[][] layer_data;
		int[] kernel_lengths;
		float[] line_data;
		String[] strings;
		for(int i = 0; i <  kernel_layers.length; i++)
		{
			layer_data = new Tensor[neuron_layers[i].neuron_count][];
			for(int j = 0; j < neuron_layers[i].neuron_count; j++)
			{
				layer_data[j] = new Tensor[neuron_layers[i+1].neuron_count];
				for(int k = 0; k < neuron_layers[i+1].neuron_count; k++)
				{
					//extract kernel data
					strings = lines[line_index].split(",");
					line_data = new float[strings.length];
					for(int l = 0; l < line_data.length; l++)
					{
						line_data[l] = Float.valueOf(strings[l]);
					}
					
					//create kernel
					layer_data[j][k] = new Tensor(line_data, kernel_layer_lengths[i]);
					
					line_index++;
				}
			}
			//create kernel layer
			kernel_layers[i] = new KernelLayer(layer_data);
		}
		
		//create the network
		ConvolutionalNeuralNetwork net = new ConvolutionalNeuralNetwork(neuron_layers[0]);
		for(int i = 0; i < kernel_layers.length; i++)
		{
			net.addKernelLayer(kernel_layers[i]);
			net.addNeuronLayer(neuron_layers[i+1]);
		}
		System.out.println(net.neuron_layer_count+", "+neuron_layers.length);
		
		return net;
	}

}




