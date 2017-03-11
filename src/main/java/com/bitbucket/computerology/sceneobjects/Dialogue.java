package com.bitbucket.computerology.sceneobjects;

import com.bitbucket.computerology.gui.GameScreen;
import com.bitbucket.computerology.world.Camera;
import com.bitbucket.computerology.sceneobjects.SceneObject;
import java.util.ArrayList;

public class Dialogue {
    
    public static int NPC_SPEECH = 0, PLAYER_OPTIONS = 1, NPC_RESPONSE = 2;
    
    int stage = 0;
    
    public String NAME = "";
    public ArrayList<String> OBJECT_SPEECH, PLAYER_RESPONSES, EVENT_VALUES, EVENT_TYPES;
    
    int index = 0;
    
    SceneObject parent;
    
    public Dialogue(SceneObject parent) {
        this.parent = parent;
        OBJECT_SPEECH = new ArrayList<String>();
        PLAYER_RESPONSES = new ArrayList<String>();
        EVENT_VALUES = new ArrayList<String>();
        EVENT_TYPES = new ArrayList<String>();
    }
    
    public void copyTo(Dialogue new_d) {
        new_d.NAME = NAME;
        new_d.PLAYER_RESPONSES.addAll(PLAYER_RESPONSES);
        new_d.OBJECT_SPEECH.addAll(OBJECT_SPEECH);
        new_d.EVENT_VALUES.addAll(EVENT_VALUES);
        new_d.EVENT_TYPES.addAll(EVENT_TYPES);
    }
    
    public void update() {
        
    }
    
    public void chooseOption(int i) {
        GameScreen.RESPONSE_MENU.hide();
        parent.CURRENT_DIALOGUE = null;
        GameScreen.CURRENT_DIALOGUE = null;
        GameScreen.CURRENT_SPEECH_BALLOON = null;
        if (EVENT_TYPES.get(i).equals("Script")) {
            parent.runScript(EVENT_VALUES.get(i), "");
        } else if (EVENT_TYPES.get(i).equals("Dialogue")) {
            parent.startDialogue(EVENT_VALUES.get(i));
        } else if (EVENT_TYPES.get(i).equals("Speech")) {
            parent.doCommand("say["+EVENT_VALUES.get(i)+"]");
        }
        stage++;
    }
    
    public int getStage() {
        return stage;
    }
    
    public void next() {
        index++;
        if (stage == 0) {
            if (index >= OBJECT_SPEECH.size()) {
                stage = 1;
                index = 0;
                parent.SPEECH_BALLOON.hide();
                if (PLAYER_RESPONSES.size() > 0) {
                    GameScreen.RESPONSE_MENU.show();
                    GameScreen.RESPONSE_MENU.setDialogue(this);
                    GameScreen.CURRENT_SPEECH_BALLOON.hide();
                    Camera.setTarget(GameScreen.PLAYER);
                }
            } else {
                parent.SPEECH_BALLOON.setText(OBJECT_SPEECH.get(index));
            }
        }
    }
    
    public void reset() {
        index = 0;
        stage = 0;
    }
    
}
