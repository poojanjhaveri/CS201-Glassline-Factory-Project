package engine.agent.Dongyoung;

import shared.Glass;
import transducer.*;

public class InlineMachine extends Component implements TReceiver{

	// DATA
	private Glass glass;
	private TChannel channel;
	private boolean loadFinished = false, actionFinished = false, releaseFinished = false;
	
	// Constructor
	public InlineMachine(TChannel channel) {
		super(channel.toString());
		this.channel = channel;
	}
	
	// MESSAGE - Directly from Transducer. Refer to function 'eventFired'
	
	// SCHEDULER
	@Override
	protected boolean pickAndExecuteAnAction(){
		if( broken ){
			return false;
		}
		
		if( loadFinished ){
			doWorkAction();
			return true;
		}
		
		if( actionFinished ){
			actionFinishedAction();
			return true;
		}
		
		if( releaseFinished ){
			releaseFinishedAction();
			return true;
		}
		
		return false;
	}
	
	// ACTION
	/*
	 * Need work - machine should work
	 * No need - machine should not work
	 */
	private void doWorkAction(){
		loadFinished = false;
		if( glass.getRecipe( channel ) ){
			transducer.fireEvent(channel, TEvent.WORKSTATION_DO_ACTION, null);
		}
		else if( !glass.getRecipe( channel ) ){
			actionFinished = true;
		}
	}
	
	private void actionFinishedAction(){
		if( nextCompFree ){
			transducer.fireEvent(channel, TEvent.WORKSTATION_RELEASE_GLASS, null);
			passGlassAction();
			actionFinished = false;
			nextCompFree = false;
		}
	}
	
	private void releaseFinishedAction(){
		glass = null;
		notifyIAmFreeAction();
		releaseFinished = false;
	}
	
	/* Notification */
	private void notifyIAmFreeAction(){
		previousComp.msgIAmFree();
	}
	
	/* Glass Pass */
	private void passGlassAction(){
		nextComp.msgHereIsGlass( glass );
	}
	
	// NON-NORM.
	public void nonNormBreak(){
		broken = true;
	}
	
	public void nonNormFix(){
		broken = false;
		stateChanged();
	}
	
	// EXTRA
	/* From Transducer */
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if( event == TEvent.WORKSTATION_LOAD_FINISHED ){
			glass = tempGlasses.remove(0);
			loadFinished = true;
		}
		else if( event == TEvent.WORKSTATION_GUI_ACTION_FINISHED ){
			actionFinished = true;
		}
		else if( event == TEvent.WORKSTATION_RELEASE_FINISHED ){
			releaseFinished = true;
		}
		stateChanged();
	}
	
	public void setter(Conveyor previousConveyor, Conveyor nextConveyor, Transducer transducer){
		previousComp = previousConveyor;
		nextComp = nextConveyor;
		this.transducer = transducer;
		transducer.register(this, channel);
	}
}