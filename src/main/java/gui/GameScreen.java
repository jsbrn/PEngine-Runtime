package gui;

import gui.elements.SpeechBubble;
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
    GUI GUI;

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
        GUI = new GUI();
        GUI.setScale(4);
        GUIElement test = new SpeechBubble("I KNOW WE WERE PRETTY GOOFY DICKED AROUND AND HAD A LOT OF FUN", 5000, true);
        //GUIElement test = new ChoiceBubble(new String[]{"Be pretty goofy", "Have a lot of fun", "Dick around", "Work on report"});
        test.setTarget(World.getWorld().getPlayer());
        GUI.addElement(test);
        initialized = true;
    }

    //draws state (screen) elements
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        World.getWorld().draw(g);
        GUI.draw(g);
    }

    //key binding and calling update() in all objects
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
        MiscMath.DELTA_TIME = delta > 200 ? 200 : delta; //200ms between frames is 5FPS
        World.getWorld().update();
        GUI.update();
    }

    public void keyPressed(int key, char c) {
        //if the gui does not do anything with the key press, send to game event manager
        if (!GUI.handleKeyPress(c)) {
            EventManager.add("keypress", new Event(new Object[]{c}, false));
            EventManager.add("keydown", new Event(new Object[]{c}, true));
        }
    }
    
    @Override
    public void keyReleased(int key, char c) {
        EventManager.add("keyrelease", new Event(new Object[]{c}, false));
        EventManager.remove("keydown", new Object[]{c});
    }

}
