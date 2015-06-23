package eu.uqasar.cubes.adapter;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import us.monoid.json.JSONException;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;
import eu.uqasar.adapter.SystemAdapter;
import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.BindedSystem;
import eu.uqasar.adapter.model.Measurement;
import eu.uqasar.adapter.model.User;
import eu.uqasar.adapter.model.uQasarMetric;
import eu.uqasar.adapter.query.QueryExpression;

/**
 * 
 * Manu Garcia <mgarcia@innopole.net>
 * 
 *  Cubes Adapter
 * 
 * Based on https://github.com/IntrasoftInternational/JiraAdapter
 * Implementing interface https://github.com/IntrasoftInternational/uQasarAdapter
 * 
 */

public class CubesAdapter implements SystemAdapter {

	// Counts the number of trials to connect to Cubes server 
	private int counter = 0;
	// Limits counts to 10
	private int counterLimit = 10;
	
	// Main constructor
    public CubesAdapter() {
    }

    @Override
    public List<Measurement> query(BindedSystem bindedSystem, User user, QueryExpression queryExpression) throws uQasarException {
    	// Number of elements that match the proposed query 
    	int  value = 0; 
    	// Collection of measurements
        LinkedList<Measurement> measurements = new LinkedList<Measurement>();
        // Retrieved JSON  
        JSONResource jsonResource = null;
        // Single measure
        String measurement = "";
        String url = "";
        
        
        CubesQueryExpresion cubesQueryExpresion = (CubesQueryExpresion) queryExpression;

        try {
        	final URI uri = new URI(bindedSystem.getUri());
        	url = uri.toString();
        	
        	// check if the provided URL ends with "/"
            if(!url.endsWith("/")){
            	url += "/";
            }        	
            
            // GET query to REST service is stored as JSONResource
            jsonResource = getJSON(url + cubesQueryExpresion.getQuery());
            
            /* Parse the JSON resource */
            if (cubesQueryExpresion.getQueryType() == uQasarMetric.AGGREGATE){			//JSONObject
				// Parse to get ALL the Object
				measurement = jsonResource.toObject().toString();
				// Parse to get the value of the "obj" within the "member"
				value = Integer.valueOf(jsonResource.toObject().getJSONObject("summary").getString("count"));
            	
            }   else if (cubesQueryExpresion.getQueryType() == uQasarMetric.FACT ||		//JSONObject
            		cubesQueryExpresion.getQueryType() == uQasarMetric.MEMBERS|| 
            		cubesQueryExpresion.getQueryType() == uQasarMetric.MODEL ||
            		cubesQueryExpresion.getQueryType() == uQasarMetric.CELL){
            	// Parse to get ALL the Object
				measurement = jsonResource.toObject().toString();
				
            } 	else if (cubesQueryExpresion.getQueryType() == uQasarMetric.FACTS || 
            		cubesQueryExpresion.getQueryType() == uQasarMetric.CUBES){      	//JSONArray
            	// Stores the JSON Array 
				measurement = jsonResource.array().toString();
				// Count the array elements and set value  
				value = jsonResource.array().length();
				
            } else {
            	throw new uQasarException(uQasarException.UQasarExceptionType.UQASAR_NOT_EXISTING_METRIC,cubesQueryExpresion.getQuery());
            }

            if(measurement != null){
            	 final JSONArray measurementResultJSONArray = new JSONArray();
            	 final JSONObject bp = new JSONObject();
            	 bp.put("self",url + cubesQueryExpresion.getQuery() );
            	 bp.put("key",cubesQueryExpresion.getQueryType() );
            	 bp.put("name",cubesQueryExpresion.getCubeName());
            	 bp.put("jsonContent", measurement);
            	 bp.put("value", String.valueOf(value));
            	 measurementResultJSONArray.put(bp);

            	 // Retrieved measurement is stored in List 
        		measurements.add(new Measurement(cubesQueryExpresion.getQueryType(), measurementResultJSONArray.toString()));
            }
		
        } catch (URISyntaxException e) {
            throw new uQasarException(uQasarException.UQasarExceptionType.BINDING_SYSTEM_BAD_URI_SYNTAX,bindedSystem,e.getCause());
        }  catch (RuntimeException e){
            throw new uQasarException(uQasarException.UQasarExceptionType.BINDING_SYSTEM_CONNECTION_REFUSED,bindedSystem,e.getCause());
        }  catch (IOException e){
            throw new uQasarException(uQasarException.UQasarExceptionType.BINDING_SYSTEM_CONNECTION_REFUSED,bindedSystem,e.getCause());
        } catch (org.codehaus.jettison.json.JSONException e) {
			throw new uQasarException(uQasarException.UQasarExceptionType.ERROR_PARSING_JSON,bindedSystem,e.getCause());
        } catch( JSONException e) {
			throw new uQasarException(uQasarException.UQasarExceptionType.ERROR_PARSING_JSON,bindedSystem,e.getCause());
		} 

        return measurements;
    }

    
    @Override
    public List<Measurement> query(String bindedSystemURL, String credentials, String queryExpression) throws uQasarException {

        final BindedSystem bindedSystem = new BindedSystem();
        bindedSystem.setUri(bindedSystemURL);
        final User user = new User();

        final String[] creds = credentials.split(":");

        user.setUsername(creds[0]);
        user.setPassword(creds[1]);

        final CubesQueryExpresion cubesQueryExpresion = new CubesQueryExpresion(queryExpression);
        final CubesAdapter cubesAdapter = new CubesAdapter();

        return cubesAdapter.query(bindedSystem,user,cubesQueryExpresion);
    }

    
    /**
     * @param url
     * @return Returns the JSON as JSON Resource
     */
    private JSONResource getJSON(String url) throws uQasarException{
      JSONResource res = null;
      final Resty resty = new Resty();

      // Connection counter +1
      counter +=1;
      
      // Replaces spaces in URL with char %20
      url = url.replaceAll(" ", "%20");

      try {
        res = resty.json(url);

      } catch (IOException e) {
        // Check if the limit of trials has been reached
			if (counter < counterLimit) {
				return getJSON(url);
			} else {
				throw new uQasarException("Cubes Server is not availabe " +
						"at this moument, error to connect with " + url);
			}
      }

      // Reset the connection counter to 0
      counter = 0;

      return res;
    }    
    
    public void printMeasurements(List<Measurement> measurements){
        final String newLine = System.getProperty("line.separator");
        for (final Measurement measurement : measurements) {
            System.out.println(measurement.getMeasurement()+newLine);

        }
    }

    //in order to invoke main from outside jar
    //mvn exec:java -Dexec.mainClass="eu.uqasar.cubes.adapter.CubesAdapter" -Dexec.args="http://uqasar.pythonanywhere.com user:password cube/jira/facts"
    public static void main(String[] args) {
    	
    	
        List<Measurement> measurements;
        final BindedSystem bindedSystem = new BindedSystem();
        bindedSystem.setUri(args[0]);

        // User
        final User user = new User();
        
        final String[] credentials = args[1].split(":");
        user.setUsername(credentials[0]);
        user.setPassword(credentials[1]);
        
        try {
        	final CubesAdapter cubeAdapter = new CubesAdapter();
        	final CubesQueryExpresion cubesQueryExpresion = new CubesQueryExpresion(args[2]);
            measurements = cubeAdapter.query(bindedSystem,user,cubesQueryExpresion);
            cubeAdapter.printMeasurements(measurements);
        } catch (uQasarException e) {
            e.printStackTrace();
        }
        
    }

}
