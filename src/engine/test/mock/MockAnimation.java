package engine.test.mock;

import transducer.*;

public class MockAnimation extends MockAgent implements TReceiver {
	
	public MockAnimation(String name) {
		super(name);
		
	}

	Transducer transducer;
	TEvent event;

	public EventLog log = new EventLog();
	
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(args != null) {
			log.add(new LoggedEvent(
					"Event fired on channel " + channel.toString() + ". Event is " + event.toString()
					+ " with parameters " + args.toString() ) );
		} else {
			log.add(new LoggedEvent(
					"Event fired on channel " + channel.toString() + ". Event is " + event.toString()
					 ) );
		}
		
	}
	
	public void setTransducer(Transducer t) {
		transducer = t;
		transducer.register(this, TChannel.CONVEYOR);
		transducer.register(this, TChannel.POPUP);
		transducer.register(this, TChannel.WASHER);
	}
	
}
