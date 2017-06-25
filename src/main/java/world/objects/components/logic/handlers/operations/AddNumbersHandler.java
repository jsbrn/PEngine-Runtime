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
    public void update() {
        getFlow().setVar((String)getParent().getOutput(0)[2], a+b);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
    }
    
    @Override
    public void clean() { a = 0; b = 0; }
    
}
