package org.aksw.deer.modules.geo.NuTtoWkt;

import java.util.ArrayList;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

public class GeomSimplification {


	public static ArrayList<String> geomSimpilfication(ArrayList<String>strings, double distanceTolerance) throws ParseException{

		ArrayList<String> simpilfiedGeom =new 	ArrayList<String>(); 
		ArrayList<Geometry> pLs= new ArrayList<Geometry>();
		ArrayList<Geometry> geom= new ArrayList<Geometry>();

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );


		for(int i=0;i<strings.size();i++) {

			Geometry pLtemp= (Polygon) reader.read(strings.get(i));
			pLs.add(pLtemp);

			Geometry geomTemp=	TopologyPreservingSimplifier.simplify(pLs.get(i),distanceTolerance);
			geom.add(geomTemp);
			String str=geom.get(i).toString();
			simpilfiedGeom.add(str);
		}

		return simpilfiedGeom;
	}

	public static ArrayList<String> geomMean(ArrayList<String>strings) throws ParseException{

		ArrayList<Geometry> pL= new ArrayList<Geometry>();
		ArrayList<String> strs=new ArrayList<String>();

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );

		for(int i=0;i<strings.size();i++) {

			Geometry pLtemp= (Polygon) reader.read(strings.get(i));
			pL.add(pLtemp);
			String str=pL.get(i).getCentroid().toString();
			strs.add(str);
		}

		return strs;
	}


}
