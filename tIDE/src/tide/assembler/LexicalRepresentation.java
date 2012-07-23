package tide.assembler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The <code>LexicalRepresentation</code> of the T-ASM source,
 * is a list of <code>LexLine</code>s. 
 * 
 * Each <code>LexLine</code> consists of a label, mnemonic and argument.
 * The mnemonic is always non-null and has a checked legal value, i.e.,
 * one of the directives or operations. The label and argument may be <code>null</code>.
 * 
 * @author GDB
 *
 */
public class LexicalRepresentation {

	private List<LexLine> lines = new ArrayList<LexLine>();

	void appendLine(String label, String mnemonic, String argument) {
		lines.add(new LexLine(label, mnemonic, argument));
	}

	public List<LexLine> lines() {
		return Collections.unmodifiableList(lines);
	}
	
	/**
	 * 
	 * @author GDB
	 *
	 */
	public class LexLine {
		final String label;
		final String mnemonic;
		final String argument;
	
		LexLine(String label, String mnemonic, String argument) {
			this.label=label;
			this.mnemonic=mnemonic;
			this.argument=argument;
		}
		
		public String getLabel() {
			return label;
		}
	
		public String getMnemonic() {
			return mnemonic;
		}
	
		public String getArgument() {
			return argument;
		}
	}


}
	
	
	

