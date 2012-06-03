package javax.vecmath;

import java.util.ArrayList;
import java.util.List;

public class LGMatrix {

	/**
	 * Scaled for big data
	 */
	private static final long serialVersionUID = 8814686708709891913L;

	/**
	 * Inner class data It's an element of matrix
	 * 
	 * @author yinxs
	 * 
	 */
	class Data {
		public int i;
		public int j;
		public double val;

		public Data(int i, int j) {
			this.i = i;
			this.j = j;
			this.val = 0.0;
		}
	}

	int nRow, nCol;
	List<Data> values;

	public LGMatrix(int row, int col) {
		nRow = row;
		nCol = col;
		this.values = new ArrayList<Data>();
	}

	/**
	 * if the position is the same
	 * 
	 * @param d
	 * @param i
	 * @param j
	 * @return
	 */
	public boolean samePos(Data d, int i, int j) {
		if ((d.i == i) && (d.j == j)) {
			return true;
		}
		return false;
	}

	/**
	 * if the position is the same
	 * 
	 * @param d
	 * @param d2
	 * @return
	 */
	public boolean samePos(Data d, Data d2) {
		if ((d.i == d2.i) && (d.j == d2.j)) {
			return true;
		}
		return false;
	}

	/**
	 * set value to an elements. If there is not such an element, create it.
	 */
	public void setElement(int row, int column, double value) {
		for (Data d : this.values) {
			if (samePos(d, row, column)) {
				d.val = value;
				return;
			}
		}
		Data d = new Data(row, column);
		d.val = value;
		this.values.add(d);
		return;
	}

	public int getNumRow() {
		return (nRow);
	}

	public int getNumCol() {
		return (nCol);
	}

	public final double getElement(int row, int column) {
		assert ((row < nRow) && (column < nCol));
		for (Data d : this.values) {
			if ((d.i == row) && (d.j == column)) {
				return d.val;
			}
		}
		return (0.0);
	}

	/**
	 * 获取一行数据，并且返回其数组
	 * 
	 * @param row
	 * @return
	 */
	public double[] getRowElement(int row) {
		double[] res = new double[nCol];
		for (int i = 0; i < nCol; ++i) {
			res[i] = 0.0;
		}
		for (Data d : this.values) {
			if (d.i == row) {
				res[d.j] = d.val;
			}
		}
		return res;
	}

	/**
	 * 获取一列数据，并返回其数组
	 * 
	 * @param col
	 * @return
	 */
	public double[] getColElement(int col) {
		double[] res = new double[nRow];
		for (int i = 0; i < nRow; ++i) {
			res[i] = 0.0;
		}
		for (Data d : this.values) {
			if (d.j == col) {
				res[d.i] = d.val;
			}
		}
		return res;
	}

	/**
	 * 矩阵m1 和 m2相乘，赋值给当前矩阵
	 * 
	 * @param m1
	 * @param m2
	 */
	public final void mul(LGMatrix m1, LGMatrix m2) {
		int i, j, k;

		if (m1.nCol != m2.nRow || nRow != m1.nRow || nCol != m2.nCol)
			throw new MismatchedSizeException(VecMathI18N.getString("GMatrix1"));

		for (i = 0; i < m1.nRow; i++) {
			for (j = 0; j < m2.nCol; j++) {
				double[] row1 = m1.getRowElement(i);
				double[] col2 = m2.getColElement(j);
				double value = 0.0;
				for (k = 0; k < m1.nCol; k++) {
					value += row1[k] * col2[k];
				}
				this.setElement(i, j, value);
			}
		}
	}

	/**
	 * 左侧矩阵转置乘以右侧矩阵，并赋值给当前矩阵
	 * 
	 * @param m1
	 * @param m2
	 */
	public final void mulTransposeLeft(LGMatrix m1, LGMatrix m2) {
		int i, j, k;

		if (m1.nRow != m2.nRow || nCol != m2.nCol || nRow != m1.nCol)
			throw new MismatchedSizeException(VecMathI18N
					.getString("GMatrix16"));

		for (i = 0; i < this.nRow; i++) {
			for (j = 0; j < this.nCol; j++) {
				double[] row1 = m1.getColElement(i);
				double[] col2 = m2.getColElement(j);
				double value = 0.0;
				for (k = 0; k < row1.length; k++) {
					value += row1[k] * col2[k];
				}
				this.setElement(i, j, value);
			}
		}
	}

	/**
	 * 两矩阵点乘，并返回结果矩阵
	 * 
	 * @param l
	 * @param r
	 * @return
	 */
	public LGMatrix DotMul(LGMatrix l, LGMatrix r) {
		assert (l.getNumRow() == r.getNumRow());
		assert (l.getNumCol() == r.getNumCol());
		LGMatrix retval = new LGMatrix(l.getNumRow(), l.getNumCol());
		for (int i = 0; i < l.getNumRow(); ++i) {
			for (int j = 0; j < l.getNumCol(); ++j) {
				retval
						.setElement(i, j, l.getElement(i, j)
								* r.getElement(i, j));
			}
		}
		return retval;
	}

	/**
	 * 两矩阵点除，并返回结果矩阵
	 * 
	 * @param l
	 * @param r
	 * @return
	 */
	public LGMatrix DotDiv(LGMatrix l, LGMatrix r) {
		assert (l.getNumRow() == r.getNumRow());
		assert (l.getNumCol() == r.getNumCol());
		LGMatrix retval = new LGMatrix(l.getNumRow(), l.getNumCol());
		for (int i = 0; i < l.getNumRow(); ++i) {
			for (int j = 0; j < l.getNumCol(); ++j) {
				if (r.getElement(i, j) == 0.0) {
					r.setElement(i, j, 0.0000000001 / (r.getNumCol() * r
							.getNumRow()));
				}
				if (l.getElement(i, j) == 0.0) {
					r.setElement(i, j, 0.0000000001 / (l.getNumCol() * l
							.getNumRow()));
				}
				retval.setElement(i, j, 1.0 * l.getElement(i, j)
						/ r.getElement(i, j));
			}
		}
		return retval;
	}

	/**
	 * 返回列矩阵
	 */
	public final void getColumn(int col, GVector vector) {
		if (vector.getSize() < nRow)
			vector.setSize(nRow);

		vector.values = this.getColElement(col);
	}

	/**
	 * 右矩阵转置，左矩阵乘以右矩阵并返回值给当前矩阵
	 * 
	 * @param m1
	 * @param m2
	 */
	public final void mulTransposeRight(LGMatrix m1, LGMatrix m2) {
		int i, j, k;

		if (m1.nCol != m2.nCol || nCol != m2.nRow || nRow != m1.nRow)
			throw new MismatchedSizeException(VecMathI18N
					.getString("GMatrix15"));

		for (i = 0; i < this.nRow; ++i) {
			for (j = 0; j < this.nCol; ++j) {
				double[] row1 = m1.getRowElement(i);
				double[] col2 = m2.getRowElement(j);
				double value = 0.0;
				for (k = 0; k < row1.length; k++) {
					value += row1[k] * col2[k];
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
