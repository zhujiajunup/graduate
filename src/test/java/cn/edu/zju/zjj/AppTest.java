package cn.edu.zju.zjj;

import emoji4j.EmojiUtils;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest{
    public static void incre(int a){
        a ++;
    }
    public static void main(String [] args){
        int [] a = {1, 2, 3};
        for(int e: a){
           e = e+1;
            System.out.print(e+" ");
        }
    }
}
