package engine.conveyorfamily.Interfaces_Poojan;

import mocks_PJ.MockOperator;
import transducer.TChannel;
import transducer.TEvent;

public interface Conveyor_PJ {

	public void eventFired(TChannel channel, TEvent event, Object[] args);

	public void msgIamFree();

	public void msgOperatorIsfree(Operator_PJ operatorAgent);
	
	public void setOperator(Operator_PJ o1);
	
	
}
