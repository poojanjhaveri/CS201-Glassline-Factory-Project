package engine.agent.Yinong;

import shared.Glass;
import transducer.TChannel;
import engine.agent.Yinong.ConveyorAgent.Mode;
import engine.interfaces.*;
import engine.interfaces.Yinong.Operator;

//This is not truly an agent.
public class ConveyorFamilyAgents implements ConveyorFamily {
	
	//data
	int index;
	String function;
	ConveyorAgent conveyor;
	PopupAgent popup;
	InlineAgent inline;
	boolean isOffline;
	
	private ConveyorFamilyAgents() {}
	public ConveyorFamilyAgents(int i, String f, boolean offline) {
		index = i;
		function = f;
		isOffline = offline;
		
		if(isOffline) {			//Conveyor Family 1-3
			conveyor = new ConveyorAgent("Conveyor", index, Mode.OFFLINE);
			popup = new PopupAgent("Popup", index);
			conveyor.setPopup(popup);
			inline = null;
		} else {				//Conveyor Family 4-7
			conveyor = new ConveyorAgent("Conveyor", index, Mode.ONLINE);
			popup = null;
			inline = new InlineAgent(index, "Inline", "nothing");
			conveyor.setInline(inline);
		}
	}

	@Override
	public void msgHereIsGlass(Glass glass) {
		conveyor.msgHereIsGlass(glass);
	}

	@Override
	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {
		popup.msgHereIsFinishedGlass(glass);
	}
	@Override
	public void msgIAmFree() {
		if(isOffline)
			popup.msgIAmFree();
		else
			inline.msgIAmFree();
	}

	public void setOperator(int index, Operator o, TChannel c, int operatorIndex) {
		popup.setOperator(index, o, c, operatorIndex);
	}
	@Override
	public void msgIHaveGlassFinished(Operator operator) {
		popup.msgIHaveGlassFinished(operator);
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
