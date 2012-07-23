package tide.assembler;

import java.io.IOException;
import java.io.InputStream;

public class FrontEnd {
	
	int vars;

	public IR parse(InputStream assemblyLanguageSourceCode) throws IOException, SyntaxException {
		final Lexer lexer = new Lexer();
		
		final LexicalRepresentation lexicalRepresentation = lexer.parse(assemblyLanguageSourceCode);
		
		final SyntacticAnalyser syntacticAnalyser = new SyntacticAnalyser(lexicalRepresentation);
		final ParseTree parseTree = syntacticAnalyser.analyse();
		
		final SemanticAnalyser semanticAnalyser = new SemanticAnalyser(parseTree);
		final SymbolTable symbolTable = semanticAnalyser.analyse();
		vars = semanticAnalyser.getNumVars();

		return new IR(parseTree, symbolTable);
	}
	
	int getNumVars()
	{
		return vars;
	}

	

}
