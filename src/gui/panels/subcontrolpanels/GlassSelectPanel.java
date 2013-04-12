
package gui.panels.subcontrolpanels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gui.panels.ControlPanel;

import javax.swing.*;

/**
 * The GlassSelectPanel class contains buttons allowing the user to select what
 * type of glass to produce.
 */
@SuppressWarnings("serial")
public class GlassSelectPanel extends JPanel
{
	/** The ControlPanel this is linked to */
	private ControlPanel parent;

	/**
	 * Creates a new GlassSelect and links it to the control panel
	 * @param cp
	 *        the ControlPanel linked to it
	 */
	public GlassSelectPanel(ControlPanel cp)
	{
		JCheckBox c1 = new JCheckBox("Cutter");
		this.add(c1);
		
		JButton addglass = new JButton("Add Glass");
		addglass.addActionListener(new add());
		
		
		this.setBackground(Color.white);
		parent = cp;
	}

	
	public class add implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
			
		}
		
	}
	
	/**
	 * Returns the parent panel
	 * @return the parent panel
	 */
	public ControlPanel getGuiParent()
	{
		return parent;
	}
}
