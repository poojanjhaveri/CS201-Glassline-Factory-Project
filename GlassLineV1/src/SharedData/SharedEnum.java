package SharedData;

/**
 * @author Dongyoung Jung
 */
public class SharedEnum {
	
	/*
	 * PASS : When a glass comes into Conveyor Family from Previous Family, it is set to PASS automatically. 
	 * 				When the glass arrives on Middle Sensor, the sensor checks if it needs Machine Work. 
	 * 				If the glass needs it, the glass is set to ¡®WORK¡¯.
	 * 				When a glass with status ¡®PASS¡¯ arrives on Popup, it is just sent to Next Family.
	 * WORK : After a glass is set to ¡®WORK,¡¯, Middle Sensor checks which operator is idle. 
	 * 				At this stage, the glass is set to either UP or DOWN. If both operators are in process, it is still in ¡®WORK.¡¯ 
	 * 				As soon as Conveyor Agent receives from operator¡¯s ¡®WorkDone¡¯ message, it repeats setting up the glass.
	 * 				If it does not need Machine Work, its state is remained unchanged, ¡®PASS.¡¯
	 * UP : As soon as the glass arrives on Popup, it is sent to Up-operator.
	 * DOWN : As soon as the glass arrives on Popup, it is sent to Down-operator.
	 * DONE : When Operator is done with work, the glass is set to ¡®DONE.¡¯
	 */
	public enum GlassStatus { PASS, WORK, UP, DOWN, DONE }
	public enum SensorType { ENTRY_SENSOR, MIDDLE_SENSOR, POPUP_SENSOR }
	public enum Sender { PREVIOUS_FAMILY, UP_OPERATOR, DOWN_OPERATOR, NEXT_FAMILY, TRANSDUCER, CONVEYOR }
}
