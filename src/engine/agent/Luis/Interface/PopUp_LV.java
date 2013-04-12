package engine.agent.Luis.Interface;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.agent.Alex.*;
//import engine.agent.Operator;
import engine.interfaces.ConveyorFamily;

public interface PopUp_LV 
{
	public abstract void msgIAmFree();
	
	public abstract void msgIHaveGlassReady(Glass glass);

	public abstract void msgHereIsGlass(Glass glass); 

	public abstract void msgIHaveGlassFinished(Operator operator); 
	
	public abstract void msgHereIsFinishedGlass(Glass glass); 
	
	public abstract void msgCannotPass();

	public abstract boolean pickAndExecuteAnAction();
	
	public abstract void eventFired(TChannel channel, TEvent event, Object[] args);

	public abstract void setOperators(Operator operatorOne, Operator operatorTwo, TChannel c);
	
	public abstract void setInteractions(ConveyorFamily cf, Conveyor_LV c, Transducer trans);
	
	public abstract String getName();
	
}
