package engine.agent.Dongyoung;

import shared.Glass;
import transducer.*;

public class Machine extends Component implements TReceiver{

	// DATA
	protected Glass glass;
	private TChannel channel;
	private boolean loadFinished = false, actionFinished = false, releaseFinished = false;
	
	// Constructor
	protected Machine(String name, TChannel channel) {
		super(name);
		this.channel = channel;
	}
	
	// MESSAGE - Directly from Transducer. Refer to function 'eventFired'
	
	// SCHEDULER
	@Override
	protected boolean pickAndExecuteAnAction(){
		
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
			actionFinishedAction();
		}
	}
	
	private void actionFinishedAction(){
		if( nextCompFree ){
			transducer.fireEvent(channel, TEvent.WORKSTATION_RELEASE_GLASS, null);
			passGlassAction();
			actionFinished = false;
		}
	}
	
	private void releaseFinishedAction(){
		glass = null;
		notifyIAmFreeAction();
		releaseFinished = false;
	}
	
	protected void notifyIAmFreeAction(){}
	protected void passGlassAction(){}
	
	// EXTRA
	/* From Transducer */
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if( channel == this.channel ){
			if( event == TEvent.WORKSTATION_LOAD_FINISHED ){
				if( debug ){
					System.out.println( "channel : " + channel.toString() + ", event : " + event.toString() );
				}
				glass = tempGlass;
				loadFinished = true;
			}
			else if( event == TEvent.WORKSTATION_GUI_ACTION_FINISHED ){
				if( debug ){
					System.out.println( "channel : " + channel.toString() + ", event : " + event.toString() );
				}
				actionFinished = true;
			}
			else if( event == TEvent.WORKSTATION_RELEASE_FINISHED ){
				if( debug ){
					System.out.println( "channel : " + channel.toString() + ", event : " + event.toString() );
				}
				releaseFinished = true;
			}
			stateChanged();
		}
	}
	
	/* Setter */
	public void setTransducer(Transducer transducer){
		this.transducer = transducer;
		transducer.register(this, channel);
	}
}