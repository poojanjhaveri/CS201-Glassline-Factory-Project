package GlassLine.Interfaces;

/** 
 * @author Dongyoung Jung
 * 
 * < SensorType >
 * public enum SensorType { ENTRY_SENSOR, MIDDLE_SENSOR, POPUP_SENSOR }
 */

import GlassLine.Shared.Glass;   // My file has this. You can delete this import.
import GlassLine.Shared.SharedData.SensorType;   // My file has this. You can delete this import.

public interface ConveyorFamily{
	
	public abstract void msgHereIsGlass( Glass glass ); //from previous CF
	
	//from operators
	public abstract void msgHereIsFinishedGlass(Operator operator, Glass glass);
	public abstract void msgIHaveGlassFinished(Operator operator);
	
	public abstract void msgIAmFree(); //from next CF
	public abstract String getName();
}
