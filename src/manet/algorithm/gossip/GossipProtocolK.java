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
	
	private final int gossip_pid;
	private final int emitterdecorator_pid;
	private final int k;
	private double prob;
	
	
	
	public GossipProtocolK(String prefix) {
		String tmp[] = prefix.split("\\.");
		gossip_pid = Configuration.lookupPid(tmp[tmp.length - 1]);
		emitterdecorator_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
		k = Configuration.getInt(prefix + "." + PAR_K);
	}
	
	@Override
	public void initiateGossip(Node host, int id, long id_initiator) {
		alreadySent = true; 
		initiator = true;
		firstRecv = true;
		System.out.println(host.getID() + " est initateur");
		Message msg = new Message(host.getID(), -1, MessageType.flooding_algo3, initiator, emitterdecorator_pid); // tag == flooding
		((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, msg); // emit
		// System.out.println(host.getID() + " initie la diffusion ");
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		
		if (event instanceof Message) {
			Message msg = new Message(host.getID(), host.getID(), MessageType.decrement, firstRecv,
					emitterdecorator_pid);
			if(!firstRecv) {
				firstRecv = true;
			}
			Message m = (Message) event;
			Message newMsg = new Message(m.getIdSrc(), m.getIdDest(), m.getTag(), null, m.getPid());
			if (!alreadySent) {
				prob = ((double)k)/((int)m.getContent());
				if (CommonState.r.nextDouble() < prob) {
					((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, newMsg);
					alreadySent = true;
				}
			}
			EDSimulator.add(0, msg, host, emitterdecorator_pid); // Decremente reception
		}
		
	}

}
