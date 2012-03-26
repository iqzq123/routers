package svd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

	private int M = 11771;// Term number
	private int N = 28569;// doc number
	private int K = 4;
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
	private String []words=new String[12000];
	private String wordFile;

	public String getWordFile() {
		return wordFile;
	}

	public void setWordFile(String wordFile) {
		this.wordFile = wordFile;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		RLSI r = new RLSI();
		r.intial(11771, 28569, 4);
		r.setWordFile("c:/data/term.txt");
		r.readData("c:/mdt.txt");
		
		r.train();
		double[][] a=new double[2][2];
		a[0][0]=1;
		a[0][1]=2;
		a[1][0]=3;
		a[1][1]=4;
		Matrix m=new Matrix(a);
		m=m.inverse();
		a=m.getArray();
		System.out.println(a[0].toString());
	}

	public void readData(String inFile) throws IOException {

		// read doc and build invertedIndex
		BufferedReader reader = null;
		FileInputStream file = new FileInputStream(new File(inFile));
		System.out.println("以行为单位读取文件内容，一次读一整行：");
		reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
		String tempString = null;

		int cnt = 0;
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
			this.invertedIndex[termId].add(new DocIndex(docId, weight));

		}
		
		BufferedReader reader1 = null;
		FileInputStream file1 = new FileInputStream(new File(this.wordFile));
		System.out.println("以行为单位读取文件内容，一次读一整行：");
		reader = new BufferedReader(new InputStreamReader(file1, "UTF-8"));
		

		cnt=0;
		while ((tempString = reader.readLine()) != null) {
			words[cnt]=tempString;
			cnt++;
		}
		reader.close();

	}

	public void intial(int m, int n, int k) {
		this.M = m;
		this.N = n;
		this.K = k;
		wordTopic = new double[m][k];
		docTopic = new double[n][k];
		R = new double[m][k];
		S = new double[k][k];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < k; j++) {
				wordTopic[i][j] = Math.random()*1;
			}
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < k; j++) {
				docTopic[i][j] = Math.random()*1;
			}
		}
		this.invertedIndex = new ArrayList[M];
		for (int i = 0; i < M; i++) {
			this.invertedIndex[i] = new ArrayList<DocIndex>();
		}
		this.doc = new ArrayList[N];
		for (int i = 0; i < N; i++) {
			this.doc[i] = new ArrayList<Term>();
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
			for (Term t : this.doc[n]) {
				double a = dotProduct(this.wordTopic[t.termId],
						this.docTopic[n]);
				error += Math.abs(a - t.frequence);
				cnt++;
			}
		}
		return error/cnt;
	}

	public void train() {

		int T = 100;
		for (int t = 0; t < T; t++) {

			System.out.println("train");
			System.out.println("doc erro:"+this.getError());
			// update U
			// compute S
			computeS();
			// compute R
			computeR();
			for (int m = 0; m < M; m++) {
				// u_{m}=0;
				for (int k = 0; k < K; k++) {
					this.wordTopic[m][k] = 0.0;
				}
				if(m%5000==0){
					//System.out.println("updateU");
				}
				
				//
				boolean convergence = false;
				
				int maxCnt=0;

				while (!convergence) {
					maxCnt++;
					if(maxCnt>100){
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
//						if(this.wordTopic[m][k]>0){
//							this.wordTopic[m][k] = (Math.abs(w_mk) - 0.5 * lamda1) / S[k][k];
//						}else{
//							this.wordTopic[m][k] = (Math.abs(w_mk) + 0.5 * lamda1) / S[k][k];
//						}

					}
					double error = 0.0;
					for (int k = 0; k < K; k++) {
						error += Math.abs(preWT[k] - this.wordTopic[m][k]);
					}
					//System.out.println(m+"in error"+error);
					if(m%10000==0){
						//System.out.println(m+"in error"+error+"maxcnt:"+maxCnt);
					}
					if(m==0){
						//System.out.println("xxx");
					}
					if (error < this.maxError) {
						convergence = true;
					}
				}

			}
			// update V with gradient method
			
			System.out.println("update V:" + t);
			//updateV();

			for (int n = 0; n < N; n++) {
				if (n % 1000 == 0) {
					System.out.println("update V:" + n);
				}
				gradientSolveV(n);
			}

		}
		for(int i=0;i<K;i++){
			this.outputTopic(i);
		}

	}

	public void outputTopic(int k){
		double []array=new double[20];
		double[] wordArray=new double[M];
		for(int i=0;i<M;i++){
			wordArray[i]=this.wordTopic[i][k];
		}
		array=MaxK.getMaxK(wordArray, 20);
		double min=array[0];
		int[]wordId=new int[20];
		String[] keywords=new String[20];
		int index=0;
		for(int i=0;i<M;i++){
			if(wordArray[i]>=min){
				//keywords[index]=this.words[i];
				wordId[index]=i;
				index++;
			}
		}
		//sort words
		for(int i=0;i<20-1;i++){
			for(int j=0;j<20-i-1;j++){
				if(this.wordTopic[wordId[j]][k]<this.wordTopic[wordId[j+1]][k]){
					int tmp=wordId[j];
					wordId[j]=wordId[j+1];
					wordId[j+1]=tmp;
				}
			}
		}
		System.out.println("topic: "+k);
		for(int i=0;i<20;i++){
			System.out.println(this.words[wordId[i]]+"	"+this.wordTopic[wordId[i]][k]);
		}
	}
	public void updateV() {

		// ////////////////update V
		double [][]II=new double[K][K];
		for(int i=0;i<K;i++){
			for(int j=0;j<K;j++){
				if(i==j){
					II[i][j]=lamda2;
				}
			}
		}
		Matrix I = new Matrix(II);
		Matrix U = new Matrix(this.wordTopic);
		Matrix UU = U.transpose().times(U);
		UU.plusEquals(I);
		Matrix Z = UU.inverse();

		double[][] O = new double[K][N];
		for (int k = 0; k < K; k++) {
			for (int n = 0; n < N; n++) {
				double product = 0.0;
//				for (int m = 0; m < M; m++) {
//					this.wordTopic[m][k]*this.
//				}
				for(Term t:this.doc[n]){
					product+=t.frequence*this.wordTopic[t.termId][k];
				}
				O[k][n]=product;
			}
		}
		
		for(int n=0;n<N;n++){
			double []OClo=new double[K];
			for(int k=0;k<K;k++){
				OClo[k]=O[k][n];
			}
			for(int k=0;k<K;k++){
				this.docTopic[n][k]=dotProduct(Z.getArray()[k],OClo);
			}
		}
		

	}

	public void gradientSolveV(int n) {

		int trainNum = 20;
		while (trainNum-- > 0) {
			double[] error = new double[M];
			double[] Uv = new double[M];
			for (int m = 0; m < M; m++) {
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
			for (int m = 0; m < M; m++) {
				error[m] = error[m] - Uv[m];
			}
			double[] part1 = new double[K];
			for (int k = 0; k < K; k++) {
				double product = 0.0;
				for (int m = 0; m < M; m++) {
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
		for (int m = 0; m < M; m++) {
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
				for (int index = 0; index < N; index++) {
					dotProduct += docTopic[index][i] * docTopic[index][j];	
				}
				S[i][j] = dotProduct;
			}
		}
	}

}
