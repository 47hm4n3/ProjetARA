package manet.algorithm.gossip;

import manet.Message;
import manet.communication.EmitterImplF;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import utils.MessageType;

public class GossipProtocolImpl extends GossipProtocolAbstract {

	private static final String PAR_EMITTERPID = "emitterfloodingprotocol";

	private final int gossip_pid;
	private final int emitterflooding_pid;
	public GossipProtocolImpl(String prefix) {
		String tmp[] = prefix.split("\\.");
		gossip_pid = Configuration.lookupPid(tmp[tmp.length - 1]);
		emitterflooding_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
	}

	@Override
	public void initiateGossip(Node host, int id, long id_initiator) {
		Message msg = new Message(host.getID(), -1, MessageType.flooding, null, gossip_pid); // tag == flooding
		((EmitterImplF) host.getProtocol(emitterflooding_pid)).emit(host, msg); // emit
		//firstTime = false;
		((GossipProtocolImpl) Network.get((int)id_initiator).getProtocol(gossip_pid)).setFirstTime(false); // emit
		System.out.println(host.getID() + " inite la diffusion ");
		
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		if (event instanceof Message) {
			if (((Message) event).getTag() == MessageType.flooding) {
				Message m = (Message) event;
				if (firstTime) {
					firstTime = false;
					((EmitterImplF) host.getProtocol(emitterflooding_pid)).decrementN(1);
					((EmitterImplF) host.getProtocol(emitterflooding_pid)).emit(host, m);
				} else {
					((EmitterImplF) host.getProtocol(emitterflooding_pid)).decrementN(1);
					System.out.println(host.getID() + " decremente");
				}
			} else {
				System.out.println("ERROR");
			}
		}
	}
}
