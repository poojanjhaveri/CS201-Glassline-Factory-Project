package engine.agent.Alex;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.agent.Agent;
import engine.agent.Luis.ConveyorAgent_LV;
import engine.agent.Luis.ConveyorFamilyAgent_LV;
import engine.agent.Luis.PopUpAgent_LV;
import engine.agent.Luis.Interface.PopUp_LV;
import engine.interfaces.ConveyorFamily;

public class Operator extends Agent{

	public Operator(String name, TChannel tc, int workStationNum){
		super(name);
		popup = new Semaphore(0, true);
		machined = new Semaphore(0, true);
		mychannel = tc;
		workstation_number = workStationNum ;
		glasses = new ArrayList<MyGlass>();
		breakNextGlassPiece = false;
		doesntGiveBackGlass = false;
	}
	/*
	 * DATA
	*****/
	enum LoadingState {Waiting, Loading, Loaded};
	enum MachiningState {Waiting, Machining, Finished};
	private Transducer transducer;
	private TChannel mychannel;
	boolean breakNextGlassPiece, doesntGiveBackGlass;

	int workstation_number;
	ConveyorFamilyAgent_LV myPopupAgent;
	ArrayList<MyGlass> glasses; //should only have one piece, but just in case
	class MyGlass{
		public Glass glass;
		public LoadingState lState;
		public MachiningState mState;
		MyGlass(Glass g){glass = g; lState = LoadingState.Waiting; 
		mState = MachiningState.Waiting;}
	}
	Semaphore popup;
	Semaphore machined;

	/*
	 * Messages
	 *
	 */


	public void msgHereIsGlass(Glass g){
		Do("Received a glass");

		glasses.add(new MyGlass(g));
		stateChanged();
	}
	public void msgIAmFree(){
		popup.release();
	}
	//from transducer
	private void msgDoneMachining(){

		machined.release();
	}
	//from tr
	public void msgLoadFinished(){
		if (!glasses.isEmpty())
		{
			print("Load finished");

			glasses.get(0).lState = LoadingState.Loaded;
			stateChanged();
		}
		else
		{
			print("Glass couldn't be found, must be broken/taken off line");
		}

	}
	private void msgReleaseFinished() {
		// TODO Auto-generated method stub


	}
	/*
	 * Scheduler
	 */



	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		//Do("Scheduler running");
		if (!glasses.isEmpty() && glasses.get(0).lState == LoadingState.Waiting)
		{
			loadGlass();
			return true;
		}
		if (!glasses.isEmpty() && glasses.get(0).mState == MachiningState.Waiting 
				 && glasses.get(0).lState == LoadingState.Loaded)
		{
			if (breakNextGlassPiece)
				breakGlass(glasses.get(0));
			else if (doesntGiveBackGlass)
				doesntGiveBack(glasses.get(0));
			else
				machineGlass(glasses.get(0));
			return true;
		}
		return false;
	}



	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		if(channel == mychannel &&  (Integer)args[0] == workstation_number) {
		if (event == TEvent.WORKSTATION_GUI_ACTION_FINISHED)
			msgDoneMachining();
		else if (event == TEvent.WORKSTATION_LOAD_FINISHED)
			msgLoadFinished();
		else if (event == TEvent.WORKSTATION_RELEASE_FINISHED)
			msgReleaseFinished();
		}

	}
	/*
	Actions
	*/
	private void doesntGiveBack(MyGlass myGlass) {
		print("AXN, doesnt give back glass");
		doesntGiveBackGlass = false;
		print("Machining glass piece " + myGlass.glass.getNumber());
		Integer[] args = new Integer[1];
		args[0] = workstation_number;
		transducer.fireEvent(mychannel, TEvent.WORKSTATION_DO_ACTION, args);
		try {
			machined.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print("Done machining glass piece" + myGlass.glass.getNumber());
		myPopupAgent.msgIHaveNoGlass(this, true);
		try {
			popup.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void breakGlass(MyGlass myGlass) {
		print("AXN, breaking glass");
		breakNextGlassPiece = false;
		glasses.remove(myGlass);
		myPopupAgent.msgIHaveNoGlass(this, false);

		popup.drainPermits();

	}

	private void loadGlass() {
		// TODO Auto-generated method stub
		Do("Loading glass");
		Integer[] args = new Integer[1];
		args[0] = workstation_number;
		transducer.fireEvent(mychannel, TEvent.WORKSTATION_DO_LOAD_GLASS, args);
		glasses.get(0).lState = LoadingState.Loading;
	}

	private void machineGlass(MyGlass myGlass){
		print("Machining glass piece " + myGlass.glass.getNumber());
		Integer[] args = new Integer[1];
		args[0] = workstation_number;
		transducer.fireEvent(mychannel, TEvent.WORKSTATION_DO_ACTION, args);
		try {
			machined.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print("Done machining glass piece" + myGlass.glass.getNumber());
		myPopupAgent.msgIHaveGlassFinished(this);
		try {
			popup.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Glass g = glasses.remove(0).glass;
		myPopupAgent.msgHereIsFinishedGlass(this,g);
		print("Releasing glass");
		transducer.fireEvent(mychannel, TEvent.WORKSTATION_RELEASE_GLASS, args);
		print("Released glass piece to popup, glass #" + myGlass.glass.getNumber());
	}

	public void print(String n){
	System.out.println(name + ": " + n);
	}

	public void setTransducer(Transducer t) {
		transducer = t;
		t.register(this, mychannel);
	}

	public void setConveyorFamily(ConveyorFamily c5) {
		this.myPopupAgent = (ConveyorFamilyAgent_LV) c5;
	}
	public void breakNextGlass(){breakNextGlassPiece = true;}
	public void dontGiveNextGlassBack(boolean b)
	{
		print("Receieve dont give glass back");
		if (b)
			doesntGiveBackGlass = true;
		else 
			doesntGiveBackGlass = false;
		stateChanged();
	}
}