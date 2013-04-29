package engine.agent.Dongyoung;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import engine.interfaces.ConveyorFamily;
import transducer.*;

public class Conveyor extends Component implements TReceiver{

	// DATA
	private Timer timer = new Timer();
	private ConveyorFamily previousFamily = null;
	private ConveyorFamily nextFamily = null;
	private boolean glassLeaveFront = false, glassLeaveBack = false;
	private boolean entrySensorCheck = false, lastSensorCheck = false;
	private boolean readyToSend = false;
	private Integer[] conveyorNum = new Integer[1];
	private int frontSensorNum, backSensorNum;
	private boolean expectFromPrevious = true;
	
	// Constructor
	public Conveyor(String name, int num, int frontSensorNum, int backSensorNum, CopyOnWriteArrayList<DY_Glass> glasses) {
		super(name, glasses);
		conveyorNum[0] = num;
		this.frontSensorNum = frontSensorNum;
		this.backSensorNum = backSensorNum;
	}
	
	// MESSAGE - Directly from Transducer. Refer to function 'eventFired'
	
	// SCHEDULER
	@Override
	protected boolean pickAndExecuteAnAction(){
		if( broken ){
			return false;
		}
		
		if( entrySensorCheck ){
			newGlassAction();
			return true;
		}
		
		if( glassLeaveFront ){
			glassLeaveFrontAction();
			return true;
		}
	
		if( lastSensorCheck ){
			checkPassAction();
			return true;
		}
		
		if( glassLeaveBack ){
			glassLeaveBackAction();
			return true;
		}
		
		if( readyToSend ){
			sendGlassAction();
			return true;
		}
		
		return false;
	}
	
	// ACTION
	/* New glass on Front Sensor */
	private void newGlassAction(){
		if( !lastSensorCheck && !readyToSend ){
			transducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, conveyorNum );
		}
		entrySensorCheck = false;
	}
	
	/* Glass leaves Front Sensor */
	private void glassLeaveFrontAction(){
		if( previousComp != null ){
			previousComp.msgIAmFree();
		}
		else if( previousFamily != null ){
			previousFamily.msgIAmFree();
			expectFromPrevious = true;
			timerStart();
		}
		glassLeaveFront = false;
	}
	
	private void timerStart(){
		/*
		timer.schedule(new TimerTask(){
			public void run(){
				if( expectFromPrevious ){
					previousFamily.msgIAmFree();
					timerStart();
				}
			}
		}, 10000);
		*/
	}
	
	private void checkPassAction(){
		transducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, conveyorNum );
		lastSensorCheck = false;
		readyToSend = true;
	}

	private void glassLeaveBackAction(){		
		glassLeaveBack = false;
	}
	
	private void sendGlassAction(){
		if( nextCompFree ){
			if( nextFamily != null ){
				nextFamily.msgHereIsGlass( glasses.remove(0).getGlass() );
			}		
			transducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, conveyorNum );
			readyToSend = false;
			nextCompFree = false;
		}
	}

	// EXTRA
	public void setConveyorBroken(boolean conveyorStatus){
		broken = conveyorStatus;
		stateChanged();
	}
	
	/* From Transducer */
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if( (Integer)args[0] == frontSensorNum ){
			if( event == TEvent.SENSOR_GUI_PRESSED ){
				entrySensorCheck = true;
				expectFromPrevious = false;
			}
			else if( event == TEvent.SENSOR_GUI_RELEASED ){
				glassLeaveFront = true;
			}
			stateChanged();
		}
		else if( (Integer)args[0] == backSensorNum ){
			if( event == TEvent.SENSOR_GUI_PRESSED ){
				lastSensorCheck = true;
			}
			else if( event == TEvent.SENSOR_GUI_RELEASED ){
				glassLeaveBack = true;
			}
			stateChanged();
		}
	}

	public void setter(Object previous, Object next, Transducer transducer){
		this.transducer = transducer;
		transducer.register(this, TChannel.SENSOR);
		
		if( previous instanceof Component ){
			previousComp = (Component)previous;
		}
		else if( previous instanceof ConveyorFamily ){
			previousFamily = (ConveyorFamily)previous;
		}

		if( next instanceof Component ){
			nextComp = (Component)next;
		}
		else if( next instanceof ConveyorFamily ){
			nextFamily = (ConveyorFamily)next;
		}
	}
}
