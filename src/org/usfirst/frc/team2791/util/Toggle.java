package org.usfirst.frc.team2791.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2791
 */
public class Toggle {
    private boolean output;
    private boolean firstSwitch = true;
    
    public Toggle(boolean defaultState) { output = defaultState; }
    
    public void giveToggleInput(boolean input) {
        if(input) {
            if(firstSwitch) { //first time the button was hit after being released
                output = !output;
                firstSwitch = false;
            } //otherwise do nothing
        } else { //butotn released
            firstSwitch = true;
        }
        
    }
   public void setManual(boolean newOutput) { output = newOutput; }
   public boolean getToggleOutput() { return output; }
   public boolean get() { return getToggleOutput(); } 
    
}