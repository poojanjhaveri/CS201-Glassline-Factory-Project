package engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily7.Agents;

import engine.agent.Dongyoung.Conveyor;
import engine.interfaces.ConveyorFamily;


/**
 * @author Dongyoung
 */
public class ConveyorAgent13 extends Conveyor{

	// DATA
	private ConveyorFamily previousFamily;
	private OvenAgent oven;
	public ConveyorAgent13(String name, int num, int frontSensorNum, int backSensorNum){
		super(name, num, frontSensorNum, backSensorNum);
	}

	// ACTION
	protected void notifyIAmFreeAction(){
		previousFamily.msgIAmFree();
	}
	
	protected void passGlassAction(){
		oven.msgHereIsGlass( glasses.remove(0) );
	}
	
	/* Setter */
	public void setComps(ConveyorFamily previousFamily, OvenAgent oven){
		this.previousFamily = previousFamily;
		this.oven = oven;
	}
}