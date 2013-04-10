package engine.agent.Dongyoung_Folder.ConveyorFamily.ConveyorFamily6.Agents;

import engine.agent.Dongyoung_Folder.Machine;
import transducer.*;


/**
 * @author Dongyoung Jung
 */
public class UVLampAgent extends Machine{

	// DATA	
	private ConveyorAgent11 conveyor11;
	private ConveyorAgent12 conveyor12;
	public UVLampAgent(String name){
		super(name, TChannel.UV_LAMP);
	}
	
	// ACTION
	protected void notifyIAmFreeAction(){
		conveyor11.msgIAmFree();
	}
	
	protected void passGlassAction(){
		conveyor12.msgHereIsGlass( glass );
		glass = null;
	}	

	/* Setter */
	public void setComps(ConveyorAgent11 conveyor11, ConveyorAgent12 conveyor12){
		this.conveyor11 = conveyor11;
		this.conveyor12 = conveyor12;
	}
}