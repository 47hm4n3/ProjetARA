package manet.algorithm.gossip;

import manet.Message;
import manet.communication.EmitterDecorator;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import utils.MessageType;

public class GossipProtocolDistance extends GossipProtocolAbstract {

	private final int emitterdecorator_pid;
	private double proba;
	static Integer cpt = 0;

	public GossipProtocolDistance(String prefix) {
		emitterdecorator_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
	}

	@Override
	public void initiateGossip(Node host, int id, long id_initiator) {
		alreadySent = true;
		firstRecv = true;
		System.out.println(host.getID() + " est initateur");
		Message msg = new Message(host.getID(), -1, MessageType.flooding_algo4, null, emitterdecorator_pid); // tag
																													// ==
		((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, msg); // emit
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		if (event instanceof Message) {
			Message msg = new Message(host.getID(), host.getID(), MessageType.decrement, null,
					emitterdecorator_pid);
			if (!firstRecv) {
				firstRecv = true;
				Message m = (Message) event;
				proba = (double) m.getContent();

				double rdm = CommonState.r.nextDouble();
				System.out.println(host.getID() + " PROBA " + proba + ", rdm = " + rdm);
				if (rdm < proba) {
					((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, m);
					alreadySent = true;
				} else {
					cpt++;
					System.out.println(host.getID() + " ne retransmet pas " + cpt);
				}
			}
			EDSimulator.add(0, msg, host, emitterdecorator_pid); // Decremente
																	// reception
		}

	}

}
