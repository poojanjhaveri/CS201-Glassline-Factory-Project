package engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily5.Agents;

import engine.agent.Dongyoung.Machine;
import engine.interfaces.ConveyorFamily;
import transducer.*;


/**
 * @author Dongyoung Jung
 */
public class PaintAgent extends Machine{

	// DATA	
	private ConveyorAgent10 conveyor10;
	private ConveyorFamily nextFamily;
	public PaintAgent(String name){
		super(name, TChannel.PAINTER);
	}
	
	// ACTION
	protected void notifyIAmFreeAction(){
		conveyor10.msgIAmFree();
	}
	
	protected void passGlassAction(){
		nextFamily.msgHereIsGlass( glass );
		glass = null;
	}	
	
	/* Setter */
	public void setComps(ConveyorAgent10 conveyor10, ConveyorFamily nextFamily){
		this.conveyor10 = conveyor10;
		this.nextFamily = nextFamily;
	}
}