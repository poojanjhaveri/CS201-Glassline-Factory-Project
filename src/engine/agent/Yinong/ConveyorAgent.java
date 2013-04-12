package engine.agent.Yinong;

import java.util.*;
import shared.*;
import transducer.*;
import engine.agent.Agent;
import engine.interfaces.*;
import engine.interfaces.Yinong.*;
import engine.agent.Alex.*;

public class ConveyorAgent extends Agent implements Conveyor, ConveyorFamily {
	//data
	int conveyorIndex;
	Mode mode;
	public enum Mode {OFFLINE, ONLINE, MEDIATING};
	Transducer transducer;
	List<Glass> glasses = Collections.synchronizedList(new ArrayList<Glass> ());
	//Pop-up Agent
	Popup popup;
	//Inline Agent
	Inline inline;
	//Next Agent / Conveyor Family
	ConveyorFamily next;
	//Previous Conveyor Family
	ConveyorFamily previous;
	//State Variables
	State popupState;
	enum State {BUSY, FREE};
	
	State inlineState;
	
	State nextState;
	
	boolean conveyorRunning;
	
	SensorState sensor1State;
	SensorState sensor2State;
	enum SensorState {PRESSED, RELEASED, NOTHING};
	
	//Constructors
	
	private ConveyorAgent () {}
	
	public ConveyorAgent(String n, int index, Mode md) {
		name = n;
		conveyorIndex = index;
		mode = md;
		//State Initialization
		popupState = State.FREE;
		inlineState = State.FREE;
		conveyorRunning = false;
		sensor1State = SensorState.NOTHING;
		sensor2State = SensorState.NOTHING;
	}
	
	//Messages and Eventfires
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(channel == TChannel.SENSOR) {
			if(event == TEvent.SENSOR_GUI_PRESSED) {
				if( (Integer)(args[0]) % 2 == 0)
					sensor1State = SensorState.PRESSED;
				else
					sensor2State = SensorState.PRESSED;
			} else if(event == TEvent.SENSOR_GUI_RELEASED) {
				if( (Integer)(args[0]) % 2 == 0)
					sensor1State = SensorState.RELEASED;
				else
					sensor2State = SensorState.RELEASED;
			}
		}
		stateChanged();
	}

	public void msgHereIsGlass(Glass glass) {
		glasses.add(glass);
		stateChanged();
	}
	
	public void msgPopupBusy() {
		popupState = State.BUSY;
		stateChanged();
	}
	
	public void msgPopupFree() {
		popupState = State.FREE;
		stateChanged();
	}
	
	@Override
	public void msgInlineFree() {
		inlineState = State.FREE;
		stateChanged();
	}
	
	//Scheduler
	@Override
	public boolean pickAndExecuteAnAction() {
		if(mode == Mode.OFFLINE) {
			//Pop-up busy, a piece of glass is ready to enter; should prevent collision. Highest priority.
			if( (popupState == State.BUSY) && (sensor2State == SensorState.PRESSED) && (conveyorRunning) ) {
				stopConveyorAndNotify();
				return true;
			}
			//Pop-up cleared; start the conveyor if it's stopped
			if( (popupState == State.FREE) && (! conveyorRunning) ) {
				startConveyor();
				return true;
			}
			//Glass exiting the conveyor. Give the glass to the pop-up on the back end
			if( sensor2State == SensorState.RELEASED ) {
				giveGlassToPopup();
				return true;
			}
		} else if(mode == Mode.ONLINE) {
			//Inline busy, a piece of glass is ready to enter; should prevent collision. Highest priority.
			if( (inlineState == State.BUSY) && (sensor2State == SensorState.PRESSED) && (conveyorRunning) ) {
				stopConveyor();
				return true;
			}
			//Inline cleared; start the conveyor if it's stopped
			if( (inlineState == State.FREE) && (! conveyorRunning) ) {
				startConveyor();
				return true;
			}
			//Glass exiting the conveyor. Give the glass to the inline on the back end
			if( (sensor2State == SensorState.RELEASED) ) {
				giveGlassToInline();
				return true;
			}
		} else {
			if( (sensor2State == SensorState.RELEASED) ) {
				giveGlassToNextThing();
				return true;
			}
		}
		//Sensor 1 cleared. Spot's available for the previous conveyor family to push a new piece of glass.
		if( sensor1State == SensorState.RELEASED ) {
			notifyPrevious();
			return true;
		}
		
		return false;
	}
	
	private void giveGlassToNextThing() {
		Do("Giving glass to the next thing");
		next.msgHereIsGlass(glasses.remove(0));
		sensor2State = SensorState.NOTHING;
		nextState = State.BUSY;
	}

	private void notifyPrevious() {
		Do("Notifying the previous conveyor family that I'm free");
		previous.msgIAmFree();
		sensor1State = SensorState.NOTHING;
	}

	private void giveGlassToInline() {
		Do("Giving glass to the Inline Agent");
		inline.msgHereIsGlass(glasses.remove(0));
		sensor2State = SensorState.NOTHING;
		inlineState = State.BUSY;
	}


	private void giveGlassToPopup() {
		Do("Giving glass to the Pop-up Agent");
		popup.msgHereIsGlass(glasses.remove(0));
		sensor2State = SensorState.NOTHING;
		popupState = popupState.BUSY;
	}

	//Actions
	private void startConveyor() {
		Do("Starting conveyor");
		
		Integer[] idx = new Integer[1];
		idx[0] = conveyorIndex;
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, idx);
		conveyorRunning = true;
	}
	
	private void stopConveyor() {
		Do("Stoping conveyor");
		
		Integer[] idx = new Integer[1];
		idx[0] = conveyorIndex;
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, idx);
		conveyorRunning = false;
	}

	private void stopConveyorAndNotify() {
		stopConveyor();
		
		Do("Notifying the Pop-up");
		
		popup.msgIHaveGlass(glasses.get(0));
	}

	//Getters, Setters and Hacks
	public String getName() {
		return name;
	}
	
	public void setPopup(Popup p) {
		popup = p;
	}
	
	public void setInline(Inline i) {
		inline = i;
	}
	
	public void setPreviousConveyorFamily(ConveyorFamily c) {
		previous = c;
	}
	
	public void setTransducer(Transducer t) {
		transducer = t;
		transducer.register(this, TChannel.SENSOR);
	}
	
	//Tests
	public int getGlassListSize() {
		return glasses.size();
	}

	public boolean isRunning() {
		return conveyorRunning;
	}
	
	public void setPopupState(State p) {
		popupState = p;
	}
	
	public void setInlineState(State i) {
		inlineState = i;
	}
	
	public void setNextState(State n) {
		nextState = n;
	}
	
	public void setNextConveyorFamily(ConveyorFamily cf) {
		next = cf;
	}

	@Override
	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {
		Do("WARNING: msgHereIsFinishedGlass is called. It shouldn't be called as this is a conveyorAgent.");
		
	}


	@Override
	public void msgIHaveGlassFinished(Operator operator) {
		Do("WARNING: msgIHaveGlassFinished is called. It shouldn't be called as this is a conveyorAgent.");
	}

	@Override
	public void msgIAmFree() {
		nextState = State.FREE;
	}

	@Override
	public void startThreads() {
		this.startThread();
	}

}
