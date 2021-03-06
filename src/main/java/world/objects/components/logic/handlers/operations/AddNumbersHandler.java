package world.objects.components.logic.handlers.operations;

import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class AddNumbersHandler extends BlockHandler {

    double a, b;
    
    @Override
    public void init() {
        a = (Double)getParent().resolveInput(0);
        b = (Double)getParent().resolveInput(1);
    }
    
    @Override
    public boolean update() {
        getFlow().setVar(getParent().resolveOutput(0), a+b);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
    @Override
    public void clean() { a = 0; b = 0; }
    
}
