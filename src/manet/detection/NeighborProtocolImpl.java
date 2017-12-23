package manet.detection;

import java.util.ArrayList;
import java.util.List;

import manet.Message;
import manet.communication.EmitterImpl;
import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import utils.MessageType;

public class NeighborProtocolImpl implements NeighborProtocol, EDProtocol {

	private static final String PAR_EMITTERPID = "emitterprotocol";
	private static final String PAR_PERIOD = "period";
	private static final String PAR_DELTA = "delta";

	private final int neighbour_pid;
	private final int emitter_pid;
	private final long period;
	private final long delta;
	private List<Long> neighbours;
	private List<Long> periodN;
	private Message msg;
	
	public NeighborProtocolImpl(String prefix) {
		String tmp[] = prefix.split("\\.");
		neighbour_pid = Configuration.lookupPid(tmp[tmp.length - 1]);
		emitter_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
		delta = Configuration.getInt(prefix + "." + PAR_DELTA);
		period = Configuration.getInt(prefix + "." + PAR_PERIOD);
	}

	@Override
	public List<Long> getNeighbors() {
		return this.neighbours;
	}

	public long getPeriod() {
		return this.period;
	}

	public long getDelta() {
		return this.delta;
	}

	public NeighborProtocolImpl clone() {
		try {
			neighbours = new ArrayList<Long>();
			periodN = new ArrayList<Long>();
			return (NeighborProtocolImpl) super.clone();
		} catch (Exception e) {
			System.out.println("Cloning NeighborProtocolImpl Failed !");
		}
		return null;
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		if (neighbour_pid != pid) {
			throw new RuntimeException("Receive Event for wrong protocol");
		}
		if (event instanceof String) {
			msg = new Message(host.getID(), -1, "", MessageType.probe, neighbour_pid);
			switch ((String) event) {
			case MessageType.firstprobe:
				((EmitterImpl) host.getProtocol(emitter_pid)).emit(host, msg) ;
				EDSimulator.add(period, MessageType.probe, host, neighbour_pid);
				break;
			case MessageType.probe:
				((EmitterImpl) host.getProtocol(emitter_pid)).emit(host, msg);
				EDSimulator.add(period, MessageType.probe, host, neighbour_pid);
				break;
			case MessageType.timer:
				for (int i = 0; i < neighbours.size(); i++) {
					if (!periodN.contains(neighbours.get(i))) {
						neighbours.remove(i);
					}
				}
				periodN.clear();
				EDSimulator.add(delta, MessageType.timer, host, neighbour_pid);
				break;
			default:
				break;
			}
		} else if (event instanceof Message) {
			long id = ((Message) event).getIdSrc();
			if (!periodN.contains(id)) {
				periodN.add(id);
			}
			if (!neighbours.contains(id)) {
				neighbours.add(id);
			}
			
		} else {
			throw new RuntimeException("Received Event of unmatching type");
		}
	}

}
