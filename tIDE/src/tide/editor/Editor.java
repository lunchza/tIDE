/**
 * An IDE for t-language coded by Peter Pretorius. Integrates assembly, execution
 * and disassembly. Several public libraries and code snippets were used, the credit
 * is as follows:
 * 
 * JEditTextArea - JEdit's text area component that supports syntax highlighting
 * 				 - Copyright (C) 1999 Slava Pestov
 * 				 - many modifications made by Peter Pretorius
 * 
 * Various assembler components - written by COMP305 lecturer Graham Barbour,
 * 								  distributed for personal use
 */
package tide.editor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Scanner;

import org.syntax.jedit.*;
import org.syntax.jedit.tokenmarker.*;

import tide.assembler.*;
import tide.disassembler.*;
import tide.vm.*;

@SuppressWarnings("serial")
public class Editor extends JFrame implements ActionListener {

	//Version ID
	final static String VERSION = "0.1a";

	final static String title = "tIDE v" + VERSION;

	//Editor status messages
	final static String STATUS_READY = title.concat(" ready");
	final static String STATUS_SAVED = "File saved successfully as ";
	final static String STATUS_RUNNING = "VM running";
	final static String STATUS_STOPPED = "VM stopped";
	final static String STATUS_COMPILED = "Program compiled successfully";
	final static String STATUS_DISASSEMBLED = " decompiled successfully";
	final static String STATUS_RUNNING_ERROR = "Stop program execution first";
	static final String STATUS_FILE_NOT_FOUND = "The system cannot find the file specified";
	static final String STATUS_EXECUTION_EXCEPTION = "Error during execution. VM reports : ";

	//Determines if a program is running in the background
	private boolean running = false;

	//Determines if the current program has been compiled
	private boolean compiled = false;

	//The path to the current file. Needed when compiling
	String path;

	//The status bar for the editor
	JStatusBar statusBar;

	//Current editor status
	String currentStatus;

	//The text area for the editor. Supports syntax highlighting
	JEditTextArea textArea;

	//The toolbar that allows for quick opening, saving, compiling etc
	JToolBar toolBar;

	//Buttons for the toolbar
	JButton newButton, openButton, saveButton, saveAsButton, compileButton, runButton, disassembleButton;	

	//Components for the new file dialog
	JFrame f;
	JButton createButton, cancelButton;
	JTextField nameField;
	JComboBox<String> comboBox1;

	//The assembler associated with this editor
	private Assembler asm;
	//The disassembler associated with this editor
	private Disassembler dasm;
	//The virtual machine associated with this editor
	private TMachine TVM;

	public Editor()
	{
		super(title);
		//retrieve a Graphics Environment for calculating maximum screen size
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();  
		Rectangle gebounds = ge.getMaximumWindowBounds();
		//GrachicsEnvironment.getMaximumWindowBounds is used instead of ToolKit to
		//accommodate the height of the start menu (if one is present)
		setSize(gebounds.getSize());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());
		init();
		setVisible(true);
		textArea.requestFocus();
	}

	/**
	 * Initialises all components and key variables
	 */
	public void init()
	{
		toolBar = new JToolBar();
		toolBar.setFloatable(false);

		newButton = new JButton("New");
		newButton.addActionListener(this);
		openButton = new JButton("Open");
		openButton.addActionListener(this);
		saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		saveAsButton = new JButton("Save as");
		saveAsButton.addActionListener(this);
		compileButton = new JButton("Compile");
		compileButton.addActionListener(this);
		runButton = new JButton("Run");
		runButton.addActionListener(this);
		disassembleButton = new JButton("Disassemble...");
		disassembleButton.addActionListener(this);

		toolBar.add(newButton);
		toolBar.add(openButton);
		toolBar.add(saveButton);
		toolBar.add(saveAsButton);
		toolBar.add(compileButton);
		toolBar.add(runButton);
		toolBar.add(disassembleButton);

		statusBar = new JStatusBar(JStatusBar.SPLIT_ORIENTATION);


		textArea = new JEditTextArea("program.t");
		textArea.setTokenMarker(new TTokenMarker());

		//Change the title of the program so that the user knows which file is
		//being edited
		this.setTitle(title + " - " + textArea.getName());

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!textArea.isSaved())
				{
					int result = JOptionPane.showConfirmDialog(null, "File has not been saved. Would you like to save now?");

					if (result == JOptionPane.CANCEL_OPTION)
						return;

					else if (result == JOptionPane.YES_OPTION)
					{
						saveFile(true);
						System.exit(0);
					}

					else
						System.exit(0);
				}

				else
					System.exit(0);
			}
		});

		add(new JScrollPane(textArea), BorderLayout.CENTER);
		add(toolBar, BorderLayout.NORTH);
		add(statusBar, BorderLayout.SOUTH);

		path = "";
		currentStatus = STATUS_READY;


		//Starts a new thread for updating the line indicator in the status bar.
		//This thread polls the textArea 5 times every second getting updated
		//line numbers
		new Thread()
		{
			@Override
			public void run()
			{
				while(true)
				{
					updateStatus();
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public static void main(String[] args)
	{
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				new Editor();
			}
		});

	}

	/**
	 * Save file to system
	 * @param showDialog determines whether or not a save file dialog is shown
	 */
	void saveFile(boolean showDialog)
	{
		File f = null;
		
		if (path == null)
		{
			path = "";
			showDialog = true;
		}
		
		if (showDialog)
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Choose target file location");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"t source(.t)", "t");

			fileChooser.setFileFilter(filter);

			f = new File(textArea.getName());
			if(path != null)
				fileChooser.setCurrentDirectory(new File(path));

			fileChooser.setSelectedFile(f);

			if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				f = fileChooser.getSelectedFile();
				path = f.getAbsolutePath();
			}

			//cancel button clicked on save dialog
			else
				return;


			if (!f.getName().endsWith(".t"))
				f = new File(f.getAbsolutePath().concat(".t"));
		}

		else
		{
			try
			{
				f = new File(path);
			}
			catch(NullPointerException e)
			{
				saveFile(true);
			}
		}

		if (f.exists() && showDialog)
		{
			int result = JOptionPane.showConfirmDialog(null, "File exists. Overwrite?");

			if (result == JOptionPane.YES_OPTION)
			{
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(f, false);
					fos.write(textArea.getText().getBytes());
					fos.close();
				} catch (FileNotFoundException e) {
					currentStatus = STATUS_FILE_NOT_FOUND;
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			else
				return;
		}

		else 
		{
			FileOutputStream fos = null;
			try {
				f.createNewFile();
				fos = new FileOutputStream(f, false);
				fos.write(textArea.getText().getBytes());
				fos.close();
			} catch (IOException e) {
				saveFile(true);
				return;
			}
		}

		if (f != null)
		{
			//Update text area name to reflect the saved name
			textArea.setName(f.getName());

			//Update path and title to the new file
			path = f.getAbsolutePath();
			this.setTitle(title + " - " + textArea.getName());

			//File saved flag set
			textArea.setSaved(true);
			currentStatus = STATUS_SAVED.concat(path);
		}
	}

	/**
	 * Load the editor window with contents of a file
	 * @param f the file to be opened
	 */
	void openFile(File f)
	{
		StringBuilder inputText = null;
		Scanner sc  = null;

		try {
			sc = new Scanner(f);
			inputText = new StringBuilder();

			while (sc.hasNext())
			{
				inputText.append(sc.nextLine() + "\n");
			}

		} catch (FileNotFoundException e1) {
			currentStatus = STATUS_FILE_NOT_FOUND;
		}

		finally
		{
			if (sc != null)
				sc.close();
			else
				return;
		}

		//Update path
		path = f.getAbsolutePath();

		//Update text area name
		textArea.setName(f.getName());

		//Update title
		this.setTitle(title + " - " + textArea.getName());

		//Set the contents of the current text area to that of the opened file
		textArea.setText(inputText.toString());
		
		textArea.setSaved(true);

		currentStatus = STATUS_READY;
	}

	/**
	 * Start a VM to execute compiled code. The editor will largely remain
	 * functional while the VM is running. The VM runs in it's own thread.
	 */
	@SuppressWarnings("static-access")
	public void run()
	{
		runButton.setText("Stop");
		running = true;
		currentStatus = STATUS_RUNNING;

		if (TVM.console != null)
		{
			TVM.console.close();
		}

		String tbcPath = path.substring(0, path.lastIndexOf(".")).concat(".tbc");

		//Create a new VM for executing the compiled code
		TVM = new TMachine();
		try {
			TMachineProgram program = new TMachineProgram(tbcPath);
			//load program into VM memory
			TVM.load(program);
			new Thread()
			{
				@Override
				public void run()
				{
					
						try {
							//run VM
							TVM.run(asm.getNumVars());						
						} catch (IllegalOpcodeException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							currentStatus = STATUS_EXECUTION_EXCEPTION + e.getMessage();
							stopRunning();
							return;
						}
				}
			}.start();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedFileException e) {

			currentStatus = "The bytecode file appears to be invalid. Program cannot run";
			running = false;
			runButton.setText("Run");
			return;
		}
	}

	/**
	 * Stop VM execution if it's running
	 */
	public void stopRunning()
	{
		//stop running	
		running = false;
		runButton.setText("Run");

		if (TMachine.console != null)
			TMachine.console.close();

		if(!currentStatus.contains(STATUS_EXECUTION_EXCEPTION))
			currentStatus = STATUS_READY;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Can't perform this action while the VM is running
		if (running && e.getSource() != runButton)
		{
			currentStatus = STATUS_RUNNING_ERROR;
			return;
		}


		//If the file has not been saved
		if (e.getSource() != saveButton && e.getSource() != saveAsButton && e.getSource() != createButton && e.getSource() != cancelButton && !textArea.isSaved() &&!running)
		{
			int result = JOptionPane.showConfirmDialog(null, "File has not been saved. Would you like to save now?");

			if (result == JOptionPane.CANCEL_OPTION)
				return;

			else if (result == JOptionPane.YES_OPTION)
				saveFile(false);
		}

		//new button clicked
		if (e.getSource() == newButton)
		{
			setEnabled(false);
			createNewFileDialog();
			currentStatus = STATUS_READY;
		}

		//open button clicked
		if (e.getSource() == openButton)
		{
			File f = null;

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Choose target file location");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"t source(.t)", "t");

			fileChooser.setFileFilter(filter);

			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				f = fileChooser.getSelectedFile();
			}

			//Cancel button was clicked on the open file dialog
			if (f == null)
				return;

			//Load the contents of the selected file into the editor
			else
				openFile(f);
		}

		//save button clicked
		if (e.getSource() == saveButton)
		{
			saveFile(false);
		}

		//save as button clicked
		if (e.getSource() == saveAsButton)
		{
			saveFile(true);
		}

		//compiled button clicked
		if (e.getSource() == compileButton)
		{
			//simple safety check for empty files, saves time by not having to create
			//an assembler to scan an empty file
			if (textArea.getText().length() == 0 || textArea.getText().trim().equals(""))
			{
				currentStatus = "Empty file!";
				return;
			}
			
			//create an assembler object to assemble the file
			asm = new Assembler();
			FileInputStream source = null;

			try {
				source = new FileInputStream(path);

				//corresponding bytecode file resides in the same location as the source
				//and has the same filename but extension ".tbc"
				String destPath = path.substring(0, path.lastIndexOf(".")).concat(".tbc");

				FileOutputStream dest = new FileOutputStream(destPath);
				asm.assemble(source, dest);
				dest.close();
			} catch (FileNotFoundException fnfe) {
				currentStatus = "File needs to be saved in order to compile";
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (SyntaxException se) {
				//Syntax Error in program, fetch error message and set it to program status
				currentStatus =  se.getMessage();
				
				//The following few lines of code handles highlighting the line
				//that corresponds to the syntax error
				
				textArea.requestFocus();
				textArea.scrollTo(se.getLine()+1, 0);
				textArea.getPainter().setSelectionColor(Color.pink);
				int dot = -1;
				try {
					dot = textArea.getLineStartOffset(se.getLine() - 1);
				} catch (ArrayIndexOutOfBoundsException e2) {
					dot = 0;
				
				}
				textArea.setCaretPosition(dot);
				try {
				textArea.select(dot, dot+textArea.getLineLength(se.getLine()-1));
				} catch (ArrayIndexOutOfBoundsException e2) {
					//textArea.select(dot, dot+3);
				
				}
				return;
			}
			//compilation was successful
			compiled = true;
			currentStatus = STATUS_COMPILED;
		}

		//Run button was clicked
		if (e.getSource() == runButton)
		{
			if (running)
				stopRunning();

			else
			{
				if (path == null || path =="")
				{
					currentStatus = "No path to bytecode file. File does not appear to have been compiled.";
					return;
				}

				//Check if code is compiled, then execute corresponding .tbc file
				String tbcPath = path.substring(0, path.lastIndexOf(".")).concat(".tbc");

				@SuppressWarnings("unused")
				File tbcFile = new File(tbcPath);

				//File hasn't been compiled
				if (!compiled)
				{
					int result = JOptionPane.showConfirmDialog(null, "Program has not been compiled. Would you like to compile now?");

					if (result == JOptionPane.CANCEL_OPTION)
						return;

					else if (result == JOptionPane.YES_OPTION)
					{
						actionPerformed(new ActionEvent(compileButton, 3, ""));

						run();
					}

					else if (result == JOptionPane.NO_OPTION)
					{
						currentStatus = "Program must be compiled before executing";
					}
				}
				else
					run();
			}
		}

		//disassemble button clicked
		if (e.getSource() == disassembleButton)
		{
			File f = null;

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Choose target file location");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"t bytecode(.tbc)", "tbc");

			fileChooser.setFileFilter(filter);

			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				f = fileChooser.getSelectedFile();
			}

			//Cancel button was clicked on the open file dialog
			if (f == null)
				return;

			File tempSourceFile = null;
			String sourcePath = "";
			String filePath = "";
			try {
				//The path to the selected bytecode file
				filePath = f.getAbsolutePath();

				//Disassembler created for the target bytecode file
				dasm = new Disassembler(filePath);

				//Corresponding path for the source file, housed in the same directory as the bytecode file
				sourcePath = filePath.substring(0, filePath.lastIndexOf(".")).concat(".t");

				tempSourceFile = dasm.disassemble(sourcePath);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (SyntaxException e2) {
				currentStatus = e2.getMessage();
			}

			//File disassembly successful, load source file into editor
			openFile(tempSourceFile);

			this.setTitle(getTitle().concat(" (disassembled)"));
			currentStatus = filePath.concat(STATUS_DISASSEMBLED);
		}

		//create button (on new file dialog) was clicked
		if (e.getSource() == createButton)
		{
			String filename = nameField.getText();

			if(filename == null || filename == "")
				filename = "program";

			//strip any extension from the file. This is to prevent users
			//from creating filenames with erroneous extensions
			if (filename.contains("."))
				filename = filename.substring(0, filename.indexOf("."));

			//add appropriate extension
			if(!filename.endsWith(".t"))
				filename = filename.concat(".t");

			textArea.setName(filename);
			textArea.setText("");

			this.setTitle(title + " - " + textArea.getName());
			path = null;
			f.dispose();
			setEnabled(true);
		}

		if (e.getSource() == cancelButton)
		{
			f.dispose();
			setEnabled(true);
		}

		textArea.requestFocus();
	}

	//Updates the status bar with the current line that the caret is on, as well
	//as the total number of lines in the program. Caret position is offset by
	//one since lines in the editor start from 1 and not zero
	public void updateStatus()
	{
		if (textArea.isSaved())
			statusBar.setStatus(currentStatus, (textArea.getCaretLine()+1) + "  :  " + textArea.getLineCount() + "  ");

		else
			statusBar.setStatus(STATUS_READY, (textArea.getCaretLine()+1) + "  :  " + textArea.getLineCount() + "  ");
	}

	//This code generated by JGuiMaker v1.2b - coded by Peter Pretorius
	void createNewFileDialog()
	{
		f = new JFrame("Create file");
		//gui declarations
		String[] comboBox1Items = {"t source file"};
		comboBox1 = new JComboBox<String>(comboBox1Items);
		JLabel label1 = new JLabel("File type");
		JLabel label2 = new JLabel("File name");
		nameField = new JTextField();
		createButton = new JButton("Create");
		cancelButton = new JButton("Cancel");

		//gui component properties
		comboBox1.setBounds(83, 26, 103, 25);
		label1.setBounds(12, 26, 46, 25);
		label2.setBounds(12, 94, 54, 25);
		nameField.setBounds(83, 94, 103, 25);
		createButton.setBounds(12, 159, 75, 30);
		cancelButton.setBounds(111, 159, 75, 30);

		//event handling
		createButton.addActionListener(this);
		cancelButton.addActionListener(this);

		f.setSize(200,230);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.setResizable(false);
		f.setLayout(null);

		//adding gui components to the frame
		f.add(comboBox1);
		f.add(label1);
		f.add(label2);
		f.add(nameField);
		f.add(createButton);
		f.add(cancelButton);
		f.setVisible(true);
		f.requestFocus();     
	}

}
