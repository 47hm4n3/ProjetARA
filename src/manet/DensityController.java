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
	private double period;
	private NeighborProtocolImpl neighbourProt;
	private List<Double> densities;
	private List<Double> standardDeviations;
	
	public DensityController(String prefix) {
		neighbour_pid = Configuration.getPid(prefix+"."+PAR_NEIGHBORPID);
		densities = new ArrayList<Double>();
		standardDeviations  = new ArrayList<Double>();
	}

	@Override
	public boolean execute() {
		//EDSimulator.add(0, MessageType.stats, node, neighbour_pid);
		System.out.println(getDensity());
		densities.add(getDensity());
		standardDeviations.add(getStandardDeviation());
		System.out.println("----------- ADensity "+getDensity());
		System.out.println("----------- ADensity "+getStandardDeviation());
		System.out.println("----------- ADensity "+getAverageDensity());
		System.out.println("----------- AStandardDeviation "+getAverageStandardDeviation());
		System.out.println("----------- DensityStandardDeviation "+getDensityStandardDeviation());
		return false;
	}

	
	public double getDensity() {
		double sum = 0;
		for(int i = 0;i < Network.size();i++) {
			neighbourProt = (NeighborProtocolImpl)Network.get(i).getProtocol(neighbour_pid);
			sum += neighbourProt.getNeighbors().size();
		}
		return sum/Network.size();
	}
	
	public double getStandardDeviation() {
		double density = getDensity();
		double sum = 0;
		for (int i = 0; i<Network.size(); i++) {
			neighbourProt = (NeighborProtocolImpl)Network.get(i).getProtocol(neighbour_pid);
			 sum += Math.pow(((double)neighbourProt.getNeighbors().size() - density),2) ;
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
		for (int i =0; i< standardDeviations.size(); i++) {
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