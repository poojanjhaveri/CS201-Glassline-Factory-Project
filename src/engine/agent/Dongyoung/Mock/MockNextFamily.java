package engine.agent.Dongyoung.Mock;

import shared.Glass;
import engine.interfaces.ConveyorFamily;
import engine.interfaces.Operator;

/**
 * @author Dongyoung Jung
 */
public class MockNextFamily implements ConveyorFamily {

	@Override
	public void msgHereIsGlass(Glass glass) {
		System.out.println("Mock Next Family : Got a glass!!");
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNextConveyorFamily(ConveyorFamily c3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startThreads() {
		// TODO Auto-generated method stub
		
	}

}
