package engine.test.mock;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import engine.interfaces.*;

public class MockPopup extends MockAgent implements Popup_Y {

	public MockPopup(String name) {
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
	public void msgIHaveGlass(Glass glass) {
		log.add(new LoggedEvent(
				"Received message msgIHaveGlass"));
		
	}

	@Override
	public void msgIHaveGlassFinished(Glass glass) {
		log.add(new LoggedEvent(
				"Received message  msgIHaveGlassFinished"));
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

	@Override
	public void setOperator(int index, Operator o, TChannel c, int operatorIndex) {

		
	}
	
}
