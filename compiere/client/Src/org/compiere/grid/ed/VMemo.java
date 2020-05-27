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
package org.compiere.grid.ed;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;

import org.compiere.apps.*;
import org.compiere.model.*;
import org.compiere.plaf.*;
import org.compiere.swing.*;
import org.compiere.util.*;

/**
 *  Text Control (JTextArea embedded in JScrollPane)
 *
 *  @author 	Jorg Janke
 *  @version 	$Id: VMemo.java,v 1.2 2006/07/30 00:51:27 jjanke Exp $
 */
public class VMemo extends CTextArea
	implements VEditor, KeyListener, FocusListener, ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *	IDE Baan Constructor
	 */
	public VMemo()
	{
		this("", false, false, true, 60, 4000);
	}	//	VMemo

	/**
	 *	Standard Constructor
	 *  @param columnName
	 *  @param mandatory
	 *  @param isReadOnly
	 *  @param isUpdateable
	 *  @param displayLength
	 *  @param fieldLength
	 */
	public VMemo (String columnName, boolean mandatory, boolean isReadOnly, boolean isUpdateable,
		int displayLength, int fieldLength)
	{
		super (fieldLength/80, 50);
		super.setName(columnName);
		LookAndFeel.installBorder(this, "TextField.border");
		this.addFocusListener(this);    //  to activate editor

		//  Create Editor
		setColumns(displayLength>VString.MAXDISPLAY_LENGTH ? VString.MAXDISPLAY_LENGTH : displayLength);	//  46
		setForeground(CompierePLAF.getTextColor_Normal());
		setBackground(CompierePLAF.getFieldBackground_Normal());

		setLineWrap(true);
		setWrapStyleWord(true);
		addFocusListener(this);
		setInputVerifier(new CInputVerifier()); //Must be set AFTER addFocusListener in order to work
		setMandatory(mandatory);
		m_columnName = columnName;
		m_fieldLength = fieldLength;

		if (isReadOnly || !isUpdateable)
			setReadWrite(false);
		addKeyListener(this);

		//	Popup
        addMouseListener(new MouseAdapter()
        {
            @Override
			public void mouseClicked(MouseEvent e)
            {
                if (SwingUtilities.isRightMouseButton(e))
                    m_popupMenu.show((Component)e.getSource(), e.getX(), e.getY());
            }
        });
        
        String actionKey = getClass().getName() + "_popop";
        InputMap iMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_MASK);
        iMap.put(ks, actionKey);
        getActionMap().put(actionKey, new AbstractAction()
        {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
            {
                Component comp = (Component)e.getSource();
                m_popupMenu.show(comp, 10, 10);
            }
        });
        
		if (columnName.equals("Script"))
			menuEditor = new CMenuItem(Msg.getMsg(Env.getCtx(), "Script"), Env.getImageIcon("Script16.gif"));
		else
			menuEditor = new CMenuItem(Msg.getMsg(Env.getCtx(), "Editor"), Env.getImageIcon("Editor16.gif"));
		menuEditor.addActionListener(this);
		m_popupMenu.add(menuEditor);
	}	//	VMemo

	/**
	 *  Dispose
	 */
	public void dispose()
	{
		m_field = null;
	}   //  dispose

	JPopupMenu			m_popupMenu = new JPopupMenu();
	private CMenuItem 	menuEditor;
	private int			m_fieldLength;

	String				m_columnName;
	String				m_oldText = "";
	private boolean		m_firstChange;
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(VMemo.class);

	/**
	 *	Set Editor to value
	 *  @param value
	 */
	@Override
	public void setValue(Object value)
	{
		super.setValue(value);
		m_firstChange = true;
		//	Always position Top 
		setCaretPosition(0);
	}	//	setValue

	/**
	 *  Property Change Listener
	 *  @param evt
	 */
	public void propertyChange (PropertyChangeEvent evt)
	{
		if (evt.getPropertyName().equals(org.compiere.model.GridField.PROPERTY))
			setValue(evt.getNewValue());
	}   //  propertyChange

	/**
	 *	ActionListener
	 *  @param e
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == menuEditor)
		{
			menuEditor.setEnabled(false);
			String s = null;
			if (m_columnName.equals("Script"))
				s = ScriptEditor.start (Msg.translate(Env.getCtx(), m_columnName), getText(), isEditable(), 0);
			else
				s = Editor.startEditor (this, Msg.translate(Env.getCtx(), m_columnName), 
					getText(), isEditable(), m_fieldLength, null);
			menuEditor.setEnabled(true);
			setValue(s);
			try
			{
				fireVetoableChange(m_columnName, null, getText());
				m_oldText = getText();
			}
			catch (PropertyVetoException pve)	{}
		}
	}	//	actionPerformed

	/**
	 *  Action Listener Interface
	 *  @param listener
	 */
	public void addActionListener(ActionListener listener)
	{
	}   //  addActionListener

	/**
	 *  Action Listener Interface
	 *  @param listener
	 */
	public void removeActionListener(ActionListener listener)
	{
	}   //  removeActionListener

	/**************************************************************************
	 *	Key Listener Interface
	 *  @param e
	 */
	public void keyTyped(KeyEvent e)	{}
	public void keyPressed(KeyEvent e)	{}

	/**
	 *	Escape 	- Restore old Text.
	 *  Indicate Change
	 *  @param e
	 */
	public void keyReleased(KeyEvent e)
	{
		//  ESC
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !getText().equals(m_oldText))
		{
			log.fine( "VMemo.keyReleased - ESC");
			setText(m_oldText);
			return;
		}
		//  Indicate Change
		if (m_firstChange && !m_oldText.equals(getText()))
		{
			log.fine( "VMemo.keyReleased - firstChange");
			m_firstChange = false;
			try
			{
				String text = getText();
				fireVetoableChange(m_columnName, text, null);   //  No data committed - done when focus lost !!!
			}
			catch (PropertyVetoException pve)	{}
		}	//	firstChange
	}	//	keyReleased

	/**
	 *	Focus Gained	- Save for Escape
	 *  @param e
	 */
	@Override
	public void focusGained (FocusEvent e)
	{
		log.config(e.paramString());
		if (e.getSource() instanceof VMemo)
			requestFocus();
		else
			m_oldText = getText();
	}	//	focusGained

	/**
	 *	Data Binding to MTable (via GridController)
	 *  @param e
	 */
	@Override
	public void focusLost (FocusEvent e)
	{
		//log.config( "VMemo.focusLost " + e.getSource(), e.paramString());
		//	something changed?
		return;

	}	//	focusLost

	/*************************************************************************/

	/**
	 *  Set Field/WindowNo 
	 *  @param mField field
	 */
	public void setField (GridField mField)
	{
		m_field = mField;
	}   //  setField

	/** Grid Field				*/
	private GridField 	m_field = null;
	
	/**
	 *  Get Field
	 *  @return gridField
	 */
	public GridField getField()
	{
		return m_field;
	}   //  getField


	/**
	 * 	VMemo.CInputVerifier
	 */
	class CInputVerifier extends InputVerifier
	{

		@Override
		public boolean verify(JComponent input)
		{
			// NOTE: We return true no matter what since the InputVerifier is
            // only introduced to fireVetoableChange in due time
			if (getText () == null && m_oldText == null)
				return true;
			else if (getText ().equals (m_oldText))
				return true;
			//
			try
			{
				String text = getText ();
				fireVetoableChange (m_columnName, null, text);
				m_oldText = text;
				return true;
			}
			catch (PropertyVetoException pve)
			{
			}
			return true;
		}	// verify
	}	// CInputVerifier

}	//	VMemo





