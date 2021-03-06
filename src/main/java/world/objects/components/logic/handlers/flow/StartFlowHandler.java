package world.objects.components.logic.handlers.flow;

import world.objects.components.logic.Block;
import world.objects.components.logic.Flow;
import world.objects.components.logic.handlers.BlockHandler;

public class StartFlowHandler extends BlockHandler {
    
    Flow f;
    String start_id;
    
    @Override
    public void init() {
        f = (Flow)getParent().resolveInput(0);
        start_id = (String)getParent().resolveInput(1);
    }
    
    @Override
    public boolean update() {
        f.getParent().launchFlow(f, start_id);
        getParent().getParent().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
    @Override
    public void clean() { }
    
}
