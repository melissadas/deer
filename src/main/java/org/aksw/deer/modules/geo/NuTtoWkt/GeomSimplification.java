package org.aksw.deer.modules.geo.NuTtoWkt;

import java.util.ArrayList;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

public class GeomSimplification {



	//static ArrayList<String>strings= new ArrayList<String>();

	static ArrayList<ArrayList<String>> allReducedGeom= new  ArrayList<ArrayList<String>>();
	static ArrayList<String> reducedGeom =new 	ArrayList<String>(); 


	static ArrayList<Geometry> pL= new ArrayList<Geometry>();
	static ArrayList<Geometry> geom= new ArrayList<Geometry>();


	private static double d1=0.09;
	private static double d2=0.1;
	private static double d3=0.11;


	static ArrayList<Double> allD=new ArrayList<Double>();

	public static ArrayList<ArrayList<String>> geomSimpilfication(ArrayList<String>strings) throws ParseException{


		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );

		allD.add(d1);
		allD.add(d2);
		allD.add(d3);

		for(int j=0;j<allD.size();j++){

			for(int i=0;i<strings.size();i++) {


				Geometry pLtemp= (Polygon) reader.read(strings.get(i));
				pL.add(pLtemp);


				//System.out.println("P"+i+" is : "+ pL.get(i));


				//double distanceTolerance = 0;
				Geometry geomTemp=	DouglasPeuckerSimplifier.simplify(pL.get(i), allD.get(j));
				geom.add(geomTemp);
				String str=geom.get(i).toString();
				reducedGeom.add(str);
				//System.out.println("geom"+i+" is : "+ geom.get(i));

			}
			allReducedGeom.add(reducedGeom);

			// TODO Auto-generated method stub

		}
		return allReducedGeom;}


}
