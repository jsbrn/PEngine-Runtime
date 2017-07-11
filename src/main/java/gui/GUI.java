package gui;

import java.awt.Font;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import world.Camera;

public class GUI {
    
    private TrueTypeFont font;
    private LinkedList<GUIElement> elements;
    private int scale;
    private static Image border, w_key, s_key, space_key;
    
    public GUI() {
        this.elements = new LinkedList<GUIElement>();
        this.scale = 1;
        Font awtFont = new Font("Arial", Font.BOLD, 16);
        font = new TrueTypeFont(awtFont, true);
        try {
            border = new Image("images/balloon.png", false, Image.FILTER_NEAREST);
            w_key = new Image("images/keys/w.png", false, Image.FILTER_NEAREST);
            s_key = new Image("images/keys/s.png", false, Image.FILTER_NEAREST);
            space_key = new Image("images/keys/space.png", false, Image.FILTER_NEAREST);
        } catch (SlickException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TrueTypeFont getFont() {
        return font;
    }
    
    public String[] wrap(String text, int width) {
        String[] split = text.trim().split("\\s"); //split on spaces
        String[] result = new String[0];
        LinkedList<String> temp = new LinkedList<String>(); temp.add("");
        int line_width = 0;
        for (String word: split) {
            int word_width = font.getWidth(word+" ");
            line_width += word_width;
            if (line_width <= width) { 
                temp.set(temp.size()-1, temp.getLast()+word+" ");
            } else { temp.add(word+" "); line_width = word_width; }
        }
        return temp.toArray(result);
    }
    
    public int getScale() { return (int)Camera.getZoom(); }
    public void setScale(int scale) { this.scale = scale; }
    
    public void update() {
        for (int i = elements.size()-1; i > -1; i--) elements.get(i).update();
    }
    
    public void addElement(GUIElement e) {
        elements.add(e);
        e.setParent(this);
    }
    
    public void removeElement(GUIElement e) {
        elements.remove(e);
        e.setParent(null);
    }
    
    public void draw(Graphics g) {
        for (int i = elements.size()-1; i > -1; i--) elements.get(i).draw(g);
    }
    
    public final boolean handleKeyPress(char c) {
        if (elements.isEmpty()) return false;
        return elements.getLast().handleKeyPress(c);
    }
    
    public static Image getBorder() {
        return border;
    }
    
    public static Image getKeyImage(char c) {
        if (c == 'w') return w_key;
        if (c == 's') return s_key;
        if (c == ' ') return space_key;
        return null;
    }
    
}
