package engine.interfaces;

/** 
 * @author Dongyoung Jung
 * 
 * < SensorType >
 * public enum SensorType { ENTRY_SENSOR, MIDDLE_SENSOR, POPUP_SENSOR }
 */

import shared.Glass;   // My file has this. You can delete this import.

public interface ConveyorFamily{
	
	public abstract void msgHereIsGlass( Glass glass ); //from previous CF
	
	//from operators
	public abstract void msgHereIsFinishedGlass(Operator operator, Glass glass);
	public abstract void msgIHaveGlassFinished(Operator operator);
	
	public abstract void msgIAmFree(); //from next CF
	public abstract String getName();
}