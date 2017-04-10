package distcomp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartUtilities; 


public class TMAN {
	
	public static void main(String args[]){
		
		//Assigning passed arguments from main
		
		int N = Integer.parseInt(args[0]);
		int k = Integer.parseInt(args[1]);
		String topology = args[2];
		ArrayList<Integer> radii = new ArrayList<Integer>();
		
		//Array of radii for Dynamic ring topology
		
		if(args.length>3){
			String radii_input = args[4];
			String[] radii_comma = radii_input.split(",");
			int num_of_radii = Integer.parseInt(args[3]);
			for(int i=0;i<num_of_radii;i++){
				radii.add(Integer.parseInt(radii_comma[i]));
			}
			
		}
		
		//Running specific methods
		
		switch(topology){
		
		case "D" :
			ExecuteRing(N,k,radii,topology);
			break;
			
		case "B" :
			ExecuteTree(N,k,topology);
			break;
			
		case "C" :
			ExecuteCresent(N,k,topology);
			break;
		
		}
		
	}
	
/***************************************************End of Main********************************************************/	
	
/***********************************************Dynamic Ring topology**************************************************/
	
	public static void ExecuteRing(int N, int k, ArrayList<Integer> radii, String topology){
	
		//Nodes list
		ArrayList<Node> nodes_list = new ArrayList<Node>();
		
		int current_radius =radii.get(0);
		int num_of_nodes = N;
		int num_of_neighbors = k;
		double angle_diff = (double)360/num_of_nodes;
		double angle = 0;
		
		//Creating the nodes
		
		for(int i=0;i<num_of_nodes;i++){
			
			double x_value = Math.cos(angle)*current_radius;
			double y_value = Math.sin(angle)*current_radius;
			nodes_list.add(new Node(i, x_value, y_value));
			angle+=angle_diff;
			
		}
		
		//Network initialization
		
		Iterator<Node> iter = nodes_list.iterator();
		Random rand = new Random();
		HashSet<Integer> set = new HashSet<Integer>();
		while(iter.hasNext()){
			
			Node n = (Node) iter.next();
			
			//picking k random neighbors
			
			while(set.size()<num_of_neighbors){
				int r = rand.nextInt(num_of_nodes);
				set.add(r);
				if(r == n.get_id())
					set.remove(r);	
			}
			
			Iterator<Integer> it = set.iterator();
			while(it.hasNext()){
				n.add_neighbor(nodes_list.get((int) it.next()));
			}
			set.clear();
			
		}
		
		//Network Evolution
		
		int target_radius = 0;
		int radius_counter = 2; //random value
		
		
		
		//Cycles
		for(int i=0;i<50;i++){	
			//Rereading the radius value
			if(i%5 == 0 && i/5<radii.size()){
				target_radius = radii.get(i/5);
			}
			
			if(i==8){
				radius_counter=0;
			}
			
			//Incrementing the radius by 1
			if(radius_counter%3 == 0 && current_radius<target_radius){
				current_radius++;
				//Updating coordinate values
				updateRingCoordinates(nodes_list,current_radius,angle_diff);
			}
			
			if(i>=8)
				radius_counter++;
			
			Iterator<Node> it = nodes_list.iterator();
			while(it.hasNext()){
				Node node = (Node) it.next();
				Node neighbor_node = node.pickNeighbor(num_of_neighbors);
				node.rearrange(neighbor_node.getList(),num_of_neighbors,topology);
				neighbor_node.rearrange(node.getList(),num_of_neighbors,topology);
			}
			
			WriteSumDistances(nodes_list,i,N,k,topology);
			
			if(i==0 || i==4 || i==9 || i==14 || i==49){
				new Export(nodes_list,topology,N,k,i+1);
				WriteNeigbors(nodes_list,i+1,N,k,topology);
			}
			
		}
		
		
		
	}
	
	public static void WriteNeigbors(ArrayList<Node> nodes_list, int i, int N, int k, String topology) {
		
		String merged ="";
		Iterator itrt = nodes_list.iterator();
		while(itrt.hasNext()){
			Node n = (Node)itrt.next();
			String n1 = Integer.toString(n.get_id());
			Iterator ir = n.getList().iterator();
			merged+= n1 + " --> ";
			ArrayList<Integer> neighbors = new ArrayList<Integer>();
			while(ir.hasNext()){
				Node nd = (Node)ir.next();
				neighbors.add(nd.get_id());
				Collections.sort(neighbors);	
			}
			String n2="";
			Iterator itrtr = neighbors.iterator();
			while(itrtr.hasNext()){
				n2+=itrtr.next();
				n2+=",";
			}
			merged+=n2;
			merged+="\r\n";
		}
		
		String filename = topology + "_N" + Integer.toString(N) + "_k" + Integer.toString(k) +"_" + i;
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename+".txt", true)))) {
		    out.println(merged);
		    
		}catch (IOException e) {
		    System.err.println(e);
		}

	
}

	public static void WriteSumDistances(ArrayList<Node> nodes_list, int i, int N, int k, String topology) {
	
		double dist =0;
		Iterator it = nodes_list.iterator();
		while(it.hasNext()){
			Node n = (Node) it.next();
			Iterator ir = n.getList().iterator();
			while(ir.hasNext()){
				Node nd = (Node)ir.next();
				dist+= Node.getDistance(n,nd,topology);
			}
		}
		
		String filename = topology + "_N" + Integer.toString(N) + "_k" + Integer.toString(k);
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename+".txt", true)))) {
		    out.println("cycle " + i + ": " + dist);
		    out.println();
		}catch (IOException e) {
		    System.err.println(e);
		}
	
}

	

	public static void updateRingCoordinates(ArrayList<Node> nodes_list, int current_radius, double angle_diff) {
		double angle =0;
		Iterator irtr = nodes_list.iterator();
		while(irtr.hasNext()){
			Node node = (Node) irtr.next();
			node.set_X(Math.cos(angle)*current_radius);
			node.set_Y(Math.sin(angle)*current_radius);
			angle+=angle_diff;
		}
		
	}

/****************************************End of Dynamic ring topology****************************************************/

/*******************************************Binary Tree Topology*********************************************************/
	
	public static void ExecuteTree(int N, int k, String topology){
		
		ArrayList<Node> tree_nodes = new ArrayList<Node>();
		
		//Creating the tree nodes
		
		for(int i=1;i<=N;i++){
			if(i==1){
				Node t = new Node(i,0,10);
				tree_nodes.add(t);
			}
			else if(i%2==0){
				Node tn = new Node(i,tree_nodes.get(i/2-1).get_X()-0.1,10-Math.floor((Math.log10(i)/Math.log10(2))));
				tree_nodes.add(tn);
			}
			else{
				Node tn = new Node(i,tree_nodes.get(i/2-1).get_X()+0.1,10-Math.floor((Math.log10(i)/Math.log10(2))));
				tree_nodes.add(tn);
			}
			
		}
		
		//Network initialization
		
		Iterator iter = tree_nodes.iterator();
		Random rand = new Random();
		HashSet<Integer> set = new HashSet<Integer>();
		while(iter.hasNext()){
			
			Node n = (Node) iter.next();
			
			//picking k random neighbors
			
			while(set.size()<k){
				int r = rand.nextInt(N)+1;
				set.add(r);
				if(r == n.get_id())
					set.remove(r);	
			}
			
			Iterator it = set.iterator();
			while(it.hasNext()){
				n.add_neighbor(tree_nodes.get((int) it.next()-1));
			}
			set.clear();
		}
		
		//Network evolution
		
		//Cycles
		for(int i=0;i<50;i++){
			Iterator it = tree_nodes.iterator();
			while(it.hasNext()){
				Node node = (Node) it.next();
				Node neighbor_node = node.pickNeighbor(k);
				ArrayList<Node> my_neighbors = node.getList();
				ArrayList<Node> your_neighbors = neighbor_node.getList();
				node.rearrange(your_neighbors,k,topology);
				neighbor_node.rearrange(my_neighbors,k,topology);
			}
			
			WriteSumDistances(tree_nodes,i,N,k,topology);
			
			if(i==0 || i==4 || i==9 || i==14 || i==49){
				new Export(tree_nodes,topology,N,k,i+1);
				WriteNeigbors(tree_nodes,i+1,N,k,topology);
			}
		}
		
	}
	
/***********************************************End of Binary tree topology**********************************************/
	
/*************************************************Cresent moon topology**************************************************/
	
	public static void ExecuteCresent(int N, int k, String topology){
		
		//Nodes list
				ArrayList<Node> moon_nodes = new ArrayList<Node>();
				
				double r1 =2*Math.sqrt(3);
				double r2 = 4;
				int num_of_nodes = N;
				int num_of_neighbors = k;
				double angle_diff = (double)360/num_of_nodes;
				double angle = 0;
				
				//Creating the nodes
				
				int j=0;
				while(moon_nodes.size()<N){
					
					double x1 = Math.cos(angle)*r1;
					double y1 = Math.sin(angle)*r1;
					double x2 = Math.cos(angle)*r2-2;
					double y2 = Math.sin(angle)*r2;	
					if(x1<=0){
						moon_nodes.add(new Node(j, x1, y1));
						j++;
					}
					if(x2<=0){
						moon_nodes.add(new Node(j, x2, y2));
						j++;
					}
					
					angle+=angle_diff;
					
				}
				
				//Network initialization
				
				Iterator<Node> iter = moon_nodes.iterator();
				Random rand = new Random();
				HashSet<Integer> set = new HashSet<Integer>();
				while(iter.hasNext()){
					
					Node n = (Node) iter.next();
					
					//picking k random neighbors
					
					while(set.size()<k){
						int r = rand.nextInt(N)+1;
						set.add(r);
						if(r == n.get_id())
							set.remove(r);	
					}
					
					Iterator<Integer> it = set.iterator();
					while(it.hasNext()){
						n.add_neighbor(moon_nodes.get((int) it.next()-1));
					}
					set.clear();
				}
				
				//Network evolution
				
				//Cycles
				for(int i=0;i<50;i++){
					Iterator it = moon_nodes.iterator();
					while(it.hasNext()){
						Node node = (Node) it.next();
						Node neighbor_node = node.pickNeighbor(k);
						ArrayList<Node> my_neighbors = node.getList();
						ArrayList<Node> your_neighbors = neighbor_node.getList();
						node.rearrange(your_neighbors,k,topology);
						neighbor_node.rearrange(my_neighbors,k,topology);
					}
					
					WriteSumDistances(moon_nodes,i,N,k,topology);
					
					if(i==0 || i==4 || i==9 || i==14 || i==49){
						new Export(moon_nodes,topology,N,k,i+1);
						WriteNeigbors(moon_nodes,i+1,N,k,topology);
					}
					
				}

	}

	
	
/************************************************End of cresent moon topology********************************************/
	
}

/*********************************************End of TMAN class**********************************************************/

