package manet;

import java.util.ArrayList;
import java.util.List;

import manet.detection.NeighborProtocolImpl;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

public class DensityController implements Control{

	private static final String PAR_NEIGHBORPID = "neighborprotocol";

	private int neighbour_pid;
	private NeighborProtocolImpl []neighbourProt;
	private List<Double> densities;
	private List<Double> standardDeviations;
	private int SIZE;
	
	public DensityController(String prefix) {
		neighbour_pid = Configuration.getPid(prefix+"."+PAR_NEIGHBORPID);
		densities = new ArrayList<Double>();
		standardDeviations  = new ArrayList<Double>();
		this.SIZE = Network.size();
		neighbourProt = new NeighborProtocolImpl[SIZE];
		for(int i = 0; i < SIZE; i++) 
			neighbourProt[i] = (NeighborProtocolImpl)Network.get(i).getProtocol(neighbour_pid);
		
	}

	@Override
	public boolean execute() {
		densities.add(getDensity());
		standardDeviations.add(getStandardDeviation());
		//System.out.println("----------- Density "+getDensity());
		//System.out.println("----------- StandardDeviation "+getStandardDeviation());
		//System.out.println("----------- AverageDensity "+getAverageDensity());
		//System.out.println("----------- AverageStandardDeviation "+getAverageStandardDeviation());
		//System.out.println("----------- DensityStandardDeviation "+getDensityStandardDeviation());
		//System.out.println("-------------------------------");
		System.out.println("----------- D "+getAverageDensity());
		System.out.println("----------- E/D "+getStandardDeviation()/getAverageDensity());
		System.out.println("----------- ED/D "+getDensityStandardDeviation()/getAverageDensity());
		return false;
	}

	public double getDensity() {
		double sum = 0;
		for(int i = 0; i < SIZE; i++) {
			sum += neighbourProt[i].getNeighbors().size();
		}
		return sum/Network.size();
	}
	
	public double getStandardDeviation() {
		double density = densities.get(densities.size()-1); // on le calcule � la premi�re instruction de la m�thode execute()
		double sum = 0;
		for (int i = 0; i < SIZE; i++) {
			 sum += Math.pow(((double)neighbourProt[i].getNeighbors().size() - density),2) ;
		}
		return Math.sqrt(sum/Network.size());
	}
	
	public double getAverageDensity() {
		double sum = 0;
		for (int i = 0; i < densities.size(); i++) {
			sum += densities.get(i);
		}
		return sum/densities.size();
	}
	
	public double getAverageStandardDeviation() {
		double sum = 0;
		for (int i = 0; i < standardDeviations.size(); i++) {
			sum += standardDeviations.get(i);
		}
		return sum/standardDeviations.size();
	}
	
	public double getDensityStandardDeviation() {
		double sum = 0;
		double num = getAverageDensity();
		for (int i = 0; i < densities.size(); i++) {
			sum += Math.pow((densities.get(i) - num),2);
		}
		return Math.sqrt(sum/densities.size());
	}
	
}
