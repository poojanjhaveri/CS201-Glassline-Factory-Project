/**
 * 
 */
package engine.conveyorfamily_Poojan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Agent;
import engine.conveyorfamily.Interfaces_Poojan.ConveyorFamilyInterface;
import engine.conveyorfamily.Interfaces_Poojan.Operator_PJ;
import engine.conveyorfamily.Interfaces_Poojan.Popup_PJ;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ.MyCGlass;
import engine.interfaces.ConveyorFamily;

/**
 * @author madiphone14
 *
 */
public class OperatorAgent_PJ extends Agent implements Operator_PJ {

	
	private String name;
	private int number;
	private ConveyorFamily myFamily;
	 public enum ostatus {NEW,PROCESS,DONE, WAITINGFORPEMISSION, WAITINGFORPERMISSION, SEND};
	 private List<Oglass> glasswithoperator = Collections.synchronizedList(new ArrayList<Oglass>());
	   Timer timer = new Timer();
	
	public class Oglass
	{
		private Glass g;
		private ostatus status;
		
		public Oglass(Glass g1)
		{
			this.g=g1;
			this.status=ostatus.NEW;
		}
	}
	
	public OperatorAgent_PJ(int no,ConveyorFamily conv1,String name)
	{
		this.myFamily=conv1;
		this.number=no;
		this.name=name;
	}
	
	
	

	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		
			synchronized(glasswithoperator){
	    	
			for(Oglass o:glasswithoperator){
		
			    if(o.status == ostatus.NEW ){
				processtheglass(o);
				return true;
			    }
			}
		    	};
		    	
		    	for(Oglass o:glasswithoperator){
		    		
				    if(o.status == ostatus.PROCESS ){
				    	
					informtheconveyor(o);
					return true;
				    }
			    	};	
			    	

			    	for(Oglass o:glasswithoperator){
			    		
					    if(o.status == ostatus.SEND ){
						senditbacktopopup(o);
						return true;
					    }
				    	};	
			    	 	
			    	
		    	
		
		
		
		return false;
	}

	


	private void senditbacktopopup(Oglass o) {
		// TODO Auto-generated method stub
	//	this.myFamily.msgHereIsFinishedGlass(o.g,this);
		
	}




	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	// MESSAGES
	
	@Override
	public void msgHereIsGlass(Glass pcglass) {
		// TODO Auto-generated method stub
		print("I recieved the glass for processing");
		glasswithoperator.add(new Oglass(pcglass));
		stateChanged();
	}

	
	// ACTIONS
	
	
	private void processtheglass(Oglass o) {
		// TODO Auto-generated method stub
	
		/*
		timer.schedule(new TimerTask(){
		    public void run(){//this routine is like a message reception    
		    	
		    }	
		}, 1000);*/
		o.status=ostatus.PROCESS;
		
		stateChanged();
	}

	private void informtheconveyor(Oglass o) {
		// TODO Auto-generated method stub
		print("I have finished processing the glass");
	//	this.myFamily.msgIHaveFinishedGlass(this);
		o.status=ostatus.WAITINGFORPEMISSION;
		stateChanged();
	}




	@Override
	public void msgIAmFree() {
		print("popup is free");
		// TODO Auto-generated method stub
		for(Oglass o:glasswithoperator){
    		
		    if(o.status == ostatus.WAITINGFORPERMISSION ){
			o.status=ostatus.SEND;
			return;
		    }
	    	};	
	}

	public int getnumber()
	{
		return this.number;
	}
	
	public String getName(){
        return name;
    }
	
}
