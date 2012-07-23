package tide.vm;

public class Memory {
	
	int SIZE = 65536; //64kb memory
	String[] memory = new String[SIZE];
	
	public Memory()
	{
		for (int i = 0; i < SIZE; i++)
			memory[i] = "EMPTY";
	}
	
public void load(int address, short value) {
	if(address < 0)
		address += SIZE;

	memory[address] = ""+value;
		
	}

	public void load(int address, String value) {
		if(address < 0)
			address += SIZE;

		memory[address] = value;
	}

	public String fetch(int address) {
		if(address < 0)
			address += SIZE;
		return memory[address];
	}
	
	public String dump()
	{
		StringBuilder sb = new StringBuilder();
		int line = 0;
		for (int i = 0; i < memory.length; i++)
			if(!memory[i].equals("EMPTY"))
				sb.append(line++ + " " + memory[i] + "\n");
			else
				line++;
		
		return ("***MEMORY DUMP***\n" + sb.toString());
	}

	

}