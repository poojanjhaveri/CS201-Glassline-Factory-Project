package engine.agent.Luis;

import engine.agent.Agent;
import engine.agent.Luis.Interface.*;
import engine.interfaces.ConveyorFamily;
import engine.agent.Alex.*;

import java.util.*;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class ConveyorAgent_LV extends Agent implements Conveyor_LV{
	
	int index;
	boolean moving;
	List<Glass> glassPieces;
	enum PopUpState {OPEN, BUSY};
	enum SensorState {PRESSED, RELEASED, NULL};
	MyPopUp myPopUp;
	SensorState sensorOne = SensorState.NULL;
	SensorState sensorTwo = SensorState.NULL;
	ConveyorFamily previousFamily;
	Transducer t;
	
	public class MyPopUp
	{
		PopUp_LV popUp;
		PopUpState state;
		
		public MyPopUp(PopUp_LV p)
		{
			popUp = p;
			state = PopUpState.OPEN;
		}
	}
	
	public ConveyorAgent_LV(String s, int i)
	{
		name = s;
		index = i;
		glassPieces = Collections.synchronizedList(new ArrayList<Glass>());
		moving = false;
		sensorOne = SensorState.NULL;
		sensorOne = SensorState.NULL;
	}
	
	/*
	 * MESSAGES
	 */

	public void msgHereIsGlass(Glass glass) 
	{
		glassPieces.add(glass);
		stateChanged();
	}
	
	public void msgPopUpBusy()
	{
		myPopUp.state = PopUpState.BUSY;
		stateChanged();
	}
	
	public void msgPopUpFree()
	{
		myPopUp.state = PopUpState.OPEN;
		stateChanged();
	}
	
	/*
	 * SCHEDULER
	 */

	public boolean pickAndExecuteAnAction() {
			
		if((myPopUp.state == PopUpState.BUSY) && (sensorTwo == SensorState.PRESSED) && (moving))
		{
			letPopUpKnowGlassIsWaiting();
			return true;
			}
			
		if((myPopUp.state == PopUpState.OPEN) && (!moving))
		{
			startUpConveyor();
			return true;
		}
		
		if(sensorTwo == SensorState.RELEASED && !(myPopUp.state == PopUpState.BUSY))
		{
			moveToPopUp();
			return true;
		}
		
		if(sensorOne == SensorState.RELEASED)
		{
			notifyPreviousFamily();
			return true;
		}

		return false;
	}
	
	/*
	 * ACTIONS
	 */
	
	private void startUpConveyor()
	{
		print("starting conveyor");
		Integer[] args = new Integer[1];
		args[0] = index;
		t.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
		moving = true; 
	}
	
	private void stopConveyor()
	{
		print("stopping conveyor");
		Integer[] args = new Integer[1];
		args[0] = index;
		t.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
		moving = false; 
	}
	
	private void letPopUpKnowGlassIsWaiting()
	{
		stopConveyor();
		print("Letting popup know glass is waiting");
		myPopUp.popUp.msgIHaveGlassReady(glassPieces.get(0));
	}
	
	private void moveToPopUp()
	{
		print("Giving glass to popUp");
		myPopUp.popUp.msgHereIsGlass(glassPieces.remove(0));
		sensorTwo = SensorState.NULL;
		myPopUp.state = PopUpState.BUSY;
		
	}
	
	private void notifyPreviousFamily()
	{
		print("Letting previous Family know I am free");
		previousFamily.msgIAmFree();
		sensorOne = SensorState.NULL;
	}

	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		if(channel == TChannel.SENSOR)
		{
			if(event == TEvent.SENSOR_GUI_PRESSED)
			{
				if((Integer)(args[0]) == index*2)
					sensorOne = SensorState.PRESSED;
				else if((Integer)(args[0]) == index*2 + 1)
					sensorTwo = SensorState.PRESSED;
			}
			else if(event == TEvent.SENSOR_GUI_RELEASED)
			{
				if((Integer)(args[0]) == index*2)
					sensorOne = SensorState.RELEASED;
				else if((Integer)(args[0]) == index*2+1)
					sensorTwo = SensorState.RELEASED;
			}
		}
		
		stateChanged();
		
	}

	@Override
	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIHaveGlassFinished(Operator operator) {
		// TODO Auto-generated method stub
		
	}
	
	public void setInteractions(ConveyorFamily cf, PopUp_LV popUp, Transducer trans)
	{
		previousFamily = cf;
		myPopUp = new MyPopUp(popUp);
		t = trans;
		t.register(this, TChannel.SENSOR);
	}
	
	public PopUp_LV getPopUp()
	{
		return myPopUp.popUp;
	}
	
	public void setPopUp(PopUp_LV p)
	{
		myPopUp.popUp = p;
	}
	
	public void setTransducer(Transducer trans)
	{
		t = trans;
		t.register(this, TChannel.SENSOR);
	}
	
	public ConveyorFamily getPrevious()
	{
		return previousFamily;
	}
	
	public int getGlassPiecesSize()
	{
		return glassPieces.size();
	}

	public void setInteractions(ConveyorFamily c2, PopUpAgent_LV popup) {
		// TODO Auto-generated method stub
		previousFamily = c2;
		setPopUp(popup);
	}
	
}
