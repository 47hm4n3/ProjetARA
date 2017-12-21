package manet.communication;

import manet.Message;
import manet.positioning.PositionProtocol;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

public class EmitterImpl implements Emitter {

	private static final String PAR_POSITIONPID = "positionprotocol";
	private static final String PAR_LATENCY = "latency";
	private static final String PAR_SCOPE = "scope";
	
	
	private final int position_pid;
	private final int latency;
	private final int scope;
	
	private PositionProtocol hostPos = null;
	private PositionProtocol destPos = null;
	private double currX = 0;
	private double currY = 0;
	private double destX = 0;
	private double destY = 0;
	private Node node = null;

	public EmitterImpl(String prefix) {
		position_pid = Configuration.getPid(prefix + "." + PAR_POSITIONPID);
		latency = Configuration.getInt(prefix + "." + PAR_LATENCY);
		scope = Configuration.getInt(prefix + "." + PAR_SCOPE);
	}

	@Override
	public void emit(Node host, Message msg) {

		for (int i = 0; i < Network.getCapacity(); i++) { // For all network nodes
			node = Network.get(i);
			if (host.getID() != node.getID()) { // Except me
				if (host.getID() == msg.getIdDest()) { // I am the recipient of the received message
					currX = hostPos.getCurrentPosition().getX();
					currY = hostPos.getCurrentPosition().getY();
					destX = destPos.getCurrentPosition().getX();
					destY = destPos.getCurrentPosition().getY();
					if (!((Math.pow(destX - currX,2) + Math.pow(destY - currY,2)) > Math.pow(this.getScope(), 2))) { // Recipient is in my scope
						hostPos = (PositionProtocol) host.getProtocol(position_pid);
						destPos = (PositionProtocol) node.getProtocol(position_pid);
						System.out.println("sending");
						EDSimulator.add(this.getLatency(), msg, node, position_pid); // Send()
					}
				}
			}
		}
	}

	@Override
	public int getLatency() {
		//return this.getLatency();
		return this.latency;
	}

	@Override
	public int getScope() {
		//return this.getScope();
		return this.scope;
	}

	public EmitterImpl clone() {
		try {
			return (EmitterImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning Communicate Failed !");
		}
		return null;
	}

}