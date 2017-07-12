package world.objects.components.logic.handlers.gui;

import gui.GUI;
import gui.elements.SpeechBubble;
import world.objects.SceneObject;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class SayHandler extends BlockHandler {
    
    SceneObject o;
    String text;
    double delay;
    boolean wait;
    
    @Override
    public void init() {
        o = (SceneObject)getParent().resolveInput(0);
        text = (String)getParent().resolveInput(1);
        delay = (Double)getParent().resolveInput(2);
        wait = (Boolean)getParent().resolveInput(3);
        SpeechBubble b = new SpeechBubble(text, (int)delay, wait);
        b.setTarget(o);
        GUI.getInstance().addElement(b);
    }
    
    @Override
    public boolean update() {
        getParent().getParent().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
    @Override
    public void clean() {  }
    
}
