package com.bitbucket.computerology.world;

import com.bitbucket.computerology.gui.GameScreen;
import com.bitbucket.computerology.gui.elements.SpeechBalloon;
import com.bitbucket.computerology.misc.Animation;
import com.bitbucket.computerology.misc.Dialogue;
import com.bitbucket.computerology.misc.Force;
import com.bitbucket.computerology.misc.MiscMath;
import com.bitbucket.computerology.misc.Script;
import java.util.ArrayList;
import java.util.Random;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class SceneObject {
    
    private double world_x, world_y, world_w, world_h;
    public String NAME;
    public int LAYER = 2;
    public boolean GRAVITY, COLLIDES;
    
    public Dialogue CURRENT_DIALOGUE;
    public Animation CURRENT_ANIMATION;
    
    public SpeechBalloon SPEECH_BALLOON;
    
    public ArrayList<Script> SCRIPTS = new ArrayList<Script>(), RUNNING_SCRIPTS = new ArrayList<Script>();
    public ArrayList<Animation> ANIMATIONS = new ArrayList<Animation>();
    public ArrayList<Dialogue> DIALOGUES = new ArrayList<Dialogue>();
    public ArrayList<Force> FORCES = new ArrayList<Force>();
    
    public ArrayList<SceneObject> INTERSECTING = new ArrayList<SceneObject>();
    
    public Force GRAVITY_VECTOR = new Force(0, 1, 0, 9.81); //when real world physics actually applies to the virtual realm *shit emoji*
    public Force JUMP_VECTOR = new Force(0, -125, 0, 0);
    
    public String TEXTURE_NAME;
    
    public SceneObject(int world_x, int world_y, int world_w, int world_h, String id) {
        this.world_x = world_x;
        this.world_y = world_y;
        this.world_w = world_w;
        this.world_h = world_h;
        this.NAME = id;
        this.LAYER = 2;
        this.GRAVITY = false;
        this.COLLIDES = true;
        this.TEXTURE_NAME = "";
        this.FORCES.add(GRAVITY_VECTOR);
        this.SPEECH_BALLOON = new SpeechBalloon(this, "");
    }
    
    public Animation getAnim(String name) {
        for (Animation a: ANIMATIONS) {
            if (a.NAME.equals(name)) {
                return a;
            }
        }
        return null;
    }
    
    public final void setAnim(String name) {
        for (Animation a: ANIMATIONS) {
            if (a.NAME.equals(name)) {
                CURRENT_ANIMATION = a;
                return;
            }
        }
        CURRENT_ANIMATION = null;
    }
    
    public final void doCommand(String command) {
        Script s = new Script(this);
        s.processCommand(command);
    }
    
    public SceneObject() {
        this.LAYER = 2;
        this.GRAVITY = false;
        this.COLLIDES = true;
        this.NAME = "";
        this.TEXTURE_NAME = "";
        this.FORCES.add(GRAVITY_VECTOR);
        this.SPEECH_BALLOON = new SpeechBalloon(this, "");
    }
    
    public void update() {
        INTERSECTING = GameScreen.CURRENT_LEVEL.getObjects(getRenderCoords()[0], getRenderCoords()[1], getOnscreenWidth(), getOnscreenHeight());
        for (int i = 0; i != RUNNING_SCRIPTS.size(); i++) {
            if (i < 0 || i >= RUNNING_SCRIPTS.size()) break;
            RUNNING_SCRIPTS.get(i).update();
        }
        if (CURRENT_DIALOGUE != null) {
            CURRENT_DIALOGUE.update();
        }
    }
    
    public void applyForceVectors() {
        
        if (GRAVITY) {
            if (!FORCES.contains(GRAVITY_VECTOR)) {
                FORCES.add(GRAVITY_VECTOR);
            }
        } else {
            FORCES.remove(GRAVITY_VECTOR);
            GRAVITY_VECTOR.reset();
        }
        
        double total_dx = 0, total_dy = 0;
        for (Force f: FORCES) {
            total_dx += f.getXMagnitude();
            total_dy += f.getYMagnitude();
            f.update();
        }
        this.addWorldPos(MiscMath.getConstant(total_dx, 1), MiscMath.getConstant(total_dy, 1));
    }
    
    /**
     * Adds x and y to the object's position if object is not colliding.
     * @param x
     * @param y 
     */
    public void addWorldPos(double x, double y) {
        //check for collision
        SceneObject x_object = null, y_object = null;
        if (COLLIDES) {
            double[] x_box = new double[]{-1000, -1000, 1, 1}, y_box = new double[]{-2000, -2000, 1, 1};
            double y_check_axis = 0, x_check_axis = 0; //the y axis is for the X check, the x axis is for the Y check

            if (x > 0) {
                x_box = new double[]{getRenderCoords()[0]+getOnscreenWidth(), getRenderCoords()[1]+(1*Camera.ZOOM), (x*Camera.ZOOM)+1, getOnscreenHeight()-(2*Camera.ZOOM)};
                x_check_axis = getRenderCoords()[0]+getOnscreenWidth();
            } else if (x < 0) {
                x_box = new double[]{getRenderCoords()[0]+(x*Camera.ZOOM)-1, getRenderCoords()[1]+(1*Camera.ZOOM), -(x*Camera.ZOOM)+1, getOnscreenHeight()-(2*Camera.ZOOM)};
                x_check_axis = getRenderCoords()[0]-(x*Camera.ZOOM)-1;
            }

            if (y > 0) {
                y_box = new double[]{getRenderCoords()[0]+(1*Camera.ZOOM), getRenderCoords()[1]+getOnscreenHeight(), getOnscreenWidth()-(2*Camera.ZOOM), y*Camera.ZOOM+1};
                y_check_axis = getRenderCoords()[1]+getOnscreenHeight();

            } else if (y < 0) {
                y_box = new double[]{getRenderCoords()[0]+(1*Camera.ZOOM), getRenderCoords()[1]+(y*Camera.ZOOM)-1, getOnscreenWidth()-(2*Camera.ZOOM), -(y*Camera.ZOOM)+1};
                y_check_axis = getRenderCoords()[1]-(y*Camera.ZOOM)-1;
            }

            double shortest = Integer.MAX_VALUE;
            for (SceneObject o: GameScreen.CURRENT_LEVEL.getObjects(x_box[0], x_box[1], x_box[2], x_box[3])) {
                if (o.COLLIDES && o.equals(this) == false && o.LAYER == 2) {
                    double side = o.getRenderCoords()[0];
                    if (x < 0) {
                        side = o.getRenderCoords()[0]+o.getOnscreenWidth();
                    }
                    double distance = MiscMath.distanceBetween(x_check_axis, 0, side, 0);
                    if (distance < shortest) {
                        shortest = distance;
                        x_object = o;
                    }
                }
            }
            shortest = Integer.MAX_VALUE;
            ArrayList<SceneObject> colliding = GameScreen.CURRENT_LEVEL.getObjects(y_box[0], y_box[1], y_box[2], y_box[3]);
            for (SceneObject o: colliding) {
                if (o.equals(this) == false) {
                    if (o.COLLIDES) {
                        if (o.LAYER == 2 || (y > 0 && getRenderCoords()[1]+getOnscreenHeight() < o.getRenderCoords()[1]+(2*Camera.ZOOM) && o.LAYER == 1)) {
                            double side = o.getRenderCoords()[1];
                            if (y < 0) {
                                side = o.getRenderCoords()[1]+o.getOnscreenHeight();
                            }
                            double distance = MiscMath.distanceBetween(0, y_check_axis, 0, side);
                            if (distance < shortest) {
                                shortest = distance;
                                y_object = o;
                            }
                        }
                    }
                }
            }
        }
        
        world_x += x;
        world_y += y;
        if (x_object != null) {
            if (x > 0) {
                this.setWorldTopLeft(x_object.getWorldTopLeft()[0]-this.getDimensions()[0], -1);
            } else {
                this.setWorldTopLeft(x_object.getWorldTopLeft()[0]+x_object.getDimensions()[0], -1);
            }
        }
        if (y_object != null) {
            if (y > 0) {
                GRAVITY_VECTOR.reset();
                FORCES.remove(JUMP_VECTOR);
                JUMP_VECTOR.reset();
                this.setWorldTopLeft(-1, y_object.getWorldTopLeft()[1]-this.getDimensions()[1]);
            } else {
                GRAVITY_VECTOR.reset();
                FORCES.remove(JUMP_VECTOR);
                JUMP_VECTOR.setYMagnitude(0);
                this.setWorldTopLeft(-1, y_object.getWorldTopLeft()[1]+y_object.getDimensions()[1]);
            }
        }
    }
    
    public int[] getWorldTopLeft() {
        return new int[]{(int)(world_x-(world_w/2)), (int)(world_y-(world_h/2))};
    }
    
    /**
     * Sets the top left world coordinates of this object (easier than calculating the real world coords and setting those).
     * Pass -1 to leave the axis unchanged.
     * @param x The world x.
     * @param y The world y.
     */
    public void setWorldTopLeft(int x, int y) {
        if (x > -1) {
            world_x = x + (world_w/2);
        }
        if (y > -1) {
            world_y = y + (world_h/2);
        }
    }
    
    //public 
    
    public void resize(double x, double y) {
        world_w += x; world_h += y;
        if (world_w < 1) {
            world_w = 1;
        }
        if (world_h < 1) {
            world_h = 1;
        }
    }
    
    public void startDialogue(String d_name) {
        for (Dialogue d: DIALOGUES) {
            if (d.NAME.equals(d_name)) {
                d.reset();
                if (d.OBJECT_SPEECH.size() > 0) {
                    if (GameScreen.RESPONSE_MENU.visible()) return;
                    this.SPEECH_BALLOON.setText(d.OBJECT_SPEECH.get(0));
                    GameScreen.addGUIElement(this.SPEECH_BALLOON);
                    System.out.println("Set object's speech bubble to: "+d.OBJECT_SPEECH.get(0));
                    if (d.OBJECT_SPEECH.size() > 1 || d.PLAYER_RESPONSES.size() > 0) {
                        GameScreen.CURRENT_SPEECH_BALLOON = this.SPEECH_BALLOON;
                    }
                    CURRENT_DIALOGUE = d;
                    GameScreen.CURRENT_DIALOGUE = d;
                    Camera.setTarget(this);
                } else if (d.PLAYER_RESPONSES.size() > 0) {
                    //set the options in the response menu
                    if (GameScreen.RESPONSE_MENU.visible()) return;
                    GameScreen.RESPONSE_MENU.setDialogue(d);
                    GameScreen.CURRENT_DIALOGUE = d;
                    CURRENT_DIALOGUE = d;
                    Camera.setTarget(this);
                } else {
                    CURRENT_DIALOGUE = null;
                    GameScreen.CURRENT_DIALOGUE = null;
                }
            }
        }
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
    
    public void setWidth(int w) {
        world_w = w;
    }
    
    public void setHeight(int h) {
        world_h = h;
    }
    
    public void setWorldX(int x) {
        world_x = x;
        if (world_w % 2 == 1) {
            world_x+=0.5;
        }
    }
    
    public void setWorldY(int y) {
        world_y = y;
        if (world_h % 2 == 1) {
            world_y+=0.5;
        }
    }
    
    public int[] getWorldCoordinates() {
        return new int[]{(int)world_x, (int)world_y};
    }
    
    public double[] getOnscreenCoords() {
        double shift_x = (Camera.WORLD_X*Camera.ZOOM)-(Display.getWidth()/2), shift_y = (Camera.WORLD_Y*Camera.ZOOM)-(Display.getHeight()/2);
        if (this.LAYER == 0) { //distance object
            shift_x /= 2;
        }
        return new double[]{((world_x*Camera.ZOOM)-shift_x), ((world_y*Camera.ZOOM)-shift_y)};
    }
    
    /**
     * Returns the actual on-screen coordinates in which the image is rendered.
     * This is because the object's X and Y correspond to the center of the object, not the top-left.
     * @return An int array of size 2 {x, y}.
     */
    public double[] getRenderCoords() {
        return new double[]{getOnscreenCoords()[0]-(getOnscreenWidth()/2), getOnscreenCoords()[1]-(getOnscreenHeight()/2)};
    }
    
    public int getOnscreenWidth() {
        return (int)(world_w*Camera.ZOOM);
    }

    public int getOnscreenHeight() {
        return (int)(world_h*Camera.ZOOM);
    }
    
    public int[] getDimensions() {
        return new int[]{(int)world_w, (int)world_h};
    }
    
    public void draw(Graphics g) {
        Image texture = null;
        String texture_name = TEXTURE_NAME;
        if (CURRENT_ANIMATION != null) {
            texture_name = CURRENT_ANIMATION.SPRITE_NAME;
        }
        if (GameScreen.OBJECT_TEXTURE_NAMES.contains(texture_name)) {
            texture = GameScreen.OBJECT_TEXTURES.get(GameScreen.OBJECT_TEXTURE_NAMES.indexOf(texture_name));
        } else if (GameScreen.ANIMATION_TEXTURE_NAMES.contains(texture_name)) {
            texture = GameScreen.ANIMATION_TEXTURES.get(GameScreen.ANIMATION_TEXTURE_NAMES.indexOf(texture_name));
        }

        if (texture != null) {
            int x = 0, w = texture.getWidth(), h = texture.getHeight();
            if (CURRENT_ANIMATION != null) {
                CURRENT_ANIMATION.update();
                for (int i = 0; i != CURRENT_ANIMATION.getIndex(); i++) {x+=CURRENT_ANIMATION.WIDTHS.get(i);}
                w = CURRENT_ANIMATION.WIDTHS.get(CURRENT_ANIMATION.getIndex());
                h = CURRENT_ANIMATION.HEIGHTS.get(CURRENT_ANIMATION.getIndex());
            }
            System.out.println("Sub-image: "+x+", 0, "+w+", "+h);
            texture = texture.getSubImage(x, 0, w, h);
            g.setColor(Color.white);
            world_w = texture.getWidth();
            world_h = texture.getHeight();
            g.drawImage(texture.getScaledCopy((int)Camera.ZOOM), (int)getRenderCoords()[0], (int)getRenderCoords()[1]);    
        }
    }
    
    /**
     * Creates a new object with exactly the same content. Skips the name.
     * @return SceneObject
     */
    public void copyTo(SceneObject o) {
        o.TEXTURE_NAME = this.TEXTURE_NAME;
        o.LAYER = this.LAYER;
        o.GRAVITY = this.GRAVITY;
        o.COLLIDES = this.COLLIDES;
        o.SCRIPTS.clear();
        for (Script s: this.SCRIPTS) {
            Script new_s = new Script(o);
            s.copyTo(new_s);
            o.SCRIPTS.add(new_s);
        }
        o.ANIMATIONS.clear();
        for (Animation a: this.ANIMATIONS) {
            Animation new_a = new Animation(o);
            a.copyTo(new_a);
            o.ANIMATIONS.add(new_a);
        }
        o.DIALOGUES.clear();
        for (Dialogue d: this.DIALOGUES) {
            Dialogue new_d = new Dialogue(o);
            d.copyTo(new_d);
            o.DIALOGUES.add(new_d);
        }
        o.setWidth(this.getDimensions()[0]);
        o.setHeight(this.getDimensions()[1]);
    }
    
}
