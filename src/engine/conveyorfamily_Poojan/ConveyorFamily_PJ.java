package engine.conveyorfamily_Poojan;

import engine.conveyorfamily.Interfaces_Poojan.ConveyorFamilyInterface;
import engine.conveyorfamily.Interfaces_Poojan.Operator_PJ;
import engine.conveyorfamily.Interfaces_Poojan.TransducerInterface_PJ;
import shared.Glass;
import shared.enums.SensorPosition;
import shared.enums.SharedData.SensorType;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class ConveyorFamily_PJ implements ConveyorFamilyInterface
{
	private int ConveryorFamilyNo;
	private ConveyorAgent_PJ conveyor;
	private PopupAgent_PJ popup;
	private InLineMachineAgent_PJ inline;
	public ConveyorFamilyInterface nextConveyorFamily;
	public ConveyorFamilyInterface previousConveyorFamily;
	
	public boolean isNextConveyorFamilyBusy;
	
	
	private Transducer transducer;
	
	public ConveyorFamily_PJ(int number, Transducer transducer2)
	{
		this.ConveryorFamilyNo=number;
		this.transducer=transducer2;
	//	this.entrysensor = new SensorAgent("Entry Sensor",1,SensorPosition.START,transducer);
	//	this.exitsensor = new SensorAgent("Exit Sensor",2,SensorPosition.END,transducer);
		this.popup = new PopupAgent_PJ("MyPopup",number,this,transducer);
		this.inline = new InLineMachineAgent_PJ("MyInline",number,this,transducer);
		this.inline.setConveyor(conveyor);
		
		this.popup.startThread();
		this.inline.startThread();
		this.conveyor = new ConveyorAgent_PJ("MyConveyor",number,this,transducer, popup, inline);
		this.popup.setConveyor(conveyor);
		this.conveyor.startThread();
		 isNextConveyorFamilyBusy=true;
	}

	
	public void msgSensorPressed(SensorType type) {
		// TODO Auto-generated method stub
		
	}

	
	public void msgSensorReleased(SensorType type) {
		// TODO Auto-generated method stub
		
	}

	
	public void msgHereIsGlass(Glass glass) {
		// TODO Auto-generated method stub
		this.conveyor.msgHereIsGlass(glass);
		
	}

	public void setNextConveyorFamily(ConveyorFamilyInterface c3)
	{
		nextConveyorFamily=c3;
		
	}
	
	public ConveyorFamilyInterface getNextConveyorFamily()
	{
		return nextConveyorFamily;
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	public boolean getStatusOfNextConveyorFamily() {
		// TODO Auto-generated method stub
		return isNextConveyorFamilyBusy;
	}
	
	
	public void setStatusOfNextConveyorFamily(Boolean y) {
		// TODO Auto-generated method stub
		isNextConveyorFamilyBusy=y;
	}
	
	

	@Override
	public void msgIamFree() {
		 
		System.out.println("I am free recieved from next conveyor family");
		isNextConveyorFamilyBusy=false;
	//	 transducer.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, null);
		// TODO Auto-generated method stub
		
	}


	@Override
	public void msgIHaveFinishedGlass(Operator_PJ o) {
		// TODO Auto-generated method stub
		System.out.println("operator has finished processing Glass");
		
		this.popup.msgOperatorHasFinishedGlass(o);
		
	}


	@Override
	public void msgHereIsFinishedGlass(Glass g, Operator_PJ operatorAgent) {
		// TODO Auto-generated method stub
		System.out.println("Sending it back to popup");
		this.popup.msgHereIsFinishedGlass(g,operatorAgent);
	}

	
	public ConveyorAgent_PJ getConveyor()
	{
		return this.conveyor;
	}

	
	
}