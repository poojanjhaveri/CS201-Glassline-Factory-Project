package SharedData;

import SharedData.SharedEnum.GlassStatus;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class Machine extends Component implements TReceiver{

	// DATA
	protected Glass glass;
	private TChannel channel;
	private boolean loadFinished = false, actionFinished = false, releaseFinished = false;
	
	/* Constructor */
	protected Machine(String name, TChannel channel) {
		super(name);
		this.channel = channel;
	}
	
	// MESSAGE
	public void msgLoadFinished(){
		glass = tempGlass;
		loadFinished = true;
		stateChanged();
	}
	
	public void msgActionFinished(){
		actionFinished = true;
		stateChanged();
	}
	
	public void msgReleaseFinished(){
		releaseFinished = true;
		stateChanged();
	}
	
	// SCHEDULER
	@Override
	protected boolean pickAndExecuteAnAction(){
		
		if( loadFinished ){
			doAction();
			return true;
		}
		
		if( actionFinished ){
			actionFinished();
			return true;
		}
		
		if( releaseFinished ){
			releaseFinished();
			return true;
		}
		
		return false;
	}
	
	// ACTION
	private void doAction(){
		glass.setStatusByWorkType(channel);
		if( glass.getStatus() == GlassStatus.WORK ){
			transducer.fireEvent(channel, TEvent.WORKSTATION_DO_ACTION, null);
		}
		else if( glass.getStatus() == GlassStatus.PASS ){
			msgActionFinished();
		}
		loadFinished = false;
	}
	
	private void actionFinished(){
		if( nextCompFree ){
			transducer.fireEvent(channel, TEvent.WORKSTATION_RELEASE_GLASS, null);
			passGlassAction();
			actionFinished = false;
		}
	}
	
	private void releaseFinished(){
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
				System.out.println( "channel : " + channel.toString() + ", event : " + event.toString() );
				msgLoadFinished();
			}
			else if( event == TEvent.WORKSTATION_GUI_ACTION_FINISHED ){
				System.out.println( "channel : " + channel.toString() + ", event : " + event.toString() );
				msgActionFinished();
			}
			else if( event == TEvent.WORKSTATION_RELEASE_FINISHED ){
				System.out.println( "channel : " + channel.toString() + ", event : " + event.toString() );
				msgReleaseFinished();
			}
		}
	}
	
	/* Setter */
	public void setTransducer(Transducer transducer){
		this.transducer = transducer;
		transducer.register(this, channel);
	}
}
