package world.objects.components.logic.handlers.general;

import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class WaitHandler extends BlockHandler {
    
    long start_time;
    double dur;
    
    @Override
    public void init() {
        start_time = System.currentTimeMillis();
        dur = (Double)getParent().resolveInput(0);
    }
    
    @Override
    public boolean update() {
        if (System.currentTimeMillis() - start_time >= dur) {
            getParent().getParent().goTo(getParent().getConn(Block.NODE_OUT));
            return true;
        }
        return false;
    }
    
    @Override
    public void clean() { start_time = 0; dur = 0; }
    
}
