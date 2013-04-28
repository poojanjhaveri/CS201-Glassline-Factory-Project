package engine.agent.Luis;

import shared.Glass;
import transducer.TChannel;
import transducer.Transducer;
import engine.agent.Alex.*;
import engine.interfaces.ConveyorFamily;

public class ConveyorFamilyAgent_LV implements ConveyorFamily{

	int index;
	ConveyorAgent_LV conveyor;
	PopUpAgent_LV popup;
	Transducer t;
	ConveyorFamily previousFamily, nextFamily;
	public TChannel mychannel;
	
	public ConveyorFamilyAgent_LV(int i, Transducer trans, int popupIndex)
	{
		index = i;
		conveyor = new ConveyorAgent_LV("Conveyor " + index, index, this);
		popup = new PopUpAgent_LV("PopUp " + index, popupIndex);
		conveyor.setInteractions(previousFamily, popup, trans);
		popup.setInteractions(nextFamily, conveyor, trans);
	}
	
	public void msgHereIsGlass(Glass glass) {
		
		conveyor.msgHereIsGlass(glass);
		
	}

	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {
		
		popup.msgHereIsFinishedGlass(operator,glass);
		
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
		
		popup.setInteractions(c3);
		
	}

	@Override
	public void setPreviousConveyorFamily(ConveyorFamily c2) {
		// TODO Auto-generated method stub
		conveyor.setInteractions(c2, popup);
	}


	public void startThreads() {
		conveyor.startThread();
		popup.startThread();
	}
	public void setChannel(TChannel tc){
		popup.channel = tc;
	}

	public void setOperators(Operator o5up, Operator o5down, TChannel c) {
		// TODO Auto-generated method stub
		popup.setOperators(o5up, o5down, c);
	}



	@Override
	public void setConveyorBroken(boolean s, int conveyorno) {
		conveyor.setBroken(s);
		
	}
	
	public void setPopUpBroken(boolean s)
	{
		popup.setPopUpBroken(s);
	}

	@Override
	public void setInlineBroken(boolean s, TChannel channel) {
		// TODO Auto-generated method stub
		
	}

	public void msgOperatorBroken(boolean i, int operatorNum) {
		System.out.println("Recieved message that operator" + operatorNum + "is/isnt working");
		popup.msgOperatorBroken(i, operatorNum);
		
	}

	public void msgIHaveNoGlass(Operator operator) {
		popup.msgIHaveNoGlass(operator);
	}

	public void breakNextGlass(int i) {
		// TODO Auto-generated method stub
		popup.msgBreakNextGlass(i);
	}

}
