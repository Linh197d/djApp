package com.gambi.quanglinh.djmixer.utils;


public class Settings {
    public static boolean isEqualizerEnabled = true;
    public static boolean isEqualizerReloaded = false;
    public static int[] seekbarpos1 = new int[5];
    public static int[] seekbarpos2 = new int[5];
    public static int presetPos;
    public static short bassStrength1 = -1;
    public static short bassStrength2 = -1;
    public static int loopedPosition1 = -1;
    public static int loopedPosition2 = -1;
    public static int loopIn1 = -1;
    public static int loopIn2 = -1;
    public static int[] arrayCues1={0,0,0,0,0,0,0,0};
    public static int[] arrayCues2={0,0,0,0,0,0,0,0};
    public static boolean isDeleteOn1 = false;
    public static boolean isDeleteOn2 = false;
    public static int sbPositionSample1 = -1;
    public static int sbPositionSample2 = -1;

    public static String[] arrayLoop = {
            "1/64",
            "1/32",
            "1/16",
            "1/8",
            "1/4",
            "1/2",
            "1",
            "2",
            "4",
            "8",
            "16",
            "32",
            "64",
            "128"};

    public static String pkName = "";
    public static void reset1(){
        seekbarpos1= new int[]{1500,1500,1500,1500,1500};
        presetPos=-1;
        bassStrength1=-1;
        loopedPosition1=-1;
        loopIn1=-1;
        arrayCues1= new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        isDeleteOn1 = false;
        sbPositionSample1 = -1;
    }
    public static void reset2(){
        seekbarpos2=new int[]{1500, 1500, 1500, 1500, 1500};
        presetPos=-1;
        bassStrength2=-1;
        loopedPosition2=-1;
        loopIn2=-1;
        arrayCues2= new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        isDeleteOn2 = false;
        sbPositionSample2 = -1;
    }
}
