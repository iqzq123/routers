package javax.vecmath;

import java.util.HashMap;
import java.util.Map;

public class FGMatrix {

	/**
	 * Inner class data It's an element of matrix
	 * 
	 * @author yinxs
	 * 
	 */
	class ArrayData {
		public Map<Integer, Double> val = new HashMap<Integer, Double>();
	}

	public static final double EPS = 1.0E-5;

	int nRow, nCol;
	ArrayData[] values;

	public FGMatrix(int row, int col) {
		nRow = row;
		nCol = col;
		/* ����Ϊ���ߣ�����һ��map */
		this.values = new ArrayData[row];
		for (int i = 0; i < row; ++i) {
			this.values[i] = new ArrayData();
		}
	}

	/**
	 * set value to an elements. If there is not such an element, create it.
	 */
	public void setElement(int row, int column, double value) {
		/* 0 Ԫ���洢 */
		if (value <= EPS) {
			if(this.values[row].val.containsKey(column)) {
				this.values[row].val.remove(column);
			}
			return;
		}
		this.values[row].val.put(column, value);
	}

	public int getNumRow() {
		return (nRow);
	}

	public int getNumCol() {
		return (nCol);
	}

	public final double getElement(int row, int column) {
		assert ((row < nRow) && (column < nCol));
		if (this.values[row].val.containsKey(column)) {
			return this.values[row].val.get(column);
		}
		return (0.0);
	}

	/**
	 * ��ȡһ�����ݣ����ҷ���������
	 * 
	 * @param row
	 * @return
	 */
	public double[] getRowElement(int row) {
		double[] res = new double[nCol];
		for (int i = 0; i < nCol; ++i) {
			res[i] = this.getElement(row, i);
		}
		return res;
	}

	/**
	 * ��ȡһ�����ݣ�������������
	 * 
	 * @param col
	 * @return
	 */
	public double[] getColElement(int col) {
		double[] res = new double[nRow];
		for (int i = 0; i < nRow; ++i) {
			res[i] = this.getElement(i, col);
		}
		return res;
	}

	/**
	 * ͬ�ϣ��������ϡ�����
	 * 
	 * @param row
	 * @return
	 */
	public ArrayData getRowElementNonZero(int row) {
		ArrayData res = new ArrayData();
		res.val.putAll(this.values[row].val);
		return res;
	}

	/**
	 * ͬ�ϣ����ϡ�����
	 * 
	 * @param col
	 * @return
	 */
	public ArrayData getColElementNonZero(int col) {
		ArrayData res = new ArrayData();
		for (int i = 0; i < this.nRow; ++i) {
			if (this.values[i].val.containsKey(col)) {
				res.val.put(i, this.values[i].val.get(col));
			}
		}
		return res;
	}

	/**
	 * ����m1 �� m2��ˣ���ֵ����ǰ����
	 * 
	 * @param m1
	 * @param m2
	 */
	public final void mul(FGMatrix m1, FGMatrix m2) {
		int i, j;

		if (m1.nCol != m2.nRow || nRow != m1.nRow || nCol != m2.nCol)
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix1"));

		for (i = 0; i < m1.nRow; i++) {
			for (j = 0; j < m2.nCol; j++) {
				ArrayData row = m1.getRowElementNonZero(i);
				ArrayData col = m2.getColElementNonZero(j);
				/* ����������һ��Ϊ�գ���ô��Ԫ�ؿ϶�Ϊ0 */
				if ((row.val.isEmpty()) || (col.val.isEmpty())) {
					continue;
				}
				double value = 0.0;
				for (int pos : row.val.keySet()) {
					if (col.val.containsKey(pos)) {
						value += row.val.get(pos) * col.val.get(pos);
					}
				}
				this.setElement(i, j, value);
			}
		}
	}

	/**
	 * ������ת�ó����Ҳ���󣬲���ֵ����ǰ����
	 * 
	 * @param m1
	 * @param m2
	 */
	public final void mulTransposeLeft(FGMatrix m1, FGMatrix m2) {
		int i, j;

		if (m1.nRow != m2.nRow || nCol != m2.nCol || nRow != m1.nCol)
			throw new MismatchedSizeException(VecMathI18N
					.getString("GMatrix16"));

		for (i = 0; i < this.nRow; i++) {
			for (j = 0; j < this.nCol; j++) {
				ArrayData row = m1.getColElementNonZero(i);
				ArrayData col = m2.getColElementNonZero(j);
				/* ����������һ��Ϊ�գ���ô��Ԫ�ؿ϶�Ϊ0 */
				if ((row.val.isEmpty()) || (col.val.isEmpty())) {
					continue;
				}
				double value = 0.0;
				for (int pos : row.val.keySet()) {
					if (col.val.containsKey(pos)) {
						value += row.val.get(pos) * col.val.get(pos);
					}
				}
				this.setElement(i, j, value);
			}
		}
	}


	/**
	 * �����о���
	 */
	public final void getColumn(int col, GVector vector) {
		if (vector.getSize() < nRow)
			vector.setSize(nRow);

		vector.values = this.getColElement(col);
	}

	/**
	 * �Ҿ���ת�ã����������Ҿ��󲢷���ֵ����ǰ����
	 * 
	 * @param m1
	 * @param m2
	 */
	public final void mulTransposeRight(FGMatrix m1, FGMatrix m2) {
		int i, j;
		double value = 0.0;

		if (m1.nCol != m2.nCol || nCol != m2.nRow || nRow != m1.nRow)
			throw new MismatchedSizeException(VecMathI18N
					.getString("GMatrix15"));

		for (i = 0; i < this.nRow; i++) {
			for (j = 0; j < this.nCol; j++) {
				ArrayData row = m1.getRowElementNonZero(i);
				ArrayData col = m2.getRowElementNonZero(j);
				/* ����������һ��Ϊ�գ���ô��Ԫ�ؿ϶�Ϊ0 */
				if ((row.val.isEmpty()) || (col.val.isEmpty())) {
					continue;
				}
				value = 0.0;
				for (int pos : row.val.keySet()) {
					if (col.val.containsKey(pos)) {
						value += row.val.get(pos) * col.val.get(pos);
					}
				}
				this.setElement(i, j, value);
			}
		}
	}

	/**
	 * toString
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(nRow * nCol * 8);

		int i, j;
		for (i = 0; i < nRow; i++) {
			for (j = 0; j < nCol; j++) {
				buffer.append(this.getElement(i, j)).append(" ");
			}
			buffer.append("\n");
		}

		return buffer.toString();
	}
}
