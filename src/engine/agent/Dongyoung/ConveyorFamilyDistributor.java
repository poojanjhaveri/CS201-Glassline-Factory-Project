package engine.agent.Dongyoung;

import engine.interfaces.ConveyorFamily;
import engine.agent.Alex.*;
import shared.Glass;
import transducer.*;

/**
 * @author Dongyoung Jung
 */
public class ConveyorFamilyDistributor implements ConveyorFamily {
	
	// Conveyors
	private Conveyor conveyor10 = new Conveyor("CONVEYOR 10", 10, 20, 21);
	private Conveyor conveyor11 = new Conveyor("CONVEYOR 11", 11, 22, 23);
	private Conveyor conveyor12 = new Conveyor("CONVEYOR 12", 12, 24, 25);
	private Conveyor conveyor13 = new Conveyor("CONVEYOR 13", 13, 26, 27);
	private Conveyor conveyor14 = new Conveyor("CONVEYOR 14", 14, 28, 29);
	
	// Machines
	private InlineMachine painter = new InlineMachine(TChannel.PAINTER);
	private InlineMachine uvLamp = new InlineMachine(TChannel.UV_LAMP);
	private InlineMachine oven = new InlineMachine(TChannel.OVEN);
	
	public ConveyorFamilyDistributor(){}

	public void setter(ConveyorFamily previousFamily, ConveyorFamily nextFamily, Transducer transducer){
		conveyor10.setter(previousFamily, painter, transducer);
		painter.setter(conveyor10, conveyor11, transducer);
		conveyor11.setter(painter, uvLamp, transducer);
		uvLamp.setter(conveyor11, conveyor12, transducer);
		conveyor12.setter(uvLamp, conveyor13, transducer);
		conveyor13.setter(conveyor12, oven, transducer);
		oven.setter(conveyor13, conveyor14, transducer);
		conveyor14.setter(oven, nextFamily, transducer);		
	}
	
	@Override
	public void msgHereIsGlass(Glass glass) {
		conveyor10.msgHereIsGlass(glass);
	}

	@Override
	public void msgIAmFree() {
		conveyor14.msgIAmFree();
	}

	@Override
	public void setPreviousConveyorFamily(ConveyorFamily previousFamily) {}
	public void setNextConveyorFamily(ConveyorFamily nextFamily) {}
	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {}
	public void msgIHaveGlassFinished(Operator operator) {}
	public void startThreads(){}
	public String getName() { return null; }
 
	/* Non-normative scenario : Conveyor Break */
	@Override
	public void setConveyorBroken(boolean s) {

	}
}