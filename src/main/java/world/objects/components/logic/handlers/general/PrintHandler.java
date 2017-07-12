package world.objects.components.logic.handlers.general;

import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class PrintHandler extends BlockHandler {
    
    String msg;
    
    @Override
    public void init() {
        msg = (String)getParent().resolveInput(0).toString();
    }
    
    @Override
    public void update() {
        System.out.println(msg);
        getParent().getParent().goTo(getParent().getConn(Block.NODE_OUT));
    }
    
    @Override
    public void clean() { msg = ""; }
    
}
