
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
import engine.agent.Alex.Operator;
import engine.agent.Alex.V1_GUI;
import engine.agent.Luis.ConveyorFamilyAgent_LV;
import engine.conveyorfamily_Poojan.ConveyorFamily_PJ;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ.MyCGlass;
import engine.interfaces.ConveyorFamily;
import gui.panels.ControlPanel;

import javax.swing.*;

import shared.Barcode;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

/**
 * The GlassSelectPanel class contains buttons allowing the user to select what
 * type of glass to produce.
 */
@SuppressWarnings("serial")
public class NonNormativePanel extends JPanel
{
	
	private class selectOfflineStationFromDropDown implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		}

	}

	Transducer transducer;

	private List<ConveyorFamily> myconveyorfamilies = Collections.synchronizedList(new ArrayList<ConveyorFamily>());
	private List<ConveyorFamily> myinlinefamilies = Collections.synchronizedList(new ArrayList<ConveyorFamily>());

	
	/** The ControlPanel this is linked to */
	private ControlPanel parent;
	String[] conveyornames = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14","Truck"};
	String[] inlinenames = { "NCCutter", "Breakout", "Manual Breakout","Drill","Cross-Seamer","Grinder", "Washer", "UV_Lamp", "Oven","Painter" };
	String[] offlineNames = { "Machine 1 - Up", "Machine 1- Down", "Machine 2 - Up", "Machine 2- Down","Machine 3 - Up", "Machine 3- Down"};
	String[] popupNames = {"5","6","7"};
	
	TChannel[] tchannelnames = { TChannel.CUTTER,TChannel.BREAKOUT,TChannel.MANUAL_BREAKOUT,TChannel.DRILL,TChannel.CROSS_SEAMER,TChannel.GRINDER,TChannel.WASHER,TChannel.UV_LAMP,TChannel.OVEN,TChannel.PAINTER };
	
	JComboBox selectconv;
	JComboBox selectinlineconv;
	JComboBox selectOffline;
	JComboBox selectPopup;
	private ArrayList<ConveyorFamily> offlineConveyorFamilies;
	private ArrayList<Operator> offlineAgents;
	
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
		
		selectinlineconv = new JComboBox(inlinenames);
		selectinlineconv.addActionListener(new selectinlinestationfromdropdown());
		
		selectOffline = new JComboBox(offlineNames);
		selectOffline.addActionListener(new selectOfflineStationFromDropDown());
		
		selectPopup = new JComboBox(popupNames);
		selectPopup.addActionListener(new selectPopupFromDropDown());
		
		JButton breakbutton = new JButton("Break");
		JButton unbreakbutton = new JButton("Unbreak");
		unbreakbutton.addActionListener(new unbreakbuttonaction());
		breakbutton.addActionListener(new breakbuttonaction());
		
		JButton breakinline = new JButton("Break");
		JButton unbreakinline = new JButton("Unbreak");
		unbreakinline.addActionListener(new unbreakinline());
		breakinline.addActionListener(new breakinline());
		
		JButton breakOffline = new JButton("Break");
		JButton unbreakOffline = new JButton("Unbreak");
		unbreakOffline.addActionListener(new unbreakOffline());
		breakOffline.addActionListener(new breakOffline());
		
		JButton breakOfflineGlass = new JButton ("Break Glass");
		breakOfflineGlass.addActionListener(new breakOfflineGlass());
		
		JButton breakPopup = new JButton("Break");
		JButton unbreakPopup = new JButton("Unbreak");
		breakPopup.addActionListener(new breakPopup());
		unbreakPopup.addActionListener(new unbreakPopup());
		
		this.add(glasschoose);
		
		
		c.gridx=0;
		c.gridy=0;
		glasschoose.add(new JLabel("CONVEYORS"),c);
		
		
		c.gridx=0;
		c.gridy=1;
		glasschoose.add(selectconv,c);
		
		c.gridx=1;
		glasschoose.add(breakbutton,c);
		
		c.gridx=2;
		glasschoose.add(unbreakbutton,c);
		
		
		
		c.gridx=0;
		c.gridy=3;
		glasschoose.add(new JLabel("INLINE"),c);
		
		
		
		c.gridx=0;
		c.gridy=4;
		glasschoose.add(selectinlineconv,c);
		
		c.gridx=1;
		glasschoose.add(breakinline,c);
		
		c.gridx=2;
		glasschoose.add(unbreakinline,c);
		
		c.gridx=0;
		c.gridy=5;
		glasschoose.add(new JLabel("OFFLINE"),c);
		c.gridy=6;
		
		glasschoose.add(selectOffline,c);
		c.gridx=1;
		glasschoose.add(breakOffline,c);
		
		c.gridx=2;
		glasschoose.add(unbreakOffline,c);
		c.gridy=7;
		c.gridx=1;
		glasschoose.add(breakOfflineGlass,c);
		
		c.gridx=0;
		c.gridy=8;
		glasschoose.add(new JLabel("POPUP"),c);
		c.gridy=9;
		glasschoose.add(selectPopup,c);
		c.gridx=1;
		glasschoose.add(breakPopup,c);
		c.gridx=2;
		glasschoose.add(unbreakPopup,c);
		
		

		
		glasschoose.setBackground(Color.GRAY);

	}

	
	
	
	public class breakbuttonaction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//Comment out the following 3 lines of codes when you need to do so.
			Integer idx[] = new Integer[1];
			idx[0] = selectconv.getSelectedIndex();
			
			if (idx[0] != 15)
				transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_BREAK, idx);
			else
				transducer.fireEvent(TChannel.TRUCK, TEvent.TRUCK_DO_BREAK, idx);
			
			myconveyorfamilies.get(selectconv.getSelectedIndex()).setConveyorBroken(true,selectconv.getSelectedIndex());
		}
		
	}
	
	
	
	public class unbreakbuttonaction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//Comment out the following 3 lines of codes when you need to do so.
			System.out.println("");
			Integer idx[] = new Integer[1];
			idx[0] = selectconv.getSelectedIndex();
			if (idx[0] != 15)
				transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_FIX, idx);
			else
				transducer.fireEvent(TChannel.TRUCK, TEvent.TRUCK_DO_FIX, idx);
			myconveyorfamilies.get(selectconv.getSelectedIndex()).setConveyorBroken(false,selectconv.getSelectedIndex());
		}
		
	}
	
	public class breakinline implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			//myinlinefamilies.get(selectinlineconv.getSelectedIndex()).setInlineBroken(true,tchannelnames[selectinlineconv.getSelectedIndex()]);
			transducer.fireEvent(tchannelnames[selectinlineconv.getSelectedIndex()], TEvent.WORKSTATION_DO_BREAK, null);
		}
		
	}
	
	
	
	public class unbreakinline implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//myinlinefamilies.get(selectinlineconv.getSelectedIndex()).setInlineBroken(false,tchannelnames[selectinlineconv.getSelectedIndex()]);
			transducer.fireEvent(tchannelnames[selectinlineconv.getSelectedIndex()], TEvent.WORKSTATION_DO_FIX, null);
		}
		
	}
	
	public class unbreakOffline implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("GUI unbreaking offline machine " + selectOffline.getSelectedIndex());
			ConveyorFamilyAgent_LV cfOffline = (ConveyorFamilyAgent_LV) offlineConveyorFamilies.get(selectOffline.getSelectedIndex()/2);
			cfOffline.msgOperatorBroken(false, selectOffline.getSelectedIndex() % 2);
		}
		
	}
	
	public class breakOfflineGlass implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("GUI breaking glass in offline machine " + selectOffline.getSelectedIndex());
			TChannel tc = null;
			Integer idx[] = new Integer[1];
			idx[0] = selectconv.getSelectedIndex()% 2;
			
			switch (selectOffline.getSelectedIndex()){
			case 0:
			case 1:
				tc = TChannel.GRINDER;
				break;
			case 2:
			case 3:
				tc = TChannel.DRILL;
				break;
			case 4:
			case 5:
				tc = TChannel.CROSS_SEAMER;
				break;
			}
			
			transducer.fireEvent(tc, TEvent.WORKSTATION_MISSING_GLASS, idx);
			ConveyorFamilyAgent_LV cfOffline = (ConveyorFamilyAgent_LV) offlineConveyorFamilies.get(selectOffline.getSelectedIndex()/2);
			cfOffline.breakNextGlass( selectOffline.getSelectedIndex() % 2);
		}
		
	}
	
	public class breakPopup implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class unbreakPopup implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	

	public class selectglassfromdropdown implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			
			
		}
		
	}
	
	public class selectinlinestationfromdropdown implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			
			
		}
	}
	
	public class selectPopupFromDropDown implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			
		}
		
	}
	
	private class breakOffline implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("GUI breaking offline machine " + selectOffline.getSelectedIndex());
			
			ConveyorFamilyAgent_LV cfOffline = (ConveyorFamilyAgent_LV) offlineConveyorFamilies.get(selectOffline.getSelectedIndex()/2);
			cfOffline.msgOperatorBroken(true, selectOffline.getSelectedIndex() % 2);
		}

	}
	
	
	public int booleanToNumber(boolean b) {
	    return b ? 1 : 0;
	}
	
	
	public void setConveyorFamily(ConveyorFamily ctemp,boolean b)
	{
		myconveyorfamilies.add(ctemp);
		if(b)
		{
		myinlinefamilies.add(ctemp);
		}
	}
	public void setOfflineConveyorFamilies(ArrayList<ConveyorFamily> cfList){
		offlineConveyorFamilies = cfList;
	}
	
	public void addOfflineAgent(Operator o){
		if (offlineAgents == null)
			offlineAgents = new ArrayList<Operator>();
		offlineAgents.add(o);
	}
	
	/**
	 * Returns the parent panel
	 * @return the parent panel
	 */
	public ControlPanel getGuiParent()
	{
		return parent;
	}


	public void setTransducer(Transducer transducer) {
		// TODO Auto-generated method stub
		this.transducer = transducer;
	}
}