package world.objects.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import misc.Assets;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Animation {
    
    private String name, spritesheet;
    private int frame_dur, frame_count; //dur in milliseconds
    private int frame = 0;
    private long last_time;
    
    public Animation() {
        this.spritesheet = "";
        this.name = "";
        this.frame_count = 1;
        this.frame_dur = 100;
    }
    
    public int getFrameDuration() { return frame_dur; }

    public void setFrameDuration(int frame_dur) {
        this.frame_dur = frame_dur;
    }
    
    public int frameCount() { return frame_count; }
    public void setFrameCount(int c) { frame_count = c < 1 ? 1 : c; }
    public void addFrameCount(int d) { setFrameCount(frame_count + d); }
    
    @Override
    public String toString() {
        return getName();
    }

    public String getSpriteSheet() {
        return spritesheet;
    }

    public void setSpriteSheet(String spritesheet) {
        this.spritesheet = spritesheet;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public boolean equalTo(Animation a) {
        if (!(name.equals(a.name))) return false;
        if (!(spritesheet.equals(a.spritesheet))) return false;
        if (frame_dur != a.frame_dur) return false;
        if (frame_count != a.frame_count) return false;
        return true;
    }
    
    public void copyTo(Animation new_a, boolean copy_name) {
        if (copy_name) new_a.name = name;
        new_a.spritesheet = spritesheet;
        new_a.frame_dur = frame_dur;
        new_a.frame_count = frame_count;
    }
    
    public void save(BufferedWriter bw) {
        try {
            bw.write("a\n");
            bw.write("n="+name+"\n");
            bw.write("s="+spritesheet+"\n");
            bw.write("fd="+frame_dur+"\n");
            bw.write("fc="+frame_count+"\n");
            bw.write("/a\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean load(BufferedReader br) {
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.equals("/a")) return true;
                
                if (line.indexOf("n=") == 0) name = line.substring(2);
                if (line.indexOf("s=") == 0) spritesheet = line.substring(2);
                if (line.indexOf("fd=") == 0) frame_dur = Integer.parseInt(line.substring(3));
                if (line.indexOf("fc=") == 0) frame_count = Integer.parseInt(line.substring(3));
                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public void draw(int osx, int osy, double z, Graphics g) {
        
        long current = System.currentTimeMillis();
        if (current - last_time > frame_dur) {
            frame++; if (frame >= frame_count) frame = 0;
            last_time = current;
        }
        
        Object asset = Assets.get(spritesheet);
        Image img = (Image)asset;
        if (img != null) {
            int f_w = img.getWidth() / frame_count;
            img = img.getSubImage(f_w*frame, 0, f_w, img.getHeight()).getScaledCopy((float)z);
            g.drawImage(img, osx - (img.getWidth()/2), osy - (img.getHeight()/2));
        }
        
    }
    
}
