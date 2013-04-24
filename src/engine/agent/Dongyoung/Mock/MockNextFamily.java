package engine.agent.Dongyoung.Mock;

import shared.Glass;
import transducer.TChannel;
import engine.interfaces.ConveyorFamily;
import engine.agent.Alex.*;

/**
 * @author Dongyoung Jung
 */
public class MockNextFamily implements ConveyorFamily {

	ConveyorFamily previousFamily;
	
	public MockNextFamily(ConveyorFamily previousFamily){
		this.previousFamily = previousFamily;
	}
	
	@Override
	public void msgHereIsGlass(Glass glass) {
		System.out.println("Mock Next Family : Got a glass!!");
		previousFamily.msgIAmFree();
	}
	
	@Override
	public String getName() {	return null;	}
	public void setNextConveyorFamily(ConveyorFamily nextFamily) {}
	public void startThreads() {}
	public void setPreviousConveyorFamily(ConveyorFamily c2) {}
	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {}
	public void msgIHaveGlassFinished(Operator operator) {}
	public void msgIAmFree() {}
	public void setConveyorBroken(boolean s, int conveyorno) {}
	public void setInlineBroken(boolean s, TChannel channel) {}
}
