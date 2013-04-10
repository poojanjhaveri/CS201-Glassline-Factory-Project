/**
 * 
 */
package mocks_PJ;


import shared.Glass;
import shared.enums.SharedData.SensorType;
import engine.conveyorfamily.Interfaces_Poojan.ConveyorFamilyInterface;
import engine.conveyorfamily.Interfaces_Poojan.Operator_PJ;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ;

/**
 * @author madiphone14
 *
 */
public class MockConveyorFamily implements ConveyorFamilyInterface {
	
	
	public EventLog log = new EventLog();

	/* (non-Javadoc)
	 * @see engine.conveyorfamily.Interfaces.ConveyorFamilyInterface#msgSensorPressed(shared.enums.SharedData.SensorType)
	 */
//	@Override
//	public void msgSensorPressed(SensorType type) {
		// TODO Auto-generated method stub

//	}

	/* (non-Javadoc)
	 * @see engine.conveyorfamily.Interfaces.ConveyorFamilyInterface#msgSensorReleased(shared.enums.SharedData.SensorType)
	 */
//	@Override
//	public void msgSensorReleased(SensorType type) {
		// TODO Auto-generated method stub

//	}

	/* (non-Javadoc)
	 * @see engine.conveyorfamily.Interfaces.ConveyorFamilyInterface#msgHereIsGlass(shared.Glass)
	 */
	@Override
	public void msgHereIsGlass(Glass glass) {
		// TODO Auto-generated method stub
		
		log.add(new LoggedEvent(
				"Received message msgHereIsGlass from Popup"+"for glass"+glass.getNumber()));
		
		
	}

	/* (non-Javadoc)
	 * @see engine.conveyorfamily.Interfaces.ConveyorFamilyInterface#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see engine.conveyorfamily.Interfaces.ConveyorFamilyInterface#msgIamFree()
	 */
	@Override
	public void msgIamFree() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent(
				"Received message msgIamFree() from Conveyor"));
		
		
	}

	/* (non-Javadoc)
	 * @see engine.conveyorfamily.Interfaces.ConveyorFamilyInterface#getStatusOfNextConveyorFamily()
	 */
	@Override
	public boolean getStatusOfNextConveyorFamily() {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public void msgIHaveFinishedGlass(Operator_PJ operatorAgent) {
		// TODO Auto-generated method stub
		
		log.add(new LoggedEvent(
				"Received message msgIHaveFinishedGlass from Operator "));
		
		
	}

	@Override
	public void msgHereIsFinishedGlass(Glass g, Operator_PJ operatorAgent) {
		// TODO Auto-generated method stub
		
		log.add(new LoggedEvent(
				"Received message msgHereIsFinishedGlass for Glass"+ g.getNumber()+"for operator"));
		
	}

	@Override
	public void setStatusOfNextConveyorFamily(Boolean y) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public ConveyorFamilyInterface getNextConveyorFamily() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNextConveyorFamily(ConveyorFamilyInterface c3) {
		// TODO Auto-generated method stub
		
	}

	

}
