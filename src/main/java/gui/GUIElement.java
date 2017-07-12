package gui;

import misc.MiscMath;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import world.objects.SceneObject;

public class GUIElement {
    
    private static Image border;
    private static SceneObject target;
    private static GUI parent;
    
    public static final int FADE_OUT = -1, FADE_IN = 1;
    
    private int fade = FADE_IN;
    private float alpha = 0, offset = 0;
    private boolean tail = false;
    
    public void update() {
        alpha += MiscMath.getConstant(fade, 0.3); 
        offset += MiscMath.getConstant(fade, 0.3);
        alpha = alpha > 1 ? 1 : (alpha < 0 ? 0 : alpha);
        offset = offset > 1 ? 1 : (offset < 0 ? 0 : offset);
        if (alpha == 0) parent.removeElement(this);
    }
    
    public boolean handleKeyPress(char c) { return false; }
    
    public final void setFade(int fade) { this.fade = fade; }

    public float getAlpha() {
        return alpha;
    }

    public void showTail(boolean tail) {
        this.tail = tail;
    }
    
    public final void setParent(GUI parent) { this.parent = parent; }
    public final GUI getParent() { return parent; }
    public final void setTarget(SceneObject target) { GUIElement.target = target; }
    
    public final int[] osc() {
        if (target == null) return new int[]{0, (int)(offset * 25)};
        return new int[]{target.getOnscreenCoords()[0] - (dims()[0]/2), 
            target.getOnscreenCoords()[1] - (target.getOnscreenHeight()/2) 
                - (dims()[1]) - (tail ? (parent.getScale()*7) : parent.getScale()*2) - (int)(offset*15)};
    }
    
    
    public int[] dims() { return new int[]{64, 64}; }
    
    public void draw(Graphics g) {
        g.setColor(new Color(1, 1, 1, alpha));
        g.fillRect(osc()[0], osc()[1], dims()[0], dims()[1]);
        drawBorder(g);
    }
    
    public final void drawBorder(Graphics g) {
        Image b = parent.getBorder();
        int s = parent.getScale();
        int[] osc = osc();
        int[] dims = dims();
        
        Image topleft = b.getSubImage(0, 0, 3, 3).getScaledCopy(s); topleft.setAlpha(alpha);
        topleft.draw(osc[0]-(2*s), osc[1]-(2*s));
        
        Image topright = b.getSubImage(3, 0, 3, 3).getScaledCopy(s); topright.setAlpha(alpha);
        topright.draw(osc[0]+dims[0]-s, osc[1]-(2*s));
        
        Image bottomleft = b.getSubImage(0, 3, 3, 3).getScaledCopy(s); bottomleft.setAlpha(alpha);
        bottomleft.draw(osc[0]-(2*s), osc[1]+dims[1]-s);
        
        Image bottomright = b.getSubImage(3, 3, 3, 3).getScaledCopy(s); bottomright.setAlpha(alpha);
        bottomright.draw(osc[0]+dims[0]-s, osc[1]+dims[1]-s); //bottomright
        
        Image left = b.getSubImage(13, 0, 2, 1).getScaledCopy(s); 
        left = left.getScaledCopy(left.getWidth(), dims[1]-(2*s));
        left.setAlpha(alpha);
        left.draw(osc[0]-(2*s), osc[1]+s);
        
        Image top = b.getSubImage(15, 0, 1, 2).getScaledCopy(s); 
        top = top.getScaledCopy(dims[0]-(2*s), top.getHeight());
        top.setAlpha(alpha);
        top.draw(osc[0]+s, osc[1]-(2*s));
        
        Image right = b.getSubImage(16, 0, 2, 1).getScaledCopy(s); 
        right = right.getScaledCopy(right.getWidth(), dims[1]-(2*s));
        right.setAlpha(alpha);
        right.draw(osc[0]+dims[0], osc[1]+s);
        
        Image bttm = b.getSubImage(18, 0, 1, 2).getScaledCopy(s); 
        bttm = bttm.getScaledCopy(dims[0]-(2*s), bttm.getHeight());
        bttm.setAlpha(alpha);
        bttm.draw(osc[0]+s, osc[1]+dims[1]);
        
        if (tail) {
            Image tail_img = b.getSubImage(7, 0, 5, 6).getScaledCopy(s); tail_img.setAlpha(alpha);
            tail_img.draw(osc[0]+dims[0]/2 - (s*5)/2, osc[1]+dims[1]);
        }
        
    }
    
}
