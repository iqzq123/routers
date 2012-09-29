package svd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Jama.Matrix;

public class RLSI {

	public class DocIndex {

		int docId;

		public DocIndex(int docId, double weight) {
			super();
			this.docId = docId;
			this.weight = weight;
		}

		double weight;
	}

	public class Term {
		public Term(int termId, double frequence) {
			super();
			this.termId = termId;
			this.frequence = frequence;
		}

		int termId;
		double frequence;
	}

	private int wordNum = 11771;// Term number
	private int docNum = 28569;// doc number
	private int K = 4;
	public void setK(int k) {
		K = k;
	}

	private double lamda1 = 0.1;
	private double lamda2 = 0.1;
	private double alpha = 0.1;
	private double[][] wordTopic = null;
	private double[][] docTopic = null;
	private double[][] S = null;
	private double[][] R = null;
	private double maxError = 0.0001;
	private List<DocIndex>[] invertedIndex = null;
	private List<Term>[] doc = null;
	private String[] words = null;
	private String wordFile;
	private int trainNum=1;

	public void setTrainNum(int trainNum) {
		this.trainNum = trainNum;
	}

	public String getWordFile() {
		return wordFile;
	}

	public void setWordFile(String wordFile) {
		this.wordFile = wordFile;
	}

	public static void test() throws IOException{
		RLSI r = new RLSI();
		r.setTrainNum(30);
		r.setK(10);
		String dictFileName="d:/download/input.tmp.wd";
		String dtFileName="d:/download/input.tmp.dt";
		String docFileName="c:/tmp.txt";
		String wfName="c:/tmp2.txt";
		r.setWordFile(dictFileName);
		r.readData(dtFileName);
		
		r.train();
		
		File outfile = new File(docFileName);
		BufferedWriter w = new BufferedWriter(new FileWriter(outfile));
		r.printDoc(w);
		w.flush();
		w.close();
		
		File outfile2 = new File(wfName);
		BufferedWriter w2 = new BufferedWriter(new FileWriter(outfile2));
		r.printWord(w2);
		w2.flush();
		w2.close();
	}
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
		if(args.length<6){
			System.out.print("RLSI parameter error");
			test();
			return;	
		}
		String dictFileName=args[0];
		String dtFileName=args[1];
		String docFileName=args[2];
		String wfName=args[3];
		for(int i=0;i<args.length;i++){
			System.out.println(args[i]+"\n");
		}
		
		RLSI r = new RLSI();
		r.setK(Integer.parseInt(args[4]));
		r.setTrainNum(Integer.parseInt(args[5]));
		r.setWordFile(dictFileName);
		r.readData(dtFileName);
		r.train();
		File outfile = new File(docFileName);
		BufferedWriter w = new BufferedWriter(new FileWriter(outfile));
		r.printDoc(w);
		w.flush();
		w.close();
		
		File outfile2 = new File(wfName);
		BufferedWriter w2 = new BufferedWriter(new FileWriter(outfile2));
		r.printWord(w2);
		w2.flush();
		w2.close();
	
	}

	public void readData(String inFile) throws IOException {

		// read doc and build invertedIndex
		BufferedReader reader = null;
		FileInputStream file = new FileInputStream(new File(inFile));
		reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
		String tempString = null;

		Set s = new HashSet();
		int maxId = -1;
		while ((tempString = reader.readLine()) != null) {
			int id = Integer.parseInt(tempString.split(",")[0]);
			if (id > maxId) {
				maxId = id;
			}
		}
		this.docNum = maxId + 1;
		reader.close();

		FileInputStream file1 = new FileInputStream(new File(this.wordFile));
		reader = new BufferedReader(new InputStreamReader(file1, "GBK"));

		int cnt = 0;
		while ((tempString = reader.readLine()) != null) {
			cnt++;
		}
		this.wordNum = cnt;
		reader.close();

		// ///////////////////
		this.intial(this.wordNum, this.docNum, this.K);

		// really read the data
		file1 = new FileInputStream(new File(this.wordFile));
		reader = new BufferedReader(new InputStreamReader(file1, "GBK"));
		cnt = 0;
		this.words = new String[this.wordNum];
		while ((tempString = reader.readLine()) != null) {
			//System.out.println(cnt);
			words[cnt] = tempString;
			//System.out.println(words[cnt]);
			cnt++;
		}
		this.wordNum = cnt;
		reader.close();

		file = new FileInputStream(new File(inFile));
		reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
		cnt = 0;
		while ((tempString = reader.readLine()) != null) {
			cnt++;
			if (cnt % 100000 == 0) {
				System.out.println("read");
			}
			String[] strArray = tempString.split(",");
			Integer docId = Integer.parseInt(strArray[0]);
			Integer termId = Integer.parseInt(strArray[1]);
			double weight = Double.parseDouble(strArray[2]);
			doc[docId].add(new Term(termId, weight));
			//System.out.println(this.N + ":" + docId);
			this.invertedIndex[termId].add(new DocIndex(docId, weight));

		}
		reader.close();
		
		System.out.println("doc Number:"+this.docNum+" term Number:"+this.wordNum);

	}

	private void intial(int m, int n, int k) {
		this.wordNum = m;
		this.docNum = n;
		this.K = k;
		wordTopic = new double[m][k];
		docTopic = new double[n][k];
		R = new double[m][k];
		S = new double[k][k];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < k; j++) {
				wordTopic[i][j] = (float) (Math.random() * 1);
			}
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < k; j++) {
				docTopic[i][j] = Math.random() * 1;
			}
		}
		this.invertedIndex = new ArrayList[wordNum];
		for (int i = 0; i < wordNum; i++) {
			this.invertedIndex[i] = new ArrayList<DocIndex>();
		}
		this.doc = new ArrayList[docNum];
		for (int i = 0; i < docNum; i++) {
			this.doc[i] = new ArrayList<Term>();
		}

	}

	public double dotProduct(double[] ds, double[] array2) {
		double ret = 0.0;
		for (int i = 0; i < ds.length; i++) {
			ret += ds[i] * array2[i];
		}
		return ret;
	}

	public double getError() {
		double error = 0.0;
		int cnt = 0;
		for (int n = 0; n < this.docNum; n++) {
			for (Term t : this.doc[n]) {
				double a = dotProduct(this.wordTopic[t.termId],
						this.docTopic[n]);
				error += Math.abs(a - t.frequence);
				cnt++;
			}
		}
		return error / cnt;
	}

	public void printDoc(Writer w) throws IOException {

		for (int i = 0; i < this.docTopic.length; i++) {
			String line = "";
			for (int k = 0; k < this.docTopic[i].length; k++) {
				line += this.docTopic[i][k] + "\t";
			}
			line+="\n";
			w.write(line);
		}
	}
	public void printWord(Writer w) throws IOException {

		
		for (int i = 0; i < this.wordTopic.length; i++) {
			String line = "";
			for (int k = 0; k < this.wordTopic[i].length; k++) {
				line += this.wordTopic[i][k] + "\t";
			}
			line+="\n";
			w.write(line);
		}
	}
	public void train() throws IOException {

		int T = trainNum;
		for (int t = 0; t < T; t++) {

			System.out.println("train");
			System.out.println("doc erro:" + this.getError());
			// update U
			// compute S
			computeS();
			// compute R
			computeR();
			for (int m = 0; m < wordNum; m++) {
				// u_{m}=0;
				for (int k = 0; k < K; k++) {
					this.wordTopic[m][k] = 0.0;
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
						preWT[k] = this.wordTopic[m][k];
					}

					for (int k = 0; k < K; k++) {
						double product = 0.0;
						for (int l = 0; l < K; l++) {
							if (l != k) {
								product += S[k][l] * this.wordTopic[m][l];
							}
						}

						double w_mk = R[m][k] - product;
						this.wordTopic[m][k] = Math.max(0,
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
						error += Math.abs(preWT[k] - this.wordTopic[m][k]);
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
			updateV();

//			for (int n = 0; n < N; n++) {
//				if (n % 1000 == 0) {
//					//System.out.println("update V:" + n);
//				}
//				// gradientSolveV(n);
//			}

		}
		// this.buildDocGraph();
		for (int i = 0; i < K; i++) {
			this.outputTopic(i);
		}

	}

	public void buildDocGraph() throws IOException {

		File out = new File("c:/docGraph15.txt");
		BufferedWriter w = new BufferedWriter(new FileWriter(out));
		int cnt = 0;
		for (int i = 0; i < this.docNum; i++) {

			double[] simArray = new double[this.docNum];
			for (int j = 0; j < this.docNum; j++) {
				if (i == j) {
					continue;
				}
				double dotproduct = 0.0;
				double a = 0.0;
				double b = 0.0;
				for (int k = 0; k < this.docTopic[i].length; k++) {
					dotproduct += this.docTopic[i][k] * this.docTopic[j][k];
					a += this.docTopic[i][k] * this.docTopic[i][k];
					b += this.docTopic[j][k] * this.docTopic[j][k];
				}
				double sim = dotproduct / (Math.sqrt(a) * Math.sqrt(b));
				simArray[j] = sim;
			}

			//
			double[] array = MaxK.getMaxK(simArray, 4);
			double min = array[0];

			for (int j = 0; j < this.docNum; j++) {
				if (simArray[j] > min) {
					String tid1 = this.words[this.doc[i].get(0).termId];
					String tid2 = this.words[this.doc[j].get(0).termId];
					String edge = i + "_" + tid1 + "\t" + j + "_" + tid2 + "\t"
							+ simArray[j] + "\n";
					w.write(edge);
					// System.out.print(edge);
					cnt++;
				}
			}
		}
		w.flush();
		w.close();
		System.out.println(cnt);
	}

	public void clusters() throws IOException {

		List[] listArray = new ArrayList[K];
		for (int i = 0; i < K; i++) {
			listArray[i] = new ArrayList();
		}
		for (int i = 0; i < this.wordNum; i++) {
			int maxK = 0;
			double max = Double.MIN_VALUE;
			for (int k = 0; k < this.K; k++) {
				if (this.wordTopic[i][k] > max) {
					max = this.wordTopic[i][k];
					maxK = k;
				}
			}
			listArray[maxK].add(this.words[i]);
		}
		File out = new File("c:/coPaper.txt");
		BufferedWriter w = new BufferedWriter(new FileWriter(out));
		for (int i = 0; i < this.K; i++) {
			w.write(i + ":\n");
			for (Object t : listArray[i]) {
				w.write(t.toString() + "\n");
			}

		}
		w.flush();
		w.close();
	}

	public void outputTopic(int k) {
		double[] array = new double[20];
		double[] wordArray = new double[wordNum];
		for (int i = 0; i < wordNum; i++) {
			wordArray[i] = this.wordTopic[i][k];
		}
		array = MaxK.getMaxK(wordArray, 20);
		double min = array[0];
		int[] wordId = new int[20];
		String[] keywords = new String[20];
		int index = 0;
		for (int i = 0; i < wordNum; i++) {
			if (wordArray[i] >= min) {
				// keywords[index]=this.words[i];
				wordId[index] = i;
				index++;
			}
		}
		// sort words
		for (int i = 0; i < 20 - 1; i++) {
			for (int j = 0; j < 20 - i - 1; j++) {
				if (this.wordTopic[wordId[j]][k] < this.wordTopic[wordId[j + 1]][k]) {
					int tmp = wordId[j];
					wordId[j] = wordId[j + 1];
					wordId[j + 1] = tmp;
				}
			}
		}
		System.out.println("topic: " + k);
		for (int i = 0; i < 20; i++) {
			System.out.println(this.words[wordId[i]] + "	"
					+ this.wordTopic[wordId[i]][k]);
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
		Matrix U = new Matrix(this.wordTopic);
		Matrix UU = U.transpose().times(U);
		UU.plusEquals(I);
		Matrix Z = UU.inverse();

		double[][] O = new double[K][docNum];
		for (int k = 0; k < K; k++) {
			for (int n = 0; n < docNum; n++) {
				double product = 0.0;
				// for (int m = 0; m < M; m++) {
				// this.wordTopic[m][k]*this.
				// }
				for (Term t : this.doc[n]) {
					product += t.frequence * this.wordTopic[t.termId][k];
				}
				O[k][n] = product;
			}
		}

		for (int n = 0; n < docNum; n++) {
			double[] OClo = new double[K];
			for (int k = 0; k < K; k++) {
				OClo[k] = O[k][n];
			}
			for (int k = 0; k < K; k++) {
				this.docTopic[n][k] = dotProduct(Z.getArray()[k], OClo);
			}
		}

	}

	public void gradientSolveV(int n) {

		int trainNum = 20;
		while (trainNum-- > 0) {
			double[] error = new double[wordNum];
			double[] Uv = new double[wordNum];
			for (int m = 0; m < wordNum; m++) {
				double product = 0.0;
				for (int k = 0; k < K; k++) {
					product += this.wordTopic[m][k] * this.docTopic[n][k];
				}
				Uv[m] = product;
			}
			for (Term t : this.doc[n]) {
				error[t.termId] = t.frequence - Uv[t.termId];
				Uv[t.termId] = 0.0;
			}
			for (int m = 0; m < wordNum; m++) {
				error[m] = error[m] - Uv[m];
			}
			double[] part1 = new double[K];
			for (int k = 0; k < K; k++) {
				double product = 0.0;
				for (int m = 0; m < wordNum; m++) {
					product += -this.wordTopic[m][k] * error[m];
				}
				part1[k] = product;
			}
			double[] direction = new double[K];
			for (int k = 0; k < K; k++) {
				direction[k] = part1[k] + lamda2 * this.docTopic[n][k];
			}
			// update Vn
			for (int k = 0; k < K; k++) {
				this.docTopic[n][k] = this.docTopic[n][k] - this.alpha
						* direction[k];
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
		for (int m = 0; m < wordNum; m++) {
			for (int k = 0; k < K; k++) {
				double dotProduct = 0.0;
				for (DocIndex tw : this.invertedIndex[m]) {
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
				for (int index = 0; index < docNum; index++) {
					dotProduct += docTopic[index][i] * docTopic[index][j];
				}
				S[i][j] = dotProduct;
			}
		}
	}

}
