package tide.vm;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class Console extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3067189502459737472L;

	JTextArea console;
	long startTime, endTime;
	int charCount;

	public Console()
	{
		super("TVM Console");
		console = new JTextArea("TVM v0.01a - coded by Peter Pretorius\n---------------------------------------------------\n");
		console.setLineWrap(true);
		addWindowListener(new WindowListener()
		{

			@Override
			public void windowActivated(WindowEvent e) {

			}

			@Override
			public void windowClosed(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {
				JOptionPane.showMessageDialog(null, "Use the stop button to close the console.");

			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {

			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowOpened(WindowEvent e) {

			}

		});
		charCount = 0;

		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		startTime = System.currentTimeMillis();
		setSize(500, 500);
		add(new JScrollPane(console));
		console.setEditable(false);
		setVisible(true);
	}

	Short readShort()
	{
		String line = JOptionPane.showInputDialog(null, "Console is requesting input.");

		append(Short.parseShort(line));

		return Short.parseShort(line);
	}

	char readChar()
	{
		String line = JOptionPane.showInputDialog(null, "Console is requesting input.");

		append(""+line.charAt(0));

		return line.charAt(0);

	}

	void write(Short s)
	{
		append(s);
		charCount++;
	}

	void write(String s)
	{
		append(s);
		charCount++;
	}

	void append(Short s)
	{
		console.setText(console.getText().concat(s.toString()));
		repaint();
	}

	void append(String s)
	{
		console.setText(console.getText().concat(s.toString()));
		repaint();
	}

	public void end()
	{
		endTime = System.currentTimeMillis();
		long executionTime = endTime-startTime;
		append("\nExecution finished in " + (float)executionTime/1000 + " seconds.");
	}

	public void close()
	{
		dispose();
	}

	public int getCharCount()
	{
		return charCount;
	}
}
