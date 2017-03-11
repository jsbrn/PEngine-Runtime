package com.bitbucket.computerology.gui.elements;

import com.bitbucket.computerology.gui.GameScreen;
import com.bitbucket.computerology.sceneobjects.Dialogue;
import com.bitbucket.computerology.misc.MiscMath;
import com.bitbucket.computerology.world.Camera;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class ResponseMenu extends GUIElement {
    
    Image[] border = new Image[9];
    Dialogue d = null;
    boolean chosen = false;
    int selected_index = 0;
    float alpha = 0;
    boolean hide = true;
    
    public ResponseMenu(Dialogue d) {
        this.hide = true;
        this.d = d;
        this.X = GameScreen.PLAYER.getRenderCoords()[0];
        this.Y = GameScreen.PLAYER.getRenderCoords()[1];
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
            Logger.getLogger(ResponseMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getSelected() { return selected_index; }
    
    public void moveCursor(int amount) {
        if (d == null) return;
        selected_index += amount;
        if (selected_index < 0) { selected_index = d.PLAYER_RESPONSES.size()-1; }
        if (selected_index >= d.PLAYER_RESPONSES.size()) selected_index = 0;
    }
    
    public void selectOption(int i) {
        if (d == null || hide) return;
        d.chooseOption(i);
        chosen = true;
    }
    
    public void update() {
        
    }
    
    public void setDialogue(Dialogue d) {
        this.d = d;
        show();
    }
    
    public void hide() {
        hide = true;
    }
    
    public boolean visible() {
        return !hide;
    }
    
    public void show() {
        selected_index = 0;
        hide = false;
    }
    
    public void draw(Graphics g) {
        
        if (d == null) return; //don't do anything if d is null :)
        
        int width = 0, height = 20;
        int longest_width = 0;
        for (String text: d.PLAYER_RESPONSES) {
            height+=GameScreen.DYNAMIC_FONT.getHeight(text)+(int)(1*Camera.ZOOM);
            if (longest_width < GameScreen.DYNAMIC_FONT.getWidth(text)+(int)(3*Camera.ZOOM)) {
                longest_width = GameScreen.DYNAMIC_FONT.getWidth(text)+(int)(3*Camera.ZOOM);
            }
        }
        width = longest_width;
        X+=MiscMath.getConstant(GameScreen.PLAYER.getRenderCoords()[0]-X, 0.15);
        Y+=MiscMath.getConstant(GameScreen.PLAYER.getRenderCoords()[1]-height-(12*Camera.ZOOM)-Y, 0.15);
        
        if (!hide) { alpha+=MiscMath.getConstant(2, 1); } else { alpha-=MiscMath.getConstant(2, 1); }
        if (alpha <= 0) alpha = 0;
        if (alpha >= 1) alpha = 1;
        if (alpha <= 0) return;
        
        g.setColor(new Color(255, 255, 255, (float)alpha));
        g.fillRect((int)(X+(2*Camera.ZOOM)), (int)(Y+(2*Camera.ZOOM)), width, height);
        
        for (Image i: border) {
            i.setAlpha((float)alpha);
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
        int str_h = 0;
        for (int i = 0; i != d.PLAYER_RESPONSES.size(); i++) {
            String text = d.PLAYER_RESPONSES.get(i);
            if (i == selected_index) {
                g.setColor(Color.blue.brighter());
                g.fillRect((int)(X+(2*Camera.ZOOM)), (int)(Y+(2*Camera.ZOOM))+str_h, width, GameScreen.DYNAMIC_FONT.getHeight(text));
                g.setColor(Color.white);
            } else {
                g.setColor(Color.black);
            }
            g.drawString(text, (int)(X+2+(3*Camera.ZOOM)), (int)(Y+2+(2*Camera.ZOOM)+str_h));
            str_h+=GameScreen.DYNAMIC_FONT.getHeight(text);
        }
        
    }
    
}
