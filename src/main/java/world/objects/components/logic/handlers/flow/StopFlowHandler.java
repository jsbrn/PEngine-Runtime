package world.objects.components.logic.handlers.flow;

import world.objects.components.logic.Block;
import world.objects.components.logic.Flow;
import world.objects.components.logic.handlers.BlockHandler;

public class StopFlowHandler extends BlockHandler {
    
    Flow f;
    String start_id;
    
    @Override
    public void init() {
        f = (Flow)getParent().resolveInput(0);
    }
    
    @Override
    public boolean update() {
        f.getParent().stopFlow(f);
        getParent().getParent().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
    @Override
    public void clean() { }
    
}
