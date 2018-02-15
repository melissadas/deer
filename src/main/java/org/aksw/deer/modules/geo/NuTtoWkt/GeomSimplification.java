package org.aksw.deer.modules.geo.NuTtoWkt;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

public class GeomSimplification {


	public static String geomSimpilfication(String string, double distanceTolerance) throws ParseException{

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );
		Geometry pLtemp= (Polygon) reader.read(string);
		System.out.println("Nrs of Polygon's Points before simpilfication= "+ pLtemp.getNumPoints());
		Geometry geomTemp=	TopologyPreservingSimplifier.simplify(pLtemp,distanceTolerance);
		System.out.println("Nrs of Polygon's Points after simpilfication= "+ geomTemp.getNumPoints());
		String str= geomTemp.toString();

		return str;
	}

	public static String geomMean(String string) throws ParseException{

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );
		Geometry pLtemp= (Polygon) reader.read(string);

		String str=pLtemp.getCentroid().toString();
		System.out.println("the mean is "+str);
		return str;
	}


}
