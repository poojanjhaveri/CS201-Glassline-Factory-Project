package engine.agent.Dongyoung;

import java.util.HashMap;

import shared.Glass;
import transducer.TChannel;

public class DY_Glass {
	private Glass glass;
	private HashMap<TChannel, Boolean> passStatus = new HashMap<TChannel, Boolean>();	
	
	public DY_Glass(Glass glass){
		this.glass = glass;
		passStatus.put(TChannel.PAINTER, false);
		passStatus.put(TChannel.UV_LAMP, false);
		passStatus.put(TChannel.OVEN, false);
	}
	
	public Boolean getNeedWork(TChannel channel){
		if( glass.getRecipe(channel) ){
			return true;
		}
		return false;
	}
	
	public Glass getGlass(){
		return glass;
	}
	
	public void setPass(TChannel channel){
		passStatus.put(channel, true);
	}
	
	public boolean getPass(TChannel channel){
		return passStatus.get(channel);
	}
}
