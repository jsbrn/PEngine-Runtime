package com.bitbucket.computerology.misc;

import com.bitbucket.computerology.gui.GameScreen;
import com.bitbucket.computerology.world.Camera;
import com.bitbucket.computerology.world.SceneObject;
import java.util.ArrayList;

public class Script {
    
    public String NAME;
    String param;
    public ArrayList<String> CONTENTS;
    
    double wait_timer = 0;
    double target_x = -1, target_y = -1;
    
    int index = 0;
    
    SceneObject parent;
    
    static CommandList COMMAND_LIST;
    
    public Script(SceneObject parent) {
        this.parent = parent;
        this.NAME = "new_script";
        this.CONTENTS = new ArrayList<String>();
        if (COMMAND_LIST == null) { COMMAND_LIST = new CommandList(); }
    }
    
    public void copyTo(Script new_s) {
        new_s.NAME = NAME; 
        new_s.setContent(GameScreen.mergeStrings(CONTENTS));
    }
    
    public void setContent(String s) {
        CONTENTS = GameScreen.parseString(s);
    }
    
    public void reset() {
        index = 0;
        param = "";
    }
    
    public void setParam(String p) {
        param = p;
    }
    
    public void processCommand(String line) {
        int lb_index = line.indexOf("[");if (lb_index <= 0 || line.length() < 2) { return; }
        String inside_brackets = line.substring(lb_index+1, line.length()-1);
        ArrayList<String> params = GameScreen.parseString(inside_brackets.replace("|", "\n")); //get the entire list of parametres
        String cmd_name = line.replace("["+inside_brackets+"]", "");
        
        CommandTemplate c = COMMAND_LIST.get(cmd_name, params.size());
        System.out.println("Processing command: "+cmd_name+"["+params.size()+" params]");
        if (c != null) {
            for (int i = 0; i != params.size(); i++) {
                String p = params.get(i);
                if (c.global) {
                    if (p.equals("param")) params.set(i, param);
                    if (p.equals("this")) params.set(i, GameScreen.CURRENT_LEVEL.NAME);
                    //player is the name of the Player instance, so although it is a keyword, it does not need an exception
                } else {
                    if (parent == null) continue;
                    if (p.equals("param")) params.set(i, param);
                    if (p.equals("this")) params.set(i, parent.NAME);
                    if (p.equals("x")) params.set(i, parent.getWorldCoordinates()[0]+"");
                    if (p.equals("y")) params.set(i, parent.getWorldCoordinates()[1]+"");
                }
            }
            c.processor().process(this, params);
        }
        
    }
    
    /**
     * Updates the script. Runs each command one-by-one.
     */
    public void update() {
        if (index > -1 && index < CONTENTS.size()) {
            processCommand(CONTENTS.get(index));
            index++;
        } else {
            if (parent != null) {
                parent.stopScript(this.NAME);
            } else {
                GameScreen.CURRENT_LEVEL.stopScript(this.NAME);
            }
            reset();
        }
    }
    
}

class CommandList {
    ArrayList<CommandTemplate> list;
    
    public CommandList() {
        list = new ArrayList<CommandTemplate>();
        /**
         * YOU CANNOT HAVE MORE THAN ONE COMMAND WITH THE SAME NAME AND PARAMETRE COUNT
         * OR THE PROGRAM WILL NOT BE ABLE TO TELL THE TWO APART. IT'S EASIER TO JUST
         * WORK AROUND THIS FUNDEMENTAL FLAW THAN TO FIX IT.
         */
        
        //these are all the global commands
        list.add(new CommandTemplate("setcampos", new String[]{"value", "value"}, true, new SetCamPos()));
        list.add(new CommandTemplate("setcamtarget", new String[]{"value", "value"}, true, new SetCamTarget2()));
        list.add(new CommandTemplate("setcamtarget", new String[]{"object"}, true, new SetCamTarget1()));
        list.add(new CommandTemplate("setcamspeed", new String[]{"value"}, true, new SetCamSpeed()));
        list.add(new CommandTemplate("setmusic", new String[]{"value"}, true, new SetMusic()));
        list.add(new CommandTemplate("setambientsound", new String[]{"value"}, true, new SetAmbientSound()));
        list.add(new CommandTemplate("wait", new String[]{"value"}, true, new Wait()));
        list.add(new CommandTemplate("enterlevel", new String[]{"level"}, true, new EnterLevel1()));
        list.add(new CommandTemplate("enterlevel", new String[]{"level", "object"}, true, new EnterLevel2()));
        list.add(new CommandTemplate("delete", new String[]{"object"}, true, new Delete()));
        list.add(new CommandTemplate("runlevelscript", new String[]{"script", "value"}, true, new RunLevelScript2()));
        list.add(new CommandTemplate("runlevelscript", new String[]{"script"}, true, new RunLevelScript1()));
        list.add(new CommandTemplate("runobjectscript", new String[]{"object", "script", "value"}, true, new RunObjectScript3()));
        list.add(new CommandTemplate("runobjectscript", new String[]{"object", "script"}, true, new RunObjectScript2()));
        list.add(new CommandTemplate("loop", new String[]{}, true, new Loop()));
        list.add(new CommandTemplate("print", new String[]{"value"}, true, new Print()));
        
        //these are the commands that require a parent object to work with
        list.add(new CommandTemplate("jump", new String[]{}, false, new Jump()));
        list.add(new CommandTemplate("dialogue", new String[]{"dialogue"}, false, new DialogueCmd()));
        list.add(new CommandTemplate("say", new String[]{"value"}, false, new Say()));
        list.add(new CommandTemplate("setanim", new String[]{"animation"}, false, new SetAnim()));
        list.add(new CommandTemplate("move", new String[]{"value", "value"}, false, new Move2()));
        list.add(new CommandTemplate("moveto", new String[]{"value", "value"}, false, new MoveTo2()));
        list.add(new CommandTemplate("relativemoveto", new String[]{"value", "value"}, false, new RelativeMoveTo2()));
        list.add(new CommandTemplate("runscript", new String[]{"script", "value..."}, false, new RunScript2()));
        list.add(new CommandTemplate("runscript", new String[]{"script"}, false, new RunScript()));
    }
    
    public CommandTemplate get(String name, int param_count) {
        for (CommandTemplate c: list) { if (c.name().equals(name) 
                && c.paramCount() == param_count) return c; }
        return null;
    }
    
}

class CommandTemplate {
    private String name;
    private String[] param_types;
    boolean global = false;
    CommandProcessor cp;
    
    /*LIST OF VALID TYPES
     * animation
     * dialogue
     * object
     * script
     * level
     * value (not a reference to anything, just a value)
     */
    
    public CommandTemplate (String name, String[] types, boolean global, CommandProcessor p) {
        this.name = name;
        this.param_types = types;
        this.cp = p;
    }
    
    public final int paramCount() {
        return param_types.length;
    }
    
    public final String name() {
        return name;
    }
    
    public final CommandProcessor processor() {
        return cp;
    }
    
    public final String type(int index) {
        if (index > -1 && index < param_types.length) return param_types[index];
        return "";
    }
    
}

class CommandProcessor {
    public void process(Script parent, ArrayList<String> params) {}
}

class SetCamPos extends CommandProcessor {
    public SetCamPos() {}
    public void process(Script parent, ArrayList<String> params) {
        Camera.WORLD_X = Integer.parseInt(params.get(0));
        Camera.WORLD_Y = Integer.parseInt(params.get(1));
    }
}

class SetCamTarget2 extends CommandProcessor {
    public SetCamTarget2() {}
    public void process(Script parent, ArrayList<String> params) {
        Camera.setTarget(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)));
    }
}

class SetCamTarget1 extends CommandProcessor {
    public SetCamTarget1() {}
    public void process(Script parent, ArrayList<String> params) {
        SceneObject o = GameScreen.CURRENT_LEVEL.getObject(params.get(0));
        if (params.get(0).equals("player")) o = GameScreen.PLAYER;
        if (o != null) Camera.setTarget(o);
    }
}

class SetCamSpeed extends CommandProcessor {
    public SetCamSpeed() {}
    public void process(Script parent, ArrayList<String> params) {
        Camera.SPEED = Integer.parseInt(params.get(0));
        
    }
}

class SetMusic extends CommandProcessor {
    public SetMusic() {}
    public void process(Script parent, ArrayList<String> params) {
    
    }
}

class SetAmbientSound extends CommandProcessor {
    public SetAmbientSound() {}
    public void process(Script parent, ArrayList<String> params) {
    
    }
}

class Wait extends CommandProcessor {
    public Wait() {}
    public void process(Script parent, ArrayList<String> params) {
        parent.wait_timer += MiscMath.getConstant(1000, 1);
        if (parent.wait_timer >= Integer.parseInt(params.get(0))) {
            parent.wait_timer = 0;
        } else {
            parent.index--; //repeat this command
        }
        
    }
}

class EnterLevel1 extends CommandProcessor {
    public EnterLevel1() {}
    public void process(Script parent, ArrayList<String> params) {
        GameScreen.enterLevel(params.get(0));
    }
}

class EnterLevel2 extends CommandProcessor {
    public EnterLevel2() {}
    public void process(Script parent, ArrayList<String> params) {
        GameScreen.enterLevel(params.get(0));
        SceneObject o = GameScreen.CURRENT_LEVEL.getObject(params.get(1));
        if (o != null) {
            GameScreen.PLAYER.setWorldX(o.getWorldCoordinates()[0]);
            GameScreen.PLAYER.setWorldX(o.getWorldCoordinates()[1]);
        }
    }
}

class Delete extends CommandProcessor {
    public Delete() {}
    public void process(Script parent, ArrayList<String> params) {
        SceneObject o = GameScreen.CURRENT_LEVEL.getObject(params.get(0));
        if (o != null) {
            GameScreen.CURRENT_LEVEL.removeObject(o);
        }
        
    }
}

class RunLevelScript2 extends CommandProcessor {
    public RunLevelScript2() {}
    public void process(Script parent, ArrayList<String> params) {
        GameScreen.CURRENT_LEVEL.runScript(params.get(0), params.get(1));
    }
}

class RunLevelScript1 extends CommandProcessor {
    public RunLevelScript1() {}
    public void process(Script parent, ArrayList<String> params) {
        GameScreen.CURRENT_LEVEL.runScript(params.get(0), "");
    }
}

class RunObjectScript3 extends CommandProcessor {
    public RunObjectScript3() {}
    public void process(Script parent, ArrayList<String> params) {
        SceneObject o = GameScreen.CURRENT_LEVEL.getObject(params.get(0));
        if (o != null) GameScreen.CURRENT_LEVEL.getObject(params.get(0)).runScript(params.get(1), params.get(2));
    }
}

class RunObjectScript2 extends CommandProcessor {
    public RunObjectScript2() {}
    public void process(Script parent, ArrayList<String> params) {
        SceneObject o = GameScreen.CURRENT_LEVEL.getObject(params.get(0));
        if (o != null) GameScreen.CURRENT_LEVEL.getObject(params.get(0)).runScript(params.get(1), "");
    }
}

class Loop extends CommandProcessor {
    public Loop() {}
    public void process(Script parent, ArrayList<String> params) {
        parent.index = -1; 
    }
}

class Print extends CommandProcessor {
    public Print() {}
    public void process(Script parent, ArrayList<String> params) {
        System.out.println(parent.NAME+": "+params.get(0));
        
    }
}

class Jump extends CommandProcessor {
    public Jump() {}
    public void process(Script parent, ArrayList<String> params) {
        if (parent.parent.FORCES.contains(parent.parent.JUMP_VECTOR) == false) {
            parent.parent.FORCES.add(parent.parent.JUMP_VECTOR);
        }
    }
}

class DialogueCmd extends CommandProcessor {
    public DialogueCmd() {}
    public void process(Script parent, ArrayList<String> params) {
        parent.parent.startDialogue(params.get(0));
    }
}

class SetAnim extends CommandProcessor {
    public SetAnim() {}
    public void process(Script parent, ArrayList<String> params) {
        parent.parent.setAnim(params.get(0));
    }
}

class Say extends CommandProcessor {
    public Say() {}
    public void process(Script parent, ArrayList<String> params) {
        if (params.get(0).length() > 0) {
            parent.parent.SPEECH_BALLOON.setText(params.get(0));
            GameScreen.addGUIElement(parent.parent.SPEECH_BALLOON);
        }
    }
}

class Move2 extends CommandProcessor {
    public Move2() {}
    public void process(Script parent, ArrayList<String> params) {
        parent.parent.addWorldPos(MiscMath.getConstant(Double.parseDouble(params.get(0)), 1), MiscMath.getConstant(Double.parseDouble(params.get(1)), 1));
    }
}

class MoveTo2 extends CommandProcessor {
    public MoveTo2() {}
    public void process(Script parent, ArrayList<String> params) {
        if (parent.target_x == -1) {
            parent.target_x = Integer.parseInt(params.get(0));
            parent.target_y = Integer.parseInt(params.get(1));
        }
        parent.parent.addWorldPos(MiscMath.getConstant(parent.target_x-parent.parent.getWorldCoordinates()[0], parent.target_x-Math.abs(parent.parent.getWorldCoordinates()[0])), 
                parent.target_y-MiscMath.getConstant(parent.parent.getWorldCoordinates()[1], parent.target_y-Math.abs(parent.parent.getWorldCoordinates()[1])));
        if (Math.abs(parent.target_x-parent.parent.getWorldCoordinates()[0]) <= 1 
                && Math.abs(parent.target_y-parent.parent.getWorldCoordinates()[1]) <= 1) {
            parent.target_x = -1;
            parent.target_y = -1;
        } else {
            parent.index--;
        }
    }
}

class RelativeMoveTo2 extends CommandProcessor {
    public RelativeMoveTo2() {}
    public void process(Script parent, ArrayList<String> params) {
        if (parent.target_x == -1) {
            parent.target_x = parent.parent.getWorldCoordinates()[0]+Integer.parseInt(params.get(0));
            parent.target_y = parent.parent.getWorldCoordinates()[1]+Integer.parseInt(params.get(1));
        }
        parent.parent.addWorldPos(MiscMath.getConstant(parent.target_x-parent.parent.getWorldCoordinates()[0], parent.target_x-Math.abs(parent.parent.getWorldCoordinates()[0])), 
                parent.target_y-MiscMath.getConstant(parent.parent.getWorldCoordinates()[1], parent.target_y-Math.abs(parent.parent.getWorldCoordinates()[1])));
        if (Math.abs(parent.target_x-parent.parent.getWorldCoordinates()[0]) <= 1 
                && Math.abs(parent.target_y-parent.parent.getWorldCoordinates()[1]) <= 1) {
            parent.target_x = -1;
            parent.target_y = -1;
        } else {
            parent.index--;
        }
    }
}

class RunScript2 extends CommandProcessor {
    public RunScript2() {}
    public void process(Script parent, ArrayList<String> params) {
        
        parent.parent.runScript(params.get(0), params.get(1));
    }
}

class RunScript extends CommandProcessor {
    public RunScript() {}
    public void process(Script parent, ArrayList<String> params) {
        GameScreen.CURRENT_LEVEL.runScript(params.get(0), "");
        parent.parent.runScript(params.get(0), "");
    }
}
