package engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily7;

import engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily7.Agents.*;
import engine.interfaces.ConveyorFamily;
import engine.agent.Alex.*;
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
	
	public void setTransducer(Transducer transducer){
		conveyor13.setTransducer(transducer);
		conveyor14.setTransducer(transducer);
		oven.setTransducer(transducer);
		oven.setComps(conveyor13, conveyor14);
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
		conveyor14.setComps(oven, nextFamily);
	}

	@Override
	public void startThreads() {
		// Nothing
	}

	@Override
	public void setPreviousConveyorFamily(ConveyorFamily previousFamily) {
		conveyor13.setComps(previousFamily, oven);
	}
}
