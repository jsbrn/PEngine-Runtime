package misc;

import java.io.File;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;
import world.objects.components.logic.handlers.general.PrintHandler;
import world.objects.components.logic.handlers.general.SetVariableHandler;
import world.objects.components.logic.handlers.general.StartHandler;
import world.objects.components.logic.handlers.general.WaitHandler;
import world.objects.components.logic.handlers.level.SwitchLevelHandler;
import world.objects.components.logic.handlers.object.AddForceHandler;
import world.objects.components.logic.handlers.object.RemoveForceHandler;
import world.objects.components.logic.handlers.object.SetAnimationHandler;
import world.objects.components.logic.handlers.operations.AddNumbersHandler;

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
        blocks = new Block[9];
        blocks[0] = new Block("Start", "@id", "s", "ftff", 
                new Object[][]{{"id", Types.TEXT, ""}}, null);
        blocks[1] = new Block("Wait", "@duration", "w", "ttff", 
                new Object[][]{{"duration", Types.NUMBER, ""}}, null);
        blocks[2] = new Block("Print to console", "@message", "p", "ttff", 
                new Object[][]{{"message", Types.TEXT, ""}}, null);
        blocks[3] = new Block("Switch to level", "@level", "stl", "ttff", 
                new Object[][]{{"level", Types.LEVEL, ""}}, null);
        blocks[4] = new Block("Set animation", "Set @object animation to @animation", "sa", "ttff", 
                new Object[][]{{"object", Types.OBJECT, ""}, {"animation", Types.ANIM, ""}}, null);
        blocks[5] = new Block("Add", "@number1 + @number2", "adn", "ttff", 
                new Object[][]{{"number1", Types.NUMBER, ""}, {"number2", Types.NUMBER, ""}}, new Object[][]{{"sum", Types.NUMBER, ""}});
        blocks[6] = new Block("Set variable", "Set @var to @value", "sv", "ttff", 
                new Object[][]{{"value", Types.ANY, ""}}, new Object[][]{{"var", Types.ANY, ""}});
        blocks[7] = new Block("Add force", "Add force @name to @object: @angle degrees, @magnitude px/s", "af", "ttff", 
                new Object[][]{{"object", Types.OBJECT, ""}, {"name", Types.TEXT, ""}, 
                    {"angle", Types.NUMBER, ""}, {"acceleration", Types.NUMBER, ""}, {"magnitude", Types.NUMBER, ""}}, null);
        blocks[8] = new Block("Remove force", "Remove force @name from @object", "rf", "ttff", 
                new Object[][]{{"object", Types.OBJECT, ""}, {"name", Types.TEXT, ""}}, null);
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
