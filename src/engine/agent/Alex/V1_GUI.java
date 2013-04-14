package engine.agent.Alex;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.JWindow;


import shared.Barcode;

public class V1_GUI extends JPanel implements ActionListener{
	JButton allRecipe, noneRecipe, some1Recipe, some2Recipe, some3Recipe;
	private BinAgent binAgent;
	JTextPane textPane;
	String textOnPane;
	private static Barcode ALL_RECIPE = new Barcode(0x3FF);
	private static Barcode NONE_RECIPE = new Barcode(0);
	private static Barcode SOME_1_RECIPE = new Barcode(0x3F);
	private static Barcode SOME_2_RECIPE = new Barcode(0x3E0);
	private static Barcode SOME_3_RECIPE = new Barcode(0x2AA);
	
	public V1_GUI(){
		super();
		//init window
		setSize(400, 600);
	//	getContentPane().setLayout( new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		
		//init buttons
		allRecipe = new JButton("Recipe: All Processes");
		noneRecipe = new JButton("Recipe: No Processes");
		some1Recipe = new JButton("Recipe: First 6 Processes");
		some2Recipe = new JButton("Recipe: Last 4 Processes");
		some3Recipe = new JButton("Recipe: Select few process");
		
		//textPane
		textPane = new JTextPane();
		textOnPane = new String();
		
		//init top and bottom jframe
		JPanel top_section = new JPanel();
		top_section.setLayout(new BoxLayout(top_section, BoxLayout.Y_AXIS));
		//register
		allRecipe.addActionListener(this);
		noneRecipe.addActionListener(this);
		some2Recipe.addActionListener(this);
		some3Recipe.addActionListener(this);
		some1Recipe.addActionListener(this);
		
		//addd buttons to top frame
		top_section.add(allRecipe);
		top_section.add(noneRecipe);
		top_section.add(some1Recipe);
		top_section.add(some2Recipe);
		top_section.add(some3Recipe);
		
		JPanel bottom_section = new JPanel();
		bottom_section.setLayout(new BoxLayout(bottom_section, BoxLayout.Y_AXIS));
		bottom_section.add(textPane);
		
		this.add(top_section);
		this.add(bottom_section);
		this.setVisible(true);
	}
	public void setBinAgent(BinAgent binAgent){
		this.binAgent = binAgent;
	}
	public void warnWaitingForCFFree() {
		// TODO Auto-generated method stub
		textOnPane = textOnPane + "Waiting for nc cutter to free to create glass!\n";
		textPane.setText(textOnPane);
	}

	public void warnCreatingGlass() {
		// TODO Auto-generated method stub
		textOnPane = textOnPane + "Bin is creating glass!\n";
		textPane.setText(textOnPane);
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == allRecipe){
			binAgent.msgCreateGlass(ALL_RECIPE );
		}
		if (e.getSource() == noneRecipe){
			binAgent.msgCreateGlass(NONE_RECIPE);
				}
		if (e.getSource() == some1Recipe){
			binAgent.msgCreateGlass(SOME_1_RECIPE);
		}
		if (e.getSource() == some2Recipe){
			binAgent.msgCreateGlass(SOME_2_RECIPE);
		}
		if (e.getSource() == some3Recipe){
			binAgent.msgCreateGlass(SOME_3_RECIPE);
		}

	}

}
