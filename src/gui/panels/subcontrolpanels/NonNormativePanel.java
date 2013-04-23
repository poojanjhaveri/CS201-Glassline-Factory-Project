
package gui.panels.subcontrolpanels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import engine.agent.Alex.BinAgent;
import engine.agent.Alex.V1_GUI;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ.MyCGlass;
import engine.interfaces.ConveyorFamily;
import gui.panels.ControlPanel;

import javax.swing.*;

import shared.Barcode;
import transducer.TChannel;

/**
 * The GlassSelectPanel class contains buttons allowing the user to select what
 * type of glass to produce.
 */
@SuppressWarnings("serial")
public class NonNormativePanel extends JPanel
{
	
	private List<ConveyorFamily> myconveyorfamilies = Collections.synchronizedList(new ArrayList<ConveyorFamily>());
	
	
	/** The ControlPanel this is linked to */
	private ControlPanel parent;
	String[] conveyornames = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" };
	
	JComboBox selectconv;
	
	
	/**
	 * Creates a new GlassSelect and links it to the control panel
	 * @param cp
	 *        the ControlPanel linked to it
	 */
	public NonNormativePanel(ControlPanel cp)
	{
		parent = cp;
		this.setBackground(Color.GRAY);
		JPanel glasschoose = new JPanel();
		glasschoose.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		selectconv = new JComboBox(conveyornames);
		selectconv.addActionListener(new selectglassfromdropdown());
		
		JButton breakbutton = new JButton("break");
		JButton unbreakbutton = new JButton("UNbreak");
		unbreakbutton.addActionListener(new unbreakbuttonaction());
		breakbutton.addActionListener(new breakbuttonaction());
		
		
		
		this.add(glasschoose);
		c.gridx=0;
		c.gridy=0;
		glasschoose.add(selectconv,c);
		this.add(breakbutton);
		this.add(unbreakbutton);

		
		glasschoose.setBackground(Color.GRAY);

	}

	
	
	
	public class breakbuttonaction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			myconveyorfamilies.get(selectconv.getSelectedIndex()).setConveyorBroken(true,selectconv.getSelectedIndex());
		}
		
	}
	
	
	
	public class unbreakbuttonaction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			myconveyorfamilies.get(selectconv.getSelectedIndex()).setConveyorBroken(false,selectconv.getSelectedIndex());
		}
		
	}
	
	

	public class selectglassfromdropdown implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			
			
		}
		
	}
	
	
	
	public int booleanToNumber(boolean b) {
	    return b ? 1 : 0;
	}
	
	
	public void setConveyorFamily(ConveyorFamily ctemp)
	{
		myconveyorfamilies.add(ctemp);
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
