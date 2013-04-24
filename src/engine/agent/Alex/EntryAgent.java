package engine.agent.Alex;

import engine.agent.Agent;


import transducer.*;

public class EntryAgent extends Agent {
	public AlexsConveyorFamily parentCF;
	public ConveyorAgent conveyor;
	private enum ConveyorStatus {BUSY, FREE};
	private enum SensorStatus {DOWN, UP};
	private boolean notifiedPreviousCF;
	private ConveyorStatus conveyorStatus;
	private SensorStatus sensorStatus;

	
	public EntryAgent(String name, AlexsConveyorFamily cf){
	//initialize it all!	
		super(name);

		
	conveyorStatus = ConveyorStatus.FREE;
	sensorStatus = SensorStatus.UP;
	notifiedPreviousCF = true;
	parentCF = cf;
	
	
	}

//	MSGS:

	public void msgConveyorFree() {
		print("Message conveyor free");
		conveyorStatus = ConveyorStatus.FREE;
		stateChanged();
	}
	
	public void msgSensorPressed() {
		print("Message, sensor pressed");
		sensorStatus = SensorStatus.DOWN;
		stateChanged();
	}

	public void msgSensorReleased() {
		print("Message, sensor released");
		sensorStatus = SensorStatus.UP;
		notifiedPreviousCF = false;
		stateChanged();
	}

//	Scheduler:

	
	@Override
	public boolean pickAndExecuteAnAction() {

		if (sensorStatus == SensorStatus.UP && conveyorStatus == ConveyorStatus.FREE && !notifiedPreviousCF){
			axnNotifyPreviousCFFree();
		}
		/*else if (sensorStatus == SensorStatus.DOWN && conveyorStatus == ConveyorStatus.FREE){
			//do nothing, let it move on
		}
		else if (sensorStatus == SensorStatus.UP && conveyorStatus == ConveyorStatus.BUSY){
			//do nothing, 
		}
		else if (sensorStatus == SensorStatus.DOWN && conveyorStatus == ConveyorStatus.BUSY){
			//do nothing
		}*/
		
		
		return true;
	}
		
		


//	ACTIONS:


	

	public void axnNotifyPreviousCFFree(){
		print("AXN, notified previous CF free");
		notifiedPreviousCF = true;
		parentCF.notifyPreviousCFFree();
	}

	

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub

	}


	public void setConveyorAgent(ConveyorAgent ca) {
		// TODO Auto-generated method stub
		conveyor = ca;
	}




}
