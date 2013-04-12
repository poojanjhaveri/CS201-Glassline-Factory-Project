package engine.test.mock;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import engine.interfaces.*;

public class MockConveyor extends MockAgent implements Conveyor_Y {

	public MockConveyor(String name) {
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
	public void msgPopupBusy() {
		log.add(new LoggedEvent(
				"Received message msgPopupBusy"));
	}

	@Override
	public void msgPopupFree() {
		log.add(new LoggedEvent(
				"Received message msgPopupFree"));
	}

	@Override
	public void msgInlineFree() {
		log.add(new LoggedEvent(
				"Received message msgInlineFree"));
	}

	@Override
	public void setPopup(Popup_Y popup) {
		
	}

	@Override
	public void setInline(Inline_Y inline) {
		
	}

}
