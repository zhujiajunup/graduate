package cn.edu.zju.zjj;

import emoji4j.EmojiUtils;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest{
    public static void main(String [] args){
        String a = "麻烦鬼\uD83D\uDC49不服咬我啊";
        System.out.println(EmojiUtils.shortCodify(a));
    }
}
