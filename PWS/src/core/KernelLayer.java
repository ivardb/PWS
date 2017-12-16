package core;

import exceptions.DimensionException;

//work in progress
public class KernelLayer {
	
	public int dimension;
	
	public int neuron_in_count;
	public int neuron_out_count;
	
	private int[] kernel_lengths;
	public Tensor[][] kernels;
	
	//kernels are generated randomly
	public KernelLayer(int[] kernel_lengths, int neuron_in_count, int neuron_out_count, float lower, float upper) throws DimensionException
	{
		this.kernel_lengths = kernel_lengths;
		this.dimension = kernel_lengths.length;
		
		this.neuron_in_count = neuron_in_count;
		this.neuron_out_count = neuron_out_count;
		
		//generate random kernels
		this.kernels = new Tensor[this.neuron_in_count][];
		for(int i = 0; i < this.neuron_in_count; i++)
		{
			this.kernels[i] = new Tensor[this.neuron_out_count];
			for(int j = 0; j <  this.neuron_out_count; j++)
			{
				this.kernels[i][j] = new Tensor(lower, upper, this.kernel_lengths);
			}
		}
	}
	
	//kernels are given as parameters
	public KernelLayer(Tensor[][] kernels) throws Exception
	{
		if(kernels.length == 0)
			throw new Exception("Cannot create kernel layers without input.");
		if(kernels[0].length == 0)
			throw new Exception("Cannot create kernel layers without output.");
		
		
		this.dimension = kernels[0][0].dimension;
		this.kernel_lengths = kernels[0][0].lengths;
		this.neuron_in_count = kernels.length;
		this.neuron_out_count = kernels[0].length;
		
		//check whether all kernels are of the same size and if there are enough of them
		for(int i = 0; i < kernels.length; i++)
		{
			if(kernels[i].length != this.neuron_out_count)
				throw new Exception("Not all kernels are specified. Only fully connected layers are possible.");
			
			
			for(int j = 0; j < kernels[i].length; j++)
			{
				if(kernels[i][j].dimension != this.dimension)
					throw new DimensionException("Not all tensors are of the same dimension.");
				else if(!(kernels[i][j].lengths.equals(this.kernel_lengths) || this.dimension == 0))
					throw new DimensionException("Not all kernels are of the same size.");
			}
		}
		
		//all checks have been passed; copy the data
		this.kernels = kernels;
	}
	
	public Tensor[] process(Tensor[] input_data) throws Exception
	{
		//validate input data
		if(input_data.length != this.neuron_in_count)
			throw new Exception("Not enough input neurons");
		
		for(int i = 0; i < this.neuron_in_count; i++)
		{
			if(input_data[i].dimension != this.dimension)
				throw new DimensionException("input tensor is of the wrong dimension");
		}
	
		//all checks have been passed, start processing
		Tensor[] output_data = new Tensor[this.neuron_out_count];
		Tensor[] temp_tensors = new Tensor[this.neuron_in_count];
		for(int i = 0; i < this.neuron_out_count; i++)
		{
			temp_tensors = new Tensor[this.neuron_in_count];
			for(int j = 0; j < this.neuron_in_count; j++)
			{				
				temp_tensors[j] = new Tensor();
				temp_tensors[j].become(input_data[j]);
				temp_tensors[j].convolveWith(this.kernels[j][i]);
			}
			output_data[i] = new Tensor(temp_tensors);
			output_data[i].flatten();
		}
		return output_data;
	}
	
}
