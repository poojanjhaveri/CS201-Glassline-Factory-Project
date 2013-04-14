package engine.agent.Alex;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import engine.agent.Agent;


import transducer.TChannel;
import transducer.TEvent;

import engine.interfaces.*;

public class PopupAgent extends Agent{
	
	//public EventLog log = new EventLog();
	
	public AlexsConveyorFamily parentCF;
	public ConveyorFamily nextCF;
	
	public MyOperator upOperator, downOperator;
	public enum NextCFState {busy, free};
	public NextCFState nextCFState;
	
	public enum UpDownState {Up, Down};
	public UpDownState upDownState;
	//enum CurrentState {emptyNoCurrentProcessing, fullNoCurrentProcessing, emptyCurrentProcessing, fullCurrentProcessing};
	public enum CurrentState {empty,full};
	
	public CurrentState popupState;
	public enum PopupEvent {loadFinished, popupMovedUp, newGlassFromConveyor, releaseFinished, popupMovedDown, finishedGlassFromOperator, nextCFIsFree};
	
	public ArrayList<PopupEvent> popupEvents;

	public boolean requestToPushGlass  = false;

	public Semaphore waitingForFinishedGlass;

	public ConveyorAgent conveyor;

	public ArrayList<AlexsConveyorFamily.MyGlass> glassOnCF;
	

	public class MyOperator {
		public MyOperator(Operator o) {
			// TODO Auto-generated constructor stub
			operator = o;
		}
		Operator operator;
		public boolean occupied = false;
		public boolean requestOpen = false;
		public Operator getOperator(){return operator;}
	}
	
	public PopupAgent(String name, AlexsConveyorFamily cf, Operator up, Operator down){
		super(name);
		
		popupEvents = new ArrayList<PopupEvent>();
		
		upOperator = new MyOperator(up);
		downOperator = new MyOperator(down);
		parentCF = cf;
		waitingForFinishedGlass = new Semaphore(0);
		nextCFState = NextCFState.busy;
		upDownState = UpDownState.Down;
		popupState = CurrentState.empty;
	}




	public void msgLoadFinished() {
		// TODO Auto-generated method stub
		String msg = new String(name + ": msg load finished");
		System.out.println(msg);
		//log.add(new LoggedEvent(msg));
		popupState = CurrentState.full;
		popupEvents.add(PopupEvent.loadFinished);
		stateChanged();
	}
	public void msgReleaseFinished(){
		String msg = new String(name + ": msg release finished");
		System.out.println(msg);
		//log.add(new LoggedEvent(msg));
		popupState = CurrentState.empty;
		
		popupEvents.add(PopupEvent.releaseFinished);
		stateChanged();
	}

	public void msgPopupMovedUp() {
		// TODO Auto-generated method stub
		String msg = new String(name + ": msg popup moved up");
		System.out.println(msg);
		//log.add(new LoggedEvent(msg));
		upDownState = UpDownState.Up;
		popupEvents.add(PopupEvent.popupMovedUp);
		stateChanged();
	}
	public void msgPopupMovedDown() {
		// TODO Auto-generated method stub
		String msg = new String(name + ": msg popup moved down");
		System.out.println(msg);
		//log.add(new LoggedEvent(msg));
		upDownState = UpDownState.Down;
		popupEvents.add(PopupEvent.popupMovedDown);
		stateChanged();
	}

	public void msgImFree() {
		// next cf is ready
		String msg = new String(name + ": msg im free from next CF");
		System.out.println(msg);
		//log.add(new LoggedEvent(msg));
		nextCFState = NextCFState.free;
		popupEvents.add(PopupEvent.nextCFIsFree);
		stateChanged();
	}
	public void msgHereIsFinishedGlass(Operator o) {
		// TODO Auto-generated method stub
		
		//popupEvents.add(PopupEvent.finishedGlassFromOperator);
		String msg = new String(name + ": msg here is finished glass from operator - " + o.getName());
		System.out.println(msg);
		//log.add(new LoggedEvent(msg));
		if(upOperator.operator == o ){
			//upOperator.requestOpen = false;
			waitingForFinishedGlass.release();
		}
		else if(downOperator.operator == o ){
			//downOperator.requestOpen = false;
			waitingForFinishedGlass.release();
		}
		stateChanged();
	}
	public void msgHereIsGlass() {
		// TODO Auto-generated method stub
		String msg = new String(name + ": msg here is glass from conveyor");
		System.out.println(msg);
		//log.add(new LoggedEvent(msg));
		requestToPushGlass = true;
		popupEvents.add(PopupEvent.newGlassFromConveyor);
		stateChanged();
	}
	public void msgIHaveFinshedGlass(Operator o) {
		// TODO Auto-generated method stub
		String msg = new String(name + ": msg I have finished glass to give recieved");
		System.out.println(msg);
		//log.add(new LoggedEvent(msg));
		if (o == upOperator.operator)
			upOperator.requestOpen = true;
		else if (o == downOperator.operator)
			downOperator.requestOpen = true;
		
		popupEvents.add(PopupEvent.finishedGlassFromOperator);
		stateChanged();
	}
	public int numOperatorsCurrentlyOccupied(){
		int count = 0;
		if (upOperator.occupied) count++;
		if (downOperator.occupied) count++;
		return count;
	}
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (!popupEvents.isEmpty()){
			PopupEvent event = popupEvents.remove(0);
			switch(event){
				case loadFinished:
					System.out.println(name+ ": handling load finished event.");
					handleLoadFinished();
					break;
				case nextCFIsFree:
					System.out.println(name+ ": handling next cf is free event.");
					handleNextCFIsFree();
					break;
				case popupMovedDown:
					System.out.println(name+ ": handling popup down event.");
					handlePopupDown();
					break;
				case popupMovedUp:
					System.out.println(name+ ": handling popup up event.");
					handlePopupUp();
					break;
				case finishedGlassFromOperator:
					System.out.println(name+ ": handling finished glass from operator event.");
					handleNewFinishedGlassRequest();
					break;
				case newGlassFromConveyor:
					System.out.println(name+ ": handling new glass from conveyor event.");
					handleNewGlassFromConveyor();
					break;
				case releaseFinished:
					System.out.println(name+ ": handling release finished event.");
					handleReleaseFinished();
					break;
			default:
				break;
				
			}
			return true;
		}

		return false;
	}




	public void handleNewFinishedGlassRequest() {
		if (upDownState == UpDownState.Up){
			if (popupState == CurrentState.empty)
				acceptFinishedGlass();
			else if (popupState == CurrentState.full)
				//at this point, something is weird, move down just in case 
				moveDown();
		}
		else if(upDownState == UpDownState.Down){
			if (popupState == CurrentState.empty)
				moveUp();
			else if (popupState == CurrentState.full)
				;//nothing to do
			
		}
	}


	public void moveDown() {
		// TODO Auto-generated method stub
		
		parentCF.movePopupDown();
	}




	public void moveUp() {
		// TODO Auto-generated method stub
		parentCF.movePopupUp();
	}




	public void acceptNewGlass(){
		requestToPushGlass = false;
		//get new glass
		conveyor.msgPopupIsReady();
		//gui will now push glass onto popup
	}

	public void handleReleaseFinished() {
		if (upDownState == UpDownState.Down ){
			if (numOperatorsCurrentlyOccupied() < 2 && requestToPushGlass){
				acceptNewGlass();
				
			}
			else if (requestToPushGlass && !parentCF.doesLastGlassOnCFNeedProcessing()){
				acceptNewGlass();
			}
			else if (requestToGiveFinishedGlassExists()){
				moveUp();
			}
			else
			{
				//do nothing
				System.out.println("Popup released glass, nothing to do for now");
			}
			
		}
		else if (upDownState == UpDownState.Up){
			if (parentCF.lineEmpty()){
				//stay up
			}
			else if (parentCF.doesLastGlassOnCFNeedProcessing() && (numOperatorsCurrentlyOccupied() == 2)){
				//stay up
			}
			else if (parentCF.doesLastGlassOnCFNeedProcessing() && (numOperatorsCurrentlyOccupied() < 2)){
				moveDown();
			}
		}
				
	}




	public boolean requestToGiveFinishedGlassExists() {
		// TODO Auto-generated method stub
		return (upOperator.requestOpen || downOperator.requestOpen);
	}




	public void handleLoadFinished() {
		if (upDownState == UpDownState.Up){
			moveDown();
		}
		else if (upDownState == UpDownState.Down){
			if (parentCF.lastItemBeenProcessed() || !parentCF.doesLastGlassOnCFNeedProcessing()){
				if (nextCFState == NextCFState.busy)
					parentCF.stopConveyor();
				else
					pushGlass();
			}
			else
				moveUp();
		}
	}




	public void handlePopupUp() {
		if (popupState == CurrentState.full)
			giveGlassToFreeOperator(); //there should be one open
		else if (popupState == CurrentState.empty)
		{
			if (requestToGiveFinishedGlassExists())
				acceptFinishedGlass();
			else if (numOperatorsCurrentlyOccupied() == 0 || parentCF.numGlassOnLine() != 0)
				moveDown();
			else
				;//stay up
		}
	}


	public void giveGlassToFreeOperator() {
		popupState = CurrentState.empty;
		
		if (!upOperator.occupied)
			{parentCF.giveGlassToOperator(upOperator.operator);
			upOperator.occupied = true;
			}
		else if (!downOperator.occupied)
			{parentCF.giveGlassToOperator(downOperator.operator);
			downOperator.occupied = true;
			}
		else
			System.out.println(": error in giving glass to operator, none available");
		
	}




	public void acceptFinishedGlass(){//multistep action
		
		if (upOperator.requestOpen){
			
			upOperator.operator.msgIAmFree();
			try {
				waitingForFinishedGlass.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//acquired
			upOperator.requestOpen = false;
		}
		if (downOperator.requestOpen){
			
			downOperator.operator.msgIAmFree();
			try {
				waitingForFinishedGlass.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//acquired
			downOperator.requestOpen = false;
		}
	}

	public void handleNextCFIsFree() {
		
		if (upDownState == UpDownState.Down){
			if (popupState == CurrentState.full && (parentCF.lastItemBeenProcessed() || !parentCF.doesLastGlassOnCFNeedProcessing()) ){
				pushGlass();
			}
		}
	}




	public void handlePopupDown() {
		if (popupState == CurrentState.full){
			if (nextCFState == NextCFState.free){
				pushGlass();
			}
			else{
				//was going to stop, but ill let the conveyor stop it if another glass gets there
			}
		}
		else if (popupState == CurrentState.empty && parentCF.doesLastGlassOnCFNeedProcessing())//empty
		{
			if (numOperatorsCurrentlyOccupied() == 2){
				parentCF.stopConveyor();
			}
			else if (numOperatorsCurrentlyOccupied() < 2){
				acceptNewGlass();
			}
		}
		else if (popupState == CurrentState.empty && !parentCF.doesLastGlassOnCFNeedProcessing())//empty
		{
			acceptNewGlass();
		}
	}




	public void handleNewGlassFromConveyor() {
		if (popupState == CurrentState.empty && upDownState == UpDownState.Down){
			if (parentCF.doesLastGlassOnCFNeedProcessing()){
				if (numOperatorsCurrentlyOccupied() < 2)
					acceptNewGlass();
				else
					;//conveyor should be stopped, so no need to stop
			}
			else if (!parentCF.doesLastGlassOnCFNeedProcessing() ){
				acceptNewGlass();
			}
		}
		else if (popupState == CurrentState.empty && upDownState == UpDownState.Up){
			if (numOperatorsCurrentlyOccupied() < 2 && parentCF.doesLastGlassOnCFNeedProcessing())
				
				moveDown();
			else if (!parentCF.doesLastGlassOnCFNeedProcessing())
				moveDown();
		}
	}

	public void pushGlass(){
		//popupState = CurrentState.empty;
		nextCFState = NextCFState.busy;
		//System.out.println("size of glass " + glassOnCF.size());
		parentCF.pushGlassOnPopup();
		
	}
	
	public void pushGlassOnPopup(){
		Integer [] args= new Integer[1];
		args[0] = AlexsConveyorFamily.cfIndex;
		parentCF.transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_RELEASE_GLASS, args);
		System.out.println("num of glass on line " + glassOnCF.size());
		nextCF.msgHereIsGlass(glassOnCF.remove(glassOnCF.size()-1).glass);
	}



	public boolean inEmptyState() {
		// TODO Auto-generated method stub
		return (popupState == CurrentState.empty);
	}
	public boolean upNotDown(){
		return upDownState == UpDownState.Up;// && upDownState != UpDownState.Down);
	}




	public void setConveyorAgent(ConveyorAgent conveyorAgent) {
		// TODO Auto-generated method stub
		this.conveyor = conveyorAgent; 
	}




	public void setParentCF(AlexsConveyorFamily alexsConveyorFamily) {
		// TODO Auto-generated method stub
		parentCF = alexsConveyorFamily;
	}




	public void setNextCF(ConveyorFamily cf) {
		// TODO Auto-generated method stub
		nextCF = cf;
	}




	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}












}
