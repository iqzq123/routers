package math_yinxs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class grlsi {
	public class Doc {
		public int index;
		public double frequency;
		public double tf_idf;
		public Doc(int i, double j, double k) {
			index = i;
			frequency = j;
			tf_idf = k;
		}
	}
	
	public class Term {
		public int index;
		public double frequency;
		public double tf_idf;
		public Term(int i, double j, double k) {
			index = i;
			frequency = j;
			tf_idf = k;
		}
	}
	
	public int M = 11771;
	public int N = 28569;
	public int K = 4;
	public double [][] termTopic = null;
	public double [][] topicDoc = null;
	
	public double lamda1 = 0.1;
	public double lamda2 = 0.1;
	public double alpha = 0.1;
	
	public List<Doc>[] inverted = null;
	public List<Term>[] doc = null;
	
	public static final double epsion = 0.0001;
	
	public void load(String fName) throws IOException {
		FileReader fr = new FileReader(fName);
		BufferedReader br = new BufferedReader(fr);
		while(br.ready()) {
			String tmpLine = br.readLine();
			int docId = Integer.parseInt(tmpLine.split(",")[0]);
			int termId = Integer.parseInt(tmpLine.split(",")[1]);
			double weight = Double.parseDouble(tmpLine.split(",")[2]);
			double tfIdf = Double.parseDouble(tmpLine.split(",")[3]);
			doc[docId].add(new Term(termId, weight, tfIdf));
			inverted[termId].add(new Doc(docId, weight, tfIdf));
		}
	}
	
	public double[] getCol(double[][] matrix, int k, int rowSize) {
		double[] retval = new double[rowSize];
		for(int i = 0 ; i < rowSize; ++i) {
			retval[i] = matrix[i][k];
		}
		return retval;
	}
	
	public void setCol(double[][] matrix, double[] vec, int k, int rowSize) {
		for(int i = 0; i < rowSize; ++i) {
			matrix[i][k] = vec[i];
		}
	}
	
	public double tf_idf(int N, int n, int freq_ij, int freq_max) {
		return (freq_ij / freq_max) * Math.log(N / n);
	}
	
	public double[] scale(double times, double[] vec, int k) {
		for(int i = 0; i < k; ++i) {
			vec[i] *= vec[i];
		}
		return vec;
	}
	
	public double dotProduct(double[] vec1, double[] vec2, int k) {
		double retval = 0.0;
		for(int i = 0; i < k; ++i) {
			retval += vec1[i]*vec2[i];
		}
		return retval;
	}
	
	public double[] sub(double[] vec1, double[] vec2, int k) {
		for(int i=0; i < k; ++i) {
			vec1[i] -= vec2[i];
		}
		return vec1;
	}
	
	public void gradientU(int iTerm, int jDoc) {
		double w_ij = 0.0;
		double d_ij = 0.0;
		for(Doc c: inverted[iTerm]) {
			if(c.index == jDoc) {
				w_ij = c.tf_idf;
				d_ij = c.frequency;
			}
		}
		double tmp_dotP = dotProduct(termTopic[iTerm], getCol(topicDoc, jDoc, K), K);
		for(int k=0; k < K; ++k) {
			termTopic[iTerm][k] -= alpha*
					(w_ij*(d_ij-tmp_dotP)*
							(-topicDoc[k][jDoc])+lamda1*termTopic[iTerm][k]);
		}
	}
	
	public void gradientV(int iTerm, int jDoc) {
		double w_ij = 0.0;
		double d_ij = 0.0;
		for(Doc c: inverted[iTerm]) {
			if(c.index == jDoc) {
				w_ij = c.tf_idf;
				d_ij = c.frequency;
			}
		}
		double tmp_dotP = dotProduct(termTopic[iTerm], getCol(topicDoc, jDoc, K), K);
		for(int k=0; k < K; ++k) {
			topicDoc[k][jDoc] -= alpha*
					((w_ij*(d_ij-tmp_dotP))*
							(-termTopic[iTerm][k])+lamda2*topicDoc[k][jDoc]);
		}
	}
	
	public void randomV() {
		for(int i=0; i < K; ++i) {
			for(int j=0; j < N; ++j) {
				topicDoc[i][j] = Math.random()*1;
			}
		}
	}
	
	public void gRlsi() {
		randomV();
		for(int t=0; t < 10; ++t) {
			for(int i=0; i < M; ++i) {
				for(int j=0; j < N; ++j) {
					gradientU(i,j);
					gradientV(i,j);
				}
			}
		}
	}
	
	public void output() {
		System.out.println("Trained success.");
	}
	
	public static void main(String[] args) throws IOException {
		grlsi test = new grlsi();
		test.load("mdt.txt");
		test.gRlsi();
	}
}

