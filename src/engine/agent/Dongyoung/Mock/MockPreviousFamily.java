package engine.agent.Dongyoung.Mock;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import engine.interfaces.ConveyorFamily;
import engine.agent.Alex.*;

public class MockPreviousFamily implements ConveyorFamily, TReceiver {

	Transducer transducer;
	ConveyorFamily nextFamily;
	
	public MockPreviousFamily(Transducer transducer){
		this.transducer = transducer;
		transducer.register(this, TChannel.SENSOR);
	}
	
	@Override
	public void msgHereIsGlass(Glass glass) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIHaveGlassFinished(Operator operator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIAmFree() {
		//System.out.println("Mock Previous Family : Next family is free!!!");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNextConveyorFamily(ConveyorFamily nextFamily) {
		this.nextFamily = nextFamily;	
	}

	@Override
	public void startThreads() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPreviousConveyorFamily(ConveyorFamily c2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if( event == TEvent.SENSOR_GUI_RELEASED && (Integer)args[0] == 19 ){
			//System.out.println("SENSOR!");
			Glass glass = new Glass(0, true, true, true, true, true, true, true, true, true, true);
			nextFamily.msgHereIsGlass(glass);
		}
	}

}
