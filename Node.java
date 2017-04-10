package distcomp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Node {
	
	private int node_id;
	private double x_coordinate;
	private double y_coordinate;
	ArrayList<Node> k_neighbors = new ArrayList<Node>();

	public Node(int id, double x, double y){
		this.node_id = id;
		this.x_coordinate = x;
		this.y_coordinate = y;
	}
	
	public int get_id(){
		return node_id;
	}
	
	public double get_X(){
		return x_coordinate;
	}
	
	public void set_X(double x){
		this.x_coordinate = x;
	}
	
	public double get_Y(){
		return y_coordinate;
	}
	
	public void set_Y(double y){
		this.y_coordinate = y;
	}
	
	public void add_neighbor(Node n){
		k_neighbors.add(n);
	}
	
	public Node pickNeighbor(int k){
		Random ran = new Random();
		int n;
		n = ran.nextInt(k);
		return k_neighbors.get(n);
	}
	
	public ArrayList<Node> getList(){
		return k_neighbors;
	}
	
	public void rearrange(ArrayList<Node> List, int k, String topology){
		ArrayList<Node> merged_list = new ArrayList<Node>();
		merged_list.addAll(List);
		merged_list.addAll(k_neighbors);
		
		Iterator iter = merged_list.iterator();
		HashMap<Node,Double> map = new HashMap<Node,Double>();
		while(iter.hasNext()){
			Node nd = (Node) iter.next();
			if(this!=nd){
			double dist = getDistance(this,nd,topology);
			map.put(nd, dist);
			}
		}
		
		Map<Node, Double> sorted_map = sortByValue(map);
		Iterator<Node> itr = sorted_map.keySet().iterator();
		int count = 1;
		ArrayList<Node> new_neighbors = new ArrayList<Node>();
		while(itr.hasNext() && count<=k){
			new_neighbors.add((Node) itr.next());
			count++;
		}
		
		this.updateNeighbors(new_neighbors);
		
	}
	
	public void updateNeighbors(ArrayList<Node> nlist) {
		this.k_neighbors.clear();
		k_neighbors.addAll(nlist);
		
	}

	//Method to sort map by values
	
	public static <K, V extends Comparable<? super V>> Map<K, V> 
    sortByValue( Map<K, V> map )
	{
	    List<Map.Entry<K, V>> list =
	        new LinkedList<Map.Entry<K, V>>( map.entrySet() );
	    Collections.sort( list, new Comparator<Map.Entry<K, V>>()
	    {
	        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
	        {
	            return (o1.getValue()).compareTo( o2.getValue() );
	        }
	    } );
	
	    Map<K, V> result = new LinkedHashMap<K, V>();
	    for (Map.Entry<K, V> entry : list)
	    {
	        result.put( entry.getKey(), entry.getValue() );
	    }
	    return result;
	}

	//Method to calculate distance between two nodes
	
	public static double getDistance(Node n1, Node n2, String topology) {
		
		double result = 0;
		switch(topology){
		
		case "D" :
		case "C" :
			double x1 = n1.get_X();
			double y1 = n1.get_Y();
			double x2 = n2.get_X();
			double y2 = n2.get_Y();
			
			result = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
			break;
			
		case "B" :
			int a = n1.get_id();
			int b = n2.get_id();
			int bits = 10;
			int alevel=bits;
			int blevel=bits;
			int commonprefix=0;
			int mask = 1 << bits-1;
			
			// find the level of node a
			while( (mask & a) == 0 )
			{
				a <<= 1;
				alevel--;
			}
			
			// find the level of node b
			while( (mask & b) == 0 )
			{
				b <<= 1;
				blevel--;
			}
			
			int length = Math.min(alevel,blevel);
			while( (mask & ~(a ^ b)) != 0 && length>0)
			{
				b <<= 1;
				a <<= 1;
				commonprefix++;
				length--;
			}
			result = alevel - commonprefix + blevel - commonprefix;
			
		}
		
		return result;
	}
	
	
}
