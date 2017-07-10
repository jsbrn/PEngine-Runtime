package world;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import misc.Assets;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import world.events.EventManager;
import world.objects.SceneObject;

public class World {
    
    private static World world;
    private ArrayList<Level> levels;
    
    private SceneObject player;
    
    private Level current_level, home_level;
    
    private World() {
        this.levels = new ArrayList<Level>();
    }
    
    public void setHomeLevel(Level l) {
        if (containsLevel(l.getName())) home_level = l;
    }

    public Level getHomeLevel() {
        return home_level;
    }
    
    public static World getWorld() { return world; }
    public static void newWorld() { world = new World(); }
    
    public boolean containsLevel(String level_name) {
        for (Level l: levels) {
            if (l.getName().equals(level_name)) return true;
        }
        return false;
    }
    
    public void addLevel(Level l) {
        levels.add(l);
    }
    
    public Level getCurrentLevel() { return current_level; }
    
    public void switchToLevel(Level l) { switchToLevel(l.getName()); }
    
    public void switchToLevel(String level_id) {
        Level old = current_level;
        for (Level l: getLevels()) {
            if (l.getName().equals(level_id)) {
                current_level = l;
                boolean target_player = l.cameraSpawn()[0] == l.playerSpawn()[0]
                        && l.cameraSpawn()[1] == l.playerSpawn()[1];
                Camera.setX(l.cameraSpawn()[0]); Camera.setY(l.cameraSpawn()[1]);
                Camera.setZoom(l.getZoom());
                if (old != null) old.removeObject(player);
                if (l.allowPlayer()) {
                    l.add(player);
                    player.setWorldX(l.playerSpawn()[0]);
                    player.setWorldY(l.playerSpawn()[1]);
                    if (target_player) Camera.setTarget(player);
                }
                System.out.println("Switched to level: "+l.getName());
            }
        }
    }
    
    public boolean save() {
        System.err.println("Saving not implemented.");
        return false;
    }
    
    public void loadProject() {
        File f = new File(Assets.PROJECT_DIR+"/game.txt");
        if (!f.exists()) {
            System.err.println("Could not load project!");
            return;
        }
        FileReader fr;
        System.out.println("Loading from file: " + f.getAbsoluteFile().getAbsolutePath());
        try {
            fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while (true) {
                String line = br.readLine();
                
                if (line == null) break;
                line = line.trim();
                
                if (line.equals("l")) {
                    Level l = new Level();
                    if (l.load(br)) addLevel(l);
                }
                
                if (line.equals("player")) {
                    player = new SceneObject();
                    if (player.load(br)) System.out.println("Loaded player data!");
                }
                
                if (line.indexOf("home=") == 0) setHomeLevel(getLevel(line.substring(5)));

            }
            br.close();
            //switch to the starting level specified at launch, or home if no starting level specified
            switchToLevel(Assets.STARTING_LEVEL.length() > 0 ? Assets.STARTING_LEVEL : home_level.getName());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public ArrayList<Level> getLevels() {
        return levels;
    }
    
    public Level getLevel(int i) {
        return i > -1 && i < levels.size() ? levels.get(i) : null;
    }
    
    public Level getLevel(String level_id) {
        for (Level l: levels) {
            if (l.getName().equals(level_id)) {
                return l;
            }
        }
        return null;
    }
    
    
    public void update() {
        if (current_level != null) current_level.update();
        EventManager.clear();
    }
    
    public void draw(Graphics g) {
        Camera.update();
        if (current_level != null) current_level.draw(g);
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
