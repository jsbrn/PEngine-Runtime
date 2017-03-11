package com.bitbucket.computerology.main;

import com.bitbucket.computerology.gui.GameScreen;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class SlickInitializer extends StateBasedGame {
    //the name of the window
    public static final String WINDOW_TITLE = "Twelve Hundred Seconds", ROOT_FOLDER_NAME = "ths";
    //set ids to the states
    public static final int GAME_SCREEN = 0;
    public static int UPDATE_ID = 10;
    boolean initialized = false;
    
    public static String PROJECT_FOLDER = "", STARTING_LEVEL = "";

    //create a window object
    public static AppGameContainer WINDOW_INSTANCE;

    public SlickInitializer(String gameTitle) {

        super(gameTitle); //set window title to "gameTitle" string

        //add states
        addState(new GameScreen(GAME_SCREEN));
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        if (initialized == false) {
            //initialize states
            getState(GAME_SCREEN).init(gc, this);
            //load "menu" state onscreen
            this.enterState(GAME_SCREEN);
            initialized = true;
        }
    }

    public static void main(String args[]) throws IOException {
        
        JOptionPane.showMessageDialog(null, "Here is a list of everything that is not implemented:\n"
                + "- audio/sounds\n"
                + "- on_collide/on_touch event scripts\n");
        
        //The arguments can be: asset_folder level_id
        //or just asset_folder (in which it will not load a level)
        
        if (args.length == 1) {
            System.out.println(args[0]);
            PROJECT_FOLDER = args[0];
        } else if (args.length == 2) {
            System.out.println(args[0]+" "+args[1]);
            PROJECT_FOLDER = args[0];
            STARTING_LEVEL = args[1];
        }
        setLocalVersion(); //update the client data so the launcher can check for updates
        createRootDirectory(); //make a home for the save files
        //the textures and entity lists are loaded in GameScreen.init() now
        try {
            //set window properties
            WINDOW_INSTANCE = new AppGameContainer(new SlickInitializer(WINDOW_TITLE));
            WINDOW_INSTANCE.setDisplayMode(820, 500, false);
            WINDOW_INSTANCE.setFullscreen(false);
            WINDOW_INSTANCE.setShowFPS(false);
            WINDOW_INSTANCE.setVSync(false);
            WINDOW_INSTANCE.setTargetFrameRate(60);
            WINDOW_INSTANCE.setAlwaysRender(true);
            WINDOW_INSTANCE.setResizable(true);
            //WINDOW_INSTANCE.setIcon("images/gui/favicon.png");
            WINDOW_INSTANCE.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public static void createRootDirectory() {
        File root = new File(System.getProperty("user.home")+"/"+ROOT_FOLDER_NAME+"/");
        if (root.exists() == false) {
            root.mkdir();
        }
    }

    public static void setLocalVersion() {
        //register the local version so the level editor can compare to the most recent update
        Properties prop = new Properties();
        try {
            prop.setProperty("updateID", ""+UPDATE_ID); //make sure this matches the server's updateID
            prop.store(
                    new FileOutputStream(System.getProperty("user.home") + "/level_editor/jars/test.properties"), null);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
