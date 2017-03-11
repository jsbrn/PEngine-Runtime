package com.bitbucket.computerology.world;

import com.bitbucket.computerology.sceneobjects.SceneObject;
import com.bitbucket.computerology.gui.GameScreen;
import com.bitbucket.computerology.misc.MiscMath;
import com.bitbucket.computerology.sceneobjects.Script;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Level {
    
    public Level() {
        
    }
    
    public boolean HAS_BEEN_ENTERED = false;
    
    public double WIDTH = Integer.MAX_VALUE, HEIGHT = Integer.MAX_VALUE;
    public ArrayList<SceneObject> ALL_OBJECTS = new ArrayList<SceneObject>(),
            DISTANT_OBJECTS = new ArrayList<SceneObject>(),
            BACKGROUND_OBJECTS = new ArrayList<SceneObject>(),
            NORMAL_OBJECTS = new ArrayList<SceneObject>(),
            FOREGROUND_OBJECTS = new ArrayList<SceneObject>();
    
    public ArrayList<Script> SCRIPTS = new ArrayList<Script>(), RUNNING_SCRIPTS = new ArrayList<Script>();
    
    private ArrayList[] layers = {DISTANT_OBJECTS, BACKGROUND_OBJECTS, NORMAL_OBJECTS, FOREGROUND_OBJECTS};
    
    public int[] SPAWN_COORD = {0, 0}, CAM_COORD = {0, 0};
    
    public String NAME = "", AMBIENT_SOUND = "", BG_MUSIC = "";
    public int R1 = 0, G1 = 0, B1 = 0, R2 = 0, G2 = 0, B2 = 0, 
            R3 = 0, G3 = 0, B3 = 0, AMBIENT_INTENSITY = 0, ZOOM = 4;
    public boolean LOOP_BG_MUSIC = true, LOOP_AMBIENT_SOUND = true, PLAY_BG_MUSIC_AUTOMATICALLY = true,
            PLAY_AMBIENT_SOUND_AUTOMATICALLY = true;
    public float MUSIC_VOLUME = 1, AMBIENT_VOLUME = 1;
    
    public void moveToLayer(int layer, SceneObject o) {
        for (int i = 0; i != layers.length; i++) {
            if (i != o.LAYER) {
                layers[i].remove(o);
            } else {
                if (layers[i].contains(o) == false) {
                    layers[i].add(o);
                }
            }
        }
    }
    
    public void moveForward(SceneObject o) {
        for (int i = 0; i != layers.length; i++) {
            if (i == o.LAYER) {
                int orig = layers[i].indexOf(o);
                if (orig < layers[i].size()-1) {
                    layers[i].remove(o);
                    layers[i].add(orig+1, o);
                }
            }
        }
    }
    
    public void moveBackward(SceneObject o) {
        for (int i = 0; i != layers.length; i++) {
            if (i == o.LAYER) {
                int orig = layers[i].indexOf(o);
                if (orig > 0) {
                    Object prev = layers[i].get(orig-1);
                    layers[i].add(orig+1, prev);
                    layers[i].remove(prev);
                }
            }
        }
    }
    
    public void removeObject(SceneObject o) {
        o.runScript("on_delete", "");
        ALL_OBJECTS.remove(o);
        for (int i = 0; i != layers.length; i++) {
            layers[i].remove(o);
        }
    }
    
    public SceneObject getObject(String id) {
        for (SceneObject o: ALL_OBJECTS) {
            if (o.NAME.equals(id)) {
                return o;
            }
        }
        return null;
    }
    
    public void update() {
        for (int i = 0; i != ALL_OBJECTS.size(); i++) {
            if (i < ALL_OBJECTS.size()) {
                ALL_OBJECTS.get(i).update();
                ALL_OBJECTS.get(i).applyForceVectors();
            } else {
                break;
            }
        }
        for (int i = 0; i != RUNNING_SCRIPTS.size(); i++) {
            if (i < RUNNING_SCRIPTS.size()) {
                RUNNING_SCRIPTS.get(i).update();
            } else {
                break;
            }
        }
    }
    
    public void draw(Graphics g) {
        double r1 = R1, g1 = G1, b1 = B1;
        double r2 = R2, g2 = G2, b2 = B2;
        int height = 10;
        int increments = (int)(Display.getHeight()/height);
        double g_add = (g2-g1)/increments;
        double r_add = (r2-r1)/increments;
        double b_add = (b2-b1)/increments;
        for (int y = 0; y < 1+increments; y+=1) {
            r1+=r_add;g1+=g_add;b1+=b_add;
            if (r1 <= 255 && g1 <= 255 && b1 <= 255 && r1 >= 0 && g1 >= 0 && b1 >= 0) {
                g.setColor(new Color((int)r1, (int)g1, (int)b1));
            }
            g.fillRect(0, y*height, (int)Display.getWidth(), height);
        }
        
        for (SceneObject o: DISTANT_OBJECTS) {
            o.draw(g);
        }
        for (SceneObject o: BACKGROUND_OBJECTS) {
            o.draw(g);
        }
        for (SceneObject o: NORMAL_OBJECTS) {
            o.draw(g);
        }
        
        for (SceneObject o: FOREGROUND_OBJECTS) {
            o.draw(g);
        }
        
        g.setColor(new Color(R3, G3, B3, AMBIENT_INTENSITY));
        g.fillRect(0, 0, Display.getWidth(), Display.getHeight());
        
    }
    
    public void drawString(String str, int x, int y, Graphics g) {
        g.setColor(Color.gray.darker());
        g.drawString(str, x+1, y+1);
        g.setColor(Color.white);
        g.drawString(str, x, y);
    }
    
    public void add(SceneObject o) {
        if (ALL_OBJECTS.contains(o) == false) {
            ALL_OBJECTS.add(o);
            moveToLayer(o.LAYER, o);
            o.runScript("on_spawn", "");
        }
    }
    
    public SceneObject getObject(int world_x, int world_y) {
        ArrayList<SceneObject> all_objects = new ArrayList<SceneObject>();
        all_objects.addAll(DISTANT_OBJECTS);
        all_objects.addAll(BACKGROUND_OBJECTS);
        all_objects.addAll(NORMAL_OBJECTS);
        all_objects.addAll(FOREGROUND_OBJECTS);
        for (int i = all_objects.size()-1; i != -1; i--) {
            SceneObject o = all_objects.get(i);
            int[] world_coords = o.getWorldCoordinates();
            int[] dims = o.getDimensions();
            if (MiscMath.pointIntersects(world_x, world_y, 
                    world_coords[0], world_coords[1], dims[0], dims[1])) {
                return o;
            }
        }
        return null;
    }
    
    public ArrayList<SceneObject> getObjects(double on_screen_x, double on_screen_y, double width, double height) {
        ArrayList<SceneObject> all_objects = new ArrayList<SceneObject>(), valid = new ArrayList<SceneObject>();
        all_objects.addAll(DISTANT_OBJECTS);
        all_objects.addAll(BACKGROUND_OBJECTS);
        all_objects.addAll(NORMAL_OBJECTS);
        all_objects.addAll(FOREGROUND_OBJECTS);
        for (int i = all_objects.size()-1; i != -1; i--) {
            SceneObject o = all_objects.get(i);
            double[] render_coords = o.getRenderCoords();
            if (MiscMath.rectanglesIntersect(on_screen_x, on_screen_y, width, height, 
                    render_coords[0], render_coords[1], o.getOnscreenWidth(), o.getOnscreenHeight())) {
                valid.add(o);
            }
        }
        return valid;
    }
    
    public void runScript(String script_name, String param) {
        for (Script s: SCRIPTS) {
            if (s.NAME.equals(script_name) && RUNNING_SCRIPTS.contains(s) == false) {
                RUNNING_SCRIPTS.add(s);
                s.setParam(param);
                break;
            }
        }
    }
    
    public void stopScript(String script_name) {
        for (Script s: RUNNING_SCRIPTS) {
            if (s.NAME.equals(script_name)) {
                s.reset();
                RUNNING_SCRIPTS.remove(s);
                break;
            }
        }
    }

    
}
