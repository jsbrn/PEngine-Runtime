package gui.elements;

import gui.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ChoiceBubble extends GUIElement {
    
    private String[] choices;
    private int selected;
    private boolean submitted;
    
    public ChoiceBubble(String[] choices) {
        this.choices = choices;
        this.selected = 0;
        this.submitted = true;
        this.showTail(false);
    }
    
    @Override
    public int[] dims() {
        return new int[]{300, 42+(choices.length+2)*20};
    }
    
    private void down() { selected = selected + 1 >= choices.length ? selected : selected + 1; }
    private void up() { selected = selected - 1 < 0 ? selected : selected - 1; }
    private void submit() {
        submitted = true;
        setFade(GUIElement.FADE_OUT);
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public int getChoice() {
        return selected;
    }
    
    @Override
    public void draw(Graphics g) {
        super.draw(g);
        int[] osc = osc();
        g.setColor(new Color(0.5f, 0.5f, 0.5f, getAlpha())); //gray
        g.setFont(getParent().getFont());
        g.drawString("Choose an option:", osc[0]+10, osc[1]+10);
        for (int c = 0; c < choices.length; c++) {
            String text = choices[c];
            boolean s = selected == c;
            Color bg = new Color(s ? 10 : 255, s ? 140 : 255, s ? 250 : 255, (int)(getAlpha()*255)); //integers
            Color txt = new Color(s ? 1 : 0, s ? 1 : 0, s ? 1 : 0, getAlpha()); //floats
            g.setColor(bg);
            g.fillRect(osc[0], osc[1]+10+((c+1)*20), dims()[0], 20);
            g.setColor(txt);
            g.drawString(text, osc[0]+10, osc[1]+10 +((c+1)*20));
        }
        Image img = GUI.getKeyImage(' '); img.setAlpha(getAlpha());
        img.draw(osc[0] + dims()[0] - 96 - 10, osc[1] + (choices.length+2)*20);
        img = GUI.getKeyImage('s'); img.setAlpha(getAlpha());
        img.draw(osc[0] + dims()[0] - 96 - 15 - 32, osc[1] + (choices.length+2)*20);
        img = GUI.getKeyImage('w'); img.setAlpha(getAlpha());
        img.draw(osc[0] + dims()[0] - 96 - 20 - 64, osc[1] + (choices.length+2)*20);
    }

    @Override
    public boolean handleKeyPress(char c) {
        if (c == 'w') { up(); return true; }
        if (c == 's') { down(); return true; }
        if (c == ' ') { submit(); return true; }
        return false;
    }
   
}