package manet;

import java.util.ArrayList;

import peersim.config.Configuration;
import peersim.core.*;

public class DensityControler implements Control{
	
	private static final String PAR_EXECT = "EXECT";
	private static final String PAR_NEIGHBORPID = "neighborprot";
	
	private double timeExec;
	private int neighborpid;
	
	
	public DensityControler(String prefix) {
		//timeExec=Configuration.getDouble(prefix+"."+PAR_EXECT);
		neighborpid = Configuration.getPid(prefix+"."+PAR_NEIGHBORPID);
	}
	
	
	@Override
	public boolean execute() {
		
		
		for(int i=0;i<Network.size();i++) {
			DetecterVoisinsQ6 v = (DetecterVoisinsQ6)Network.get(i).getProtocol(neighborpid);
			ArrayList<Long> l = (ArrayList<Long>) v.getNeighbors();
			System.out.println("Les voisins de "+i+" sont");
			for(int j=0;j<l.size();j++) {
				System.out.print(l.get(j)+" ");
			}
			System.out.println();
		}
		
		return false;
	}
	
}
