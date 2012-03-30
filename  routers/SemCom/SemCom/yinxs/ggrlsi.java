package math_yinxs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.*;

public class ggrlsi {
	List<MatElement> tData = null;
	int M = 11771;
	int N = 28569;
	int K = 4;
	int trainTime = 1;
	GVector UVector[] = null;
	GVector VVector[] = null;
	GVector MaxFreqWordForAFile = null;
	GVector MaxCntFileForAWord = null;
	double lamda = 0.1;
	double alpha = 0.01;
	double err = 0.0;
	private String[] words = new String[12000];

	public ggrlsi() {
		this.tData = new ArrayList<MatElement>();
		this.UVector = new GVector[M];
		for (int i = 0; i < M; ++i) {
			this.UVector[i] = new GVector(K);
		}
		this.VVector = new GVector[N];
		for (int i = 0; i < N; ++i) {
			this.VVector[i] = new GVector(K);
		}
		MaxFreqWordForAFile = new GVector(N);
		MaxCntFileForAWord = new GVector(M);
	}

	public void load(String fName, String fTerm) {
		FileReader fr;
		FileReader fr2;
		try {
			fr = new FileReader(fName);
			BufferedReader br = new BufferedReader(fr);
			fr2 = new FileReader(fTerm);
			BufferedReader br2 = new BufferedReader(fr2);
			while (br.ready()) {
				String tmpLine = br.readLine();
				int docId = Integer.parseInt(tmpLine.split(",")[0]);
				int termId = Integer.parseInt(tmpLine.split(",")[1]);
				double weight = Double.parseDouble(tmpLine.split(",")[2]);
				this.tData.add(new MatElement(docId, termId, weight));
				this.MaxCntFileForAWord.setElement(termId,
						(MaxCntFileForAWord.getElement(termId)) + 1);
				if (this.MaxFreqWordForAFile.getElement(docId) < weight) {
					this.MaxFreqWordForAFile.setElement(docId, weight);
				}
			}
			int i=0;
			while(br2.ready()) {
				words[i++] = br2.readLine();
			}
			br.close();
			fr.close();
			br2.close();
			fr2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double tf_idf(int N, double n, double freq_ij, double freq_max) {
		if((freq_max != 0) && (n != 0)) {
			return (freq_ij / freq_max) * Math.log(1.0 * N / n);
		}else {
			return 0.0;
		}
	}

	public void updateU(MatElement mt) {
		int iTerm = mt.getJ();
		int jDoc = mt.getI();
		double w = mt.getVal();
		double tfIdf = tf_idf(N, MaxCntFileForAWord.getElement(iTerm), w,
				MaxFreqWordForAFile.getElement(jDoc));
		double dotP = UVector[iTerm].dot(VVector[jDoc]);
		double error = tfIdf * (w - dotP);
		GVector VTmp = new GVector(VVector[jDoc]);
		GVector UTmp = new GVector(UVector[iTerm]);
		VTmp.scale(-error);
		UTmp.scale(lamda);
		UTmp.add(VTmp);
		UTmp.scale(alpha);
		UVector[iTerm].sub(UTmp);
		for(int i=0; i < K; ++i) {
			System.out.print(i+"\t"+UVector[iTerm].getElement(i)+ "\t");
			if(UVector[iTerm].getElement(i) == Double.NaN) {
				System.out.println("Here are something in U");
			}
		}
		System.out.println();
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public void updateV(MatElement mt) {
		int iTerm = mt.getJ();
		int jDoc = mt.getI();
		double w = mt.getVal();
		double tfIdf = tf_idf(N, MaxCntFileForAWord.getElement(iTerm), w,
				MaxFreqWordForAFile.getElement(jDoc));
		double dotP = UVector[iTerm].dot(VVector[jDoc]);
		double error = tfIdf * (w - dotP);
		GVector VTmp = new GVector(VVector[jDoc]);
		GVector UTmp = new GVector(UVector[iTerm]);
		UTmp.scale(-error);
		VTmp.scale(lamda);
		VTmp.add(UTmp);
		VTmp.scale(alpha);
		VVector[jDoc].sub(VTmp);
		for(int i=0; i < K; ++i) {
			System.out.print(i+"\t"+VVector[jDoc].getElement(i)+"\t");
			if(VVector[jDoc].getElement(i) == Double.NaN) {
				System.out.println("Here are something in V");
			}
		}
		System.out.println();
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

//	public void getError() {
//		this.err = 0.0;
//		int cnt = 0;
//		for (MatElement mt : tData) {
//			this.err += Math.abs(mt.getVal()
//					- UVector[mt.getJ()].dot(VVector[mt.getI()]));
//			cnt++;
//		}
//		if (cnt != 0) {
//			this.err /= cnt;
//		}
//	}

	public void randomV() {
		for (int i = 0; i < N; ++i) {
			for (int j = 0; j < K; ++j) {
				VVector[i].setElement(j, Math.random() * 1);
			}
		}
	}

	public void ggRlsi() throws NumberFormatException, IOException {
		randomV();
//		while ((this.trainTime--) != 0) {
//			FileReader fr = new FileReader(fName);
//			BufferedReader br = new BufferedReader(fr);
//			while(br.ready()) {
//				String tmpLine = br.readLine();
//				int docId = Integer.parseInt(tmpLine.split(",")[0]);
//				int termId = Integer.parseInt(tmpLine.split(",")[1]);
//				double weight = Double.parseDouble(tmpLine.split(",")[2]);
//				MatElement mt = new MatElement(docId, termId, weight);
//				updateU(mt);
//				updateV(mt);
//			}
		int i = 0;
		while((this.trainTime--) != 0) {
			System.out.println(this.trainTime);
			for (MatElement mt : tData) {
				if(i == 101119) {
					System.out.println("here.");
				}
				if((i++)%100 == 0) {
					System.out.println(mt);
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
				System.out.println(i);
				updateU(mt);
				updateV(mt);
				//getError();
				//System.out.println(this.trainTime + "\tError:\t" + this.err);
			}
		}
	}

	public double[] getMaxK(double[] array, int k) {
		double[] topArray = new double[k];
		for (int i = 0; i < k; i++) {
			topArray[i] = array[i];
		}
		// build heap
		for (int i = k / 2 - 1; i >= 0; i--) {
			HeapAdjust(topArray, i, k - 1);
		}
		for (int i = k; i < array.length; i++) {
			if (array[i] > topArray[0]) {
				topArray[0] = array[i];
				HeapAdjust(topArray, 0, k - 1);
			}
		}
		return topArray;
	}

	private void HeapAdjust(double[] toparray, int top, int len) {
		double tmp = toparray[top];
		for (int i = top * 2; i <= len; i *= 2) {
			if (toparray[i] > toparray[i + 1] && i < len) {
				i++;
			}
			if (tmp > toparray[i]) {
				toparray[top] = toparray[i];
				top = i;
			} else {
				break;
			}
		}
	}

	public void outPutTopics() {
		for (int a = 0; a < this.K; a++) {
			double[] array = new double[this.VVector.length];
			for (int i = 0; i < this.VVector.length; i++) {
				array[i] = this.VVector[i].getElement(a);
//				if(this.VVector[i].getElement(a) == Double.NaN) {
//					System.out.println("fuck!");
//				}
			}
			double minValue = this.getMaxK(array, 20)[0];
			String[] keywords = new String[20];
			int index = 0;
			for (int i = 0; i < this.VVector.length; i++) {
				if (this.VVector[i].getElement(a) > minValue) {
					keywords[index] = this.words[i];
					index++;
					if(index == 19) {
						break;
					}
				}
			}
			System.out.println("topic" + a + ":");
			for (int i = 0; i < keywords.length; i++) {
				System.out.println(keywords[i]);
			}
		}
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		ggrlsi test = new ggrlsi();
		test.load("mdt.txt","term.txt");
		test.ggRlsi();
		test.outPutTopics();
	}
}

