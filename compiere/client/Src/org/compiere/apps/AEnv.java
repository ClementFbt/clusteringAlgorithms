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
import java.awt.event.*;
import java.io.*;
import java.rmi.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

import javax.swing.*;

import org.compiere.db.*;
import org.compiere.framework.*;
import org.compiere.interfaces.*;
import org.compiere.model.*;
import org.compiere.swing.*;
import org.compiere.util.*;

/**
 *  Windows Application Environment and utilities
 *
 *  @author 	Jorg Janke
 *  @version 	$Id: AEnv.java,v 1.2 2006/07/30 00:51:27 jjanke Exp $
 */
public final class AEnv
{
	/**
	 *  Show in the center of the screen.
	 *  (pack, set location and set visibility)
	 * 	@param window Window to position
	 */
	public static void showCenterScreen(Window window)
	{
		positionCenterScreen(window);
		window.setVisible(true);
		window.toFront();
	}   //  showCenterScreen

	/**
	 *	Position window in center of the screen
	 * 	@param window Window to position
	 */
	public static void positionCenterScreen(Window window)
	{
		positionScreen (window, SwingConstants.CENTER);
	}	//	positionCenterScreen

	/**
	 *  Show in the center of the screen.
	 *  (pack, set location and set visibility)
	 * 	@param window Window to position
	 * 	@param position SwingConstants
	 */
	public static void showScreen(Window window, int position)
	{
		positionScreen(window, position);
		window.setVisible(true);
		window.toFront();
	}   //  showScreen


	/**
	 *	Position window in center of the screen
	 * 	@param window Window to position
	 * 	@param position SwingConstants
	 */
	public static void positionScreen (Window window, int position)
	{
		window.pack();
		Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension wSize = window.getSize();
		int maxWidth = (int)(sSize.width*.97);
		int maxHeight = (int)(sSize.height*.97);
		//	fit on window
		if (wSize.height > maxHeight)
			wSize.height = maxHeight;
		if (wSize.width > maxWidth)
			wSize.width = maxWidth;
		window.setSize(wSize);
		//	Center
		int x = (sSize.width - wSize.width) / 2;
		int y = (sSize.height - wSize.height) / 2;
		if (position == SwingConstants.CENTER)
			;
		else if (position == SwingConstants.NORTH_WEST)
		{
			x = 0;
			y = 0;
		}
		else if (position == SwingConstants.NORTH)
		{
			y = 0;
		}
		else if (position == SwingConstants.NORTH_EAST)
		{
			x = (sSize.width - wSize.width);
			y = 0;
		}
		else if (position == SwingConstants.WEST)
		{
			x = 0;
		}
		else if (position == SwingConstants.EAST)
		{
			x = (sSize.width - wSize.width);
		}
		else if (position == SwingConstants.SOUTH)
		{
			y = (sSize.height - wSize.height);
		}
		else if (position == SwingConstants.SOUTH_WEST)
		{
			x = 0;
			y = (sSize.height - wSize.height);
		}
		else if (position == SwingConstants.SOUTH_EAST)
		{
			x = (sSize.width - wSize.width);
			y = (sSize.height - wSize.height);
		}
		//
		window.setLocation(x, y);
	}	//	positionScreen

	/**
	 *	Position in center of the parent window.
	 *  (pack, set location and set visibility)
	 * 	@param parent Parent Window
	 * 	@param window Window to position
	 */
	public static void showCenterWindow(Window parent, Window window)
	{
		window.pack();
		Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension wSize = window.getSize();
		int maxWidth = (int)(sSize.width*.97);
		int maxHeight = (int)(sSize.height*.97);
		//	fit on window
		if (wSize.height > maxHeight)
			wSize.height = maxHeight;
		if (wSize.width > maxWidth)
			wSize.width = maxWidth;
		window.setSize(wSize);
		//
		window.setLocationRelativeTo(parent);
		window.setVisible(true);
		window.toFront();
	}   //  showCenterWindow

	/**
	 *	Position in center of the parent window
	 *
	 * @param parent Parent Window
	 * @param window Window to position
	 */
	public static void positionCenterWindow(Window parent, Window window)
	{
		if (parent == null)
		{
			positionCenterScreen(window);
			return;
		}
		window.pack();
		//
		Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension wSize = window.getSize();
		int maxWidth = (int)(sSize.width*.97);
		int maxHeight = (int)(sSize.height*.97);
		//	fit on window
		if (wSize.height > maxHeight)
			wSize.height = maxHeight;
		if (wSize.width > maxWidth)
			wSize.width = maxWidth;
		window.setSize(wSize);
		//	center in parent
		Rectangle pBounds = parent.getBounds();
		//	Parent is in upper left corner
		if (pBounds.x == pBounds.y && pBounds.x == 0)
		{
			positionCenterScreen(window);
			return;
		}
		//  Find middle
		int x = pBounds.x + ((pBounds.width-wSize.width)/2);
		if (x < 0)
			x = 0;
		int y = pBounds.y + ((pBounds.height-wSize.height)/2);
		if (y < 0)
			y = 0;

		//	Is it on Screen?
		if (x + wSize.width > sSize.width)
			x = sSize.width - wSize.width;
		if (y + wSize.height > sSize.height)
			y = sSize.height - wSize.height;
		//
	//	System.out.println("Position: x=" + x + " y=" + y + " w=" + wSize.getWidth() + " h=" + wSize.getHeight()
	//		+ " - Parent loc x=" + pLoc.x + " y=" + y + " w=" + pSize.getWidth() + " h=" + pSize.getHeight());
		window.setLocation(x, y);
	}	//	positionCenterScreen

	
	/*************************************************************************
	 * 	Get Button
	 *	@param iconName
	 *	@return button
	 */
	public static CButton getButton (String iconName)
	{
		CButton button = new CButton(Env.getImageIcon(iconName + "16.gif"));
		button.setMargin(new Insets (0, 0, 0, 0));
		button.setToolTipText(Msg.getMsg(Env.getCtx(), iconName));
		button.setDefaultCapable(false);
		return button;
	}	//	getButton


	/**
	 *	Create Menu Title (translate it and set Mnemonics).
	 *	Based on MS notation of &Help => H is Mnemonics
	 *
	 *  @param AD_Message message
	 *  @return JMenu
	 */
	public static JMenu getMenu (String AD_Message)
	{
		JMenu menu = new JMenu();
		String text = Msg.getMsg(Env.getCtx(), AD_Message);
		int pos = text.indexOf('&');
		if (pos != -1 && text.length() > pos)	//	We have a nemonic
		{
			char ch = text.toUpperCase().charAt(pos+1);
			if (ch != ' ')
			{
				text = text.substring(0, pos) + text.substring(pos+1);
				menu.setMnemonic(ch);
			}
		}
		menu.setText(text);
		return menu;
	}	//	getMenu

	/**
	 *  Create Menu Item.
	 *  @param actionName   action command
	 *  @param iconName optional name of the icon, defaults to action if null
	 *  @param ks       optional key stroke
	 *  @param menu     menu to add menu item to
	 *  @param al       action listener to register
	 *  @return MenuItem
	 */
	public static JMenuItem addMenuItem (String actionName, String iconName, KeyStroke ks,
		JMenu menu, ActionListener al)
	{
		if (iconName == null)
			iconName = actionName;
		String text = Msg.getMsg(Env.getCtx(), actionName);
		CMenuItem mi = new CMenuItem(text, Env.getImageIcon(iconName + "16.gif"));
		mi.setActionCommand(actionName);
		if (ks != null)
			mi.setAccelerator(ks);
		if (menu != null)
			menu.add(mi);
		if (al != null)
			mi.addActionListener(al);
		return mi;
	}   //  addMenuItem

	/**
	 *  Perform action command for common menu items.
	 * 	Created in AMenu.createMenu(), APanel.createMenu(), FormFrame.createMenu()
	 *  @param actionCommand known action command
	 *  @param WindowNo window no
	 *  @param c Container parent
	 *  @return true if actionCommand was found and performed
	 */
	public static boolean actionPerformed (String actionCommand, int WindowNo, Container c)
	{
		MRole role = MRole.getDefault();
		//  File Menu   ------------------------
		if (actionCommand.equals("PrintScreen"))
		{
			PrintScreenPainter.printScreen (Env.getFrame(c));
		}
		else if (actionCommand.equals("ScreenShot"))
		{
			ScreenShot.createJPEG(Env.getFrame(c), null);
		}
	//	else if (actionCommand.equals("Report"))
	//	{
	//		AEnv.showCenterScreen (new ProcessStart());
	//	}
		else if (actionCommand.equals("Exit"))
		{
			if (ADialog.ask(WindowNo, c, "ExitApplication?"))
				AEnv.exit(0);
		}

		//  View Menu   ------------------------
		else if (actionCommand.equals("InfoProduct"))
		{
			org.compiere.apps.search.Info.showProduct (Env.getFrame(c), WindowNo);
		}
		else if (actionCommand.equals("InfoBPartner"))
		{
			org.compiere.apps.search.Info.showBPartner (Env.getFrame(c), WindowNo);
		}
		else if (actionCommand.equals("InfoAsset"))
		{
			org.compiere.apps.search.Info.showAsset (Env.getFrame(c), WindowNo);
		}
		else if (actionCommand.equals("InfoAccount") && MRole.getDefault().isShowAcct())
		{
			new org.compiere.acct.AcctViewer();
		}
		else if (actionCommand.equals("InfoSchedule"))
		{
			new org.compiere.apps.search.InfoSchedule (Env.getFrame(c), null, false);
		}
		else if (actionCommand.equals("InfoOrder"))
		{
			org.compiere.apps.search.Info.showOrder (Env.getFrame(c), WindowNo, "");
		}
		else if (actionCommand.equals("InfoInvoice"))
		{
			org.compiere.apps.search.Info.showInvoice (Env.getFrame(c), WindowNo, "");
		}
		else if (actionCommand.equals("InfoInOut"))
		{
			org.compiere.apps.search.Info.showInOut (Env.getFrame(c), WindowNo, "");
		}
		else if (actionCommand.equals("InfoPayment"))
		{
			org.compiere.apps.search.Info.showPayment (Env.getFrame(c), WindowNo, "");
		}
		else if (actionCommand.equals("InfoCashLine"))
		{
			org.compiere.apps.search.Info.showCashLine (Env.getFrame(c), WindowNo, "");
		}
		else if (actionCommand.equals("InfoAssignment"))
		{
			org.compiere.apps.search.Info.showAssignment (Env.getFrame(c), WindowNo, "");
		}

		//  Go Menu     ------------------------
		else if (actionCommand.equals("WorkFlow"))
		{
			startWorkflowProcess(0,0);
		}
		else if (actionCommand.equals("Home"))
		{
			Env.getWindow(0).toFront();
		}

		//  Tools Menu  ------------------------
		else if (actionCommand.equals("Calculator"))
		{
			AEnv.showCenterScreen (new org.compiere.grid.ed.Calculator(Env.getFrame(c)));
		}
		else if (actionCommand.equals("Calendar"))
		{
			AEnv.showCenterScreen (new org.compiere.grid.ed.Calendar(Env.getFrame(c)));
		}
		else if (actionCommand.equals("Editor"))
		{
			AEnv.showCenterScreen (new org.compiere.grid.ed.Editor(Env.getFrame(c)));
		}
		else if (actionCommand.equals("Script"))
		{
			new ScriptEditor();
		}
		else if (actionCommand.equals("DataMigration"))
		{
			new DataMigrationEditor(c);
		}
		else if (actionCommand.equals("Preference"))
		{
			if (role.isShowPreference())
				AEnv.showCenterScreen(new Preference (Env.getFrame(c), WindowNo));
		}

		//  Help Menu   ------------------------
		else if (actionCommand.equals("Online"))
		{
			Env.startBrowser(org.compiere.Compiere.getURL());
		}
		else if (actionCommand.equals("EMailSupport"))
		{
			ADialog.createSupportEMail(Env.getFrame(c), Env.getFrame(c).getTitle(), "\n\n");
		}
		else if (actionCommand.equals("About"))
		{
			AEnv.showCenterScreen(new AboutBox(Env.getFrame(c)));
		}
		else
			return false;
		//
		return true;
	}   //  actionPerformed

	/**
	 *  Set Text and Mnemonic for Button.
	 *  Create Mnemonics of text containing "&".
	 *	Based on MS notation of &Help => H is Mnemonics
	 *  @param b The button
	 *  @param text The text with optional Mnemonics
	 */
	public static void setTextMnemonic (JButton b, String text)
	{
		if (text == null || b == null)
			return;
		int pos = text.indexOf('&');
		if (pos != -1)					//	We have a nemonic
		{
			char ch = text.charAt(pos+1);
			b.setMnemonic(ch);
			b.setText(text.substring(0, pos) + text.substring(pos+1));
		}
		b.setText(text);
	}   //  setTextMnemonic

	/**
	 *  Get Mnemonic character from text.
	 *  @param text text with "&"
	 *  @return Mnemonic or 0
	 */
	public static char getMnemonic (String text)
	{
		int pos = text.indexOf('&');
		if (pos != -1)					//	We have a nemonic
			return text.charAt(pos+1);
		return 0;
	}   //  getMnemonic

	
	/*************************************************************************
	 * 	Zoom
	 *	@param AD_Table_ID
	 *	@param Record_ID
	 */
	public static void zoom (int AD_Table_ID, int Record_ID)
	{
		String TableName = null;
		int AD_Window_ID = 0;
		int PO_Window_ID = 0;
		String sql = "SELECT TableName, AD_Window_ID, PO_Window_ID FROM AD_Table WHERE AD_Table_ID=?";
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql, (Trx) null);
			pstmt.setInt(1, AD_Table_ID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
			{
				TableName = rs.getString(1);
				AD_Window_ID = rs.getInt(2);
				PO_Window_ID = rs.getInt(3);
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		//  Nothing to Zoom to
		if (TableName == null || AD_Window_ID == 0)
			return;

		//	PO Zoom ?
		if (PO_Window_ID != 0)
		{
			String whereClause = TableName + "_ID=" + Record_ID;
			
			AD_Window_ID=ZoomTarget.getZoomAD_Window_ID(TableName, 0, whereClause, true);
			
			if (AD_Window_ID == 0)
				return ;
		}
		
		log.config(TableName + " - Record_ID=" + Record_ID );
		AWindow frame = new AWindow();
		if (!frame.initWindow(AD_Window_ID, Query.getEqualQuery(TableName + "_ID", Record_ID)))
			return;
		AEnv.showCenterScreen(frame);
		frame = null;
	}	//	zoom

	/**
	 * 	Zoom
	 *	@param query query
	 */
	public static void zoom (Query query)
	{
		if (query == null || query.getTableName() == null || query.getTableName().length() == 0)
			return;
		String TableName = query.getTableName();
		int AD_Window_ID = 0;
		int PO_Window_ID = 0;
		String sql = "SELECT AD_Window_ID, PO_Window_ID FROM AD_Table WHERE TableName=?";
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql, (Trx) null);
			pstmt.setString(1, TableName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
			{
				AD_Window_ID = rs.getInt(1);
				PO_Window_ID = rs.getInt(2);
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		//  Nothing to Zoom to
		if (AD_Window_ID == 0)
			return;

		//	PO Zoom ?
		boolean isSOTrx = true;
		if (PO_Window_ID != 0)
		{
			AD_Window_ID=ZoomTarget.getZoomAD_Window_ID(TableName, 0, query.getWhereClause(), isSOTrx);
			
			if (AD_Window_ID == 0)
				return ;
		}
		
		log.config(query + " (IsSOTrx=" + isSOTrx + ")");
		AWindow frame = new AWindow();
		if (!frame.initWindow(AD_Window_ID, query))
			return;
		AEnv.showCenterScreen(frame);
		frame = null;
	}	//	zoom
	
	
	/**
	 *	Exit System
	 *  @param status System exit status (usually 0 for no error)
	 */
	public static void exit (int status)
	{
		if (s_server != null)
		{
			try
			{
				s_server.remove();
			}
			catch (Exception ex)
			{
			}
		}
		Env.exitEnv(status);
	}	//	exit

	/**
	 * 	Is Workflow Process view enabled.
	 *	@return true if enabled
	 */
	public static boolean isWorkflowProcess ()
	{
		if (s_workflow == null)
		{
			s_workflow = Boolean.FALSE;					
			int AD_Table_ID = 645;	//	AD_WF_Process	
			if (MRole.getDefault().isTableAccess (AD_Table_ID, true))	//	RO
				s_workflow = Boolean.TRUE;
			else
			{
				AD_Table_ID = 644;	//	AD_WF_Activity	
				if (MRole.getDefault().isTableAccess (AD_Table_ID, true))	//	RO
					s_workflow = Boolean.TRUE;
				else
					log.config(s_workflow.toString());
			}
			//	Get Window
			if (s_workflow.booleanValue())
			{
				s_workflow_Window_ID = DB.getSQLValue (null,
					"SELECT AD_Window_ID FROM AD_Table WHERE AD_Table_ID=?", AD_Table_ID);
				if (s_workflow_Window_ID == 0)
					s_workflow_Window_ID = 297;	//	fallback HARDCODED
				//	s_workflow = Boolean.FALSE;
				log.config(s_workflow + ", Window=" + s_workflow_Window_ID);
			}
		}
		return s_workflow.booleanValue();
	}	//	isWorkflowProcess

	
	/**
	 * 	Start Workflow Process Window
	 *	@param AD_Table_ID optional table
	 *	@param Record_ID optional record
	 */
	public static void startWorkflowProcess (int AD_Table_ID, int Record_ID)
	{
		if (s_workflow_Window_ID == 0)
			return;
		//
		Query query = null;
		if (AD_Table_ID != 0 && Record_ID != 0)
		{
			query = new Query("AD_WF_Process");
			query.addRestriction("AD_Table_ID", Query.EQUAL, AD_Table_ID);
			query.addRestriction("Record_ID", Query.EQUAL, Record_ID);
		}
		//
		AWindow frame = new AWindow();
		if (!frame.initWindow(s_workflow_Window_ID, query))
			return;
		AEnv.showCenterScreen(frame);
		frame = null;
	}	//	startWorkflowProcess
	
	
	/*************************************************************************/

	/** Workflow Menu		*/
	private static Boolean	s_workflow = null;
	/** Workflow Menu		*/
	private static int		s_workflow_Window_ID = 0;
	
	/**	Server Re-tries		*/
	private static int 		s_serverTries = 0;
	/**	Server Session		*/
	private static Server	s_server = null;
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(AEnv.class);

	/**
	 *  Is AppsServer Active ?
	 *  @return true if active
	 */
	public static boolean isServerActive()
	{
		boolean contactAgain = s_server == null && s_serverTries == 0;
		boolean ok = CConnection.get().isAppsServerOK(contactAgain);
		if (ok)
		{
			s_serverTries = 0;
			return true;
		}
		if (s_serverTries > 1)	//	try twice
			return false;

		//	Try to connect
		try
		{
			s_serverTries++;
			log.config("try #" + s_serverTries);
			ok = CConnection.get().isAppsServerOK(true);
			if (ok)
				s_serverTries = 0;
		}
		catch (Exception ex)
		{
			ok = false;
			s_server = null;
		}
		//
		return ok;
	}   //  isServerActive

	/**
	 *  Get Server Version
	 *  @return Apps Server Version
	 *  @see ALogin#checkVersion
	 */
	public static String getServerVersion ()
	{
		return CConnection.get().getServerVersion();
	}   //  getServerVersion

	/**	Window Cache		*/
	private static final CCache<Integer,GridWindowVO>	s_windows 
		= new CCache<Integer,GridWindowVO>("AD_Window", 10); 
	
	/**
	 *  Get Window Model
	 *
	 *  @param WindowNo  Window No
	 *  @param AD_Window_ID window
	 *  @param AD_Menu_ID menu
	 *  @return Model Window Value Object
	 */
	public static GridWindowVO getMWindowVO (int WindowNo, int AD_Window_ID, int AD_Menu_ID)
	{
		log.config("Window=" + WindowNo + ", AD_Window_ID=" + AD_Window_ID);
		GridWindowVO mWindowVO = null;
		if (AD_Window_ID != 0 && Ini.isCacheWindow())	//	try cache
		{
			mWindowVO = s_windows.get(null, AD_Window_ID);
			if (mWindowVO != null)
			{
				mWindowVO = mWindowVO.clone(WindowNo);
				log.info("Cached=" + mWindowVO);
			}
		}
		//  try to get from Server when enabled
		if (mWindowVO == null && DB.isRemoteObjects() && isServerActive())
		{
			log.config("trying server");
			try
			{
				s_server = CConnection.get().getServer();
				if (s_server != null)
				{
					mWindowVO = s_server.getWindowVO(Env.getCtx(), WindowNo, AD_Window_ID, AD_Menu_ID);
					log.config("from Server: success");
				}
			}
			catch (RemoteException e)
			{
				log.log(Level.SEVERE, "(RE)", e);
				mWindowVO = null;
				s_server = null;
			}
			catch (Exception e)
			{
				Throwable tt = e.getCause();
				if (tt != null && tt instanceof InvalidClassException)
					log.log(Level.SEVERE, "(Server<>Client class) " + tt);
				else if (tt != null && tt instanceof NotSerializableException)
					log.log(Level.SEVERE, "Serialization: " + tt.getMessage(), e);
				else
					log.log(Level.SEVERE, "ex", e);
				mWindowVO = null;
				s_server = null;
			}
			catch (Throwable t)
			{
				log.log(Level.SEVERE, t.toString());
				mWindowVO = null;
				s_server = null;
			}
			if (mWindowVO != null)
				s_windows.put(AD_Window_ID, mWindowVO);
		}	//	from Server

		//  Create Window Model on Client
		if (mWindowVO == null)
		{
			log.config("create local");
			mWindowVO = GridWindowVO.create (Env.getCtx(), WindowNo, AD_Window_ID, AD_Menu_ID);
			if (mWindowVO != null)
				s_windows.put(AD_Window_ID, mWindowVO);
		}	//	from Client
		if (mWindowVO == null)
			return null;
		
		//  Check (remote) context
		Ctx ctx = Env.getCtx ();
		if (!mWindowVO.ctx.equals(ctx))
		{
			//  Remote Context is called by value, not reference
			//  Add Window properties to context
			Set<Map.Entry<String,String>> set = mWindowVO.ctx.entrySet();
			Iterator<Map.Entry<String,String>> it = set.iterator();
			while (it.hasNext())
			{
				Map.Entry<String,String> entry = it.next();
				String key = entry.getKey();
				if (key.startsWith(WindowNo+"|"))
				{
					String value = entry.getValue();
					ctx.setContext(key, value);
				}
			}
			//  Sync Context
			mWindowVO.setCtx(Env.getCtx());
		}
		return mWindowVO;
	}   //  getWindow

	/**
	 *  Post Immediate
	 *  @param  WindowNo 		window
	 *  @param  AD_Table_ID     Table ID of Document
	 *  @param  AD_Client_ID    Client ID of Document
	 *  @param  Record_ID       Record ID of this document
	 *  @param  force           force posting
	 *  @return null if success, otherwise error
	 */
	public static String postImmediate ( Ctx ctx, int WindowNo, int AD_Client_ID, 
		int AD_Table_ID, int Record_ID, boolean force)
	{
		log.config("Window=" + WindowNo 
			+ ", AD_Table_ID=" + AD_Table_ID + "/" + Record_ID
			+ ", Force=" + force);

		String error = null;
		//  try to get from Server when enabled
		if (isServerActive())
		{
			log.config("trying server");
			try
			{
				s_server = CConnection.get().getServer();
				if (s_server != null)
				{
					error = s_server.postImmediate( ctx, AD_Client_ID, 
						AD_Table_ID, Record_ID, force, null);
					log.config("from Server: " + (error== null ? "OK" : error));
				}
				else
				{
					ADialog.error(WindowNo, null, "NoAppsServer");
					return "NoAppsServer";
				}
			}
			catch (RemoteException e)
			{
				log.log(Level.WARNING, "(RE)", e);
				error = e.getMessage();
				s_server = null;
			}
			catch (Exception e)
			{
				log.log(Level.WARNING, "ex", e);
				error = e.getMessage();
				s_server = null;
			}
		}
		else
		{
			ADialog.error(WindowNo, null, "NoAppsServer");
			return "NoAppsServer";
		}
		return error;
	}   //  postImmediate

	/**
	 *  Cache Reset
	 *  @param  tableName	table name
	 *  @param  Record_ID	record id
	 */
	public static void cacheReset (String tableName, int Record_ID)
	{
		log.config("TableName=" + tableName + ", Record_ID=" + Record_ID);

		//  try to get from Server when enabled
		if (isServerActive())
		{
			log.config("trying server");
			try
			{
				Server server = CConnection.get().getServer();
				if (server != null)
				{
					server.cacheReset(tableName, Record_ID); 
				}
			}
			catch (RemoteException e)
			{
				log.log(Level.SEVERE, "(RE)", e);
				s_server = null;
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, "ex", e);
				s_server = null;
			}
		}
	}   //  cacheReset
		
}	//	AEnv
