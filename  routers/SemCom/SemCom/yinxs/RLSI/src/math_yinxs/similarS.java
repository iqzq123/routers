/*
 * 计算两个句子的相似度，前提是都已经分好词的结果。
 * 例如 “我 是 中国 人” 中间使用空格分开。
 */
package math_yinxs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class similarS {
	public String s1;
	public String s2;
//	public similarS() {
//	}
	public similarS(String ts1, String ts2) {
		this.s1 = ts1;
		this.s2 = ts2;
	}
	/*
	 * 计算两个向量的余弦相似度
	 * 即 cos<a,b> = a*b/(|a|*|b|)
	 */
	public double cosine(List<Double> a, List<Double> b) {
		Object[] ay = a.toArray();
		Object[] by = b.toArray();
		assert(a.size() == b.size());
		double retval = 0.0;
		double sum = 0.0;
		for(double ta : a) {
			sum += (ta*ta);
		}
		double lnorm = Math.sqrt(sum);
		sum = 0.0;
		for(double tb : b) {
			sum += (tb*tb);
		}
		double rnorm = Math.sqrt(sum);
		sum = 0.0;
		for(int i=0; i< a.size(); ++i) {
			sum += ((double)ay[i]*(double)by[i]);
		}
		double innerProduct = Math.sqrt(sum);
		// assert((lnorm != 0) && (rnorm != 0));
		retval = innerProduct / (lnorm * rnorm);
		return retval;
	}
	/*
	 * 计算相似度得分
	 */
	public double SScore() {
		double retval = 0.0;
		HashMap<String, Integer> lwords = new HashMap<String, Integer>();
		HashMap<String, Integer> rwords = new HashMap<String, Integer>();
		String[] left = this.s1.split(" ");
		String[] right = this.s2.split(" ");
		for(String tleft : left) {
			if(lwords.containsKey(tleft)) {
				lwords.put(tleft, lwords.get(tleft)+1);
			} else {
				lwords.put(tleft, 1);
			}
		}
		for(String tright: right) {
			if(rwords.containsKey(tright)) {
				rwords.put(tright, rwords.get(tright)+1);
			} else {
				rwords.put(tright, 1);
			}
		}
		Set<String> set = lwords.keySet();
		Set<String> rset = rwords.keySet();
		set.addAll(rset);
		List<Double> a = new ArrayList<Double>();
		List<Double> b = new ArrayList<Double>();
		for(String key: set) {
			if(lwords.containsKey(key)) {
				a.add((double)lwords.get(key));
			} else {
				a.add(0.0);
			}
			if(rwords.containsKey(key)) {
				b.add((double)rwords.get(key));
			}else {
				b.add(0.0);
			}
		}
		retval = this.cosine(a, b);
		return retval;
	}
}
