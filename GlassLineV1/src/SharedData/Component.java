package SharedData;

import transducer.Transducer;

public class Component extends Agent{

	// DATA
	protected Transducer transducer;
	protected String name;
	protected Glass tempGlass;
	
	protected boolean nextCompFree = true;
	protected boolean newGlass = false;
	protected boolean checkPass = false;
	
	protected Component(String name){
		this.name = name;
		super.startThread();
	}
	
	// MESSAGE
	public void msgIAmFree(){
		nextCompFree = true;
		stateChanged();
	}
	
	public void msgHereIsGlass(Glass glass){
		tempGlass = glass;
	}	
	
	@Override
	// SCHEDULER
	protected boolean pickAndExecuteAnAction() {
		System.out.println("TEST1");
		return false;
	}

	// ACTION
	
	
	
	// EXTRA
	/* Getter */
	public String getName(){
		return name;
	}
}
