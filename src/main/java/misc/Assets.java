package misc;

import java.io.File;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.*;
import world.objects.components.logic.handlers.events.*;
import world.objects.components.logic.handlers.general.*;
import world.objects.components.logic.handlers.gui.*;
import world.objects.components.logic.handlers.level.*;
import world.objects.components.logic.handlers.object.*;
import world.objects.components.logic.handlers.operations.*;

public class Assets {
    
    public static String USER_HOME, PROJECT_DIR, STARTING_LEVEL;
    private static HashMap asset_map;
    
    private static Block[] blocks;
    
    public static int size() { return blocks.length; }
    public static Block getBlock(int index) {
        if (index > -1 && index < blocks.length) return blocks[index];
        return null;
    }
    
    public static Block getBlock(String type) {
        for (Block b: blocks)
            if (b.getType().equals(type)) return b;
        return null;
    }
    public static Block[] getBlocks() { return blocks; }
    
    /**
     * Loads all assets from the project's assets folder. Clears all previously loaded assets
     * first. Should be called on every new project load.
     */
    public static void load() {
        if (asset_map == null) asset_map = new HashMap();
        asset_map.clear();
        File assets = new File(PROJECT_DIR+"/assets");
        loadFromExternalFolder(assets);
        initBlockList();
        System.out.println("Loaded "+asset_map.size()+" assets.");
    }
    
    public static void delete(File asset_or_folder) {
        if (asset_or_folder.isDirectory()) {
            File[] list = asset_or_folder.listFiles();
            for (File f: list) {
                if (f.isDirectory()) delete(f); else f.delete();
            }
        }
        asset_or_folder.delete();
    }
    
    public static int assetCount() { return asset_map.size(); }
    
    private static void initBlockList() {   
        blocks = new Block[16];
        blocks[0] = new Block("Start", "@id", "s", "ftff", 
                new Object[][]{{"id", Types.TEXT, ""}}, null);
        blocks[1] = new Block("Wait", "@duration", "w", "ttff", 
                new Object[][]{{"duration", Types.NUMBER, ""}}, null);
        blocks[2] = new Block("Print to console", "@message", "p", "ttff", 
                new Object[][]{{"message", Types.ANY, ""}}, null);
        blocks[3] = new Block("Switch to level", "@level", "stl", "ttff", 
                new Object[][]{{"level", Types.LEVEL, ""}}, null);
        blocks[4] = new Block("Set animation", "Set @object animation to @animation", "sa", "ttff", 
                new Object[][]{{"object", Types.OBJECT, "Object()"}, {"animation", Types.ANIM, ""}}, null);
        blocks[5] = new Block("Add", "@number1 + @number2", "adn", "ttff", 
                new Object[][]{{"number1", Types.NUMBER, ""}, {"number2", Types.NUMBER, ""}}, new Object[][]{{"sum", Types.NUMBER, ""}});
        blocks[6] = new Block("Set variable", "Set @var to @value", "sv", "ttff", 
                new Object[][]{{"value", Types.ANY, ""}}, new Object[][]{{"var", Types.ANY, ""}});
        blocks[7] = new Block("Add force", "Add force @name to @object: @angle degrees, @magnitude px/s", "af", "ttff", 
                new Object[][]{{"object", Types.OBJECT, "Object()"}, {"name", Types.TEXT, ""}, 
                    {"angle", Types.NUMBER, "0"}, {"acceleration", Types.NUMBER, "0"}, {"magnitude", Types.NUMBER, "0"}}, null);
        blocks[8] = new Block("Remove force", "Remove force @name from @object", "rf", "ttff", 
                new Object[][]{{"object", Types.OBJECT, "Object()"}, {"name", Types.TEXT, ""}}, null);
        blocks[9] = new Block("Is key pressed", "@key", "ikp", "tftt", 
                new Object[][]{{"key", Types.TEXT, ""}}, null);
        blocks[10] = new Block("Await key press", "@key", "akp", "ttff", 
                new Object[][]{{"key", Types.TEXT, ""}}, null);
        blocks[11] = new Block("Await key release", "@key", "akr", "ttff", 
                new Object[][]{{"key", Types.TEXT, ""}}, null);
        blocks[12] = new Block("Await collision", "Between @object1 & @object2", "ac", "ttff", 
                new Object[][]{{"object1", Types.OBJECT, ""}, {"object2", Types.OBJECT, ""}}, null);
        blocks[13] = new Block("If objects intersect", "@object1 & @object2", "ioac", "tftt", 
                new Object[][]{{"object1", Types.OBJECT, ""}, {"object2", Types.OBJECT, ""}}, null);
        blocks[14] = new Block("Say", "@object says: @message", "say", "ttff", 
                new Object[][]{{"object", Types.OBJECT, "Object()"}, {"message", Types.TEXT, ""},
                    {"lifespan (mills)", Types.NUMBER, "1000"}, {"require keypress", Types.BOOLEAN, "false"}}, null);
        blocks[15] = new Block("Await player choice", "@choices", "apc", "ttff", 
                new Object[][]{{"choices", Types.TEXT_LIST, "List(\"Choice 1\", \"Choice 2\")"}}, 
                new Object[][]{{"choice", Types.NUMBER, ""}});
    }
    
    public static BlockHandler createBlockHandler(String block_type) {
        if ("s".equals(block_type)) return new StartHandler();
        if ("w".equals(block_type)) return new WaitHandler();
        if ("p".equals(block_type)) return new PrintHandler();
        if ("stl".equals(block_type)) return new SwitchLevelHandler();
        if ("sa".equals(block_type)) return new SetAnimationHandler();
        if ("adn".equals(block_type)) return new AddNumbersHandler();
        if ("sv".equals(block_type)) return new SetVariableHandler();
        if ("af".equals(block_type)) return new AddForceHandler();
        if ("rf".equals(block_type)) return new RemoveForceHandler();
        if ("ikp".equals(block_type)) return new IsKeyDownHandler();
        if ("akp".equals(block_type)) return new AwaitKeyPressedHandler();
        if ("akr".equals(block_type)) return new AwaitKeyReleasedHandler();
        if ("ac".equals(block_type)) return new AwaitCollisionHandler();
        if ("ioac".equals(block_type)) return new IfObjectsIntersectHandler();
        if ("say".equals(block_type)) return new SayHandler();
        if ("apc".equals(block_type)) return new AwaitPlayerChoiceHandler();
        return null;
    }
    
    public static Object get(String key) {
        key = key.replaceAll("[/\\\\]", File.separator);
        if (!asset_map.containsKey(key)) return null;
        return asset_map.get(key);
    }
    
    private static void loadFromExternalFolder(File assets) {
        if (assets.isDirectory()) {
            File[] files = assets.listFiles();
            for (File f: files) {
                if (f.isDirectory()) {
                    loadFromExternalFolder(f);
                } else {
                    if (f.getName().contains(".png")) {
                        Image img;
                        try {
                            img = new Image(f.getAbsolutePath(), false, Image.FILTER_NEAREST);
                            String key = f.getAbsolutePath().replace(assets.getAbsolutePath()+File.separator, "")
                                    .replaceAll("[/\\\\]", File.separator); //sanitize
                            asset_map.put(key, img);
                            System.out.println("Loaded asset "+key+"!");
                        } catch (SlickException e) {
                            JOptionPane.showMessageDialog(null, "Error loading assets!\n"+e.getLocalizedMessage());
                            break;
                        }
                    }
                }
            }
        }
    }

}
