package engine.agent.Luis.Interface;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.agent.Alex.*;
import engine.interfaces.ConveyorFamily;

public interface Conveyor_LV {
	
	public abstract void msgHereIsGlass(Glass glass);  
	
	public abstract void msgPopUpFree(); 

	public abstract void msgPopUpBusy(); 

	public abstract boolean pickAndExecuteAnAction(); 
	
	public abstract void eventFired(TChannel channel, TEvent event, Object[] args);

	public abstract void msgHereIsFinishedGlass(Operator operator, Glass glass);

	public abstract void msgIHaveGlassFinished(Operator operator);
	
	public abstract void setInteractions(ConveyorFamily cf, PopUp_LV popUp, Transducer trans);

}
