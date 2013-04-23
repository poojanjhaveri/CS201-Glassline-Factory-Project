package mocks_PJ;


import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import engine.conveyorfamily.Interfaces_Poojan.Conveyor_PJ;
import engine.conveyorfamily.Interfaces_Poojan.Operator_PJ;

public class MockConveyor extends MockAgent implements Conveyor_PJ{
	
	
	public EventLog log = new EventLog();

	public MockConveyor(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIamFree() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent(
				"Received message msgIamFree() from Popup"));
		
	}

	@Override
	public void msgOperatorIsfree(Operator_PJ operatorAgent) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent(
				"Received message msgOperatorIsfree from Popup"+" for operator "));
		
		
	}

	public void setOperator(Operator_PJ o1) {
		// TODO Auto-generated method stub
		
	}

	public void msgHereIsGlass(Glass glass) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent(
				"Received message msgHereIsGlass from ConveyorFamily"));
	}

	@Override
	public void setisINLINEBusy(Boolean s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Boolean getsecondconveyorfree() {
		// TODO Auto-generated method stub
		return null;
	}



}
