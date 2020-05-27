/******************************************************************************
 * Product: Compiere ERP & CRM Smart Business Solution                        *
 * Copyright (C) 1999-2007 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 3600 Bridge Parkway #102, Redwood City, CA 94065, USA      *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.compiere.apps;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import org.compiere.plaf.*;

/**
 *  Online Help Browser & Link.
 *
 *  @author     Jorg Janke
 *  @version    $Id: OnlineHelp.java,v 1.2 2006/07/30 00:51:27 jjanke Exp $
 */
public class OnlineHelp extends JEditorPane implements HyperlinkListener
{
	/** **/
	private static final long serialVersionUID = 1L;

	/**
	 *  Default Constructor
	 */
	public OnlineHelp()
	{
		super ();
		setEditable (false);
		setContentType ("text/html; charset=UTF-8");
		addHyperlinkListener (this);
	}   //  OnlineHelp

	/**
	 *  Constructor
	 *  @param url URL to load
	 */
	public OnlineHelp (String url)
	{
		this();
		try
		{
			if (url != null && url.length() > 0)
				setPage(url);
		}
		catch (Exception e)
		{
			System.err.println("OnlineHelp URL=" + url + " - " + e);
		}
	}   //  OnlineHelp

	/**
	 *  Constructor
	 *  @param loadOnline load online URL
	 */
	public OnlineHelp (boolean loadOnline)
	{
		this (loadOnline ? BASE_URL : null);
	}   //  OnlineHelp

	/** Base of Online Help System      */
	protected static final String   BASE_URL = "http://www.compiere.org/help/";

	
	/**************************************************************************
	 *	Hyperlink Listener
	 *  @param e event
	 */
	public void hyperlinkUpdate (HyperlinkEvent e)
	{
	//	System.out.println("OnlineHelp.hyperlinkUpdate - " + e.getDescription() + " " + e.getURL());
		if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
			return;

		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		//
		if (e instanceof HTMLFrameHyperlinkEvent)
		{
			HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
			HTMLDocument doc = (HTMLDocument)getDocument();
			doc.processHTMLFrameHyperlinkEvent(evt);
		}
		else if (e.getURL() == null)
			//	remove # of the reference
			scrollToReference(e.getDescription().substring(1));
		else
		{
			try
			{
				setPage(e.getURL());
			}
			catch (Throwable t)
			{
				System.err.println("Help.hyperlinkUpdate - " + t.toString());
				displayError("Error", e.getURL(), t);
			}
		}
		this.setCursor(Cursor.getDefaultCursor());
	}	//	hyperlinkUpdate

	/**
	 *  Set Text
	 *  @param text text
	 */
	@Override
	public void setText (String text)
	{
		setBackground (CompierePLAF.getInfoBackground());
		Document doc = getDocument();
		doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
		super.setText(text);
		setCaretPosition(0);        //  scroll to top
	}   //  setText

	/**
	 *  Load URL async
	 *  @param url url
	 */
	@Override
	public void setPage (final URL url)
	{
		setBackground (Color.white);
		Runnable pgm = new Runnable()
		{
			public void run()
			{
				loadPage(url);
			}
		};
		new Thread(pgm).start();
	}   //  setPage

	/**
	 *  Load Page Async
	 *  @param url url
	 */
	void loadPage (URL url)
	{
		try
		{
			super.setPage(url);
		}
		catch (Exception e)
		{
			displayError("Error: URL not found", url, e);
		}
	}   //  loadPage

	/**
	 *  Display Error message
	 *  @param header header
	 *  @param url url
	 *  @param exception exception
	 */
	protected void displayError (String header, Object url, Object exception)
	{
		StringBuffer msg = new StringBuffer ("<HTML><BODY>");
		msg.append("<H1>").append(header).append("</H1>")
			.append("<H3>URL=").append(url).append("</H3>")
			.append("<H3>Error=").append(exception).append("</H3>")
			.append("<p>&copy;&nbsp;Compiere &nbsp; ")
			.append("<A HREF=\"").append(BASE_URL).append ("\">Online Help</A></p>")
			.append("</BODY></HTML>");
		setText(msg.toString());
	}   //  displayError

	/*************************************************************************/

	/** Online links.
	 *  Key=AD_Window_ID (as String) - Value=URL
	 */
	private static HashMap<String,String>	s_links = new HashMap<String,String>();
	static
	{
		new Worker (BASE_URL, s_links).start();
	}

	/**
	 *  Is Online Help available.
	 *  @return true if available
	 */
	public static boolean isAvailable()
	{
		return s_links.size() != 0;
	}   //  isAvailable

}   //  OnlineHelp

/**
 *  Online Help Worker
 */
class Worker extends Thread
{
	/**
	 *  Worker Constructor
	 *  @param urlString url
	 *  @param links links
	 */
	Worker (String urlString, HashMap<String,String> links)
	{
		m_urlString = urlString;
		m_links = links;
		setPriority(Thread.MIN_PRIORITY);
	}   //  Worker

	private String      m_urlString = null;
	private HashMap<String,String>     m_links = null;

	/**
	 *  Worker: Read available Online Help Pages
	 */
	@Override
	public void run()
	{
		if (m_links == null)
			return;
		URL url = null;
		try
		{
			url = new URL (m_urlString);
		}
		catch (Exception e)
		{
			System.err.println("OnlineHelp.Worker.run (url) - " + e);
		}
		if (url == null)
			return;

		//  Read Reference Page
		try
		{
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
			HTMLEditorKit kit = new HTMLEditorKit();
			HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
			doc.putProperty("IgnoreCharsetDirective", Boolean.valueOf(true));
			kit.read (new InputStreamReader(is), doc, 0);
			if (false)
				dumpTags(doc, HTML.Tag.A);

			//  Get The Links to the Help Pages
			HTMLDocument.Iterator it = doc.getIterator(HTML.Tag.A);
			Object target = null;
			Object href = null;
			while (it != null && it.isValid())
			{
				AttributeSet as = it.getAttributes();
				//	~ href=/help/100/index.html target=Online title=My Test
			//	System.out.println("~ " + as);

				//  key keys
				if (target == null || href == null)
				{
					Enumeration<?> en = as.getAttributeNames();
					while (en.hasMoreElements())
					{
						Object o = en.nextElement();
						if (target == null && o.toString().equals("target"))
							target = o;		//	javax.swing.text.html.HTML$Attribute
						else if (href == null && o.toString().equals("href"))
							href = o;
					}
				}

				if (target != null && "Online".equals(as.getAttribute(target)))
				{
					//  Format: /help/<AD_Window_ID>/index.html
					String hrefString = (String)as.getAttribute(href);
					if (hrefString != null)
					{
						try
						{
					//		System.err.println(hrefString);
							String AD_Window_ID = hrefString.substring(hrefString.indexOf('/',1), hrefString.lastIndexOf('/'));
							m_links.put(AD_Window_ID, hrefString);
						}
						catch (Exception e)
						{
							System.err.println("OnlineHelp.Worker.run (help) - " + e);
						}
					}
				}
				it.next();
			}
			is.close();
		}
		catch (ConnectException e)
		{
		//	System.err.println("OnlineHelp.Worker.run URL=" + url + " - " + e);
		}
		catch (UnknownHostException uhe)
		{
		//	System.err.println("OnlineHelp.Worker.run " + uhe);
		}
		catch (ProtocolException pe)
		{
		//	System.err.println("OnlineHelp.Worker.run " + pe);
		}
		catch (Exception e)
		{
			System.err.println("OnlineHelp.Worker.run (e) " + e);
		//	e.printStackTrace();
		}
		catch (Throwable t)
		{
			System.err.println("OnlineHelp.Worker.run (t) " + t);
		//	t.printStackTrace();
		}
	//	System.out.println("OnlineHelp - Links=" + m_links.size());
	}   //  run

	/**
	 * 	Diagnostics
	 * 	@param doc html document
	 * 	@param tag html tag
	 */
	private void dumpTags (HTMLDocument doc, HTML.Tag tag)
	{
		System.out.println("Doc=" + doc.getBase() + ", Tag=" + tag);
		HTMLDocument.Iterator it = doc.getIterator(tag);
		while (it != null && it.isValid())
		{
			AttributeSet as = it.getAttributes();
			System.out.println("~ " + as);
			it.next();
		}
	}	//	printTags

}   //  Worker
