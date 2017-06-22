package world.objects.components.logic.handlers;

import misc.Types;
import world.objects.components.logic.Block;

public class PrintHandler extends BlockHandler {
    
    String msg;
    
    @Override
    public void init() {
        msg = Types.toString((String)getParent().getInput(0)[2]);
    }
    
    @Override
    public void update() {
        System.out.println(msg);
        getParent().getParent().goTo(getParent().getConn(Block.NODE_OUT));
    }
    
}
