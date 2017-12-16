package core;

import exceptions.DimensionException;
import exceptions.NumericalException;

public class NeuronLayer {

	//the amount of neurons in the layer
	public int neuron_count;
	
	//the dimension of the Tensors the neurons expect as input
	public int dimension_in;
	
	//the output dimension. 
	public int dimension_out;
	
	//the exact size of the expected input tensors
	public int[] input_lengths;
	
	//the exact pooling size
	public int[] pooling_lengths;
	
	//the exact output size
	public int[] output_lengths;
	
	public Tensor[] neuron_data;
	
	//used in backpropagation
	public Tensor[] propagation_wideners;
	public Tensor[] delta_tensors;
	public Tensor[] relu_derivative;
	
	//make sure all lengths are powers of 2, or an exception will be thrown
	public NeuronLayer(int neuron_count, int[] input_lengths, int[] pooling_lengths) throws DimensionException, NumericalException
	{
		this.neuron_count = neuron_count;
		this.input_lengths = input_lengths;
		this.dimension_in = input_lengths.length;
		this.dimension_out = this.dimension_in;
		
		this.pooling_lengths = pooling_lengths;
		if(pooling_lengths.length != this.dimension_in)
		{
			throw new DimensionException("the pooling kernel must have the same dimension as the input Tensor");
		}
		for(int i = 0; i < this.pooling_lengths.length; i++)
		{
			//length must be positive and after a bitwise OR with itself minus one it must equal 0. 
			//this is equivalent to stating that length must be an integer power of 2.
			if(this.pooling_lengths[i] <= 0 && (this.pooling_lengths[i] & (this.pooling_lengths[i]-1)) != 0)
			{
				throw new NumericalException("all lengths of the pooling kernel must be integer powers of 2");
			}
			else if(this.pooling_lengths[i] > this.input_lengths[i])
			{
				throw new NumericalException("the kernel cannot be larger than the input");
			}
			else if(this.pooling_lengths[i] == this.input_lengths[i])
			{
				dimension_out--;
			}
		}
		
		this.output_lengths = new int[this.dimension_out];
		for(int i = 0; i < this.dimension_out; i++)
		{
			output_lengths[i] = input_lengths[i] / pooling_lengths[i];
		}
		
		this.neuron_data = new Tensor[neuron_count];
		this.propagation_wideners = new Tensor[neuron_count];
		this.delta_tensors = new Tensor[neuron_count];
		this.relu_derivative = new Tensor[neuron_count];
	}
	
	//performs ReLu and pooling
	public void process(Tensor[] input) throws DimensionException
	{
		//load all values (actually copy them)
		if(input.length != this.neuron_count)
		{
			throw new DimensionException("wrong number of input tensors");
		}
		for(int i = 0; i < input.length; i++)
		{
			if(input[i].lengths.equals(this.input_lengths))
			{
				throw new DimensionException("input tensor is of the wrong size");
			}
			neuron_data[i] = new Tensor(this.input_lengths);
			neuron_data[i].become(input[i]);
		}
		
		//perform ReLu and pooling. Try to reduce the dimension
		for(int i = 0; i < this.neuron_count; i++)
		{
			this.relu_derivative[i] = neuron_data[i].ReLu();
			this.propagation_wideners[i] = neuron_data[i].maxPool(pooling_lengths);
			neuron_data[i].reduceDimension();
		}
	}
}
	