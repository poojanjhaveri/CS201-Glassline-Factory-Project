package engine.conveyorfamily.Interfaces_Poojan;

import engine.conveyorfamily_Poojan.ConveyorAgent_PJ;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;

public interface TransducerInterface_PJ {

	void fireEvent(TChannel allAgents, TEvent sensorGuiPressed, Object[] args);

	void register(TReceiver toRegister, TChannel channel);
}
