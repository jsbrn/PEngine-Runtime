package com.bitbucket.computerology.gui;

import com.bitbucket.computerology.gui.elements.*;
import com.bitbucket.computerology.main.SlickInitializer;
import com.bitbucket.computerology.sceneobjects.Animation;
import com.bitbucket.computerology.sceneobjects.Dialogue;
import com.bitbucket.computerology.sceneobjects.Script;
import com.bitbucket.computerology.world.Camera;
import java.util.ArrayList;

import com.bitbucket.computerology.world.Level;
import com.bitbucket.computerology.world.Player;
import com.bitbucket.computerology.sceneobjects.SceneObject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class GameScreen extends BasicGameState {
    
    public static Level CURRENT_LEVEL;
    public static ArrayList<String> OBJECT_TEXTURE_NAMES = new ArrayList<String>();
    public static ArrayList<Image> OBJECT_TEXTURES = new ArrayList<Image>();
    public static ArrayList<String> ANIMATION_TEXTURE_NAMES = new ArrayList<String>();
    public static ArrayList<Image> ANIMATION_TEXTURES = new ArrayList<Image>();
    public static ArrayList<String> AUDIO_NAMES = new ArrayList<String>();
    public static ArrayList<Sound> AUDIO = new ArrayList<Sound>();
    public static ArrayList<String> LEVEL_NAMES = new ArrayList<String>();
    public static ArrayList<Level> LEVELS = new ArrayList<Level>();
    
    public static ArrayList<String> FONT_NAMES = new ArrayList<String>();
    public static ArrayList<AngelCodeFont> FONTS = new ArrayList<AngelCodeFont>();
    public static AngelCodeFont DYNAMIC_FONT = null;
    
    public static double FPS = 0;
    
    static ArrayList<GUIElement> GUI = new ArrayList<GUIElement>();
    
    public static Dialogue CURRENT_DIALOGUE = null;
    public static SpeechBalloon CURRENT_SPEECH_BALLOON = null;
    public static ResponseMenu RESPONSE_MENU = null;
    
    boolean initialized = false;
    
    public static int DELTA_TIME = 0;
    
    public static Player PLAYER;
    Input input;

    public GameScreen(int state) {

    }
    
    public AngelCodeFont getFont(String name) {
        int index = FONT_NAMES.indexOf(name);
        if (index > -1) {
            return FONTS.get(index);
        }
        return null;
    }

    @Override
    public int getID() {
        return 0;
    }
    
    public static boolean enterLevel(String level_id) {
        System.out.println("Entering level: "+level_id);
        if (CURRENT_LEVEL != null) CURRENT_LEVEL.removeObject(PLAYER);
        int index = LEVEL_NAMES.indexOf(level_id);
        if (index > -1) {
            CURRENT_LEVEL = LEVELS.get(index);
            PLAYER.setWorldX(CURRENT_LEVEL.SPAWN_COORD[0]);
            PLAYER.setWorldY(CURRENT_LEVEL.SPAWN_COORD[1]);
            CURRENT_LEVEL.add(PLAYER);
            Camera.setTarget(CURRENT_LEVEL.CAM_COORD[0], CURRENT_LEVEL.CAM_COORD[1]);
            System.out.println("Entered!");
            if (CURRENT_LEVEL.HAS_BEEN_ENTERED == false) {
                CURRENT_LEVEL.runScript("on_first_enter", "");
            } else {
                CURRENT_LEVEL.runScript("on_enter", "");
                CURRENT_LEVEL.HAS_BEEN_ENTERED = true;
            }
            return true;
        }
        return false;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        if (initialized == false) {
            PLAYER = new Player();
            RESPONSE_MENU = new ResponseMenu(null);
            addGUIElement(RESPONSE_MENU);
            FONTS.add(new AngelCodeFont("fonts/small.fnt", "fonts/small.png")); FONT_NAMES.add("small");
            FONTS.add(new AngelCodeFont("fonts/normal.fnt", "fonts/normal.png")); FONT_NAMES.add("normal");
            FONTS.add(new AngelCodeFont("fonts/medium.fnt", "fonts/medium.png")); FONT_NAMES.add("medium");
            FONTS.add(new AngelCodeFont("fonts/large.fnt", "fonts/large.png")); FONT_NAMES.add("large");
            FONTS.add(new AngelCodeFont("fonts/x_large.fnt", "fonts/x_large.png")); FONT_NAMES.add("x_large");
            System.out.println("Loaded "+FONT_NAMES.size()+" fonts!");
        }
        initialized = true;
    }

    //draws state (screen) elements
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        
        if (Camera.ZOOM <= 4) {
           DYNAMIC_FONT = getFont("small");
        }
        if (Camera.ZOOM >= 5 && Camera.ZOOM <= 7) {
            DYNAMIC_FONT = getFont("normal");
        }
        if (Camera.ZOOM >= 8 && Camera.ZOOM <= 10) {
            DYNAMIC_FONT = getFont("medium");
        }
        if (Camera.ZOOM >= 11 && Camera.ZOOM <= 13) {
            DYNAMIC_FONT = getFont("large");
        }
        if (Camera.ZOOM >= 14) {
            DYNAMIC_FONT = getFont("x_large");
        }
        
        if (DYNAMIC_FONT == null) {g.setColor(Color.red);g.fillRect(0, 0, 5, 5);return;}
        
        if (CURRENT_LEVEL != null) {
            CURRENT_LEVEL.draw(g);
        }
        
        for (GUIElement e: GUI) {
            if (e != null) { e.draw(g); }
        }
        
        g.setFont(FONTS.get(0));
        g.setColor(Color.white);
        g.drawString("FPS: "+gc.getFPS()+" (Test build)", 10, 10);
    }
    
    public static void addGUIElement(GUIElement g) {
        if (GUI.contains(g) == false) {
            GUI.add(g);
        }
    }

    //key binding and calling update() in all objects
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
      
        if (CURRENT_LEVEL == null) {
            loadProject();
        }
        
        input = gc.getInput();
        FPS = gc.getFPS();
        DELTA_TIME = delta;
        
        for (GUIElement g: GUI) {
            if (g != null) {
                g.update();
            }
        }
       
        if (CURRENT_LEVEL != null) {
            Camera.update();
            CURRENT_LEVEL.update();

            if (RESPONSE_MENU.visible() == false) {
                if (input.isKeyDown(Input.KEY_D)) {
                    PLAYER.doCommand("move[30|0]");
                }
                if (input.isKeyDown(Input.KEY_A)) {
                    PLAYER.doCommand("move[-30|0]");
                }
            }
            if (input.isKeyPressed(Input.KEY_SPACE)) {
                if (CURRENT_DIALOGUE != null) {
                    if (CURRENT_SPEECH_BALLOON != null) {
                        if (CURRENT_SPEECH_BALLOON.visible()) {
                            CURRENT_SPEECH_BALLOON.getParent().CURRENT_DIALOGUE.next();
                            return;
                        }

                    }
                    if (RESPONSE_MENU.visible()) {
                        CURRENT_DIALOGUE.chooseOption(RESPONSE_MENU.getSelected());
                        return;
                    }
                } else {
                    PLAYER.doCommand("jump[]");
                }
                return;
            }
            if (input.isKeyPressed(Input.KEY_C)) {
                for (SceneObject o: CURRENT_LEVEL.ALL_OBJECTS) {
                    o.doCommand("dialogue[greetings]");
                }
            }
            if (input.isKeyPressed(Input.KEY_S)) {
                PLAYER.setWorldY(PLAYER.getWorldCoordinates()[1]+4);
            }
            if (input.isKeyPressed(Input.KEY_UP) || (input.isKeyPressed(Input.KEY_W) && RESPONSE_MENU.visible())) {
                RESPONSE_MENU.moveCursor(-1);
            }
            if (input.isKeyPressed(Input.KEY_DOWN) || (input.isKeyPressed(Input.KEY_S) && RESPONSE_MENU.visible())) {
                RESPONSE_MENU.moveCursor(1);
            }
        }
    }

    public void keyPressed(int key, char c) {
    }
    
    public static void loadProject() {
        LEVELS.clear();
        Properties prop = new Properties();
        
        try {
            FileInputStream f = new FileInputStream(SlickInitializer.PROJECT_FOLDER+"/project.txt");
            prop.load(f);
        } catch(Exception e) {}
        
        //load the player data and import it into the player object
        GameScreen.PLAYER = new Player();
        loadObject(-1, 0, true, prop).copyTo(PLAYER);
        GameScreen.PLAYER.LAYER = 2;
        
        //load levels
        int lcount = Integer.parseInt(prop.getProperty("LvlCount"));
        for (int i = 0; i != lcount; i++) {
            loadLevel(i, prop);
        }
        //load assets
        loadAssets();
        //enter the level
        if (SlickInitializer.STARTING_LEVEL.length() == 0) enterLevel(LEVELS.get(0).NAME);
            else enterLevel(SlickInitializer.STARTING_LEVEL);
    }
    
    static void loadLevel(int index, Properties prop) {
        Level level = new Level();
        String top_rgb = prop.getProperty("Lvl"+index+"TopBGColor");
        String bottom_rgb = prop.getProperty("Lvl"+index+"BottomBGColor");
        String amb_rbg = prop.getProperty("Lvl"+index+"AmbientColor");
        String spawn = prop.getProperty("Lvl"+index+"Spawn");
        String campos = prop.getProperty("Lvl"+index+"CameraPosition");
        ArrayList<Integer> t_rgb = parseIntegers(top_rgb), b_rgb = parseIntegers(bottom_rgb), 
                a_rgb = parseIntegers(amb_rbg), spwn = parseIntegers(spawn), cam_pos = parseIntegers(campos);
        level.R1 = t_rgb.get(0);level.G1 = t_rgb.get(1);level.B1 = t_rgb.get(2);
        level.R2 = b_rgb.get(0);level.G2 = b_rgb.get(1);level.B2 = b_rgb.get(2);
        level.R3 = a_rgb.get(0);level.G3 = a_rgb.get(1);level.B3 = a_rgb.get(2);
        level.AMBIENT_INTENSITY = Integer.parseInt(prop.getProperty("Lvl"+index+"AmbientIntensity"));
        level.AMBIENT_VOLUME = Float.parseFloat(prop.getProperty("Lvl"+index+"AmbientSoundVolume"));
        level.MUSIC_VOLUME = Float.parseFloat(prop.getProperty("Lvl"+index+"BGMusicVolume"));
        level.SPAWN_COORD = new int[]{spwn.get(0), spwn.get(1)};
        level.CAM_COORD = new int[]{cam_pos.get(0), cam_pos.get(1)};
        level.AMBIENT_SOUND = prop.getProperty("Lvl"+index+"AmbientSound");
        level.NAME = prop.getProperty("Lvl"+index+"ID");
        level.BG_MUSIC = prop.getProperty("Lvl"+index+"Music");
        level.ZOOM = Integer.parseInt(prop.getProperty("Lvl"+index+"Zoom"));
        level.WIDTH = Integer.parseInt(prop.getProperty("Lvl"+index+"Width"));
        level.HEIGHT = Integer.parseInt(prop.getProperty("Lvl"+index+"Height"));
        level.LOOP_BG_MUSIC = Boolean.parseBoolean(prop.getProperty("Lvl"+index+"LoopBGMusic"));
        level.LOOP_AMBIENT_SOUND = Boolean.parseBoolean(prop.getProperty("Lvl"+index+"LoopAmbientSound"));
        level.PLAY_BG_MUSIC_AUTOMATICALLY = Boolean.parseBoolean(prop.getProperty("Lvl"+index+"AutoBGMusic"));
        level.PLAY_AMBIENT_SOUND_AUTOMATICALLY = Boolean.parseBoolean(prop.getProperty("Lvl"+index+"AutoAmbientSound"));

        int obj_count = Integer.parseInt(prop.getProperty("Lvl"+index+"ObjectCount"));
        for (int i = 0; i != obj_count; i++) {
            SceneObject o = loadObject(index, i, false, prop);
            if (o != null) level.add(o);
        }

        //load level scripts
        int script_count = Integer.parseInt(prop.getProperty("Lvl"+index+"ScrCount"));
        for (int ii = 0; ii != script_count; ii++) {
            Script s = new Script(null);
            level.SCRIPTS.add(s);
            s.NAME = prop.getProperty("Lvl"+index+"Scr"+ii+"Name");
            s.CONTENTS = parseString(prop.getProperty("Lvl"+index+"Scr"+ii+"Content"));
        }
        
        LEVELS.add(level);
        LEVEL_NAMES.add(level.NAME);

    }
    
    static SceneObject loadObject(int lindex, int oindex, boolean gallery, Properties prop) {
        String prefix = "Lvl"+lindex;
        if (gallery) { prefix = "Gall"; }
        System.out.println("Loading object "+(prefix+"Obj"+oindex)+", gallery = "+true+", level = "+lindex);
        SceneObject o = new SceneObject();
        String pos = prop.getProperty(prefix+"Obj"+oindex+"Pos");
        String dim = prop.getProperty(prefix+"Obj"+oindex+"Dim");
        ArrayList<Integer> position = parseIntegers(pos), dimensions = parseIntegers(dim);
        o.setWidth(dimensions.get(0));
        o.setHeight(dimensions.get(1));
        o.setWorldX(position.get(0)-(dimensions.get(0)/2));
        o.setWorldY(position.get(1)-(dimensions.get(1)/2));
        o.NAME = prop.getProperty(prefix+"Obj"+oindex+"Name");
        o.TEXTURE_NAME = prop.getProperty(prefix+"Obj"+oindex+"Img").replace(".png", "");
        o.LAYER = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"Layer"));
        o.GRAVITY = Boolean.parseBoolean(prop.getProperty(prefix+"Obj"+oindex+"Grav"));
        o.COLLIDES = Boolean.parseBoolean(prop.getProperty(prefix+"Obj"+oindex+"Coll"));
        //o.CLASS = prop.getProperty(prefix+"Obj"+oindex+"Type"); THE GAME DOESNT NEED TO KNOW THE CLASS OF THE OBJECT
        
        int anim_count = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"AnimCount"));
        for (int ii = 0; ii != anim_count; ii++) {
            Animation s = new Animation(o);
            o.ANIMATIONS.add(s);
            s.NAME = prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Name");
            s.SPRITE_NAME = prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"SpriteName");
            s.WIDTHS = parseIntegers(prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Widths"));
            s.HEIGHTS = parseIntegers(prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Heights"));
            s.LOOP = Boolean.parseBoolean(prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Loops"));
            s.DURATIONS = parseIntegers(prop.getProperty(prefix+"Obj"+oindex+"Anim"+ii+"Durs"));
        }
        int dialogue_count = Integer.parseInt(prop.getProperty(prefix+"Obj"+oindex+"DlgCount"));
        for (int ii = 0; ii != dialogue_count; ii++) {
            Dialogue d = new Dialogue(o);
            o.DIALOGUES.add(d);
            d.NAME = prop.getProperty(prefix+"Obj"+oindex+"Dlg"+ii+"Name");
            d.OBJECT_SPEECH = parseString(prop.getProperty(prefix+"Obj"+oindex+"Dlg"+ii+"Spch"));
            d.PLAYER_RESPONSES = parseString(prop.getProperty(prefix+"Obj"+oindex+"Dlg"+ii+"PResps"));
            d.EVENT_VALUES = parseString(prop.getProperty(prefix+"Obj"+oindex+"Dlg"+ii+"EvtVals"));
            d.EVENT_TYPES = parseString(prop.getProperty(prefix+"Obj"+oindex+"Dlg"+ii+"EvtTypes"));
        }
        return o;
    }
    
    private static void addTexturesToAssets(File textures, ArrayList<String> names, ArrayList<Image> imgs) {
        if (textures.isDirectory()) {
            File[] files = textures.listFiles();
            for (File f: files) {
                if (f.getName().contains(".png")) {
                    BufferedImage img;
                    try {
                        imgs.add(new Image(f.getAbsolutePath(), false, Image.FILTER_NEAREST));
                        names.add(f.getName().replace(".png", ""));
                        System.out.println("Loaded texture "+names.get(names.size()-1)+".png");
                    } catch (SlickException e) {
                        JOptionPane.showMessageDialog(null, "Error loading assets!\n"+e.getLocalizedMessage());
                        names.clear();
                        imgs.clear();
                        break;
                    }
                }
            }
        }
    }
    
    private static void addAudioToAssets(File textures, ArrayList<String> names, ArrayList<Sound> audio) {
        if (textures.isDirectory()) {
            File[] files = textures.listFiles();
            for (File f: files) {
                if (f.getName().contains(".ogg")) {
                    //load audio from folder and fill the array
                }
            }
        }
    }
    
    private static void loadAssets() {
        addTexturesToAssets(new File(SlickInitializer.PROJECT_FOLDER+"/assets/textures/objects"), OBJECT_TEXTURE_NAMES, OBJECT_TEXTURES);
        addTexturesToAssets(new File(SlickInitializer.PROJECT_FOLDER+"/assets/textures/animations"), ANIMATION_TEXTURE_NAMES, ANIMATION_TEXTURES);
        //addAudioToAssets
    }
    
    /**
     * Takes a String consisting of a set of integers separated by spaces, and returns a filled ArrayList
     * containing said integers. If the String could not be parsed (ex. has invalid characters), it returns
     * an ArrayList with 420 elements, all with a value of 0.
     * @param s The String to read.
     * @param verbose Should a message dialog be opened if an error occurs?
     * @return An ArrayList<Integer> (see above).
     */
    public static ArrayList<Integer> parseIntegers(String s) {
        ArrayList<Integer> values = new ArrayList<Integer>();
        boolean stop = false;
        if (s != null) {
            if (s.length() == 0) {
                stop = true;
            }
        } else {
            stop = true;
        }
        if (stop) {
            for (int i = 0; i != 420; i++) {
                values.add(0);
            }
            return values;
        }
        try {
            int val = 0;
            for (int i = 0; i != s.length(); i++) {
                char c = s.charAt(i);
                if (c == ' ') {
                    values.add(val);
                    val = 0;
                } else {
                    val *= 10;
                    val += Integer.parseInt(s.charAt(i)+"");
                    if (i == s.length()-1) {
                        values.add(val);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            for (int i = 0; i != 420; i++) {
                values.add(0);
            }
        }
        return values;
    }
    
    public static ArrayList<String> parseString(String s) {
        ArrayList<String> strs = new ArrayList<String>();
        String command = "";
        System.out.println("Parsing string: "+s);
        for (int i = 0; i != s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\n') {
                strs.add(command);
                command = "";
            } else {
                command += c;
                if (i == s.length()-1) {
                    strs.add(command);
                }
            }
        }
        System.out.println("Size of generated array: "+strs.size());
        return strs;
    }
    
    /**
     * Returns a String object containing every element in the specified array, separated by "\n". Used for writing to the save file.
     * @param str_arr
     * @return A String object.
     */
    public static String mergeStrings(ArrayList<String> str_arr) {
        String c = "";
        for (String s: str_arr) {
            c+=s+"\n";
        }
        return c.trim();
    }
    
    public static String integersToString(ArrayList<Integer> int_arr) {
        String c = "";
        for (int s: int_arr) {
            c+=s+" ";
        }
        return c.trim();
    }
    
}
