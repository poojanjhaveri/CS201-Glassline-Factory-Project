package engine.interfaces.Yinong;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;

public interface Conveyor {
	
	public abstract void eventFired(TChannel channel, TEvent event, Object[] args);
	public abstract void msgHereIsGlass(Glass glass);
	public abstract void msgPopupBusy();
	public abstract void msgPopupFree();
	public abstract void msgInlineFree();
	public abstract String getName();
	public abstract void setPopup(Popup popup);
	public abstract void setInline(Inline inline);
	
}
