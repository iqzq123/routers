package svd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import model.Term;

public class BiasLSI {

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
		for (int cnt = 0; cnt < 100; cnt++) {
			for (int n = 0; n < N; n++) {
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
					// error[m] = error[m] - Uv[m];
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
				for (int k = 0; k < K; k++) {
					direction[k] = part1[k] + lamda2 * this.docTopic[n][k];

				}
				// update Vn
				for (int k = 0; k < K; k++) {
					this.docTopic[n][k] = this.docTopic[n][k] - this.alpha
							* direction[k];
				}

			}
			//update Un
			
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

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BiasLSI r = new BiasLSI();
		// r.intial(11771, 28569, 4);
		r.setWordFile("c:/data/term.txt");
		// r.readData("c:/mdt.txt");
		r.readData1("c:/data/");
		r.train();

	}

}
