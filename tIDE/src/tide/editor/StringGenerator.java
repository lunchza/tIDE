/**
 * This class is currently independent of the editor, but may be integrated
 * at some point. It is a tool for generating subroutines that do String output
 * in t-language (for prompts etc), as strings are currently not supported very well 
 */

package tide.editor;

import java.util.Scanner;

public class StringGenerator {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		System.out.print("Enter String to convert :");
		String input = new Scanner(System.in).nextLine();
		System.out.println("----------");
		for (int i = 0; i < input.length(); i++)
		{
			char c = input.charAt(i);
			
			System.out.println("LDI " + (int)c + "\t;" + c);
			System.out.println("OTA");
		}

	}

}
