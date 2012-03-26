package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class TermsSelector {

	private String inFile = null;
	private String wordFile=null;

	public void run() throws IOException {
		BufferedReader reader = null;
		FileInputStream file = new FileInputStream(new File("c:/mdt.txt"));
		System.out.println("以行为单位读取文件内容，一次读一整行：");
		reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));

		File out = new File("c:/data/mdt2.txt");
		BufferedWriter w = new BufferedWriter(new FileWriter(out));
		String tempString = "";

		int index = 0;
		int cnt = 0;
		Set<Integer> terms = new HashSet<Integer>();
		while ((tempString = reader.readLine()) != null) {
			String[] strArray = tempString.split(",");
			if (Integer.parseInt(strArray[1]) == index) {
				cnt++;
			} else {
				if (cnt < 100) {
					System.out.println("add"+index);
					terms.add(index);
				}else{
					System.out.println("remove"+index);
				}
				index++;
				cnt = 0;
			}
		}
		reader.close();
		BufferedReader reader11 = null;
		FileInputStream file11 = new FileInputStream(new File("c:/mdt.txt"));
		System.out.println("以行为单位读取文件内容，一次读一整行：");
		reader11 = new BufferedReader(new InputStreamReader(file11, "UTF-8"));
		String newStr="";
		//replace
		Hashtable<Integer,Integer> table=new Hashtable<Integer,Integer>();
		int i=0;
		for(Iterator<Integer> it=terms.iterator();it.hasNext();){
			table.put(it.next(),i);
			i++;
		}
		while ((newStr = reader11.readLine()) != null) {
			String[] array=newStr.split(",");
			if(terms.contains(Integer.parseInt(array[1]))){
			     String s=array[0]+","+table.get(Integer.parseInt(array[1]))+","+array[2];
			     w.write(s+"\n");
			}
		}

		FileInputStream file1 = new FileInputStream(new File("c:/data/term.txt"));
		System.out.println("以行为单位读取文件内容，一次读一整行：");
		BufferedReader reader1 = new BufferedReader(new InputStreamReader(file1, "UTF-8"));
		String []words=new String[12000];

		File out1 = new File("c:/data/term2.txt");
		BufferedWriter w1 = new BufferedWriter(new FileWriter(out1));
		int j=0;
		while((tempString=reader1.readLine())!=null){
			words[j]=tempString;
			j++;
		}
		for(Iterator it=table.entrySet().iterator();it.hasNext();){
			Map.Entry<Integer, Integer> map=(Entry<Integer, Integer>) it.next();
			Integer orig=map.getKey();
			Integer newId=map.getValue();
			w1.write(newId+","+words[orig]+"\n");		
		}
		w1.flush();
		w1.close();

	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TermsSelector s=new TermsSelector();
		s.run();

	}

	public void setInFile(String inFile) {
		this.inFile = inFile;
	}

	public String getInFile() {
		return inFile;
	}

}
