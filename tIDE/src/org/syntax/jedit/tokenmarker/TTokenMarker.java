package org.syntax.jedit.tokenmarker;

import org.syntax.jedit.KeywordMap;

public class TTokenMarker extends CTokenMarker {
	
	// private members
	private static KeywordMap tKeywords;
	
	public TTokenMarker()
	{
		super(false,getKeywords());
	}
	
	public static KeywordMap getKeywords()
	{
		if(tKeywords == null)
		{
			tKeywords = new KeywordMap(false);
			
			tKeywords.add(";",Token.COMMENT1);
			//tKeywords.add("/*",Token.COMMENT2);
			
			tKeywords.add("BEG",Token.KEYWORD2);
			tKeywords.add("ORG",Token.KEYWORD2);
			tKeywords.add("EQU",Token.KEYWORD2);
			tKeywords.add("DC",Token.KEYWORD2);
			tKeywords.add("DS",Token.KEYWORD2);
			tKeywords.add("END",Token.KEYWORD2);
			
			tKeywords.add("NOP",Token.LITERAL2);
			tKeywords.add("CLA",Token.LITERAL2);
			tKeywords.add("CLX",Token.LITERAL2);
			tKeywords.add("INC",Token.LITERAL2);
			tKeywords.add("DEC",Token.LITERAL2);
			tKeywords.add("INX",Token.LITERAL2);
			tKeywords.add("DEX",Token.LITERAL2);
			tKeywords.add("TAX",Token.LITERAL2);
			tKeywords.add("INI",Token.LITERAL2);
			tKeywords.add("INA",Token.LITERAL2);
			tKeywords.add("OTI",Token.LITERAL2);
			tKeywords.add("OTA",Token.LITERAL2);
			tKeywords.add("PSH",Token.LITERAL2);
			tKeywords.add("POP",Token.LITERAL2);
			tKeywords.add("RET",Token.LITERAL2);
			tKeywords.add("HLT",Token.LITERAL2);	
			tKeywords.add("LDA",Token.KEYWORD1);
			tKeywords.add("LDX",Token.KEYWORD1);
			tKeywords.add("LDI",Token.KEYWORD1);
			tKeywords.add("STA",Token.KEYWORD1);
			tKeywords.add("STX",Token.KEYWORD1);
			tKeywords.add("ADD",Token.KEYWORD1);
			tKeywords.add("ADX",Token.KEYWORD1);
			tKeywords.add("ADI",Token.KEYWORD1);
			tKeywords.add("SUB",Token.KEYWORD1);
			tKeywords.add("SBX",Token.KEYWORD1);
			tKeywords.add("SBI",Token.KEYWORD1);
			tKeywords.add("CMP",Token.KEYWORD1);
			tKeywords.add("CPX",Token.KEYWORD1);
			tKeywords.add("CPI",Token.KEYWORD1);
			tKeywords.add("LSP",Token.KEYWORD1);
			tKeywords.add("LSI",Token.KEYWORD1);
			tKeywords.add("BRN",Token.KEYWORD1);
			tKeywords.add("BZE",Token.KEYWORD1);
			tKeywords.add("BNZ",Token.KEYWORD1);
			tKeywords.add("BPZ",Token.KEYWORD1);
			tKeywords.add("BNG",Token.KEYWORD1);
			tKeywords.add("JSR",Token.KEYWORD1);
		}
		return tKeywords;
	}
}
