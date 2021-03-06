package engine.interfaces;

/** 
 * @author Dongyoung Jung
 * 
 * < SensorType >
 * public enum SensorType { ENTRY_SENSOR, MIDDLE_SENSOR, POPUP_SENSOR }
 */

import shared.Glass;   // My file has this. You can delete this import.
import transducer.TChannel;
import engine.agent.Alex.*;

public interface ConveyorFamily{
	
	public abstract void msgHereIsGlass( Glass glass ); //from previous CF
	
	//from operators
	public abstract void msgHereIsFinishedGlass(Operator operator, Glass glass);
	public abstract void msgIHaveGlassFinished(Operator operator);
	
	public abstract void msgIAmFree(); //from next CF
	public abstract String getName();
	
	public abstract void setNextConveyorFamily(ConveyorFamily c3);
	public abstract void setPreviousConveyorFamily(ConveyorFamily c2);
	
	public abstract void setConveyorBroken(boolean s,int conveyorno);
	public abstract void setInlineBroken(boolean s, TChannel channel);
	
	public abstract void startThreads();


	
}
