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
package org.compiere.grid;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

import org.compiere.apps.*;
import org.compiere.grid.ed.*;
import org.compiere.model.*;
import org.compiere.swing.*;
import org.compiere.util.*;

/**
 *  Single Row Panel.
 *  Called from GridController
 *  <pre>
 *	Structure
 *		this (CPanel - Group Bag Layout)
 *			group
 *			label - field
 *
 *  Spacing:
 *  -------------------
 *  Total Top = 10+2
 *  Total Right = 0+12
 *  Total Left = 0+12
 *  Total Bottom = 3+9
 *  -------------------
 *       2
 *  12 Label 0+5 Field 0
 *       3+2=5
 *  12 Label 0+5 Field 0
 *       3
 *
 *  </pre>
 *  @author 	Jorg Janke
 *  @version 	$Id: VPanel.java,v 1.3 2006/07/30 00:51:28 jjanke Exp $
 */
public final class VPanel extends CPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *	Constructor
	 */
	public VPanel()
	{
		super(new GridBagLayout());
		setName("VPanel");
		setBorder(null);

		//	Set initial values of constraint
		m_gbc.anchor = GridBagConstraints.NORTHWEST;
		m_gbc.gridy = 0;			//	line
		m_gbc.gridx = 0;
		m_gbc.gridheight = 1;
		m_gbc.gridwidth = 1;
		m_gbc.insets = m_zeroInset;
		m_gbc.fill = GridBagConstraints.HORIZONTAL;
		m_gbc.weightx = 0;
		m_gbc.weighty = 0;
		m_gbc.ipadx = 0;
		m_gbc.ipady = 0;
	}	//	VPanel
	
	/** In JComponent's client properties when the component can/cannot be visible. */
	public static final String COMPONENT_VISIBLE = "VPanel.COMPONENT_VISIBLE";
	
	/** GridBag Constraint      */
	private GridBagConstraints	m_gbc = new GridBagConstraints();

	/** Orientation             */
	private final boolean       m_leftToRight = Language.getLoginLanguage().isLeftToRight();
	/** Label Inset             */
	private final Insets 		m_labelInset =
		m_leftToRight ? new Insets(2,12,3,0) : new Insets(2,5,3,0);     // 	top,left,bottom,right
	/** Field Inset             */
	private final Insets 		m_fieldInset =
		m_leftToRight ? new Insets(2,5,3,0)  : new Insets(2,12,3,0);	// 	top,left,bottom,right
	/** Zero Inset              */
	private final Insets 		m_zeroInset = new Insets(0,0,0,0);
	//
	private int 				m_line = 0;
	/** Previous Field Group Header     */
	private String              m_oldFieldGroup = null;
	/** DefaultFocusField		*/
	private VEditor				m_defaultFocusField = null;
	/** Focus Traversal Policy	*/
	private static FocusTraversalPolicy s_focusPolicy = null;
	
	/** Map of group name to list of components in group. */
	HashMap<String, ArrayList<JComponent>> m_groupToCompsMap =
		new HashMap<String, ArrayList<JComponent>>();
	
	/** Map of group name to list of components in group. */
	HashMap<Component, GridField> m_compToFieldMap =
		new HashMap<Component, GridField>();
	
	/** Map of group name to list of components in group. */
	private HashMap<String, VLine> m_groupToVlineMap = new HashMap<String, VLine>();

	/**	Logger	*/
	private static CLogger log = CLogger.getCLogger (VPanel.class);

	/** Expand icon path	*/
	private static final String s_expandIconPath = "/org/compiere/images/Expand9.gif";

	/** Expand icon path	*/
	static final ImageIcon s_expandIcon;
	/** Static				*/
	static
	{
		URL url = VPanel.class.getResource(s_expandIconPath);
		s_expandIcon = (url == null) ? null : new ImageIcon(url);
		if (s_expandIcon == null)			
			log.severe("image not found: " + s_expandIconPath);
	}

	/** Collapse icon			*/
	static final ImageIcon 		s_collapseIcon;
	/** Collapse icon path. 	*/
	private static final String s_collapseIconPath = "/org/compiere/images/Collapse9.gif";
	/** Static Init				*/
	static
	{
		URL url = VPanel.class.getResource(s_collapseIconPath);
		s_collapseIcon = (url == null) ? null : new ImageIcon(url);
		if (s_collapseIcon == null)			
			log.severe("Image not found: " + s_collapseIconPath);
	}

	/**
	 * 	Set Field Mnemonic
	 *	@param mField field
	 */
	public void setMnemonic (GridField mField)
	{
		if (mField.isCreateMnemonic())
			return;
		String text = mField.getHeader();
		int pos = text.indexOf('&');
		if (pos != -1 && text.length() > pos)	//	We have a nemonic - creates Ctrl_Shift_
		{
			char mnemonic = text.toUpperCase().charAt(pos+1);
			if (mnemonic != ' ')
			{
				if (!m_mnemonics.contains(mnemonic))
				{
					mField.setMnemonic(mnemonic);
					m_mnemonics.add(mnemonic);
				}
				else
					log.warning(mField.getColumnName() 
						+ " - Conflict - Already exists: " + mnemonic + " (" + text + ")");
			}
		}
	}	//	setMnemonic
	
	/**
	 *	Add Field and Label to Panel
	 *  @param editor editor
	 *  @param mField field model
	 */
	public void addField (VEditor editor, GridField mField)
	{
		CLabel label = VEditorFactory.getLabel(mField); 
		if (label == null && editor == null)
			return;

		boolean sameLine = mField.isSameLine();
		if (addGroup(mField.getFieldGroup()))               		//	sets top
			sameLine = false;

		if (sameLine)    							//	Set line #
			m_gbc.gridy = m_line-1;
		else
			m_gbc.gridy = m_line++;

		//	*** The Label ***
		if (label != null)
		{
			m_gbc.gridwidth = 1;
			m_gbc.insets = m_labelInset;
			m_gbc.fill = GridBagConstraints.HORIZONTAL;	//	required for right justified
			//	Set column #
			if (m_leftToRight)
				m_gbc.gridx = sameLine ? 2 : 0;
			else
				m_gbc.gridx = sameLine | mField.isLongField() ? 3 : 1;
			//	Weight factor for Label
			m_gbc.weightx = 0;
			//
			if (mField.isCreateMnemonic())
				setMnemonic(label, mField.getMnemonic());
			//  Add Label
			this.add(label, m_gbc);
			
			addToCompList(label);
			m_compToFieldMap.put(label, mField);
		}

		//	*** The Field ***
		if (editor != null)
		{
			JComponent field = (JComponent)editor;
			//	Default Width
			m_gbc.gridwidth = mField.isLongField() ? 3 : 1;
			m_gbc.insets = m_fieldInset;
		//	m_gbc.fill = GridBagConstraints.NONE;
			m_gbc.fill = GridBagConstraints.HORIZONTAL;
			//	Set column #
			if (m_leftToRight)
				m_gbc.gridx = sameLine ? 3 : 1;
			else
				m_gbc.gridx = sameLine ? 2 : 0;
			//	Weight factor for Fields
			m_gbc.weightx = 1;
			//	Add Field
			this.add(field, m_gbc);
			//	Link Label to Field
			if (label != null)
				label.setLabelFor(field);
			else if (mField.isCreateMnemonic())
				setMnemonic(editor, mField.getMnemonic());
			
			addToCompList(field);
			m_compToFieldMap.put(field, mField);
			
			//	Default Focus
			if (m_defaultFocusField == null && mField.isDefaultFocus())
			{
				m_defaultFocusField = editor;
				if (s_focusPolicy == null)
					s_focusPolicy = new AFocusTraversalPolicy();
				setFocusTraversalPolicy(s_focusPolicy);
				setFocusTraversalPolicyProvider(true);
			}
		}
	}	//	addField
	
	/**
	 * Tracking the components of a field group in the a map of lists.
	 */
	private void addToCompList(JComponent comp)
	{
		if (m_oldFieldGroup != null && !m_oldFieldGroup.equals(""))
		{
			ArrayList<JComponent> compList = m_groupToCompsMap.get(m_oldFieldGroup);
			if (compList == null)
			{
				compList = new ArrayList<JComponent>();
				m_groupToCompsMap.put(m_oldFieldGroup, compList);
			}
			compList.add(comp);
		}
	}

	/**
	 *	Add Group
	 *  @param fieldGroup field group
	 *  @return true if group added
	 */
	private boolean addGroup(final String fieldGroup)
	{
		//	First time - add top
		if (m_oldFieldGroup == null)
		{
			addTop();
			m_oldFieldGroup = "";
		}

		if (fieldGroup == null || fieldGroup.length() == 0 || fieldGroup.equals(m_oldFieldGroup))
			return false;
		m_oldFieldGroup = fieldGroup;

		CPanel group = new CPanel();
		final VLine vLine = new VLine(fieldGroup, s_collapseIcon);
		m_groupToVlineMap.put(m_oldFieldGroup, vLine);
		group.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent me)
			{
				if (SwingUtilities.isLeftMouseButton(me))
				//	&& me.getClickCount() == 2)
				{
					boolean visible = vLine.getIcon() == s_expandIcon;
					for (JComponent comp : m_groupToCompsMap.get(fieldGroup))
					{
						GridField field = m_compToFieldMap.get(comp);
						comp.setVisible(field.isDisplayed(true)&& visible);
						comp.putClientProperty(COMPONENT_VISIBLE, Boolean.valueOf(visible));
					}
					vLine.setIcon(visible ? s_collapseIcon : s_expandIcon);
				}
			}
		});
		group.setBorder(vLine);
		group.add(Box.createVerticalStrut(VLine.SPACE));
		
		m_gbc.gridx = 0;
		m_gbc.gridy = m_line++;
		m_gbc.gridwidth = 4;
		this.add(group, m_gbc);
		//	reset
		m_gbc.gridwidth = 1;
		return true;
	}	//	addGroup

	/**
	 *	Add Top (10) and right (12) gap
	 */
	private void addTop()
	{
		//	Top Gap
		m_gbc.gridy = m_line++;
		this.add(Box.createVerticalStrut(10), m_gbc);    	//	top gap
		//	Right gap
		m_gbc.gridx = 4;									//	5th column
		m_gbc.gridwidth = 1;
		m_gbc.weightx = 0;
		m_gbc.insets = m_zeroInset;
		m_gbc.fill = GridBagConstraints.NONE;
		this.add(Box.createHorizontalStrut(12), m_gbc);
	}	//	addTop

	/**
	 *	Add End (9) of Form
	 */
	public void addEnd()
	{
		m_gbc.gridx = 0;
		m_gbc.gridy = m_line;
		m_gbc.gridwidth = 1;
		m_gbc.insets = m_zeroInset;
		m_gbc.fill = GridBagConstraints.HORIZONTAL;
		m_gbc.weightx = 0;
		//
		this.add(Box.createVerticalStrut(9), m_gbc);		//	botton gap
	}	//	addEnd

	/**
	 * 	Set Mnemonic for Label CTRL_SHIFT_x
	 *	@param label label
	 *	@param predefinedMnemonic predefined Mnemonic
	 */
	private void setMnemonic (CLabel label, char predefinedMnemonic)
	{
		String text = label.getText();
		int pos = text.indexOf("&");
		if (pos != -1 && predefinedMnemonic != 0)
		{
			text = text.substring(0, pos) + text.substring(pos+1);
			label.setText(text);
			label.setSavedMnemonic(predefinedMnemonic);
			m_fields.add(label);
			log.finest(predefinedMnemonic + " - " + label.getName());
		}
		else
		{
			char mnemonic = getMnemonic(text, label);
			if (mnemonic != 0)
				label.setSavedMnemonic(mnemonic);
		//	label.setDisplayedMnemonic(mnemonic);
		}
	}	//	setMnemonic
	
	/**
	 * 	Set Mnemonic for Check Box or Button
	 *	@param editor check box or button - other ignored
	 *	@param predefinedMnemonic predefined Mnemonic
	 */
	private void setMnemonic (VEditor editor, char predefinedMnemonic)
	{
		if (editor instanceof VCheckBox)
		{
			VCheckBox cb = (VCheckBox)editor;
			String text = cb.getText();
			int pos = text.indexOf("&");
			if (pos != -1 && predefinedMnemonic != 0)
			{
				text = text.substring(0, pos) + text.substring(pos+1);
				cb.setText(text);
				cb.setSavedMnemonic(predefinedMnemonic);
				m_fields.add(cb);
				log.finest(predefinedMnemonic + " - " + cb.getName());
			}
			else
			{
				char mnemonic = getMnemonic(text, cb);
				if (mnemonic != 0)
					cb.setSavedMnemonic(mnemonic);
			//	cb.setMnemonic(mnemonic);
			}
		}
		//	Button
		else if (editor instanceof VButton)
		{
			VButton b = (VButton)editor;
			String text = b.getText();
			int pos = text.indexOf("&");
			if (pos != -1 && predefinedMnemonic != 0)
			{
				text = text.substring(0, pos) + text.substring(pos+1);
				b.setText(text);
				b.setSavedMnemonic(predefinedMnemonic);
				m_fields.add(b);
				log.finest(predefinedMnemonic + " - " + b.getName());
			}
			else if (b.getColumnName().equals("DocAction"))
			{
				b.getInputMap(WHEN_IN_FOCUSED_WINDOW)
					.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.SHIFT_MASK, false), "pressed");
				b.getInputMap(WHEN_IN_FOCUSED_WINDOW)
					.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.SHIFT_MASK, true), "released");
			//	Util.printActionInputMap(b);
			}
			else if (b.getColumnName().equals("Posted"))
			{
				b.getInputMap(WHEN_IN_FOCUSED_WINDOW)
					.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, Event.SHIFT_MASK, false), "pressed");
				b.getInputMap(WHEN_IN_FOCUSED_WINDOW)
					.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, Event.SHIFT_MASK, true), "released");
			//	Util.printActionInputMap(b);
			}
			else
			{
				char mnemonic = getMnemonic(text, b);
				if (mnemonic != 0)
					b.setSavedMnemonic(mnemonic);
			}
		}
	}	//	setMnemonic

	/**
	 * 	Get Mnemonic from text
	 *	@param text text
	 *	@param source component
	 *	@return Mnemonic or 0 if not unique
	 */
	private char getMnemonic (String text, Component source)
	{
		if (text == null || text.length() == 0)
			return 0;
		String oText = text;
		text = text.trim().toUpperCase();
		char mnemonic = text.charAt(0);
		if (m_mnemonics.contains(mnemonic))
		{
			mnemonic = 0;
			//	Beginning new word
			int index = text.indexOf(' ');
			while (index != -1 && text.length() > index)
			{
				char c = text.charAt(index+1);
				if( Character.isLetterOrDigit( c ) && Character.UnicodeBlock.of( c ) == Character.UnicodeBlock.BASIC_LATIN
						&& !m_mnemonics.contains( c ) )
				{
					mnemonic = c;
					break;
				}
				index = text.indexOf(' ', index+1);
			}
			//	Any character
			if (mnemonic == 0)
			{
				for (int i = 1; i < text.length(); i++)
				{
					char c = text.charAt(i);
					if( Character.isLetterOrDigit( c ) && Character.UnicodeBlock.of( c ) == Character.UnicodeBlock.BASIC_LATIN
							&& !m_mnemonics.contains( c ) )
					{
						mnemonic = c;
						break;
					}
				}
			}
			//	Nothing found
			if (mnemonic == 0)
			{
				log.finest("None for: " + oText);
				return 0;	//	 if first char would be returned, the first occurance is invalid.
			}
		}
		m_mnemonics.add(mnemonic);
		m_fields.add(source);
		log.finest(mnemonic + " - " + source.getName());
		return mnemonic;
	}	//	getMnemonic
	
	/** Used Mnemonics		*/
	private ArrayList<Character> m_mnemonics = new ArrayList<Character>(30);
	/** Mnemonic Fields		*/
	private ArrayList<Component> m_fields = new ArrayList<Component>(30);
	
	/**
	 * 	Set Window level Mnemonics
	 *	@param set true if set otherwise unregiser
	 */
	public void setMnemonics (boolean set)
	{
		int size = m_fields.size();
		for (int i = 0; i < size; i++)
		{
			Component c = m_fields.get(i);
			if (c instanceof CLabel)
			{
				CLabel l = (CLabel)c;
				if (set)
					l.setDisplayedMnemonic(l.getSavedMnemonic());
				else
					l.setDisplayedMnemonic(0);
			}
			else if (c instanceof VCheckBox)
			{
				VCheckBox cb = (VCheckBox)c;
				if (set)
					cb.setMnemonic(cb.getSavedMnemonic());
				else
					cb.setMnemonic(0);
			}
			else if (c instanceof VButton)
			{
				VButton b = (VButton)c;
				if (set)
					b.setMnemonic(b.getSavedMnemonic());
				else
					b.setMnemonic(0);
			}
		}
	}	//	setMnemonics
	
	/**************************************************************************
	 *  Set Background to AD_Color_ID (nop)
	 *  @param AD_Color_ID Color
	 */
	public void setBackground (int AD_Color_ID)
	{
	}   //  setBackground
	
	
	/**
	 * 	Get Default Focus Field
	 *	@return field if defined
	 */
	public VEditor getDefaultFocus()
	{
		return m_defaultFocusField;
	}	//	getDefaultFocus
	
	
	/**
	 * 	Request Focus In Window
	 *	@return focus request
	 */
	@Override
	public boolean requestFocusInWindow()
	{
		if (m_defaultFocusField != null)
		{
			if (m_defaultFocusField.isReadWrite())
				return m_defaultFocusField.getFocusableComponent().requestFocusInWindow();
		}
	    return super.requestFocusInWindow();
	}
}	//	VPanel
