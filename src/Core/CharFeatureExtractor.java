/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import Math.Vector;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author kostis
 */
public class CharFeatureExtractor extends MachineLearning.FeatureExtractor<Character>{

    public static final int LETTERS=26+3;
    public static final int START=0;
    public static final int STOP=LETTERS-1;
    public static final int UNKNOWN=LETTERS-2;
    
    public static final char START_CHAR=':';
    public static final char STOP_CHAR='.';
    public static final char UNKNOWN_CHAR='~';
    
    public static int char2index(char c){
        int result = (int) c - 96;
        if (c==START_CHAR)
            return START;
        if (c==STOP_CHAR)
            return STOP;
        if (result<0||result>LETTERS)
            return UNKNOWN;
        return result;
    }
    
    public static char index2char(int idx){
        char c = (char) (idx + 96);
        c=idx==UNKNOWN?UNKNOWN_CHAR:c;
        c=idx==STOP?STOP_CHAR:c;
        c=idx==START?START_CHAR:c;
        return c;
    }
    
    @Override
    public Vector data2pattern(Character data) {
        Vector result = new Vector(LETTERS);
        int idx = char2index(data);
        //System.out.printf("%c - %d\n",c ,idx);
        result.set(idx, 1);
        return result;
    }

    @Override
    public Character pattern2data(Vector pattern) {
        int idx = pattern.argMax();
        return index2char(idx);
    }
    
    public Vector[] str2patterns(String word){
        Character[] data_list = new Character[word.length()];
        for (int i=0;i<data_list.length;i++)
            data_list[i]=word.charAt(i);
        return super.data2pattern(data_list);
    }
    
    public String patterns2str(Vector[] patterns){
        String result = "";
        for (int i=0;i<patterns.length;i++)
            result+=pattern2data(patterns[i]);
        return result;
    }
    
    public static Vector getStartPattern(){
        Vector result = new Vector(LETTERS);
        result.set(START, 1);
        return result;
    }

    public static Vector getStopPattern(){
        Vector result = new Vector(LETTERS);
        result.set(STOP, 1);
        return result;
    }

    public static Vector getUnPattern(){
        Vector result = new Vector(LETTERS);
        result.set(UNKNOWN, 1);
        return result;
    }
    
    public static String padd(String word){
        return START_CHAR+word+STOP_CHAR;
    }
    
    
}
