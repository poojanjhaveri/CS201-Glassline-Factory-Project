
package gui.panels;
/*
import engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily5.ConveyorFamily5;
import engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily6.ConveyorFamily6;
import engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily7.ConveyorFamily7;
import engine.agent.Dongyoung.Mock.MockNextFamily;
import engine.agent.Dongyoung.Mock.MockPreviousFamily;
import engine.agent.Dongyoung.Mock.TestAni;
*/
import engine.agent.Alex.BinAgent;
import engine.agent.Alex.Operator;
import engine.agent.Alex.V1_GUI;
import engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily5.ConveyorFamily5;
import engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily6.ConveyorFamily6;
import engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily7.ConveyorFamily7;
import engine.agent.Dongyoung.Mock.MockNextFamily;
import engine.agent.Dongyoung.Mock.MockPreviousFamily;
import engine.agent.Dongyoung.Mock.TestAni;
import engine.agent.Luis.*;

import engine.agent.Yinong.ConveyorAgent;
import engine.agent.Yinong.ConveyorAgent.Mode;
import engine.agent.Yinong.ConveyorFamilyAgents;

import engine.agent.Yinong.*;
import engine.conveyorfamily.Interfaces_Poojan.ConveyorFamilyInterface;
import engine.conveyorfamily_Poojan.ConveyorFamily_PJ;
import engine.interfaces.ConveyorFamily;
import gui.drivers.FactoryFrame;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

/**
 * The FactoryPanel is highest level panel in the actual kitting cell. The
 * FactoryPanel makes all the back end components, connects them to the
 * GuiComponents in the DisplayPanel. It is responsible for handing
 * communication between the back and front end.
 */
@SuppressWarnings("serial")
public class FactoryPanel extends JPanel
{
	/** The frame connected to the FactoryPanel */
	private FactoryFrame parent;

	/** The control system for the factory, displayed on right */
	private ControlPanel cPanel;

	/** The graphical representation for the factory, displayed on left */
	private DisplayPanel dPanel;

	/** Allows the control panel to communicate with the back end and give commands */
	private Transducer transducer;

	/**
	 * Constructor links this panel to its frame
	 */
	public FactoryPanel(FactoryFrame fFrame)
	{
		parent = fFrame;

		// initialize transducer
		transducer = new Transducer();
		transducer.startTransducer();

		// use default layout
		// dPanel = new DisplayPanel(this);
		// dPanel.setDefaultLayout();
		// dPanel.setTimerListeners();

		// initialize and run
		this.initialize();
		this.initializeBackEnd();
	}

	/**
	 * Initializes all elements of the front end, including the panels, and lays
	 * them out
	 */
	private void initialize()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		// initialize control panel
		cPanel = new ControlPanel(this, transducer);

		// initialize display panel
		dPanel = new DisplayPanel(this, transducer);

		// add panels in
		// JPanel tempPanel = new JPanel();
		// tempPanel.setPreferredSize(new Dimension(830, 880));
		// this.add(tempPanel);

		this.add(dPanel);
		this.add(cPanel);
	}

	/**
	 * Feel free to use this method to start all the Agent threads at the same time
	 */
	private void initializeBackEnd()
	{
		// ===========================================================================
		// TODO initialize and start Agent threads here
		// ===========================================================================
		System.out.println("Back end initialization finished.");
		
		// Poojan Jhaveri
		
		/*
		ConveyorFamily_PJ c1 = new  ConveyorFamily_PJ(0,transducer);
		transducer.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, null);
		c1.msgHereIsGlass(new Glass(0,true, true, true, true, true, true, true, false, false, false));
		ConveyorFamily_PJ c2 = new  ConveyorFamily_PJ(1,transducer);
		c1.setNextConveyorFamily(c2);
		System.out.println(c1.getNextConveyorFamily());
	
		ConveyorFamily_PJ c1 = new  ConveyorFamily_PJ(0,transducer);
		ConveyorFamily_PJ c2 = new  ConveyorFamily_PJ(1,transducer);
		c1.setNextConveyorFamily(c2);
		
		V1_GUI gui = new V1_GUI();
		BinAgent bin = new BinAgent("bin agent", transducer, c1, gui);
		gui.setBinAgent(bin);
		bin.startThread();
		*/
		//alex
		
		
		V1_GUI gui = new V1_GUI();
		this.cPanel.glassInfoPanel.add(gui);
		BinAgent bin = new BinAgent("bin agent", transducer, gui);
		gui.setBinAgent(bin);
		bin.startThread();
		ConveyorFamily_PJ c1 = new  ConveyorFamily_PJ(0,transducer,bin);
		ConveyorFamily c2 = new ConveyorFamilyAgents(2, "Breakout", false);
		bin.setNextConveyorFamily(c1);
		c1.setNextConveyorFamily(c2);
		c2.setPreviousConveyorFamily(c1);
		
		( (ConveyorFamilyAgents) c2).setChannel(TChannel.BREAKOUT);
		( (ConveyorFamilyAgents) c2).setTransducer(transducer);
		
		ConveyorFamily c3 = new ConveyorFamilyAgents(3, "Manual_Breakout", false);
		c2.setNextConveyorFamily(c3);
		( (ConveyorFamilyAgents) c3).setChannel(TChannel.MANUAL_BREAKOUT);
		( (ConveyorFamilyAgents) c3).setTransducer(transducer);
		c3.setPreviousConveyorFamily(c2);
		
		ConveyorFamily c4 = new ConveyorAgent("Conveyor4", 4, Mode.MEDIATING);
		c3.setNextConveyorFamily(c4);
		c4.setPreviousConveyorFamily(c3);
		( (ConveyorAgent) c4).setTransducer(transducer);
		
		ConveyorFamily c5 = new ConveyorFamilyAgent_LV(5, transducer);
		((ConveyorFamilyAgent_LV)c5).setChannel(TChannel.DRILL);	
		Operator o5up = new Operator("operator 5- up", TChannel.DRILL, 1);
		Operator o5down = new Operator("operator 5- down", TChannel.DRILL, 2);
		((ConveyorFamilyAgent_LV)c5).setOperators(o5up, o5down, TChannel.DRILL);
		c4.setNextConveyorFamily(c5);
		c5.setPreviousConveyorFamily(c4);
		
		ConveyorFamily c6 = new ConveyorFamilyAgent_LV(6, transducer);
		((ConveyorFamilyAgent_LV)c6).setChannel(TChannel.CROSS_SEAMER);
		Operator o6up = new Operator("operator 5- up", TChannel.CROSS_SEAMER, 3);
		Operator o6down = new Operator("operator 5- down", TChannel.CROSS_SEAMER, 4);
		((ConveyorFamilyAgent_LV)c6).setOperators(o6up, o6down, TChannel.CROSS_SEAMER);
		c5.setNextConveyorFamily(c6);
		c6.setPreviousConveyorFamily(c5);
		
		ConveyorFamily c7 = new ConveyorFamilyAgent_LV(7, transducer);
		((ConveyorFamilyAgent_LV)c7).setChannel(TChannel.GRINDER);

		Operator o7up = new Operator("operator 7- up", TChannel.GRINDER, 5);
		Operator o7down = new Operator("operator 7- down", TChannel.GRINDER, 6);
		((ConveyorFamilyAgent_LV)c7).setOperators(o7up, o7down, TChannel.GRINDER);
		c6.setNextConveyorFamily(c7);
		c7.setPreviousConveyorFamily(c6);
		
		//NEED MORE HERE TO HAVE CF1 SET UP: Start thread, set everything, etc.
		o5down.startThread();
		o5up.startThread();
		o6down.startThread();
		o6up.startThread();
		o7up.startThread();
		o7down.startThread();
		
		
		c1.startThreads();
		c2.startThreads();
		c3.startThreads();
		c4.startThreads();
		c5.startThreads();
		c6.startThreads();
		c7.startThreads();
		
		
		
		
		
		
	//	c1.startThreads();
	//	c2.startThreads();
	//	c3.startThreads();
	//	bin.startThreads();
		//c2.setNextConveyorFamily();
		
		//runDongyoung();
	}
	
	public void runDongyoung(){
		new TestAni(transducer);
		ConveyorFamily5 family5 = new ConveyorFamily5();
		ConveyorFamily6 family6 = new ConveyorFamily6();
		ConveyorFamily7 family7 = new ConveyorFamily7();
		MockPreviousFamily previousFamily = new MockPreviousFamily(transducer);
		MockNextFamily nextFamily = new MockNextFamily();
				
		previousFamily.setPreviousConveyorFamily(family5);
		
		family5.setTransducer(transducer);
		family6.setTransducer(transducer);
		family7.setTransducer(transducer);
		
		family5.setPreviousConveyorFamily(previousFamily);
		family5.setNextConveyorFamily(family6);
		family6.setPreviousConveyorFamily(family5);
		family6.setNextConveyorFamily(family7);
		family7.setPreviousConveyorFamily(family6);
		family7.setNextConveyorFamily(nextFamily);
	}

	/**
	 * Returns the parent frame of this panel
	 * 
	 * @return the parent frame
	 */
	public FactoryFrame getGuiParent()
	{
		return parent;
	}

	/**
	 * Returns the control panel
	 * 
	 * @return the control panel
	 */
	public ControlPanel getControlPanel()
	{
		return cPanel;
	}

	/**
	 * Returns the display panel
	 * 
	 * @return the display panel
	 */
	public DisplayPanel getDisplayPanel()
	{
		return dPanel;
	}
}
