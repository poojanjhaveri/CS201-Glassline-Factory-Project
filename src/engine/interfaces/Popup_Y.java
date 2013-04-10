package engine.interfaces;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;

public interface Popup_Y {
	
	public abstract void eventFired(TChannel channel, TEvent event, Object[] args);
	public abstract void msgHereIsGlass(Glass glass);
	public abstract void msgIHaveGlass(Glass glass);
	public abstract void msgIHaveGlassFinished(Operator operator, Glass glass);
	public abstract void msgHereIsFinishedGlass(Glass glass);
	public abstract void msgIAmFree();
	public abstract String getName();
	public abstract void setOperator(int index, Operator o, TChannel c,
			int operatorIndex);

}
