/*
 * 读取分好词的每个文件
 * 寻找topk相似度
 */
package math_yinxs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class findSimilar {
	public String fName;
	public String sentence;
	public findSimilar(String f, String s) {
		assert(f != null);
		fName = f;
		sentence = s;
	}
	public ArrayList<Integer> topK(int k) throws IOException {
		ArrayList<Double> ad = new ArrayList<Double>();
		FileReader fr = new FileReader(fName);
		BufferedReader br = new BufferedReader(fr);
		while(br.ready()) {
			String tmpLine = br.readLine();
			similarS tmpS = new similarS(sentence, tmpLine);
			ad.add(tmpS.SScore());
		}
		br.close();
		fr.close();
		Object[] ady = ad.toArray();
		Object tmp;
		int[] index = new int[ad.size()];
		int itmp;
		for(int i=0; i<ad.size(); ++i) {
			index[i] = i;
		}
		for(int i=0; i < k; ++i) {
			for(int j=0; i < ad.size()-1; ++j) {
				if((double)ady[j] > (double)ady[j+1]) {
					tmp = ady[j];
					ady[j] = ady[j+1];
					ady[j+1] = tmp;
					itmp = index[j];
					index[j] = index[j+1];
					index[j+1] = itmp;
				}
			}
		}
		ArrayList<Integer> ai = new ArrayList<Integer>();
		for(int i=ad.size()-1; i > ad.size()-k; --i) {
			System.out.print(index[i]+"\t");
			ai.add(index[i]);
		}
		System.out.println();
		return ai;
	}
	
	public int best1(ArrayList<Integer> topk) {
		
		return 0;
	}
}
