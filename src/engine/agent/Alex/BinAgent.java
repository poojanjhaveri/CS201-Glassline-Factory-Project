package engine.agent.Alex;

import java.util.ArrayList;
import java.util.Queue;

import engine.agent.Agent;
import engine.agent.interfaces.ConveyorFamily;
import shared.Barcode;
import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class BinAgent extends Agent {
	/*
	 * Data
	 */
	V1_GUI gui;
	int numGlassCreated = 0;
	ArrayList<TransducerEvent> events;
	ArrayList<GlassRequest> requests;
	
	
	enum TransducerEvent {glassCreated}
	Transducer transducer;
	MyConveyorFamily ncCutter;
	boolean currentlyCreatingGlass = false;
	boolean nextCFFree = false;
	
	
	
	private class GlassRequest{
		public Glass glass;
		public boolean dealtWith;
		public GlassRequest(Glass g){
			glass = g;
			dealtWith = false;
			
		}
	}
	private class MyConveyorFamily {
		public ConveyorFamily conveyor;
		public MyConveyorFamily(ConveyorFamily cf){
			conveyor = cf;
		}
	}
	
	
	public BinAgent(String n, Transducer t, ConveyorFamily ncCutter, V1_GUI gui){
		super (n);
		this.gui = gui;
		events = new ArrayList<TransducerEvent>();
		requests = new ArrayList<GlassRequest>();
		
		this.ncCutter = new MyConveyorFamily(ncCutter);
		transducer = t;
		transducer.register(this, TChannel.BIN);
		
	}


	/*
	 * Messages:
	 */
	
	//from transducer
	private void msgGlassCreated(){
		System.out.println(name + ": Glass has been created msg recieved from transducer");
		events.add(TransducerEvent.glassCreated);
		stateChanged();
	}

	public void msgIAmFree(){
		nextCFFree = true;
		stateChanged();
	}

	public void msgCreateGlass(Barcode bc){
		System.out.println(name + ": Message to create glass recieved");
		boolean[] recipe = bc.translateToRecipe();
		
		GlassRequest glassReq = 
		new GlassRequest(
		new Glass(numGlassCreated++, recipe[0], recipe[1], 
				recipe[2], recipe[3],
				recipe[4], recipe[5], 
				recipe[6], recipe[7], 
				recipe[8], recipe[9]));
		requests.add(glassReq);
		stateChanged();
	}





	


	/*
	 * Scheduler
	 * 
	 */
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub

		for (TransducerEvent event: events){
			if (event == TransducerEvent.glassCreated)
			{
				giveGlassToNCCutter();
				return true;
			}
		}
		
		for (GlassRequest gr: requests){
			if (!gr.dealtWith && nextCFFree && !currentlyCreatingGlass){
				createGlassGUI(gr);
				return true;
			}
		}
		for (GlassRequest gr: requests){
			if (!gr.dealtWith && !nextCFFree ){
				warnInGUIWaitingForCFFree(gr);
				return true;
			}
		}
		
		for (GlassRequest gr: requests){
			if (!gr.dealtWith && currentlyCreatingGlass ){
				warnCreatingGlass(gr);
				return true;
			}
		}



		return false;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		if (channel == TChannel.BIN && event == TEvent.BIN_PART_CREATED){
			this.msgGlassCreated();
		}
	}
		/*
		 *Actions 
		 */
	


	private void createGlassGUI(GlassRequest gr){
		gr.dealtWith = true;
		currentlyCreatingGlass = true;
		Integer [] args = new Integer[1];
		transducer.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, args);
	}

	private void giveGlassToNCCutter(){
		System.out.println(name + ": Giving glass to nc Cutter");
		nextCFFree = false;
		//get first glass in requests
		ncCutter.conveyor.msgHereIsGlass(requests.remove(0).glass);
		currentlyCreatingGlass = false;
	}
	private void warnInGUIWaitingForCFFree(GlassRequest gr) {
		gr.dealtWith = true;
		gui.warnWaitingForCFFree();
		

	}
	private void warnCreatingGlass(GlassRequest gr){
		gr.dealtWith = true;
		gui.warnCreatingGlass();
	}


}
