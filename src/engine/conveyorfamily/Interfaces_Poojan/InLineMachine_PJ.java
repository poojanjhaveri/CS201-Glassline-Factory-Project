/**
 * 
 */
package engine.conveyorfamily.Interfaces_Poojan;

import shared.Glass;

/**
 * @author madiphone14
 *
 */
public interface InLineMachine_PJ {


	void msgGlassDoesNotNeedProcessing(Glass glass);

	void msgGlassNeedsProcessing(Glass pcglass, Boolean needsProcessing);


	void msgIamFreeForGlass();

}
