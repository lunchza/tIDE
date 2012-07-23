package tide.assembler;

public class SyntaxException extends Exception {
	
	String message;
	int line = -1;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3938465150941607850L;
	
	public SyntaxException()
	{
		super();
	}
	
	public SyntaxException(String message)
	{
		super(message);
		this.message = message;
	}
	
	public SyntaxException(String message, int line)
	{
		super(message);
		this.message = message;
		this.line = line;
	}
	
	@Override
	public String getMessage()
	{
		if (line != -1)
			return message + " at line " + line;
		
		return message;
	}
	
	public int getLine()
	{
		return line;
	}

}
