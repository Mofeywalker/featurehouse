

package org.gjt.sp.jedit.textarea;

import org.gjt.sp.jedit.Debug;
import org.gjt.sp.util.Log;


class FirstLine extends Anchor
{
	
	int skew;

	
	FirstLine(DisplayManager displayManager,
		TextArea textArea)
	{
		super(displayManager,textArea);
	} 

	
	@Override
	public void changed()
	{
		
		if(Debug.SCROLL_DEBUG)
		{
			Log.log(Log.DEBUG,this,"changed() before: "
				+ physicalLine + ':' + scrollLine
				+ ':' + skew);
		} 

		ensurePhysicalLineIsVisible();

		int screenLines = displayManager
			.getScreenLineCount(physicalLine);
		if(skew >= screenLines)
			skew = screenLines - 1;

		
		if(Debug.SCROLL_VERIFY)
		{
			System.err.println("SCROLL_VERIFY");
			int verifyScrollLine = 0;

			for(int i = 0; i < displayManager.getBuffer()
				.getLineCount(); i++)
			{
				if(!displayManager.isLineVisible(i))
					continue;

				if(i >= physicalLine)
					break;

				verifyScrollLine += displayManager
					.getScreenLineCount(i);
			}

			if(verifyScrollLine != scrollLine)
			{
				Exception ex = new Exception(scrollLine + ":" + verifyScrollLine);
				Log.log(Log.ERROR,this,ex);
			}
		}

		if(Debug.SCROLL_DEBUG)
		{
			Log.log(Log.DEBUG,this,"changed() after: "
				+ physicalLine + ':' + scrollLine
				+ ':' + skew);
		} 
	} 

	
	@Override
	public void reset()
	{
		if(Debug.SCROLL_DEBUG)
			Log.log(Log.DEBUG,this,"reset()");

		int oldPhysicalLine = physicalLine;
		physicalLine = 0;
		scrollLine = 0;

		int i = displayManager.getFirstVisibleLine();

		for(;;)
		{
			if(i >= oldPhysicalLine)
				break;

			scrollLine += displayManager.getScreenLineCount(i);

			int nextLine = displayManager.getNextVisibleLine(i);
			if(nextLine == -1)
				break;
			else
				i = nextLine;
		}

		physicalLine = i;

		int screenLines = displayManager.getScreenLineCount(physicalLine);
		if(skew >= screenLines)
			skew = screenLines - 1;

		textArea.updateScrollBar();
	} 

	
	
	void physDown(int amount, int screenAmount)
	{
		if(Debug.SCROLL_DEBUG)
		{
			Log.log(Log.DEBUG,this,"physDown() start: "
				+ physicalLine + ':' + scrollLine);
		}

		skew = 0;

		if(!displayManager.isLineVisible(physicalLine))
		{
			int lastVisibleLine = displayManager.getLastVisibleLine();
			if(physicalLine > lastVisibleLine)
				physicalLine = lastVisibleLine;
			else
			{
				int nextPhysicalLine = displayManager.getNextVisibleLine(physicalLine);
				amount -= nextPhysicalLine - physicalLine;
				scrollLine += displayManager.getScreenLineCount(physicalLine);
				physicalLine = nextPhysicalLine;
			}
		}

		for(;;)
		{
			int nextPhysicalLine = displayManager.getNextVisibleLine(
				physicalLine);
			if(nextPhysicalLine == -1)
				break;
			else if(nextPhysicalLine > physicalLine + amount)
				break;
			else
			{
				scrollLine += displayManager.getScreenLineCount(physicalLine);
				amount -= nextPhysicalLine - physicalLine;
				physicalLine = nextPhysicalLine;
			}
		}

		if(Debug.SCROLL_DEBUG)
		{
			Log.log(Log.DEBUG,this,"physDown() end: "
				+ physicalLine + ':' + scrollLine);
		}

		callChanged = true;

		
		
		if(screenAmount < 0)
			scrollUp(-screenAmount);
		else if(screenAmount > 0)
			scrollDown(screenAmount);
	} 

	
	
	void physUp(int amount, int screenAmount)
	{
		if(Debug.SCROLL_DEBUG)
		{
			Log.log(Log.DEBUG,this,"physUp() start: "
				+ physicalLine + ':' + scrollLine);
		}

		skew = 0;

		if(!displayManager.isLineVisible(physicalLine))
		{
			int firstVisibleLine = displayManager.getFirstVisibleLine();
			if(physicalLine < firstVisibleLine)
				physicalLine = firstVisibleLine;
			else
			{
				int prevPhysicalLine = displayManager.getPrevVisibleLine(physicalLine);
				amount -= physicalLine - prevPhysicalLine;
			}
		}

		for(;;)
		{
			int prevPhysicalLine = displayManager.getPrevVisibleLine(
				physicalLine);
			if(prevPhysicalLine == -1)
				break;
			else if(prevPhysicalLine < physicalLine - amount)
				break;
			else
			{
				amount -= physicalLine - prevPhysicalLine;
				physicalLine = prevPhysicalLine;
				scrollLine -= displayManager.getScreenLineCount(
					prevPhysicalLine);
			}
		}

		if(Debug.SCROLL_DEBUG)
		{
			Log.log(Log.DEBUG,this,"physUp() end: "
				+ physicalLine + ':' + scrollLine);
		}

		callChanged = true;

		
		
		if(screenAmount < 0)
			scrollUp(-screenAmount);
		else if(screenAmount > 0)
			scrollDown(screenAmount);
	} 

	
	
	void scrollDown(int amount)
	{
		if(Debug.SCROLL_DEBUG)
			Log.log(Log.DEBUG,this,"scrollDown()");

		ensurePhysicalLineIsVisible();

		amount += skew;

		skew = 0;

		while(amount > 0)
		{
			int screenLines = displayManager.getScreenLineCount(physicalLine);
			if(amount < screenLines)
			{
				skew = amount;
				break;
			}
			else
			{
				int nextLine = displayManager.getNextVisibleLine(physicalLine);
				if(nextLine == -1)
					break;
				boolean visible = displayManager.isLineVisible(physicalLine);
				physicalLine = nextLine;
				if(visible)
				{
					amount -= screenLines;
					scrollLine += screenLines;
				}
			}
		}

		callChanged = true;
	} 

	
	
	void scrollUp(int amount)
	{
		if(Debug.SCROLL_DEBUG)
			Log.log(Log.DEBUG,this,"scrollUp() before:" + this);

		ensurePhysicalLineIsVisible();

		if(amount <= skew)
		{
			
			
			skew -= amount;
		}
		else
		{
			
			amount -= skew;
			skew = 0;

			while(amount > 0)
			{
				int prevLine = displayManager.getPrevVisibleLine(physicalLine);
				if(prevLine == -1)
					break;
				
				physicalLine = prevLine;

				int screenLines = displayManager.getScreenLineCount(physicalLine);
				scrollLine -= screenLines;
				if(amount < screenLines)
				{
					skew = screenLines - amount;
					break;
				}
				else
					amount -= screenLines;
			}
		}

		if(Debug.SCROLL_DEBUG)
			Log.log(Log.DEBUG,this,"scrollUp() after:" + this);
		callChanged = true;
	} 

	
	void ensurePhysicalLineIsVisible()
	{
		if(!displayManager.isLineVisible(physicalLine))
		{
			if(physicalLine > displayManager.getLastVisibleLine())
			{
				physicalLine = displayManager.getLastVisibleLine();
				scrollLine = displayManager.getScrollLineCount() - 1;
			}
			else if(physicalLine < displayManager.getFirstVisibleLine())
			{
				physicalLine = displayManager.getFirstVisibleLine();
				scrollLine = 0;
			}
			else
			{
				physicalLine = displayManager.getNextVisibleLine(physicalLine);
				scrollLine += displayManager.getScreenLineCount(physicalLine);
			}
		}
	} 

	
	@Override
	public String toString()
	{
		return "FirstLine["+physicalLine+','+scrollLine+','+skew+']';
	} 
}