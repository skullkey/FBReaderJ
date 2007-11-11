package org.zlibrary.core.application.toolbar;

public class ButtonItem extends Item {
	private int myActionId;
	private String myIconName;
	//private ZLResource myTooltip;
	private	ButtonGroup myButtonGroup;
	
	/*public ButtonItem(int actionId, String iconName, ZLResource tooltip) {
		myActionId = actionId;
		myIconName = iconName;
		//myTooltip = tooltip;
	}*/

	public Type type() {
		return Type.BUTTON;
	}

	public int actionId() {
		return myActionId;
	}
	
	public String iconName() {
		return myIconName;
	}
	
	public String tooltip() {
		//if (!myTooltip.hasValue()) {
			//static const String EMPTY;
			//return EMPTY;
		//}
		return null;//myTooltip.value();
	}

	public ButtonGroup buttonGroup() {
		return myButtonGroup;
	}
	
	public boolean isToggleButton() {
		return myButtonGroup != null;
	}
	
	public void press() {
		if (isToggleButton()) { 
			myButtonGroup.press(this);
		}
	}
	
	public boolean isPressed() {
		return isToggleButton() && (this == myButtonGroup.PressedItem);
	}

	public void setButtonGroup(ButtonGroup bg) {
		if (myButtonGroup != null) {
			//myButtonGroup.Items.erase(this);
		}
		
		myButtonGroup = bg;
		
		if (myButtonGroup != null) {
			//myButtonGroup.Items.insert(this);
		}
	}	
}