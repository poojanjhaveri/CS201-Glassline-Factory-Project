package engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily7;

import engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily7.Agents.*;
import engine.interfaces.ConveyorFamily;
import engine.interfaces.Operator;
import shared.Glass;
import transducer.Transducer;
/**
 * @author Dongyoung Jung
 */
public class ConveyorFamily7 implements ConveyorFamily {

	private ConveyorAgent13 conveyor13 = new ConveyorAgent13("CONVEYOR 13", 13, 26, 27);
	private ConveyorAgent14 conveyor14 = new ConveyorAgent14("CONVEYOR 14", 14, 28, 29);
	private OvenAgent oven = new OvenAgent("OVEN");
	
	public ConveyorFamily7(){}
	
	public void setComps(ConveyorFamily previousFamily, ConveyorFamily nextFamily, Transducer transducer){
		conveyor13.setComps(previousFamily, oven);
		conveyor13.setTransducer(transducer);
		conveyor14.setComps(oven, nextFamily);
		conveyor14.setTransducer(transducer);
		oven.setComps(conveyor13, conveyor14);
		oven.setTransducer(transducer);
	}

	@Override
	public void msgHereIsGlass(Glass glass) {
		conveyor13.msgHereIsGlass(glass);
	}

	@Override
	public void msgIAmFree() {
		conveyor14.msgIAmFree();
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

	@Override
	public void setNextConveyorFamily(ConveyorFamily c3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startThreads() {
		// TODO Auto-generated method stub
		
	}
}
