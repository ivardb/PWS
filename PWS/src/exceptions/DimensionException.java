package exceptions;

//this exception is meant to be thrown when there are issues with tensor dimensions

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
