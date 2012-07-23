package tide.assembler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Assembler {
	
	/**
	 * Translates <em>T-Machine Assembly Language</em> into <em>T-Machine Byte Code</em>.
	 * 
	 * @param assemblyLanguageSourceCode for the T-Machine.
	 * @param machineCode for the T-Machine.
	 * @throws SyntaxException 
	 * @throws IOException 
	 */
	private int vars;
	public void assemble(final InputStream assemblyLanguageSourceCode, final OutputStream machineCode) throws IOException, SyntaxException {
		
		// Front end.
		final FrontEnd frontEnd = new FrontEnd();
		final IR internalRepresentation = frontEnd.parse(assemblyLanguageSourceCode);
		//Get the number of variables encountered from the front-end
		vars = frontEnd.getNumVars();

		// Back end.
		final BackEnd backEnd = new BackEnd();
		backEnd.translate(internalRepresentation,machineCode);
	}
	
	//Get the number of variables associated with this assembler
	public int getNumVars()
	{
		return vars;
	}
	
	
}
