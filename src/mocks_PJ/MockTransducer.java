package mocks_PJ;


import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import engine.agent.Agent;
import engine.conveyorfamily.Interfaces_Poojan.TransducerInterface_PJ;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ;

public class MockTransducer implements TransducerInterface_PJ {

	public EventLog log = new EventLog();
	
	public void fireEvent(TChannel allAgents, TEvent sensorGuiPressed,
			Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void register(TReceiver toRegister, TChannel channel) {
		// TODO Auto-generated method stub
		
	}


	


}
