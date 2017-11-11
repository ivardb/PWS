package exceptions;

public class DimensionException extends Exception{
	
	public DimensionException() {};
	
	public DimensionException(String message)
	{
		super(message);
	}
	
	public DimensionException(Throwable clause)
	{
		super(clause);
	}
	
	public DimensionException(String message, Throwable clause)
	{
		super(message, clause);
	}
}
