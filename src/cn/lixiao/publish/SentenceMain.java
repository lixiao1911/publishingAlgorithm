package cn.lixiao.publish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.ls.LSException;

/**
 * @类名称: SentenceMain.java
 * @类描述： 句对核心引擎,可以按照编辑习惯分割中英文语句（非保密）
 * @创建人: 李枭
 * @创建时间： 2017年3月6日 上午10:28:33
 * @修改人：
 * @修改时间： 2017年3月6日 上午10:28:33
 * @修改备注：
 */
public class SentenceMain {
    private static Logger log = Logger.getLogger(SentenceMain.class.getName());
    private static final int MIN_THRESHOLD = 1;// 设置分割粒度，为保证能分割短句此处设置1
    private static final int MAX_THRESHOLD = 100;
    private static Pattern senpattern = Pattern
            .compile("(^|(?![.!…]))[“]?[^.!…]+[.!…][…]?[”]?");// 匹配。！？以及带后引号的。！？此处有缺陷需要优化lx20170306
    private static Pattern senpattern2 = Pattern
            .compile("(^|(?![。！……]))[“]?[^。！……]+[。！？][……]?[”]?");
    private static Pattern senpatterntest = Pattern
    // .compile("(^|(?![.!?…。！？……]))[“]?[^.!?…。！？……]+[.!?…。！？……][………]?[”]?");
            .compile("(^|(?![.!?…。！？……]))[\"]?[“]?[^.!?…。！？……]+[.!?…。！？……][………]?[”]?[\"]?");
    private static Pattern whitespacePattern = Pattern.compile("^\\s*$");// 匹配空格键
    private List<String> list = new ArrayList<String>();

    public SentenceMain() {
    };

    public static void main(String[] args) {
        SentenceMain sMain = new SentenceMain(      
                " “找出企业所投资的项目组合，就可以归结出企业所采取的作法，也就是实际上的策略。事实上，要落实组织所认定的策略，就必须将它转化成一连串工作，也就是所谓的项目。项目是企业在持续经营的过程中，为了达成特定目标所进行的临时计划。项目、特别是平凡的项目，正是策略执行的真正施力点。只有透过项目，才能创造出新产品、新服务、新制度、新技能、新联盟，或是满足内、外顾客需求的新机制。企业的项目组合造就了它未来的价值。项目组合，亦即公司选择要投资的一连串项目及计划，就是变革的推动者，而变革计划成功与否，就取决于选择及管理变革项目的能力。成功的策略执行，必须将项目组合与营运策略紧密整合在一起。” ");        
           }

    public SentenceMain(String str) {
        SegSentence(str);
    }

    public List<String> SegSentence(String str) {
        log.info("-------seg sentences start by lx's engine-------");
        StringBuffer sb = new StringBuffer(MAX_THRESHOLD);
        Matcher m = senpatterntest.matcher(str);
        /* 按照句子结束符分割句子 */
        String[] substrs = senpatterntest.split(str);
        log.info("seging sentence,split size=" + substrs.length);
        /* 将句子结束符连接到相应的句子后 */
        if (substrs.length > 0) {
            int count = 0;
            while (count < substrs.length) {
                if (m.find()) {
                    substrs[count] += m.group();
                }
                count++;
            }
        }
        list = Arrays.asList(substrs);
        List<String> arraylist = new ArrayList<String>(list);// 转化arraylist增强可操作性
        String last = arraylist.get(arraylist.size() - 1);
        if (null == last || "".equals(last)
                || whitespacePattern.matcher(last).matches()) {// 正则匹配空格，段尾自带空格要去除
            arraylist.remove(arraylist.size() - 1);// 删除最后一个空白分割
        }
        log.info("-------seg sentence engine end ,sentence size="
                + arraylist.size() + "------");
        for (Iterator i = arraylist.iterator(); i.hasNext();) {
            System.out.println(i.next());
        }

        List<String> ls = combineST(arraylist);
        log.info("-------combinest，size:" + ls.size() + "-------");
        for (Iterator i = ls.iterator(); i.hasNext();) {
            System.out.println(i.next());
        }
        //return arraylist;
        return ls;
    }

    String marks_en = "\"";
    String marks_cn = "“";

    // 防止误分割成对符号问题算法
    public List<String> combineST(List<String> ls) {
        if (ls.size() > 1) {
            LinkedList<Character> ll = new LinkedList<Character>();// 实现一个栈
            int ins = 0;
            int ins2 = 0;
            StringBuffer data = new StringBuffer();
            for (int i = 0; i < ls.size(); i++) {
                char[] chs = ls.get(i).toCharArray();
                int tt = 0;
                if (ins != 0) {
                    ins++;
                }
                for (char c : chs) {
                    if(c == "“".toCharArray()[0]||c == "”".toCharArray()[0]){
                    if (c == "“".toCharArray()[0]) {
                        ll.push("”".toCharArray()[0]);
                        ins++;
                        tt++;
                    }
                    if (!ll.isEmpty()) {
                        if (c == ll.peek()) {// 栈顶元素弹出
                            System.out.println("2pop---");
                            ll.pop();
                            ins--;
                            tt--;
                        }
                    }
                    }
                    // -------------英文状态前后引号相同的栈策略---------
                    if (c == marks_en.toCharArray()[0]) {
                        if (!ll.isEmpty()) {
                            if (c == ll.peek()) {// 栈顶元素弹出
                                System.out.println("pop----");
                                ll.pop();
                                ins--;
                                tt--;
                            }
                        } else {
                            ll.push(marks_en.toCharArray()[0]);
                            System.out.println("push--->");
                            ins++;
                            tt++;
                        }
                    }
                    // ------------------------------------------------
                }
                ins2 = tt;
                System.out.println("ins2:"+ins2);
                if (!ll.isEmpty()) {
                    data = data.append(ls.get(i));
                } else {
                    if (ins2 != 0) {
                        if (ins > 0) {
                            System.out.println("ins:"+ins);
                            data = data.append(ls.get(i));
                            Iterator<String> iterator=ls.iterator();
                            for (int t = ins - 1; t > 0; t--) {
                                ls.remove(t-1);
                            }
                            ls.set(i - (ins - 1), data.toString());
                            ls.remove(i - ins);
                            i = i - ins;
                            data.setLength(0);
                            ins = 0;
                        }

                    } else {
                        ins = 0;
                    }
                }
            }
        }
        return ls;
    }

}
