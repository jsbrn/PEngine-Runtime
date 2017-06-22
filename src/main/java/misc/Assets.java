package misc;

import java.io.File;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.StartHandler;

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
        blocks = new Block[1];
        blocks[0] = new Block("Start", "@id", "s", "ttff", 
                new Object[][]{{"id", Types.TEXT, ""}}, null);
        blocks[0].setHandler(new StartHandler());
        blocks[1] = new Block("Wait", "@duration", "s", "ttff", 
                new Object[][]{{"duration", Types.NUMBER, ""}}, null);
        blocks[2] = new Block("Log", "@message", "s", "ttff", 
                new Object[][]{{"message", Types.TEXT, ""}}, null);
        blocks[3] = new Block("Switch to level", "@level", "stl", "ttff", 
                new Object[][]{{"level", Types.LEVEL, ""}}, null);
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
