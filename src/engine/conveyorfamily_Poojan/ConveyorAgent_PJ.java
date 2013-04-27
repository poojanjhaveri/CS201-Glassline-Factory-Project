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


public class ConveyorAgent_PJ extends Agent implements Conveyor_PJ {


	private String name;
	private int number;
	private Transducer myTransducer;
	private ConveyorFamily MyFamily;
	public ConveyorFamily NEXTFamily;
	public ConveyorFamily PREVIOUSFamily;

	private InLineMachine_PJ myinline;

	private enum GlassStatusConveyor{NEW,DONE,ONENTRYSENSOR,CHECKED, ONEXITSENSOR, NEEDSMACHINEPROCESSING, NOMACHINEPROCESSING, CHECKINGPROCESSING, FIRSTDONE, INLINEBUSY, ONLASTSENSOR, ONLASTSENSORSTOP, ONTHIRDSENSOR, THIRDSENSORDONE};

	private Boolean isPopUpBusy;
	private Boolean isINLINEBusy;
	private Boolean isConveyorRunning;
	private boolean isNextConveyorFamilyBusy;


	private enum ConveyorState{Running,Stopped,Jammed,Need_Fix, Need_Run, Need_Break};
	private enum SensorState{Pressed, Released,None};

	private ConveyorState conveyor0;


	private Boolean secondconveyorfree;

	private List<MyCGlass> glassonconveyor = Collections.synchronizedList(new ArrayList<MyCGlass>());



	public ConveyorAgent_PJ(String string,int number, ConveyorFamily c1, Transducer transducer,Popup_PJ p1,InLineMachine_PJ p2,ConveyorFamily cp) {
	this.name=string;
	this.number=number;
	this.MyFamily=c1;
	this.myinline=p2;
	this.PREVIOUSFamily=cp;

	this.isPopUpBusy=false;
	this.isINLINEBusy=false;
	myTransducer = transducer;

	print("NUMBER MINE IS"+this.number);
	
	conveyor0=ConveyorState.Need_Run;
	
	myTransducer.register(this, TChannel.ALL_GUI);

	myTransducer.register(this, TChannel.CUTTER);
	myTransducer.register(this, TChannel.SENSOR);
	myTransducer.register(this, TChannel.ALL_AGENTS);
	myTransducer.register(this, TChannel.CONVEYOR);

	Object[] conveyornumber={this.number};
	isConveyorRunning=true;
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











	@Override
	public boolean pickAndExecuteAnAction() {


		while(conveyor0==ConveyorState.Need_Run)
		{
			print("Starting the conveyor");
			startconveyor0();
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

				 if(mg.status == GlassStatusConveyor.ONEXITSENSOR && isINLINEBusy){
					 inlinebusy(mg);
					 return true;
				 }
			 }
		 }; 


		 synchronized(glassonconveyor){

			 for(MyCGlass mg:glassonconveyor){

				 if(mg.status == GlassStatusConveyor.INLINEBUSY && !isINLINEBusy && conveyor0!=ConveyorState.Jammed){
					 {starttheconveyor(mg);
					 return true;
					 }			
				 }
			 }
		 };

		 synchronized(glassonconveyor){

			 for(MyCGlass mg:glassonconveyor){

				 if(mg.status == GlassStatusConveyor.ONEXITSENSOR && !isINLINEBusy){
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


/*
	private void unbreakConveyor(int i) {
		Object [] no={i};
		if(i==0)
		{
			myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,no);
			conveyor0=ConveyorState.Running;
		}
		else
		{
			myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,no);
		
		}
    	stateChanged();
	}
*/



	private void inlinebusy(MyCGlass mg) {

		Object[] cno={this.number};
		 myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_STOP,cno);
		 mg.status=GlassStatusConveyor.INLINEBUSY;
		 conveyor0=ConveyorState.Stopped;
		 stateChanged();
	}




/*

	private void breakConveyor(int i) {

		Object [] no={i};
		if(i==0)
		{
			myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_STOP,no);
			conveyor0=ConveyorState.Jammed;
		}
		else
		{
			myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_STOP,no);
		
		}
    	stateChanged();
    	
	}*/






	private void starttheconveyor(MyCGlass mg) {
		print("start conveyor 0");
		Object [] no={this.getNumber()};
    	myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,no);
    	mg.status=GlassStatusConveyor.ONEXITSENSOR;
    	conveyor0=ConveyorState.Running;
    	stateChanged();
	}


	private void startconveyor0() {
		Object [] no={this.getNumber()};
    	myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,no);
    	conveyor0=ConveyorState.Running;
    	stateChanged();
	}




	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {


		if(channel == TChannel.SENSOR)
		{
			if(event == TEvent.SENSOR_GUI_PRESSED)
			{

				if((Integer)args[0]==0)
				{
					onentrysensor((Integer)args[1]);
			    };    	
			}


			if(event == TEvent.SENSOR_GUI_RELEASED)
			{
				if((Integer)args[0]==0)
				{
					this.PREVIOUSFamily.msgIAmFree();
				}    	
			}

			if(event == TEvent.SENSOR_GUI_PRESSED)
			{
				if((Integer)args[0]==1)
				{
					onexitsensor((Integer)args[1]);
			    };    	
			}
			stateChanged();
			return;
		}
		
		if( (channel == TChannel.CONVEYOR) && ( (Integer) (args[0]) == this.number) ) {
			if(event == TEvent.CONVEYOR_BROKEN) {
				
				print("BREAKKK");
				conveyor0 = ConveyorState.Jammed;
				print("");
				stateChanged();		
				return;
				
			} else if (event == TEvent.CONVEYOR_FIXED) {
			
				print("NEED RUN RECIEVED");
				conveyor0 = ConveyorState.Need_Run;
				stateChanged();		
				return;
			}
		}
		
		

	}



	// MESSAGES




	public void msgHereIsGlass(Glass g1) {
		print("Glass Recieved. Conveyor Start"+this.number);
		MyCGlass mcg = new MyCGlass(g1);
		mcg.NeedsProcessing = mcg.pcglass.getRecipe(TChannel.CUTTER);
		glassonconveyor.add(mcg);
		print("glass number "+g1.getNumber());
		stateChanged();
	}

	public void msgIamFree() {

		isPopUpBusy=false;
		isINLINEBusy=false;
		stateChanged();
	}



	// ACTIONS

	private void checktheglass(MyCGlass mg) {

		print("Checking the functionalities of glass"+mg.pcglass.getRecipe(TChannel.CUTTER));
		mg.NeedsProcessing=mg.pcglass.getRecipe(TChannel.CUTTER);
		mg.status=GlassStatusConveyor.CHECKED;
		stateChanged();
	}

	private void onentrysensor(Integer args) {

		synchronized(glassonconveyor){
			for(MyCGlass mg:glassonconveyor){

				if(mg.pcglass.getNumber() == args){
					{
						mg.status=GlassStatusConveyor.ONENTRYSENSOR;
						stateChanged();
						return;
					}
				}
			}
			}

	}


	private void onexitsensor(Integer args) {

		synchronized(glassonconveyor){
			for(MyCGlass mg:glassonconveyor){

				if(mg.pcglass.getNumber() == args){
					{
						isConveyorRunning=true;
						mg.status=GlassStatusConveyor.ONEXITSENSOR;
						stateChanged();
						return;
					}
				}
			}
			}

	}



	private void PassingGlassToInLineMachine(MyCGlass mg) {

			this.myinline.msgGlassNeedsProcessing(mg.pcglass,mg.NeedsProcessing);
			print("Glass passed to inline machine");
			mg.status=GlassStatusConveyor.FIRSTDONE;	
			isINLINEBusy=true;
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
		return this.isINLINEBusy;
	}



	@Override
	public void setOperator(Operator_PJ o1) {
		//

	}


	@Override
	public void setisINLINEBusy(Boolean s) {
		// 
		isINLINEBusy=s;
		stateChanged();
	}



	@Override
	public void msgOperatorIsfree(Operator_PJ operatorAgent) {
		// 

	}






	



	public void msgIsNextConveyorFamilyBusy() {
		// TODO Auto-generated method stub
		isNextConveyorFamilyBusy=false;
		stateChanged();
	}











}