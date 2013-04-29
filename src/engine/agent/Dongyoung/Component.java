package engine.agent.Dongyoung;

import java.util.concurrent.CopyOnWriteArrayList;

import transducer.*;

public class Component extends Agent{

	// DATA
	protected Component previousComp;
	protected Component nextComp;
	protected Transducer transducer;
	protected String name;
	protected CopyOnWriteArrayList<DY_Glass> glasses;
	protected boolean nextCompFree = true;   // Next Component's status
	protected boolean debug = false, broken = false;
	
	// Constructor
	protected Component(String name, CopyOnWriteArrayList<DY_Glass> glasses){
		this.name = name;
		this.glasses = glasses;
		super.startThread();
	}
	
	// MESSAGE
	public void msgIAmFree(){
		if( debug ){	print("Received message 'IAmFree'");	}
		nextCompFree = true;
		stateChanged();
	}
	
	@Override
	// SCHEDULER
	protected boolean pickAndExecuteAnAction() { return false; }

	/* Getter */
	public String getName(){
		return name;
	}
}