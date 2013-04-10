package engine.interfaces.Yinong;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;

public interface Inline {
	
	public abstract void eventFired(TChannel c, TEvent event, Object[] args);
	public abstract void msgHereIsGlass(Glass glass);
	public abstract void msgIAmFree();
	public abstract String getName();
	
}
