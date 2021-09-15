import com.github.houbb.opencc4j.util.ZhConverterUtil;
import util.BCConvert;
import util.PinYinConvert;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//用来过滤待检测文件的类
public class WordFilter {

	private static Map<String, String> sensitiveWordMap = new HashMap(); //记录敏感词类的映射关系
	private static String[] args; //命令行参数
	private static int resultCount = 0; //记录过滤结果数量
	private static int lineCount = 1; // 记录当前过滤文件中的行数
	private static final FilterSet set = new FilterSet(); // 存储首字
	private static final Map<Integer, WordNode> nodes = new HashMap<Integer, WordNode>(1024, 1); // 存储节点
	private static final Set<Integer> stopwdSet = new HashSet<>(); // 停顿词
	private static List<String> stopwdList;



	public static List<String> getStopwdList() {
		return stopwdList;
	}

	public static void setArgs(String[] args) {
		WordFilter.args = args;
	}

	public static Map<String, String> getSensitiveWordMap() {
		return sensitiveWordMap;
	}

	public static int getResultCount() {
		return resultCount;
	}
	//初始化，获取敏感词文件和停顿词（当遍历到停顿词时直接跳过，解决敏感词中插入字符的情况）
	public static void init() {
		addSensitiveWord(readWordFromFile(args[0]));
		String[] stopwd = new String[] {" ", "…", "!", ".", ",", "#", "$", "%", ":", "&", "*", "(", ")", "|", "?", "/", "@", "\"", "'", ";", "[", "]", "{", "}", "+", "~", "-", "_", "=", "^", "<", ">", " ", "　", "！", "。", "，", "￥", "（", "）", "？", "、", "“", "‘", "；", "【", "】", "——", "……", "《", "》", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
		stopwdList = Arrays.asList(stopwd);
		addStopWord(stopwdList);
	}
	/**
	 * 判断字符串中是否包含中文
	 * @param str
	 * 待校验字符串
	 * @return 是否为中文
	 * @warn 不能校验是否为中文标点符号
	 */
	public static boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
	//递归生成敏感词各种排列组合变形
	public static void sensitiveWordCombination(List<String> words, String buf, int type, String curr, int index) {
		if (index == -1) {
			WordFilter.sensitiveWordCombination(words, buf, 1, "", 0);
			WordFilter.sensitiveWordCombination(words, buf, 2, "", 0);
			WordFilter.sensitiveWordCombination(words, buf, 3, "", 0);
			WordFilter.sensitiveWordCombination(words, buf, 4, "", 0);
		}
		else if (index < buf.length()) {
			if (type == 1) {
				curr += buf.substring(index, index + 1);
				if (index == buf.length() - 1) {
					words.add(curr);
					sensitiveWordMap.put(curr, buf);
				}
				else {
					sensitiveWordCombination(words, buf, 1, curr, index + 1);
					sensitiveWordCombination(words, buf, 2, curr, index + 1);
					sensitiveWordCombination(words, buf, 3, curr, index + 1);
					sensitiveWordCombination(words, buf, 4, curr, index + 1);
				}
			}
			if (type == 2) {
				curr += PinYinConvert.getPinyin(buf.substring(index, index + 1));
				if (index == buf.length() - 1) {
					words.add(curr);
					sensitiveWordMap.put(curr, buf);
				}
				else {
					sensitiveWordCombination(words, buf, 1, curr, index + 1);
					sensitiveWordCombination(words, buf, 2, curr, index + 1);
					sensitiveWordCombination(words, buf, 3, curr, index + 1);
					sensitiveWordCombination(words, buf, 4, curr, index + 1);
				}
			}
			if (type == 3) {
				curr += PinYinConvert.getFirstPinyin(buf.substring(index, index + 1));
				if (index == buf.length() - 1) {
					words.add(curr);
					sensitiveWordMap.put(curr, buf);
				}
				else {
					sensitiveWordCombination(words, buf, 1, curr, index + 1);
					sensitiveWordCombination(words, buf, 2, curr, index + 1);
					sensitiveWordCombination(words, buf, 3, curr, index + 1);
					sensitiveWordCombination(words, buf, 4, curr, index + 1);
				}
			}
			if (type == 4) {
				curr += ZhConverterUtil.toTraditional(buf.substring(index, index + 1));
				if (index == buf.length() - 1) {
					words.add(curr);
					sensitiveWordMap.put(curr, buf);
				}
				else {
					sensitiveWordCombination(words, buf, 1, curr, index + 1);
					sensitiveWordCombination(words, buf, 2, curr, index + 1);
					sensitiveWordCombination(words, buf, 3, curr, index + 1);
					sensitiveWordCombination(words, buf, 4, curr, index + 1);
				}
			}
		}
	}

	/**
	 * 增加敏感词
	 * 
	 * @param path
	 * @return
	 */
	public static List<String> readWordFromFile(String path) {
		List<String> words;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
			words = new ArrayList<>(1200);
			for (String buf = ""; (buf = br.readLine()) != null;) {
				if (buf == null || buf.trim().equals(""))
					continue;
				if (isContainChinese(buf)) {
					sensitiveWordCombination(words, buf, 1, "", -1);
				}
				else {
					String temp = "";
					for (int i = 0; i < buf.length(); i++) {
						temp += charConvert(buf.charAt(i));
					}
				}
				sensitiveWordMap.put(buf.toLowerCase(), buf);
				words.add(buf);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
			}
		}
		return words;
	}

	/**
	 * 增加停顿词
	 * 
	 * @param words
	 */
	private static void addStopWord(final List<String> words) {
		if (!isEmpty(words)) {
			char[] chs;
			for (String curr : words) {
				chs = curr.toCharArray();
				for (char c : chs) {
					stopwdSet.add(charConvert(c));
				}
			}
		}
	}

	//根据敏感词组合添加DFA树的节点
	private static void addSensitiveWord(final List<String> words) {
		if (!isEmpty(words)) {
			char[] chs;
			int fchar;
			int lastIndex;
			WordNode fnode; // 首字母节点
			for (String curr : words) {
				chs = curr.toCharArray();
				fchar = charConvert(chs[0]);
				if (!set.contains(fchar)) {// 没有首字定义
					set.add(fchar);// 首字标志位 可重复add,反正判断了，不重复了
					fnode = new WordNode(fchar, chs.length == 1);
					nodes.put(fchar, fnode);
				} else {
					fnode = nodes.get(fchar);
					if (!fnode.isLast() && chs.length == 1)
						fnode.setLast(true);
				}
				lastIndex = chs.length - 1;
				for (int i = 1; i < chs.length; i++) {
					fnode = fnode.addIfNoExist(charConvert(chs[i]), i == lastIndex);
				}
			}
		}
	}
	//遍历待检测文件，记录结果
	public static final StringBuilder doFilter(final String src) {
		StringBuilder result = new StringBuilder();
		if (set != null && nodes != null) {
			WordNode node = null;
			StringBuilder chs = new StringBuilder(src);
			for (int i = 0; i < chs.length(); i++) {
				int currc = charConvert(chs.charAt(i));
				if (currc == '\n')
					lineCount++;
				if (!set.contains(currc)) {
					continue;
				}
				node = nodes.get(currc);
				if (node == null)
					continue;
				boolean couldMark = false;
				int markNum = -1;
				if (node.isLast()) {
					couldMark = true;
					markNum = 0;
				}
				int k = i;
				int cpcurrc = currc;
				for (; ++k < chs.length();) {
					int temp = charConvert(chs.charAt(k));
					if (temp == cpcurrc)
						continue;
					if (stopwdSet != null && stopwdSet.contains(temp))
						continue;
					node = node.querySub(temp);
					if (node == null)
						break;
					if (node.isLast()) {
						couldMark = true;
						markNum = k - i;
					}
					cpcurrc = temp;
				}
				if (couldMark) {
					result.append("Line").append(lineCount).append(": ");
					String temp = "";
					String temp2 = "";
					for (k = 0; k <= markNum; k++) {
						temp2 += chs.charAt(k + i);
						String s = Character.toString(chs.charAt(k + i));
						if (!stopwdList.contains(s))
							temp += s.toLowerCase();
					}
					result.append("<").append(sensitiveWordMap.get(temp)).append("> ").append(temp2).append('\n');
					resultCount++;
					i = i + markNum;
				}
			}
			return result;
		}

		return null;
	}
	/**
	 * 大写转化为小写 全角转化为半角
	 * 
	 * @param src
	 * @return
	 */
	private static int charConvert(char src) {
		int r = BCConvert.qj2bj(src);
		return (r >= 'A' && r <= 'Z') ? r + 32 : r;
	}

	/**
	 * 判断一个集合是否为空
	 * 
	 * @param col
	 * @return
	 */
	public static <T> boolean isEmpty(final Collection<T> col) {
		if (col == null || col.isEmpty()) {
			return true;
		}
		return false;
	}
}
