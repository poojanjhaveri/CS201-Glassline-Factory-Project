package engine.agent.Luis;

import java.util.*;
import java.util.concurrent.Semaphore;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

import engine.agent.Agent;
import engine.agent.Alex.Operator;
import engine.interfaces.ConveyorFamily;

public class Truck_Agent_LV extends Agent implements ConveyorFamily{

	String name;
	ConveyorFamily previousFamily;
	Transducer t;
	List<Glass> truckGlass;
	boolean broken= false;
	enum TruckState{PARKED,COMMUTING,ARRIVED,LOADING,BROKEN,FIXED,NEEDS_BREAK,NEEDS_FIX};
	Semaphore drivingSemaphore = new Semaphore(0,true);
	TruckState state;
	TruckState tempState;

	public Truck_Agent_LV(String n)
	{
		name = n;
		truckGlass = new ArrayList<Glass>();
		state = TruckState.ARRIVED;
	}

	//Messages
	public void msgHereIsGlass(Glass glass)
	{
		truckGlass.add(glass);
		stateChanged();
	}

	//Scheduler
	public boolean pickAndExecuteAnAction() {

	    if(state == TruckState.NEEDS_BREAK)
		{
		    breakTruck();
		    return true;
		}

	    if(state == TruckState.NEEDS_FIX)
		{
		    fixTruck();
		    return true;
		}
	    
	    if(state == TruckState.BROKEN)
		{
		    broken = true;
		    state = TruckState.PARKED;
		    return true;
		}
	    
	    if(state == TruckState.FIXED)
		{
		    broken = false;
		    damageControl();
		    return true;
		}

		if(state == TruckState.ARRIVED)
		{
			tellConveyorIAmFree();
			return true;
		}

		if(state == TruckState.LOADING)
		{
			if(!broken)
			{
				for(Glass g : truckGlass)
				{
					moveGlass(g);
					return true;
				}
			}
			else
			{
				return true;
			}
		}

		return false;
	}

	public void eventFired(TChannel channel, TEvent event, Object[] args) {

			if(channel == TChannel.TRUCK)
			{
				if(event == TEvent.TRUCK_GUI_LOAD_FINISHED)
				{
					state = TruckState.LOADING;
				}
				else if(event == TEvent.TRUCK_GUI_EMPTY_FINISHED)
				{
					state = TruckState.ARRIVED;
				}
				else if (event == TEvent.TRUCK_DO_BREAK){
					setBroken(true);
				}
				else if (event == TEvent.TRUCK_DO_FIX){
					setBroken(false);
				}
			}
	
			stateChanged();
	}

	//Actions
	public void tellConveyorIAmFree()
	{
		print("Letting conveyor know I can load glass");
		if(truckGlass.size()!=0)
			truckGlass.remove(0);
		previousFamily.msgIAmFree();
		state = TruckState.PARKED;
	}

	public void moveGlass(Glass g)
	{	
			print("Delivering Glass");
			Integer[] args = new Integer[1];
			args[0] = 0;
			t.fireEvent(TChannel.TRUCK, TEvent.TRUCK_DO_EMPTY,args);
			state = TruckState.COMMUTING;	
	}

    public void breakTruck()
    {
    	print("Truck broken");
    	state = TruckState.BROKEN;
    }

    public void fixTruck()
    {
    	print("Truck fixed");
    	state = TruckState.FIXED;
    }
    
    public void damageControl()
    {
    	if(truckGlass.size()!=0)
    	{
    		for(int i=0; i<truckGlass.size();i++)
			{
    			print("Delivering Glass");
    			Integer[] args = new Integer[1];
    			args[0] = 0;
    			t.fireEvent(TChannel.TRUCK, TEvent.TRUCK_DO_EMPTY,args);
    			state = TruckState.COMMUTING;	
			}
	    }
    	else
	    {
    		state = TruckState.ARRIVED;
    		stateChanged();
	    }
    }

	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {
		print("N/A");
	}

	public void msgIHaveGlassFinished(Operator operator) {
		print("N/A");
	}

	public void msgIAmFree() {
		print("N/A");
	}

	public void setNextConveyorFamily(ConveyorFamily c3) {
		print("N/A");
	}

	@Override
	public void setPreviousConveyorFamily(ConveyorFamily cf) {
		previousFamily = cf;		
	}

	public void setTransducer(Transducer trans){
		t = trans;
		t.register(this, TChannel.TRUCK);
	}

	public void setBroken(boolean s)
	{
		if(s)
		    {
			state = TruckState.NEEDS_BREAK;
			stateChanged();
		    }
		else
		    {
			state = TruckState.NEEDS_FIX;
			stateChanged();
		    }
	}

	public void startThreads() {
		this.startThread();
	}

	public String getName(){
		return name;
	}

	public void setConveyorBroken(boolean s, int conveyorno) {
	    setBroken(s);

	}

	public void setInlineBroken(boolean s, TChannel channel) {
		print("no inline for truck");
	}




}
