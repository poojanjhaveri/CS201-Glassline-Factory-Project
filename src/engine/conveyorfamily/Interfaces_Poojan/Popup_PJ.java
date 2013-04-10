/**
 * 
 */
package engine.conveyorfamily.Interfaces_Poojan;

import shared.Glass;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ.MyCGlass;

/**
 * @author madiphone14
 *
 */
public interface Popup_PJ {

	void msgINeedToPassGlass();

	void msgGlassDoesNotNeedProcessing(Glass glass);

	void msgGlassNeedsProcessing(Glass pcglass, Operator_PJ op);

}
