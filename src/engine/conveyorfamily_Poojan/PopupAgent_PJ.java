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
import engine.conveyorfamily.Interfaces_Poojan.Operator_PJ;
import engine.conveyorfamily.Interfaces_Poojan.Popup_PJ;
import engine.conveyorfamily.Interfaces_Poojan.TransducerInterface_PJ;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ.GlassStatusConveyor;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ.MyCGlass;
import engine.conveyorfamily_Poojan.ConveyorAgent_PJ.MyOperators;



public class PopupAgent_PJ  extends Agent implements Popup_PJ  {

	private int number;
	private String name;
	private Transducer myTransducer;
	private Conveyor_PJ myconveyor;
	public ConveyorFamilyInterface MyFamily;
	private ConveyorFamilyInterface NEXTFamily;
	private Semaphore popupconveyor = new Semaphore(1,true);
	private Semaphore popupoperator = new Semaphore(1,true);
	private List<ROperator> respondtooperators = Collections.synchronizedList(new ArrayList<ROperator>());
	
	private List<MyPGlass> glassonpopup = Collections.synchronizedList(new ArrayList<MyPGlass>());
	private List<MyPGlass> finishedglassonpopup = Collections.synchronizedList(new ArrayList<MyPGlass>());
	
	
	private boolean finisheddone;

	public enum GlassStatusPopup{NOPROCESSING,PROCESSING,BEINGPROCESSED, DONE,DONE2};
	public enum opstatus{RESPOND,DONE};
	public PopupAgent_PJ(String string, int i, ConveyorFamilyInterface conveyorFamily,
			Transducer transducer) {
		// TODO Auto-generated constructor stub
		
		this.name=string;
		this.number=i;

		this.MyFamily=conveyorFamily;
		
		myTransducer = transducer;
		myTransducer.register(this, TChannel.ALL_GUI);
		myTransducer.register(this, TChannel.CONVEYOR);
		myTransducer.register(this, TChannel.ALL_AGENTS);
	}
	

	public class ROperator
	{
		private Operator_PJ myoperator;
		private  opstatus status;
		
		public ROperator(Operator_PJ o)
		{
			this.myoperator=o;
			this.status=opstatus.RESPOND;
		 	
		}
	}
	
	
	
	public class MyPGlass
	{
		private Glass pcglass;
		private GlassStatusPopup status;
		private Operator_PJ myoperator;
		
		public MyPGlass(Glass g)
		{
			this.pcglass=g;
			this.status=GlassStatusPopup.NOPROCESSING;
		}
		
		public MyPGlass(Glass g,Operator_PJ o)
		{
			this.pcglass=g;
			this.myoperator=o;
			this.status=GlassStatusPopup.PROCESSING;
		}
	}
	

	@Override
	public boolean pickAndExecuteAnAction() {
		

		synchronized(glassonpopup){
	    	
			for(MyPGlass mg:glassonpopup){
		
			    if(mg.status == GlassStatusPopup.NOPROCESSING ){
				glassprocessingfinished(mg);
				return true;
			    }
			}
		    	};
		    	
		    	synchronized(glassonpopup){
			    	
					for(MyPGlass mg:glassonpopup){
				
					    if(mg.status == GlassStatusPopup.PROCESSING ){
					    	
					    	telltheoperator(mg);
					    	
						return true;
					    }
					}
				    	}; 	
				    	
				    	
				    	synchronized(glassonpopup){
					    	
							for(MyPGlass mg:glassonpopup){
						
							    if(mg.status == GlassStatusPopup.DONE ){
								glassonpopup.remove(mg);
								return true;
							    }
							}
						    	}; 	
		    	
						    	synchronized(finishedglassonpopup){
							    	
									for(MyPGlass mg:finishedglassonpopup){
								
									    if(mg.status == GlassStatusPopup.PROCESSING ){
										shiptheglass(mg);
										return true;
									    }
									}
								    	}; 	
								    	
								    	
								    	synchronized(finishedglassonpopup){
									    	
											for(MyPGlass mg:finishedglassonpopup){
										
											    if(mg.status == GlassStatusPopup.DONE ){
												glassonpopup.remove(mg);
												return true;
											    }
											}
										    	}; 	

						    	
		
		
		// TODO Auto-generated method stub
		return false;
	}

	



	private void telltheoperator(MyPGlass mg) {
		// TODO Auto-generated method stub
		mg.status=GlassStatusPopup.DONE2;
		mg.myoperator.msgHereIsGlass(mg.pcglass);
		
		stateChanged();
	}





	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		if(channel == TChannel.POPUP)
		{
			if(event == TEvent.POPUP_GUI_MOVED_DOWN)
			{
				if((Integer)args[0]==this.number)
				{
					
					if(finisheddone==true)
					{
					finisheddone=false;
					print("finsished");
					}
					else
					{
					
					this.myconveyor.msgIamFree();
					try {
						popupconveyor.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
			    };    	
			}	
			
			if(event == TEvent.POPUP_GUI_MOVED_UP)
			{
				if((Integer)args[0]==this.number)
				{
					
					for(ROperator mg:respondtooperators){
						
					    if(mg.status == opstatus.RESPOND ){
						mg.myoperator.msgIAmFree();
					    }
					}
					try {
						popupoperator.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    };    	
			}
			
			/*
			if(event == TEvent.POPUP_GUI_LOAD_FINISHED)
			{
				if((Integer)args[0]==this.number)
				{
					this.myconveyor.msgIamFree();
					try {
						popupconveyor.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    };    	
			}*/
			
			if(event == TEvent.POPUP_GUI_RELEASE_FINISHED)
			{
				if((Integer)args[0]==this.number)
				{
					for(MyPGlass mg:finishedglassonpopup){
					    if(mg.status == GlassStatusPopup.BEINGPROCESSED ){
					this.myconveyor.msgOperatorIsfree(mg.myoperator);
						mg.status=GlassStatusPopup.DONE;
						stateChanged();
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
	
	public int getrespondtooperatorssize(){
        return  respondtooperators.size();
    }
	
	public int getglassonpopupsize(){
        return  glassonpopup.size();
    }
	
	
	public int getfinishedglassonpopupsize(){
        return  finishedglassonpopup.size();
    }
	

	public void setConveyor(Conveyor_PJ conveyor) {
		// TODO Auto-generated method stub
		this.myconveyor=conveyor;
	}

	// MESSAGES
	
	
	@Override
	public void msgINeedToPassGlass() {
		// TODO Auto-generated method stub
		print("Asking for permission");
		Object[] args1 = {this.number};
		myTransducer.fireEvent(TChannel.POPUP,TEvent.POPUP_DO_MOVE_DOWN,args1);
	}



	@Override
	public void msgGlassDoesNotNeedProcessing(Glass glass) {
		popupconveyor.release();
		print("Glass received. Glass does not need processing");
		
		// TODO Auto-generated method stub
		glassonpopup.add(new MyPGlass(glass));
		stateChanged();
	}
	
	@Override
	public void msgGlassNeedsProcessing(Glass pcglass, Operator_PJ op) {
		// TODO Auto-generated method stub
		print("Glass received. Glass needs processing");
		glassonpopup.add(new MyPGlass(pcglass,op));
		stateChanged();
	}
	
	

	public void msgHereIsFinishedGlass(Glass g, Operator_PJ operatorAgent) {
		// TODO Auto-generated method stub
		print("here is the finished glass");
		popupoperator.release();
		Object[] args1 = {this.number};
		finisheddone=true;
		myTransducer.fireEvent(TChannel.POPUP,TEvent.POPUP_DO_MOVE_DOWN,args1);
		finishedglassonpopup.add(new MyPGlass(g,operatorAgent));
		stateChanged();

	}
	
	

	public void msgOperatorHasFinishedGlass(Operator_PJ o) {
		// TODO Auto-generated method stub
		print("Operator has finished");
		Object[] args1 = {this.number};
		myTransducer.fireEvent(TChannel.POPUP,TEvent.POPUP_DO_MOVE_UP,args1);
		respondtooperators.add(new ROperator(o));
	
		stateChanged();
		
	}

	
	
	
	
	// ACTIONS

	private void glassprocessingfinished(MyPGlass mg) {
		// TODO Auto-generated method stub
		if(!(this.MyFamily.getStatusOfNextConveyorFamily()))
				{
			print("RELEASE THE GLASS. PROCESSING DONE");
				Object[] args1 = {this.number};
					myTransducer.fireEvent(TChannel.POPUP,TEvent.POPUP_RELEASE_GLASS,args1);
					this.MyFamily.getNextConveyorFamily().msgHereIsGlass(mg.pcglass);
					mg.status=GlassStatusPopup.DONE;
					stateChanged();
				}
	} 



	

	private void shiptheglass(MyPGlass mg) {
		// TODO Auto-generated method stub
		Object[] args1 = {this.number};
		if(!this.MyFamily.getStatusOfNextConveyorFamily())
		{
			myTransducer.fireEvent(TChannel.POPUP,TEvent.POPUP_RELEASE_GLASS,args1);
			this.MyFamily.getNextConveyorFamily().msgHereIsGlass(mg.pcglass);	
			mg.status=GlassStatusPopup.BEINGPROCESSED;
			stateChanged();
		}
		
	}






	
	

}

