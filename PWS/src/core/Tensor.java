package core;

import java.util.concurrent.ThreadLocalRandom;
import exceptions.*;

public class Tensor {

	public int dimension; //the dimension of the Tensor
	public int[] lengths; //the different 'lengths' of the tensor
	
	private int total_data_length; //the total amount of scalars contained in the tensor
	private int[] index_products;	//some helper variables used in the serialization of indices
	private float[] data; //the actual, serialized, tensor data
	
	public Tensor(int... lengths)
	{
		this.dimension = lengths.length;
		this.lengths = lengths;
		
		//compute the total data length using a multidimensional volume calculation
		//meanwhile, initialize the index_products helper variables
		this.total_data_length = 1;
		this.index_products = new int[this.dimension];
		for(int i = 0; i < this.dimension; i++)
		{
			this.index_products[i] = this.total_data_length;
			this.total_data_length *= this.lengths[i];
		}
		
		//initialize the data array
		this.data = new float[this.total_data_length];
	}
	
	public Tensor(float[] data, int... lengths) throws DimensionException
	{
		this(lengths);
		
		if(data.length != this.total_data_length)
		{
			throw new IndexOutOfBoundsException("data is not of the right size");
		}
		this.data = data;
	}
	
	//create a tensor of given dimensions and fill it with random data in the given range
	public Tensor(float lower, float upper, int... lengths)
	{
		this(lengths);
		for(int i = 0; i < this.total_data_length; i++)
		{
			this.data[i] = ThreadLocalRandom.current().nextFloat() * (upper - lower) + lower;
		}
	}
	
	//merges an array of same-size tensors into one large tensor
	public Tensor(Tensor[] tensors) throws DimensionException
	{
		//create a new, temporary tensor of the correct dimensions
		int[] lengths = new int[tensors[0].dimension + 1];
		for(int i = 0; i < tensors[0].dimension; i++)
		{
			if(tensors[i].dimension != tensors[0].dimension)
			{
				throw new DimensionException("Not all tensors are of the same dimension");
			}
			lengths[i] = tensors[0].lengths[i];
		}
		lengths[tensors[0].dimension] = tensors.length;
		Tensor tmp = new Tensor(lengths);
		
		//put in all the data
		for(int i = 0; i < this.total_data_length; i++)
		{
			for(int j = 0; j < tensors.length; j++)
			{
				tmp.data[tensors.length * i + j] = tensors[j].data[i];
			}
		}
		
		this.become(tmp);
	}
	
	public void become(Tensor tensor)
	{
		this.dimension = tensor.dimension;
		this.lengths = tensor.lengths;
		this.total_data_length = tensor.total_data_length;
		this.index_products = tensor.index_products;
		this.data = tensor.data;
	}

	public float get(int... indices) throws DimensionException
	{		
		if(indices.length != this.dimension)
		{
			throw new DimensionException("the amount of indices must be equal to the dimension");
		}
		
		return this.data[this.getSerializedDataIndex(indices)];
	}

	public float[] getSerializedData()
	{
		return this.data;
	}
	
	public void set(float value, int... indices) throws DimensionException
	{
		if(indices.length != this.dimension)
		{
			throw new DimensionException("the amount of indices must be equal to the dimension");
		}
		this.data[this.getSerializedDataIndex(indices)] = value;
	}

	public void flatten()
	{
		//create a new tensor, with the last dimension 'missing'
		int[] new_lengths = new int[this.dimension - 1];
		for(int i = 0; i < this.dimension - 1; i++)
		{
			new_lengths[i] = this.lengths[i];
		}
		Tensor tmp = new Tensor(new_lengths);
		
		//take the average of every 'column', take it's average and put it into a single cell in the new tensor
		int depth = this.lengths[this.dimension - 1];
		for(int i = 0; i < tmp.total_data_length; i++)
		{
			float sum = 0;
			for(int j = 0; j < depth; j++)
			{
				sum += this.data[depth*i + j];
			}
			tmp.data[i] = sum / depth;
		}
		
		this.become(tmp);
	}
	
	//reduces dimension wherever possible
	//for example: a matrix of width 1 becomes a vector
	public void reduceDimension() throws DimensionException
	{
		//count how many dimensions can be removed
		int reduction_count = 0;
		for(int length : this.lengths)
		{
			if(length == 1)
			{
				reduction_count++;
			}
		}
		
		//create a temporary tensor to hold the result
		int[] resulting_lengths = new int[this.dimension - reduction_count];
		int index = 0;
		for(int i = 0; i < this.dimension; i++)
		{
			if(this.lengths[i] != 1)
			{
				resulting_lengths[index] = this.lengths[i];
				index++;
			}
		}
		Tensor result = new Tensor(resulting_lengths);
		
		int[] indices = new int[result.dimension];
		for(int i = 0; i < result.dimension; i++)
		{
			indices[i] = 0;
		}
		
		int[] old_indices = new int[this.dimension];
		
		for(int i = 0; i < result.total_data_length; i++)
		{
			//find the indices in the original tensor from the indices in the resulting tensor
			index = 0;
			for(int j = 0; j < this.dimension; j++)
			{
				if(this.lengths[j] == 1)
				{
					old_indices[j] = 0;
				}
				else
				{
					old_indices[j] = indices[index];
				}
				index++;
			}
			
			//copy
			result.set(this.get(), indices);
			
			//update indices
			indices[0]++;
			for(int j = 0; j < result.dimension - 1; j++)
			{
				if(indices[j] == result.lengths[j])
				{
					indices[j] = 0;
					indices[j+1]++;
				}
			}
		}
		
		this.become(result);
	}
	
	//performs max pooling on the tensor, using a 'kernel' of the given size
	public void maxPool(int... pooling_lengths) throws DimensionException
	{		
		if(pooling_lengths.length != this.dimension)
		{
			throw new DimensionException("dimensions don't match");
		}
		
		//if the pooling 'kernel' is a single cell, nothing will happen and we might as well return
		boolean trivial = true;
		for(int length : pooling_lengths)
		{
			if(length != 1)
			{
				trivial = false;
				break;
			}
		}
		if(trivial)
		{
			return;
		}
		
		//create a temporary tensor to hold the result
		int[] resulting_lengths = new int[this.dimension];
		for(int i = 0; i < this.dimension; i++)
		{
			resulting_lengths[i] = (int)Math.ceil((double)this.lengths[i]/(double)pooling_lengths[i]);
		}
		Tensor result = new Tensor(resulting_lengths);
		
		//initialize result indices
		int[] indices = new int[this.dimension];
		for(int i = 0; i < this.dimension; i++)
		{
			indices[i] = 0;
		}
		
		//compute total size of pooling kernel
		int total_pooling_size = 1;
		for(int length : pooling_lengths)
		{
			total_pooling_size *= length;
		}
		
		for(int i = 0; i < result.total_data_length; i++)
		{
			float max_value = 0.0f;
			
			//initialize pooling indices
			int[] pooling_indices = new int[this.dimension];
			for(int j = 0; j < this.dimension; j++)
			{
				pooling_indices[j] = 0;
			}
			
			for(int j = 0; j < total_pooling_size; j++)
			{
				//check whether the cell we want to check is not outside the tensor (which would give an IndexOutOfBoundsException)
				int[] total_indices = new int[this.dimension];
				boolean is_inside_tensor = true;
				for(int k = 0; k < this.dimension; k++)
				{
					total_indices[k] = indices[k]*pooling_lengths[k]+pooling_indices[k];
					if(total_indices[k] >= this.lengths[k])
					{
						is_inside_tensor = false;
						break;
					}
				}
								
				if(is_inside_tensor)
				{
					max_value = Math.max(max_value, this.get(total_indices));
				}
				
				//update pooling indices
				pooling_indices[0]++;
				for(int k = 0; k < pooling_indices.length - 1; k++)
				{
					if(pooling_indices[k] == pooling_lengths[k])
					{
						pooling_indices[k] = 0;
						pooling_indices[k+1]++;
					}
				}
			}
			
			result.set(max_value, indices);
			
			//update result indices
			indices[0]++;
			for(int j = 0; j < indices.length-1; j++)
			{
				if(indices[j] == result.lengths[j])
				{
					indices[j] = 0;
					indices[j+1]++;
				}
			}
		}
		
		this.become(result);
	}
	
	//performs the convolution operation on the tensor with given kernel as operator
	//zero padding is used, so the resulting tensor will be the exact same size as the original
	public void convolveWith(Tensor kernel) throws DimensionException
	{
		if(this.dimension != kernel.dimension)
		{
			throw new DimensionException("dimensions don't match");
		}
		
		//create a temporary tensor to hold the result
		int[] resulting_lengths = new int[this.dimension];
		for(int i = 0; i < this.dimension; i++)
		{
			resulting_lengths[i] = this.lengths[i];
		}
		Tensor result = new Tensor(resulting_lengths);
		
		//holds the 'coordinate' of the cell
		int[] indices = new int[this.dimension];							
		for(int i = 0; i < this.dimension; i++)	
		{
			//start the iteration in the 'top-left corner'
			indices[i] = 0;													
		}
		
		for(int index = 0; index < result.total_data_length; index++)
		{
			//this variable holds the result of applying the convolution operation to a single cell
			float sum = 0.0f;	

			//holds the 'coordinate' index of the kernel cell
			int[] kernel_indices = new int[this.dimension];					
			for(int j = 0; j < this.dimension; j++)
			{
				//we start the iteration in the 'top-left corner'
				kernel_indices[j] = 0;										
			}
			//iterate over the kernel
			for(int kernel_index = 0; kernel_index < kernel.total_data_length; kernel_index++)				
			{
				//holds the 'coordinates' of the cell whose value is going to be multiplied with a kernel cell value
				//also checks whether the cell lies inside the tensor (otherwise, we assume it's 0)
				boolean is_inside_tensor = true;
				int[] value_indices = new int[this.dimension];				
				for(int i = 0; i < this.dimension; i++)
				{
					value_indices[i] = indices[i] + kernel_indices[i];
					if(value_indices[i] >= this.lengths[i])
					{
						is_inside_tensor = false;
					}
				}

				//add the result of the multiplication to the sum
				if(is_inside_tensor)
				{
					sum += this.get(value_indices)*kernel.get(kernel_indices);	
				}		
				
				//update the kernel coordinates
				kernel_indices[0]++;
				for(int i = 0; i < this.dimension; i++)						
				{
					if(kernel_indices[i] == kernel.lengths[i] && i != this.dimension - 1)
					{
						kernel_indices[i] = 0;
						kernel_indices[i+1]++;
					}
				}
			}
			
			//set the cell in the 'result' tensor to the correct value
			result.set(sum, indices);									
			
			//update the tensor coordinates
			indices[0]++;
			for(int i = 0; i < this.dimension; i++)					
			{
				if(indices[i] == result.lengths[i] && i != this.dimension - 1)
				{
					indices[i] = 0;
					indices[i+1]++;
				}
			}
		}
		
		this.become(result);											
	}
	
	//rectified linear unit
	public void ReLu()
	{
		for(int i = 0; i < this.total_data_length; i++)
		{
			if(this.data[i] < 0)
			{
				this.data[i] = 0.0f;
			}
		}
	}
	
	//turn multidimensional indices into a single index in the serialized array
	private int getSerializedDataIndex(int... indices) throws DimensionException
	{
		if(indices.length != this.dimension)
		{
			throw new DimensionException("the amount of indices must be equal to the dimension");
		}
		
		int index = 0;
		for(int i = 0; i < this.dimension; i++)
		{
			index += indices[i] * this.index_products[i];
		}
		return index;
	}
}

