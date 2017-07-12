package world.objects.components.logic.handlers.general;

import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class PrintHandler extends BlockHandler {
    
    Object msg;
    
    @Override
    public void init() {
        msg = getParent().resolveInput(0);
    }
    
    @Override
    public boolean update() {
        System.out.println(msg);
        getParent().getParent().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
    @Override
    public void clean() { msg = ""; }
    
}
