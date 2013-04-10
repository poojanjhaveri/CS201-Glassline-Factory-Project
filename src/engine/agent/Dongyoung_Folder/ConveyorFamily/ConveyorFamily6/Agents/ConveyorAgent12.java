package engine.agent.Dongyoung_Folder.ConveyorFamily.ConveyorFamily6.Agents;

import engine.agent.Dongyoung_Folder.Conveyor;
import engine.interfaces.ConveyorFamily;


/**
 * @author Dongyoung
 */
public class ConveyorAgent12  extends Conveyor{

	// DATA
	private UVLampAgent uvLamp;
	private ConveyorFamily nextFamily;
	public ConveyorAgent12(String name, int num, int frontSensorNum, int backSensorNum){
		super(name, num, frontSensorNum, backSensorNum);
	}
	
	// ACTION
	protected void notifyIAmFreeAction(){
		uvLamp.msgIAmFree();
	}
	
	protected void passGlassAction(){
		nextFamily.msgHereIsGlass( glasses.remove(0) );
	}
	
	protected void glassLeaveFrontAction(){
		uvLamp.msgIAmFree();
	}
	
	/* Setter */
	public void setComps(UVLampAgent uvLamp, ConveyorFamily nextFamily){
		this.uvLamp = uvLamp;
		this.nextFamily = nextFamily;
	}
}