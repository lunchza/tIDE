package tide.assembler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Scanner;

import tide.assembler.Grammar.GR_Directive;
import tide.assembler.Grammar.GR_Operation;

/**
 * The <code>Lexer</code> parses the T-ASM source 
 * and produces the <code>LexicalRepresentation</code>.
 * 
 * 
 * @author GDB
 *
 */
public class Lexer {

	private LexicalRepresentation ir = null;
	private int currentLine = 0;
	
	public LexicalRepresentation parse(final InputStream source) throws IOException,
			SyntaxException {
		
		// This should NEVER occurr! So just checking!
		if (ir!=null) throw new IllegalStateException();
		
		ir = new LexicalRepresentation();
		
		// The source is line-based, so we take advantage of this.
		//
		LineNumberReader lineReader = null;
		try {

			lineReader = new LineNumberReader(new InputStreamReader(source));

			while (lineReader.ready()) {
				String line = lineReader.readLine();
				
				currentLine++;
				parseLine(line);
			}

			return ir;

		} finally {

			lineReader.close();

		}

	}


	@SuppressWarnings("unused")
	private void parseLine(final String line) throws SyntaxException {

		
		final String label;
		final String mnemonic;
		final String argument;
		final String comment;
		
		
		final Scanner s = new Scanner(line);
		if (!s.hasNext()) {
			label = " ";
			mnemonic = null;
			argument = null;
			comment = null;
			// Blank line
		} else {
			// Non-blank line

			String first_token = s.next();

			if (tokenIsComment(first_token)) {
				// Comment-only line
				label = " "; //slight hack, adds a blank line entity to the parse tree to fix line number reporting
				mnemonic = null;
				argument = null;
				comment = first_token;
			} else {
				// First token is not a comment.
				if (tokenIsMnemonic(first_token)) {
					// First token is a mnemonic, hence no preceding label.
					label = null;
					mnemonic = first_token;						
				} else {
					// First token MUST be a label, and hence
					// the next token MUST be a mnemonic.
					if (!tokenIsLabel(first_token)) {
						throw new SyntaxException();
					} else {
						// The first-token is indeed a legal label.
						label = first_token;
						// The next-token MUST be a mnemonic
						if (!s.hasNext()) {
							throw new SyntaxException("Unrecognised symbol \'" + label + "\'", currentLine);
						}
						String second_token = s.next();
						if (!tokenIsMnemonic(second_token)) {
							throw new SyntaxException("Error on token " + first_token + ". Possibly missing space between label and mnemonic ", currentLine);
						}
						// The second_token is indeed a mnemonic
						mnemonic = second_token;
					}

				}
				
				// We turn to the third-token.
				// Three cases arise.

				if (!s.hasNext()) {
					// CASE 1: No third token, hence no argument and no
					// comment;
					argument = null;
					comment = null;
				} else {
					// There is a third token.
					String third_token = s.next();
					// This is either a argument or a comment;
					if (tokenIsComment(third_token)) {
						// Third token if comment, hence no argument;
						argument = null;
						comment = third_token;
					} else {
						// Third token is not a comment, hence is an
						// argument.
						argument = third_token;
						// If there is a fourth token it must be comment!
						if (s.hasNext()) {
							String fourth_token = s.next();
							if (tokenIsComment(fourth_token)) {
								comment = fourth_token;
							} else {
								// There is a forth token but it is not a
								// comment!
								throw new SyntaxException();
							}
						} else {
							// No forth token
							comment = null;
						}
					}

				}
			}
		}
		
		// So we have a "fairly" well-formed line.
		
		// We should perform more syntax checking.
		
		if (label==null && mnemonic==null && argument==null) {
			// IGNORE
			return;
		}
		
		// Meaningful line.
		// Must have mnemonic. However blank line will have an empty label
		if (label != null && !label.equals(" ") && mnemonic==null) {
			throw new SyntaxException();
		}
		
		// Has mnemonic.
		// 
		
		ir.appendLine(label,mnemonic,argument);
	}
	
	/**
	 * Is this token a legal label?
	 * 
	 * @param token
	 * @return
	 */
	private static boolean tokenIsLabel(final String token) {
		if (tokenIsComment(token) || tokenIsMnemonic(token)) {
			return false;
		}
		// For now permit any other form, why not?
		return true;
	}

	/**
	 * Is this token a legal mnemonic?
	 * @param token
	 * @return
	 */
	private static boolean tokenIsMnemonic(final String token) {
		return GR_Directive.isDirective(token) || GR_Operation.isOperation(token);
	}

	/**
	 * Is this token a legal comment?
	 * 
	 * @param token
	 * @return
	 */
	private static boolean tokenIsComment(final String token) {
		return token.startsWith(";");
	}
	
	
}
