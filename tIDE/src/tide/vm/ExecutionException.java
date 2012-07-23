package tide.vm;

@SuppressWarnings("serial")
public class ExecutionException extends Exception {
	String message;
	
	public ExecutionException(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}

}
