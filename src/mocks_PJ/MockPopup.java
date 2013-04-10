/**
 * 
 */
package mocks_PJ;


import shared.Glass;
import engine.conveyorfamily.Interfaces_Poojan.Operator_PJ;
import engine.conveyorfamily.Interfaces_Poojan.Popup_PJ;

/**
 * @author madiphone14
 *
 */
public class MockPopup extends MockAgent implements Popup_PJ {

	public MockPopup(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	
	public EventLog log = new EventLog();
	/* (non-Javadoc)
	 * @see engine.conveyorfamily.Interfaces.Popup#msgINeedToPassGlass()
	 */
	@Override
	public void msgINeedToPassGlass() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent(
				"Received message msgINeedToPassGlass from Conveyor "));		

	}

	@Override
	public void msgGlassDoesNotNeedProcessing(Glass glass) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent(
				"Received message msgGlassDoesNotNeedProcessing from Conveyor "
						 + " for glass number " + glass.getNumber()));
		
		
	}

	@Override
	public void msgGlassNeedsProcessing(Glass pcglass, Operator_PJ op) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent(
				"Received message msgGlassNeedsProcessing from Conveyor "
						 + " for glass number " + pcglass.getNumber()));
		
	}

}
