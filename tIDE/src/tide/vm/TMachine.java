package tide.vm;

import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.List;

public class TMachine {

	private Memory memory;
	private short PC;
	private short R_SP;
	private short R_A;
	private short R_X;
	private boolean F_Z = false;
	private boolean F_P = false;
	public static Console console;

	boolean finished;


	public TMachine()
	{
		R_A = 0000;
		R_X = 0000;
		R_SP = 0000;
		memory = new Memory();
	}

	public void load(TMachineProgram program) throws FileNotFoundException {

		PC = program.getStartLocation();
		short currentAddress = PC;
		List<String> code = program.getCode();
		for (String word : code) {
			memory.load(currentAddress, word);
			currentAddress++;
		}
	}

	public void run(int numVars) throws IllegalOpcodeException, ExecutionException {
		finished=false;
		console = new Console();

		//memory.dump();
	
		//Skip the data segment
		for (int i = 0; i < numVars; i++)
			PC++;

		while (!finished) {
			short currentOpcode;
			try
			{
			 currentOpcode = Short.decode("0x" + memory.fetch(PC));
			}
			catch (NumberFormatException e)
			{
				finished = true;
				throw new ExecutionException("Illegal memory reference. Possibly missing HLT operation");
			}
			short arg;
			try
			{
				switch (currentOpcode) {
				case 0:
					//NOP
					break;
				case 1:
					//CLA
					R_A = 0000;
					break;
				case 2:
					//CLX
					R_X = 0000;
					break;
				case 3:
					//INC
					R_A++;
					if(R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 4:
					//DEC
					R_A--;
					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 5:
					//INX
					R_X++;
					if (R_X < 0)
						F_P = false;
					else
						F_P = true;

					if (R_X == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 6:
					//DEX
					R_X--;
					if (R_X < 0)
						F_P = false;
					else
						F_P = true;

					if (R_X == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 7:
					//TAX
					R_X = R_A;
					break;
				case 8:
					//INI
					R_A = console.readShort();

					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 9:
					//INA
					char c = console.readChar();
					R_A = (short)c;

					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 10:
					//OTI
					console.write(R_A);
					break;
				case 11:
					//OTA
					console.write(""+(char)R_A);
					if (R_A > 127)
						System.out.println();
					break;
				case 12:
					//PSH
					R_SP--;
					memory.load(R_SP, R_A);
					break;
				case 13:
					//POP
					R_A = Short.parseShort(memory.fetch(R_SP));
					R_SP++;

					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 14:
					//RET
					PC = Short.parseShort(memory.fetch(R_SP));
					R_SP++;
					break;
				case 15: // HLT
					finished=true;
					break;
				case 16:
					//LDA
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					R_A = Short.parseShort(memory.fetch(arg));

					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 17:
					//LDX
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					R_A = Short.parseShort(memory.fetch(arg + R_X));

					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 18:
					//LDI
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					R_A = arg;

					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 19:
					//STA
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					memory.load(arg, R_A);
					break;
				case 20:
					//STX
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					memory.load(arg+R_X, R_A);
					break;
				case 21:
					//ADD
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					R_A += Short.parseShort(memory.fetch(arg));
					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 22:
					//ADX
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					R_A += Short.parseShort(memory.fetch(arg + R_X));

					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 23:
					//ADI
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					R_A += arg;

					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 24:
					//SUB
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					R_A -= Short.parseShort(memory.fetch(arg));

					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 25:
					//SBX
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					R_A -= Short.parseShort("0x"+memory.fetch(arg + R_X));

					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 26:
					//SBI
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					R_A -= arg;

					if (R_A < 0)
						F_P = false;

					else
						F_P = true;

					if (R_A == 0)
						F_Z = true;

					else
						F_Z = false;
					break;
				case 27:
					//CMP
					PC++;
					arg = Short.parseShort(memory.fetch(PC));

					if (R_A - Short.parseShort(memory.fetch(arg)) == 0)
						F_Z = true;
					else
						F_Z = false;

					if (R_A - Short.parseShort(memory.fetch(arg)) < 0)
						F_P = false;
					else
						F_P = true;
					break;
				case 28:
					//CPX
					PC++;
					arg = Short.parseShort(memory.fetch(PC));

					if (R_A - Short.parseShort(memory.fetch(arg)) == 0)
						F_Z = true;
					else
						F_Z = false;

					if (R_A - Short.parseShort(memory.fetch(arg)) < 0)
						F_P = false;
					else
						F_P = true;
					break;
				case 29:
					//CPI
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					if (R_A - arg == 0)
						F_Z = true;
					else
						F_Z = false;

					if (R_A - arg < 0)
						F_P = false;
					else
						F_P = true;
					break;
				case 30:
					//LSP
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					R_SP = Short.parseShort(memory.fetch(arg));
					break;
				case 31:
					//LSI
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					R_SP = arg;
					break;
				case 32:
					//BRN
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					PC = (short) (arg-1);
					break;
				case 33:
					//BZE
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					if (F_Z)
						PC = (short) (arg-1);
					break;
				case 34:
					//BNZ
					PC++;			
					arg = Short.parseShort(memory.fetch(PC));
					if (!F_Z)
						PC = (short) (arg-1);

					break;
					
				case 35:
					//BPZ
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					if (F_P)
						PC = (short) (arg-1);
					break;
				case 36:
					//BNG
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					if (!F_P)
						PC = (short) (arg-1);
					break;
				case 37:
					//JSR
					PC++;
					arg = Short.parseShort(memory.fetch(PC));
					short currentAddress = PC;
					PC = (short) (arg-1);
					//address offset by 1 so that the method doesn't keep calling itself
					memory.load(--R_SP, currentAddress);
					break;

				default:
					throw new IllegalOpcodeException(currentOpcode);
				}

				if (console.getCharCount() > 3000)
					throw new OverFlowException();
				PC++;
			}

			catch (InputMismatchException IME)
			{
				finished = true;
				throw new ExecutionException("Unexpected input - 16 bit number expected");
			}

			catch (IllegalOpcodeException IOE)
			{
				finished = true;
				throw new ExecutionException("Unsupported opcode " + currentOpcode + " at address " + PC);
			}

			catch (NumberFormatException NFE)
			{
				finished = true;
				throw new ExecutionException("Unable to decode input");
			} 
			catch (OverFlowException e)
			{
				finished = true;
				throw new ExecutionException("Out of memory!");
			}

		}
		finished = true;
		console.end();
	}

	public boolean isFinished() {
		return finished;
	}
}
