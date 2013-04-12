package engine.conveyorfamily.Interfaces_Poojan;

import engine.conveyorfamily_Poojan.ConveyorAgent_PJ;
import engine.conveyorfamily_Poojan.ConveyorFamily_PJ;
import engine.conveyorfamily_Poojan.OperatorAgent_PJ;
import shared.Glass;
import shared.enums.SharedData.SensorType;

public interface ConveyorFamilyInterface {

	
	//public abstract void msgSensorPressed( SensorType type ); // From Transducer

	//public abstract void msgSensorReleased( SensorType type ); // From Transducer

	public abstract void msgHereIsGlass(Glass glass) ;

	public abstract String getName();

	public abstract void msgIamFree();
	
	// WE need to add this
	public abstract boolean getStatusOfNextConveyorFamily();
	
	public abstract void setNextConveyorFamily(ConveyorFamilyInterface c3);
	


	public abstract void msgIHaveFinishedGlass(Operator_PJ operatorAgent);

	public abstract void msgHereIsFinishedGlass(Glass g,
			Operator_PJ operatorAgent);

	public void setStatusOfNextConveyorFamily(Boolean y) ;

	
}
