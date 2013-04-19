package engine.agent.Dongyoung;

import java.util.concurrent.CopyOnWriteArrayList;
import engine.interfaces.ConveyorFamily;
import shared.Glass;
import transducer.*;

public class Conveyor extends Component implements TReceiver{

	// DATA
	protected ConveyorFamily previousFamily = null;
	protected ConveyorFamily nextFamily = null;
	protected CopyOnWriteArrayList<Glass> glasses = new CopyOnWriteArrayList<Glass>();
	protected boolean glassLeaveFront = false;
	private Integer[] conveyorNum = new Integer[1];
	private int frontSensorNum, backSensorNum, sensorNum;
	
	// Constructor
	public Conveyor(String name, int num, int frontSensorNum, int backSensorNum) {
		super(name);
		conveyorNum[0] = num;
		this.frontSensorNum = frontSensorNum;
		this.backSensorNum = backSensorNum;
	}
	
	// MESSAGE - Directly from Transducer. Refer to function 'eventFired'
	
	// SCHEDULER
	@Override
	protected boolean pickAndExecuteAnAction(){
		// New Glass on Front Sensor
		if( newGlass ){
			newGlassAction();
			return true;
		}
		
		// Glass leaves Front Sensor
		if( glassLeaveFront ){
			glassLeaveFrontAction();
			return true;
		}
		
		// New Glass on Back Sensor
		if( checkPass ){
			checkPassAction();
			return true;
		}
		
		return false;
	}
	
	// ACTION
	/*
	 * Check if next component is ready to accept glasses.
	 * If not, the conveyor keeps the glass waiting.
	 */
	private void checkPassAction(){
		transducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, conveyorNum );
		if( nextCompFree ){
			nextCompFree = false;
			passGlassAction();
			transducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, conveyorNum );
			checkDone = true;
		}
		else{
			checkDone = false;
		}
		checkPass = false;
	}
	
	/* New glass on Front Sensor */
	private void newGlassAction(){
		transducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, conveyorNum );
		glasses.add( tempGlass );
		tempGlass = null;
		newGlass = false;
		conveyorCheck();
	}
	
	/* Glass leaves Front Sensor */
	private void glassLeaveFrontAction(){
		glassLeaveFront = false;
		notifyIAmFreeAction();
	}

	/* Notification */
	private void notifyIAmFreeAction(){
		if( previousFamily == null ){
			previousComp.msgIAmFree();
		}
		else{
			previousFamily.msgIAmFree();
		}
	}
	
	/* Glass Pass */
	private void passGlassAction(){
		if( nextFamily == null ){
			nextComp.msgHereIsGlass( glasses.remove(0) );
		}
		else{
			nextFamily.msgHereIsGlass( glasses.remove(0) );
		}
	}
	
	// EXTRA
	/* From Transducer */
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		sensorNum = (Integer)args[0];
		if( event == TEvent.SENSOR_GUI_PRESSED ){
			if( sensorNum == frontSensorNum ){
				newGlass = true;
			}
			else if( sensorNum == backSensorNum ){
				checkPass = true;
			}
			stateChanged();
		}
		else if( event == TEvent.SENSOR_GUI_RELEASED ){
			if( sensorNum == frontSensorNum ){
				glassLeaveFront = true;
				stateChanged();
			}
		}	
	}
	
	/* Everytime the conveyor status is changed, it should check the conveyor should run or stops. */
	private void conveyorCheck(){
		// Glass on Front Sensor or on Conveyor, but no Glass on Back Sensor
		if( ( newGlass || !glasses.isEmpty() ) && !checkPass ){
			transducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, conveyorNum );
		}
		else{
			transducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, conveyorNum );
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
