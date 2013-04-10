package engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily5;

import engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily5.Agents.*;
import engine.interfaces.ConveyorFamily;
import engine.interfaces.Operator;

import shared.Glass;
import transducer.Transducer;

/**
 * @author Dongyoung Jung
 */
public class ConveyorFamily5 implements ConveyorFamily {
	
	private ConveyorAgent10 conveyor10 = new ConveyorAgent10("CONVEYOR 10", 10, 20, 21);
	private PaintAgent paint = new PaintAgent("PAINT");
	
	public ConveyorFamily5(){}

	public void setComps(ConveyorFamily previousFamily, ConveyorFamily nextFamily, Transducer transducer){
		conveyor10.setComps(previousFamily, paint);
		conveyor10.setTransducer(transducer);
		paint.setComps(conveyor10, nextFamily);
		paint.setTransducer(transducer);
	}
	
	@Override
	public void msgHereIsGlass(Glass glass) {
		conveyor10.msgHereIsGlass(glass);
	}

	@Override
	public void msgIAmFree() {
		paint.msgIAmFree();
	}

	@Override
	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIHaveGlassFinished(Operator operator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
