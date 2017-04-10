package distcomp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Export {
	
	Export(ArrayList<Node> list, String topology, int N, int k, int cycle){
		Iterator<Node> it =list.iterator();
		XYSeriesCollection dataset = new XYSeriesCollection();

		ArrayList<XYSeries> series_list = new ArrayList<XYSeries>();
		while(it.hasNext()){
			Node n = (Node)it.next();
			XYDataItem myXY = new XYDataItem(n.get_X(),n.get_Y());
			
			Iterator<Node> i = n.getList().iterator();
			while(i.hasNext()){
				Node nd = (Node)i.next();
				XYSeries series = new XYSeries("",false);
				series.add(myXY);
				series.add(new XYDataItem(nd.get_X(),nd.get_Y()));
				series_list.add(series);
			}
		}
		
		//System.out.println(series_list.size());
		Iterator<XYSeries> irt = series_list.iterator();
		while(irt.hasNext()){
			dataset.addSeries((XYSeries) irt.next());
		}
		
		JFreeChart xylineChart = ChartFactory.createXYLineChart(
		         topology, 
		         "X",
		         "Y", 
		         dataset,
		         PlotOrientation.VERTICAL, 
		         false, true, false);
		      
		      int width = 640; /* Width of the image */
		      int height = 480; /* Height of the image */ 
		      String filename = topology + "_N" + Integer.toString(N) + "_k" + Integer.toString(k) +"_" + Integer.toString(cycle);
		      File XYChart = new File( filename + ".jpeg" ); 
		      try {
				ChartUtilities.saveChartAsJPEG( XYChart, xylineChart, width, height);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
}


