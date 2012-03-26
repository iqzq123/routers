package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class BuptImporter {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		File outfile = new File("c:/buptSci.txt");
		BufferedWriter w = new BufferedWriter(new FileWriter(outfile));

		File dir = new File("f:/data/ss");
		String[] files = dir.list();
		int cnt=0;
		for (int a = 0; a < files.length; a++) {
			String fileName = dir.getAbsolutePath()+"/"+files[a];
			BufferedReader reader = null;
			FileInputStream file = new FileInputStream(new File(fileName));
			System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			String authors = "";
			String ti = "";
			String ab = "";
			while ((tempString = reader.readLine()) != null) {
				// System.out.println(line+":"+tempString + "\n");
				line++;

				boolean finished = false;
				if (tempString.length() < 2) {
					continue;
				}
				String prefix = (String) tempString.subSequence(0, 2);
				// System.out.println("prefix:"+prefix);
				if (prefix.equals("AU")) {
					String au = tempString.substring(3);
					authors += au + "|";
					String nextAu = "";
					nextAu = reader.readLine();
					String nextPre = (String) nextAu.subSequence(0, 1);
					while (nextPre.equals(" ")) {
						authors += nextAu + "|";
						nextAu = reader.readLine();
						if(nextAu==null){
							break;
						}
						if(nextAu.length()<2){
							continue;
						}
						nextPre = (String) nextAu.subSequence(0, 1);
					}
				}
				if (prefix.equals("TI")) {
					String subTi = tempString.substring(3);
					ti += subTi + " ";
					String nextTi = "";
					nextTi = reader.readLine();
					String nextPre = (String) nextTi.subSequence(0, 1);
					while (nextPre.equals(" ")) {
						ti += nextTi + " ";
						nextTi = reader.readLine();
						if(nextTi.length()<2){
							continue;
						}
						nextPre = (String) nextTi.subSequence(0, 1);
					}
				}

				if (prefix.equals("AB")) {
					String subAb = tempString.substring(3);
					ab += subAb;
					String nextAb = "";
					nextAb = reader.readLine();
					String nextPre = (String) nextAb.subSequence(0, 1);
					while (nextPre.equals(" ")) {
						ab += nextAb;
						nextAb = reader.readLine();
						if(nextAb.length()<2){
							continue;
						}
						nextPre = (String) nextAb.subSequence(0, 1);
					}
					finished = true;
				}

				if (finished) {

					String out = authors + "#" + ti + "#" + ab;
					String out1 = "";
					String[] array = out.split("\\s");
					for (int i = 0; i < array.length; i++) {
						out1 += array[i] + "+";
					}
					char[] cArray = out1.toCharArray();
					char[] cArray1 = new char[cArray.length];
					cArray1[0] = cArray[0];
					int index = 1;

					for (int i = 1; i < cArray.length; i++) {

						if (cArray[i] == '+'
								&& (cArray[i - 1] == '+'
										|| cArray[i - 1] == ',' || cArray[i - 1] == '|')) {
							continue;
						} else {
							cArray1[index] = cArray[i];
							index++;
						}
					}
					String out2 = String.valueOf(cArray1, 0, index - 1);
					cnt++;
					System.out.println("cnt:"+cnt);
					if(cnt==62){
						System.out.println("xx");
					}
					w.write(out2+"\n");
					authors = "";
					ti = "";
					ab = "";
				}

			}
		}
		w.flush();
		w.close();

	}

}
