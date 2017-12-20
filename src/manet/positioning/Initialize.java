package manet.positioning;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

public class Initialize implements Control {

	private static final String PAR_POSITIONPID = "positionprotocol";

	private final int position_pid;
	private Node n = null;
	private PositionProtocol p = null;

	public Initialize(String prefix) {
		position_pid = Configuration.getPid(prefix + "." + PAR_POSITIONPID);
	}

	@Override
	public boolean execute() {
		for (int i = 0; i < Network.size(); i++) {
			n = Network.get(i);
			p = (PositionProtocol) n.getProtocol(position_pid);
			p.initialiseCurrentPosition(Network.get(i));
			EDSimulator.add(1, PositionProtocolImpl.loop_event, n, position_pid);
		}
		return false;
	}

}
