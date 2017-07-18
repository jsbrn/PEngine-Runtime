package world.objects.components.logic.handlers.gui;

import gui.GUI;
import gui.elements.ChoiceBubble;
import java.util.ArrayList;
import misc.Types;
import world.World;
import world.objects.SceneObject;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class AwaitPlayerChoiceHandler extends BlockHandler {
    
    SceneObject o;
    ArrayList<String> choices;
    ChoiceBubble b;
    
    @Override
    public void init() {
        o = World.getWorld().getPlayer();
        choices = (ArrayList<String>)getParent().resolveInput(0);
        for (int i = 0; i < choices.size(); i++) 
            choices.set(i, Types.parseText(choices.get(i)));
        b = new ChoiceBubble(choices.toArray(new String[]{}));
        b.setTarget(o);
        GUI.getInstance().addElement(b);
    }
    
    @Override
    public boolean update() {
        if (b.isSubmitted()) {
            getFlow().setVar((String)getParent().resolveOutput(0), b.getChoice());
            getParent().getParent().goTo(getParent().getConn(Block.NODE_OUT));
        }
        return true;
    }
    
}
