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
import engine.agent.Alex.BinAgent;
import engine.conveyorfamily.Interfaces_Poojan.Conveyor_PJ;
import engine.conveyorfamily.Interfaces_Poojan.ConveyorFamilyInterface;
import engine.conveyorfamily.Interfaces_Poojan.InLineMachine_PJ;
import engine.conveyorfamily.Interfaces_Poojan.Operator_PJ;
import engine.conveyorfamily.Interfaces_Poojan.Popup_PJ;
import engine.conveyorfamily.Interfaces_Poojan.TransducerInterface_PJ;
import engine.conveyorfamily_Poojan.PopupAgent_PJ.GlassStatusPopup;
import engine.interfaces.ConveyorFamily;


public class HalfConveyorAgent extends Agent implements Conveyor_PJ {


	private String name;
	private int number;
	private Transducer myTransducer;
	public ConveyorFamily MyFamily;
	public ConveyorFamily NEXTFamily;
	public ConveyorFamily PREVIOUSFamily;

	private InLineMachine_PJ myinline;

	public enum GlassStatusConveyor{NEW,DONE,ONENTRYSENSOR,CHECKED, ONEXITSENSOR, NEEDSMACHINEPROCESSING, NOMACHINEPROCESSING, CHECKINGPROCESSING, FIRSTDONE, INLINEBUSY, ONLASTSENSOR, ONLASTSENSORSTOP, ONTHIRDSENSOR, THIRDSENSORDONE, CHECKEDDONE};

	private Boolean isPopUpBusy;
//	private Boolean isINLINEBusy;
	private Boolean isConveyorRunning;
	public boolean isNextConveyorFamilyBusy;


	private enum ConveyorState{Running,Stopped,Jammed,Need_Fix, Need_Run, Need_Break};
	private enum SensorState{Pressed, Released,None};

	ConveyorState conveyor1;

	private List<MyCGlass> glassonconveyor = Collections.synchronizedList(new ArrayList<MyCGlass>());



	public HalfConveyorAgent(String string,int number, ConveyorFamily c1, Transducer transducer,Popup_PJ p1,InLineMachine_PJ p2,ConveyorFamily cp) {
		
	this.name=string;
	this.number=number;
	this.MyFamily=c1;
	this.myinline=p2;
	this.PREVIOUSFamily=cp;

	this.isPopUpBusy=false;
	myTransducer = transducer;


	conveyor1=ConveyorState.Need_Run;

	myTransducer.register(this, TChannel.CUTTER);
	myTransducer.register(this, TChannel.SENSOR);
	myTransducer.register(this, TChannel.ALL_AGENTS);

	Object[] conveyornumber={this.number};
	isNextConveyorFamilyBusy=false;
	}


	public class MyCGlass
	{
		private Glass pcglass;
		private GlassStatusConveyor status;
		private Boolean NeedsProcessing;

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


	// MESSAGES




		public void msgHereIsGlass(Glass g1) {
			print("Glass Recieved."+this.number);
			MyCGlass mcg = new MyCGlass(g1);
			glassonconveyor.add(mcg);
			print("glass number "+g1.getNumber());
			stateChanged();
		}

		
		public void msgIamFree() {
			stateChanged();
		}


		public void msgIsNextConveyorFamilyBusy() {
			
			isNextConveyorFamilyBusy=false;
			stateChanged();
		}






	@Override
	public boolean pickAndExecuteAnAction() {
		
		while(conveyor1==ConveyorState.Need_Fix)
		{
			unbreakConveyor(1);
		}

		while(conveyor1==ConveyorState.Need_Break)
		{
			breakConveyor(1);
		}

		while(conveyor1==ConveyorState.Need_Run)
		{
			startconveyor1();
		}
		
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

				 if(mg.status == GlassStatusConveyor.ONEXITSENSOR && isNextConveyorFamilyBusy){
					 nextconveyorfamilybusy(mg);
					 return true;
				 }
			 }
		 }; 


		 synchronized(glassonconveyor){

			 for(MyCGlass mg:glassonconveyor){

				 if(mg.status == GlassStatusConveyor.INLINEBUSY && !isNextConveyorFamilyBusy && conveyor1!=ConveyorState.Jammed){
					 starttheconveyor(mg);
					 return true;
					 			
				 }
			 }
		 };

		 synchronized(glassonconveyor){

			 for(MyCGlass mg:glassonconveyor){

				 if(mg.status == GlassStatusConveyor.ONEXITSENSOR && !isNextConveyorFamilyBusy){
					 PassingGlassToInLineMachine(mg);
					 return true;
				 }
			 }
		 };  
		 

		
		 
		 
		 synchronized(glassonconveyor){

			 for(MyCGlass mg:glassonconveyor){

				 if(mg.status == GlassStatusConveyor.DONE){
					 glassonconveyor.remove(mg);
					 return true;
				 }
			 }
		 }

		 
		return false;
	}






	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {


		if(channel == TChannel.SENSOR)
		{
			
			if(event == TEvent.SENSOR_GUI_PRESSED)
			{

				if((Integer)args[0]==2)
				{
					onentrysensor((Integer)args[1]);
			    };    	
			}

			if(event == TEvent.SENSOR_GUI_PRESSED)
			{
				if((Integer)args[0]==2)
				{
					onexitsensor((Integer)args[1]);
			    };    	
			}


			if(event == TEvent.SENSOR_GUI_RELEASED)
			{
				if((Integer)args[0]==3)
				{
					this.myinline.msgIamFreeForGlass();
				}

			}
			
			
		}
	}


	private void startconveyor1()
	{
		Object [] no={this.getNumber()};
    	myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,no);
    	conveyor1=ConveyorState.Running;
    	stateChanged();
	}
	
	private void starttheconveyor(MyCGlass mg) {
		print("start conveyor 1");
		Object [] no={this.getNumber()};
    	myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,no);
    	mg.status=GlassStatusConveyor.ONEXITSENSOR;
    	conveyor1=ConveyorState.Running;
    	
    	print("starttheconveyor ACTION");
    	stateChanged();
	}
	

	/**
	 * Glass on the entry sensor of half conveyor
	**/
	private void onentrysensor(Integer args) {

		synchronized(glassonconveyor){
			for(MyCGlass mg:glassonconveyor){

				if(mg.pcglass.getNumber() == args){
					{
						print("ON ENTRY SENSOR OF HALF CONVEYOR");
						mg.status=GlassStatusConveyor.ONENTRYSENSOR;
						stateChanged();
						return;
					}
				}
			}
			}

	}

	/**
	 * Glass on the exit sensor of half conveyor
	**/
	private void onexitsensor(Integer args) {

		synchronized(glassonconveyor){
			for(MyCGlass mg:glassonconveyor){

				if(mg.pcglass.getNumber() == args){
					{
						mg.status=GlassStatusConveyor.ONEXITSENSOR;
						stateChanged();
						return;
					}
				}
			}
			}

	}

	
	// ACTIONS

		private void checktheglass(MyCGlass mg) {

			//mg.status=GlassStatusConveyor.CHECKED;
			if(conveyor1==ConveyorState.Running)
			{
				print("IT IS RUNNING");
			}
			mg.status=GlassStatusConveyor.CHECKED;
			stateChanged();
			
		}
	
		

		private void nextconveyorfamilybusy(MyCGlass mg) {

			Object[] cno={this.number};
			 myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_STOP,cno);
			 mg.status=GlassStatusConveyor.INLINEBUSY;
			 conveyor1=ConveyorState.Stopped;
			 
			 print("nextconveyorfamilybusy ACTION");
			 stateChanged();
		}



	private void PassingGlassToInLineMachine(MyCGlass mg) {

		

		  Object[] cno ={this.number};
			isNextConveyorFamilyBusy=true;
			this.NEXTFamily.msgHereIsGlass(mg.pcglass);
			print("RELEASE THE GLASS. PROCESSING DONE");
			
	    	print("CONVEYOR STATE 1: "+conveyor1);
			print("xxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			mg.status=GlassStatusConveyor.DONE;
			stateChanged();
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

	public void getoperatorlist(){

    }

	public Boolean getisPopUpBusy(){
        return this.isPopUpBusy;
    }


	public Boolean getisINLINEBusy()
	{
		return isNextConveyorFamilyBusy;
		//return this.isINLINEBusy;
	}



	@Override
	public void setOperator(Operator_PJ o1) {
		//

	}


	@Override
	public void setisINLINEBusy(Boolean s) {
		// 
	//	isINLINEBusy=s;
		stateChanged();
	}



	@Override
	public void msgOperatorIsfree(Operator_PJ operatorAgent) {
		// 

	}




	private void breakConveyor(int i) {

		Object [] no={i};
		if(i==1)
		{
			myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_STOP,no);
			conveyor1=ConveyorState.Jammed;
		}
    	stateChanged();
    	
	}


	private void unbreakConveyor(int i) {
		Object [] no={i};
		if(i==1)
		{
			myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,no);
			conveyor1=ConveyorState.Running;
		}
    	stateChanged();

	}




	



	public void setbrokenstatus(boolean s,int i) {
		// 
		if(s)
		{
		Object [] no={i};
		if(i==0)
		{
			
		}
		else
		{
			conveyor1=ConveyorState.Need_Break;
		}
		}
		else
		{
			Object [] no={i};
			if(i==0)
			{
				
			}
			else
			{
				conveyor1=ConveyorState.Need_Fix;
			}
		}

    	stateChanged();

	}











}