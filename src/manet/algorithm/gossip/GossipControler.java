package manet.algorithm.gossip;

import java.util.ArrayList;
import java.util.List;

import manet.communication.EmitterDecorator;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

/**
 * @author pixel
 *
 */
public class GossipControler implements Control {
	/* 
	 * Controller permettant au similuateur de lancer les diffusions 
	 * Ainsi que caculer l'atteignabilite des noeuds et l'economie de rediffusion
	 */
	
	private static final String PAR_EMITTERPID = "emitterdecoratorprotocol";
	private static final String PAR_GOSSIPPID = "gossipprotocol";
	private static final String PAR_WAVESNUMBER = "wavesnumber";

	private final int emitterdecorator_pid;
	private final int gossip_pid;
	private final int waves_number; // Nombre de vague
	private Node node;
	private int wave; // Numero de la vague actuelle
	/**
	 * Liste permettant de stocker le nombre de noeuds moyen atteignables (pour calculer l'ecart type)
	 */
	private List<Double> atts;
	
	/**
	 * Liste permettant de stocker le nombre de noeuds moyen ne rediffusant pas (pour calculer l'ecart type)
	 */
	private List<Double> ERs;
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
				
			if (!start) {// Permet initialement de lancer une diffusion avant de calculer les ATT et ER
					atts.add(getAtt()); // calculate and save this wave's Att
					ERs.add(getER()); // calculate and save this wave's ER
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
		return false;
	}
	
	/**
	 * Remet l'etat initial des variables utilisees par les algorithmes de diffusion
	 */
	private void resetStates() {
		for (int i = 0; i < Network.size(); i++) {
			Node n = Network.get(i);
			GossipProtocolAbstract gpf = ((GossipProtocolAbstract) n.getProtocol(gossip_pid));
			if (gpf.getFirstRecv()) {
				gpf.setFirstRecv(false);
				gpf.setAlreadySent(false);
			}
			gpf.setTimerArmed(0);
		}
	}

	/**
	 * @return Pourcentage de noeuds atteignables ayant recu le message
	 */
	public double getAtt() {
		double nbAtt = 0 ;
		for (int i = 0; i < Network.size(); i++) { // increment the number of attainable nodes
			Node n = Network.get(i);
			GossipProtocolAbstract gpf = ((GossipProtocolAbstract) n.getProtocol(gossip_pid));
			if (gpf.getFirstRecv()) {
				nbAtt++;
			}

		}

		return nbAtt / ((double) Network.size());
	}

	/**
	 * @return Pourcentage de noeuds atteignables ayant recu le message et n'ayant pas retransmis
	 */
	public double getER() {
		double r = 0;
		double t = 0;
		for (int i = 0; i < Network.size(); i++) { // increment the number of attainable nodes and those who already transmitted
			Node n = Network.get(i);
			GossipProtocolAbstract gpf = ((GossipProtocolAbstract) n.getProtocol(gossip_pid));
			r += gpf.getFirstRecv() ? 1 : 0;
			t += gpf.getAlreadySent() ? 1 : 0;
		}

		return (r - t) / r;
	}
	

	/**
	 * @return le nombre de noeuds moyen atteignables
	 */
	public double getAverageAtt() {
		return atts.stream().reduce((e1,e2)->e1+e2).get()/atts.size();
	}

	/**
	 * @return le nombre de noeuds moyen ne rediffusant pas
	 */
	public double getAverageER() {
		return ERs.stream().reduce((e1,e2)->e1+e2).get()/ERs.size();
	}

	/**
	 * @return l'ecart type de l'atteignabilite
	 */
	public double getAttStandardDeviation() {
		double num = getAverageAtt();
		return Math.sqrt(atts.parallelStream().map(e->Math.pow((e - num), 2)).reduce((e1,e2)->e1+e2).get()/atts.size());
	}

	/**
	 * @return l'ecart type de l'economie de rediffusiant
	 */
	public double getERStandardDeviation() {
		double num = getAverageER();
		return Math.sqrt(ERs.parallelStream().map(e->Math.pow((e - num), 2)).reduce((e1,e2)->e1+e2).get()/ERs.size());
		
	}

	/**
	 * Choisit aleatoirement l'identifiant de l'initiateur de la prochaine diffusion
	 */
	private void initialize() {
		node = Network.get(Math.abs(CommonState.r.nextInt(Network.size())));
	}
}
