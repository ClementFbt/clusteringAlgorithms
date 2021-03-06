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
import java.sql.*;
import java.text.*;
import java.util.logging.*;

import javax.swing.*;

import org.compiere.apps.search.*;
import org.compiere.common.constants.*;
import org.compiere.model.*;
import org.compiere.plaf.*;
import org.compiere.swing.*;
import org.compiere.util.*;

/**
 *	Resource Assignment Entry
 *
 * 	@author 	Jorg Janke
 * 	@version 	$Id: VAssignment.java,v 1.2 2006/07/30 00:51:28 jjanke Exp $
 */
public class VAssignment extends JComponent
	implements VEditor, ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *	IDE Constructor
	 */
	public VAssignment()
	{
		this (false, false, true);
	}	//	VAssigment

	/**
	 *	Create Resource Assigment.
	 *  <pre>
	 * 		Resource DateTimeFrom Qty UOM Button
	 *  </pre>
	 *  @param mandatory mandatory
	 *  @param isReadOnly read only
	 *  @param isUpdateable updateable
	 */
	public VAssignment (boolean mandatory, boolean isReadOnly, boolean isUpdateable)
	{
		super();
	//	super.setName(columnName);
		LookAndFeel.installBorder(this, "TextField.border");
		this.setLayout(new BorderLayout());
		//  Size
		this.setPreferredSize(m_text.getPreferredSize());
		int height = m_text.getPreferredSize().height;

		//	***	Text	***
		m_text.setEditable(false);
		m_text.setFocusable(false);
		m_text.setHorizontalAlignment(SwingConstants.LEADING);
		//	Background
		setMandatory(mandatory);
		this.add(m_text, BorderLayout.CENTER);

		//	***	Button	***
		m_button.setIcon(Env.getImageIcon("Assignment10.gif"));
		m_button.setPreferredSize(new Dimension(height, height));
		m_button.addActionListener(this);
		m_button.setFocusable(true);
		this.add(m_button, BorderLayout.EAST);

		setBorder( null );

		//	Prefereed Size
		this.setPreferredSize(this.getPreferredSize());		//	causes r/o to be the same length
		//	ReadWrite
		if (isReadOnly || !isUpdateable)
			setReadWrite(false);
		else
			setReadWrite(true);

        //  Popup
        m_text.addMouseListener(new MouseAdapter()
        {
            @Override
			public void mouseClicked(MouseEvent e)
            {
                if (SwingUtilities.isRightMouseButton(e))
                    m_popupMenu.show((Component)e.getSource(), e.getX(), e.getY());
            }
        });
        
        String actionKey = m_text.getClass().getName() + "_popop";
        InputMap iMap = m_text.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_MASK);
        iMap.put(ks, actionKey);
        m_text.getActionMap().put(actionKey, new AbstractAction()
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
        
		menuEditor = new CMenuItem(Msg.getMsg(Env.getCtx(), "InfoResource"), Env.getImageIcon("Zoom16.gif"));
		menuEditor.addActionListener(this);
		m_popupMenu.add(menuEditor);
	}	//	VAssignment

	/**	Data Value				*/
	private Object				m_value = null;
	/** Get Info				*/
	private PreparedStatement	m_pstmt = null;

	/** The Text Field          */
	private JTextField			m_text = new JTextField (VLookup.DISPLAY_LENGTH);
	/** The Button              */
	private CButton				m_button = new CButton();

	JPopupMenu					m_popupMenu = new JPopupMenu();
	private CMenuItem 			menuEditor;

	private boolean				m_readWrite;
	private boolean				m_mandatory;

	/**	The Format				*/
	private DateFormat			m_dateFormat = DisplayType.getDateFormat(DisplayTypeConstants.DateTime);
	private NumberFormat		m_qtyFormat = DisplayType.getNumberFormat(DisplayTypeConstants.Quantity);
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(VAssignment.class);
	
	/**
	 * 	Dispose resources
	 */
	public void dispose()
	{
		try
		{
			if (m_pstmt != null)
				m_pstmt.close();
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "");
		}
		m_text = null;
		m_button = null;
		m_field = null;
	}	//	dispose

	/**
	 * 	Set Mandatory
	 * 	@param mandatory mandatory
	 */
	public void setMandatory (boolean mandatory)
	{
		m_mandatory = mandatory;
		m_button.setMandatory(mandatory);
		setBackground (false);
	}	//	setMandatory

	/**
	 * 	Get Mandatory
	 *  @return mandatory
	 */
	public boolean isMandatory()
	{
		return m_mandatory;
	}	//	isMandatory

	/**
	 * 	Set ReadWrite
	 * 	@param rw read rwite
	 */
	public void setReadWrite (boolean rw)
	{
		m_readWrite = rw;
		m_button.setReadWrite(rw);
		setBackground (false);
	}	//	setReadWrite

	/**
	 * 	Is Read Write
	 * 	@return read write
	 */
	public boolean isReadWrite()
	{
		return m_readWrite;
	}	//	isReadWrite

	/**
	 * 	Set Foreground
	 * 	@param color color
	 */
	@Override
	public void setForeground (Color color)
	{
		m_text.setForeground(color);
	}	//	SetForeground

	/**
	 * 	Set Background
	 * 	@param error Error
	 */
	public void setBackground (boolean error)
	{
		if (error)
			setBackground(CompierePLAF.getFieldBackground_Error());
		else if (!m_readWrite)
			setBackground(CompierePLAF.getFieldBackground_Inactive());
		else if (m_mandatory)
			setBackground(CompierePLAF.getFieldBackground_Mandatory());
		else
			setBackground(CompierePLAF.getInfoBackground());
	}	//	setBackground

	/**
	 * 	Set Background
	 * 	@param color Color
	 */
	@Override
	public void setBackground (Color color)
	{
		m_text.setBackground(color);
	}	//	setBackground

	/**
	 * 	Request Focus
	 */
	@Override
	public void requestFocus()
	{
		m_text.requestFocus();
	}	//	requestFocus

	/**
	 * 	Request Focus In Window
	 *	@return focus request
	 */
	@Override
	public boolean requestFocusInWindow()
	{
		return m_text.requestFocusInWindow();
	}	//	requestFocusInWindow
	
	/**
	 * 	Get Focus Component
	 *	@return component
	 */
	public Component getFocusableComponent()
	{
		return m_text;
	}	//	getFocusComponent

	/**************************************************************************
	 * 	Set/lookup Value
	 * 	@param value value
	 */
	public void setValue(Object value)
	{
		if (value == m_value)
			return;
		m_value = value;
		int S_ResourceAssignment_ID = 0;
		if (m_value != null && m_value instanceof Integer)
			S_ResourceAssignment_ID = ((Integer)m_value).intValue();
		//	Set Empty
		if (S_ResourceAssignment_ID == 0)
		{
			m_text.setText("");
			return;
		}

		//	Statement
		if (m_pstmt == null)
			m_pstmt = DB.prepareStatement("SELECT r.Name,ra.AssignDateFrom,ra.Qty,uom.UOMSymbol "
				+ "FROM S_ResourceAssignment ra, S_Resource r, S_ResourceType rt, C_UOM uom "
				+ "WHERE ra.S_ResourceAssignment_ID=?"
				+ " AND ra.S_Resource_ID=r.S_Resource_ID"
				+ " AND r.S_ResourceType_ID=rt.S_ResourceType_ID"
				+ " and rt.C_UOM_ID=uom.C_UOM_ID", (Trx) null);
		//
		try
		{
			m_pstmt.setInt(1, S_ResourceAssignment_ID);
			ResultSet rs = m_pstmt.executeQuery();
			if (rs.next())
			{
				StringBuffer sb = new StringBuffer(rs.getString(1));
				sb.append(" ").append(m_dateFormat.format(rs.getTimestamp(2)))
					.append(" ").append(m_qtyFormat.format(rs.getBigDecimal(3)))
					.append(" ").append(rs.getString(4).trim());
				m_text.setText(sb.toString());
			}
			else
				m_text.setText("<" + S_ResourceAssignment_ID + ">");
			rs.close();
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
	}	//	setValue

	/**
	 * 	Get Value
	 * 	@return value
	 */
	public Object getValue()
	{
		return m_value;
	}	//	getValue

	/**
	 * 	Get Display Value
	 *	@return info
	 */
	public String getDisplay()
	{
		return m_text.getText();
	}	//	getDisplay

	/**
	 *  Set Field/WindowNo 
	 *  @param mField
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
	 *  Action Listener Interface
	 *  @param listener listener
	 */
	public void addActionListener(ActionListener listener)
	{
		m_text.addActionListener(listener);
	}   //  addActionListener

	/**
	 *  Action Listener Interface
	 *  @param listener
	 */
	public void removeActionListener(ActionListener listener)
	{
		m_text.removeActionListener(listener);
	}   //  removeActionListener

	/**
	 * 	Action Listener - start dialog
	 * 	@param e Event
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (!m_button.isEnabled())
			return;
		m_button.setEnabled(false);
		//
		Integer oldValue = (Integer)getValue();
		int S_ResourceAssignment_ID = oldValue == null ? 0 : oldValue.intValue();
		MResourceAssignment ma = new MResourceAssignment(Env.getCtx(), S_ResourceAssignment_ID, null);

		//	Start VAssignment Dialog
		if (S_ResourceAssignment_ID != 0)
		{
			VAssignmentDialog vad = new VAssignmentDialog (Env.getFrame(this), ma, true, true);
			ma = vad.getMResourceAssignment();
		}
		//	Start InfoSchedule directly
		else
		{
			InfoSchedule is = new InfoSchedule(Env.getFrame(this), ma, true);
			ma = is.getMResourceAssignment();
		}

		//	Set Value
		if (ma != null && ma.getS_ResourceAssignment_ID() != 0)
		{
			setValue(Integer.valueOf(ma.getS_ResourceAssignment_ID()));
			try
			{
				fireVetoableChange("S_ResourceAssignment_ID", new Object(), getValue());
			}
			catch (PropertyVetoException pve)
			{
				log.log(Level.SEVERE, "", pve);
			}
		}
		m_button.setEnabled(true);
		requestFocus();
	}	//	actionPerformed

	/**
	 *  Property Change Listener
	 *  @param evt event
	 */
	public void propertyChange (PropertyChangeEvent evt)
	{
		if (evt.getPropertyName().equals(org.compiere.model.GridField.PROPERTY))
			setValue(evt.getNewValue());
	}   //  propertyChange

}	//	VAssignment
