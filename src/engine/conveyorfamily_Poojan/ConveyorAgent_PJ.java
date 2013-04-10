package engine.conveyorfamily_Poojan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mocks_PJ.MockOperator;


import shared.Glass;
import shared.enums.SharedData.WorkType;
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
import engine.conveyorfamily_Poojan.PopupAgent_PJ.GlassStatusPopup;


public class ConveyorAgent_PJ extends Agent implements Conveyor_PJ {
	
	
	private String name;
	private int number;
	private Transducer myTransducer;
	public ConveyorFamilyInterface MyFamily;

	private Popup_PJ mypopup;
	private InLineMachine_PJ myinline;
	
	public enum GlassStatusConveyor{NEW,DONE,ONENTRYSENSOR,CHECKED, ONEXITSENSOR, NEEDSMACHINEPROCESSING, NOMACHINEPROCESSING, CHECKINGPROCESSING, FIRSTDONE};
	public enum operatorstatus{FREE,BUSY};
	
	private Boolean isPopUpBusy;
	private Boolean isINLINEBusy;
	private Boolean isConveyorRunning;
	
	private List<MyCGlass> glassonconveyor = Collections.synchronizedList(new ArrayList<MyCGlass>());
	private List<MyOperators> operatorlist = Collections.synchronizedList(new ArrayList<MyOperators>());
	
	
	public ConveyorAgent_PJ(String string,int number, ConveyorFamilyInterface c1, Transducer transducer,Popup_PJ p1,InLineMachine_PJ p2) {
		// TODO Auto-generated constructor stub
	this.name=string;
	this.number=number;
	this.MyFamily=c1;
	this.mypopup=p1;
	this.myinline=p2;
	
	
	this.isPopUpBusy=false;
	this.isINLINEBusy=false;
	myTransducer = transducer;
	
	myTransducer.register(this, TChannel.CUTTER);
	myTransducer.register(this, TChannel.SENSOR);
	myTransducer.register(this, TChannel.ALL_AGENTS);
	
	Object[] args={number};
	isConveyorRunning=true;
	myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,args);
	
	}
	
	public class MyCGlass
	{
		private Glass pcglass;
		private GlassStatusConveyor status;
		private Boolean NeedsProcessing;
		private MyOperators myoperator;
		
		public MyCGlass(Glass g)
		{
			this.pcglass=g;
			this.status=GlassStatusConveyor.NEW;
			
			
		}
		
	}
	
	public class MyOperators
	{
		private Operator_PJ op;
		private boolean occupied;
	
		
		public MyOperators(Operator_PJ o)
		{
			this.op=o;
			this.occupied=false;
		}
		
	}
	
	
	

	@Override
	public boolean pickAndExecuteAnAction() {

		synchronized(glassonconveyor){
	    	
			for(MyCGlass mg:glassonconveyor){
		
			    if(mg.status == GlassStatusConveyor.ONENTRYSENSOR ){
			    	
				checktheglass(mg);
				return true;
			    }
			}
		    	};
		    	
		    	synchronized(glassonconveyor){
			    	
					for(MyCGlass mg:glassonconveyor){
				
					    if(mg.status == GlassStatusConveyor.ONEXITSENSOR ){
					    	PassingGlassToInLineMachine(mg);
						return true;
					    }
					}
				    	};  
				    	
				    	synchronized(glassonconveyor){
					    	
							for(MyCGlass mg:glassonconveyor){
						
							    if(mg.status == GlassStatusConveyor.NOMACHINEPROCESSING && isPopUpBusy==false ){
								PassingGlassToPopup(mg);
								return true;
							    }
							}
						    	};   	
				    	   	
						    	
						    	synchronized(glassonconveyor){
							    	
									for(MyCGlass mg:glassonconveyor){
								
									    if(mg.status == GlassStatusConveyor.NEEDSMACHINEPROCESSING){
									//	PassingGlassToPopupToProcess(mg);
									    	PassingGlassToInLineMachine(mg);
print("sending sending");
										return true;
									    }
									}
								    	};   	
				    	
								    	/*
								    	synchronized(glassonconveyor){
									    	
											for(MyCGlass mg:glassonconveyor){
										
											    if(mg.status == GlassStatusConveyor.NEEDSMACHINEPROCESSING){
												PassingGlassToPopupToProcess(mg);
												return true;
											    }
											}
										    	};  */
		    	
		    	
		    	
		
		
		// TODO Auto-generated method stub
		return false;
	}



	


	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		
		
		// TODO Auto-generated method stub
		if(channel == TChannel.SENSOR)
		{
			if(event == TEvent.SENSOR_GUI_PRESSED)
			{
				
				if((Integer)args[0]==0)
				{
					for(MyCGlass mg:glassonconveyor){
					
						if(mg.pcglass.getNumber() == (Integer)args[1]){
							{
								Object [] no={this.getNumber()};
								if(isConveyorRunning)
								{
						    	myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,no);
						    	isConveyorRunning=true;
								}
								mg.status=GlassStatusConveyor.ONENTRYSENSOR;
								print("SENSOR PRESSED");
								print("Conveyor started"+this.getNumber());
								stateChanged();
							}
						}
					}
			    };    	
			}
			
			
			if(event == TEvent.SENSOR_GUI_RELEASED)
			{
				if((Integer)args[0]==this.getNumber())
				{
					
				//	for(MyCGlass mg:glassonconveyor){
						
					//	if(mg.pcglass.getNumber() == glassno){
						//	{
								print("SENSOR RELEASED");
								this.MyFamily.msgIamFree();
								this.MyFamily.msgIamFree();
							
						//	}
					//	}
					}
			    //};    	
			}
			
			if(event == TEvent.SENSOR_GUI_PRESSED)
			{
				if((Integer)args[0]==1)
				{
					for(MyCGlass mg:glassonconveyor){
						if(mg.pcglass.getNumber() == (Integer)args[1]){
							{
								print("2nd sensor");
								Object [] no={this.getNumber()};
						//		myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_STOP,no);
								isConveyorRunning=true;
								mg.status=GlassStatusConveyor.ONEXITSENSOR;
								stateChanged();
							}
						}
					}
			    };    	
			}
			
			if(event == TEvent.SENSOR_GUI_PRESSED)
			{
				if((Integer)args[0]==2)
				{	
					Object[] cno ={1};
					   myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,cno);
					}    	
			}
			
			if(event == TEvent.SENSOR_GUI_PRESSED)
			{
				if((Integer)args[0]==3)
				{	
					
					
					for(MyCGlass mg:glassonconveyor){
						if(mg.pcglass.getNumber() == (Integer)args[1]){
							{
								  if(!(this.MyFamily.getStatusOfNextConveyorFamily()))
									{
									  print("4th sensor");
										Object[] cno ={1};
										myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_STOP,cno);
									  
									  print("RELEASE THE GLASS. PROCESSING DONE");
									Object[] args1 = {1};
										myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,cno);
										mg.status=GlassStatusConveyor.DONE;
										//stateChanged();
									}
							}
					
						}
 
					} 

				}
			}
			
			
			
			
			if(event == TEvent.SENSOR_GUI_RELEASED)
			{
				if((Integer)args[0]==2)
				{	
					
					}    	
			}
			
			
			
		}
		
	}
	
	
	// MESSAGES

	public void msgHereIsGlass(Glass g1) {
		// TODO Auto-generated method stub
		print("Glass Recieved. Conveyor Start");
		glassonconveyor.add(new MyCGlass(g1));
		stateChanged();
	}
	
	public void msgIamFree() {
		// TODO Auto-generated method stub
		isPopUpBusy=false;
	}
	
	@Override
	public void msgOperatorIsfree(Operator_PJ operatorAgent) {
		// TODO Auto-generated method stub
		for(MyOperators mg:operatorlist){
			
		    if(mg.op == operatorAgent ){
			mg.occupied=false;
		    }
		}
	}
	
	
	
	// ACTIONS
	
	private void checktheglass(MyCGlass mg) {
		// TODO Auto-generated method stub
		print("Checking the functionalities of glass"+mg.pcglass.getRecipe(TChannel.CUTTER));
		mg.NeedsProcessing=mg.pcglass.getRecipe(TChannel.CUTTER);
		mg.status=GlassStatusConveyor.CHECKED;
	}
	
	
	private void PassingGlassToPopup(MyCGlass mg) {
		// TODO Auto-generated method stub
		print("Glass passed to the popup. Conveyor starts again");
		this.mypopup.msgGlassDoesNotNeedProcessing(mg.pcglass);
		Object[] args1={this.number};
		myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,args1);
		isConveyorRunning=true;
		mg.status=GlassStatusConveyor.DONE;
		
	}
	
	
	/*
	private void PassingGlassToPopupToProcess(MyCGlass mg) {
		// TODO Auto-generated method stub
		print("Glass passed to the popup. Conveyor STOP. Glass Needs Processing");
		this.mypopup.msgGlassNeedsProcessing(mg.pcglass,mg.myoperator.op);
		isPopUpBusy=true;
		mg.status=GlassStatusConveyor.DONE;	
	}*/
	
	private void PassingGlassToInLineMachine(MyCGlass mg) {
		// TODO Auto-generated method stub
		print("Glass passed to the inline machine. Conveyor STOP. Glass Needs Processing");
		this.myinline.msgGlassNeedsProcessing(mg.pcglass,mg.NeedsProcessing);
		
		//this.myinline.msgGlassDoesNotNeedProcessing(mg.pcglass);
		
		isINLINEBusy=true;
		mg.status=GlassStatusConveyor.FIRSTDONE;	
		
	}
	
	
	



	private void passtheglass(MyCGlass mg) {
		// TODO Auto-generated method stub
		
		mg.status=GlassStatusConveyor.CHECKINGPROCESSING;
		print("mg.status"+mg.NeedsProcessing);
		if(mg.NeedsProcessing==true)
		{
			
			
		  for(int i=0; i < operatorlist.size(); i++){
				
				if(!operatorlist.get(i).occupied ){
				    synchronized(operatorlist){
				    
				    	mg.myoperator=operatorlist.get(i);
				    	operatorlist.get(i).occupied=true;
				    	mg.status=GlassStatusConveyor.NEEDSMACHINEPROCESSING;
				    	this.mypopup.msgINeedToPassGlass();
				    	print("machine NEeds Processing");
				    	return;
				    }
				}
				
			    }
		}
		else
		{
			print("Machine does not need processing");
			this.mypopup.msgINeedToPassGlass();
			mg.status=GlassStatusConveyor.NOMACHINEPROCESSING;
			
			
		}
		
		
		
		
		
	}

	

	public void setOperator(Operator_PJ o1)
	{
		operatorlist.add(new MyOperators(o1));
	}
	
	
	public String getName(){
        return name;
    }

	public int getNumber(){
        return number;
    }
	
	public int getglassonconveyorsize(){
        return glassonconveyor.size();
    }
	
	public int getoperatorlist(){
        return operatorlist.size();
    }
	
	public Boolean getisPopUpBusy(){
        return this.isPopUpBusy;
    }
	


	







}
