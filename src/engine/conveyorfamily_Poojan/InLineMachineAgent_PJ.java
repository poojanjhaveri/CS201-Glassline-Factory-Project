package engine.conveyorfamily_Poojan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.agent.Agent;
import engine.conveyorfamily.Interfaces_Poojan.Conveyor_PJ;
import engine.conveyorfamily.Interfaces_Poojan.ConveyorFamilyInterface;
import engine.conveyorfamily.Interfaces_Poojan.InLineMachine_PJ;
import engine.conveyorfamily.Interfaces_Poojan.Operator_PJ;
import engine.conveyorfamily.Interfaces_Poojan.Popup_PJ;
import engine.conveyorfamily.Interfaces_Poojan.TransducerInterface_PJ;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ.GlassStatusConveyor;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ.MyCGlass;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ.MyOperators;
import engine.interfaces.ConveyorFamily;



public class InLineMachineAgent_PJ extends Agent implements InLineMachine_PJ  {

	private int number;
	private String name;
	private Transducer myTransducer;
	public Conveyor_PJ myconveyor;
	public ConveyorFamily MyFamily;
	private ConveyorFamily NEXTFamily;
	
	private List<MyPGlass> glassoninline = Collections.synchronizedList(new ArrayList<MyPGlass>());
	private List<MyPGlass> finishedglassonpopup = Collections.synchronizedList(new ArrayList<MyPGlass>());
	
	
	private boolean secondconveyorfree;
	
	private boolean inlinebroken;

	private enum GlassStatusInline{NEW,CHECKING,NOPROCESSING,PROCESSING,BEINGPROCESSED, DONE,DONE2, PROCESSINGDONE};
	public InLineMachineAgent_PJ(String string, int i, ConveyorFamily conveyorFamily,
			Transducer transducer) {
	
		
		this.name=string;
		this.number=i;
		secondconveyorfree=true;
		this.MyFamily=conveyorFamily;
		
		myTransducer = transducer;
		myTransducer.register(this, TChannel.ALL_GUI);
		myTransducer.register(this, TChannel.CUTTER);
		myTransducer.register(this, TChannel.CONVEYOR);
		myTransducer.register(this, TChannel.ALL_AGENTS);
	}
	
	
	
	
	public class MyPGlass
	{
		private Glass pcglass;
		private GlassStatusInline status;
		Boolean NeedsProcessing;
		
		public MyPGlass(Glass g, boolean b)
		{
			this.pcglass=g;
			this.status=GlassStatusInline.NEW;
			this.NeedsProcessing=b;
			print(""+b);
		}
	}
	
	
	// MESSAGES

		@Override
		public void msgGlassDoesNotNeedProcessing(Glass glass) {
			print("Glass received. Glass does not need processing");
			
			glassoninline.add(new MyPGlass(glass,false));
			stateChanged();
		}
		
		public void msgGlassNeedsProcessing(Glass pcglass,Boolean choice) {
			
			print("Glass received. Glass needs processing"+choice+pcglass.getNumber());
			glassoninline.add(new MyPGlass(pcglass,choice));
			stateChanged();
		}
		

		@Override
		public void msgIamFreeForGlass() {
			
			secondconveyorfree = true;
			stateChanged();
		}
	
	

	@Override
	public boolean pickAndExecuteAnAction() {
		

	synchronized(glassoninline){
	    	
			for(MyPGlass mg:glassoninline){
		
			    if(mg.status == GlassStatusInline.CHECKING ){
				checkingforglass(mg);
				return true;
			    }
			}
		    	};
		
		
		    	synchronized(glassoninline){
			    	
					for(MyPGlass mg:glassoninline){
				
					    if(mg.status == GlassStatusInline.PROCESSING && !inlinebroken ){
					    processtheglassaction(mg);
						return true;
					    }
					}
				    	};    	
		    	
		    	
		synchronized(glassoninline){
	    	
			for(MyPGlass mg:glassoninline){
			    if(mg.status == GlassStatusInline.NOPROCESSING && secondconveyorfree && !inlinebroken){
			    
			    shiptheglasstonextconveyor(mg);
				return true;
			    }
			}
		    	};
		    	
		    	synchronized(glassoninline){
			    	
					for(MyPGlass mg:glassoninline){
					    if(mg.status == GlassStatusInline.PROCESSINGDONE && secondconveyorfree && !inlinebroken ){
					    	
						shiptheglasstonextconveyor(mg);
						return true;
					    }
					}
				    	}; 	
		    	
				    	
				    	
				    	synchronized(glassoninline){
					    	
							for(MyPGlass mg:glassoninline){
						
							    if(mg.status == GlassStatusInline.DONE ){
								glassoninline.remove(mg);
								return true;
							    }
								}
						    	}; 	

		
		// 
		return false;
	}

	
	private void shiptheglasstonextconveyor(MyPGlass mg) {
		// 
		print("RELEASING GLASS");
		secondconveyorfree=false;
		mg.status=GlassStatusInline.DONE;
		myTransducer.fireEvent(TChannel.CUTTER, TEvent.WORKSTATION_RELEASE_GLASS, null);
		myconveyor.msgIamFree();
		stateChanged();
	
		
	}


	private void glassdone(MyPGlass mg)
	{
		mg.status=GlassStatusInline.DONE2;
		stateChanged();
	}



	private void processtheglassaction(MyPGlass mg) {
		// 
		myTransducer.fireEvent(TChannel.CUTTER, TEvent.WORKSTATION_DO_ACTION, null);
		print("Workstation do action");
		mg.status=GlassStatusInline.DONE2;
		stateChanged();
	}






	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		if(channel == TChannel.CUTTER)
		{
			if(event == TEvent.WORKSTATION_LOAD_FINISHED)
			{
				synchronized(glassoninline){
					for(MyPGlass mg:glassoninline){
					    if(mg.status == GlassStatusInline.NEW ){
					    	print("cutter called");
						mg.status=GlassStatusInline.CHECKING;
						
						
						stateChanged();
						return;
					    }	
					}
			}
			}
			
			if(event == TEvent.WORKSTATION_LOAD_FINISHED)
			{
				
				synchronized(glassoninline){
					for(MyPGlass mg:glassoninline){
						 if(mg.status == GlassStatusInline.DONE2 ){
							 mg.status=GlassStatusInline.PROCESSINGDONE;
							 print("Load fininshed");
							 
								stateChanged();
								return;
							    }	
					}
			}
			}
			
			
			
			if(event == TEvent.WORKSTATION_GUI_ACTION_FINISHED)
			{
				
				synchronized(glassoninline){
					for(MyPGlass mg:glassoninline){
					    if(mg.status == GlassStatusInline.DONE2 ){
					    	mg.status=GlassStatusInline.PROCESSINGDONE;
					  
						stateChanged();
						return;
					    }	
					}
			}
			}
			
			
			}
			
		
	}
	
	
	
	public String getName(){
        return name;
    }
	
	public int getNumber(){
        return number;
    }
	
	
	public int getglassonpopupsize(){
        return  glassoninline.size();
    }
	
	
	public int getfinishedglassonpopupsize(){
        return  finishedglassonpopup.size();
    }
	

	
	
	

	private void checkingforglass(MyPGlass mg)
	{
		if(mg.NeedsProcessing==true)
		{
			mg.status=GlassStatusInline.PROCESSING;
		}
		else
		{
			mg.status=GlassStatusInline.NOPROCESSING;
			print("NO PRocessing");
			
		}
		stateChanged();
	}





	public void setConveyor(Conveyor_PJ conveyor) {
		
		this.myconveyor=conveyor;
	}

	public void setbrokenstatus(boolean brokenstate) {
		inlinebroken=true;
		stateChanged();
		
	}

	
	
	

}

