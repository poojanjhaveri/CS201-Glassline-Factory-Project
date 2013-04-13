package engine.agent.Luis;

import shared.Glass;
import transducer.Transducer;
import engine.agent.Alex.*;
import engine.interfaces.ConveyorFamily;

public class ConveyorFamilyAgent_LV implements ConveyorFamily{

	int index;
	ConveyorAgent_LV conveyor;
	PopUpAgent_LV popup;
	Transducer t;
	ConveyorFamily previousFamily, nextFamily;
	
	public ConveyorFamilyAgent_LV(int i, Transducer trans)
	{
		index = i;
		conveyor = new ConveyorAgent_LV("Conveyor " + index, index);
		popup = new PopUpAgent_LV("PopUp " + index, index);
		conveyor.setInteractions(previousFamily, popup, trans);
		popup.setInteractions(nextFamily, conveyor, trans);
	}
	
	public void msgHereIsGlass(Glass glass) {
		
		conveyor.msgHereIsGlass(glass);
		
	}

	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {
		
		popup.msgHereIsFinishedGlass(glass);
		
	}

	public void msgIHaveGlassFinished(Operator operator) {
		
		popup.msgIHaveGlassFinished(operator);
		
	}

	public void msgIAmFree() {
		
		popup.msgIAmFree();
		
	}

	public String getName() {
		return null;
	}

	@Override
	public void setNextConveyorFamily(ConveyorFamily c3) {
		// TODO Auto-generated method stub
		nextFamily=c3;
	}

	@Override
	public void setPreviousConveyorFamily(ConveyorFamily c2) {
		// TODO Auto-generated method stub
		previousFamily=c2;
	}

	@Override
	public void startThreads() {
		conveyor.startThread();
		popup.startThread();
	}

}
