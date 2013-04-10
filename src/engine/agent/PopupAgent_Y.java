package engine.agent;

import shared.*;
import shared.enums.SharedData.WorkType;
import transducer.*;
import engine.interfaces.*;
import java.util.*;
import java.util.concurrent.*;

public class PopupAgent_Y extends Agent implements Popup_Y {

	//data
	int popupIndex;
	Transducer transducer;
	WorkType type;
	//Operators
	MyOperator[] operator = new MyOperator[2];
	private class MyOperator {
		Operator o;
		boolean isFree;
		TChannel channel;
		int index;
		Semaphore sem = new Semaphore(0, true);
		
		public MyOperator(Operator op, boolean isf, TChannel c, int i) {
			o = op;
			isFree = isf;
			channel = c;
			index = i;
		}
	}
	//Next Conveyor Family
	ConveyorFamily next;
	boolean nextFree;
	//Conveyor Agent
	Conveyor_Y conveyor;
	//Glass
	MyGlass glassOnPopup;
	List<MyGlass> glasses = Collections.synchronizedList(new ArrayList<MyGlass> () );
	private class MyGlass {
		Glass glass;
		GlassState state;
		Operator operator;
		int operatorIndex;
		
		private MyGlass () {}
		public MyGlass(Glass g, GlassState s) {
			glass = g;
			state = s;
		}
		
	}
	enum GlassState {ON_ENTRY, PENDING, NEED_PROCESSING, DONE, PASS, NOTHING};
	//State Variables
	PopupState state;				//Popup Sensor
	PopupLevel level;
	enum PopupState {LOADED, EMPTY};
	enum PopupLevel {UP, DOWN};
	//Semaphores
	Semaphore loadSemaphore = new Semaphore(0, true);
	Semaphore elevSemaphore = new Semaphore(0, true);
	
	//Constructors
	
	private PopupAgent_Y () {}
	
	public PopupAgent_Y(String n, int index){
		name = n;
		popupIndex = index;
		//State Initialization
		nextFree = true;
		state = PopupState.EMPTY;
		level = PopupLevel.DOWN;
	}
	
	//Messages and Eventfires
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		if( (channel == TChannel.POPUP) && ((Integer)(args[0]) == popupIndex )) {
			if(event == TEvent.POPUP_GUI_MOVED_DOWN)
				elevSemaphore.release();
			if(event == TEvent.POPUP_GUI_MOVED_UP)
				elevSemaphore.release();
			if(event == TEvent.POPUP_GUI_LOAD_FINISHED)
				loadSemaphore.release();
			if(event == TEvent.POPUP_GUI_RELEASE_FINISHED)
				loadSemaphore.release();
		} else if ( (channel == operator[0].channel) && ( (Integer)(args[0]) == operator[0].index )) {
			if(event == TEvent.WORKSTATION_LOAD_FINISHED)
				operator[0].sem.release();
			if(event == TEvent.WORKSTATION_RELEASE_FINISHED)
				operator[0].sem.release();
		} else if ( (channel == operator[1].channel) && ( (Integer)(args[0]) == operator[1].index )) {
			if(event == TEvent.WORKSTATION_LOAD_FINISHED)
				operator[1].sem.release();
			if(event == TEvent.WORKSTATION_RELEASE_FINISHED)
				operator[1].sem.release();
		}
	}
	
	public void msgHereIsGlass(Glass glass) {
		synchronized(glasses) {
			for(MyGlass g : glasses ){
				if(g.glass == glass) {
					g.state = GlassState.PENDING;
					glassOnPopup = g;
					stateChanged();
					return;
				}
			}
			MyGlass g = new MyGlass(glass, GlassState.PENDING);
			glasses.add(g);
			glassOnPopup = g;
		}
		stateChanged();
	}

	public void msgIHaveGlass(Glass glass) {
		synchronized(glasses) {
			for(MyGlass g : glasses ){
				if(g.glass == glass) {
					g.state = GlassState.ON_ENTRY;
					stateChanged();
					return;
				}
			}
			glasses.add(new MyGlass(glass, GlassState.ON_ENTRY));
		}
		stateChanged();
	}
	
	public void msgIHaveGlassFinished(Operator operator) {
		synchronized(glasses) {
			for(MyGlass g : glasses ){
				if(g.operator == operator) {
					g.state = GlassState.DONE;
				}
			}
		}
		stateChanged();
	}

	public void msgHereIsFinishedGlass(Glass glass) {
		synchronized(glasses) {
			for(MyGlass g : glasses ){
				if(g.glass == glass) {
					g.state = GlassState.PASS;
					glassOnPopup = g;
				}
			}
		}
		stateChanged();
	}

	public void msgIAmFree() {
		nextFree = true;
		stateChanged();
	}
	
	//Scheduler
	@Override
	public boolean pickAndExecuteAnAction() {
		MyGlass tempGlass = null;
		
		//RULE 1: Accept finished glass when operator has it done
		if(state == PopupState.EMPTY) {
			synchronized(glasses) {
				for(MyGlass g : glasses) {
					if(g.state == GlassState.DONE) {
						tempGlass = g;
					}
				}
			}
			if(tempGlass != null) {
				acceptFinishedGlass(tempGlass, tempGlass.operatorIndex);
				return true;
			}
		}
		
		//RULE 2: Once a glass comes in, decide if it needs processing
		synchronized(glasses) {
			for(MyGlass g : glasses) {
				if(g.state == GlassState.PENDING) {
					tempGlass = g;
				}
			}
		}
		if(tempGlass != null) {
			identifyGlass(tempGlass);
			return true;
		}
		
		//RULE 3: If a glass either has been processed or doesn't need processing, pass it to the next conveyor
		if(nextFree) {
			synchronized(glasses) {
				for(MyGlass g : glasses) {
					if(g.state == GlassState.PASS) {
						tempGlass = g;
					}
				}
			}
			if(tempGlass != null) {
				pushGlass(tempGlass);
				return true;
			}
		}
		
		//RULE 4: If at least one of operators is free and a glass needs processing, give the glass to an operator
		if(operatorFree(0) || operatorFree(1)) {
			synchronized(glasses) {
				for(MyGlass g : glasses) {
					if(g.state == GlassState.NEED_PROCESSING) {
						tempGlass = g;
					}
				}
			}
			if(tempGlass != null) {
				if(operatorFree(0) )
					giveOperatorGlass(tempGlass, 0);
				else
					giveOperatorGlass(tempGlass, 1);
				return true;
			}
		}
		
		//RULE 5: If either it's the case that one of operators is free, or the incoming glass doesn't need any processing, accept the glass.
		if(state == PopupState.EMPTY) {
			synchronized(glasses) {
				for(MyGlass g : glasses) {
					if(g.state == GlassState.ON_ENTRY) {
						tempGlass = g;
					}
				}
			}
			if(tempGlass != null) {
				if ( (! tempGlass.glass.getRecipe(type)) || (operatorFree(0) || operatorFree(1)) ) {
					acceptGlass(tempGlass);
					return true;
				}
			}
		}
		
		return false;
	}
	
	//Actions
	private void acceptGlass(MyGlass tempGlass) {
		Do("Accepting glass from the conveyor");
		if(level == PopupLevel.UP)
			lowerPopup();
		conveyor.msgPopupFree();
		//block the thread until accepting finished
		try {
			loadSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		state = PopupState.LOADED;
		tempGlass.state = GlassState.PENDING;
		conveyor.msgPopupBusy();
	}

	private void giveOperatorGlass(MyGlass tempGlass, int operatorIndex) {
		Do("Giving operator glass to process");
		if(level == PopupLevel.DOWN)
			raisePopup();
		operator[operatorIndex].o.msgHereIsGlass(tempGlass.glass);
		tempGlass.operator = operator[operatorIndex].o;
		//OperatorAgent will load the glass
		try {
			operator[operatorIndex].sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tempGlass.operatorIndex = operatorIndex;
		operator[operatorIndex].isFree = false;
		tempGlass.state = GlassState.NOTHING;
		state = PopupState.EMPTY;
		glassOnPopup = null;
	}

	private void pushGlass(MyGlass tempGlass) {
		Do("Pushing glass onto next conveyor");
		if(level == PopupLevel.UP)
			lowerPopup();
		next.msgHereIsGlass(tempGlass.glass);
		
		Integer[] idx = new Integer[1];
		idx[0] = popupIndex;
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_RELEASE_GLASS, idx);
		try {
			loadSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		state = PopupState.EMPTY;
		conveyor.msgPopupFree();
		glassOnPopup = null;
		glasses.remove(tempGlass);
		nextFree = false;
	}

	private void identifyGlass(MyGlass tempGlass) {
		Do("Deciding if glass needs processing");
		if(tempGlass.glass.getRecipe(type))
			tempGlass.state = GlassState.NEED_PROCESSING;
		else
			tempGlass.state = GlassState.PASS;
	}
	
	private void lowerPopup() {
		Do("Lowering Popup");
		Integer[] idx = new Integer[1];
		idx[0] = popupIndex;
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN, idx);
		
		try {
			elevSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(state == PopupState.EMPTY)
			conveyor.msgPopupFree();
		level = PopupLevel.DOWN;
	}
	
	private void raisePopup() {
		Do("Raising Popup");
		Integer[] idx = new Integer[1];
		idx[0] = popupIndex;
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP, idx);
		
		try {
			elevSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.msgPopupBusy();
		level = PopupLevel.UP;
	}

	private void acceptFinishedGlass(MyGlass tempGlass, int operatorIndex) {
		Do("Accepting glass from finished operator");
		if(level == PopupLevel.DOWN)
			raisePopup();
		operator[operatorIndex].o.msgIAmFree();
		//OperatorAgent will unload the glass
		try {
			operator[operatorIndex].sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		operator[operatorIndex].isFree = true;
		tempGlass.state = GlassState.PASS;
		state = PopupState.LOADED;
	}

	//Getters, Setters and Hacks
	public String getName() {
		return name;
	}
	
	public void setTransducer(Transducer t) {
		transducer = t;
		transducer.register(this, TChannel.POPUP);
	}
	
	public void setOperator(int index, Operator o, TChannel c, int operatorIndex) {
		boolean alreadyRegistered = false;
		if(operator[0] != null) {
			if(operator[0].channel == c)
				alreadyRegistered = true;
		} else if (operator[1] != null) {
			if(operator[1].channel == c)
				alreadyRegistered = true;
		}
		operator[index] = new MyOperator(o, true, c, operatorIndex);
		if(! alreadyRegistered)
			transducer.register(this, c);
	}

	
	public void setNextConveyorFamily(ConveyorFamily cf) {
		next = cf;
	}
	
	public void setConveyor(Conveyor_Y c) {
		conveyor = c;
	}
	
	public void setWorkType(WorkType wt) {
		type = wt;
	}
	
	public boolean operatorFree(int index) {
		return operator[index].isFree;
	}
	
	//Test
	public int getGlassListSize() {
		return glasses.size();
	}
	
	public void setNextFree(boolean nf) {
		nextFree = nf;
	}

}
