package engine.agent.Dongyoung;

import shared.Glass;
import transducer.*;

public class Component extends Agent{

	// DATA
	protected Transducer transducer;
	protected String name;
	protected Glass tempGlass;
	protected boolean debug = true;
	
	protected boolean nextCompFree = true;   // Next Component's status
	protected boolean newGlass = false;   // New glass on Front Sensor
	protected boolean checkPass = false;   // New glass on Next Sensor
	protected boolean checkDone = true;   // Glass passed to next Component safely?
	
	// Constructor
	protected Component(String name){
		this.name = name;
		super.startThread();
	}
	
	// MESSAGE
	public void msgIAmFree(){
		nextCompFree = true;
		// If a glass still stays, it needs to be sent.
		if( !checkDone ){
			checkPass = true;
		}
		stateChanged();
	}
	
	public void msgHereIsGlass(Glass glass){
		tempGlass = glass;
	}	
	
	@Override
	// SCHEDULER
	protected boolean pickAndExecuteAnAction() {
		return false;
	}

	// ACTION - No Common Action between all components

	/* Getter */
	public String getName(){
		return name;
	}
}
