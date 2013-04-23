package engine.agent.Dongyoung.Mock;

import shared.Glass;
import transducer.*;
import engine.interfaces.ConveyorFamily;
import engine.agent.Alex.*;

public class MockPreviousFamily implements ConveyorFamily, TReceiver {

	Transducer transducer;
	ConveyorFamily nextFamily;
	
	public MockPreviousFamily(ConveyorFamily nextFamily, Transducer transducer){
		this.nextFamily = nextFamily;
		this.transducer = transducer;
		transducer.register(this, TChannel.SENSOR);
	}

	@Override
	public void msgIAmFree() {
		System.out.println("Mock Previous Family : Next family is free!!!");
	}
	
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if( event == TEvent.SENSOR_GUI_RELEASED && (Integer)args[0] == 19 ){
			System.out.println("SENSOR!");
			Glass glass = new Glass(0, true, true, true, true, true, true, true, true, true, true);
			nextFamily.msgHereIsGlass(glass);
		}
	}

	@Override
	
	public String getName() {	return null;	}
	public void setNextConveyorFamily(ConveyorFamily nextFamily) {}
	public void startThreads() {}
	public void setPreviousConveyorFamily(ConveyorFamily c2) {}
	public void msgHereIsGlass(Glass glass) {}
	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {}
	public void msgIHaveGlassFinished(Operator operator) {}

	@Override
	public void setConveyorBroken(boolean s, int conveyorno) {
		// TODO Auto-generated method stub
		
	}
}
