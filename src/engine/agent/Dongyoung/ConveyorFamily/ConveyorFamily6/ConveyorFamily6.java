package engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily6;

import engine.agent.Dongyoung.ConveyorFamily.ConveyorFamily6.Agents.*;
import engine.interfaces.ConveyorFamily;
import engine.agent.Alex.*;
import shared.Glass;
import transducer.Transducer;

/**
 * @author Dongyoung
 */
public class ConveyorFamily6 implements ConveyorFamily {
	
	private ConveyorAgent11 conveyor11 = new ConveyorAgent11("CONVEYOR 11", 11, 22, 23);
	private ConveyorAgent12 conveyor12 = new ConveyorAgent12("CONVEYOR 12", 12, 24, 25);
	private UVLampAgent uvLamp = new UVLampAgent("UV LAMP");
	
	public ConveyorFamily6(){}
	
	public void setTransducer(Transducer transducer){
		conveyor11.setTransducer(transducer);
		uvLamp.setTransducer(transducer);
		conveyor12.setTransducer(transducer);
		uvLamp.setComps(conveyor11, conveyor12);
	}

	@Override
	public void msgHereIsGlass(Glass glass) {
		conveyor11.msgHereIsGlass(glass);
	}

	@Override
	public void msgIAmFree() {
		conveyor12.msgIAmFree();
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
		conveyor12.setComps(uvLamp, nextFamily);
	}

	@Override
	public void startThreads() {
		// Nothing
	}

	@Override
	public void setPreviousConveyorFamily(ConveyorFamily previousFamily) {
		conveyor11.setComps(previousFamily, uvLamp);
	}
}
