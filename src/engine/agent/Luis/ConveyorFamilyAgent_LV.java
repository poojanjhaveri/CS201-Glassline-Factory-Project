package engine.agent;

import shared.Glass;
import transducer.Transducer;
import interfaces.ConveyorFamily;

public class ConveyorFamilyAgent_LV implements ConveyorFamily{

	int index;
	ConveyorAgent_LV conveyor;
	PopUpAgent popup;
	Transducer t;
	ConveyorFamily previousFamily, nextFamily;
	
	public ConveyorFamilyAgent_LV(int i, Transducer trans)
	{
		index = i;
		conveyor = new ConveyorAgent_LV("Conveyor " + index, index);
		popup = new PopUpAgent("PopUp " + index, index);
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

}
