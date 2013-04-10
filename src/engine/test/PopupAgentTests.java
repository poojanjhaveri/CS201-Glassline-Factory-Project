package engine.test;

import shared.Glass;
import transducer.*;
import junit.framework.TestCase;
import engine.agent.*;
import engine.test.mock.*;

public class PopupAgentTests extends TestCase {
	
	/*
	 * Most actions are implemented using multi-step action. Thus, Pop-up Sensor and message reception are often tested together.
	 * Unit Testing isn't a running thread. To make sure tests are ablt to run, all Semaphores are released first, and then acquired.
	 */

	public void testMsgIHaveGlassAndPopupSensor() {
		
		/*
		 * This test checks if msgIHaveGlass works properly. If it does, it should accept the glass from the conveyor.
		 */
		PopupAgent_Y popup = new PopupAgent_Y("Popup",1);
		Transducer transducer = new Transducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		popup.setTransducer(transducer);
		transducer.startTransducer();
		
		assertTrue("Popup should have an empty list of glass. Instead, the size of the glass list is " + popup.getGlassListSize(),
				popup.getGlassListSize() == 0);
		
		MockConveyor conveyor = new MockConveyor("Conveyor");
		MockOperator operator1 = new MockOperator("Operator1");
		MockOperator operator2 = new MockOperator("Operator2");
		
		popup.setConveyor(conveyor);
		popup.setOperator(0, operator1, TChannel.GRINDER, 2);
		popup.setOperator(1, operator2, TChannel.GRINDER, 3);
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		popup.msgIHaveGlass(glass);
		assertTrue("Popup should have a glass registered. Instead, the size of the glass list is " + popup.getGlassListSize(),
				popup.getGlassListSize() == 1);
		
		Integer[] idx = new Integer[1];
		idx[0] = 1;
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, idx);
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		popup.pickAndExecuteAnAction();
		
		assertTrue("MockConveyor should have received message msgPopupFree. ", conveyor.log.containsString("msgPopupFree"));
		
		assertTrue("MockConveyor should have received message msgPopupBusy. ", conveyor.log.containsString("msgPopupBusy"));
		
	}
	
	public void testMsgHereIsGlassAndPopupSensorAndOperator() {
		/*
		 * This test checks if msgIHaveGlass works properly. Scheduler runs twice in this test, and popup will give 
		 * the glass to an operator.
		 */
		
		PopupAgent_Y popup = new PopupAgent_Y("Popup",1);
		Transducer transducer = new Transducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		popup.setTransducer(transducer);
		transducer.startTransducer();
		
		assertTrue("Popup should have an empty list of glass. Instead, the size of the glass list is " + popup.getGlassListSize(),
				popup.getGlassListSize() == 0);
		
		MockConveyor conveyor = new MockConveyor("Conveyor");
		MockOperator operator1 = new MockOperator("Operator1");
		MockOperator operator2 = new MockOperator("Operator2");
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockConveyor should have an empty event log. Instead, " + 
				"MockConveyor's event log reads "+conveyor.log.toString(), 0, conveyor.log.size());
		assertEquals("MockOperator1 should have an empty event log. Instead, " + 
				"MockOperator1's event log reads "+operator1.log.toString(), 0, operator1.log.size());
		assertEquals("MockOperator2 should have an empty event log. Instead, " + 
				"MockOperator1's event log reads "+operator2.log.toString(), 0, operator2.log.size());
		
		popup.setConveyor(conveyor);
		popup.setOperator(0, operator1, TChannel.GRINDER, 2);
		popup.setOperator(1, operator2, TChannel.GRINDER, 3);
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		popup.msgIHaveGlass(glass);
		assertTrue("Popup should have a glass registered. Instead, the size of the glass list is " + popup.getGlassListSize(),
				popup.getGlassListSize() == 1);
		
		Integer[] idx = new Integer[1];
		idx[0] = 1;
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, idx);
		popup.pickAndExecuteAnAction();
		
		assertTrue("MockConveyor should have received message msgPopupFree. ", conveyor.log.containsString("msgPopupFree"));
		
		popup.msgHereIsGlass(glass);
		
		assertTrue("MockConveyor should have received message msgPopupBusy. ", conveyor.log.containsString("msgPopupBusy"));
		
		popup.pickAndExecuteAnAction();
		
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, idx);
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		idx[0] = 2;
		transducer.fireEvent(TChannel.GRINDER, TEvent.WORKSTATION_LOAD_FINISHED, idx);
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		popup.pickAndExecuteAnAction();
		
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
		
		assertTrue("MockAnimation should have received event POPUP_DO_MOVE_UP. ", anim.log.containsString("POPUP_DO_MOVE_UP"));
		assertTrue("MockOperator1 should have received message msgHereIsGlass. ", operator1.log.containsString("msgHereIsGlass"));
		
		
	}
	
	public void testMsgIHaveGlassFinished() {
		/*
		 * This test checks if msgIHaveGlassFinished works properly. After previous test finishes and it receives msgIHaveGlassFinished,
		 * it'll tell the operator that it's ready to accept the finished glass.
		 */
		
		PopupAgent_Y popup = new PopupAgent_Y("Popup",1);
		Transducer transducer = new Transducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		popup.setTransducer(transducer);
		transducer.startTransducer();
		
		assertTrue("Popup should have an empty list of glass. Instead, the size of the glass list is " + popup.getGlassListSize(),
				popup.getGlassListSize() == 0);
		
		MockConveyor conveyor = new MockConveyor("Conveyor");
		MockOperator operator1 = new MockOperator("Operator1");
		MockOperator operator2 = new MockOperator("Operator2");
		
		popup.setConveyor(conveyor);
		popup.setOperator(0, operator1, TChannel.GRINDER, 2);
		popup.setOperator(1, operator2, TChannel.GRINDER, 3);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockConveyor should have an empty event log. Instead, " + 
				"MockConveyor's event log reads "+conveyor.log.toString(), 0, conveyor.log.size());
		assertEquals("MockOperator1 should have an empty event log. Instead, " + 
				"MockOperator1's event log reads "+operator1.log.toString(), 0, operator1.log.size());
		assertEquals("MockOperator2 should have an empty event log. Instead, " + 
				"MockOperator1's event log reads "+operator2.log.toString(), 0, operator2.log.size());
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		popup.msgIHaveGlass(glass);
		assertTrue("Popup should have a glass registered. Instead, the size of the glass list is " + popup.getGlassListSize(),
				popup.getGlassListSize() == 1);
		
		Integer[] idx = new Integer[1];
		idx[0] = 1;
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, idx);
		popup.pickAndExecuteAnAction();
		
		assertTrue("MockConveyor should have received message msgPopupFree. ", conveyor.log.containsString("msgPopupFree"));
		
		popup.msgHereIsGlass(glass);
		
		assertTrue("MockConveyor should have received message msgPopupBusy. ", conveyor.log.containsString("msgPopupBusy"));
		
		popup.pickAndExecuteAnAction();
		
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		idx[0] = 2;
		transducer.fireEvent(TChannel.GRINDER, TEvent.WORKSTATION_LOAD_FINISHED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		popup.pickAndExecuteAnAction();
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
		
		assertTrue("MockAnimation should have received event POPUP_DO_MOVE_UP. ", anim.log.containsString("POPUP_DO_MOVE_UP"));
		
		assertTrue("MockOperator1 should have received message msgHereIsGlass. ", operator1.log.containsString("msgHereIsGlass"));
		
		popup.msgIHaveGlassFinished(glass);
		transducer.fireEvent(TChannel.GRINDER, TEvent.WORKSTATION_RELEASE_FINISHED, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		popup.pickAndExecuteAnAction();
		assertTrue("MockOperator1 should have received message msgIAmFree. ", operator1.log.containsString("msgIAmFree"));
		
	}
	
	public void testMsgHereIsFinishedGlass() {
		/*
		 * This test checks if msgHereIsFinishedGlass works properly. After it raises the pop-up to accept the finished glass,
		 * it'll then lower the popup and give it to the next conveyor family (given that it's free).
		 */
		
		PopupAgent_Y popup = new PopupAgent_Y("Popup",1);
		Transducer transducer = new Transducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		popup.setTransducer(transducer);
		transducer.startTransducer();
		
		assertTrue("Popup should have an empty list of glass. Instead, the size of the glass list is " + popup.getGlassListSize(),
				popup.getGlassListSize() == 0);
		
		MockConveyor conveyor = new MockConveyor("Conveyor");
		MockOperator operator1 = new MockOperator("Operator1");
		MockOperator operator2 = new MockOperator("Operator2");
		MockConveyorFamily next = new MockConveyorFamily("Next");
		
		popup.setConveyor(conveyor);
		popup.setOperator(0, operator1, TChannel.GRINDER, 2);
		popup.setOperator(1, operator2, TChannel.GRINDER, 3);
		popup.setNextConveyorFamily(next);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockConveyor should have an empty event log. Instead, " + 
				"MockConveyor's event log reads "+conveyor.log.toString(), 0, conveyor.log.size());
		assertEquals("MockOperator1 should have an empty event log. Instead, " + 
				"MockOperator1's event log reads "+operator1.log.toString(), 0, operator1.log.size());
		assertEquals("MockOperator2 should have an empty event log. Instead, " + 
				"MockOperator1's event log reads "+operator2.log.toString(), 0, operator2.log.size());

		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		popup.msgIHaveGlass(glass);
		assertTrue("Popup should have a glass registered. Instead, the size of the glass list is " + popup.getGlassListSize(),
				popup.getGlassListSize() == 1);
		
		Integer[] idx = new Integer[1];
		idx[0] = 1;
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, idx);
		popup.pickAndExecuteAnAction();
		
		assertTrue("MockConveyor should have received message msgPopupFree. ", conveyor.log.containsString("msgPopupFree"));
		
		popup.msgHereIsGlass(glass);
		
		assertTrue("MockConveyor should have received message msgPopupBusy. ", conveyor.log.containsString("msgPopupBusy"));
		
		popup.pickAndExecuteAnAction();
		
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		idx[0] = 2;
		transducer.fireEvent(TChannel.GRINDER, TEvent.WORKSTATION_LOAD_FINISHED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		popup.pickAndExecuteAnAction();
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
		
		assertTrue("MockAnimation should have received event POPUP_DO_MOVE_UP. ", anim.log.containsString("POPUP_DO_MOVE_UP"));
		
		assertTrue("MockOperator1 should have received message msgHereIsGlass. ", operator1.log.containsString("msgHereIsGlass"));
		
		popup.msgIHaveGlassFinished(glass);
		transducer.fireEvent(TChannel.GRINDER, TEvent.WORKSTATION_RELEASE_FINISHED, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		popup.pickAndExecuteAnAction();
		assertTrue("MockOperator1 should have received message msgIAmFree. ", operator1.log.containsString("msgIAmFree"));
		
		popup.msgHereIsFinishedGlass(glass);
		
		idx[0] = 1;
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, idx);
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		popup.pickAndExecuteAnAction();
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
		
		assertTrue("MockAnimation should have received event POPUP_DO_MOVE_DOWN. ", anim.log.containsString("POPUP_DO_MOVE_DOWN"));
		assertTrue("Next Conveyor Family should have received message msgHereIsGlass ", next.log.containsString("msgHereIsGlass"));
		
	}
	
	public void testMsgIAmFree() {
		/*
		 * This test checks if msgHereIsFinishedGlass works properly. After it raises the pop-up to accept the finished glass,
		 * it'll then lower the popup and give it to the next conveyor family (given that it's free).
		 * This test is also a comprehensive test to make sure pop-up works completely.
		 */
		
		PopupAgent_Y popup = new PopupAgent_Y("Popup",1);
		Transducer transducer = new Transducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		popup.setTransducer(transducer);
		transducer.startTransducer();
		
		assertTrue("Popup should have an empty list of glass. Instead, the size of the glass list is " + popup.getGlassListSize(),
				popup.getGlassListSize() == 0);
		
		MockConveyor conveyor = new MockConveyor("Conveyor");
		MockOperator operator1 = new MockOperator("Operator1");
		MockOperator operator2 = new MockOperator("Operator2");
		MockConveyorFamily next = new MockConveyorFamily("Next");
		
		popup.setConveyor(conveyor);
		popup.setOperator(0, operator1, TChannel.GRINDER, 2);
		popup.setOperator(1, operator2, TChannel.GRINDER, 3);
		popup.setNextConveyorFamily(next);
		
		popup.setNextFree(false);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockConveyor should have an empty event log. Instead, " + 
				"MockConveyor's event log reads "+conveyor.log.toString(), 0, conveyor.log.size());
		assertEquals("MockOperator1 should have an empty event log. Instead, " + 
				"MockOperator1's event log reads "+operator1.log.toString(), 0, operator1.log.size());
		assertEquals("MockOperator2 should have an empty event log. Instead, " + 
				"MockOperator1's event log reads "+operator2.log.toString(), 0, operator2.log.size());

		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		popup.msgIHaveGlass(glass);
		assertTrue("Popup should have a glass registered. Instead, the size of the glass list is " + popup.getGlassListSize(),
				popup.getGlassListSize() == 1);
		
		Integer[] idx = new Integer[1];
		idx[0] = 1;
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, idx);
		popup.pickAndExecuteAnAction();
		
		assertTrue("MockConveyor should have received message msgPopupFree. ", conveyor.log.containsString("msgPopupFree"));
		
		popup.msgHereIsGlass(glass);
		
		assertTrue("MockConveyor should have received message msgPopupBusy. ", conveyor.log.containsString("msgPopupBusy"));
		
		popup.pickAndExecuteAnAction();
		
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		idx[0] = 2;
		transducer.fireEvent(TChannel.GRINDER, TEvent.WORKSTATION_LOAD_FINISHED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		popup.pickAndExecuteAnAction();
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
		
		assertTrue("MockAnimation should have received event POPUP_DO_MOVE_UP. ", anim.log.containsString("POPUP_DO_MOVE_UP"));
		
		assertTrue("MockOperator1 should have received message msgHereIsGlass. ", operator1.log.containsString("msgHereIsGlass"));
		
		popup.msgIHaveGlassFinished(glass);
		transducer.fireEvent(TChannel.GRINDER, TEvent.WORKSTATION_RELEASE_FINISHED, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		popup.pickAndExecuteAnAction();
		assertTrue("MockOperator1 should have received message msgIAmFree. ", operator1.log.containsString("msgIAmFree"));
		
		popup.msgHereIsFinishedGlass(glass);
		
		idx[0] = 1;
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		popup.pickAndExecuteAnAction();
		assertTrue("Next Conveyor Family shouldn't have received message msgHereIsGlass ", ! next.log.containsString("msgHereIsGlass"));
		
		popup.msgIAmFree();
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		popup.pickAndExecuteAnAction();
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
		
		assertTrue("MockAnimation should have received event POPUP_DO_MOVE_DOWN. ", anim.log.containsString("POPUP_DO_MOVE_DOWN"));
		assertTrue("Next Conveyor Family should have received message msgHereIsGlass ", next.log.containsString("msgHereIsGlass"));
	}
	
	public void testComprehensiveRunWith2Glasses() {
		PopupAgent_Y popup = new PopupAgent_Y("Popup",1);
		Transducer transducer = new Transducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		popup.setTransducer(transducer);
		transducer.startTransducer();
		
		assertTrue("Popup should have an empty list of glass. Instead, the size of the glass list is " + popup.getGlassListSize(),
				popup.getGlassListSize() == 0);
		
		MockConveyor conveyor = new MockConveyor("Conveyor");
		MockOperator operator1 = new MockOperator("Operator1");
		MockOperator operator2 = new MockOperator("Operator2");
		MockConveyorFamily next = new MockConveyorFamily("Next");
		
		popup.setConveyor(conveyor);
		popup.setOperator(0, operator1, TChannel.GRINDER, 2);
		popup.setOperator(1, operator2, TChannel.GRINDER, 3);
		popup.setNextConveyorFamily(next);
		
		popup.setNextFree(false);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockConveyor should have an empty event log. Instead, " + 
				"MockConveyor's event log reads "+conveyor.log.toString(), 0, conveyor.log.size());
		assertEquals("MockOperator1 should have an empty event log. Instead, " + 
				"MockOperator1's event log reads "+operator1.log.toString(), 0, operator1.log.size());
		assertEquals("MockOperator2 should have an empty event log. Instead, " + 
				"MockOperator1's event log reads "+operator2.log.toString(), 0, operator2.log.size());

		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		Glass glass2 = new Glass(2);
		boolean[] tempRecipe2 = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe2[i] = false;}
		glass2.setRecipe(tempRecipe2);
		
		popup.msgIHaveGlass(glass);
		popup.msgIHaveGlass(glass2);
		assertTrue("Popup should have two glass registered. Instead, the size of the glass list is " + popup.getGlassListSize(),
				popup.getGlassListSize() == 2);
		
		Integer[] idx = new Integer[1];
		idx[0] = 1;
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, idx);
		popup.pickAndExecuteAnAction();
		
		assertTrue("MockConveyor should have received message msgPopupFree. ", conveyor.log.containsString("msgPopupFree"));
		
		popup.msgHereIsGlass(glass);
		
		assertTrue("MockConveyor should have received message msgPopupBusy. ", conveyor.log.containsString("msgPopupBusy"));
		
		popup.pickAndExecuteAnAction();
		
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		idx[0] = 2;
		transducer.fireEvent(TChannel.GRINDER, TEvent.WORKSTATION_LOAD_FINISHED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		popup.pickAndExecuteAnAction();
		popup.pickAndExecuteAnAction();
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
		
		assertTrue("MockAnimation should have received event POPUP_DO_MOVE_UP. ", anim.log.containsString("POPUP_DO_MOVE_UP"));
		
		assertTrue("MockOperator1 should have received message msgHereIsGlass. ", operator1.log.containsString("msgHereIsGlass"));
		
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, idx);
		popup.pickAndExecuteAnAction();
		
		assertTrue("MockConveyor should have received message msgPopupFree. ", conveyor.log.containsString("msgPopupFree"));
		
		popup.msgHereIsGlass(glass);
		
		assertTrue("MockConveyor should have received message msgPopupBusy. ", conveyor.log.containsString("msgPopupBusy"));
		
		
		
		
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, idx);
		popup.pickAndExecuteAnAction();
		
		assertTrue("MockConveyor should have received message msgPopupFree. ", conveyor.log.containsString("msgPopupFree"));
		
		popup.msgHereIsGlass(glass);
		
		assertTrue("MockConveyor should have received message msgPopupBusy. ", conveyor.log.containsString("msgPopupBusy"));
		
		
		
		
	}
	
}
