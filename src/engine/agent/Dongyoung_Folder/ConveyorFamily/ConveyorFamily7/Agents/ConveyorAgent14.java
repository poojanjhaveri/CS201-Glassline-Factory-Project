package engine.agent.Dongyoung_Folder.ConveyorFamily.ConveyorFamily7.Agents;

import engine.agent.Dongyoung_Folder.Conveyor;
import engine.interfaces.ConveyorFamily;


/**
 * @author Dongyoung
 */
public class ConveyorAgent14  extends Conveyor{

	// DATA	
	private OvenAgent oven;
	private ConveyorFamily nextFamily;
	public ConveyorAgent14(String name, int num, int frontSensorNum, int backSensorNum){
		super(name, num, frontSensorNum, backSensorNum);
	}
	
	// ACTION
	protected void notifyIAmFreeAction(){
		oven.msgIAmFree();
	}
	
	protected void passGlassAction(){
		nextFamily.msgHereIsGlass( glasses.remove(0) );
	}
	
	protected void glassLeaveFrontAction(){
		oven.msgIAmFree();
	}

	/* Setter */
	public void setComps(OvenAgent oven, ConveyorFamily nextFamily){
		this.oven = oven;
		this.nextFamily = nextFamily;
	}
}