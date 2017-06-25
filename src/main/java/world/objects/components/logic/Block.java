package world.objects.components.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import misc.Assets;
import misc.MiscMath;
import misc.Types;
import world.World;
import world.objects.components.logic.handlers.BlockHandler;

public class Block {
    
    public static final int NODE_COUNT = 4, NODE_IN = 0, NODE_OUT = 1, NODE_YES = 2, NODE_NO = 3;
    
    private boolean[] nodes; //in, out, yes, no
    private int[][] conns;
    private Object[][] inputs, outputs;
    private Flow parent;
    private String name, type, summary;
    private int id;
    private int x = 50, y = 50;
    
    private BlockHandler handler;

    /**
     * Creates a new Block from the specified template block in Assets.
     * @param type The type/class of the block.
     * @return null if no block by that type, else the new Block.
     */
    public static Block create(String type) {
        Block template = Assets.getBlock(type);
        if (template == null) {
            System.err.println("Cannot find a block by type \""+type+"\"");
            return null;
        }
        Block new_b = new Block();
        template.copyTo(new_b, false);
        return new_b;
    }
    
    public Block() {
        this.id = Math.abs(new Random().nextInt()-1)+1;
        this.name = "";
        this.type = "";
        this.nodes = new boolean[NODE_COUNT];
        this.conns = new int[NODE_COUNT][2];
    }
    
    public void update() { 
        if (handler != null) {
            handler.update();
        }
    }
    
    public BlockHandler getHandler() { return handler; }
    
    public Object resolveInput(int i) {
        String input = (String)inputs[i][2];
        int t = (Integer)(inputs[i][1]);
        int t_ = Types.getType(input);
        if (t_ == Types.NUMBER) return Types.parseNumber(input);
        if (t_ == Types.TEXT) return Types.parseText(input);
        if (t_ == Types.BOOLEAN) return Types.parseBoolean(input);
        if (t_ == Types.ANIM) return Types.parseAnimation(World.getWorld().getCurrentLevel(), getParent().getParent(), input);
        if (t_ == Types.FLOW) return Types.parseFlow(World.getWorld().getCurrentLevel(), getParent().getParent(), getParent(), input);
        if (t_ == Types.LEVEL) return Types.parseLevel(World.getWorld().getCurrentLevel(), input);
        if (t_ == Types.OBJECT) return Types.parseObject(World.getWorld().getCurrentLevel(), getParent().getParent(), input);
        if (t_ == Types.ASSET) return Types.parseAsset(input);
        if (t_ == Types.VARIABLE) return getParent().getVar(input);
        
        System.err.println("Cannot resolve input \""+input+"\"");
        return null;
    }
    
    public Flow getParent() { return parent; }
    
    /**
     * Creates a new flowchart block.
     * @param name The title visible in the menu and in the editor.
     * @param type The internal identifier class of this block.
     * @param node_str A string ("tttt", "ffff", etc.) that determines which nodes are enabled.
     * @param inputs The inputs (a list of {name, type} object arrays). null will initialize to an empty array.
     * @param outputs See inputs.
     */
    public Block(String name, String summary, String type, String node_str, Object[][] inputs, Object[][] outputs) {
        this.id = Math.abs(new Random().nextInt()-1)+1;
        this.name = name;
        this.type = type;
        this.summary = summary == null ? "" : summary;
        this.nodes = MiscMath.toBooleanArray(node_str.replace("t", "true ").replace("f", "false "));
        this.conns = new int[NODE_COUNT][2];
        this.inputs = inputs != null ? inputs : new Object[0][3];
        this.outputs = outputs != null ? outputs : new Object[0][3];
    }
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("b\n");
            bw.write("t="+type+"\n");
            bw.write("id="+id+"\n");
            bw.write("x="+x+"\n");
            bw.write("y="+y+"\n");
            String c = ""; for (int[] conn: conns) c += conn[0]+" "+conn[1]+" ";
            bw.write("conns="+c+"\n");
            c = "";
            for (Object[] input: inputs) c += "{"+(String)input[2]+"}";
            bw.write("inputs="+c+"\n");
            c = ""; for (Object[] output: outputs) c += "{"+(String)output[2]+"}";
            bw.write("outputs="+c+"\n");
            bw.write("/b\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean load(BufferedReader br) {
        System.out.println("Loading block...");
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.equals("/b")) return true;
                if (line.indexOf("t=") == 0) { 
                    Block template = Assets.getBlock(line.substring(2));
                    if (template != null) {
                        template.copyTo(this, false);
                        System.out.println("Block: "+name+" ("+type+")");
                    }
                }
                if (line.indexOf("id=") == 0) id = Integer.parseInt(line.trim().replace("id=", ""));
                if (line.indexOf("x=") == 0) x = Integer.parseInt(line.trim().replace("x=", ""));
                if (line.indexOf("y=") == 0) y = Integer.parseInt(line.trim().replace("y=", ""));
                if (line.indexOf("conns=") == 0) {
                    String[] conns_list = line.substring(6).split(" ");
                    for (int i = 0; i < conns.length; i++) {
                        conns[i][0] = Integer.parseInt(conns_list[i*2]);
                        conns[i][1] = Integer.parseInt(conns_list[(i*2)+1]);
                    }
                }
                if (line.indexOf("inputs=") == 0) {
                    String[] inputs_list = line.substring(7).split("\\}\\{");
                    for (int i = 0; i < inputs_list.length; i++) {
                        if (i >= inputs.length) break;
                        inputs[i][2] = inputs_list[i].replaceAll("[{}]", "");
                    }
                }
                if (line.indexOf("outputs=") == 0) {
                    String[] outputs_list = line.substring(8).split("\\}\\{");
                    for (int i = 0; i < outputs_list.length; i++) {
                        if (i >= outputs.length) break;
                        outputs[i][2] = outputs_list[i].replaceAll("[{}]", "");
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Sets the ID of the block to i. This will break any connections referencing the old ID.
     * Should only be used in saving/loading block data.
     */
    public void setID(int i) {
        id = i;
    }
    
    public void setParent(Flow f) {
        parent = f;
    }
    
    public void setHandler(BlockHandler bh) {
        handler = bh;
    }
    
    public String getType() { return type; }
    
    public void setX(int new_x) { x = new_x; }
    public void setY(int new_y) { y = new_y; }
    
    public int getID() { return id; }
    
    public int[] getCoords() { return new int[]{x, y}; }
    
    public int inputCount() { return inputs.length; }
    public int outputCount() { return outputs.length; }
    
    public Object[] getInput(int index) {
        return inputs[index];
    }
    
    public Object[] getOutput(int index) {
        return outputs[index];
    }
    
    public int getConn(int index) {
        return conns[index][0];
    }
    
    public void randomID() {
        this.id = Math.abs(new Random().nextInt());
    }
    
    public void copyTo(Block b, boolean copy_id) {
        b.name = name;
        b.type = type;
        b.summary = summary;
        if (copy_id) b.id = id;
        b.x = x; b.y = y;
        b.nodes = new boolean[nodes.length];
        b.conns = new int[conns.length][2];
        b.inputs = new Object[inputs.length][3];
        b.outputs = new Object[outputs.length][3];
        System.arraycopy(nodes, 0, b.nodes, 0, nodes.length);
        for (int i = 0; i < conns.length; i++) b.conns[i] = new int[]{conns[i][0], conns[i][1]};
        for (int i = 0; i < b.inputs.length; i++) b.inputs[i] = 
                new Object[]{inputs[i][0], inputs[i][1], inputs[i][2]};
        for (int i = 0; i < b.outputs.length; i++) b.outputs[i] = 
                new Object[]{outputs[i][0], outputs[i][1], outputs[i][2]};
        b.handler = Assets.createBlockHandler(b.type);
        b.handler.setParent(b);
    }
    
    protected void clean() {
        for (int[] conn: conns) if (parent.getBlockByID(conn[0]) == null) { conn[0] = 0; conn[1] = 0; }
    }
    
}