package engine.conveyorfamily_Poojan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import shared.Glass;
import shared.enums.SharedData.WorkType;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.agent.Agent;

import engine.conveyorfamily.Interfaces_Poojan.Conveyor_PJ;
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
	private ConveyorFamily MyFamily;
	public ConveyorFamily NEXTFamily;
	private ConveyorFamily PREVIOUSFamily;

	private InLineMachine_PJ myinline;

	private enum GlassStatusConveyor{NEW,DONE,ONENTRYSENSOR,CHECKED, ONEXITSENSOR, NEEDSMACHINEPROCESSING, NOMACHINEPROCESSING, CHECKINGPROCESSING, FIRSTDONE, INLINEBUSY, ONLASTSENSOR, ONLASTSENSORSTOP, ONTHIRDSENSOR, THIRDSENSORDONE, CHECKEDDONE, ONENTRYSENSORSTOP};

	private Boolean isPopUpBusy;
	private Boolean isConveyorRunning;
	
	private boolean isNextConveyorFamilyBusy;
	 private ConveyorAgent_PJ previousconveyor;
	
	private enum ConveyorState{Running,Stopped,Jammed,Need_Fix, Need_Run, Need_Break};
	private enum SensorState{Pressed, Released,None, NOTHING, PRESSED, RELEASED};

	private ConveyorState conveyor1;
	private SensorState sensor1State;
	private SensorState sensor2State;
	

	private List<MyCGlass> glassonconveyor = Collections.synchronizedList(new ArrayList<MyCGlass>());



	public HalfConveyorAgent(String string,int number, ConveyorFamily c1, Transducer transducer,Popup_PJ p1,InLineMachine_PJ p2,ConveyorFamily cp) {
		
	this.name=string;
	this.number=number;
	
	this.MyFamily=c1;
	this.myinline=p2;
	this.PREVIOUSFamily=cp;

	this.isPopUpBusy=false;
	myTransducer = transducer;

	sensor1State = SensorState.NOTHING;
	sensor2State = SensorState.NOTHING;
	conveyor1 = ConveyorState.Stopped;

	myTransducer.register(this, TChannel.ALL_GUI);
	myTransducer.register(this, TChannel.SENSOR);
	myTransducer.register(this, TChannel.ALL_AGENTS);
	myTransducer.register(this, TChannel.CONVEYOR);
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
			stateChanged();
		}

		
		public void msgIamFree() {
			
			isNextConveyorFamilyBusy=false;
			print("NEXT CONVEYOR FAMILY -> FREE "+ isNextConveyorFamilyBusy);
			
			stateChanged();
			
		}


		public void msgIsNextConveyorFamilyBusy() {
			
			isNextConveyorFamilyBusy=false;
			print("NEXT CONVEYOR FAMILY -> FREE "+ isNextConveyorFamilyBusy);
			stateChanged();
		}






	@Override
	public boolean pickAndExecuteAnAction() {

		
		while(conveyor1==ConveyorState.Need_Run)
		{
			print("Starting the conveyor");
			startconveyor1();
		}
		
		
		
		if( (isNextConveyorFamilyBusy==true) && (sensor2State == SensorState.PRESSED) && (conveyor1==ConveyorState.Running) ) {
			stopConveyor();
			return true;
		}
		
		if( (isNextConveyorFamilyBusy==false) && (conveyor1==ConveyorState.Stopped) ) {
			startconveyor1();
			return true;
		}
		
		if( (sensor2State == SensorState.RELEASED) ) {
			shipttheglass();
			return true;
		}
		
		
		if( sensor1State == SensorState.RELEASED ) {
			notifyPrevious();
			startconveyor1();
			return true;
		}
		

		 
		return false;
	}






	private void stopConveyor() {
		
		Object[] ar={this.number};
		print("MY NUMBER ISSSSSSSSS"+this.number);
		myTransducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, ar);
		conveyor1 = ConveyorState.Stopped;
		
		
	}


	private void unbreakconveyor() {
		
		Integer[] idx = new Integer[1];
		idx[0] = this.number;
		print("MY NUMBER ISSSSSSSSS"+this.number);
		myTransducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, idx);
		conveyor1 = ConveyorState.Running;
	}




	
	




	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {


		
		
		if(channel == TChannel.SENSOR)
		{
			
			if(event == TEvent.SENSOR_GUI_PRESSED)
			{

				if((Integer)args[0]==2)
				{
					this.previousconveyor.msgIamFree();
					sensor1State = SensorState.PRESSED;
			    };    	
			}

			if(event == TEvent.SENSOR_GUI_PRESSED)
			{
				if((Integer)args[0]==3)
				{
					sensor2State = SensorState.PRESSED;
			    };    	
			}


			if(event == TEvent.SENSOR_GUI_RELEASED)
			{
				if((Integer)args[0]==2)
				{
					sensor1State = SensorState.RELEASED;
					
				}

			}
			
			if(event == TEvent.SENSOR_GUI_RELEASED)
			{
				if((Integer)args[0]==3)
				{
					sensor2State = SensorState.RELEASED;
				}

			}
			stateChanged();	
			return;
		}
		
		
		if( (channel == TChannel.CONVEYOR) && ( (Integer) (args[0]) == this.number) ) {
			if(event == TEvent.CONVEYOR_BROKEN) {
				
				conveyor1 = ConveyorState.Jammed;
				stateChanged();		
				return;
				
			} else if (event == TEvent.CONVEYOR_FIXED) {
				conveyor1 = ConveyorState.Need_Run;
				stateChanged();		
				return;
			}
		}
		
	}


	private void shipttheglass() {
		// TODO Auto-generated method stub
		this.NEXTFamily.msgHereIsGlass(glassonconveyor.get(0).pcglass);
		sensor2State = SensorState.NOTHING;
		isNextConveyorFamilyBusy=true;
		glassonconveyor.remove(0);
	}


	private void startconveyor1()
	{
		Object [] no={this.getNumber()};
    	myTransducer.fireEvent(TChannel.CONVEYOR,TEvent.CONVEYOR_DO_START,no);
    	conveyor1=ConveyorState.Running;
	}
	

	private void notifyPrevious() {
		Do("Notifying the previous conveyor family that I'm free");
		this.myinline.msgIamFreeForGlass();
		sensor1State = SensorState.NOTHING;
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


	public void setConveyor(ConveyorAgent_PJ conveyor) {
		// TODO Auto-generated method stub
		this.previousconveyor=conveyor;
	}



/*
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
*/



	


/*
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
*/










}