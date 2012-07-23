/**
 * Written by Peter Pretorius
 * Unlimited for now (why not?) but possibly could change with a maximum limit, after
 * which the first half of the clipboard is discarded.
 */
package org.syntax.jedit;

import java.util.ArrayList;

public class UnlimitedClipboard {

	ArrayList<String> clipboard;
	int position;
	
	public UnlimitedClipboard()
	{
		position = 0;
		clipboard = new ArrayList<String>();
	}
	
	public void update(String s)
	{
		clipboard.add(s);
		position = clipboard.size();
	}
	
	public String undo()
	{
		if(position == 0)
		{
			String first = clipboard.get(position);
			clipboard = new ArrayList<String>();
			return first;
		}
		return clipboard.get(--position);
	}
	
	public boolean isEmpty()
	{
		return clipboard.isEmpty();
	}
	
	public String redo()
	{
		if(position < clipboard.size())
		{
			return clipboard.get(position++);
		}
		
		else return null;
	}
	
}
