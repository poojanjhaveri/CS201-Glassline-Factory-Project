package engine.test.mock;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import engine.interfaces.*;

public class MockOperator extends MockAgent implements Operator {

	public MockOperator(String name) {
		super(name);
	
	}

	public EventLog log = new EventLog();
	
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		log.add(new LoggedEvent(
				"Event fired on channel " + channel.toString() + ". Event is " + event.toString()
				+ " with parameters " + args.toString() ) );

	}

	@Override
	public void msgHereIsGlass(Glass glass) {
		log.add(new LoggedEvent(
				"Received message msgHereIsGlass"));
	}

	@Override
	public void msgIAmFree() {
		log.add(new LoggedEvent(
				"Received message msgIAmFree"));
		
	}
	
	

}
