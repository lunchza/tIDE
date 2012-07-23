package tide.assembler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tide.assembler.Grammar.GR_Directive;
import tide.assembler.Grammar.GR_Operation;
import tide.assembler.LexicalRepresentation.LexLine;


public class ParseTree {

	private List<PT_Entity> entities;
	private String startAddress;
	private static int currentLine;
	
	ParseTree() {
		entities = new LinkedList<ParseTree.PT_Entity>();
		currentLine = 0;
	}
	
	public List<PT_Entity> entities()
	{
		return Collections.unmodifiableList(entities);
	}
	
	public String getStartAddress()
	{
		return startAddress;
	}
	
	public PT_Entity getLastEntity()
	{
		return entities.get(entities.size()-1);
	}
	
	void append(LexLine lexLine) throws SyntaxException {
		//Append a Blank-line entity to the parseTree
		//The only purpose of this is to ensure line number reporting is 
		//accurate
		if (lexLine.getLabel() != null && lexLine.getLabel().equals(" "))
		{
			_appendBlankLine();
			currentLine++;
			return;
		}
		
		currentLine++;
		String mnemonic = lexLine.getMnemonic();
		if (Grammar.GR_Directive.isDirective(mnemonic)) {
			// DIRECTIVE
			Grammar.GR_Directive directive = Grammar.GR_Directive.toDirective(mnemonic);
			_appendDirective(lexLine.getLabel(),directive,lexLine.getArgument());
		} else {
			if (Grammar.GR_Operation.isOperation(mnemonic)) {
				// OPERATION
				Grammar.GR_Operation operation = Grammar.GR_Operation.toOperation(mnemonic);
				_appendOperation(lexLine.getLabel(),operation,lexLine.getArgument());
			} else {
				throw new SyntaxException();
			}
		}

	}
	
	private void _appendBlankLine()
	{
		entities.add(new PT_BlankLine());
	}

	
	// **********************************************************************
	// **********************************************************************
	
	private void _appendOperation(String label, GR_Operation operation,
			String argument) throws SyntaxException {
		
		// We treat the label and operation as distinct entities!
		if (label!=null && !label.isEmpty()) {
			// is labeled.
			entities.add(new PT_Label(label));
		}
		if (operation.hasArg()) {
			// This operation takes an argument
			if (argument==null || argument.isEmpty()) {
				throw new SyntaxException("Missing argument for operation " + operation.getText(), currentLine);
			}
			entities.add(new PT_OneArgOperation(operation,argument));
		} else {
			// This operation does not take an argument
			if (argument!=null && !argument.isEmpty()) {
				throw new SyntaxException();
			}
			entities.add(new PT_NoArgOperation(operation));
		}

	}

	
	// **********************************************************************
	// **********************************************************************


	// **********************************************************************
	// **********************************************************************

	
	/**
	 * There are so few directives with so little commonality,
	 * that is is worth a full analysis at this point.
	 *  
	 * @param label
	 * @param directive
	 * @param argument
	 * @throws SyntaxException
	 */
	private void _appendDirective(String label, GR_Directive directive,
			String argument) throws SyntaxException {
		
		
		switch (directive) {
		case BEG:
			if (label!=null || argument!=null) {
				throw new SyntaxException();
			}
			entities.add(new PT_Directive_BEG());
			return;
		case END:
			if (label!=null || argument!=null) {
				throw new SyntaxException();
			}
			entities.add(new PT_Directive_END());
			return;
		case ORG:
			if (label!=null) {
				throw new SyntaxException();
			}
			entities.add(new PT_Directive_ORG(argument));
			startAddress = argument;
			return;
		case DC:
			// We treat the label and operation as distinct entities!
			if (label!=null && !label.isEmpty()) {
				// is labeled.
				entities.add(new PT_Label(label));
			}
			entities.add(new PT_Directive_DC(label, argument));
			return;
		case DS:
			// We treat the label and operation as distinct entities!
			if (label!=null && !label.isEmpty()) {
				// is labeled.
				entities.add(new PT_Label(label));
			}
			entities.add(new PT_Directive_DS(label, argument));
			return;
		case EQU:
			entities.add(new PT_Directive_EQU(label,argument));
			return;

		default:
			throw new IllegalStateException("Impossible");
		}
		
		
	}
	
	
	
	// **********************************************************************
	// **********************************************************************
	// **********************************************************************
	// **********************************************************************
	// **********************************************************************
	// **********************************************************************
	
	
	
	
	

	// **********************************************************************
	// **********************************************************************
	// **********************************************************************
	// **********************************************************************
	// **********************************************************************
	// **********************************************************************
	
	public static abstract class PT_Entity {
		
	}
	
	public static class PT_Label extends PT_Entity {

		private String name;

		public String getName() {
			return name;
		}

		private PT_Label(final String label) throws SyntaxException {
			String trimLabel=label.trim();
			if (_isLegalLabel(trimLabel)) {
				if (trimLabel.endsWith(":"))
					trimLabel = trimLabel.substring(0, trimLabel.length()-1); //remove colon from the end of the label
				this.name = trimLabel;
			}
			else {
				throw new SyntaxException("Invalid label identifier \"" + trimLabel + "\"", currentLine);
			}
			
		}

		private boolean _isLegalLabel(String label) throws SyntaxException {
			String illegalChars = "!@#$%^&*()-=+/?><|\\\'\"[]{}";
			for (int i = 0; i < illegalChars.length(); i++)
			{
				if(label.contains(""+illegalChars.charAt(i)))
					throw new SyntaxException("Invalid characters in label \'" + label + "\'", currentLine);
			}
			//label may have at most 8 significant characters
			if (label.length() > 9)
				throw new SyntaxException("Invalid label \'" + label + "\'. Identifier cannot be longer than 8 characters.", currentLine);
			return true;		
		}
	}
	
	public static abstract class PT_Operation extends PT_Entity {
		
		private GR_Operation gr_operation;
		
		public GR_Operation getGR_Operation() {
			return gr_operation;
		}
		protected PT_Operation(GR_Operation operation) {
			this.gr_operation=operation;
		}
	}
	
	public static class PT_NoArgOperation extends PT_Operation {


		private PT_NoArgOperation(Grammar.GR_Operation operation) throws SyntaxException {
			super(operation);
			if (operation.hasArg()) {
				throw new SyntaxException();
			}
		}}
	public static class PT_OneArgOperation extends PT_Operation {

		private String argument;

		private PT_OneArgOperation(GR_Operation operation, String argument) throws SyntaxException {
			super(operation);
			
			if (!operation.hasArg()) {
				throw new SyntaxException();
			}
			
			else
				this.argument = argument;
		}

		public String getArgument() {
			return argument;
		}}
	
	//Necessary for reporting correct line numbers
	public static class PT_BlankLine extends PT_Entity
	{
		
	}
	
	//Necessary for reporting correct line numbers
	public static class PT_CommentLine extends PT_Entity
	{
		
	}


	public static abstract class PT_Directive extends PT_Entity {
		
	}
	
	public static class PT_Directive_BEG extends PT_Directive {}
	
	public static class PT_Directive_END extends PT_Directive {}
	
	public static class PT_Directive_ORG extends PT_Directive {

		private String address;

		public String getAddress() {
			return address;
		}

		private PT_Directive_ORG(String argument) throws SyntaxException {
			if (!_isValidAddress(argument)) {
				throw new SyntaxException();
			}
			this.address=argument;
		}

		private boolean _isValidAddress(String argument) throws SyntaxException {
			if (argument == null)
				throw new SyntaxException("Missing address for ORG directive");
			short arg = Short.MIN_VALUE;
			try
			{
				arg = Short.parseShort(argument);
			}
			catch (NumberFormatException e)
			{		
				throw new SyntaxException("Invalid address for ORG directive. Address must be a value between 0 and 65535");	
			}
			
			if (arg < 0 || arg > 65535)
				throw new SyntaxException("Address must be a value between 0 and 65535");
			
			return true;
		}}

	
	public static class PT_Directive_DC extends PT_Directive {
		
		String associatedLabel;
		String value;

		public PT_Directive_DC(String associatedLabel, String value) {
			this.associatedLabel = associatedLabel;
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public String getAssociatedLabel() {
			return associatedLabel;
		}}
	
	
	
	public static class PT_Directive_DS extends PT_Directive {

		String associatedLabel;
		String length;

		public PT_Directive_DS(String associatedLabel, String length) {
			this.associatedLabel = associatedLabel;
			this.length = length;
		}

		public String getLength() {
			return length;
		}

		public String getAssociatedLabel() {
			return associatedLabel;
		}

	}
	
	public static class PT_Directive_EQU extends PT_Directive {

		String identifier, value;
		
		public PT_Directive_EQU(String identifier, String value) {
			this.identifier = identifier;
			this.value = value;
		}

		public String getIdentifier() {
			return identifier;
		}

		public String getValue() {
			return value;
		}}
}
