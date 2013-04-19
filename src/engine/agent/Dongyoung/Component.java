package engine.agent.Dongyoung;

import shared.Glass;
import transducer.*;

public class Component extends Agent{

	// DATA
	protected Component previousComp;
	protected Component nextComp;
	protected Transducer transducer;
	protected String name;
	protected Glass tempGlass;
	
	protected boolean nextCompFree = true;   // Next Component's status
	protected boolean newGlass = false;   // New glass on Front Sensor
	protected boolean checkPass = false;   // New glass on Next Sensor
	protected boolean checkDone = true;   // Glass passed to next Component safely?
	protected boolean debug = true;
	
	// Constructor
	protected Component(String name){
		this.name = name;
		super.startThread();
	}
	
	// MESSAGE
	public void msgIAmFree(){
		if( debug ){	print("Received message 'IAmFree'");	}
		nextCompFree = true;
		// If a glass still stays, it needs to be sent.
		if( !checkDone ){
			checkPass = true;
		}
		stateChanged();
	}
	
	public void msgHereIsGlass(Glass glass){
		if( debug ){	print("Received message 'HereIsGlass' : " + glass.getNumber());	}
		tempGlass = glass;
	}
	
	@Override
	// SCHEDULER
	protected boolean pickAndExecuteAnAction() { return false; }

	/* Getter */
	public String getName(){
		return name;
	}
}
