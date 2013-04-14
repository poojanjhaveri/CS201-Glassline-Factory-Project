package engine.agent.Alex;
import engine.interfaces.*;

import java.util.ArrayList;

import engine.agent.Agent;


import transducer.*;

public class EntryAgent extends Agent {
	
	public EntryAgent(String name, AlexsConveyorFamily cf){
	//initialize it all!	
		super(name);
	conveyorStatus = ConveyorStatus.Free;
	lineStatus = LineStatus.freeNotNotified;
	parentCF = cf;
	
	events = new ArrayList<EntryEvent>();
	
	}
	
	public AlexsConveyorFamily parentCF;
	public ConveyorFamily previousCF;
	public ConveyorAgent conveyor;


	public enum LineStatus {busy, freeNotNotified, freeNotified};
	public enum SensorStatus {pressed, released};
	public SensorStatus sensorStatus = SensorStatus.released;
	public LineStatus lineStatus;

	public enum ConveyorStatus {Busy, Free};

	public  ConveyorStatus conveyorStatus;

	public enum EntryEvent {sensorPressed, sensorReleased};
	public ArrayList<EntryEvent> events;
//	MSGS:
	
	public void msgSensorPressed() {
		// TODO Auto-generated method stub
		String msg = new String(name + ": msg sensor pressed");
		System.out.println(msg);
		//log.add(new //loggedEvent(msg));
		EntryEvent event = EntryEvent.sensorPressed;
		sensorStatus = SensorStatus.pressed;
		lineStatus = LineStatus.busy;
		events.add(event);
		stateChanged();
	}

	public void msgSensorReleased() {
		// TODO Auto-generated method stub
		String msg = new String(name + ": msg sensor released");
		System.out.println(msg);
		//log.add(new //loggedEvent(msg));
		EntryEvent event = EntryEvent.sensorReleased;
		sensorStatus = SensorStatus.released;
		lineStatus = LineStatus.freeNotified;
		events.add(event);
		stateChanged();
	}

//	Scheduler:

	
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub

		
	if (!events.isEmpty())
	{
		EntryEvent event;
		event = events.remove(0);
		if (event == EntryEvent.sensorPressed){
			//told by the gui that a glass is on the sensor
			if (conveyorStatus == ConveyorStatus.Busy){
				stopConveyor();
				
			}
			else if (conveyorStatus == ConveyorStatus.Free){
				startConveyor();
				//let the glass move on through
				//pushGlass();
				
			}
		}
		else if (event == EntryEvent.sensorReleased){
			notifyPreviousCFFree();
		}
	
		return true;
	}
		
		


	if (lineStatus == LineStatus.freeNotNotified){
		notifyPreviousCFFree();
		return true;
	}
	return false;
}


//	ACTIONS:


	/*private void pushGlass() {
		// TODO Auto-generated method stub
		lineStatus = LineStatus.freeNotNotified;
		//dont stop conveyor
		conveyor.msgHereIsGlass();
	}*/

	private void startConveyor() {
		// TODO Auto-generated method stub
		
		lineStatus = LineStatus.freeNotNotified;
		parentCF.startConveyor();
	}

	private void stopConveyor() {
		// TODO Auto-generated method stub
		lineStatus = LineStatus.busy;
		parentCF.stopConveyor();
	}


	

	public void notifyPreviousCFFree(){
		//let previous cf know (through parent CF)
		previousCF.msgIAmFree();
		//change state
		lineStatus = LineStatus.freeNotified;
	}

	
	//Inherited, not used
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub

	}

	public void msgConveyorFree() {
		// TODO Auto-generated method stub
		String msg = new String(name +": msg conveyor free");
		System.out.println(msg);
		//log.add(new //loggedEvent(msg));
		conveyorStatus = conveyorStatus.Free;
		stateChanged();
	}

	public void setConveyorAgent(ConveyorAgent ca) {
		// TODO Auto-generated method stub
		conveyor = ca;
	}

	public void setPreviousConveyorFamily(ConveyorFamily c2) {
		// TODO Auto-generated method stub
		previousCF = c2;
	}


}
