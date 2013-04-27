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

	public enum GlassStatusConveyor{NEW,DONE,ONENTRYSENSOR,CHECKED, ONEXITSENSOR, NEEDSMACHINEPROCESSING, NOMACHINEPROCESSING, CHECKINGPROCESSING, FIRSTDONE, INLINEBUSY, ONLASTSENSOR, ONLASTSENSORSTOP, ONTHIRDSENSOR, THIRDSENSORDONE, CHECKEDDONE, ONENTRYSENSORSTOP};

	private Boolean isPopUpBusy;
//	private Boolean isINLINEBusy;
	private Boolean isConveyorRunning;
	
	public boolean isNextConveyorFamilyBusy;
	public boolean frontsensor=false; //newGlass
	public boolean nextsensor=false;
	public boolean finished=true;

	

	private enum ConveyorState{Running,Stopped,Jammed,Need_Fix, Need_Run, Need_Break};
	private enum SensorState{Pressed, Released,None};

	ConveyorState conveyor1;

	private List<MyCGlass> glassonconveyor = Collections.synchronizedList(new ArrayList<MyCGlass>());



	public HalfConveyorAgent(String string,int number, ConveyorFamily c1, Transducer transducer,Popup_PJ p1,InLineMachine_PJ p2,ConveyorFamily cp) {
		
	this.name=string;
	this.number=1;
	this.MyFamily=c1;
	this.myinline=p2;
	this.PREVIOUSFamily=cp;

	this.isPopUpBusy=false;
	myTransducer = transducer;


	
	
	conveyor1=ConveyorState.Need_Run;

	myTransducer.register(this, TChannel.CUTTER);
	myTransducer.register(this, TChannel.SENSOR);
	myTransducer.register(this, TChannel.ALL_AGENTS);
	myTransducer.register(this, TChannel.CONVEYOR);
	
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
			
			print("NEXT CONVEYOR FAMILY -> FREE");
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
			return false;
		}

		
		
		
		
		if(frontsensor)
		{
			print("HEY I am stil heree");
			Cantheconveyorstart();
			return true;
		}
		
		
				
				
		if( nextsensor ){
			glassonsecondsensor();
			return true;
		}
		
		
		synchronized(glassonconveyor){
			for(MyCGlass mg:glassonconveyor){

				if(mg.status == GlassStatusConveyor.DONE){
				glassonconveyor.remove(mg);
				return true;
			    }
			}
		};
		

		

		 
		return false;
	}






	private void glassonsecondsensor() {
		
		Object[] cno={this.number};
		myTransducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, cno );
		print("STOPPED");
		if( !isNextConveyorFamilyBusy ){
			isNextConveyorFamilyBusy = false;
			this.NEXTFamily.msgHereIsGlass( glassonconveyor.get(0).pcglass);
			glassonconveyor.get(0).status=GlassStatusConveyor.DONE;
			myTransducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, cno );
			print("RUNNING");
			finished = true;
		}
		else{
			finished = false;
		}
		nextsensor = false;
	//	stateChanged();
	}


	private void Cantheconveyorstart() {
		
			Object[] cno={this.number};
			myTransducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP,cno);
			print("STOPPED");
			frontsensor = false;
			
			if( ( frontsensor || !glassonconveyor.isEmpty() ) && !nextsensor && finished ){
				myTransducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, cno );
				print("RUNNING");
			}
			else{
				myTransducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, cno );
				print("STOPPED");
			}
	}



	
	




	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {


		if(channel == TChannel.SENSOR)
		{
			
			if(event == TEvent.SENSOR_GUI_PRESSED)
			{

				if((Integer)args[0]==2)
				{
					frontsensor=true;
					stateChanged();
			    };    	
			}

			if(event == TEvent.SENSOR_GUI_PRESSED)
			{
				if((Integer)args[0]==3)
				{
					nextsensor=true;
					stateChanged();
			    };    	
			}


			if(event == TEvent.SENSOR_GUI_RELEASED)
			{
				if((Integer)args[0]==2)
				{
					this.myinline.msgIamFreeForGlass();
					stateChanged();
				}

			}
			
			if(event == TEvent.SENSOR_GUI_RELEASED)
			{
				if((Integer)args[0]==3)
				{
					isNextConveyorFamilyBusy=true;
					stateChanged();
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
	

	/**
	 * Glass on the entry sensor of half conveyor
	**/
	private void onentrysensor(Integer args) {

		synchronized(glassonconveyor){
			for(MyCGlass mg:glassonconveyor){

				if(mg.pcglass.getNumber() == args){
					{
						print("ON ENTRY SENSOR");
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
						print("ON LAST SENSOR");
						mg.status=GlassStatusConveyor.ONLASTSENSOR;
						stateChanged();
						return;
					}
				}
			}
			}

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
			myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_STOP,no);
			conveyor1=ConveyorState.Jammed;
    	stateChanged();
    	print("Conveyor broken");
    	
	}


	private void unbreakConveyor(int i) {
		Object [] cno={i};
		if(i==1)
		{
			if( ( frontsensor || !glassonconveyor.isEmpty() ) && !nextsensor && finished ){
				myTransducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, cno );
				conveyor1=ConveyorState.Running;
			}
			else{
				myTransducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, cno );
				conveyor1=ConveyorState.Stopped;
			}
		}

		
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