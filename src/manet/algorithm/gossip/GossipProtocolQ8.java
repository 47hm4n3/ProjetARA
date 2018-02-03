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

	private final int emitterdecorator_pid;
	private final int neighborProtocol_pid;
	private final int my_pid;
	private double prob;
	private final int tmin;
	private final int tmax;
	
	
	
	private ArrayList<Long> nodeList;

	public GossipProtocolQ8(String prefix) {
		String tmp[] = prefix.split("\\.");
		my_pid = Configuration.lookupPid(tmp[tmp.length - 1]);
		emitterdecorator_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
		neighborProtocol_pid = Configuration.getPid(prefix + "." + PAR_NEIGHBORPID);
		tmin = Configuration.getInt(prefix+"."+PAR_TMIN);
		tmax = Configuration.getInt(prefix+"."+PAR_TMAX);
		
	}

	public GossipProtocolQ8 clone () {
		nodeList = new ArrayList<Long>();
		return (GossipProtocolQ8) super.clone();
	}


	@Override
	public void initiateGossip(Node host, int id, long id_initiator) {
		alreadySent = true; 
		firstRecv = true;
		System.out.println(host.getID()+" JE SUIS LE INIT, MES VOISINS SONT : ");
		System.out.println(getNeighbors(host));
		Message msg = new Message(host.getID(), -1, MessageType.flooding_algo3_algo8, getNeighbors(host), emitterdecorator_pid); // tag == flooding
		((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, msg); // emit
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {

		if (event instanceof Message) {
			Message msg = new Message(host.getID(), host.getID(), MessageType.decrement, firstRecv,
					emitterdecorator_pid);
			Message m = (Message) event;
			switch(m.getTag()) {
			case MessageType.flooding_algo3_algo8 : 
				if(isTimerArmed) {
					
					System.out.println(host.getID()+ " JAI RECU DE MON VOISIN ID : "+m.getIdSrc());
					ArrayList<Long> l = (ArrayList<Long>)((DataProtoProba)m.getContent()).getL();
					System.out.println(l);
					
					nodeList.removeAll(l);

					System.out.println(host.getID()+ " MA LISTE NEW LX");
					System.out.println(nodeList);
					return;
				}
				if(!firstRecv) {
					nodeList = getNeighbors(host);

					System.out.println(host.getID()+ " MA LISTE DE VOISINS");
					System.out.println(nodeList);

					System.out.println(host.getID()+ " JAI RECU DE MON VOISIN ID : "+m.getIdSrc());
					ArrayList<Long> l = (ArrayList<Long>)((DataProtoProba)m.getContent()).getL();
					System.out.println(l);

					nodeList.removeAll(l);

					System.out.println(host.getID()+ " MA LISTE LX");
					System.out.println(nodeList);

					firstRecv = true;
					Message newMsg = new Message(host.getID(), m.getIdDest(), m.getTag(), nodeList, m.getPid());
					prob =  (double)((DataProtoProba)m.getContent()).getProba();
					System.out.println(host.getID() + " PROBA_K = " + prob);
					if (CommonState.r.nextDouble() < prob) {
						((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, newMsg);
						alreadySent = true;
					}else {
						isTimerArmed = true;
						int timer = (CommonState.r.nextInt()*(tmax-tmin))+tmin;
						System.out.println(host.getID()+ " JAI CHOISIS TIMER : "+timer);
						Message tm = new Message(host.getID(), host.getID(), MessageType.timer_algo8, null, my_pid);
						EDSimulator.add(timer,tm,host,my_pid);
					}
				}
				EDSimulator.add(0, msg, host, emitterdecorator_pid); // Decremente reception
				break;
			case MessageType.timer_algo8 :
				if(nodeList.size()!=0) {
					Message newMsg = new Message(host.getID(), m.getIdDest(), m.getTag(), nodeList, m.getPid());
					((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, newMsg);
				}
				break;
			default :
				System.out.println("ERROR MESSAGE GOSSIPALGO8");
				break;
			}

		}	
	}

	private ArrayList<Long> getNeighbors(Node host){
		NeighborProtocolImpl NB = (NeighborProtocolImpl) host.getProtocol(neighborProtocol_pid);
		return (ArrayList<Long>) NB.getNeighbors();
	}

}
