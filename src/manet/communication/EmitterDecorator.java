package manet.communication;


import manet.Message;
import manet.positioning.PositionProtocol;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import utils.MessageType;

public class EmitterDecorator extends EmitterImpl implements EDProtocol {

	private static final String PAR_POSITIONPID = "positionprotocol";
	private static final String PAR_GOSSIPPID = "gossipprotocol";

	private final int gossip_pid;
	private final int position_pid;

	private static Integer N;

	public EmitterDecorator(String prefix) {
		super(prefix);
		position_pid = Configuration.getPid(prefix + "." + PAR_POSITIONPID);
		gossip_pid = Configuration.getPid(prefix + "." + PAR_GOSSIPPID);
		N = 0;
	}

	public EmitterDecorator clone() {
		return (EmitterDecorator) super.clone();
	}

	@Override
	public int getLatency() {
		return super.getLatency();
	}

	@Override
	public int getScope() {
		return super.getScope();
	}

	@Override
	public void emit(Node host, Message msg) {

		Message newMsg = new Message(msg.getIdSrc(), msg.getIdDest(), msg.getTag(), null, msg.getPid());
		super.emit(host, newMsg);
		N += nbNeighbors(host);

	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		if (event instanceof Message) {
			Message msg = ((Message) event);
			Message newMsg = null;
			switch(msg.getTag()) {

			case MessageType.decrement :
				N--;
				break;

			case MessageType.flooding : 
				newMsg = new Message(msg.getIdSrc(), msg.getIdSrc(), msg.getTag(), null, msg.getPid());
				break;

			case MessageType.flooding_algo3 :
				int v = nbNeighbors(host);
				newMsg = new Message(msg.getIdSrc(), msg.getIdSrc(), msg.getTag(), v, msg.getPid());
				break;

			case MessageType.flooding_algo4 :
				newMsg = new Message(msg.getIdSrc(), msg.getIdSrc(), msg.getTag(), calculateDistance(host,msg.getIdSrc()), msg.getPid());
				break;

			default :
				System.out.println("ERROR");
				break;
			}
			EDSimulator.add(0, newMsg, host, gossip_pid);
		} else {

		}

	}

	private double calculateDistance(Node host, long idSrc) {
		PositionProtocol hostPos = null;
		PositionProtocol nodePos = null;
		Node node = null;
		node = Network.get((int)idSrc);
		hostPos = (PositionProtocol) host.getProtocol(position_pid);
		nodePos = (PositionProtocol) node.getProtocol(position_pid);
		return hostPos.getCurrentPosition().distance(nodePos.getCurrentPosition())/(double)getScope();

	}

	private int nbNeighbors(Node host) {
		PositionProtocol hostPos = null;
		PositionProtocol nodePos = null;
		Node node = null;
		int cpt = 0;
		for (int i = 0; i < Network.size(); i++) { // For all network nodes
			node = Network.get(i);
			if (host.getID() != node.getID()) { // Except me
				hostPos = (PositionProtocol) host.getProtocol(position_pid);
				nodePos = (PositionProtocol) node.getProtocol(position_pid);
				if ((hostPos.getCurrentPosition().distance(nodePos.getCurrentPosition()) <= this.getScope())) { // In my
					// scope
					cpt++;
				}
			}
		}
		return cpt;
	}


	public void decrementN(int n) {
		N -= n;
	}

	public int getN() {
		return N;
	}

}
