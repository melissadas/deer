/**
 *
 */
package org.aksw.deer.workflow;

import java.io.IOException;
import org.aksw.deer.helper.datastructure.RunContext;
import org.aksw.deer.server.Server;
import org.aksw.deer.workflow.rdfspecs.RDFConfigExecutor;
import org.apache.log4j.Logger;


/**
 * @author sherif
 */
public class DeerController {

  private static final Logger logger = Logger.getLogger(DeerController.class);
  private static final String HELP_MESSAGE =
    "To run DEER from command-line, provide the RDf configuration file as " +
      "the only one parameter for the DEER jar file. \n" +
      "Example: deer.jar config.ttl \n" +
      "For details about the configuration file see DEER manual at " +
      "https://github.com/GeoKnow/DEER/blob/master/DEER_manual/deer_manual.pdf ";


  /**
   * @author sherif
   */
  public DeerController() {
  }

//	/**
//	 * run GeoLift through command line
//	 * @param args
//	 * @throws IOException
//	 * @author sherif
//	 */
//	public static void runDeer(String args[]) throws IOException{
//		long startTime = System.currentTimeMillis();
//		String inputFile	= "";
//		String configFile	= "";
//		String outputFile	= "";
//
//		for(int i=0; i<args.length; i+=2){
//			if(args[i].equals("-i") || args[i].toLowerCase().equals("--input")){
//				inputFile = args[i+1];
//			}
//			if(args[i].equals("-c") || args[i].toLowerCase().equals("--config")){
//				configFile = args[i+1];
//			}
//			if(args[i].equals("-o") || args[i].toLowerCase().equals("--output")){
//				outputFile = args[i+1];
//			}
//		} 
//
//		Model startModel =  Reader.readModel(inputFile);
//		Multimap<String, Map<String, String>> parameters = HashMultimap.create();
//		if(configFile.toLowerCase().endsWith(".csv") || configFile.toLowerCase().endsWith(".tsv")){
//			parameters = TSVConfigReader.getParameters(configFile);
//		} else { // read RDF config file
//			parameters = RDFConfigReader.getParameters(configFile);
//		}
//		WorkflowHandler wfh = new WorkflowHandler(startModel, parameters);
//
//		if(!outputFile.equals("")){
//			wfh.getEnrichedModel().write(new FileWriter(outputFile), "TTL");
//		}else{
//			wfh.getEnrichedModel().write(System.out, "TTL");
//		}
//		Long totalTime = System.currentTimeMillis() - startTime;
//		logger.info("***** Done in " + totalTime + "ms *****");
//	}

  public static void run(String args[]) throws IOException {
    if (args.length == 0 || args[0].equals("-?") || args[0].toLowerCase().equals("--help")) {
      //show help message
      logger.info(DeerController.HELP_MESSAGE);
      System.exit(0);
    }
    if (args[0].equals("-l") || args[0].toLowerCase().equals("--list")) {
      org.aksw.deer.json.JSONConfigWriter.write();
      System.exit(0);
    }
    if (args[0].equals("-s") || args[0].toLowerCase().equals("--server")) {
      Server.main(args);
      System.out.println("Starting DEER server - press enter to stop server");
      System.in.read();
      System.out.println("Stopping DEER server...");
      System.exit(0);
    }
    //program didn't terminate until here so run RDF config mode
    long startTime = System.currentTimeMillis();
    RDFConfigExecutor executor = new RDFConfigExecutor(args[0], new RunContext(0, ""));
    executor.execute();
    Long totalTime = System.currentTimeMillis() - startTime;
    logger.info("Running DEER Done in " + totalTime + "ms");
  }

  /**
   * @author sherif
   */
  public static void main(String args[]) throws IOException {
    run(args);
  }
}
