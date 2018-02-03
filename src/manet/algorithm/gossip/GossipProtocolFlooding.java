package manet.algorithm.gossip;

import manet.Message;
import manet.communication.EmitterDecorator;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import utils.MessageType;

public class GossipProtocolFlooding extends GossipProtocolAbstract {

	private final int emitterdecorator_pid;

	public GossipProtocolFlooding(String prefix) {
		emitterdecorator_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
	}

	public GossipProtocolFlooding clone() {
		return (GossipProtocolFlooding) super.clone();
	}

	@Override
	public void initiateGossip(Node host, int id, long id_initiator) {
		alreadySent = true;
		firstRecv = true;
		Message msg = new Message(host.getID(), -1, MessageType.flooding, null, emitterdecorator_pid); // tag
																											// ==
		((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, msg); // emit
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
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
