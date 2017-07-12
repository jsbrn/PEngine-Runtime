package gui;

import misc.Assets;
import misc.MiscMath;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import world.Camera;
import world.Force;
import world.Level;
import world.World;
import world.events.*;
import world.objects.SceneObject;

public class GameScreen extends BasicGameState {

    private boolean debug = false;
    Input input;
    private boolean initialized = false;

    public GameScreen(int state) {

    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        if (initialized) return;
        Assets.load();
        World.newWorld();
        World.getWorld().loadProject();
        GUI.init();
        initialized = true;
    }

    //draws state (screen) elements
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        World.getWorld().draw(g);
        GUI.getInstance().draw(g);
        if (debug) {
            SceneObject player = World.getWorld().getPlayer();
            Level current_level = World.getWorld().getCurrentLevel();
            g.setColor(Color.white);
            g.drawString("Level: "+current_level+" ("+(current_level == null ? "" : current_level.getName())+")", 10, 10);
            g.drawString("Camera: "+Camera.getX()+", "+Camera.getY(), 10, 30);
            g.drawString("Player: "+player.getWorldCoords()[0]
                    +", "+player.getWorldCoords()[1], 10, 50);
            double[] view = Camera.getViewPort();
            double[] l_bounds = World.getWorld().getCurrentLevel().bounds();
            g.drawString("Viewport: "+view[0]+", "+view[1]+", "+(view[2]+view[0])+", "+view[3]+view[1], 10, 70);
            g.drawString("Level bounds: "+l_bounds[0]+", "+l_bounds[1]+", "+(l_bounds[2]+l_bounds[0])+", "+(l_bounds[3]+l_bounds[1]), 10, 90);
            Force pgrav = player.getForce("gravity");
            g.drawString("Player gravity: "+pgrav.get(Force.MAG)+", "+pgrav.get(Force.ACC), 10, 110);
        }
    }

    //key binding and calling update() in all objects
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
        MiscMath.DELTA_TIME = delta > 200 ? 200 : delta; //200ms between frames is 5FPS
        World.getWorld().update();
        GUI.getInstance().update();
    }

    public void keyPressed(int key, char c) {
        //if the gui does not do anything with the key press, send to game event manager
        if (!GUI.getInstance().handleKeyPress(c)) {
            EventManager.add("keypress", new Event(new Object[]{c}, false));
            EventManager.add("keydown", new Event(new Object[]{c}, true));
        }
    }
    
    @Override
    public void keyReleased(int key, char c) {
        if (key == Input.KEY_F3) debug = !debug;
        EventManager.add("keyrelease", new Event(new Object[]{c}, false));
        EventManager.remove("keydown", new Object[]{c});
    }

}
