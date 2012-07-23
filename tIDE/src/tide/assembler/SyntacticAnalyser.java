package tide.assembler;

import tide.assembler.LexicalRepresentation.LexLine;


public class SyntacticAnalyser {

	private LexicalRepresentation lexicalRepresentation;


	public SyntacticAnalyser(LexicalRepresentation lexicalRepresentation) {
		this.lexicalRepresentation=lexicalRepresentation;
	}


	public ParseTree analyse() throws SyntaxException {
		ParseTree parseTree = new ParseTree();
		for (LexLine lexLine : lexicalRepresentation.lines()) {
			parseTree.append(lexLine);
		}
		return parseTree;
	}

}
