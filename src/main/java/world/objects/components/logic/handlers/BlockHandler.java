package world.objects.components.logic.handlers;

import world.objects.components.logic.Block;
import world.objects.components.logic.Flow;

public class BlockHandler {
    
    private Block parent;
    
    public void update() {}
    
    public final Block getParent() { return parent; }
    public final void setParent(Block parent) { this.parent = parent; }
    /**
     * Get the parent block's parent, that is, the Flow this handler is working in.
     * @return A Flow instance, or null if none found for some reason.
     */
    public final Flow getFlow() { return getParent() == null ? null : getParent().getParent(); }
    
    public void init() {}
    public void clean() {}
    
    
}
