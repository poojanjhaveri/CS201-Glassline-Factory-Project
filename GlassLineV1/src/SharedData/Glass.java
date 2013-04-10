package SharedData;

import java.util.HashMap;

import transducer.TChannel;

import SharedData.SharedEnum.*;

/*
 * Glass Class
 * This class contains its Recipe.
 * 
 * @author Dongyoung Jung
 */
public class Glass {
	// Glass Number. Starts from 1, maybe?
	private int number;

	/* 
	 * You can check by WorkType
	 * True - Necessary, False - Unnecessary
	 */
	private HashMap<TChannel, Boolean> recipe = new HashMap<TChannel, Boolean>();
	
	/*
	 * There are five status.
	 * 
	 * PASS - When glass gets into New Conveyor Family, its default status is set to PASS. Also, if it does not need Machine work, it is not changed.
	 * WORK - Glass needs the Machine Work.
	 * UP - Glass goes to Up-operator.
	 * DOWN - Glass goes to Down-operator.
	 * DONE - Glass is done with Machine Work.
	 */
	private GlassStatus status = GlassStatus.PASS;
	
	/* CONSTRUCTOR */
	public Glass(int number, boolean breakout, boolean manual_breakout, boolean cross_seamer, boolean drill, boolean grinder, boolean cutter, boolean washer, boolean uv_lamp, boolean oven, boolean painter){
		this.number = number;
		recipe.put( TChannel.BREAKOUT, breakout );
		recipe.put( TChannel.MANUAL_BREAKOUT, manual_breakout );
		recipe.put( TChannel.CROSS_SEAMER, cross_seamer );
		recipe.put( TChannel.DRILL, drill );
		recipe.put( TChannel.GRINDER, grinder );
		recipe.put( TChannel.CUTTER, cutter );
		recipe.put( TChannel.WASHER, washer );
		recipe.put( TChannel.UV_LAMP, uv_lamp );
		recipe.put( TChannel.OVEN, oven );
		recipe.put( TChannel.PAINTER, painter);
	}
	
	/*
	 * PASS : Does not need work.
	 * WORK : Need work, but not assigned with an operator.
	 * UP : Need work, and assigned with Up-operator.
	 * DOWN : Need work, and assigned with Down-operator.
	 * DONE : Done with machine work.
	 */
	public void setStatusPass(){ status = GlassStatus.PASS; }
	public void setStatusWork(){ status = GlassStatus.WORK; }
	public void setStatusUp(){ status = GlassStatus.UP; }
	public void setStatusDown(){ status = GlassStatus.DOWN; }
	public void setStatusDone(){ status = GlassStatus.DONE; }

	/* Return Glass Status */
	public GlassStatus getStatus(){  return status;  }
	
	/* Returns the assigned number of glass */
	public int getNumber(){  return number;  }
	
	/*
	 * You can check if the glass needs Machine Work.
	 * If this glass needs the type of Machine Work, it sets the status into 'WORK'
	 * You need this maybe before passing the glass onto Popup Sensor? Maybe?
	 * If it does need the work, it remains as 'PASS' which means that it just passes by and moves to Next Conveyor Family.
	 */
	public void setStatusByWorkType( TChannel type ){  if( recipe.get( type ) == true ) setStatusWork();  }	
}
