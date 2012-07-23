package tide.assembler;

public class IR {


	private ParseTree parseTree;
	private SymbolTable symbolTable;

	public ParseTree getParseTree() {
		return parseTree;
	}

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	IR(ParseTree parseTree, SymbolTable symbolTable) {
		this.parseTree=parseTree;
		this.symbolTable=symbolTable;
	}
}
