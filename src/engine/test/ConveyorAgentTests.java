package engine.test;

import junit.framework.TestCase;
import shared.*;
import engine.agent.*;
import engine.agent.ConveyorAgent_Y.Mode;
import engine.test.mock.*;
import transducer.*;

public class ConveyorAgentTests extends TestCase {
	
	/* 
	 * The following tests are comprehensive. They include tests with 1, 2 and 3 glasses but also test basic cases. As Sensors are integrated
	 * in ConveyorAgent, some test involves with testing both message reception and sensors. This will ensure both of them being working.
	 * ConveyorIndex = 1; (ConveyorFamily 2)
	 * PopupIndex = 1;
	 * SensorIndex = 2 & 3.
	 * 
	 * NOTE: Transducer is actually running. In the very rare case that some tests fail, replace all "Thread.sleep(10)" in all tests with
	 * "Thread.sleep(20)" or even bigger numbers. There's race conditions between this test thread and transducer thread.
	 */
	
	
	public void testMsgHereIsGlass() {
	/*
	 *  This test checks if msgHereIsGlass() works properly. Initially conveyor is not running and pop-up is free. After it receives
	 *  a glass and realizes it's not running, it'll start the conveyor. Mock animation is used in this test.
	 */
		ConveyorAgent_Y conveyor = new ConveyorAgent_Y("Conveyor", 1, Mode.OFFLINE);
		Transducer transducer = new Transducer();
		Transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		transducer.startTransducer();
		
		conveyor.setTransducer(transducer);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		assertTrue("Conveyor should have an empty list of glass. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
		assertTrue("Conveyor should not be running at the beginning. Instead, conveyor is running. ", ! conveyor.isRunning());
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		
		conveyor.msgHereIsGlass(glass);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have a glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 1);
		
		assertTrue("Conveyor should be running now. Instead, conveyor is not running. ", conveyor.isRunning());
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue("MockAnimation should have received event CONVEYOR_DO_START. ", anim.log.containsString("CONVEYOR_DO_START"));
	}
	
	public void testMsgPopupBusyAndSensor2Pressed() {
		/*
		 *  This test checks if msgPopupBusy() and Sensor 2 work properly. It goes through all previous test; it'll have a running conveyor at
		 *  that moment. After it realizes that the pop-up gets busy and Sensor 2 is pressed (a collision is imminent if the conveyor
		 *  is not stopped), it'll stop the conveyor.
		 */
		ConveyorAgent_Y conveyor = new ConveyorAgent_Y("Conveyor", 1, Mode.OFFLINE);
		Transducer transducer = new Transducer();
		Transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		transducer.startTransducer();
		
		conveyor.setTransducer(transducer);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		MockPopup popup = new MockPopup("Popup");
		conveyor.setPopup(popup);
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		assertTrue("Conveyor should have an empty list of glass. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
		assertTrue("Conveyor should not be running at the beginning. Instead, conveyor is running. ", ! conveyor.isRunning());
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockPopup should have an empty event log. Instead, " + 
				"MockPopup's event log reads "+popup.log.toString(), 0, popup.log.size());
		
		conveyor.msgHereIsGlass(glass);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have a glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 1);
		
		assertTrue("Conveyor should be running now. Instead, conveyor is not running. ", conveyor.isRunning());
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue("MockAnimation should have received event CONVEYOR_DO_START. ", anim.log.containsString("CONVEYOR_DO_START"));
		
		conveyor.msgPopupBusy();
		Integer[] idx = new Integer[1];
		idx[0] = 3;
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have stopped now. Instead, conveyor is running. ", ! conveyor.isRunning());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("MockAnimation should have received event CONVEYOR_DO_STOP. ", anim.log.containsString("CONVEYOR_DO_STOP"));
		assertTrue("MockPopup should have received message msgIHaveGlass. ", popup.log.containsString("msgIHaveGlass"));
	}
	
	public void testMsgPopupFreeAndSensor2Pressed() {
		/*
		 *  This test checks if msgPopupFree() and Sensor 2 work properly. It goes through all previous test; after pop-up becomes free again,
		 *  ConveyorAgent will restart the conveyor.
		 */
		ConveyorAgent_Y conveyor = new ConveyorAgent_Y("Conveyor", 1, Mode.OFFLINE);
		Transducer transducer = new Transducer();
		Transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		transducer.startTransducer();
		
		conveyor.setTransducer(transducer);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		MockPopup popup = new MockPopup("Popup");
		conveyor.setPopup(popup);
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		assertTrue("Conveyor should have an empty list of glass. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
		assertTrue("Conveyor should not be running at the beginning. Instead, conveyor is running. ", ! conveyor.isRunning());
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockPopup should have an empty event log. Instead, " + 
				"MockPopup's event log reads "+popup.log.toString(), 0, popup.log.size());
		
		conveyor.msgHereIsGlass(glass);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have a glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 1);
		
		assertTrue("Conveyor should be running now. Instead, conveyor is not running. ", conveyor.isRunning());
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue("MockAnimation should have received event CONVEYOR_DO_START. ", anim.log.containsString("CONVEYOR_DO_START"));
		
		conveyor.msgPopupBusy();
		Integer[] idx = new Integer[1];
		idx[0] = 3;
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have stopped now. Instead, conveyor is running. ", ! conveyor.isRunning());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("MockAnimation should have received event CONVEYOR_DO_STOP. ", anim.log.containsString("CONVEYOR_DO_STOP"));
		assertTrue("MockPopup should have received message msgIHaveGlass. ", popup.log.containsString("msgIHaveGlass"));
		
		conveyor.msgPopupFree();
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have restarted now. Instead, conveyor is not running. ", conveyor.isRunning());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("MockAnimation should have received event CONVEYOR_DO_START. ", anim.log.containsString("CONVEYOR_DO_START"));
		
	}
	
	public void testMsgInlineFreeAndSensor2WithTwoGlasses() {
		/*
		 * This test checks if msgInlineFree() and sensor 2 work properly with 2 glasses. First, it will receive a glass and give it to 
		 * the inline machine. It will label the inline as busy now. Then the second glass should stop when it gets to the end of the 
		 * conveyor. After it receives msgInlineFree(), it should restart the conveyor.
		 */
		ConveyorAgent_Y conveyor = new ConveyorAgent_Y("Conveyor", 1, Mode.ONLINE);
		Transducer transducer = new Transducer();
		Transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		transducer.startTransducer();
		
		conveyor.setTransducer(transducer);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		MockInline inline = new MockInline("Inline");
		conveyor.setInline(inline);
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		assertTrue("Conveyor should have an empty list of glass. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
		assertTrue("Conveyor should not be running at the beginning. Instead, conveyor is running. ", ! conveyor.isRunning());
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		
		conveyor.msgHereIsGlass(glass);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have a glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 1);
		
		assertTrue("Conveyor should be running now. Instead, conveyor is not running. ", conveyor.isRunning());
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue("MockAnimation should have received event CONVEYOR_DO_START. ", anim.log.containsString("CONVEYOR_DO_START"));
		Integer[] idx = new Integer[1];
		idx[0] = 3;
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have an empty list of glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
		assertTrue("MockInline should have received message msgHereIsGlass. ", inline.log.containsString("msgHereIsGlass"));
		
		conveyor.msgHereIsGlass(glass);
		
		assertTrue("Conveyor should have a glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 1);
		
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should be stopping now. Instead, conveyor is running. ", ! conveyor.isRunning());
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue("MockAnimation should have received event CONVEYOR_DO_STOP. ", anim.log.containsString("CONVEYOR_DO_STOP"));
		
		conveyor.msgInlineFree();
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should be running now. Instead, conveyor is not running. ", conveyor.isRunning());
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue("MockAnimation should have received event CONVEYOR_DO_START. ", anim.log.containsString("CONVEYOR_DO_START"));

	}
	
	public void testSensor1() {
		/*
		 * This test checks if sensor 1 works. If it does, when a glass gets off of sensor 1 it'll notify the previous conveyor family that
		 * it can accept new glass.
		 */
		ConveyorAgent_Y conveyor = new ConveyorAgent_Y("Conveyor", 1, Mode.ONLINE);
		MockConveyorFamily previous = new MockConveyorFamily("ConveyorFamily");
		conveyor.setPreviousConveyorFamily(previous);
		
		assertEquals("MockConveyorFamily should have an empty event log. Instead, " + 
				"MockConveyorFamily's event log reads "+previous.log.toString(), 0, previous.log.size());
		
		Transducer transducer = new Transducer();
		Transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		transducer.startTransducer();
		
		conveyor.setTransducer(transducer);
		Integer[] idx = new Integer[1];
		idx[0] = 2;
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		conveyor.pickAndExecuteAnAction();
		conveyor.pickAndExecuteAnAction();
		
		
		
		assertTrue("MockConveyorFamily should have received message msgIAmFree. ", previous.log.containsString("msgIAmFree"));
		
	}
	
	public void test2GlassesCase1() {
		/*
		 *  This test checks if the conveyor runs with 2 glasses. Case 1 is that the second glass gets blocked on entry.
		 */
		ConveyorAgent_Y conveyor = new ConveyorAgent_Y("Conveyor", 1, Mode.OFFLINE);
		Transducer transducer = new Transducer();
		Transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		transducer.startTransducer();
		
		conveyor.setTransducer(transducer);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		MockPopup popup = new MockPopup("Popup");
		conveyor.setPopup(popup);
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		assertTrue("Conveyor should have an empty list of glass. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
		assertTrue("Conveyor should not be running at the beginning. Instead, conveyor is running. ", ! conveyor.isRunning());
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockPopup should have an empty event log. Instead, " + 
				"MockPopup's event log reads "+popup.log.toString(), 0, popup.log.size());
		
		conveyor.msgHereIsGlass(glass);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have a glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 1);
		
		assertTrue("Conveyor should be running now. Instead, conveyor is not running. ", conveyor.isRunning());
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Integer[] idx = new Integer[1];
		idx[0] = 3;
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have an empty list of glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
		
		conveyor.msgHereIsGlass(glass);
		
		conveyor.pickAndExecuteAnAction();
		
		
		
		assertTrue("Conveyor should have a glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 1);
		
		assertTrue("Conveyor should be running now. Instead, conveyor is not running. ", conveyor.isRunning());
		
		
		conveyor.msgPopupBusy();
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have stopped now. Instead, conveyor is running. ", ! conveyor.isRunning());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("MockAnimation should have received event CONVEYOR_DO_STOP. ", anim.log.containsString("CONVEYOR_DO_STOP"));
		assertTrue("MockPopup should have received message msgIHaveGlass. ", popup.log.containsString("msgIHaveGlass"));
		
		conveyor.msgPopupFree();
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have restarted now. Instead, conveyor is not running. ", conveyor.isRunning());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("MockAnimation should have received event CONVEYOR_DO_START. ", anim.log.containsString("CONVEYOR_DO_START"));
		
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have an empty list of glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
	}
	
	public void test2GlassesCase2() {
		/*
		 *  This test checks if the conveyor runs with 2 glasses. Case 2 is that the first glass gets blocked on entry.
		 */
		ConveyorAgent_Y conveyor = new ConveyorAgent_Y("Conveyor", 1, Mode.OFFLINE);
		Transducer transducer = new Transducer();
		Transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		transducer.startTransducer();
		
		conveyor.setTransducer(transducer);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		MockPopup popup = new MockPopup("Popup");
		conveyor.setPopup(popup);
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		assertTrue("Conveyor should have an empty list of glass. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
		assertTrue("Conveyor should not be running at the beginning. Instead, conveyor is running. ", ! conveyor.isRunning());
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockPopup should have an empty event log. Instead, " + 
				"MockPopup's event log reads "+popup.log.toString(), 0, popup.log.size());
		
		
		
		conveyor.msgHereIsGlass(glass);
		
		conveyor.pickAndExecuteAnAction();
		
		
		
		assertTrue("Conveyor should have a glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 1);
		
		assertTrue("Conveyor should be running now. Instead, conveyor is not running. ", conveyor.isRunning());
		
		
		conveyor.msgPopupBusy();
		Integer[] idx = new Integer[1];
		idx[0] = 3;
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have stopped now. Instead, conveyor is running. ", ! conveyor.isRunning());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("MockAnimation should have received event CONVEYOR_DO_STOP. ", anim.log.containsString("CONVEYOR_DO_STOP"));
		assertTrue("MockPopup should have received message msgIHaveGlass. ", popup.log.containsString("msgIHaveGlass"));
		
		conveyor.msgPopupFree();
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have restarted now. Instead, conveyor is not running. ", conveyor.isRunning());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("MockAnimation should have received event CONVEYOR_DO_START. ", anim.log.containsString("CONVEYOR_DO_START"));
		
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have an empty list of glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
		
		conveyor.msgHereIsGlass(glass);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have a glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 1);
		
		assertTrue("Conveyor should be running now. Instead, conveyor is not running. ", conveyor.isRunning());
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have an empty list of glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
	}
	
	public void test3GlassesCase1() {
		/*
		 *  This test checks if the conveyor runs with 3 glasses. Case 1 is that the second and third glass gets blocked on entry.
		 */
		ConveyorAgent_Y conveyor = new ConveyorAgent_Y("Conveyor", 1, Mode.OFFLINE);
		Transducer transducer = new Transducer();
		Transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		transducer.startTransducer();
		
		conveyor.setTransducer(transducer);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		MockPopup popup = new MockPopup("Popup");
		conveyor.setPopup(popup);
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		assertTrue("Conveyor should have an empty list of glass. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
		assertTrue("Conveyor should not be running at the beginning. Instead, conveyor is running. ", ! conveyor.isRunning());
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockPopup should have an empty event log. Instead, " + 
				"MockPopup's event log reads "+popup.log.toString(), 0, popup.log.size());
		
		conveyor.msgHereIsGlass(glass);
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have a glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 1);
		
		assertTrue("Conveyor should be running now. Instead, conveyor is not running. ", conveyor.isRunning());
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Integer[] idx = new Integer[1];
		idx[0] = 3;
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have an empty list of glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
		
		conveyor.msgHereIsGlass(glass);
		
		conveyor.pickAndExecuteAnAction();
		
		
		
		assertTrue("Conveyor should have a glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 1);
		
		assertTrue("Conveyor should be running now. Instead, conveyor is not running. ", conveyor.isRunning());
		
		
		conveyor.msgPopupBusy();
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have stopped now. Instead, conveyor is running. ", ! conveyor.isRunning());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("MockAnimation should have received event CONVEYOR_DO_STOP. ", anim.log.containsString("CONVEYOR_DO_STOP"));
		assertTrue("MockPopup should have received message msgIHaveGlass. ", popup.log.containsString("msgIHaveGlass"));
		
		conveyor.msgPopupFree();
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have restarted now. Instead, conveyor is not running. ", conveyor.isRunning());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("MockAnimation should have received event CONVEYOR_DO_START. ", anim.log.containsString("CONVEYOR_DO_START"));
		
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have an empty list of glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
	}
	
	public void test3GlassesCase2() {
		/*
		 *  This test checks if the conveyor runs with 3 glasses. Case 2 is that the first glass gets blocked on entry.
		 */
		ConveyorAgent_Y conveyor = new ConveyorAgent_Y("Conveyor", 1, Mode.OFFLINE);
		Transducer transducer = new Transducer();
		Transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		transducer.startTransducer();
		
		conveyor.setTransducer(transducer);
		
		MockAnimation anim = new MockAnimation("Animation");
		anim.setTransducer(transducer);
		
		MockPopup popup = new MockPopup("Popup");
		conveyor.setPopup(popup);
		
		Glass glass = new Glass(1);
		boolean[] tempRecipe = new boolean[7];
		for(int i=0; i<7; i++) {tempRecipe[i] = false;}
		tempRecipe[1] = true;
		glass.setRecipe(tempRecipe);
		
		assertTrue("Conveyor should have an empty list of glass. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
		assertTrue("Conveyor should not be running at the beginning. Instead, conveyor is running. ", ! conveyor.isRunning());
		assertEquals("MockAnimation should have an empty event log. Instead, " + 
				"MockAnimation's event log reads "+anim.log.toString(), 0, anim.log.size());
		assertEquals("MockPopup should have an empty event log. Instead, " + 
				"MockPopup's event log reads "+popup.log.toString(), 0, popup.log.size());
		
		conveyor.msgHereIsGlass(glass);
		conveyor.msgHereIsGlass(glass);
		conveyor.msgHereIsGlass(glass);
		
		Integer[] idx = new Integer[1];
		idx[0] = 3;
		
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have three glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 3);
		
		assertTrue("Conveyor should be running now. Instead, conveyor is not running. ", conveyor.isRunning());
		
		conveyor.msgPopupBusy();
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, idx);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have stopped now. Instead, conveyor is running. ", ! conveyor.isRunning());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("MockAnimation should have received event CONVEYOR_DO_STOP. ", anim.log.containsString("CONVEYOR_DO_STOP"));
		assertTrue("MockPopup should have received message msgIHaveGlass. ", popup.log.containsString("msgIHaveGlass"));
		
		conveyor.msgPopupFree();
		conveyor.pickAndExecuteAnAction();
		assertTrue("Conveyor should have restarted now. Instead, conveyor is not running. ", conveyor.isRunning());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("MockAnimation should have received event CONVEYOR_DO_START. ", anim.log.containsString("CONVEYOR_DO_START"));
		
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have 2 glasses now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 2);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("Conveyor should have 1 glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 1);
		
		transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, idx);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyor.pickAndExecuteAnAction();
		
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue("Conveyor should have an empty list of glass now. Instead, the size of the glass list is " + conveyor.getGlassListSize(),
				conveyor.getGlassListSize() == 0);
	}
}
