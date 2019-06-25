package logic;

import java.io.Serializable;

public class Posicao implements Serializable {
	private static final long serialVersionUID = -7013579423472909226L;
	private int x;
	private int y;

	public Posicao(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return String.format("X[%d]Y[%d]", x, y);
	}
}
