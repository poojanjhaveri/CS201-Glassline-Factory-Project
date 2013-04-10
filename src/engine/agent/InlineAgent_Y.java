package engine.agent;

import shared.*;
import shared.enums.SharedData.WorkType;
import transducer.*;
import engine.interfaces.*;
import java.util.concurrent.*;

public class InlineAgent_Y extends Agent implements Inline_Y {
	
	//data
	int index;
	String function;
	Transducer transducer;
	TChannel channel;
	WorkType type;
	
	Glass glassOnSpot;
	ConveyorFamily next;
	boolean nextFree;
	
	Conveyor_Y conveyor;
	Semaphore machineSemaphore = new Semaphore(0, true);
	
	//Constructor
	public InlineAgent_Y(int i, String n, String func) {
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
		if(glassOnSpot.getRecipe(type) ) {
			transducer.fireEvent(channel, TEvent.WORKSTATION_DO_ACTION, null);
			try {
				machineSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		while(! nextFree) {}
		
		next.msgHereIsGlass(glassOnSpot);
		transducer.fireEvent(channel, TEvent.WORKSTATION_RELEASE_GLASS, null);
		try {
			machineSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		glassOnSpot = null;
		conveyor.msgInlineFree();
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
	
	public void setWorkType(WorkType wt) {
		type = wt;
	}

	public void setNextConveyorFamily(ConveyorFamily cf) {
		next = cf;
	}
	
	public void setConveyor(Conveyor_Y c) {
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