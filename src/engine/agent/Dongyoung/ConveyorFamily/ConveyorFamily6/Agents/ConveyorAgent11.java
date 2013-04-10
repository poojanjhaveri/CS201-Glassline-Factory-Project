package engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily6.Agents;

import engine.agent.Dongyoung.Conveyor;
import engine.interfaces.ConveyorFamily;

/**
 * @author Dongyoung
 */
public class ConveyorAgent11 extends Conveyor{

	// DATA
	private ConveyorFamily previousFamily;
	private UVLampAgent uvLamp;
	
	public ConveyorAgent11(String name, int num, int frontSensorNum, int backSensorNum){
		super(name, num, frontSensorNum, backSensorNum);
	}
	
	// ACTION
	protected void notifyIAmFreeAction(){
		previousFamily.msgIAmFree();
	}
	
	protected void passGlassAction(){
		uvLamp.msgHereIsGlass( glasses.remove(0) );
	}	
	
	protected void glassLeaveFrontAction(){
		previousFamily.msgIAmFree();
	}
	
	/* Setter */
	public void setComps(ConveyorFamily previousFamily, UVLampAgent uvLamp){
		this.previousFamily = previousFamily;
		this.uvLamp = uvLamp;
	}
}