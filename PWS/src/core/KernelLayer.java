package core;


//work in progress
public class KernelLayer {
	
	public int dimension;
	
	public int neuron_in_count;
	public int neuron_out_count;
	
	private Tensor[][] kernels;
	
	public KernelLayer(int dimension, int neuron_in_count, int neuron_out_count)
	{
		this.dimension = dimension;
		this.neuron_in_count = neuron_in_count;
		this.neuron_out_count = neuron_out_count;
	}
	
}
