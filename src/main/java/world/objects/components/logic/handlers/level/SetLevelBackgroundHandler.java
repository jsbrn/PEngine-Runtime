package world.objects.components.logic.handlers.level;

import java.util.ArrayList;
import misc.MiscMath;
import misc.Types;
import org.newdawn.slick.Color;
import world.Level;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class SetLevelBackgroundHandler extends BlockHandler {

    Level l;
    int[] new_top, new_bottom, old_top, old_bottom, curr_top, curr_bottom;
    double transition, lifespan;
    
    @Override
    public void init() {
        l = (Level)getParent().resolveInput(0);
        ArrayList<String> t = (ArrayList<String>)getParent().resolveInput(1);
        ArrayList<String> b = (ArrayList<String>)getParent().resolveInput(2);
        new_top = new int[]{(int)Types.parseNumber(t.get(0)), 
            (int)Types.parseNumber(t.get(1)), (int)Types.parseNumber(t.get(2))};
        new_bottom = new int[]{(int)Types.parseNumber(b.get(0)), 
            (int)Types.parseNumber(b.get(1)), (int)Types.parseNumber(b.get(2))};
        old_top = new int[]{l.getTopBGColor().getRed(), l.getTopBGColor().getGreen(), l.getTopBGColor().getBlue()};
        old_bottom = new int[]{l.getBottomBGColor().getRed(), l.getBottomBGColor().getGreen(), l.getBottomBGColor().getBlue()};
        System.arraycopy(old_top, 0, curr_top, 0, old_top.length);
        System.arraycopy(old_bottom, 0, curr_bottom, 0, old_bottom.length);
        
        transition = (Double)getParent().resolveInput(3);
        lifespan = 0;
    }
    
    @Override
    public boolean update() {
        transition -= MiscMath.getConstant(1000, 1);
        lifespan += MiscMath.getConstant(1000, 1);
        for (int i = 0; i < curr_top.length; i++) curr_top[i] += MiscMath.getConstant(new_top[0]-old_top[0], transition/1000);
        for (int i = 0; i < curr_bottom.length; i++) curr_bottom[i] += MiscMath.getConstant(new_bottom[0]-old_bottom[0], transition/1000);
        l.setTopBGColor(new Color(curr_top[0], curr_top[1], curr_top[1]));
        l.setBottomBGColor(new Color(curr_bottom[0], curr_bottom[1], curr_bottom[2]));
        if (transition <= 0) {
            l.setTopBGColor(new Color(new_top[0], new_top[1], new_top[1]));
            l.setBottomBGColor(new Color(new_bottom[0], new_bottom[1], new_bottom[2]));
            getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        }
        return false;
    }
    
    @Override
    public void clean() { }
    
}
