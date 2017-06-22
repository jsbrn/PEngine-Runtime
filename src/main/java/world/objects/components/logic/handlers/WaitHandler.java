package world.objects.components.logic.handlers;

import misc.Types;
import world.objects.components.logic.Block;

public class WaitHandler extends BlockHandler {
    
    long start_time = -1;
    double dur = -1;
    
    @Override
    public void init() {
        start_time = System.currentTimeMillis();
        dur = Types.toNumber((String)getParent().getInput(0)[2]);
    }
    
    @Override
    public void update() {
        if (System.currentTimeMillis() - start_time >= dur) 
            getParent().getParent().goTo(getParent().getConn(Block.NODE_OUT));
    }
    
}
