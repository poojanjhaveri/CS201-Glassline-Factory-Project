package engine.agent;

import interfaces.Conveyor_LV;
import interfaces.ConveyorFamily;
import interfaces.PopUp_LV;

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
	SensorState sensorOne;
	SensorState sensorTwo;
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
		
		if(sensorTwo == SensorState.RELEASED)
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
		System.out.println("starting conveyor");
		Integer[] args = new Integer[1];
		args[0] = index;
		t.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
		moving = true; 
	}
	
	private void stopConveyor()
	{
		System.out.println("stopping conveyor");
		Integer[] args = new Integer[1];
		args[0] = index;
		t.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
		moving = false; 
	}
	
	private void letPopUpKnowGlassIsWaiting()
	{
		stopConveyor();
		System.out.println("Letting popup know glass is waiting");
		myPopUp.popUp.msgIHaveGlassReady(glassPieces.get(0));
	}
	
	private void moveToPopUp()
	{
		System.out.println("Giving glass to popUp");
		myPopUp.popUp.msgHereIsGlass(glassPieces.get(0));
		sensorTwo = SensorState.NULL;
		myPopUp.state = PopUpState.BUSY;
		
	}
	
	private void notifyPreviousFamily()
	{
		System.out.println("Letting previous Family know I am free");
		previousFamily.msgIAmFree();
		sensorOne = SensorState.NULL;
	}

	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		if(channel == TChannel.SENSOR)
		{
			if(event == TEvent.SENSOR_GUI_PRESSED)
			{
				if((Integer)(args[0])% 2 == 0)
					sensorOne = SensorState.PRESSED;
				else
					sensorTwo = SensorState.PRESSED;
			}
			else if(event == TEvent.SENSOR_GUI_RELEASED)
			{
				if((Integer)(args[0])% 2 == 0)
					sensorOne = SensorState.RELEASED;
				else
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
	
}
