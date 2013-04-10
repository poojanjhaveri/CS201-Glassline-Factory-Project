package engine.test;

import shared.*;
import transducer.*;
import engine.agent.*;
import engine.test.mock.*;
import junit.framework.TestCase;

public class InlineAgentTests extends TestCase {
	
	/*
	 * The following 2 tests complete all possibilities of an inline agent. testMsgIAmFree is the complete communication between its conveyor and
	 * the next conveyor family. As an inline processing unit can only accept one glass, it'll ignore any incoming glass when it's doing some 
	 * processing (the thread will be busy.)
	 */
	
	public void testMsgHereIsGlass() {
		/*
		 * This test checks if msgHereIsGlass works properly. If it does, it'll start processing the glass.
		 */
		InlineAgent_Y inline = new InlineAgent_Y(3, "Inline", "WASHER");
		Transducer transducer = new Transducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		transducer.startTransducer();
		
		inline.setChannel(TChannel.WASHER);
		inline.setTransducer(transducer);
		
		MockConveyor conveyor = new MockConveyor("Conveyor");
		MockConveyorFamily next = new MockConveyorFamily("Next");
		inline.setConveyor(conveyor);
		inline.setNextConveyorFamily(next);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		assertEquals("Inline Agent should not have any glass initially. ", inline.getInlineGlass(), null);
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockConveyor should have an empty event log. Instead, " + 
				"MockConveyor's event log reads "+conveyor.log.toString(), 0, conveyor.log.size());
		assertEquals("MockConveyorFamily should have an empty event log. Instead, " + 
				"MockConveyorFamily's event log reads "+next.log.toString(), 0, next.log.size());
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[3] = true;
		glass.setRecipe(tempRecipe);
		
		
		inline.msgHereIsGlass(glass);
		assertTrue("Inline Agent should have a glass now. ", inline.getInlineGlass() != null);
		
		transducer.fireEvent(TChannel.WASHER, TEvent.WORKSTATION_GUI_ACTION_FINISHED, null);
		transducer.fireEvent(TChannel.WASHER, TEvent.WORKSTATION_RELEASE_FINISHED, null);
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		inline.pickAndExecuteAnAction();
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		assertTrue("MockAnimation should have received event WORKSTATION_DO_ACTION. ", anim.log.containsString("WORKSTATION_DO_ACTION"));
		assertTrue("MockAnimation should have received event WORKSTATION_RELEASE_GLASS. ", anim.log.containsString("WORKSTATION_RELEASE_GLASS"));
		assertTrue("MockConveyorFamily should have received message msgHereIsGlass. ", next.log.containsString("msgHereIsGlass"));
		assertTrue("MockConveyor should have received message msgInlineFree. ", conveyor.log.containsString("msgInlineFree"));
		
	}
	
	public void testMsgIAmFree() {
		/*
		 * This test checks if msgIAmFree works properly. Initially nextFree is false. Inline Agent will release the glass after nextFree is set 
		 * to true.
		 */
		InlineAgent_Y inline = new InlineAgent_Y(3, "Inline", "WASHER");
		Transducer transducer = new Transducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		transducer.startTransducer();
		
		inline.setChannel(TChannel.WASHER);
		inline.setTransducer(transducer);
		inline.setNextFree(false);
		
		MockConveyor conveyor = new MockConveyor("Conveyor");
		MockConveyorFamily next = new MockConveyorFamily("Next");
		inline.setConveyor(conveyor);
		inline.setNextConveyorFamily(next);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		assertEquals("Inline Agent should not have any glass initially. ", inline.getInlineGlass(), null);
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockConveyor should have an empty event log. Instead, " + 
				"MockConveyor's event log reads "+conveyor.log.toString(), 0, conveyor.log.size());
		assertEquals("MockConveyorFamily should have an empty event log. Instead, " + 
				"MockConveyorFamily's event log reads "+next.log.toString(), 0, next.log.size());
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[3] = true;
		glass.setRecipe(tempRecipe);
		
		
		inline.msgHereIsGlass(glass);
		assertTrue("Inline Agent should have a glass now. ", inline.getInlineGlass() != null);
		
		transducer.fireEvent(TChannel.WASHER, TEvent.WORKSTATION_GUI_ACTION_FINISHED, null);
		transducer.fireEvent(TChannel.WASHER, TEvent.WORKSTATION_RELEASE_FINISHED, null);
		
		inline.msgIAmFree();
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		inline.pickAndExecuteAnAction();
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		assertTrue("MockAnimation should have received event WORKSTATION_DO_ACTION. ", anim.log.containsString("WORKSTATION_DO_ACTION"));
		assertTrue("MockAnimation should have received event WORKSTATION_RELEASE_GLASS. ", anim.log.containsString("WORKSTATION_RELEASE_GLASS"));
		assertTrue("MockConveyorFamily should have received message msgHereIsGlass. ", next.log.containsString("msgHereIsGlass"));
		assertTrue("MockConveyor should have received message msgInlineFree. ", conveyor.log.containsString("msgInlineFree"));
		
	}
	
	
}
