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
	
	public abstract void msgSensorPressed( SensorType type ); // From Transducer
	
	public abstract void msgSensorReleased( SensorType type ); // From Transducer
	
	public abstract void msgHereIsGlass( Glass glass );
		
	public abstract String getName();
}