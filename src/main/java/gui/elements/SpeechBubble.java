package gui.elements;

import gui.GUI;
import gui.GUIElement;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class SpeechBubble extends GUIElement {
    
    private String text;
    private long delay, creation;
    private boolean wait_for_user;
    
    public SpeechBubble(String text, int delay, boolean wait_for_user) {
        this.text = text;
        this.delay = delay;
        this.wait_for_user = wait_for_user;
        this.creation = System.currentTimeMillis();
        this.showTail(true);
    }

    @Override
    public void update() {
        super.update();
        if (System.currentTimeMillis() > creation+delay && !wait_for_user) setFade(FADE_OUT);
    }
    
    @Override
    public int[] dims() {
        String[] p = getParent().wrap(text, 250);
        int f_w = p.length == 1 ? getParent().getFont().getWidth(text) : 250;
        int f_h = getParent().getFont().getHeight(text)*p.length;
        return new int[]{f_w + 20 < 115 && wait_for_user ? 115 : f_w + 20, f_h + 20 + (wait_for_user ? 42 : 0)};
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        int[] osc = osc();
        g.setColor(new Color(0, 0, 0, getAlpha()));
        g.setFont(getParent().getFont());
        String[] p = getParent().wrap(text, 250);
        for (int i = 0; i < p.length; i++) g.drawString(p[i], osc[0]+10, osc[1]+10
                +(i*getParent().getFont().getHeight(text)));
        if (wait_for_user) {
            Image img = GUI.getKeyImage(' '); img.setAlpha(getAlpha());
            img.draw(osc[0] + dims()[0] - 96 - 10, osc[1] + dims()[1] - 32 - 10);
        }
    }
    
    @Override
    public boolean handleKeyPress(char c) {
        if (c == ' ' && wait_for_user) { setFade(GUIElement.FADE_OUT); return true; }
        return false;
    }
    
}
