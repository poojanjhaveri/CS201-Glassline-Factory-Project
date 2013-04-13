package engine.agent.Dongyoung;

import shared.Glass;
import transducer.*;

public class Component extends Agent{

	// DATA
	protected Transducer transducer;
	protected String name;
	protected Glass tempGlass;
	protected boolean debug = true;
	
	protected boolean nextCompFree = true;
	protected boolean newGlass = false;
	protected boolean checkPass = false;
	protected boolean checkDone = true;
	
	protected Component(String name){
		this.name = name;
		super.startThread();
	}
	
	// MESSAGE
	public void msgIAmFree(){
		nextCompFree = true;
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

	// ACTION

	// EXTRA
	/* Getter */
	public String getName(){
		return name;
	}
}
