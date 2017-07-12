package misc;

import java.util.ArrayList;
import world.Level;
import world.World;
import world.objects.SceneObject;
import world.objects.components.Animation;
import world.objects.components.logic.Flow;

public class Types {
    
    public static final int 
            ANY = 0, 
            VARIABLE = 1, 
            NUMBER = 2,
            TEXT_LIST = 3,
            TEXT = 4, 
            BOOLEAN = 5, 
            ANIM = 6,
            FLOW = 7, 
            OBJECT = 8, 
            LEVEL = 9, 
            ASSET = 10;
    
    private static Type[] types = {
        new TypeAny(),
        new TypeVar(),
        new TypeNumber(),
        new TypeTextList(),
        new TypeText(),
        new TypeBoolean(),
        new TypeAnim(),
        new TypeFlow(),
        new TypeObject(),
        new TypeLevel(),
        new TypeAsset()
    };
    
    public static Type getType(int index) {
        return types[index];
    }
    
    public static int getType(String input) {
        for (int i = 1; i < types.length; i++) if (types[i].typeOf(input)) return i;
        return -1;
    }
    
    public static String getTypeName(int type) {
        return types[type].getName();
    }
    
    public static boolean isValidInput(String input, int type) {
        return types[type].typeOf(input);
    }
    
    public static boolean isComplex(int type) { return types[type] instanceof ComplexType; }
    
    public static double parseNumber(String number) { return Double.parseDouble(number); }
    public static String parseText(String text) { return text.substring(1, text.length()-1); }
    public static boolean parseBoolean(String bool) { return Boolean.parseBoolean(bool); }
    
    public static Animation parseAnimation(Level l, SceneObject o, String anim) {
        if (!types[ANIM].typeOf(anim)) return null;
        String p[] = ((ComplexType)types[ANIM]).getParams(anim);
        if (p.length == 1 && o != null) return o.getAnimation(p[0]);;
        if (p.length == 2 && l != null) {
            SceneObject o2 = l.getObject(p[1]);
            if (o2 != null) return o2.getAnimation(p[0]);
        }
        if (p.length == 3) {
            Level l2 = World.getWorld().getLevel(p[2]); 
            l2 = l2 == null ? World.getWorld().getCurrentLevel() : l2;
            SceneObject o2 = l2.getObject(p[1]);
            if (o2 != null) return o2.getAnimation(p[0]);
        }
        return null;
    }
    
    public static Flow parseFlow(Level l, SceneObject o, Flow f, String flow) {
        if (!types[FLOW].typeOf(flow)) return null;
        String p[] = ((ComplexType)types[FLOW]).getParams(flow);
        if (p.length == 0) return f;
        if (p.length == 1 && o != null) return o.getFlow(p[0]);
        if (p.length == 2 && l != null) {
            SceneObject o2 = l.getObject(p[1]);
            if (o2 != null) return o2.getFlow(p[0]);
        }
        if (p.length == 3) {
            Level l2 = World.getWorld().getLevel(p[2]); 
            l2 = l2 == null ? World.getWorld().getCurrentLevel() : l2;
            SceneObject o2 = l2.getObject(p[1]);
            if (o2 != null) return o2.getFlow(p[0]);
        }
        return null;
    }
    
    public static Level parseLevel(Level l, String level) {
        if (!types[LEVEL].typeOf(level)) return null;
        String name = level.replace("Level(", "").replace(")", "");
        return name.length() == 0 ? l : World.getWorld().getCurrentLevel();
    }
    
    public static Object parseAsset(String asset) {
        if (!types[ASSET].typeOf(asset)) return null;
        return Assets.get(asset.replace("Asset(", "").replace(")", ""));
    }
    
    public static SceneObject parseObject(Level l, SceneObject o, String scene_object) {
        if (!types[OBJECT].typeOf(scene_object)) return null;
        String p[] = ((ComplexType)types[OBJECT]).getParams(scene_object);
        if (p.length == 0) return o;
        if (p.length == 1 && l != null) return l.getObject(p[0]);
        if (p.length == 2 && l != null) {
            Level l2 = World.getWorld().getLevel(p[1]);
            if (l2 == null) return null;
            SceneObject o2 = l2.getObject(p[0]);
            return o2;
        }
        return null;
    }
    public static ArrayList<String> parseTextList(String input) {
        if (!types[TEXT_LIST].typeOf(input)) return null;
        ArrayList<String> list = new ArrayList<String>();
        input = input.substring(5, input.length()-1).trim();
        String[] params = input.split("\"[ ]*,[ ]*\"");
        for (int i = 0; i < params.length; i++) { 
            if (i < params.length - 1) params[i] += "\"";
            if (i > 0) params[i] = "\""+params[i];
            System.out.println(i+": "+params[i]);
            if (Types.getType(Types.TEXT).typeOf(params[i])) list.add(params[i].substring(1, params[i].length()-1));
        }
        return list;
    }
    
}

/**
 * GENERIC TYPES
 */

class Type {
    
    private String name;

    protected void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    /**
     * Returns true if this type is the super type of the value provided.
     * Overridden by the type declarations.
     * @param value
     * @return 
     */
    public boolean typeOf(String value) {
        if (value == null) return false;
        if (value.length() == 0) return false;
        return value.replaceAll("[{}\t\r\n]", "").equals(value);
    }

}

class ComplexType extends Type {
    
    private String params_regex, alias;
    //String params_regex = "([^,]+?|([^,]+[,][^,]+?)|([^,]+[,][^,]+[,][^,]+?))";
    
    public final void setParams(int i, boolean allow_none) {
        i = (int)MiscMath.clamp(i, 1, 3);
        params_regex = "("+"([^,]+?)"
                +(i >= 2 ? "|([^,]+[,][^,]+?)" : "")
                +(i == 3 ? "|([^,]+[,][^,]+[,][^,]+?)" : "")+")"+(allow_none ? "?" : "");
    }
    
    public final String[] getParams(String input) { 
        String p[] = input.substring(alias.length()+1, input.length()-1).split("([ ]*)?[,]([ ]*)?");
        if (p.length == 1) if (p[0].length() == 0) return new String[0];
        return p;
    }
    
    public final String getAlias() { return alias; }
    public final void setAlias(String w) { alias = w; }
    public final String getParamsRegex() { return params_regex; }
    public boolean isValidName(String name) { return false; }
    
}

/**
 * TYPE CLASSES
 */

class TypeAny extends Type {
    public TypeAny() { 
        setName("Any"); 
    }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        int t = Types.getType(value);
        return t >= 0;
    }    
}

class TypeVar extends Type {
    public TypeVar() { 
        setName("Variable"); 
    }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        if (value == null) return false;
        if (value.trim().length() == 0) return false;
        if (value.trim().equals("true") || value.trim().equals("false")
                || value.trim().toLowerCase().equals("player")) return false;
        return value.replaceAll("^[a-zA-Z_$][a-zA-Z_$0-9]*$", "").equals("");
    }    
}

class TypeNumber extends Type {
    public TypeNumber() { setName("Number"); }
    
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.replaceAll("((\\+|-)?([0-9]+)(\\.[0-9]+)?)|((\\+|-)?\\.?[0-9]+)", "").equals("");
    }
}

class TypeText extends Type {
    public TypeText() { setName("Text"); }

    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.charAt(0) == '"' && value.charAt(value.length()-1) == '"';
    }
}

class TypeBoolean extends Type {
    public TypeBoolean() { setName("Boolean"); }

    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.equals("true") || value.equals("false");
    }
}

class TypeTextList extends Type {
    public TypeTextList() { setName("List (Text)"); }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        value = value.trim();
        if (value.indexOf("List(") == 0 && value.lastIndexOf(")") == value.length()-1) {
            value = value.substring(5, value.length()-1).trim();
            String[] params = value.split("\"[ ]*,[ ]*\"");
            for (int i = 0; i < params.length; i++) { 
                if (i < params.length - 1) params[i] += "\"";
                if (i > 0) params[i] = "\""+params[i];
                System.out.println(i+": "+params[i]);
                if (!Types.getType(Types.TEXT).typeOf(params[i])) return false;
            }
            return true;
        }
        return false;
    }
}

/**
 * COMPLEX TYPE CLASSES
 */

class TypeAnim extends ComplexType {
    public TypeAnim() { 
        setName("Animation"); 
        setAlias("Anim");
        setParams(3, false);
    }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.replaceAll("("+getAlias()+"\\()"+getParamsRegex()+"(\\))", "").equals("");
    }    
}

class TypeFlow extends ComplexType {
    public TypeFlow() { 
        setName("Flow");
        setAlias("Flow");
        setParams(3, true);
    }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.replaceAll("("+getAlias()+"\\()"+getParamsRegex()+"(\\))", "").equals("");
    }    
}

class TypeObject extends ComplexType {
    public TypeObject() { 
        setName("Object");
        setAlias("Object"); 
        setParams(2, true);
    }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.replaceAll("("+getAlias()+"\\()"+getParamsRegex()+"(\\))", "").equals("");
    }  
}

class TypeLevel extends ComplexType {
    public TypeLevel() {
        setName("Level"); 
        setAlias("Level"); 
        setParams(1, true);
    }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.replaceAll("("+getAlias()+"\\()"+getParamsRegex()+"(\\))", "").equals("");
    }    
}

class TypeAsset extends ComplexType {
    public TypeAsset() { 
        setName("Asset");
        setAlias("Asset");
        setParams(1, false);
    }
    @Override
    public boolean typeOf(String value) {
        if (!super.typeOf(value)) return false;
        return value.replaceAll("^("+getAlias()+"\\()"+getParamsRegex()+"(\\))$", "").equals("");
    }    
}
