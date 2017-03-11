package com.bitbucket.computerology.gui.elements;

import com.bitbucket.computerology.gui.GameScreen;
import com.bitbucket.computerology.misc.MiscMath;
import com.bitbucket.computerology.world.Camera;
import com.bitbucket.computerology.sceneobjects.SceneObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class SpeechBalloon extends GUIElement {
    
    Image[] border = new Image[9];
    
    SceneObject parent;
    boolean waits_for_input = false;
    String text;
    float alpha = 0, lifespan = 0;
    boolean hide = true;
    
    public SpeechBalloon(SceneObject parent, String text) {
        this.parent = parent;
        this.text = text;
        this.X = parent.getRenderCoords()[0];
        this.Y = parent.getRenderCoords()[1];
        this.hide = false;
    }
    
    public void setText(String t) {
        text = t;
        hide = false;
        alpha = 0;
        lifespan = 0;
        X = parent.getRenderCoords()[0];
        Y = parent.getRenderCoords()[1];
    }
    
    public boolean setWaitsForInput(boolean b) {
        if (b) {
            if (GameScreen.CURRENT_SPEECH_BALLOON == null) {
                GameScreen.CURRENT_SPEECH_BALLOON = this;
                waits_for_input = true;
                return true;
            }
        } else {
            if (GameScreen.CURRENT_SPEECH_BALLOON.equals(this)) {
                GameScreen.CURRENT_SPEECH_BALLOON = null;
                waits_for_input = false;
                return true;
            }
        }
        return false;
    }
    
    public void hide() { hide = true; }
    public SceneObject getParent() { return parent; }
    
    public void draw(Graphics g) {
        
        if (text.length() == 0) return;
        
        if (border[0] == null) {
            try {
                border[0] = new Image("bubble_top_left.png", false, Image.FILTER_NEAREST);
                border[1] = new Image("bubble_top.png", true, Image.FILTER_NEAREST);
                border[2] = new Image("bubble_top_right.png", false, Image.FILTER_NEAREST);
                border[3] = new Image("bubble_right.png", false, Image.FILTER_NEAREST);
                border[4] = new Image("bubble_bottom_right.png", false, Image.FILTER_NEAREST);
                border[5] = new Image("bubble_bottom.png", false, Image.FILTER_NEAREST);
                border[6] = new Image("bubble_bottom_left.png", false, Image.FILTER_NEAREST);
                border[7] = new Image("bubble_left.png", false, Image.FILTER_NEAREST);
                border[8] = new Image("bubble_pointer.png", false, Image.FILTER_NEAREST);
            } catch (SlickException ex) {
                Logger.getLogger(SpeechBalloon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        int width = GameScreen.DYNAMIC_FONT.getWidth(text)+(int)(3*Camera.ZOOM), height = GameScreen.DYNAMIC_FONT.getHeight(text)+(int)(1*Camera.ZOOM);
        X+=MiscMath.getConstant(parent.getRenderCoords()[0]-X, 0.15);
        Y+=MiscMath.getConstant(parent.getRenderCoords()[1]-height-(12*Camera.ZOOM)-Y, 0.15);
        
        if (this.equals(GameScreen.CURRENT_SPEECH_BALLOON)) { 
            if (width < (11*Camera.ZOOM)+GameScreen.DYNAMIC_FONT.getWidth("(press ALT)")) {
                width += (11*Camera.ZOOM)+GameScreen.DYNAMIC_FONT.getWidth("(press ALT)") - width;
            }
        }
        
        if (!hide) { alpha+=MiscMath.getConstant(2, 1); } else { alpha-=MiscMath.getConstant(2, 1); }
        if (alpha < 0) { alpha = 0; }
        if (alpha >= 1) alpha = 1;
        if (alpha == 0) return;
        
        if (this.equals(GameScreen.CURRENT_SPEECH_BALLOON) == false) {
            lifespan += MiscMath.getConstant(1, 1);
        }
        if (lifespan > 3) { hide(); }
        
        g.setColor(new Color(255, 255, 255, (float)alpha));
        g.fillRect((int)(X+(2*Camera.ZOOM)), (int)(Y+(2*Camera.ZOOM)), width, height);
        
        for (int i = 0; i != border.length; i++) {
            border[i].setAlpha(alpha);
            //System.out.println("border[i] alpha = "+border[i].getAlpha());
        }
        
        g.drawImage(border[7].getScaledCopy(2, (int)(height/Camera.ZOOM)).getScaledCopy((int)Camera.ZOOM), (int)X, (int)(Y+(2*Camera.ZOOM)));
        g.drawImage(border[3].getScaledCopy(2, (int)(height/Camera.ZOOM)).getScaledCopy((int)Camera.ZOOM), (int)(X+width+(2*Camera.ZOOM)), (int)(Y+(2*Camera.ZOOM)));
        
        g.drawImage(border[0].getScaledCopy((int)Camera.ZOOM), (int)X, (int)Y);
        g.drawImage(border[1].getScaledCopy((int)(width/Camera.ZOOM), 2).getFlippedCopy(false, true).getScaledCopy((int)Camera.ZOOM), 
                (int)(X+(2*Camera.ZOOM)), (int)Y);
        g.drawImage(border[2].getScaledCopy((int)Camera.ZOOM), 
                (int)(X+(Camera.ZOOM)+width), (int)Y);
        
        g.drawImage(border[6].getScaledCopy((int)Camera.ZOOM), (int)X, (int)Y+height+(int)Camera.ZOOM);
        g.drawImage(border[5].getScaledCopy((width/(int)Camera.ZOOM), 2).getScaledCopy((int)Camera.ZOOM), 
                (int)(X+(2*Camera.ZOOM)), (int)Y+height+(int)(2*Camera.ZOOM));
        g.drawImage(border[4].getScaledCopy((int)Camera.ZOOM), 
                (int)(X+(Camera.ZOOM)+width), (int)Y+height+(int)Camera.ZOOM);
        
        g.drawImage(border[8].getScaledCopy((int)Camera.ZOOM), (int)(X+(5*Camera.ZOOM)), (int)(Y+height+(2*Camera.ZOOM)));
        
        g.setColor(Color.black);
        g.setFont(GameScreen.DYNAMIC_FONT);
        g.drawString(text, (int)(X+2+(3*Camera.ZOOM)), (int)(Y+2+(2*Camera.ZOOM)));
        g.setColor(Color.gray);
        if (this.equals(GameScreen.CURRENT_SPEECH_BALLOON)) { g.drawString("(press SPACE)", (int)(X+(11*Camera.ZOOM)), (int)(Y+height+(5*Camera.ZOOM))); }
        
    }
    
    public boolean visible() {
        return !hide;
    }
    
}
