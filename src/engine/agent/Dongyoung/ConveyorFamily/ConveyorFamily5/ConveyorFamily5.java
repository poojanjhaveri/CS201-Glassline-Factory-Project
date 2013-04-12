package engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily5;

import engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily5.Agents.*;
import engine.interfaces.ConveyorFamily;
import engine.agent.Alex.*;

import shared.Glass;
import transducer.Transducer;

/**
 * @author Dongyoung Jung
 */
public class ConveyorFamily5 implements ConveyorFamily {
	
	private ConveyorAgent10 conveyor10 = new ConveyorAgent10("CONVEYOR 10", 10, 20, 21);
	private PaintAgent paint = new PaintAgent("PAINT");
	
	public ConveyorFamily5(){}

	public void setTransducer(Transducer transducer){
		conveyor10.setTransducer(transducer);
		paint.setTransducer(transducer);
	}
	
	@Override
	public void msgHereIsGlass(Glass glass) {
		System.out.println("Conveyor Family 5 received a glass from previous Conveyor Family");
		conveyor10.msgHereIsGlass(glass);
	}

	@Override
	public void msgIAmFree() {
		paint.msgIAmFree();
	}

	@Override
	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {
		// Nothing
	}

	@Override
	public void msgIHaveGlassFinished(Operator operator) {
		// Nothing
	}

	@Override
	public String getName() {
		// Nothing
		return null;
	}

	@Override
	public void setNextConveyorFamily(ConveyorFamily nextFamily) {
		paint.setComps(conveyor10, nextFamily);
	}

	@Override
	public void startThreads(){
		// Nothing
	}

	@Override
	public void setPreviousConveyorFamily(ConveyorFamily previousFamily) {
		conveyor10.setComps(previousFamily, paint);
	}
}
