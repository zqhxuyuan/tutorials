package com.zqh.midd.lucene;

import junit.framework.TestCase;
import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.StringReader;

public class AnalyzerTest extends TestCase {

    //生成analyzer实例
    protected PaodingAnalyzer analyzer = new PaodingAnalyzer();

    protected StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws Exception{
        AnalyzerTest test = new AnalyzerTest();
        test.token("中华人民共和国");
    }

    public void token(String text) throws Exception{
        TokenStream ts = analyzer.tokenStream("text", new StringReader(text));
        //添加工具类  注意：以下这些与之前lucene2.x版本不同的地方
        CharTermAttribute offAtt = (CharTermAttribute) ts.addAttribute(CharTermAttribute.class);
        // 循环打印出分词的结果，及分词出现的位置
        while (ts.incrementToken()) {
            System.out.print(offAtt.toString() + "\t");
        }
    }

    protected String dissect(String input) {
        try {
            //取得Token流
            TokenStream stream = analyzer.tokenStream("", new StringReader(input));
            //重置到流的开始位置
            stream.reset();
            //TermAttribute termAtt = (TermAttribute) stream.addAttribute(TermAttribute.class);
            OffsetAttribute offAtt = (OffsetAttribute) stream.addAttribute(OffsetAttribute.class);
            while (stream.incrementToken()) {
                System.out.println(//termAtt.term() + " " +
                    offAtt.startOffset() + " " + offAtt.endOffset());
            }
            /*
            Token token;
            sb.setLength(0);
            while ((token = stream.next()) != null) {
                sb.append(token.termText()).append('/');
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            */
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public void test000() {
        String result = dissect("a");
        assertEquals("", result);
    }

    public void test001() {
        String result = dissect("空格 a 空格");
        assertEquals("空格/空格", result);
    }

    public void test002() {
        String result = dissect("A座");
        assertEquals("a座", result);
    }

    public void test003() {
        String result = dissect("u盘");
        assertEquals("u盘", result);
    }

    public void test004() {
        String result = dissect("刚买的u盘的容量");
        assertEquals("刚/买的/u盘/容量", result);
    }

    public void test005() {
        String result = dissect("K歌之王很好听");
        assertEquals("k歌之王/很好/好听", result);
    }
    // --------------------------------------------------------------
    // 仅包含词语的句子分词策略
    // --------------------------------------------------------------

    /**
     * 句子全由词典词语组成，但词语之间没有包含、交叉关系
     */
    public void test100() {
        String result = dissect("台北中文国际");
        assertEquals("台北/中文/国际", result);
    }

    /**
     * 句子全由词典词语组成，但词语之间有包含关系
     */
    public void test101() {
        String result = dissect("北京首都机场");
        assertEquals("北京/首都/机场", result);
    }

    /**
     * 句子全由词典词语组成，但词语之间有交叉关系
     */
    public void test102() {
        String result = dissect("东西已经拍卖了");
        assertEquals("东西/已经/拍卖/卖了", result);
    }

    /**
     * 句子全由词典词语组成，但词语之间有包含、交叉等复杂关系
     */
    public void test103() {
        String result = dissect("羽毛球拍");
        assertEquals("羽毛/羽毛球/球拍", result);
    }

    // --------------------------------------------------------------
    // noise词汇和单字的分词策略
    // --------------------------------------------------------------

    /**
     * 词语之间有一个noise字(的)
     */
    public void test200() {
        String result = dissect("足球的魅力");
        assertEquals("足球/魅力", result);
    }

    /**
     * 词语之间有一个noise词语(因之)
     */
    public void test201() {
        String result = dissect("主人因之生气");
        assertEquals("主人/生气", result);
    }

    /**
     * 词语前后分别有单字和双字的noise词语(与,有关)
     */
    public void test202() {
        String result = dissect("与谋杀有关");
        assertEquals("谋杀", result);
    }

    /**
     * 前有noise词语(哪怕)，后面跟随了连续的noise单字(了,你)
     */
    public void test203() {
        String result = dissect("哪怕朋友背叛了你");
        assertEquals("朋友/背叛", result);
    }

    /**
     * 前后连续的noise词汇(虽然,某些)，词语中有noise单字(很)
     */
    public void test204() {
        String result = dissect("虽然某些动物很凶恶");
        assertEquals("动物/凶恶", result);
    }

    // --------------------------------------------------------------
    // 词典没有收录的字符串的分词策略
    // --------------------------------------------------------------


    /**
     * 仅1个字的非词汇串(东,西,南,北)
     */
    public void test300() {
        String result = dissect("东&&西&&南&&北");
        assertEquals("东/西/南/北", result);
    }


    /**
     * 仅两个字的非词汇串(古哥,谷歌,收狗,搜狗)
     */
    public void test302() {
        String result = dissect("古哥&&谷歌&&收狗&&搜狗");
        assertEquals("古哥/谷歌/收狗/搜狗", result);
    }

    /**
     * 多个字的非词汇串
     */
    public void test303() {
        String result = dissect("这是鸟语：玉鱼遇欲雨");
        assertEquals("这是/鸟语/玉鱼/鱼遇/遇欲/欲雨", result);
    }

    /**
     * 两个词语之间有一个非词汇的字(真)
     */
    public void test304() {
        String result = dissect("朋友真背叛了你了!");
        assertEquals("朋友/真/背叛", result);
    }

    /**
     * 两个词语之间有一个非词汇的字符串(盒蟹)
     */
    public void test305() {
        String result = dissect("建设盒蟹社会");
        assertEquals("建设/盒蟹/社会", result);
    }

    /**
     * 两个词语之间有多个非词汇的字符串(盒少蟹)
     */
    public void test306() {
        String result = dissect("建设盒少蟹社会");
        assertEquals("建设/盒少/少蟹/社会", result);
    }

    // --------------------------------------------------------------
    // 不包含小数点的汉字数字
    // --------------------------------------------------------------


    /**
     * 单个汉字数字
     */
    public void test400() {
        String result = dissect("二");
        assertEquals("2", result);
    }

    /**
     * 两个汉字数字
     */
    public void test61() {
        String result = dissect("五六");
        assertEquals("56", result);
    }

    /**
     * 多个汉字数字
     */
    public void test62() {
        String result = dissect("三四五六");
        assertEquals("3456", result);
    }

    /**
     * 十三
     */
    public void test63() {
        String result = dissect("十三");
        assertEquals("13", result);
    }

    /**
     * 二千
     */
    public void test65() {
        String result = dissect("二千");
        assertEquals("2000", result);
    }

    /**
     * 两千
     */
    public void test651() {
        String result = dissect("两千");
        assertEquals("2000", result);
    }
    /**
     * 两千
     */
    public void test6511() {
        String result = dissect("两千个");
        assertEquals("2000/个", result);
    }

    /**
     * 2千
     */
    public void test652() {
        String result = dissect("2千");
        assertEquals("2000", result);
    }

    /**
     *
     */
    public void test653() {
        String result = dissect("3千万");
        assertEquals("30000000", result);
    }

    /**
     *
     */
    public void test654() {
        String result = dissect("3千万个案例");
        assertEquals("30000000/个/案例", result);
    }


    /**
     *
     */
    public void test64() {
        String result = dissect("千万");
        assertEquals("千万", result);
    }

    public void test66() {
        String result = dissect("两两");
        assertEquals("两两", result);
    }

    public void test67() {
        String result = dissect("二二");
        assertEquals("22", result);
    }

    public void test68() {
        String result = dissect("2.2两");
        assertEquals("2.2/两", result);
    }

    public void test69() {
        String result = dissect("二两");
        assertEquals("2/两", result);
    }


    public void test690() {
        String result = dissect("2两");
        assertEquals("2/两", result);
    }

    public void test691() {
        String result = dissect("2千克");
        assertEquals("2000/克", result);
    }

    public void test692() {
        String result = dissect("2公斤");
        assertEquals("2/公斤", result);
    }

    public void test693() {
        String result = dissect("2世纪");
        assertEquals("2/世纪", result);
    }

    public void test7() {
        String result = dissect("哪怕二");
        assertEquals("2", result);
    }

}
