/**
 * 
 */
package mocks_PJ;

import shared.Glass;
import engine.conveyorfamily.Interfaces_Poojan.Operator_PJ;

/**
 * @author madiphone14
 *
 */
public class MockOperator extends MockAgent implements Operator_PJ {

	
	public EventLog log = new EventLog();
	
	public MockOperator(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	

	/* (non-Javadoc)
	 * @see engine.conveyorfamily.Interfaces.Operator#msgHereIsGlass(shared.Glass)
	 */
	@Override
	public void msgHereIsGlass(Glass pcglass) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent(
				"Received message msgHereIsGlass from Popup "+pcglass.getNumber()));		
		
	}

	/* (non-Javadoc)
	 * @see engine.conveyorfamily.Interfaces.Operator#msgIAmFree()
	 */
	@Override
	public void msgIAmFree() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent(
				"Received message msgIAmFree from Popup "));		

	}

}
