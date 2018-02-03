package utils;

import java.util.ArrayList;

public class DataProtoProba {
	
	private ArrayList<Long> l;
	private double proba;
	
	public DataProtoProba(ArrayList<Long> l, double proba) {
		this.l = l;
		this.proba = proba;
	}

	public ArrayList<Long> getL() {
		return l;
	}

	public void setL(ArrayList<Long> l) {
		this.l = l;
	}

	public double getProba() {
		return proba;
	}

	public void setProba(double proba) {
		this.proba = proba;
	}
	
	
}
