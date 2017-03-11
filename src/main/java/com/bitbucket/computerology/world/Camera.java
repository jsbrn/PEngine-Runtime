package com.bitbucket.computerology.world;

import com.bitbucket.computerology.sceneobjects.SceneObject;
import com.bitbucket.computerology.gui.GameScreen;
import com.bitbucket.computerology.misc.MiscMath;
import org.lwjgl.opengl.Display;

public class Camera {
    public static double WORLD_X = 0, WORLD_Y = 0, ZOOM = 4;
    public static double SPEED = 1;
    static SceneObject target = null;
    static int[] target_point = new int[]{0, 0};
    
    public static void move(double x, double y) {
        WORLD_X += x;
        WORLD_Y += y;
    }
    
    public static void setTarget(SceneObject o) {
        target = o;
    }
    
    public static void setTarget(int w_x, int w_y) {
        target_point[0] = w_x; target_point[1] = w_y;
    }
    
    public static void update() {
        int height = 32;
        int t_x = target_point[0], t_y = target_point[1];
        if (target != null) {
            t_x = target.getWorldCoordinates()[0];
            t_y = target.getWorldCoordinates()[1];
            if (target.getDimensions()[1] > 0) {
                height = target.getDimensions()[1];
            }
        }
        
        System.out.println("current level zoom = "+GameScreen.CURRENT_LEVEL.ZOOM);
        ZOOM = (int)(((Display.getHeight()/4)/height)*((double)GameScreen.CURRENT_LEVEL.ZOOM/4.0));
        WORLD_X +=(MiscMath.getConstant(t_x-WORLD_X, 0.2)*SPEED);
        WORLD_Y +=(MiscMath.getConstant(t_y-WORLD_Y, 0.2)*SPEED);
        /*if (Math.abs(t_x-WORLD_X) > 1 || Math.abs(t_y-WORLD_Y) > 1) {
            for (SceneObject o: GameScreen.CURRENT_LEVEL.DISTANT_OBJECTS) {
                o.addWorldPos((MiscMath.getConstant(t_x-WORLD_X, 0.2)*SPEED)/2, (MiscMath.getConstant(t_y-WORLD_Y, 0.2)*SPEED)/2);
            }
        }*/
        int pixels_x = (int)(Display.getWidth()/ZOOM), pixels_y = (int)(Display.getHeight()/ZOOM);
        if (pixels_x % 2 == 1) {pixels_x+=1;}if (pixels_y % 2 == 1) {pixels_y+=1;}
        boolean left = pixels_x / 2 > WORLD_X;
        boolean right = WORLD_X + (pixels_x/2) > GameScreen.CURRENT_LEVEL.WIDTH;
        boolean top = pixels_y / 2 > WORLD_Y;
        boolean bottom = WORLD_Y + (pixels_y/2) > GameScreen.CURRENT_LEVEL.HEIGHT;

        if (GameScreen.CURRENT_LEVEL.WIDTH <= pixels_x) {
            WORLD_X = GameScreen.CURRENT_LEVEL.WIDTH / 2;
        } else {
            if (left) {
               WORLD_X = pixels_x/2; 
            } else if (right) {
                WORLD_X = GameScreen.CURRENT_LEVEL.WIDTH - (pixels_x/2);
            }
        }
        if (GameScreen.CURRENT_LEVEL.HEIGHT <= pixels_y) {
            WORLD_Y = (int)(GameScreen.CURRENT_LEVEL.HEIGHT - (pixels_y / 2));
        } else {
            if (bottom) {
               WORLD_Y = (int)(GameScreen.CURRENT_LEVEL.HEIGHT - (pixels_y/2));
            } else if (top) {
                WORLD_Y = (int)(pixels_y/2);
            }
        }
        
        if (ZOOM <= 4) {
            ZOOM = 4;
        }
        
    }
    
    public static void setTarget(String id) {
        SceneObject oldtarget = target;
        target = GameScreen.CURRENT_LEVEL.getObject(id);
        if (target == null) target = oldtarget;
    }
    
}
