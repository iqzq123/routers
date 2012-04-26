package com;
/**
 * 这个非负分解是小数据量版本
 * 在这个版本上跑大数据量会出现溢出问题
 * 如分解大数据量，请参阅 nmf.java
 * by yinxs
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.vecmath.*;

public class nmf2 {
	private GMatrix U = null;
	private GMatrix V = null;
	private GMatrix X = null;
	private int M;
	private int N;
	private int K;
	private int iteratenum;
	
	public GMatrix getV() {
		return V;
	}
	public nmf2() {
		this.K = 2;
		this.iteratenum = 100;
		this.M = 30;
		this.N = 62;
		X = new GMatrix(M,N); /* 该处初始化决定数据起初均为零 */
		U = new GMatrix(M,K);
		V = new GMatrix(N,K);
	}
	
	public nmf2(int k, int m, int n, int it) {
		this.K = k;
		this.iteratenum = it;
		this.M = m;
		this.N = n;
		X = new GMatrix(M,N); /* 该处初始化决定数据起初均为零 */
		U = new GMatrix(M,K);
		V = new GMatrix(N,K);
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
	
	private GMatrix DotDiv(GMatrix l, GMatrix r) {
		assert(l.getNumRow() == r.getNumRow());
		assert(l.getNumCol() == r.getNumCol());
		GMatrix retval = new GMatrix(l.getNumRow(),l.getNumCol());
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
	
	private GMatrix DotMul(GMatrix l, GMatrix r) {
		assert(l.getNumRow() == r.getNumRow());
		assert(l.getNumCol() == r.getNumCol());
		GMatrix retval = new GMatrix(l.getNumRow(),l.getNumCol());
		for(int i=0; i < l.getNumRow(); ++i) {
			for(int j=0; j < l.getNumCol(); ++j) {
				retval.setElement(i, j, l.getElement(i, j)*r.getElement(i, j));
			}
		}
		return retval;
	}
	
	private double getErr(GMatrix l, GMatrix r) {
		double retval = 0.0;
		for(int i=0; i < l.getNumRow(); ++i) {
			for(int j=0; j < l.getNumCol(); ++j) {
				retval += (Math.abs(l.getElement(i, j)-r.getElement(i, j)));
			}
		}
		return retval;
	}
	
	public void train() {
		GMatrix UV = new GMatrix(M,N);
		GMatrix XX = new GMatrix(M,N);
		/* 初始化 U V 两个矩阵 */
		this.RandomMatrix();
		/* 归一化 V 矩阵的列 */
		this.normCol();
		/* 开始迭代 */
		for(int it=0; it < this.iteratenum; it++) {
			System.out.println("迭代："+it);
			/* for U */
			GMatrix XV = new GMatrix(M,K);
			XV.mul(X,V);
			GMatrix UVV = new GMatrix(M,K);
			GMatrix VV = new GMatrix(K,K);
			VV.mulTransposeLeft(V, V);
			UVV.mul(U,VV);
			GMatrix tmp1 = this.DotDiv(XV, UVV);
			U = this.DotMul(U, tmp1);
//			UV.mul(U, V);
//			XX = this.DotDiv(X, UV);
//			GMatrix UU = new GMatrix(M,K);
//			UU.mulTransposeRight(XX, V);
//			U = this.DotMul(U, UU);
			
			/* for V */
			GMatrix XU = new GMatrix(N,K);
			XU.mulTransposeLeft(X, U);
			GMatrix VUU = new GMatrix(N,K);
			GMatrix UU = new GMatrix(K,K);
			UU.mulTransposeLeft(U, U);
			VUU.mul(V,UU);
			GMatrix tmp2 = this.DotDiv(XU, VUU);
			V = this.DotMul(V, tmp2);
//			UV.mul(U, V);
//			XX = this.DotDiv(X, UV);
//			GMatrix VV = new GMatrix(K,N);
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
	
	public static void main(String args) throws IOException {
		nmf2 test = new nmf2();
		test.load("c:/doc-word.txt");
		test.train();
	}
}
