package manet.algorithm.gossip;

import manet.Message;
import manet.communication.EmitterDecorator;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import utils.MessageType;

public class GossipProtocolK extends GossipProtocolAbstract {
	
	private static final String PAR_K = "k";
	
	private final int emitterdecorator_pid;
	private final int k;
	private double prob;
	
	public GossipProtocolK(String prefix) {
		emitterdecorator_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
	k = Configuration.getInt(prefix + "." + PAR_K);
	}
	
	@Override
	public void initiateGossip(Node host, int id, long id_initiator) {
		alreadySent = true; 
		firstRecv = true;
		Message msg = new Message(host.getID(), -1, MessageType.flooding_algo3, null, emitterdecorator_pid); // tag == flooding
		((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, msg); // emit
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		
		if (event instanceof Message) {
			Message msg = new Message(host.getID(), host.getID(), MessageType.decrement, firstRecv,
					emitterdecorator_pid);
			if(!firstRecv) {
				firstRecv = true;
			Message m = (Message) event;
			Message newMsg = new Message(host.getID(), m.getIdDest(), m.getTag(), m.getContent(), m.getPid());
				prob = (double)m.getContent();
				System.out.println(host.getID() + " PROBA_K = " + prob);
				if (CommonState.r.nextDouble() < prob) {
					((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, newMsg);
					alreadySent = true;
				}
			}
			EDSimulator.add(0, msg, host, emitterdecorator_pid); // Decremente reception
		}	
	}

}
