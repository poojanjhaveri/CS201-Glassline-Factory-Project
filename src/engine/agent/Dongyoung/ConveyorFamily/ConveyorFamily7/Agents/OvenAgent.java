package engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily7.Agents;

import engine.agent.Dongyoung.Machine;
import transducer.*;

/**
 * @author Dongyoung Jung
 */
public class OvenAgent extends Machine{

	// DATA
	private ConveyorAgent13 conveyor13;
	private ConveyorAgent14 conveyor14;
	public OvenAgent(String name){
		super(name, TChannel.OVEN);
	}

	// ACTION
	protected void notifyIAmFreeAction(){
		conveyor13.msgIAmFree();
	}
	
	protected void passGlassAction(){
		conveyor14.msgHereIsGlass( glass );
	}	

	/* Setter */
	public void setComps(ConveyorAgent13 conveyor13, ConveyorAgent14 conveyor14){
		this.conveyor13 = conveyor13;
		this.conveyor14 = conveyor14;
	}
}