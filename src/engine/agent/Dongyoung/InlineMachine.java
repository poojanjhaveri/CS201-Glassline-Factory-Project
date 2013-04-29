package engine.agent.Dongyoung;

<<<<<<< HEAD
import java.util.concurrent.CopyOnWriteArrayList;

=======
>>>>>>> Revert "Dongyoung's Conveyor/Inline Recipe Bug Fixed"
import transducer.*;

public class InlineMachine extends Component implements TReceiver{

	// DATA
	private TChannel channel;
	private boolean loadFinished = false, actionFinished = false, releaseFinished = false;
	
	// Constructor
	public InlineMachine(TChannel channel, CopyOnWriteArrayList<DY_Glass> glasses) {
		super(channel.toString(), glasses);
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
<<<<<<< HEAD
		for(int i=0 ; i<glasses.size() ; i++){
			if( !glasses.get(i).getPass(channel) ){
				if( glasses.get(i).getNeedWork(channel) ){
					transducer.fireEvent(channel, TEvent.WORKSTATION_DO_ACTION, null);
				}
				else{
					actionFinished = true;
				}
				
				// Check Path
				for(DY_Glass g:glasses)
					if(!g.getPass(channel)){
						g.setPass(channel);
						break;
					}
				
				break;
			}
		}		
=======
		if( glasses.get(0).getRecipe( channel ) ){
			transducer.fireEvent(channel, TEvent.WORKSTATION_DO_ACTION, null);
		}
		else{
			actionFinished = true;
		}
>>>>>>> Revert "Dongyoung's Conveyor/Inline Recipe Bug Fixed"
	}

	private void actionFinishedAction(){
		if( nextCompFree ){
			transducer.fireEvent(channel, TEvent.WORKSTATION_RELEASE_GLASS, null);
			actionFinished = false;
			nextCompFree = false;
		}
	}
	
	private void releaseFinishedAction(){
		previousComp.msgIAmFree();
<<<<<<< HEAD
		releaseFinished = false;
=======
	}
	
	/* Glass Pass */
	private void passGlassAction(){
		nextComp.msgHereIsGlass( glasses.remove(0) );
>>>>>>> Revert "Dongyoung's Conveyor/Inline Recipe Bug Fixed"
	}

	// EXTRA
	/* From Transducer */
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if( event == TEvent.WORKSTATION_LOAD_FINISHED ){
			loadFinished = true;
		}
		else if( event == TEvent.WORKSTATION_GUI_ACTION_FINISHED ){
			actionFinished = true;
		}
		else if( event == TEvent.WORKSTATION_RELEASE_FINISHED ){
			releaseFinished = true;
		}
		else if( event == TEvent.WORKSTATION_BROKEN ){
			broken = true;
		}
		else if( event == TEvent.WORKSTATION_FIXED ){
			broken = false;
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