package manet.algorithm.gossip;

import java.util.ArrayList;
import java.util.List;

import manet.communication.EmitterDecorator;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class GossipControler implements Control {

	private static final String PAR_EMITTERPID = "emitterdecoratorprotocol";
	private static final String PAR_GOSSIPPID = "gossipprotocol";
	private static final String PAR_WAVESNUMBER = "wavesnumber";

	private final int emitterdecorator_pid;
	private final int gossip_pid;
	private final int waves_number;
	private Node node;
	private int wave;
	private List<Double> atts;
	private List<Double> ERs;
	private double nbAtt;
	private boolean start = true;

	public GossipControler(String prefix) {
		emitterdecorator_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
		gossip_pid = Configuration.getPid(prefix + "." + PAR_GOSSIPPID);
		waves_number = Configuration.getInt(prefix + "." + PAR_WAVESNUMBER);
		wave = waves_number;
		atts = new ArrayList<Double>();
		ERs = new ArrayList<Double>();
		initialize(); // Pick a new node to broadcast
	}

	@Override
	public boolean execute() {
		if (wave > 0) {
			
			if (((EmitterDecorator) node.getProtocol(emitterdecorator_pid)).getN() == 0) { // the previous wave has finished
				
			if (!start) {
					
					System.out.println("vague " + (waves_number - wave + 1) + " " + node.getID());
					double d = getAtt();
					System.out.println("atteignabilité = " + d);
					atts.add(d); // calculate and save this wave's Att
					ERs.add(getER()); // calculate and save this wave's ER
					// System.out.println("ER = "+getER());
					wave--; // decrement number of remaining waves
					resetStates();
					
				}
				initialize(); // Pick a new node to broadcast
				((GossipProtocolAbstract) node.getProtocol(gossip_pid)).initiateGossip(node, wave, node.getID()); // initiate a new wave
				start = false;
			}
		} else {
			System.out.println("Moyenne Att : " + getAverageAtt());
			System.out.println("Moyenne ER : " + getAverageER());
			System.out.println("Ecart type Att : " + getAttStandardDeviation());
			System.out.println("Ecart type ER : " + getERStandardDeviation());
			return true;
		}
		// System.out.println("ALLO"+atts.size());
		return false;
	}
	
	private void resetStates() {
		for (int i = 0; i < Network.size(); i++) { // reset the first time reception boolean to true
			Node n = Network.get(i);
			GossipProtocolAbstract gpf = ((GossipProtocolAbstract) n.getProtocol(gossip_pid));
			if (gpf.getFirstRecv()) {
				gpf.setFirstRecv(false);
				gpf.setAlreadySent(false);
			}
			gpf.setTimerArmed(false);
		}
	}

	public double getAtt() {
		// Pourcentage de noeuds atteignables ayant recu le message
		nbAtt = 0;
		for (int i = 0; i < Network.size(); i++) { // reset the first time reception boolean to true
			Node n = Network.get(i);
			GossipProtocolAbstract gpf = ((GossipProtocolAbstract) n.getProtocol(gossip_pid));
			if (gpf.getFirstRecv()) {
				nbAtt++;
			}

		}

		System.out.println();
		System.out.println("nbAtt = " + nbAtt);

		return nbAtt / ((double) Network.size());
	}

	public double getER() {
		double r = 0;
		double t = 0;
		// Nombre de noeuds ayant recu r
		// Nombre de noeuds ayant transmis t
		// ER = (r - t) / r
		for (int i = 0; i < Network.size(); i++) {
			Node n = Network.get(i);
			GossipProtocolAbstract gpf = ((GossipProtocolAbstract) n.getProtocol(gossip_pid));
			r += gpf.getFirstRecv() ? 1 : 0;
			t += gpf.getAlreadySent() ? 1 : 0;
		}

		System.out.println("r = " + r + ", t = " + t);

		return (r - t) / r;
	}
	

	public double getAverageAtt() {
		double sum = 0;
		for (int i = 0; i < atts.size(); i++) {
			sum += atts.get(i);
		}

		return sum / atts.size();
	}

	public double getAverageER() {
		double sum = 0;
		for (int i = 0; i < ERs.size(); i++) {
			sum += ERs.get(i);
		}
		return sum / ERs.size();
	}

	public double getAttStandardDeviation() {
		double sum = 0;
		double num = getAverageAtt();
		for (int i = 0; i < atts.size(); i++) {
			sum += Math.pow((atts.get(i) - num), 2);
		}
		return Math.sqrt(sum / atts.size());
	}

	public double getERStandardDeviation() {
		double sum = 0;
		double num = getAverageER();
		for (int i = 0; i < ERs.size(); i++) {
			sum += Math.pow((ERs.get(i) - num), 2);
		}
		return Math.sqrt(sum / ERs.size());
	}

	private void initialize() {
		node = Network.get(Math.abs(CommonState.r.nextInt(Network.size())));
	}
}
