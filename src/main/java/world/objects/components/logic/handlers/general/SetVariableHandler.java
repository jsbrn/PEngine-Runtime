package world.objects.components.logic.handlers.general;

import world.objects.components.logic.Block;
import world.objects.components.logic.Flow;
import world.objects.components.logic.handlers.BlockHandler;

public class SetVariableHandler extends BlockHandler {

    Flow f;
    String var;
    Object val;
    
    @Override
    public void init() {
        f = (Flow)getParent().resolveInput(0);
        var = (String)getParent().resolveInput(1);
        val = getParent().resolveInput(2);
    }
    
    @Override
    public boolean update() {
        f.setVar(var, val);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
    @Override
    public void clean() { f = null; var = ""; val = null; }
    
}
