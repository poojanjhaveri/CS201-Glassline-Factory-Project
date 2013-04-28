package engine.agent.Dongyoung;

import java.util.concurrent.CopyOnWriteArrayList;

import shared.Glass;
import transducer.*;

public class Component extends Agent{

	// DATA
	protected Component previousComp;
	protected Component nextComp;
	protected Transducer transducer;
	protected String name;
	protected CopyOnWriteArrayList<Glass> glasses = new CopyOnWriteArrayList<Glass>();
	protected boolean nextCompFree = true;   // Next Component's status
	protected boolean debug = false, broken = false;
	
	// Constructor
	protected Component(String name){
		this.name = name;
		super.startThread();
	}
	
	// MESSAGE
	public void msgIAmFree(){
		if( debug ){	print("Received message 'IAmFree'");	}
		nextCompFree = true;
		stateChanged();
	}
	
	public void msgHereIsGlass(Glass glass){
		if( debug ){	print("Received message 'HereIsGlass' : " + glass.getNumber());	}
		glasses.add(glass);
	}
	
	@Override
	// SCHEDULER
	protected boolean pickAndExecuteAnAction() { return false; }

	/* Getter */
	public String getName(){
		return name;
	}
}
