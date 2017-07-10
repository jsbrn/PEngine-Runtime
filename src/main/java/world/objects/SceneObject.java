package world.objects;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import misc.Assets;
import misc.MiscMath;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import world.Camera;
import world.Force;
import world.Level;
import world.World;
import world.events.Event;
import world.events.EventManager;
import world.objects.components.Animation;
import world.objects.components.logic.Flow;

public class SceneObject {
    
    private double world_x, world_y, world_w, world_h;
    private String name, type, texture;
    private int layer;
    private boolean gravity, collides;
    
    private ArrayList<Animation> animations;
    private ArrayList<Flow> flows, active_flows;
    private Animation current_anim;
    private HashMap<String, Force> forces;
    
    boolean hitbox = false;
    
    public SceneObject() {
        this.layer = Level.NORMAL_LAYER;
        this.gravity = false;
        this.type = "";
        this.collides = true;
        this.name = "";
        this.texture = "";
        this.world_w = 16;
        this.world_h = 16;
        this.animations = new ArrayList<Animation>();
        this.flows = new ArrayList<Flow>();
        this.active_flows = new ArrayList<Flow>();
        this.forces = new HashMap<String, Force>();
    }
    
    public void addForce(String name, Force f) {
        if (!forces.containsKey(name)) forces.put(name, f);
    }
    
    public void removeForce(String name) { forces.remove(name); }
    
    public Force getForce(String name) { return forces.get(name); }
    
    public void launchFlow(Flow f, String start) {
        if (flows.contains(f)) {
            if (f.start(start)) active_flows.add(f);
        }
    }
    
    public void stopFlow(Flow f) {
        if (active_flows.contains(f)) {
            f.stop();
            active_flows.remove(f);
        }
    }
    
    public void pauseFlow(Flow f) {
        if (active_flows.contains(f)) active_flows.remove(f);
    }
    
    public void setAnimation(Animation a) {
        current_anim = a;
    }
    
    public void update() {
        double[] vel = new double[2];
        for (Force f: forces.values()) {
            f.update();
            vel[0] += f.velocity()[0];
            vel[1] += f.velocity()[1];
        }
        //apply collision rules and move
        move(MiscMath.getConstant(vel[0], 1), MiscMath.getConstant(vel[1], 1));
        
        for (int i = active_flows.size() - 1; i > -1; i--) {
            active_flows.get(i).update();
            if (active_flows.get(i).finished())
                active_flows.remove(i);
        }
    }
    
    public void move(double x, double y) {
        
        /**
        * This is the only part of the code which I am not proud of.
        * It's a giant mess. Collision sucks.
        */
        
        //check for collision
        SceneObject x_object = null, y_object = null;
        if (collides) {
            double[] x_box = new double[]{-Integer.MAX_VALUE, -Integer.MAX_VALUE, 1, 1}, y_box = new double[]{-Integer.MAX_VALUE, -Integer.MAX_VALUE, 1, 1};
            double y_check_axis = 0, x_check_axis = 0; //the y axis is for the X check, the x axis is for the Y check

            if (x > 0) {
                x_box = new double[]{getRenderCoords()[0]+getOnscreenWidth(), getRenderCoords()[1]+(1*Camera.getZoom()), (x*Camera.getZoom())+1, getOnscreenHeight()-(2*Camera.getZoom())};
                x_check_axis = getRenderCoords()[0]+getOnscreenWidth();
            } else if (x < 0) {
                x_box = new double[]{getRenderCoords()[0]+(x*Camera.getZoom())-1, getRenderCoords()[1]+(1*Camera.getZoom()), -(x*Camera.getZoom())+1, getOnscreenHeight()-(2*Camera.getZoom())};
                x_check_axis = getRenderCoords()[0]-(x*Camera.getZoom())-1;
            }

            if (y > 0) {
                y_box = new double[]{getRenderCoords()[0]+(1*Camera.getZoom()), getRenderCoords()[1]+getOnscreenHeight(), getOnscreenWidth()-(2*Camera.getZoom()), y*Camera.getZoom()+1};
                y_check_axis = getRenderCoords()[1]+getOnscreenHeight();
            } else if (y < 0) {
                y_box = new double[]{getRenderCoords()[0]+(1*Camera.getZoom()), getRenderCoords()[1]+(y*Camera.getZoom())-1, getOnscreenWidth()-(2*Camera.getZoom()), -(y*Camera.getZoom())+1};
                y_check_axis = getRenderCoords()[1]-(y*Camera.getZoom())-1;
            }

            double shortest = Integer.MAX_VALUE;
            Level current = World.getWorld().getCurrentLevel();
            for (SceneObject o: current.getObjects(x_box[0], x_box[1], x_box[2], x_box[3])) {
                if (o.collides && o.equals(this) == false && o.layer == Level.NORMAL_LAYER) {
                    double side = o.getRenderCoords()[0];
                    if (x < 0) {
                        side = o.getRenderCoords()[0]+o.getOnscreenWidth();
                    }
                    double distance = MiscMath.distance(x_check_axis, 0, side, 0);
                    if (distance < shortest) {
                        shortest = distance;
                        x_object = o;
                    }
                }
            }
            shortest = Integer.MAX_VALUE;
            ArrayList<SceneObject> colliding = current.getObjects(y_box[0], y_box[1], y_box[2], y_box[3]);
            for (SceneObject o: colliding) {
                if (o.equals(this) == false) {
                    if (o.collides) {
                        if (o.layer == Level.NORMAL_LAYER || (y > 0 
                                && getRenderCoords()[1]+getOnscreenHeight() < o.getRenderCoords()[1]+(2*Camera.getZoom()) 
                                && o.layer == Level.BACKGROUND_LAYER)) {
                            double side = o.getRenderCoords()[1];
                            if (y < 0) side = o.getRenderCoords()[1]+o.getOnscreenHeight();
                            double distance = MiscMath.distance(0, y_check_axis, 0, side);
                            if (distance < shortest) {
                                shortest = distance;
                                y_object = o;
                            }
                        }
                    }
                }
            }
        }
        
        world_x += x; world_y += y;
        
        if (x_object != null) {
            if (x > 0) {
                this.setWorldX(x_object.getWorldCoords()[0]-(x_object.getDimensions()[0]/2)-((double)this.getDimensions()[0]/2));
            } else {
                this.setWorldX(x_object.getWorldCoords()[0]+(x_object.getDimensions()[0]/2)+(this.getDimensions()[0]/2));
            }
            EventManager.add("collision", new Event(new Object[]{this, x_object}, false));
        }
        if (y_object != null) {
            getForce("gravity").reset();
            removeForce("jump");
            if (y > 0) {
                this.setWorldY(y_object.getWorldCoords()[1]-((double)y_object.getDimensions()[1]/2)-((double)this.getDimensions()[1]/2));
            } else {
                this.setWorldY(y_object.getWorldCoords()[1]+(y_object.getDimensions()[1]/2)+(this.getDimensions()[1]/2));
            }
            //if y obj = x obj then at this point an event has already been created
            if (!y_object.equals(x_object)) EventManager.add("collision", new Event(new Object[]{this, y_object}, false));
        }
        
    }
    
    public boolean containsAnimation(String name) {
        for (Animation o: animations) {
            if (o.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsFlow(String name) {
        for (Flow o: flows) {
            if (o.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }
    
    public void resize(double x, double y) {
        if (!hitbox) return;
        world_w += x; world_h += y;
        world_w = world_w < 2 ? 2 : world_w;
        world_h = world_h < 2 ? 2 : world_h;
    }
    
    public void setLayer(int l) { layer = l; }
    
    public void setWidth(int w) {
        world_w = w < 2 ? 2 : w;
    }
    
    public void setHeight(int h) {
        world_h = h < 2 ? 2 : h;
    }
    
    public void setWorldX(double x) {
        world_x = x;
    }
    
    public void setWorldY(double y) {
        world_y = y;
    }
    
    public String getType() { return type; }
    
    public void setType(String type) { this.type = type; }
    
    public void setName(String name) { this.name = name; }
    
    public String getName() { return name; }
    
    public int getLayer() { return layer; }
    
    public boolean gravity() { return gravity; }
    
    public boolean collides() { return collides; }
    
    public void setGravity(boolean g) { this.gravity = g; }
    
    public void setCollides(boolean c) { this.collides = c; }
    
    public double[] getWorldCoords() {
        return new double[]{world_x, world_y};
    }
    
    public int[] getOnscreenCoords() {
        double[] wc = getWorldCoords();
        int[] osc = MiscMath.getOnscreenCoords(wc[0], wc[1]);
        return osc;
    }
    
    public int getOnscreenWidth() {
        int[] dims = getDimensions();
        return (int)(dims[0]*Camera.getZoom());
    }
    
    public int getOnscreenHeight() {
        int[] dims = getDimensions();
        return (int)(dims[1]*Camera.getZoom());
    }
    
    public int[] getDimensions() {
        Object o = Assets.get(texture);
        if (o != null) {
            Image img = (Image)o;
            return new int[]{img.getWidth(), img.getHeight()};
        }
        return new int[]{(int)world_w, (int)world_h};
    }
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("so\n");
                bw.write("xy="+getWorldCoords()[0]+" "+getWorldCoords()[1]+"\n");
                bw.write("wh="+getDimensions()[0]+" "+getDimensions()[1]+"\n");
                bw.write("n="+name+"\n");
                bw.write("t="+type+"\n");
                bw.write("tx="+texture+"\n");
                bw.write("h="+hitbox+"\n");
                bw.write("g="+gravity+"\n");
                bw.write("c="+collides+"\n");
                bw.write("l="+layer+"\n");
                for (Flow f: flows) f.save(bw);
                for (Animation a: animations) a.save(bw);
            bw.write("/so\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean load(BufferedReader br) {
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.equals("/so")) {
                    System.out.println("SceneObject "+name+" loaded at "+world_x+", "+world_y);
                    return true;
                }
                
                if (line.indexOf("n=") == 0) name = line.substring(2);
                if (line.indexOf("t=") == 0) type = line.substring(2);
                if (line.indexOf("tx=") == 0) texture = line.substring(3);
                if (line.indexOf("h=") == 0) hitbox = Boolean.parseBoolean(line.substring(2));
                if (line.indexOf("g=") == 0) {
                    gravity = Boolean.parseBoolean(line.substring(2));
                    if (gravity) addForce("gravity", new Force(0, 1, 9.81, 1));
                }
                if (line.indexOf("c=") == 0) collides = Boolean.parseBoolean(line.substring(2));
                if (line.indexOf("l=") == 0) layer = Integer.parseInt(line.substring(2));
                
                if (line.indexOf("xy=") == 0) {
                    double[] coords = MiscMath.toDoubleArray(line.substring(3));
                    world_x = coords[0]; world_y = coords[1];
                }
                if (line.indexOf("wh=") == 0) {
                    int[] dims = MiscMath.toIntArray(line.substring(3));
                    world_w = dims[0]; world_h = dims[1];
                }
                
                if (line.equals("a")) {
                    Animation a = new Animation();
                    if (a.load(br)) animations.add(a);
                }
                
                if (line.equals("f")) {
                    Flow a = new Flow();
                    if (a.load(br)) {
                        flows.add(a);
                        a.setParent(this);
                        this.launchFlow(a, "default");
                    }
                }
                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Copies all properties and values (except for the name and coordinates) to the specified object.
     * @param o The specified object.
     */
    public void copyTo(SceneObject o) {
        o.texture = this.texture;
        o.type = this.type;
        o.layer = this.layer;
        o.gravity = this.gravity;
        o.collides = this.collides;
        o.hitbox = this.hitbox;

        //DELETE any unlocked anims in O
        for (int i = o.animations.size() - 1; i > -1; i--) 
            o.animations.remove(i);
        //copy over all anims in this obj that are not in O
        for (Animation a: animations) {
            Animation copy_over = new Animation();
            a.copyTo(copy_over, true);
            o.animations.add(copy_over);
        }
        
        //DELETE any unlocked anims in O
        for (int i = o.flows.size() - 1; i > -1; i--) 
            o.flows.remove(i);
        //copy over all anims in this obj that are not in O
        for (Flow a: flows) {
            Flow copy_over = new Flow();
            a.copyTo(copy_over, true);
        }
        
        o.setWidth(this.getDimensions()[0]);
        o.setHeight(this.getDimensions()[1]);
        
    }
    
    public Animation getAnimation(String name) {
        for (Animation a: animations) {
            if (a.getName().equals(name)) return a;
        }
        return null;
    }
    
    public Flow getFlow(String name) {
        for (Flow f: flows) {
            if (f.getName().equals(name)) return f;
        }
        return null;
    }
    
    public void draw(Graphics g) {
        
        int[] osc = getOnscreenCoords();
        int x = osc[0], y = osc[1];
        int w = getOnscreenWidth();
        int h = getOnscreenHeight();
        
        if (current_anim == null) {
            Object asset = Assets.get(texture);
            Image img = (Image)asset;

            if (img != null) {
                g.setColor(Color.white);
                g.drawImage(img.getScaledCopy((float)Camera.getZoom()), osc[0]-(w/2), osc[1]-(h/2));
            }
        } else {
            current_anim.draw(x, y, Camera.getZoom(), g);
        }
        
    }
    
    public ArrayList<Animation> getAnimations() { return animations; }
    public ArrayList<Flow> getFlows() { return flows; }
    
    public int[] getRenderCoords() {
        int[] osc = getOnscreenCoords();
        osc[0] -= getOnscreenWidth()/2;
        osc[1] -= getOnscreenHeight()/2;
        return osc;
    }
    
}
