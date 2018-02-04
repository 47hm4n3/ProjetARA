package manet.algorithm.gossip;

import java.util.ArrayList;
import manet.Message;
import manet.communication.EmitterDecorator;
import manet.detection.NeighborProtocolImpl;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import utils.DataProtoProba;
import utils.MessageType;

public class GossipProtocolQ8 extends GossipProtocolAbstract {
	
	
	protected static final String PAR_NEIGHBORPID = "neighborprotocol";
	protected static final String PAR_TMIN = "tmin";
	protected static final String PAR_TMAX = "tmax";
	protected static final String PAR_START ="strat";

	private final int emitterdecorator_pid;
	private final int neighborProtocol_pid;
	private final int my_pid;
	private double prob;
	private final int tmin;
	private final int tmax;
	private final int strat; // Choix entre 3 (algorithme k/V) et 4 (algorithme distance)
	
	private ArrayList<Long> nodeList;

	public GossipProtocolQ8(String prefix) {
		String tmp[] = prefix.split("\\.");
		my_pid = Configuration.lookupPid(tmp[tmp.length - 1]);
		emitterdecorator_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
		neighborProtocol_pid = Configuration.getPid(prefix + "." + PAR_NEIGHBORPID);
		tmin = Configuration.getInt(prefix+"."+PAR_TMIN);
		tmax = Configuration.getInt(prefix+"."+PAR_TMAX);
		strat = Configuration.getInt(prefix+"."+PAR_START);
		
		if(strat != 3 && strat !=4) {
			System.err.println("ERREUR STRAT DIFFERENT DE 3 ET 4");
			System.exit(-1);
		}
		
	}

	public GossipProtocolQ8 clone () {
		nodeList = new ArrayList<Long>();
		return (GossipProtocolQ8) super.clone();
	}


	@Override
	public void initiateGossip(Node host, int id, long id_initiator) {
		alreadySent = true; 
		firstRecv = true;
		isTimerArmed = 0;
		Message msg = null;
		DataProtoProba d = new DataProtoProba(getNeighbors(host),-1);
		switch(strat) {
		case 3 : msg = new Message(host.getID(), -1, MessageType.flooding_algo3_algo8, d, emitterdecorator_pid); // tag == flooding
			break;
			
		case 4 : msg = new Message(host.getID(), -1, MessageType.flooding_algo4_algo8, d, emitterdecorator_pid); // tag == flooding
			break;
		default :
			System.exit(-1);
		}
		((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, msg);
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {

		if (event instanceof Message) {
			
			Message msg = new Message(host.getID(), host.getID(), MessageType.decrement, firstRecv,
					emitterdecorator_pid);
			Message m = (Message) event;
			switch(m.getTag()) {
			case MessageType.flooding_algo4_algo8:
			case MessageType.flooding_algo3_algo8: 
				if(isTimerArmed==1) {
					// QUAND ON RECOIT DES MESSAGES PENDANT LE TIMER
					ArrayList<Long> l = (ArrayList<Long>)((DataProtoProba)m.getContent()).getL();
					nodeList.removeAll(l);
				}
				
				if(!firstRecv) {
					// Uniquement exécutée à la première réception
					firstRecv = true;
					nodeList = getNeighbors(host); // Initialisation de lx à la liste de voisins
					ArrayList<Long> l = (ArrayList<Long>)((DataProtoProba)m.getContent()).getL(); // Récupère les voisins de l'émetteur
					nodeList.removeAll(l); // On soutrait de ses voisins, les voisins de l'émetteur
					DataProtoProba d = new DataProtoProba(getNeighbors(host),-1);
					Message newMsg = new Message(host.getID(), -1, MessageType.flooding_algo3_algo8, d, emitterdecorator_pid); // tag == flooding
					prob =  (double)((DataProtoProba)m.getContent()).getProba();
					if (CommonState.r.nextDouble() < prob) {
						((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, newMsg);
						alreadySent = true;
					}else {
						// On arme le timer si on a pas pu envoyer
						isTimerArmed ++;
						double timer = (CommonState.r.nextDouble()*(tmax-tmin))+tmin;
						Message tm = new Message(host.getID(), host.getID(), MessageType.timer_algo8, null, my_pid);
						EDSimulator.add((int)timer,tm,host,my_pid);
						return;
					}
					
				}
				EDSimulator.add(0, msg, host, emitterdecorator_pid); // Decremente reception
				break;
			case MessageType.timer_algo8 :
				// TIME OUT
				if(!nodeList.isEmpty()) {
					// Si la liste lx est vide, on diffuse
					Message newMsg = null;
					DataProtoProba d = new DataProtoProba(getNeighbors(host),-1);
					switch(strat) {
					case 3 : newMsg = new Message(host.getID(), -1, MessageType.flooding_algo3_algo8, d, emitterdecorator_pid); // tag == flooding
						break;
					case 4 : newMsg = new Message(host.getID(), -1, MessageType.flooding_algo4_algo8, d, emitterdecorator_pid); // tag == flooding
						break;
					default :
						System.exit(-1);
					}
					((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, newMsg);
					alreadySent = true;
				}
				EDSimulator.add(0, msg, host, emitterdecorator_pid);
				isTimerArmed ++; // On ne trait plus les message suivant, à partir de ce moment on en fait que décrémenter le nombre de message en transit
				break;
			default :
				System.out.println("ERROR MESSAGE GOSSIP ALGO 8");
				System.exit(-1);
			}
		}
	}

	private ArrayList<Long> getNeighbors(Node host){
		NeighborProtocolImpl NB = (NeighborProtocolImpl) host.getProtocol(neighborProtocol_pid);
		return (ArrayList<Long>) ((ArrayList<Long>) NB.getNeighbors()).clone();
	}

}
