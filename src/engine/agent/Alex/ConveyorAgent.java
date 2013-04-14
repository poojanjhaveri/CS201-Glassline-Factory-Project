package engine.agent.Alex;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import engine.agent.Agent;
import engine.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;

public class ConveyorAgent extends Agent {
	private PopupStatus nextCFStatus;

	public ConveyorAgent(String n,AlexsConveyorFamily parent){
		super(n);
		popupPush = new Semaphore(1);
		nextCFStatus =  PopupStatus.free;
		conveyorState = ConveyorState.freeNotNotified;
		events = new ArrayList<ConveyorEvent>();

		parentCF = parent;
	}
	

	public void setEntryAgent(EntryAgent ea){
		entryAgent = ea;
	}
	
	
	public Semaphore popupPush;
	public AlexsConveyorFamily parentCF;
	public EntryAgent entryAgent;
	
	public enum ConveyorEvent {sensorPressed, sensorReleased};
	public ArrayList<ConveyorEvent> events;
	public enum ConveyorState {busy, freeNotNotified, freeNotified};
	public enum PopupStatus {busy, unknown, free};
	
	public ConveyorState conveyorState;
	private ConveyorFamily nextCF;
	
	public void msgSensorPressed() {
		// TODO Auto-generated method stub
		String msg = new String(name + ": msg sensor pressed");
		System.out.println(msg);
		//log.add(new //loggedEvent(msg));
		events.add(ConveyorEvent.sensorPressed);
		stateChanged();
	}

	public void msgSensorReleased() {
		// TODO Auto-generated method stub
		String msg = new String(name + ": msg sensor released");
		System.out.println(msg);
		//log.add(new //loggedEvent(msg));
		events.add(ConveyorEvent.sensorReleased);
		stateChanged();
	}

	/*public void msgHereIsGlass() {
		// TODO Auto-generated method stub
		
	}*/
	public void msgPopupIsReady(){
		String msg = new String(name + ": nextCF is ready");
		System.out.println(msg);
		//log.add(new //loggedEvent(msg));
		nextCFStatus = PopupStatus.free;
		popupPush.release();
	}
	
	@Override
	public boolean pickAndExecuteAnAction() {
		
		if (!events.isEmpty())
		{
			ConveyorEvent event;
			event = events.remove(0);
			if (event == ConveyorEvent.sensorPressed){
				try {
					pushGlass();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //multistep action, will use a semaphore to guarantee not to push on next cf
				/*
				//told by the gui that a glass is on the sensor
				if (popupStatus != PopupStatus.free){
					stopConveyor();
					
				}
				else if (popupStatus == PopupStatus.free){
					
					//let the glass move on through
					
					
				}*/
			}
			else if (event == ConveyorEvent.sensorReleased){
				notifyEntryFree();
			}
		
			return true;
		}
		
		if (conveyorState == ConveyorState.freeNotNotified){
			notifyEntryFree();
		}
		return false;
	}

	public void stopConveyor() {
		String msg = new String(name + ": stopping conveyor");
		System.out.println(msg);
		//log.add(new //loggedEvent(msg));
		parentCF.stopConveyor();
		conveyorState = ConveyorState.busy;
	}

	public void notifyEntryFree() {

		String msg = new String(name + ": notified entry agent that I'm free");
		
		System.out.println(msg);
		//log.add(new //loggedEvent(msg));
		entryAgent.msgConveyorFree();
		conveyorState = ConveyorState.freeNotified;;
		
	}
	public void startConveyor(){
		String msg = new String(name + ": starting conveyor");
		System.out.println(msg);
		//log.add(new //loggedEvent(msg));
		parentCF.startConveyor();
		
	}
	
	public void pushGlass() throws InterruptedException {
		String msg = new String(name + ": letting popup know i have glass, stopping conveyor in mean time...");
		System.out.println(msg);
		////log.add(new //loggedEvent(msg));
		
		//nextCF.msgHereIsGlass();
		//parentCF.pushGlassOnPopup();
		parentCF.pushGlassOnPopup();
		
		stopConveyor();
		
		popupPush.acquire();
		
		startConveyor();
		System.out.println(name + ": glass pushed to popup.");
		//assume popup is busy
		nextCFStatus = PopupStatus.busy;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void msgImFree() {
		// TODO Auto-generated method stub
		
	}


	public void setNextConveyorFamily(ConveyorFamily c3) {
		// TODO Auto-generated method stub
		nextCF = c3;
		}
	



}
