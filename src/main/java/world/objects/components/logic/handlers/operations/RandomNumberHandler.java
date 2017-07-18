package world.objects.components.logic.handlers.operations;

import java.util.Random;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class RandomNumberHandler extends BlockHandler {

    double min, max, result;
    Random r;
    
    @Override
    public void init() {
        r = new Random();
        if (getParent().inputCount() == 1) {
            max = (Double)getParent().resolveInput(0);
            result = r.nextDouble()*max;
        }
        if (getParent().inputCount() == 2)
            min = (Double)getParent().resolveInput(0);
            max = (Double)getParent().resolveInput(1);
            result = min + (r.nextDouble()*(max-min));
    }
    
    @Override
    public boolean update() {
        getFlow().setVar((String)getParent().resolveOutput(0), result);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
    @Override
    public void clean() {  }
    
}
