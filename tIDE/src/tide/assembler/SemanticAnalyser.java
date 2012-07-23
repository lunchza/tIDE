package tide.assembler;

import tide.assembler.ParseTree.PT_Directive_DC;
import tide.assembler.ParseTree.PT_Directive_DS;
import tide.assembler.ParseTree.PT_Directive_EQU;
import tide.assembler.ParseTree.PT_Label;
import tide.assembler.ParseTree.PT_Operation;

public class SemanticAnalyser {

	private ParseTree parseTree;
	//The current line being analysed
	int currentLine = 0;
	//Have the BEG and END directives been declared?
	boolean hasBEG = false;
	boolean hasEND = false;
	//Does the program have at least one HLT?
	boolean hasHalt = false;
	//Determines whether or not the data segment has been established
	boolean dataSegmentComplete = false;
	
	//keeps track of how many variable declarations have been encountered
	int numVars = 0;

	public SemanticAnalyser(ParseTree parseTree) {
		this.parseTree=parseTree;
	}

	public SymbolTable analyse() throws SyntaxException {
		SymbolTable symbolTable = null;
		try
		{
		symbolTable = new SymbolTable(parseTree.getStartAddress());
		}
		catch (NumberFormatException e)
		{
			throw new SyntaxException("Unable to find start address for this program. Missing ORG directive?");
		}
		//Build symbol table
		for (ParseTree.PT_Entity entity: parseTree.entities())
		{
			currentLine++;
			
			//Don't do anything besides increment currentLine for blank/comment-only lines
			if (entity instanceof ParseTree.PT_BlankLine)
				continue;
			
			//Check that ORG is the first declaration
			if (currentLine == 1 && !(entity instanceof ParseTree.PT_Directive_ORG))
				throw new SyntaxException("Error: ORG must appear on the first line of the program");
			
			//ignore certain directives. This ensures address consistency
			//if (entity instanceof ParseTree.PT_Directive_BEG || entity instanceof ParseTree.PT_Directive_ORG || entity instanceof ParseTree.PT_Directive_END)
			//	continue;
			
			if (entity instanceof ParseTree.PT_Directive_ORG)
			{
				if (currentLine != 1)
					throw new SyntaxException("Error: ORG must appear on the first line of the program", currentLine);
				else
					continue;
			}
			
			//Since each argument takes up a location in memory, if an argument
			//is present PC needs to be offset by one to compensate
			if (entity instanceof ParseTree.PT_OneArgOperation)
				symbolTable.incrementPC();
			
			if (entity instanceof ParseTree.PT_Label)
			{
			symbolTable.add(((PT_Label) entity).getName());
			
			//These are necessary to avoid incrementing PC, since the label essentially
			//gets ignored by the compiler. The line number also has not changed since
			//the label gets ignored
				currentLine--;
				continue;
			}
			
			if (entity instanceof ParseTree.PT_Directive_DC)
			{
				//Check that the DC directive is in the data segment
				if (!dataSegmentComplete)
				{
					numVars++;
					symbolTable.add(((PT_Directive_DC) entity).getValue(), symbolTable.get(((ParseTree.PT_Directive_DC) entity).getAssociatedLabel()));
				}
				
				else
					throw new SyntaxException("Misplaced construct " + ((ParseTree.PT_Directive_DC) (entity)).getAssociatedLabel(), currentLine);
			}
			
			if (entity instanceof ParseTree.PT_Directive_DS)
			{
				//Check that the DS directive is in the data segment
				if (!dataSegmentComplete)
				{
					
					symbolTable.add(((PT_Directive_DS) entity).getLength(), symbolTable.get(((ParseTree.PT_Directive_DS) entity).getAssociatedLabel()));
					
					//For each reserved space in memory, increase PC and increase variable count
					for (int i = 0; i < Integer.parseInt(((PT_Directive_DS) entity).getLength()) ;i++)
					{
						numVars++;
						symbolTable.incrementPC();
					}
					
					/*//reserve memory equal to length
					for (int i = 0; i < Integer.parseInt(((PT_Directive_DS) entity).getValue()); i++)
					{
						numVars++;
						symbolTable.add("0000");
					}*/
				}
				
				else
					throw new SyntaxException("Misplaced construct " + ((ParseTree.PT_Directive_DC) (entity)).getAssociatedLabel(), currentLine);
				
				continue;
			}
			
			if (entity instanceof ParseTree.PT_Directive_EQU)
			{
				//Check that the EQU directive is in the data segment
				if (!dataSegmentComplete)
				{
				//add the identifier to the table as a separate entity
				symbolTable.add(((ParseTree.PT_Directive_EQU) entity).getIdentifier());
				
				//Update the identifier to reflect the value
				symbolTable.add(((PT_Directive_EQU) entity).getValue(), symbolTable.get(((ParseTree.PT_Directive_EQU) entity).getIdentifier()));
				
				continue; //EQU doesn't appear in the assembled file
				}
				else
					throw new SyntaxException("Misplaced construct " + ((ParseTree.PT_Directive_EQU) (entity)).getIdentifier(), currentLine);
			}
			
			if (entity instanceof ParseTree.PT_Directive_BEG)
			{
				dataSegmentComplete = true;
				hasBEG = true;
				continue;
			}
			
			if (entity instanceof ParseTree.PT_Directive_END)
			{
				if (!dataSegmentComplete)
					throw new SyntaxException("BEG directive missing");
				
				else if (entity != parseTree.getLastEntity())
					throw new SyntaxException("Error, END directive must be at the end of the program", currentLine);
				
				else
				{
					hasEND = true;
					continue;
				}
			}
						
			if (entity instanceof ParseTree.PT_NoArgOperation || entity instanceof ParseTree.PT_OneArgOperation)
			{				
				//code appearing before the BEG directive has been declared
				if (!hasBEG)
				{
					throw new SyntaxException("Misplaced construct " +  ((PT_Operation) entity).getGR_Operation().getText() + ". Must appear after BEG statement", currentLine);
				}
			}
			//Increase the program counter for the symbol table
			symbolTable.incrementPC();
		}
		
		if (!hasEND)
			throw new SyntaxException("Missing END directive");
		
		return symbolTable;
	}
	//Returns the number of variables encountered in the parse tree
	int getNumVars()
	{
		return numVars;
	}
}
