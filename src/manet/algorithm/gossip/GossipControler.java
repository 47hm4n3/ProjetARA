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
			System.out.println("vague "+w);
			initialize(); // Pick a new node to broadcast
			if (((EmitterImplF)node.getProtocol(emitterflooding_pid)).getN() == 0) { // the previous wave is finished
				((GossipProtocolImpl)node.getProtocol(gossip_pid)).initiateGossip(node, w, node.getID());
				w--; // decrement number of remaining waves
				atts.add(getAtt());
				ERs.add(getER());
				for(int i=0;i<Network.size();i++) {
						((GossipProtocolAbstract)Network.get(i).getProtocol(gossip_pid)).setFirstTime(true);
				}
				
			}
		}
		return false;
	}

	public double getAtt () {
		double n = ((EmitterImplF)node.getProtocol(emitterflooding_pid)).getNinit();
		return n/Network.size();
	}
	
	public double getER () {
		return 0.0;
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
		
		return 0.0;
	}
	
	public double getERStandardDeviation () {
		
		return 0.0;
	}
	
	
	private void initialize() {
		node = Network.get(Math.abs(CommonState.r.nextInt(Network.size())));
	}
}
