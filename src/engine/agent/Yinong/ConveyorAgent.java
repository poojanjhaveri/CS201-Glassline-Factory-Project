package engine.agent.Yinong;

import java.util.*;
import shared.*;
import transducer.*;
import engine.agent.Agent;
import engine.interfaces.*;
import engine.interfaces.Yinong.Conveyor;
import engine.interfaces.Yinong.Inline;
import engine.interfaces.Yinong.Popup;

public class ConveyorAgent extends Agent implements Conveyor, ConveyorFamily {
	//data
	int conveyorIndex;
	Mode mode;
	public enum Mode {OFFLINE, ONLINE};
	Transducer transducer;
	List<Glass> glasses = Collections.synchronizedList(new ArrayList<Glass> ());
	//Pop-up Agent
	Popup popup;
	//Inline Agent
	Inline inline;
	//Previous Conveyor Family
	ConveyorFamily previous;
	//State Variables
	PopupState popupState;
	enum PopupState {BUSY, FREE};
	
	InlineState inlineState;
	enum InlineState {BUSY, FREE};
	
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
		popupState = PopupState.FREE;
		inlineState = InlineState.FREE;
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
		popupState = PopupState.BUSY;
		stateChanged();
	}
	
	public void msgPopupFree() {
		popupState = PopupState.FREE;
		stateChanged();
	}
	
	@Override
	public void msgInlineFree() {
		inlineState = InlineState.FREE;
		stateChanged();
	}
	
	//Scheduler
	@Override
	public boolean pickAndExecuteAnAction() {
		if(mode == Mode.OFFLINE) {
			//Pop-up busy, a piece of glass is ready to enter; should prevent collision. Highest priority.
			if( (popupState == PopupState.BUSY) && (sensor2State == SensorState.PRESSED) && (conveyorRunning) ) {
				stopConveyorAndNotify();
				return true;
			}
			//Pop-up cleared; start the conveyor if it's stopped
			if( (popupState == PopupState.FREE) && (! conveyorRunning) ) {
				startConveyor();
				return true;
			}
			//Glass exiting the conveyor. Give the glass to the pop-up on the back end
			if( sensor2State == SensorState.RELEASED ) {
				giveGlassToPopup();
				return true;
			}
		} else {
			//Inline busy, a piece of glass is ready to enter; should prevent collision. Highest priority.
			if( (inlineState == InlineState.BUSY) && (sensor2State == SensorState.PRESSED) && (conveyorRunning) ) {
				stopConveyor();
				return true;
			}
			//Inline cleared; start the conveyor if it's stopped
			if( (inlineState == InlineState.FREE) && (! conveyorRunning) ) {
				startConveyor();
				return true;
			}
			//Glass exiting the conveyor. Give the glass to the inline on the back end
			if( (sensor2State == SensorState.RELEASED) ) {
				giveGlassToInline();
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
	
	private void notifyPrevious() {
		Do("Notifying the previous conveyor family that I'm free");
		previous.msgIAmFree();
		sensor1State = SensorState.NOTHING;
	}

	private void giveGlassToInline() {
		Do("Giving glass to the Inline Agent");
		inline.msgHereIsGlass(glasses.remove(0));
		sensor2State = SensorState.NOTHING;
		inlineState = InlineState.BUSY;
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
	
	public void setPopupState(PopupState p) {
		popupState = p;
	}
	
	public void setInlineState(InlineState i) {
		inlineState = i;
	}

	@Override
	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {
		// Do nothing.
		
	}

	@Override
	public void msgIAmFree() {
		// Do nothing.
		
	}

	@Override
	public void msgIHaveGlassFinished(Operator operator) {
		// Do nothing.
	}

}
