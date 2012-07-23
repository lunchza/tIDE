package tide.assembler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class SymbolTable {
	
	private Map<String, Short> symbolTable;
	private short PC;
	
	public SymbolTable(String startAddress) throws SyntaxException
	{
		symbolTable = new HashMap<String, Short>();
		try
		{
		PC = Short.parseShort(startAddress);
		}
		//invalid address, although theoretically this should never be encountered
		//since this program was addressed when building the parse tree
		catch (NumberFormatException nfe)
		{
			throw new SyntaxException("Invalid address for ORG directive. Address must be a value between 0 and 65535");	
		}
	}
	
	public Map<String, Short> symbols()
	{
		return Collections.unmodifiableMap(symbolTable);
	}
	
	public void add(String symbol) throws SyntaxException
	{
		//System.out.println("Added " + symbol + " to symbol table with address " + PC);
		if(symbolTable.containsKey(symbol))
			throw new SyntaxException("Duplicate declaration of identifier " + symbol);
		
		symbolTable.put(symbol, PC);
	}
	
	public void add(String symbol, short address) throws SyntaxException
	{
		//System.out.println("Added " + symbol + " to symbol table with address " + address);
		if(symbolTable.containsValue(symbol))
			throw new SyntaxException("Duplicate declaration of identifier " + symbol);
		
		symbolTable.put(symbol, address);
	}
	
	public Short get(String symbol)
	{
		return symbolTable.get(symbol);
	}
	
	public void incrementPC()
	{
		PC++;
	}
}
