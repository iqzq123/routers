/**
 * @file grlsi.java
 * @brief 
 * @details
 * @author yinxusen@gmail.com
 * @date 2012-3-27
 */

package math_yinxs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class grlsi {
	public class Doc {
		public int index;
		public double frequency;
		public double tf_idf;
		public Doc(int i, double j) {
			index = i;
			frequency = j;
		}
	}
	
	public class Term {
		public int index;
		public double frequency;
		public double tf_idf;
		public Term(int i, double j) {
			index = i;
			frequency = j;
		}
	}
	
	public int M = 11771;
	public int N = 28569;
	public int K = 4;
	public double [][] termTopic = null;
	public double [][] topicDoc = null;
	public double [] MaxFreq = null;
	public int [] MaxNum = null;
	
	public double lamda1 = 0.1;
	public double lamda2 = 0.1;
	public double alpha = 0.05;
	
	public List<Doc>[] inverted = null;
	public List<Term>[] doc = null;
	
	public static final double epsion = 0.0001;
	
	@SuppressWarnings("unchecked")
	public grlsi() {
		inverted = new ArrayList[M];
		doc = new ArrayList[N];
		termTopic = new double[M][K];
		topicDoc = new double[K][N];
		MaxFreq = new double[N];
		MaxNum = new int[M];
		for(int i=0; i < N; ++i) {
			MaxFreq[i] = 0.0;
		}
		for(int i=0; i < M; ++i) {
			MaxNum[i] = 0;
		}
	}
	
	public void load(String fName) throws IOException {
		FileReader fr = new FileReader(fName);
		BufferedReader br = new BufferedReader(fr);
		while(br.ready()) {
			String tmpLine = br.readLine();
			int docId = Integer.parseInt(tmpLine.split(",")[0]);
			int termId = Integer.parseInt(tmpLine.split(",")[1]);
			double weight = Double.parseDouble(tmpLine.split(",")[2]);
			if(MaxFreq[docId] < weight) {
				MaxFreq[docId] = weight;
			}
			doc[docId] = new ArrayList<Term>();
			inverted[termId] = new ArrayList<Doc>();
			doc[docId].add(new Term(termId, weight));
			inverted[termId].add(new Doc(docId, weight));
			MaxNum[termId]++;
		}
		br.close();
		fr.close();
	}
	
	public double[] getCol(double[][] matrix, int k, int rowSize) {
		double[] retval = new double[rowSize];
		for(int i = 0 ; i < rowSize; ++i) {
			retval[i] = matrix[i][k];
		}
		return retval;
	}
	
	/*
	public void setCol(double[][] matrix, double[] vec, int k, int rowSize) {
		for(int i = 0; i < rowSize; ++i) {
			matrix[i][k] = vec[i];
		}
	}
	*/
	
	public double tf_idf(int N, int n, double freq_ij, double freq_max) {
		return (freq_ij / freq_max) * Math.log(1.0 * N / n);
	}
	
	public double[] scale(double times, double[] vec, int k) {
		for(int i = 0; i < k; ++i) {
			vec[i] *= times;
		}
		return vec;
	}
	
	public double dotProduct(double[] vec1, double[] vec2, int k) {
		double retval = 0.0;
		for(int i = 0; i < k; ++i) {
			retval += (vec1[i]*vec2[i]);
		}
		return retval;
	}
	
	/*
	public double[] sub(double[] vec1, double[] vec2, int k) {
		for(int i=0; i < k; ++i) {
			vec1[i] -= vec2[i];
		}
		return vec1;
	}
	*/
	
	public void gradientU(int iTerm, int jDoc, double dw) {
		double w_ij = 0.0;
		double d_ij = dw;
		w_ij = tf_idf(N, MaxNum[iTerm], dw, MaxFreq[iTerm]);
		double tmp_dotP = dotProduct(termTopic[iTerm], getCol(topicDoc, jDoc, K), K);
		for(int k=0; k < K; ++k) {
			termTopic[iTerm][k] -= alpha*
					((w_ij*(d_ij-tmp_dotP)*
							(-topicDoc[k][jDoc])+lamda1*termTopic[iTerm][k]));
		}
		// System.out.println("Train one U");
	}
	
	public void gradientV(int iTerm, int jDoc, double dw) {
		double w_ij = 0.0;
		double d_ij = dw;
		w_ij = tf_idf(N, MaxNum[iTerm], dw, MaxFreq[iTerm]);
		double tmp_dotP = dotProduct(termTopic[iTerm], getCol(topicDoc, jDoc, K), K);
		for(int k=0; k < K; ++k) {
			topicDoc[k][jDoc] -= alpha*
					((w_ij*(d_ij-tmp_dotP))*
							(-termTopic[iTerm][k])+lamda2*topicDoc[k][jDoc]);
		}
		// System.out.println("Train one V");
	}
	
	public void randomV() {
		for(int i=0; i < K; ++i) {
			for(int j=0; j < N; ++j) {
				topicDoc[i][j] = Math.random()*1;
			}
		}
	}
	
	/*
	public void gRlsi() throws InterruptedException {
		randomV();
		for(int t=0; t < 1; ++t) {
			for(int i=0; i < M; ++i) {
				for(int j=0; j < N; ++j) {
					// System.out.println("Training...");
					gradientU(i,j);
					gradientV(i,j);
					// System.out.println("Now: "+i +"\t" +j);
				}
				System.out.println(i + "\tError:\t" + getError());
				Thread.sleep(3000);
			}
		}
	}
	*/
	public void gRlsi(String fName) throws InterruptedException, NumberFormatException, IOException {
		int ccnt = 0;
		randomV();
		FileReader fr = new FileReader(fName);
		BufferedReader br = new BufferedReader(fr);
		for(int i=0; i < 10; ++i) {
			while(br.ready()) {
				String tmpLine = br.readLine();
				int docId = Integer.parseInt(tmpLine.split(",")[0]);
				int termId = Integer.parseInt(tmpLine.split(",")[1]);
				double weight = Double.parseDouble(tmpLine.split(",")[2]);
				gradientU(termId,docId,weight);
				gradientV(termId,docId,weight);
				
				if((ccnt++)%50 == 0) {
					double err = getError();
					System.out.println("\tError:\t" + err);
					Thread.sleep(3000);
				}
				
			}
			
			
		}
		br.close();
		fr.close();		
	}
	
	public void output() {
		for(int i=0; i < M; ++i) {
			for(int j=0; j < K; ++j) {
				System.out.println(termTopic[i][j]+"\t");
			}
			System.out.println();
		}
		
		for(int i=0; i < K; ++i) {
			for(int j=0; j < N; ++j) {
				System.out.println(topicDoc[i][j]+"\t");
			}
			System.out.println();
		}
		
		System.out.println("Trained success.");
	}
	
	public double getError() {
		double err = 0.0;
		int cnt = 0;
		for(int i=0; i < N; ++i) {
			for(Term t : doc[i]) {
				double a = dotProduct(termTopic[t.index], getCol(topicDoc, i, K), K);
				err += Math.abs(a-t.frequency);
				cnt++;
			}
		}
		if(cnt == 0) {
			return err;
		}else {
			return err/cnt;
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		grlsi test = new grlsi();
		test.load("mdt.txt");
		test.gRlsi("mdt.txt");
	}
}

