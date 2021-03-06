package util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.slashie.libjcsi.wswing.StrokeInformer;
import net.slashie.libjcsi.wswing.WSwingConsoleInterface;

@SuppressWarnings("serial")
public class JTextAreaWithListener extends JTextArea implements KeyListener{
	private WSwingConsoleInterface j;
	Robot robot = null;
	int[] mousePosition1 = {30, 55};
	int[] mousePosition2 = {50, 55};
	boolean useMousePosition2 = false;

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (robot == null) {
			try {
				robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
		int lengthBefore = main.Main.messageLabel.getText().length();
		int lengthAfter = lengthBefore;
		StrokeInformer strokeInformer = new StrokeInformer();
		int code = strokeInformer.charCode(arg0);
		try {
			if (main.Main.unequipPressed) {
				main.Main.unequipItemAction(code);
				main.Main.unequipPressed = false;
			}
			else if (main.Main.spellsPressed) {
				main.Main.spellAction(code);
				main.Main.spellsPressed = false;
			}
			else if (main.Main.throwPressed) {
				main.Main.throwAction(code);
				main.Main.throwPressed = false;
			}
			else {
				main.Main.makeMovement(code);
				lengthAfter = main.Main.messageLabel.getText().length();
				if (lengthAfter == lengthBefore && !main.Main.isTwoKeysInput(code)) {
					j.getTargetFrame().requestFocus();
				} else {
					int[] positionToUse = mousePosition1;
					if (!useMousePosition2) {
						positionToUse = mousePosition2;
					}
					robot.mouseMove(positionToUse[0], positionToUse[1]);
					useMousePosition2 = !useMousePosition2;
				}
			}
		} catch (JsonIOException | JsonSyntaxException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if (main.Main.isInputType(main.Main.unequipItemInput, strokeInformer.charCode(arg0))) {
			main.Main.unequipPressed = true;
		} else if (main.Main.isInputType(main.Main.spellInput, strokeInformer.charCode(arg0))) {
			main.Main.spellsPressed = true;
		} else if (main.Main.isInputType(main.Main.throwItemInput, strokeInformer.charCode(arg0))) {
			main.Main.throwPressed = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	public JTextAreaWithListener(WSwingConsoleInterface j) {
		this.j = j;
		addKeyListener(this);
	}

}
