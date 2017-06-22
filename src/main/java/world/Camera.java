package world;

import misc.MiscMath;
import misc.Window;
import world.objects.SceneObject;

public class Camera {

    private static int target_coords[];
    private static SceneObject target_object;

    private static double x = 0, y = 0;
    private static double zoom = 8, speed = 5;

    /**
     * Sets camera focus speed.
     */
    public static void setSpeed(int s) { speed = s >= 0 ? s : 0; }

    public static double getX() {
        return x;
    }

    public static void setX(int x) {
        Camera.x = x;
    }

    public static double getY() {
        return y;
    }

    public static void setY(int y) {
        Camera.y = y;
    }

    public static double getZoom() {
        return zoom;
    }

    public static void setZoom(int z) {
        zoom = z;
        if (zoom > 3) zoom = 3;
        if (zoom < 1) zoom = 1;
    }

    public static void addZoom(int z) {
        setZoom((int)getZoom()+z);
    }

    public static void reset() {
        zoom = 1;
        x = 0;
        y = 0;
    }

    public static void setTarget(int wx, int wy) {
        target_coords = new int[]{wx, wy};
        target_object = null;
    }

    public static SceneObject getTarget() { return target_object; }

    public static void setTarget(SceneObject e) {
        target_object = e;
        target_coords = null;
    }

    public static void update() {
        
        int[] target = target_coords;
        target = target_object == null ? target : new int[]{(int)target_object.getWorldCoords()[0], 
            (int)target_object.getWorldCoords()[1]};
        
        if (target != null) {
            double dist = MiscMath.distance(x, y, target[0], target[1]);
            if (dist > 5) {
                double vel[] = MiscMath.calculateVelocity((int)(target[0]-x), (int)(target[1]-y));
                x += MiscMath.getConstant(vel[0]*speed*dist, 1);
                y += MiscMath.getConstant(vel[1]*speed*dist, 1);
            }
        }
        
        //get the rectangle determining the level bounds
        int[] bounds = World.getWorld().getCurrentLevel().bounds();
        double[] dbounds = new double[]{bounds[0], bounds[1], bounds[2], bounds[3]};
        //get the onscreen coords (top left) of the bounds
        double[] c_bounds = new double[]{
            (x - (Window.getWidth()/2/zoom)), 
            (y - (Window.getHeight()/2/zoom)),
            (Window.getWidth()/zoom),
            (Window.getHeight()/zoom)
        };
        dbounds[2] += dbounds[0]; dbounds[3] += dbounds[1];
        c_bounds[2] += c_bounds[0]; c_bounds[3] += c_bounds[1];
        
        //out of bounds (left top right bottom)
        boolean oob[] = new boolean[]{
            dbounds[0] > c_bounds[0],
            dbounds[1] > c_bounds[1],
            dbounds[2] < c_bounds[2],
            dbounds[3] < c_bounds[3]
        };
        if (oob[0]) x += dbounds[0] - c_bounds[0];
        if (oob[1]) y += dbounds[1] - c_bounds[1];
        if (oob[2]) x -= c_bounds[2] - dbounds[2];
        if (oob[3]) y -= c_bounds[3] - dbounds[3];
        if (oob[0] && oob[2]) x = dbounds[0] + ((dbounds[2]-dbounds[0])/2);
        if (oob[1] && oob[3]) x = dbounds[1] + ((dbounds[3]-dbounds[1])/2);
        
        System.out.println(dbounds[0]+" "+dbounds[1]+" "+dbounds[2]+" "+dbounds[3]);
        System.out.println(c_bounds[0]+" "+c_bounds[1]+" "+c_bounds[2]+" "+c_bounds[3]);
        
        
    }
}
