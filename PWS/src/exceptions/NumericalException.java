package exceptions;

//this exception is meant to be thrown when there are numerical errors for which no other exception is appropriate.

public class NumericalException extends Exception{

	public NumericalException() {};
	
	public NumericalException(String message)
	{
		super(message);
	}
	
	public NumericalException(Throwable clause)
	{
		super(clause);
	}
	
	public NumericalException(String message, Throwable clause)
	{
		super(message, clause);
	}
	
}
