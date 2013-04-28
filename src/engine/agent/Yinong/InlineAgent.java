package engine.agent.Yinong;

import shared.*;
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
	boolean isBroken;
	
	Conveyor conveyor;
	Semaphore brokenSemaphore = new Semaphore(0, true);
	Semaphore machineSemaphore = new Semaphore(0, true);
	Semaphore nextSemaphore = new Semaphore(1, true);
	
	//Constructor
	private InlineAgent () {}
	
	public InlineAgent(int i, String n, String func) {
		index = i;
		name = n;
		function = func;
		
		glassOnSpot = null;
		nextFree = true;
		isBroken = false;
	}

	//Messages and Eventfires
	@Override
	public void eventFired(TChannel c, TEvent event, Object[] args) {
		Do("My Channel: "+this.channel.toString()+", Channel Received: "+c.toString()
				+", Event Received: "+event.toString());
		if(channel == c) {
			if(event == TEvent.WORKSTATION_LOAD_FINISHED) {
				machineSemaphore.release();
				Do("Glass loading finished.");
			}
			if(event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
				machineSemaphore.release();
				Do("Glass processing finished.");
			}
			if(event == TEvent.WORKSTATION_RELEASE_FINISHED) {
				machineSemaphore.release();
				Do("Glass unloading finished.");
			}
			if(event == TEvent.WORKSTATION_BROKEN) {
				Do("Reached here.");
				isBroken = true;
				stateChanged();
			}
			if(event == TEvent.WORKSTATION_FIXED) {
				Do("Reached there.");
				brokenSemaphore.release();
				isBroken = false;
				stateChanged();
			}
		}
	}

	public void msgHereIsGlass(Glass glass) {
		glassOnSpot = glass;
		Do("Received a glass.");
		stateChanged();
	}

	public void msgIAmFree() {
		Do("Received msgIAmFree from the next conveyor (family).");
		nextFree = true;
		nextSemaphore.release();
		stateChanged();
	}
	
	//Scheduler
	@Override
	public boolean pickAndExecuteAnAction() {
		if(glassOnSpot != null && ! isBroken ) {
			processGlass();
			return true;
		}
		return false;
	}

	//Actions
	private void processGlass() {
		checkBroken();
		Do("Waiting glass to be fully loaded into machine.");
		//STEP 1: Wait until GUI finishes loading the glass.
		try {
			machineSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		checkBroken();
		//STEP 2: GUI finishes loading; make GUI process the glass and wait until it's done
		Do("Asking GUI machine to process the glass. I'll wait until it's finished.");
		if(glassOnSpot.getRecipe(channel) ) {
			transducer.fireEvent(channel, TEvent.WORKSTATION_DO_ACTION, null);
			try {
				machineSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		checkBroken();
		//STEP 3: Code will get stuck here unless next thing is free
		try {
			nextSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//Do("LOOK! I'm here");
		//STEP 4: Release glass to the next thing
		checkBroken();
		Do("Asking GUI machine to release the glass. I'll wait until it's fully released.");
		next.msgHereIsGlass(glassOnSpot);
		transducer.fireEvent(channel, TEvent.WORKSTATION_RELEASE_GLASS, null);
		try {
			machineSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		checkBroken();
		//STEP 5: Unloading finished. Notify the conveyor that I'm free.
		Do("Notifying conveyor that I'm free.");
		glassOnSpot = null;
		nextFree = false;
		conveyor.msgInlineFree();
		//Do("LOOK! I'm here");
		checkBroken();
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
	
	public void setInlineBroken(boolean b, TChannel c) {
		if(channel == c) {
			isBroken = b;
			stateChanged();
		}
	}
	
	private void checkBroken() {
		if(isBroken) {
			try {
				brokenSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
