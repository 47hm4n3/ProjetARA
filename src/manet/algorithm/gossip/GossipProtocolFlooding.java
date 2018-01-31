package manet.algorithm.gossip;

import manet.Message;
import manet.communication.EmitterDecorator;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import utils.MessageType;

public class GossipProtocolFlooding extends GossipProtocolAbstract {

	private static final String PAR_PROBA = "proba";

	private final int gossip_pid;
	private final int emitterdecorator_pid;
	private final double proba;

	public GossipProtocolFlooding(String prefix) {
		String tmp[] = prefix.split("\\.");
		gossip_pid = Configuration.lookupPid(tmp[tmp.length - 1]);
		emitterdecorator_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
		proba = Configuration.getDouble(prefix + "." + PAR_PROBA, 1.0);
	}

	public GossipProtocolFlooding clone() {
		return (GossipProtocolFlooding) super.clone();
	}

	@Override
	public void initiateGossip(Node host, int id, long id_initiator) {
		alreadySent = true;
		initiator = true;
		firstRecv = true;
		Message msg = new Message(host.getID(), -1, MessageType.flooding, initiator, emitterdecorator_pid); // tag
																											// ==
																											// flooding
		((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, msg); // emit
		// System.out.println(host.getID() + " initie la diffusion ");
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		if (event instanceof Message) {
			Message msg = new Message(host.getID(), host.getID(), MessageType.decrement, firstRecv,
					emitterdecorator_pid);
			if (!firstRecv) {
				firstRecv = true;
				// }
				Message m = (Message) event;

				// if (!alreadySent) {
				if (CommonState.r.nextDouble() < proba) {
					((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, m);
					alreadySent = true;
				}
			}
			EDSimulator.add(0, msg, host, emitterdecorator_pid); // Decremente
																	// reception
		}
	}

}
