package tide.assembler;

public interface Grammar {

	public enum GR_Directive {
		BEG ("BEG", LabelType.NOT_USED, ArgumentType.NOT_USED),
		END ("END", LabelType.NOT_USED, ArgumentType.NOT_USED),
		ORG ("ORG", LabelType.NOT_USED, ArgumentType.ADDRESS),
		DC ("DC", LabelType.OPTIONAL, ArgumentType.VALUE),
		DS ("DS", LabelType.OPTIONAL, ArgumentType.LENGTH),
		EQU ("EQU", LabelType.IDENTIFIER, ArgumentType.VALUE);
		
		public enum LabelType {
			NOT_USED, OPTIONAL, IDENTIFIER
		}
		
		public enum ArgumentType {
			NOT_USED, ADDRESS, VALUE, LENGTH
		}
		
		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public LabelType getLabelType() {
			return labelType;
		}

		public void setLabelType(LabelType labelType) {
			this.labelType = labelType;
		}

		public ArgumentType getArgumentType() {
			return argumentType;
		}

		private String text;
		private LabelType labelType;
		private ArgumentType argumentType;
		
		private GR_Directive(String text, LabelType labelType, ArgumentType argumentType) {
			this.text=text;
			this.labelType=labelType;
			this.argumentType=argumentType;
		}
		
		static public boolean isDirective(String text) {
			for (GR_Directive d : values()) {
				if (d.text.equals(text)) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * 
		 * @param text
		 * @return null is no such ENUM_Directive.
		 * @throws SyntaxException 
		 */
		static public GR_Directive toDirective(String text) throws SyntaxException {
			for (GR_Directive d : values()) {
				if (d.text.equals(text)) {
					return d;
				}
			}
			throw new SyntaxException();
		}		
	}

	public enum GR_Operation {
		NOP ("NOP",false),
		CLA ("CLA",false),
		CLX ("CLX",false),
		INC ("INC",false),
		DEC ("DEC",false),
		INX ("INX",false),
		DEX ("DEX",false),
		TAX ("TAX",false),
		INI ("INI",false),
		INA ("INA",false),
		OTI ("OTI",false),
		OTA ("OTA",false),
		PSH ("PSH",false),
		POP ("POP",false),
		RET ("RET",false),
		HLT ("HLT",false),
		LDA ("LDA",true),
		LDX ("LDX",true),
		LDI ("LDI",true),
		STA ("STA",true),
		STX ("STX",true),
		ADD ("ADD",true),
		ADX ("ADX",true),
		ADI ("ADI",true),
		SUB ("SUB",true),
		SBX ("SBX",true),
		SBI ("SBI",true),
		CMP ("CMP",true),
		CPX ("CPX",true),
		CPI ("CPI",true),
		LSP ("LSP",true),
		LSI ("LSI",true),
		BRN ("BRN",true),
		BZE ("BZE",true),
		BNZ ("BNZ",true),
		BPZ ("BPZ",true),
		BNG ("BNG",true),
		JSR ("JSR",true);
			
		private String text;
		private boolean hasArg;
		
		private GR_Operation(String text,boolean hasArg) {
			this.text=text;
			this.hasArg=hasArg;
		}

		static public boolean isOperation(String text) {
			for (GR_Operation o : values()) {
				if (o.text.equals(text)) {
					return true;
				}
			}
			return false;
		}
				
		/**
		 * 
		 * @param text
		 * @return null is no such ENUM_Operation.
		 */
		static public GR_Operation toOperation(String text) {
			for (GR_Operation o : values()) {
				if (o.text.equals(text)) {
					return o;
				}
			}
			return null;
		}

		public boolean hasArg() {
			return hasArg;
		}

		public String getText() {
			return text;
		}
	}	
}
