package engine.interfaces;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;

public interface Inline_Y {
	
	public abstract void eventFired(TChannel c, TEvent event, Object[] args);
	public abstract void msgHereIsGlass(Glass glass);
	public abstract void msgIAmFree();
	public abstract String getName();
	
}
