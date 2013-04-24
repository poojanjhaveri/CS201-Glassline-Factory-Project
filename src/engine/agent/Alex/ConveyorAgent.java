package engine.agent.Alex;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import engine.agent.Agent;
import engine.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;

public class ConveyorAgent extends Agent {
	
	private AlexsConveyorFamily parentCF;
	private EntryAgent entryAgent;
	
	private enum NextCFStatus {BUSY, FREE};
	private enum SensorState {DOWN, UP};
	private enum ConveyorState {STOPPED, RUNNING};
	private enum BreakConveyor {BROKEN, NEED_BREAK, RUNNING, NEED_RUN};

	private BreakConveyor breakConveyor;
	
	public ConveyorState conveyorState;
	private NextCFStatus nextCFStatus;
	private SensorState sensorState;
	
	public ConveyorAgent(String n,AlexsConveyorFamily parent){
		super(n);
		nextCFStatus = NextCFStatus.FREE;
		conveyorState = ConveyorState.RUNNING;
		sensorState = SensorState.UP;
		breakConveyor = BreakConveyor.RUNNING;
		parentCF = parent;
	}
	
	
	public void setEntryAgent(EntryAgent ea){
		entryAgent = ea;
	}
	
	
	public void msgImFree() {
		print("Message recieved, I'm free");
		nextCFStatus = NextCFStatus.FREE;
		stateChanged();
	}


	
	public void msgSensorPressed() {
		print("Message,Sensor pressed");
		sensorState = SensorState.DOWN;
		stateChanged();
	}

	public void msgSensorReleased() {
		print("Message, sensor released");
		sensorState = SensorState.UP;
		stateChanged();
	}

	
	@Override
	public boolean pickAndExecuteAnAction() {
		if (breakConveyor == BreakConveyor.NEED_BREAK && conveyorState == ConveyorState.RUNNING){
			axnBreakConveyor();
			return true;
		}
		else if(breakConveyor == BreakConveyor.BROKEN){
			return false;
		}
		else if (breakConveyor == BreakConveyor.NEED_RUN && nextCFStatus == NextCFStatus.FREE){
			axnFixConveyor();
			return true;
		}
		if (sensorState == SensorState.DOWN && nextCFStatus == NextCFStatus.BUSY && conveyorState == ConveyorState.STOPPED){
			//already stopped, wait for free msg
			return true;
		}
		else if (sensorState == SensorState.DOWN && nextCFStatus == NextCFStatus.BUSY && conveyorState == ConveyorState.RUNNING){
			//sensor is pressed, but next is busy
			axnStopConveyor();
		}
		else if (sensorState == SensorState.DOWN && nextCFStatus == NextCFStatus.FREE && conveyorState == ConveyorState.STOPPED){
			axnStartConveyor();
		}
		else if (sensorState == SensorState.DOWN && nextCFStatus == NextCFStatus.FREE && conveyorState == ConveyorState.RUNNING){
			axnPushGlass();
		}
		else if (sensorState == SensorState.UP && nextCFStatus == NextCFStatus.BUSY && conveyorState == ConveyorState.STOPPED){
			//nothing on sensor
			
		}
		else if (sensorState == SensorState.UP && nextCFStatus == NextCFStatus.FREE && conveyorState == ConveyorState.STOPPED){
			axnStartConveyor();
		}
		else if (sensorState == SensorState.UP && nextCFStatus == NextCFStatus.BUSY && conveyorState == ConveyorState.RUNNING){
			//do nothing
		}
		else if (sensorState == SensorState.UP && nextCFStatus == NextCFStatus.FREE && conveyorState == ConveyorState.RUNNING){
			axnStartConveyor();
		}
		return false;
	}

	private void axnFixConveyor() {
		// TODO Auto-generated method stub
			axnStartConveyor();
			entryAgent.msgConveyorFree();
			breakConveyor = BreakConveyor.RUNNING;

	}


	private void axnBreakConveyor() {
		print("AXN, Break Conveyor");
		breakConveyor = BreakConveyor.BROKEN;
		axnStopConveyor();
	}


	private void axnPushGlass() {
		print("AXN, Push glass");
		nextCFStatus = NextCFStatus.BUSY;
		//PUSH THE GLASS
		parentCF.pushGlass();
		//SEND MSG TO ENTRYTHAT IM FREE
		entryAgent.msgConveyorFree();
	}
	
	public void axnStopConveyor() {
		print("AXN, Stop Conveyor");
		//SET STATE
		conveyorState = ConveyorState.STOPPED;
		//STOP CONVEYOR
		parentCF.stopConveyor();
	}

	public void axnStartConveyor(){
		print("AXN, Start conveyor");
		conveyorState = ConveyorState.RUNNING;
		
		parentCF.startConveyor();
		
	}


	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
	}


	public void setConveyorBroken(boolean s) {
		if (s && breakConveyor != BreakConveyor.BROKEN)
			breakConveyor = BreakConveyor.NEED_BREAK;
		else if (!s && breakConveyor != BreakConveyor.RUNNING)
			breakConveyor = BreakConveyor.NEED_RUN;
		stateChanged();
	}


}
