package SharedData.Interface;

import SharedData.Glass;

public interface ConveyorFamily{
	
	public abstract void msgHereIsGlass( Glass glass ); //from previous CF
	
	//from operators
	//public abstract void msgHereIsFinishedGlass(Operator operator, Glass glass);
	//public abstract void msgIHaveGlassFinished(Operator operator);
	
	public abstract void msgIAmFree(); //from next CF
	//public abstract String getName();
}
