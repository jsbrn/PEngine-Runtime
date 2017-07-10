package main;

import gui.GameScreen;
import java.io.IOException;
import misc.Assets;
import misc.Window;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class SlickInitializer extends StateBasedGame {

    public static final int GAME_SCREEN = 0;

    public SlickInitializer(String gameTitle) {

        super(gameTitle); //set window title to "gameTitle" string

        //add states
        addState(new GameScreen(GAME_SCREEN));
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        //initialize states
        getState(GAME_SCREEN).init(gc, this);
        //enter state
        this.enterState(GAME_SCREEN);
    }

    public static void main(String args[]) throws IOException {
        
        Assets.PROJECT_DIR = args.length > 0 ? args[0] : "";
        Assets.STARTING_LEVEL = args.length > 1 ? args[1] : "";

        //the textures and entity lists are loaded in GameScreen.init() now
        try {
            //set window properties
            Window.WINDOW_INSTANCE = new AppGameContainer(new SlickInitializer("PEngine - Test "
                    + "[\""+Assets.PROJECT_DIR+"\", \""+Assets.STARTING_LEVEL+"\"]"));
            Window.WINDOW_INSTANCE.setDisplayMode(820, 500, false);
            Window.WINDOW_INSTANCE.setFullscreen(false);
            Window.WINDOW_INSTANCE.setShowFPS(false);
            Window.WINDOW_INSTANCE.setVSync(true);
            Window.WINDOW_INSTANCE.setTargetFrameRate(60);
            Window.WINDOW_INSTANCE.setAlwaysRender(true);
            Window.WINDOW_INSTANCE.setResizable(true);
            //WINDOW_INSTANCE.setIcon("images/gui/favicon.png");
            Window.WINDOW_INSTANCE.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
