package engine.test.mock;

import shared.Glass;
import engine.interfaces.*;

public class MockConveyorFamily extends MockAgent implements ConveyorFamily {

	public MockConveyorFamily(String name) {
		super(name);
		
	}
	
	public EventLog log = new EventLog();

	@Override
	public void msgHereIsGlass(Glass glass) {
		log.add(new LoggedEvent(
				"Received message msgHereIsGlass"));
	}

	@Override
	public void msgHereIsFinishedGlass(Glass glass) {
		log.add(new LoggedEvent(
				"Received message msgHereIsFinishedGlass"));
	}

	@Override
	public void msgIAmFree() {
		log.add(new LoggedEvent(
				"Received message msgIAmFree"));
	}
	
	

}
