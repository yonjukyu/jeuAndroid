package com.example.jeu;

public class Saltot {
    public int rotation_sense;
    public int dolphin_rotation;

    public Saltot(int rotation_sense, int dolphin_rotation){
        this.rotation_sense = rotation_sense;
        this.dolphin_rotation = dolphin_rotation;
    }

    public void doTheSaltot(){

    }




    public int getRotation_sense() {
        return rotation_sense;
    }

    public void setRotation_sense(int rotation_sense) {
        this.rotation_sense = rotation_sense;
    }

    public int getDolphin_rotation() {
        return dolphin_rotation;
    }

    public void setDolphin_rotation(int dolphin_rotation) {
        this.dolphin_rotation = dolphin_rotation;
    }
}
