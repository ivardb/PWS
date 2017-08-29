package core;

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
	
	public Tensor(float[] data, int... lengths)
	{
		this(lengths);
		this.data = data;
	}
	
	//merges an array of same-size tensors into one large tensor
	public Tensor(Tensor[] tensors)
	{
		//create a new, temporary tensor of the correct dimensions
		int[] lengths = new int[tensors[0].dimension + 1];
		for(int i = 0; i < tensors[0].dimension; i++)
		{
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

	public float get(int... indices)
	{		
		return this.data[this.getSerializedDataIndex(indices)];
	}

	public float[] getSerializedData()
	{
		return this.data;
	}
	
	public void set(float value, int... indices)
	{
		this.data[this.getSerializedDataIndex(indices)] = value;
	}

	public void flatten()
	{
		//ToDo
	}
	
	public void convolveWith(Tensor kernel)
	{
		//ToDo
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
	private int getSerializedDataIndex(int... indices)
	{
		int index = 0;
		for(int i = 0; i < this.dimension; i++)
		{
			index += indices[i] * this.index_products[i];
		}
		return index;
	}
}

