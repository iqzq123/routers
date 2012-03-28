package svd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import utils.ArrayOP;

import model.InvertedIndex;
import model.Term;

import Jama.Matrix;

public class HeteNetTopic {

	public class InvertedIndex {

		int docId;

		public InvertedIndex(int docId, double weight) {
			super();
			this.docId = docId;
			this.weight = weight;
		}

		double weight;
	}

	private int M = 11771;// Term number
	private int N = 28569;// doc number
	private int K = 4;
	private double lamda1 = 0.1;
	private double lamda2 = 0.1;
	private double lamda3 = 0;
	private double lamda4 = 0;
	private double alpha = 0.001;
	private double[][] termTopic = null;
	private double[][] docTopic = null;
	private double[][] S = null;
	private double[][] R = null;
	private double maxError = 0.01;
	private List<InvertedIndex>[] indexs = null;
	private List<Term>[] docs = null;
	private String[] terms = new String[12000];
	private String wordFile;

	private int authorNum = 28703;
	private int confNum = 20;

	private double[][] authorTopic = null;
	private double[][] confTopic = null;

	// save doc-author with two list array
	private List<Integer>[] docOfAuthor = null;
	private List<Integer>[] authorofDoc = null;
	// save doc-venue with a array and a List array
	private int[] confOfDoc = null;
	private List<Integer>[] docOfConf = null;

	// private String []terms=new String[12000];

	public String getWordFile() {
		return wordFile;
	}

	public void setWordFile(String wordFile) {
		this.wordFile = wordFile;
	}

	public void readData1(String inPath) throws IOException {

		termTopic = new double[M][K];
		docTopic = new double[N][K];
		R = new double[M][K];
		S = new double[K][K];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < K; j++) {
				termTopic[i][j] = Math.random() * 1;
			}
		}

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < K; j++) {
				docTopic[i][j] = Math.random() * 1;
			}
		}
		this.indexs = new ArrayList[M];
		for (int i = 0; i < M; i++) {
			this.indexs[i] = new ArrayList<InvertedIndex>();
		}
		this.docs = new ArrayList[N];
		for (int i = 0; i < N; i++) {
			this.docs[i] = new ArrayList<Term>();
		}

		this.docOfAuthor = new List[this.authorNum];
		for (int i = 0; i < this.authorNum; i++) {
			this.docOfAuthor[i] = new ArrayList<Integer>();
		}
		this.authorofDoc = new List[N];
		for (int i = 0; i < this.N; i++) {
			this.authorofDoc[i] = new ArrayList<Integer>();
		}

		this.confOfDoc = new int[N];
		this.docOfConf = new List[this.confNum];
		for (int i = 0; i < this.confNum; i++) {
			this.docOfConf[i] = new ArrayList<Integer>();
		}

		this.authorTopic = new double[this.authorNum][K];
		this.confTopic = new double[this.confNum][K];
		BufferedReader reader1 = null;
		FileInputStream file1 = new FileInputStream(new File(inPath
				+ "/mdt.txt"));
		System.out.println("read mdt£º");
		reader1 = new BufferedReader(new InputStreamReader(file1, "UTF-8"));
		String tempString = null;

		int cnt = 0;
		while ((tempString = reader1.readLine()) != null) {
			cnt++;
			if (cnt % 100000 == 0) {
				System.out.println("read");
			}
			String[] strArray = tempString.split(",");
			Integer docId = Integer.parseInt(strArray[0]);
			int termId = Integer.parseInt(strArray[1]);
			double weight = Double.parseDouble(strArray[2]);
			docs[docId].add(new Term(termId, weight));
			this.indexs[termId].add(new InvertedIndex(docId, weight));
		}
		reader1.close();

		BufferedReader reader2 = null;
		FileInputStream file2 = new FileInputStream(new File(inPath
				+ "/mda.txt"));
		System.out.println("read mda£º");
		reader2 = new BufferedReader(new InputStreamReader(file2, "UTF-8"));

		cnt = 0;
		while ((tempString = reader2.readLine()) != null) {
			cnt++;
			if (cnt % 100000 == 0) {
				System.out.println("read");
			}
			String[] strArray = tempString.split(",");
			int docId = Integer.parseInt(strArray[0]);
			int authorId = Integer.parseInt(strArray[1]);
			this.docOfAuthor[authorId].add(docId);
			this.authorofDoc[docId].add(authorId);

		}

		BufferedReader reader3 = null;
		FileInputStream file3 = new FileInputStream(new File(inPath
				+ "/mdc.txt"));
		System.out.println("read mdc£º");
		reader3 = new BufferedReader(new InputStreamReader(file3, "UTF-8"));
		cnt = 0;
		while ((tempString = reader3.readLine()) != null) {
			cnt++;
			if (cnt % 100000 == 0) {
				System.out.println("read");
			}
			String[] strArray = tempString.split(",");
			int docId = Integer.parseInt(strArray[0]);
			int confId = Integer.parseInt(strArray[1]);
			this.docOfConf[confId].add(docId);
			this.confOfDoc[docId] = confId;

		}

		BufferedReader reader4 = null;
		FileInputStream file4 = new FileInputStream(new File(inPath
				+ "/term.txt"));
		System.out.println("read term");
		reader4 = new BufferedReader(new InputStreamReader(file4, "UTF-8"));
		cnt = 0;
		while ((tempString = reader4.readLine()) != null) {
			if (cnt % 100000 == 0) {
				System.out.println("read");
			}
			this.terms[cnt] = tempString;
			cnt++;

		}

	}

	public double dotProduct(double[] array1, double[] array2) {
		double ret = 0.0;
		for (int i = 0; i < array1.length; i++) {
			ret += array1[i] * array2[i];
		}
		return ret;
	}

	public double getError() {
		double error = 0.0;
		int cnt = 0;
		for (int n = 0; n < this.N; n++) {
			for (Term t : this.docs[n]) {
				double a = dotProduct(this.termTopic[t.termId],
						this.docTopic[n]);
				error += Math.abs(a - t.frequence);
				cnt++;
			}
		}
		return error / cnt;
	}

	public void train() {

		int T = 50;
		for (int t = 0; t < T; t++) {

			System.out.println("train:" + t);
			System.out.println("doc erro:" + this.getError());
			// update U
			// compute S
			computeS();
			// compute R
			computeR();
			for (int m = 0; m < M; m++) {
				// u_{m}=0;
				for (int k = 0; k < K; k++) {
					this.termTopic[m][k] = 0.0;
				}
				if (m % 5000 == 0) {
					// System.out.println("updateU");
				}

				//
				boolean convergence = false;

				int maxCnt = 0;

				while (!convergence) {
					maxCnt++;
					if (maxCnt > 100) {
						break;
					}
					double[] preWT = new double[K];
					for (int k = 0; k < K; k++) {
						preWT[k] = this.termTopic[m][k];
					}

					for (int k = 0; k < K; k++) {
						double product = 0.0;
						for (int l = 0; l < K; l++) {
							if (l != k) {
								product += S[k][l] * this.termTopic[m][l];
							}
						}

						double w_mk = R[m][k] - product;
						this.termTopic[m][k] = Math.max(0,
								(Math.abs(w_mk) - 0.5 * lamda1))
								* sign(w_mk) / S[k][k];
						// if(this.wordTopic[m][k]>0){
						// this.wordTopic[m][k] = (Math.abs(w_mk) - 0.5 *
						// lamda1) / S[k][k];
						// }else{
						// this.wordTopic[m][k] = (Math.abs(w_mk) + 0.5 *
						// lamda1) / S[k][k];
						// }

					}
					double error = 0.0;
					for (int k = 0; k < K; k++) {
						error += Math.abs(preWT[k] - this.termTopic[m][k]);
					}
					// System.out.println(m+"in error"+error);
					if (m % 10000 == 0) {
						// System.out.println(m+"in error"+error+"maxcnt:"+maxCnt);
					}
					if (m == 0) {
						// System.out.println("xxx");
					}
					if (error < this.maxError) {
						convergence = true;
					}
				}

			}
			// update V with gradient method

			System.out.println("update V:" + t);
			// updateV1();

			this.gradientSolveV(t);

		}
		for (int i = 0; i < K; i++) {
			this.outputTopic(i);
		}

	}

	public void outputTopic(int k) {
		double[] array = new double[20];
		double[] wordArray = new double[M];
		for (int i = 0; i < M; i++) {
			wordArray[i] = this.termTopic[i][k];
		}
		array = MaxK.getMaxK(wordArray, 20);
		double min = array[0];
		int[] wordId = new int[20];
		String[] keywords = new String[20];
		int index = 0;
		for (int i = 0; i < M; i++) {
			if (wordArray[i] >= min) {
				// keywords[index]=this.words[i];
				wordId[index] = i;
				index++;
			}
		}
		// sort words
		for (int i = 0; i < 20 - 1; i++) {
			for (int j = 0; j < 20 - i - 1; j++) {
				if (this.termTopic[wordId[j]][k] < this.termTopic[wordId[j + 1]][k]) {
					int tmp = wordId[j];
					wordId[j] = wordId[j + 1];
					wordId[j + 1] = tmp;
				}
			}
		}
		System.out.println("topic: " + k);
		for (int i = 0; i < 20; i++) {
			System.out.println(this.terms[wordId[i]] + "	"
					+ this.termTopic[wordId[i]][k]);
		}
	}

	public void updateV() {

		// ////////////////update V
		double[][] II = new double[K][K];
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < K; j++) {
				if (i == j) {
					II[i][j] = lamda2;
				}
			}
		}
		Matrix I = new Matrix(II);
		Matrix U = new Matrix(this.termTopic);
		Matrix UU = U.transpose().times(U);
		UU.plusEquals(I);
		Matrix Z = UU.inverse();

		double[][] O = new double[K][N];
		for (int k = 0; k < K; k++) {
			for (int n = 0; n < N; n++) {
				double product = 0.0;
				// for (int m = 0; m < M; m++) {
				// this.wordTopic[m][k]*this.
				// }
				for (Term t : this.docs[n]) {
					product += t.frequence * this.termTopic[t.termId][k];
				}
				O[k][n] = product;
			}
		}

		for (int n = 0; n < N; n++) {
			double[] OClo = new double[K];
			for (int k = 0; k < K; k++) {
				OClo[k] = O[k][n];
			}
			for (int k = 0; k < K; k++) {
				this.docTopic[n][k] = dotProduct(Z.getArray()[k], OClo);
			}
		}

	}

	public void updateV1() {

		// ////////////////update V
		double[][] II = new double[K][K];
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < K; j++) {
				if (i == j) {
					II[i][j] = lamda2 + lamda3 + lamda4;
				}
			}
		}
		Matrix I = new Matrix(II);
		Matrix U = new Matrix(this.termTopic);
		Matrix UU = U.transpose().times(U);
		Matrix temp = UU.plus(I);
		Matrix Z = temp.inverse();

		// compute author-topic and conf-topic
		for (int i = 0; i < this.authorNum; i++) {
			ArrayOP.clear(this.authorTopic[i]);
			for (Integer docId : this.docOfAuthor[i]) {
				ArrayOP.addEquals(this.authorTopic[i], this.docTopic[docId]);
			}
			ArrayOP.divideEquals(this.authorTopic[i], this.docOfAuthor[i]
					.size());
		}

		for (int i = 0; i < this.confNum; i++) {
			ArrayOP.clear(this.confTopic[i]);
			for (Integer docId : this.docOfConf[i]) {
				ArrayOP.addEquals(this.confTopic[i], this.docTopic[docId]);
			}
			ArrayOP.divideEquals(this.confTopic[i], this.docOfConf[i].size());
		}

		double[][] O = new double[K][N];

		for (int k = 0; k < K; k++) {
			for (int n = 0; n < N; n++) {
				double product = 0.0;
				// for (int m = 0; m < M; m++) {
				// this.wordTopic[m][k]*this.
				// }
				for (Term t : this.docs[n]) {
					product += t.frequence * this.termTopic[t.termId][k];
				}
				O[k][n] = product;
			}
		}

		// refine O
		for (int n = 0; n < N; n++) {
			// compute x0
			double[] averAuTopic = new double[K];
			for (Integer a : this.authorofDoc[n]) {
				ArrayOP.addEquals(averAuTopic, this.authorTopic[a]);
			}
			ArrayOP.divideEquals(averAuTopic, this.authorofDoc[n].size());
			double[] x0 = ArrayOP.times(averAuTopic, lamda3);
			ArrayOP.addEquals(x0, ArrayOP.times(
					this.confTopic[this.confOfDoc[n]], lamda4));
			ArrayOP.divideEquals(x0, lamda2 + lamda3 + lamda4);
			double[] UUx0 = new double[K];
			for (int k = 0; k < K; k++) {
				UUx0[k] = ArrayOP.dotProduct(UU.getArray()[k], x0);
			}
			for (int k = 0; k < K; k++) {
				O[k][n] = O[k][n] - UUx0[k];
			}
			double[] OClo = new double[K];
			for (int k = 0; k < K; k++) {
				OClo[k] = O[k][n];
			}
			for (int k = 0; k < K; k++) {
				this.docTopic[n][k] = (dotProduct(Z.getArray()[k], OClo) + x0[k]);
			}

		}
		// for(int n=0;n<N;n++){
		//		
		// double []OClo=new double[K];
		// for(int k=0;k<K;k++){
		// OClo[k]=O[k][n];
		// }
		// for(int k=0;k<K;k++){
		// this.docTopic[n][k]=dotProduct(Z.getArray()[k],OClo);
		// }
		// }

	}

	public void gradientSolveV2() {

		for (int n = 0; n < N; n++) {
			if (n % 5000 == 0) {
				System.out.println("grandient num:" + n);
			}
			int trainNum = 0;
			while (trainNum++ < 5) {
				// compute author-topic and conf-topic
				double[] avAuTopic = new double[K];
				for (Integer au : this.authorofDoc[n]) {
					double[] auTopic = new double[K];
					for (Integer docId : this.docOfAuthor[au]) {
						ArrayOP.addEquals(auTopic, this.docTopic[docId]);
					}
					// topic of au equals average of all his papers
					ArrayOP.divideEquals(auTopic, this.docOfAuthor[au].size());
					ArrayOP.addEquals(avAuTopic, auTopic);
				}
				// divide the number of authors of the paper n
				ArrayOP.divideEquals(avAuTopic, this.authorofDoc[n].size());
				double[] avConfTopic = new double[K];
				int confId = this.confOfDoc[n];
				for (Integer docId : this.docOfConf[confId]) {
					ArrayOP.addEquals(avConfTopic, this.docTopic[docId]);
				}
				ArrayOP
						.divideEquals(avConfTopic, this.docOfConf[confId]
								.size());

				// gradient decent
				double[] error = new double[M];
				double[] Uv = new double[M];
				for (int m = 0; m < M; m++) {
					double product = 0.0;
					for (int k = 0; k < K; k++) {
						product += this.termTopic[m][k] * this.docTopic[n][k];
					}
					Uv[m] = product;
				}
				for (Term t : this.docs[n]) {
					error[t.termId] = t.frequence - Uv[t.termId];
					Uv[t.termId] = 0.0;
				}
				for (int m = 0; m < M; m++) {
					error[m] = error[m] - Uv[m];
				}
				double[] part1 = new double[K];
				for (int k = 0; k < K; k++) {
					double product = 0.0;
					for (int m = 0; m < M; m++) {
						product += -this.termTopic[m][k] * error[m];
					}
					part1[k] = product;
				}
				double[] direction = new double[K];

				for (int k = 0; k < K; k++) {
					direction[k] = part1[k] + lamda2 * this.docTopic[n][k]
							+ lamda3 * (this.docTopic[n][k] - avAuTopic[k])
							+ lamda4 * (this.docTopic[n][k] - avConfTopic[k]);
				}
				// for (int k = 0; k < K; k++) {
				// direction[k] = part1[k] + lamda2 * this.docTopic[n][k];
				// }
				// update Vn
				for (int k = 0; k < K; k++) {
					this.docTopic[n][k] = this.docTopic[n][k] - this.alpha
							* direction[k];
				}

			}
		}
	}

	public void gradientSolveV(int gT) {

		int trainNum = 5;
		while (trainNum-- > 0) {

			for (int n = 0; n < N; n++) {

				if (n % 5000 == 0) {
					System.out.println("grandient num:" + n);
				}

				// compute author-topic and conf-topic
				double[] avAuTopic = new double[K];
				for (Integer au : this.authorofDoc[n]) {
					double[] auTopic = new double[K];
					int docSize = 0;
					for (Integer docId : this.docOfAuthor[au]) {
						if (docId != n) {
							docSize++;
							ArrayOP.addEquals(auTopic, this.docTopic[docId]);
						}

					}
					// topic of au equals average of all his papers
					if (docSize > 0) {
						ArrayOP.divideEquals(auTopic, docSize);
					}

					ArrayOP.addEquals(avAuTopic, auTopic);
				}
				// divide the number of authors of the paper n
				ArrayOP.divideEquals(avAuTopic, this.authorofDoc[n].size());
				double[] avConfTopic = new double[K];
				int confId = this.confOfDoc[n];
				for (Integer docId : this.docOfConf[confId]) {
					ArrayOP.addEquals(avConfTopic, this.docTopic[docId]);
				}
				ArrayOP
						.divideEquals(avConfTopic, this.docOfConf[confId]
								.size());
				//
				double[] error = new double[M];
				double[] Uv = new double[M];
				for (int m = 0; m < M; m++) {
					double product = 0.0;
					for (int k = 0; k < K; k++) {
						product += this.termTopic[m][k] * this.docTopic[n][k];
					}
					Uv[m] = product;
				}
				for (Term t : this.docs[n]) {
					error[t.termId] = t.frequence - Uv[t.termId];
					Uv[t.termId] = 0.0;
				}
				for (int m = 0; m < M; m++) {
					//error[m] = error[m] - Uv[m];
				}
				double[] part1 = new double[K];
				for (int k = 0; k < K; k++) {
					double product = 0.0;
					for (int m = 0; m < M; m++) {
						product += -this.termTopic[m][k] * error[m];
					}
					part1[k] = product;
				}
				double[] direction = new double[K];
				lamda3 = 0.01;
				lamda4 = 0.0;
				for (int k = 0; k < K; k++) {
					double a = lamda3
							* (this.docTopic[n][k] * 10000.0 - avAuTopic[k] * 10000.0)
							/ 10000.0;
					double b = lamda4 * (this.docTopic[n][k] - avConfTopic[k]);
					// System.out.println("a:"+a);
				
				
					direction[k] = part1[k] + lamda2 * this.docTopic[n][k];
				

				}
				// update Vn
				for (int k = 0; k < K; k++) {
					this.docTopic[n][k] = this.docTopic[n][k] - this.alpha
							* direction[k];
				}
//				// update Un
//				double[] part2 = new double[K];
//				for (int k = 0; k < K; k++) {
//					double product = 0.0;
//					for (int n1 = 0; n1 < N; n1++) {
//						product += -this.termTopic[n1][k] * error[n1];
//					}
//					part1[k] = product;
//				}
			}
		}

	}

	private double sign(double val) {
		if (val > 0) {
			return 1.0;
		} else {
			return -1.0;
		}
	}

	private void computeR() {
		for (int m = 0; m < M; m++) {
			for (int k = 0; k < K; k++) {
				double dotProduct = 0.0;
				for (InvertedIndex tw : this.indexs[m]) {
					dotProduct += this.docTopic[tw.docId][k] * tw.weight;
				}
				R[m][k] = dotProduct;
			}
		}
	}

	private void computeS() {
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < K; j++) {
				double dotProduct = 0.0;
				for (int index = 0; index < N; index++) {
					dotProduct += docTopic[index][i] * docTopic[index][j];
				}
				S[i][j] = dotProduct;
			}
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		HeteNetTopic r = new HeteNetTopic();
		// r.intial(11771, 28569, 4);
		r.setWordFile("c:/data/term.txt");
		// r.readData("c:/mdt.txt");
		r.readData1("c:/data/");

		r.train();
		double[][] a = new double[2][2];
		a[0][0] = 1;
		a[0][1] = 2;
		a[1][0] = 3;
		a[1][1] = 4;
		Matrix m = new Matrix(a);
		m = m.inverse();
		a = m.getArray();
		System.out.println(a[0].toString());
	}

}
