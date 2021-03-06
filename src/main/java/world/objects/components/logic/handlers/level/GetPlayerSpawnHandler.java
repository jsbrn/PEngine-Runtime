package world.objects.components.logic.handlers.level;

import world.Level;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class GetPlayerSpawnHandler extends BlockHandler {

    Level l;
    
    @Override
    public void init() {
        l = (Level)getParent().resolveInput(0);
    }
    
    @Override
    public boolean update() {
        getFlow().setVar(getParent().resolveOutput(0), l.playerSpawn()[0]);
        getFlow().setVar(getParent().resolveOutput(1), l.playerSpawn()[1]);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
}
