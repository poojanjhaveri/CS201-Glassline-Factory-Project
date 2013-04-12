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
	
	public void setComps(ConveyorFamily previousFamily, ConveyorFamily nextFamily, Transducer transducer){
		conveyor11.setComps(previousFamily, uvLamp);
		conveyor11.setTransducer(transducer);
		conveyor12.setComps(uvLamp, nextFamily);
		conveyor12.setTransducer(transducer);
		uvLamp.setComps(conveyor11, conveyor12);
		uvLamp.setTransducer(transducer);
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

	@Override
	public void setPreviousConveyorFamily(ConveyorFamily c2) {
		// TODO Auto-generated method stub
		
	}
}
