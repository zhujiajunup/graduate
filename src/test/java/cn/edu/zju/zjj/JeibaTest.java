package cn.edu.zju.zjj;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2017/12/22 22:17
 */
public class JeibaTest {

    public static void main(String [] args){
        String str = "[思考]无论怎么弄，保姆最后必须是要死刑。放火的目的难道是冷了取暖？就是为了烧死人啊。" ;
        Result result = ToAnalysis.parse(str);
        System.out.println(result);
    }
}
