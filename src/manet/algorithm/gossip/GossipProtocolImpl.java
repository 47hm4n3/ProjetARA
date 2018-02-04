package manet.algorithm.gossip;

import manet.Message;
import manet.communication.EmitterDecorator;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import utils.MessageType;

public class GossipProtocolImpl extends GossipProtocolAbstract{
	
	protected static final String PAR_GOSSIP_STRAT = "strat";
	
	
	private final int emitterdecorator_pid;
	private final int gossipstrat;
	
	public GossipProtocolImpl(String prefix) {
		emitterdecorator_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
		gossipstrat = Configuration.getInt(prefix+"."+PAR_GOSSIP_STRAT);
	}
	
	@Override
	public void initiateGossip(Node host, int id, long id_initiator) {
		alreadySent = true;
		firstRecv = true;
		Message msg = null;
		switch(gossipstrat) {
		case 1: msg = new Message(host.getID(), -1, MessageType.flooding_algo1, null, emitterdecorator_pid);	
			break;
		case 2 : msg = new Message(host.getID(), -1, MessageType.flooding_algo2, null, emitterdecorator_pid);	
			break;
		case 3 : msg = new Message(host.getID(), -1, MessageType.flooding_algo3, null, emitterdecorator_pid);	
			break;
		case 4 : msg = new Message(host.getID(), -1, MessageType.flooding_algo4, null, emitterdecorator_pid);	
			break;
		default :
			System.err.println("CLASS GOSSIPROTOCOLIMPL - PROTOCOL NON VALIDE");
			System.err.println("CHOISIR 1, 2, 3 ou 4");
			System.exit(-1);
		}
																								// ==
		((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, msg);
		
	}

	@Override
	public void processEvent(Node host, int pid, Object event)  {
		if (event instanceof Message) {
			if (!firstRecv) {
				firstRecv = true;
				Message m = (Message) event;
				Message newMsg = new Message(host.getID(), m.getIdDest(), m.getTag(), m.getContent(), m.getPid());
				double proba = (double) m.getContent();
				if (CommonState.r.nextDouble() < proba) {
					((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, newMsg);
					alreadySent = true;
				}
			}
			Message msg = new Message(host.getID(), host.getID(), MessageType.decrement, null,
					emitterdecorator_pid);
			EDSimulator.add(0, msg, host, emitterdecorator_pid); // Decremente
		 														// reception
		}
	}

}
