package gui;

import misc.Assets;
import misc.MiscMath;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import world.World;
import world.events.*;

public class GameScreen extends BasicGameState {

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
        initialized = true;
    }

    //draws state (screen) elements
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        World.getWorld().draw(g);
    }

    //key binding and calling update() in all objects
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
        MiscMath.DELTA_TIME = delta > 200 ? 200 : delta; //200ms between frames is 5FPS
        World.getWorld().update();
    }

    public void keyPressed(int key, char c) {
        EventManager.add("keypress", new Event(new Object[]{c}, false));
        EventManager.add("keydown", new Event(new Object[]{c}, true));
    }
    
    @Override
    public void keyReleased(int key, char c) {
        EventManager.add("keyrelease", new Event(new Object[]{c}, false));
        EventManager.remove("keydown", new Object[]{c});
    }

}
