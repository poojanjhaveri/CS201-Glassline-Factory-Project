package engine.agent.Dongyoung_Folder.ConveyorFamily.ConveyorFamily5.Agents;

import engine.agent.Dongyoung_Folder.Conveyor;
import engine.interfaces.ConveyorFamily;

/**
 * @author Dongyoung
 */
public class ConveyorAgent10 extends Conveyor{
	
	// DATA
	private ConveyorFamily previousFamily;
	private PaintAgent paint;
	
	public ConveyorAgent10(String name, int num, int frontSensorNum, int backSensorNum){
		super(name, num, frontSensorNum, backSensorNum);
	}

	// ACTION
	protected void notifyIAmFreeAction(){
		previousFamily.msgIAmFree();
	}
	
	protected void passGlassAction(){
		paint.msgHereIsGlass( glasses.remove(0) );
	}
	
	protected void glassLeaveFrontAction(){
		previousFamily.msgIAmFree();
	}
	
	/* Setter */
	public void setComps(ConveyorFamily previousFamily, PaintAgent paint){
		this.previousFamily = previousFamily;
		this.paint = paint;
	}
}