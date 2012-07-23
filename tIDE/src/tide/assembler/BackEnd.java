package tide.assembler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import tide.assembler.Grammar.GR_Operation;
import tide.assembler.ParseTree.PT_Directive_DC;
import tide.assembler.ParseTree.PT_Directive_DS;
import tide.assembler.ParseTree.PT_Directive_ORG;

public class BackEnd {

	ParseTree parseTree;
	SymbolTable symbolTable;

	public void translate(IR inText, OutputStream dest) throws IOException, SyntaxException {

		PrintWriter writer = new PrintWriter(dest);

		parseTree = inText.getParseTree();
		symbolTable = inText.getSymbolTable();

		//second pass

		int currentLine = 0;

		for (ParseTree.PT_Entity entity: parseTree.entities())
		{
			currentLine++;

			//Decision tree based on entity type
			
			//no arg operation encountered
			if (entity instanceof ParseTree.PT_NoArgOperation)
			{
				GR_Operation op = ((ParseTree.PT_NoArgOperation) entity).getGR_Operation();

				if(op.getText().equals("NOP"))
					writer.printf("%04X\n", 0x00);

				else if(op.getText().equals("CLA"))
					writer.printf("%04X\n", 0x01);

				else if(op.getText().equals("CLX"))
					writer.printf("%04X\n", 0x02);

				else if(op.getText().equals("INC"))
					writer.printf("%04X\n", 0x03);

				else if(op.getText().equals("DEC"))
					writer.printf("%04X\n", 0x04);

				else if(op.getText().equals("INX"))
					writer.printf("%04X\n", 0x05);

				else if(op.getText().equals("DEX"))
					writer.printf("%04X\n", 0x06);

				else if(op.getText().equals("TAX"))
					writer.printf("%04X\n", 0x07);

				else if(op.getText().equals("INI"))
					writer.printf("%04X\n", 0x08);

				else if(op.getText().equals("INA"))
					writer.printf("%04X\n", 0x09);

				else if(op.getText().equals("OTI"))
					writer.printf("%04X\n", 0x0A);

				else if(op.getText().equals("OTA"))
					writer.printf("%04X\n", 0x0B);

				else if(op.getText().equals("PSH"))
					writer.printf("%04X\n", 0x0C);

				else if(op.getText().equals("POP"))
					writer.printf("%04X\n", 0x0D);

				else if(op.getText().equals("RET"))
					writer.printf("%04X\n", 0x0E);

				else if(op.getText().equals("HLT"))
					writer.printf("%04X\n", 0x0F);

				else
					throw new SyntaxException();
			}

			//one arg operation encountered
			else if (entity instanceof ParseTree.PT_OneArgOperation)
			{
				GR_Operation op = ((ParseTree.PT_OneArgOperation) entity).getGR_Operation();

				//Sentinel value for argument. Makes it easier to determine if an
				//argument is a literal or an address. This should be sufficient since
				//the maximum value usable in t-language is far above this value
				int argument = Integer.MIN_VALUE;

				try{

					if(op.getText().equals("LDA"))
						writer.printf("%04X\n", 0x10);

					else if(op.getText().equals("LDX"))
						writer.printf("%04X\n", 0x11);

					else if(op.getText().equals("LDI"))
					{
						writer.printf("%04X\n", 0x12);

						argument = Integer.decode(((ParseTree.PT_OneArgOperation) entity).getArgument());

					}

					else if(op.getText().equals("STA"))
						writer.printf("%04X\n", 0x13);

					else if(op.getText().equals("STX"))
						writer.printf("%04X\n", 0x14);

					else if(op.getText().equals("ADD"))
						writer.printf("%04X\n", 0x15);

					else if(op.getText().equals("ADX"))
						writer.printf("%04X\n", 0x16);

					else if(op.getText().equals("ADI"))
					{
						writer.printf("%04X\n", 0x17);
						argument = Integer.decode(((ParseTree.PT_OneArgOperation) entity).getArgument());
					}

					else if(op.getText().equals("SUB"))
						writer.printf("%04X\n", 0x18);

					else if(op.getText().equals("SBX"))
						writer.printf("%04X\n", 0x19);

					else if(op.getText().equals("SBI"))
					{
						writer.printf("%04X\n", 0x1A);
						argument = Integer.decode(((ParseTree.PT_OneArgOperation) entity).getArgument());
					}

					else if(op.getText().equals("CMP"))
						writer.printf("%04X\n", 0x1B);

					else if(op.getText().equals("CPX"))
						writer.printf("%04X\n", 0x1C);

					else if(op.getText().equals("CPI"))
					{
						writer.printf("%04X\n", 0x1D);
						argument = Integer.decode(((ParseTree.PT_OneArgOperation) entity).getArgument());
					}

					else if(op.getText().equals("LSP"))
						writer.printf("%04X\n", 0x1E);

					else if(op.getText().equals("LSI"))
					{
						writer.printf("%04X\n", 0x1F);
						argument = Integer.decode(((ParseTree.PT_OneArgOperation) entity).getArgument());
					}

					else if(op.getText().equals("BRN"))
						writer.printf("%04X\n", 0x20);


					else if(op.getText().equals("BZE"))
						writer.printf("%04X\n", 0x21);

					else if(op.getText().equals("BNZ"))
						writer.printf("%04X\n", 0x22);

					else if(op.getText().equals("BPZ"))
						writer.printf("%04X\n", 0x23);

					else if(op.getText().equals("BNG"))
						writer.printf("%04X\n", 0x24);


					else if(op.getText().equals("JSR"))
						writer.printf("%04X\n", 0x25);


					else
						throw new SyntaxException();

				}
				catch (NumberFormatException e)
				{
					throw new SyntaxException("Invalid argument for operation " + op.getText(), currentLine);
				}

				//Determine if an address was supplied as an argument
				if (argument == Integer.MIN_VALUE)
				{
					//Need to find the corresponding address in the symbol table
					try {
						argument = symbolTable.get(((ParseTree.PT_OneArgOperation) entity).getArgument());

					} catch (NullPointerException e) {
						throw new SyntaxException("Syntax error. Argument for " + op.getText() + " must be a valid, existing label", currentLine);
					}
				}

				//finally, write the argument for the operation
				writer.printf("%04d\n", argument);
			}
			
			else if (entity instanceof ParseTree.PT_Label) //label encountered
				currentLine--;
				
			
			try
			{
			//ORG directive encountered
			if (entity instanceof ParseTree.PT_Directive_ORG)
			{
				writer.printf("%04d\n", Integer.parseInt(((PT_Directive_ORG) entity).getAddress()));
			}

			//DC directive encountered
			if (entity instanceof ParseTree.PT_Directive_DC)
			{
				writer.printf("%04d\n", Integer.parseInt(((PT_Directive_DC) entity).getValue()));
			}
			
			if (entity instanceof ParseTree.PT_Directive_DS)
			{
				//first, labelled address is written
				writer.printf("0000\n");
				
				//write "length" empty bytes
				for (int i = 1; i < Integer.parseInt(((PT_Directive_DS) entity).getLength());i++)
				{
					writer.print("0000\n");
				}
			}
			/*removed since EQU should not appear in the assembled file
			//EQU directive encountered
				if (entity instanceof ParseTree.PT_Directive_EQU)
				{
				writer.printf("%04d\n", Integer.parseInt(((PT_Directive_EQU) entity).getValue()));
				}
			
			*/
			}
			catch (NumberFormatException e)
			{
				throw new SyntaxException("Invalid argument for directive ", currentLine);
			}
		}
		writer.close();

	}
}
