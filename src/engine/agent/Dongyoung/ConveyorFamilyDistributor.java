package engine.agent.Dongyoung;

import java.util.HashMap;

import engine.interfaces.ConveyorFamily;
import engine.agent.Alex.*;
import shared.Glass;
import transducer.*;

/**
 * @author Dongyoung Jung
 */
public class ConveyorFamilyDistributor implements ConveyorFamily {

	private Conveyor[] conveyors = new Conveyor[5];
	private HashMap<TChannel, InlineMachine> inlineMachines = new HashMap<TChannel, InlineMachine>();
	
	public ConveyorFamilyDistributor(){
		// Conveyor Generation
		for(int i=0 ; i<5 ; i++){
			conveyors[i] = new Conveyor("CONVEYOR"+(i+10), i+10, 2*(i+10), 2*(i+10)+1);
		}
		
		// Inline Machine Generation
		inlineMachines.put( TChannel.PAINTER, new InlineMachine(TChannel.PAINTER) );
		inlineMachines.put( TChannel.UV_LAMP, new InlineMachine(TChannel.UV_LAMP) );
		inlineMachines.put( TChannel.OVEN, new InlineMachine(TChannel.OVEN) );
	}

	public void setter(ConveyorFamily previousFamily, ConveyorFamily nextFamily, Transducer transducer){
		conveyors[0].setter(previousFamily, inlineMachines.get(TChannel.PAINTER), transducer);
		inlineMachines.get(TChannel.PAINTER).setter(conveyors[0], conveyors[1], transducer);
		conveyors[1].setter(inlineMachines.get(TChannel.PAINTER), inlineMachines.get(TChannel.UV_LAMP), transducer);
		inlineMachines.get(TChannel.UV_LAMP).setter(conveyors[1], conveyors[2], transducer);
		conveyors[2].setter(inlineMachines.get(TChannel.UV_LAMP), conveyors[3], transducer);
		conveyors[3].setter(conveyors[2], inlineMachines.get(TChannel.OVEN), transducer);
		inlineMachines.get(TChannel.OVEN).setter(conveyors[3], conveyors[4], transducer);
		conveyors[4].setter(inlineMachines.get(TChannel.OVEN), nextFamily, transducer);
	}
	
	@Override
	public void msgHereIsGlass(Glass glass) {
		conveyors[0].msgHereIsGlass(glass);
	}

	@Override
	public void msgIAmFree() {
		conveyors[4].msgIAmFree();
	}

	@Override
	public void setPreviousConveyorFamily(ConveyorFamily previousFamily) {}
	public void setNextConveyorFamily(ConveyorFamily nextFamily) {}
	public void msgHereIsFinishedGlass(Operator operator, Glass glass) {}
	public void msgIHaveGlassFinished(Operator operator) {}
	public void startThreads(){}
	public String getName() { return null; }
	public void setConveyorBroken(boolean s) {}
	
	/*  
	 * Non-normative scenario : Conveyor Break(10~14)
	 * Thread stops as soon as the conveyor breaks.
	 * Also the conveyor stops moving if it was moving.
	 */
	public void setConveyorBroken(boolean s, int num){
		if(s){
			conveyors[num-10].nonNormBreak();
		}
		else if(!s){
			conveyors[num-10].nonNormFix();
		}
	}
	
	/*  
	 * Non-normative scenario : Inline Machine Break(10~14)
	 * Thread stops as soon as the inline machine breaks.
	 */
	public void setInlineBroken(boolean s, TChannel channel){
		if(s){
			inlineMachines.get(channel).nonNormBreak();
		}
		else if(!s){
			inlineMachines.get(channel).nonNormFix();
		}
	}
}