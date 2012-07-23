/**
 * @author Peter Pretorius
 * This is a simple program that I wrote to emulate the "status bar" panel inherently
 * present in other languages and windows in particular. The status is delivered by means
 * of a JLabel.
 */

package tide.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JStatusBar extends JPanel {
	private JLabel message1, message2;
	public static final int LEFT_ORIENTATION = 0;
	public static final int RIGHT_ORIENTATION = 1;
	public static final int SPLIT_ORIENTATION = 2;
	
	private int orientation;
	
	public JStatusBar(int orientation)
	{
		this.orientation = orientation;
		setOrientation(orientation);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		message1 = new JLabel("");
		message2 = new JLabel("");
		
		if (orientation == SPLIT_ORIENTATION)
		{
			message1.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(message1, BorderLayout.WEST);
			
			message2.setAlignmentX(Component.RIGHT_ALIGNMENT);
			add(message2, BorderLayout.EAST);
		}
		else
			add(message1);
		
	}
	
	public JStatusBar(String m, int orientation)
	{
		this.orientation = orientation;
		setOrientation(orientation);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		message1 = new JLabel(m);
		message2 = new JLabel(m);
		if (orientation == SPLIT_ORIENTATION)
		{
			message1.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(message1, BorderLayout.WEST);
			
			message2.setAlignmentX(Component.RIGHT_ALIGNMENT);
			add(message2, BorderLayout.EAST);
		}
		else
			add(message1);
	}
	
	public void setStatus(String s)
	{
		message1.setText(s);
	}
	
	public void setStatus(String s1, String s2)
	{
		if (orientation != SPLIT_ORIENTATION)
		{
			setStatus(s1);
		}
		else
		{
			message1.setText(s1);
			message2.setText(s2);
		}
			
	}
	
	public void setOrientation(int orientation)
	{
		switch(orientation)
		{
		case LEFT_ORIENTATION:
			setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			break;
		case RIGHT_ORIENTATION:
			setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
			break;
		case SPLIT_ORIENTATION:
			setLayout(new BorderLayout());
			break;
		}
	}

}
