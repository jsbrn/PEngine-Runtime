package world;

import misc.MiscMath;
import misc.Window;
import world.objects.SceneObject;

public class Camera {

    private static int target_coords[];
    private static SceneObject target_object;

    private static double x = 0, y = 0;
    private static double zoom = 8, speed = 5;
    
    private static double[] viewport = new double[4];

    /**
     * Sets camera focus speed.
     */
    public static void setSpeed(int s) { speed = s >= 0 ? s : 0; }

    public static double getX() {
        double cam_bounds[] = getViewPort();
        double l_bounds[] = World.getWorld().getCurrentLevel() == null ? new double[4]
                : World.getWorld().getCurrentLevel().bounds();
        if (l_bounds[2] <= cam_bounds[2]) return l_bounds[0] + (l_bounds[2]/2);
        return MiscMath.clamp(x, l_bounds[0] + (cam_bounds[2]/2), 
                l_bounds[0] + l_bounds[2] - (cam_bounds[2]/2)) - 0.5;
    }

    public static void setX(double x) {
        Camera.setTarget(null);
        Camera.x = x;
    }

    public static double getY() {
        double cam_bounds[] = getViewPort();
        double l_bounds[] = World.getWorld().getCurrentLevel() == null ? new double[4]
                : World.getWorld().getCurrentLevel().bounds();
        if (l_bounds[3] <= cam_bounds[3]) return l_bounds[1] + (l_bounds[3]/2);
        return MiscMath.clamp(y, l_bounds[1] + (cam_bounds[3]/2), 
                l_bounds[1] + l_bounds[3] - (cam_bounds[3]/2)) - 0.5;
    }

    public static void setY(double y) {
        Camera.setTarget(null);
        Camera.y = y;
    }
    
    public static void move(double x, double y) {
        Camera.x += x; Camera.y += y;
    }

    public static double getZoom() {
        return zoom;
    }

    public static void setZoom(int z) {
        zoom = z < 1 ? 1 : z;
    }

    public static void addZoom(int z) {
        setZoom((int)getZoom()+z);
    }

    public static void reset() {
        zoom = 1; x = 0; y = 0; target_coords = null; target_object = null;
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
    
    public static double[] getViewPort() {
        return viewport;
    }
    
    public static double[] oob() {
        //left top right bottom
        double[] l_bounds = World.getWorld().getCurrentLevel().bounds();
        double[] cam_bounds = getViewPort();
        double diffs[] = new double[]{
            l_bounds[0]-cam_bounds[0],
            l_bounds[1]-cam_bounds[1],
            (cam_bounds[2]+cam_bounds[0])-(l_bounds[2]+l_bounds[0]),
            (cam_bounds[3]+cam_bounds[1])-(l_bounds[3]+l_bounds[1])
        };
        return diffs;
    }

    public static void update() {
        
        //track viewport
        double[] display_tl_wc = MiscMath.getWorldCoords(0, 0);
        double[] display_br_wc = MiscMath.getWorldCoords(Window.getWidth(), Window.getHeight());
        viewport = new double[]{display_tl_wc[0], display_tl_wc[1], 
            display_br_wc[0]-display_tl_wc[0], display_br_wc[1]-display_tl_wc[1]};
        
        //determine target
        int[] target = target_coords;
        target = target_object == null ? target : new int[]{(int)target_object.getWorldCoords()[0], 
            (int)target_object.getWorldCoords()[1]};
        
        //determine camera velocity and move camera
        if (target != null) {
            double dist = MiscMath.distance(x, y, target[0], target[1]);
            if (dist > 1) {
                double vel[] = MiscMath.calculateVelocity((int)(target[0]-x), (int)(target[1]-y));
                move(MiscMath.getConstant(vel[0]*speed*dist, 1), MiscMath.getConstant(vel[1]*speed*dist, 1));
            }
        }
        
    }
}
