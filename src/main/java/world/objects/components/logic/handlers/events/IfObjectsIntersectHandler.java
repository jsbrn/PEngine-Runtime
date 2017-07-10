package world.objects.components.logic.handlers.events;

import misc.MiscMath;
import world.objects.SceneObject;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class IfObjectsIntersectHandler extends BlockHandler {
    
    SceneObject a, b;
    
    @Override
    public void init() {
        a = (SceneObject)getParent().resolveInput(0);
        b = (SceneObject)getParent().resolveInput(1);
    }
    
    @Override
    public void update() {
        int[] a_rc = a.getRenderCoords(), b_rc = b.getRenderCoords();
        boolean c = MiscMath.rectanglesIntersect(a_rc[0], a_rc[1], a.getOnscreenWidth(), a.getOnscreenHeight(), 
                b_rc[0], b_rc[1], b.getOnscreenWidth(), b.getOnscreenHeight());
        boolean l = a.getLayer() == b.getLayer();
        getFlow().goTo(getParent().getConn(c && l ? Block.NODE_YES : Block.NODE_NO));
    }
    
}
