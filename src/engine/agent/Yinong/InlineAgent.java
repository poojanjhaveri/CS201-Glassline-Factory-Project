package engine.agent.Yinong;

import shared.*;
import shared.enums.SharedData.WorkType;
import transducer.*;
import engine.agent.Agent;
import engine.interfaces.*;
import engine.interfaces.Yinong.Conveyor;
import engine.interfaces.Yinong.Inline;

import java.util.concurrent.*;

public class InlineAgent extends Agent implements Inline {
	
	//data
	int index;
	String function;
	Transducer transducer;
	TChannel channel;
	
	Glass glassOnSpot;
	ConveyorFamily next;
	boolean nextFree;
	
	Conveyor conveyor;
	Semaphore machineSemaphore = new Semaphore(0, true);
	
	//Constructor
	public InlineAgent(int i, String n, String func) {
		index = i;
		name = n;
		function = func;
		
		glassOnSpot = null;
		nextFree = true;
	}

	//Messages and Eventfires
	@Override
	public void eventFired(TChannel c, TEvent event, Object[] args) {
		if(channel == c) {
			if(event == TEvent.WORKSTATION_LOAD_FINISHED)
				machineSemaphore.release();
			if(event == TEvent.WORKSTATION_GUI_ACTION_FINISHED)
				machineSemaphore.release();
			if(event == TEvent.WORKSTATION_RELEASE_FINISHED)
				machineSemaphore.release();
		}
	}

	public void msgHereIsGlass(Glass glass) {
		glassOnSpot = glass;
		stateChanged();
	}

	public void msgIAmFree() {
		nextFree = true;
		stateChanged();
	}
	
	//Scheduler
	@Override
	public boolean pickAndExecuteAnAction() {
		if(glassOnSpot != null) {
			processGlass();
			return true;
		}
		return false;
	}

	//Actions
	private void processGlass() {
		Do("Processing Glass");
		try {
			machineSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(glassOnSpot.getRecipe(channel) ) {
			transducer.fireEvent(channel, TEvent.WORKSTATION_DO_ACTION, null);
			try {
				machineSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		while(! nextFree) {}
		//Do("LOOK! I'm here");
		next.msgHereIsGlass(glassOnSpot);
		transducer.fireEvent(channel, TEvent.WORKSTATION_RELEASE_GLASS, null);
		try {
			machineSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		glassOnSpot = null;
		conveyor.msgInlineFree();
		//Do("LOOK! I'm here");
	}

	//Getters, Setters and Hacks
	public String getName() {
		return name;
	}
	
	public void setTransducer(Transducer t) {
		transducer = t;
		t.register(this, channel);
	}
	
	//Do this first.
	public void setChannel(TChannel c) {
		channel = c;
	}
	

	public void setNextConveyorFamily(ConveyorFamily cf) {
		next = cf;
	}
	
	public void setConveyor(Conveyor c) {
		conveyor = c;
	}
	
	//Test
	public Glass getInlineGlass() {
		return glassOnSpot;
	}
	
	public void setNextFree(boolean nf) {
		nextFree = nf;
	}
	
}
