package com.XWH;

import com.github.houbb.opencc4j.core.ZhConvert;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.junit.jupiter.api.Test;
import util.PinYinConvert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TestSensitiveWdFilter {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int sum = 0;
        int temp;
        while ((temp = scanner.nextInt()) != -1) {
            sum += temp;
        }
        System.out.println(sum);
    }
    @Test
    public void test() throws IOException {
        List<String> list1 = new ArrayList<>();
        readFile("D:\\敏感词过滤\\src\\main\\resources\\ans.txt", list1);
        List<String> list2 = new ArrayList<>();
        readFile("D:\\敏感词过滤\\src\\main\\resources\\ans1.txt", list2);
        int count = 0;
        for (String s : list1) {
            if (!list2.contains(s)) {
                System.out.println(s);
                count++;
            }
        }
        System.out.println(count);
    }
    public static void readFile(String path, List<String> list) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
        for (String buf = ""; (buf = br.readLine()) != null;) {
            list.add(buf);
        }
    }

    public static void sensitiveWordCombination1(List<String> words, String buf, int type, String curr, int index) {
        if (index < buf.length()) {
            if (type == 1) {
                curr += buf.substring(index, index + 1);
                if (index == buf.length() - 1)
                    words.add(curr);
                else {
                    sensitiveWordCombination1(words, buf, 1, curr, index + 1);
                    sensitiveWordCombination1(words, buf, 2, curr, index + 1);
                    sensitiveWordCombination1(words, buf, 3, curr, index + 1);
                    sensitiveWordCombination1(words, buf, 4, curr, index + 1);
                }
            }
            if (type == 2) {
                curr += PinYinConvert.getPinyin(buf.substring(index, index + 1));
                if (index == buf.length() - 1)
                    words.add(curr);
                else {
                    sensitiveWordCombination1(words, buf, 1, curr, index + 1);
                    sensitiveWordCombination1(words, buf, 2, curr, index + 1);
                    sensitiveWordCombination1(words, buf, 3, curr, index + 1);
                    sensitiveWordCombination1(words, buf, 4, curr, index + 1);
                }
            }
            if (type == 3) {
                curr += PinYinConvert.getFirstPinyin(buf.substring(index, index + 1));
                if (index == buf.length() - 1)
                    words.add(curr);
                else {
                    sensitiveWordCombination1(words, buf, 1, curr, index + 1);
                    sensitiveWordCombination1(words, buf, 2, curr, index + 1);
                    sensitiveWordCombination1(words, buf, 3, curr, index + 1);
                    sensitiveWordCombination1(words, buf, 4, curr, index + 1);
                }
            }
            if (type == 4) {
                curr += ZhConverterUtil.toTraditional(buf.substring(index, index + 1));
                if (index == buf.length() - 1)
                    words.add(curr);
                else {
                    sensitiveWordCombination1(words, buf, 1, curr, index + 1);
                    sensitiveWordCombination1(words, buf, 2, curr, index + 1);
                    sensitiveWordCombination1(words, buf, 3, curr, index + 1);
                    sensitiveWordCombination1(words, buf, 4, curr, index + 1);
                }
            }
        }
    }

}