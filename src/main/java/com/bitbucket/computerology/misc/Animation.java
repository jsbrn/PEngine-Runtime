package com.bitbucket.computerology.misc;

import com.bitbucket.computerology.world.SceneObject;
import java.util.ArrayList;

public class Animation {
    
    public ArrayList<Integer> DURATIONS = new ArrayList<Integer>(), WIDTHS = new ArrayList<Integer>(), HEIGHTS = new ArrayList<Integer>();
    public String NAME = "", SPRITE_NAME = "";
    public boolean LOOP = false;
    
    long last_time = 0;
    int index = 0;
    int duration = 0;
    
    SceneObject parent;
    
    public Animation(SceneObject parent) {
        this.parent = parent;
        this.last_time = System.currentTimeMillis();
    }
    
    public int getIndex() {
        return index;
    }
    
    public void reset() {
        index = 0;
    }
    
    public void copyTo(Animation new_a) {
        new_a.NAME = NAME;
        new_a.LOOP = LOOP;
        new_a.SPRITE_NAME = SPRITE_NAME;
        new_a.DURATIONS.addAll(DURATIONS);
        new_a.WIDTHS.addAll(WIDTHS);
        new_a.HEIGHTS.addAll(HEIGHTS);
    }
    
    public void update() {
        if (System.currentTimeMillis() >= last_time+duration) {
            if (index >= DURATIONS.size()) {
                index = DURATIONS.size()-1;
                if (LOOP) {
                    reset();
                } else {
                    parent.CURRENT_ANIMATION = null;
                    parent.runScript("on_anim_completion", "");
                }
            } else { 
                duration = DURATIONS.get(index);
                last_time = System.currentTimeMillis();
                parent.runScript("on_anim_frame_"+(index+1), "");
                index++;
                if (index >= DURATIONS.size()) {
                    reset();
                }
            }
        }
    }
    
}
