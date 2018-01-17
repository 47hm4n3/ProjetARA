package manet.algorithm.gossip;


import java.util.ArrayList;
import java.util.List;

import manet.communication.EmitterImplF;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class GossipControler implements Control{
	
	private static final String PAR_EMITTERPID = "emitterfloodingprotocol";
	private static final String PAR_GOSSIPPID = "gossipprotocol";
	private static final String PAR_WAVESNUMBER = "wavesnumber";

	private final int emitterflooding_pid;
	private final int gossip_pid;
	private final int waves_number;
	private Node node;
	private int w;
	private List<Double> atts;
	private List<Double> ERs;
	
	public GossipControler (String prefix) {
		emitterflooding_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
		gossip_pid = Configuration.getPid(prefix + "." + PAR_GOSSIPPID);
		waves_number = Configuration.getInt(prefix + "." + PAR_WAVESNUMBER);
		w = waves_number;
		atts = new ArrayList<Double>();
		ERs = new ArrayList<Double>();
	}

	@Override
	public boolean execute() {
		if (w > 0) {
			System.out.println("vague "+ (waves_number - w + 1));
			initialize(); // Pick a new node to broadcast
			if (((EmitterImplF)node.getProtocol(emitterflooding_pid)).getN() == 0) { // the previous wave has finished
				for(int i=0;i<Network.size();i++) { // reset the first time reception boolean to true
					((GossipProtocolAbstract)Network.get(i).getProtocol(gossip_pid)).setFirstTime(true);
				}
				((GossipProtocolImpl)node.getProtocol(gossip_pid)).initiateGossip(node, w, node.getID()); // initiate a new wave
				w--; // decrement number of remaining waves
				atts.add(getAtt()); // calculate and save this wave's Att
				System.out.println("Att = "+getAtt());
				ERs.add(getER()); // calculate and save this wave's ER
				System.out.println("ER  = "+getER());
			}
		}
		return false;
	}

	public double getAtt () {
		// Pourcentage de noeuds atteignables ayant recu le message
		return ((EmitterImplF)node.getProtocol(emitterflooding_pid)).getN()/Network.size();
	}
	
	public double getER () {
		int r = 1;
		int t = 0;
		// Nombre de noeuds ayant recu r
		// Nombre de noeuds ayant transmis t
		// ER = (r - t) / r
		return (r - t)/r;
	}

	public double getAverageAtt () {
		double sum = 0;
		for (int i =0; i < atts.size(); i++) {
			sum += atts.get(i);
		}
		return sum/atts.size();
	}
	
	public double getAverageER () {
		double sum = 0;
		for (int i =0; i < ERs.size(); i++) {
			sum += ERs.get(i);
		}
		return sum/ERs.size();
	}
	
	public double getAttStandardDeviation () {
		double sum = 0;
		double num = getAverageAtt();
		for (int i =0; i < atts.size(); i++) {
			sum += Math.pow((atts.get(i) - num),2);
		}
		return Math.sqrt(sum/atts.size());
	}
	
	public double getERStandardDeviation () {
		double sum = 0;
		double num = getAverageER();
		for (int i =0; i < ERs.size(); i++) {
			sum += Math.pow((ERs.get(i) - num),2);
		}
		return Math.sqrt(sum/ERs.size());
	}
	
	private void initialize() {
		node = Network.get(Math.abs(CommonState.r.nextInt(Network.size())));
	}
}
