package tide.vm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TMachineProgram {

	private List<String> code;
	private short startLocation;

	public TMachineProgram(String filepath) throws FileNotFoundException, MalformedFileException {
		File fileName = new File(filepath);
		Scanner scanner = new Scanner(fileName);
		if (!scanner.hasNext()) {
			throw new MalformedFileException();
		}
		String firstLine = scanner.nextLine();
		startLocation = Short.parseShort(firstLine);
		code = new ArrayList<String>();
		while(scanner.hasNext()){
			String line = scanner.nextLine();
			if (line.contains("/"))
				line = line.substring(0, line.indexOf("/")); // strip comments
			//code.add(Short.decode("0x" + line));	
			code.add(line);
		}
		scanner.close();
	}
	
	public short getStartLocation() {
		//startLocation.setValue(startLocation.getIntValue()-1);
		return startLocation;//TODO: was --startLocation
	}

	public List<String> getCode()  {
		return code;
	}

}
