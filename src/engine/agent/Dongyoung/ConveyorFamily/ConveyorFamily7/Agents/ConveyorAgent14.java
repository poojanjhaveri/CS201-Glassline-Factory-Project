package engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily7.Agents;

import engine.agent.Dongyoung.Conveyor;
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
	
	/* Setter */
	public void setComps(OvenAgent oven, ConveyorFamily nextFamily){
		this.oven = oven;
		this.nextFamily = nextFamily;
	}
}