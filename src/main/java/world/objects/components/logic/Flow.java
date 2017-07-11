package world.objects.components.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import misc.Types;
import world.objects.SceneObject;

public class Flow {
        
    private ArrayList<Block> blocks;
    private HashMap<String, Object> vars, starts;
    private Block current_block;
    private String name;
    private SceneObject parent;
    
    public Flow() {
        this.blocks = new ArrayList<Block>();
        this.name = "";
        this.vars = new HashMap();
        this.starts = new HashMap();
    }
    
    public boolean varExists(String var) { return vars.containsKey(var); }
    public void setVar(String var, Object value) { vars.put(var, value); }
    public Object getVar(String var) { return vars.get(var); }
    
    public void setParent(SceneObject o) { parent = o; }
    public SceneObject getParent() { return parent; }
    
    public String getName() { return name; }
    public void setName(String new_) { name = new_; }
    
    public boolean start(String start) {
        Block s = (Block)starts.get(start);
        if (s != null) current_block = s;
        return s != null;
    }
    
    public void stop() {
        current_block = null;
    }
    
    public void update() {
        int count = 0; Block last_block = current_block;
        while (count++ < 10) {
            last_block = current_block;
            if (current_block != null) {
                current_block.update();
            } else { break; }
        }
    }
    
    public boolean goTo(int block_id) {
        if (current_block != null) current_block.getHandler().clean();
        Block next = getBlockByID(block_id);
        current_block = next;
        if (next != null) current_block.getHandler().init();
        return next != null;
    }
    
    public boolean finished() { return current_block == null; }
    
    public Block getBlock(int index) {
        return blocks.get(index);
    }
    
    public int blockCount() {
        return blocks.size();
    }
    
    public int indexOf(Block b) {
        return blocks.indexOf(b);
    }
    
    public void addBlock(Block b) {
        if (blocks.contains(b) == false) {
            blocks.add(b);
            if (b.getType().equals("s")) starts.put(Types.parseText((String)b.getInput(0)[2]), b);
            b.setParent(this);
        } else return;
    }
    
    public Block getBlockByID(int id) {
        for (Block b: blocks) {
            if (b.getID() == id) {
                return b;
            }
        }
        return null;
    }
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("f\n");
            bw.write("id="+name+"\n");
            for (Block b: blocks) b.save(bw);
            bw.write("/f\n");
        } catch (IOException ex) {
            Logger.getLogger(Flow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean load(BufferedReader br) {
        System.out.println("Loading flow...");
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                if (line.equals("/f")) return true;
                if (line.indexOf("id=") == 0) name = line.trim().replace("id=", "");
                if (line.equals("b")) {
                    Block b = new Block();
                    if (b.load(br)) addBlock(b);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Flow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Flow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * Copies the contents of this flow to flow F.
     * @param f The flow, stupid.
     */
    public void copyTo(Flow f, boolean copy_name) {
        if (copy_name) f.name = name;
        f.blocks.clear();
        for (Block b: blocks) {
            Block new_b = new Block();
            b.copyTo(new_b, true);
            f.addBlock(new_b);
        }
    }
    
}
