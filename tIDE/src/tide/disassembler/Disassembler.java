package tide.disassembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import tide.assembler.SyntaxException;

public class Disassembler {
	
	/**
	 * Contains each line of code to be disassembled as a separate item
	 */
	private List<String> code;
	private boolean hasArgument;

	public Disassembler (String filename) throws FileNotFoundException
	{
		hasArgument = false;
		
		//initialise code list
		code = new ArrayList<String>();
		
		File file = new File(filename);
		Scanner sc = new Scanner(file);
		while(sc.hasNext())
		{
			//add each line of code to the code list
			code.add(sc.nextLine());
		}
		sc.close();
	}
	
	public File disassemble(String outPath) throws FileNotFoundException, SyntaxException
	{
		File outFile = new File(outPath);
		PrintWriter outWriter = new PrintWriter(outFile);
		
		//write the starting memory address
		try
		{
		outWriter.write("ORG " + code.get(0) + "\n");
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new SyntaxException("Error disassembling file. It appears to be invalid.");
		}
		for (int i = 1; i < code.size(); i++)
		{
				outWriter.write(interpret(code.get(i)));
			
			if (hasArgument)
			{
				outWriter.write(" " + code.get(i++));
				hasArgument = false;
			}

			outWriter.write("\n");
		}
		outWriter.close();
		return outFile;
	}
	
	private String interpret(String line)
	{		
		if (line.equals("0000"))
			return "NOP";
		
		else if (line.equals("0001"))
			return "CLA";
			
		else if (line.equals("0002"))
			return "CLX";
				
		else if (line.equals("0003"))
			return "INC";
					
		else if (line.equals("0004"))
			return "DEC";
						
		else if (line.equals("0005"))
			return "INX";
			
		else if (line.equals("0006"))
			return "DEX";
			
		else if (line.equals("0007"))
			return "TAX";
			
		else if (line.equals("0008"))
			return "INI";
			
		else if (line.equals("0009"))
			return "INA";
			
		else if (line.equals("000A"))
			return "OTI";
		
		else if (line.equals("000B"))
			return "OTA";
		
		else if (line.equals("000C"))
			return "PSH";
		
		else if (line.equals("000D"))
			return "POP";
		
		else if (line.equals("000E"))
			return "RET";
		
		else if (line.equals("000F"))
			return "HLT";
		
		else if (line.equals("0010"))
		{
			hasArgument = true;
			return "LDA";
		}
		
		else if (line.equals("0011"))
		{
			hasArgument = true;
			return "LDX";
		}
		
		else if (line.equals("0012"))
		{
			hasArgument = true;
			return "LDI";
		}
		
		else if (line.equals("0013"))
		{
			hasArgument = true;
			return "STA";
		}
		
		else if (line.equals("0014"))
		{
			hasArgument = true;
			return "STX";
		}
		
		else if (line.equals("0015"))
		{
			hasArgument = true;
			return "ADD";
		}
		
		else if (line.equals("0016"))
		{
			hasArgument = true;
			return "ADX";
		}
		
		else if (line.equals("0017"))
		{
			hasArgument = true;
			return "ADI";
		}
		
		else if (line.equals("0018"))
		{
			hasArgument = true;
			return "SUB";
		}
		
		else if (line.equals("0019"))
		{
			hasArgument = true;
			return "SBX";
		}
		
		else if (line.equals("001A"))
		{
			hasArgument = true;
			return "SBI";
		}
		
		else if (line.equals("001B"))
		{
			hasArgument = true;
			return "CMP";
		}
		
		else if (line.equals("001C"))
		{
			hasArgument = true;
			return "CPX";
		}
		
		else if (line.equals("001D"))
		{
			hasArgument = true;
			return "CPI";
		}
		
		else if (line.equals("001E"))
		{
			hasArgument = true;
			return "LSP";
		}
		
		else if (line.equals("001F"))
		{
			hasArgument = true;
			return "LSI";
		}
		
		else if (line.equals("0020"))
		{
			hasArgument = true;
			return "BRN";
		}
		
		else if (line.equals("0021"))
		{
			hasArgument = true;
			return "BZE";
		}
		
		else if (line.equals("0022"))
		{
			hasArgument = true;
			return "BNZ";
		}
		
		else if (line.equals("0023"))
		{
			hasArgument = true;
			return "BPZ";
		}
		
		else if (line.equals("0024"))
		{
			hasArgument = true;
			return "BNG";
		}
		
		else if (line.equals("0025"))
		{
			hasArgument = true;
			return "JSR";
		}
		
		else
			return "???";	
	}
	
}
