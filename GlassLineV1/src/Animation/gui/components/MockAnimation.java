package Animation.gui.components;

import java.awt.event.ActionEvent;
import java.io.Serializable;

import transducer.TChannel;
import transducer.TEvent;

public class MockAnimation extends GuiComponent implements Serializable{

	public MockAnimation(){
		transducer.register(this, TChannel.SENSOR);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		System.out.println("TChannel : " + channel.toString() + ", TEvent : " + event.toString());
	}
}
