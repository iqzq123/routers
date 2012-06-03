package nmf;
/**
 * 大数据的NMF版本
 * by yinxs
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.vecmath.*;

public class LNMF {
	private LGMatrix U = null;
	private LGMatrix V = null;
	private LGMatrix X = null;
	private int M;
	private int N;
	private int K;
	private int iteratenum;
	
	public LGMatrix getV() {
		return V;
	}
	
	public LNMF() {
		this.K = 2;
		this.iteratenum = 100;
		this.M = 30;
		this.N = 62;
		X = new LGMatrix(M,N); /* 该处初始化决定数据起初均为零 */
		U = new LGMatrix(M,K);
		V = new LGMatrix(N,K);
	}
	
	public LNMF(int k, int m, int n, int it) {
		this.K = k;
		this.iteratenum = it;
		this.M = m;
		this.N = n;
		X = new LGMatrix(M,N); /* 该处初始化决定数据起初均为零 */
		U = new LGMatrix(M,K);
		V = new LGMatrix(N,K);
	}
	
	public void load(int[][] d, int[][] w) {
		for(int i=0; i < d.length; ++i) {
			for(int j=0; j < d[i].length; ++j) {
				X.setElement(i, d[i][j], w[i][j]);
			}
		}
	}
	
	public void load(String fName) throws IOException {
		FileReader fr = new FileReader(fName);
		BufferedReader br = new BufferedReader(fr);
		while(br.ready()) {
			String tmpLine = br.readLine();
			int i = Integer.parseInt(tmpLine.split(",")[0]);
			int j = Integer.parseInt(tmpLine.split(",")[1]);
			double w = Double.parseDouble(tmpLine.split(",")[2]);
			assert((i>=0)&&(i<=M));
			assert((j>=0)&&(j<=N));
			X.setElement(i, j, w);
		}
		br.close();
		fr.close();
	}
	
	private void RandomMatrix() {
		for(int i=0; i < U.getNumRow(); ++i) {
			for(int j=0; j < U.getNumCol(); ++j) {
				U.setElement(i, j, Math.random()*1);
			}
		}
		for(int i=0; i < V.getNumRow(); ++i) {
			for(int j=0; j < V.getNumCol(); ++j) {
				V.setElement(i, j, Math.random()*1);
				if(V.getElement(i, j) == 0.0) {
					System.out.println("Here");
				}
			}
		}
	}
	
	/**
	 * 对于矩阵 V 实施归一化
	 */
	private void normCol() {
		GVector sum = new GVector(V.getNumRow());
		for(int i=0; i < V.getNumRow(); ++i) {
			double tmp = 0.0;
			for(int j=0; j < V.getNumCol(); ++j) {
				tmp += V.getElement(i, j);
			}
			sum.setElement(i, tmp);
			for(int j=0; j < V.getNumCol(); ++j) {
				if(sum.getElement(i) == 0.0) {
					sum.setElement(i, 0.0000000001/sum.getSize());
				}
				if(V.getElement(i, j) == 0.0) {
					V.setElement(i, j, 0.0000000001/(V.getNumCol()*V.getNumRow()));
				}
				V.setElement(i, j, 1.0*V.getElement(i, j)/sum.getElement(i));
			}
		}
	}
	
	private LGMatrix DotDiv(LGMatrix l, LGMatrix r) {
		assert(l.getNumRow() == r.getNumRow());
		assert(l.getNumCol() == r.getNumCol());
		LGMatrix retval = new LGMatrix(l.getNumRow(),l.getNumCol());
		for(int i=0; i < l.getNumRow(); ++i) {
			for(int j=0; j < l.getNumCol(); ++j) {
				if(r.getElement(i, j) == 0.0) {
					r.setElement(i, j, 0.0000000001/(r.getNumCol()*r.getNumRow()));
				}
				if(l.getElement(i, j) == 0.0) {
					r.setElement(i, j, 0.0000000001/(l.getNumCol()*l.getNumRow()));
				}
				retval.setElement(i, j, 1.0*l.getElement(i, j)/r.getElement(i, j));
			}
		}
		return retval;
	}
	
	private LGMatrix DotMul(LGMatrix l, LGMatrix r) {
		assert(l.getNumRow() == r.getNumRow());
		assert(l.getNumCol() == r.getNumCol());
		LGMatrix retval = new LGMatrix(l.getNumRow(),l.getNumCol());
		for(int i=0; i < l.getNumRow(); ++i) {
			for(int j=0; j < l.getNumCol(); ++j) {
				retval.setElement(i, j, l.getElement(i, j)*r.getElement(i, j));
			}
		}
		return retval;
	}
	
	private double getErr(LGMatrix l, LGMatrix r) {
		double retval = 0.0;
		for(int i=0; i < l.getNumRow(); ++i) {
			for(int j=0; j < l.getNumCol(); ++j) {
				retval += (Math.abs(l.getElement(i, j)-r.getElement(i, j)));
			}
		}
		return retval;
	}
	
	public void train() {
		LGMatrix UV = new LGMatrix(M,N);
		LGMatrix XX = new LGMatrix(M,N);
		/* 初始化 U V 两个矩阵 */
		this.RandomMatrix();
		/* 归一化 V 矩阵的列 */
		this.normCol();
		/* 开始迭代 */
		for(int it=0; it < this.iteratenum; it++) {
			System.out.println("迭代："+it);
			/* for U */
			LGMatrix XV = new LGMatrix(M,K);
			XV.mul(X,V);
			LGMatrix UVV = new LGMatrix(M,K);
			LGMatrix VV = new LGMatrix(K,K);
			VV.mulTransposeLeft(V, V);
			UVV.mul(U,VV);
			LGMatrix tmp1 = this.DotDiv(XV, UVV);
			
//			System.out.println("for U"+tmp1+"\n");
			U = this.DotMul(U, tmp1);
//			UV.mul(U, V);
//			XX = this.DotDiv(X, UV);
//			LGMatrix UU = new LGMatrix(M,K);
//			UU.mulTransposeRight(XX, V);
//			U = this.DotMul(U, UU);
			
			/* for V */
			LGMatrix XU = new LGMatrix(N,K);
			XU.mulTransposeLeft(X, U);
			LGMatrix VUU = new LGMatrix(N,K);
			LGMatrix UU = new LGMatrix(K,K);
			UU.mulTransposeLeft(U, U);
			VUU.mul(V,UU);
			LGMatrix tmp2 = this.DotDiv(XU, VUU);
		    GVector v1 = new GVector(tmp2.getNumCol());
		    tmp2.getColumn(0, v1);
			System.out.println("for V"+v1+"\n");
			V = this.DotMul(V, tmp2);
//			UV.mul(U, V);
//			XX = this.DotDiv(X, UV);
//			LGMatrix VV = new LGMatrix(K,N);
//			VV.mulTransposeLeft(U, XX);
//			V = this.DotMul(V, VV);
			
			/* U 归一化 */
			
			XX.mulTransposeRight(U, V);
			this.normCol();
			System.out.println("错误率："+this.getErr(X, XX));
		}
		
//		System.out.print(U);
//		System.out.println();
//		System.out.print(V);
	}
	
	public void output(String f) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write(U.toString());
		writer.write("\n");
		writer.write(V.toString());
		writer.close();
	}
	
	public static void main(String[] args) throws IOException {
		LNMF test = new LNMF();
		test.load("D:/data/coPaper.txt");
		test.train();
		test.output("D:/data/UV.txt");
	}
}
