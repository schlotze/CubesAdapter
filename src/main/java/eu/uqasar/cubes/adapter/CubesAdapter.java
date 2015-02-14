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
 * Cubes Adapter
 * 
 * Based on https://github.com/IntrasoftInternational/JiraAdapter
 * 
 */

public class CubesAdapter implements SystemAdapter {

	String value = "";
	private Integer connectionTrials = 0;
	
    public CubesAdapter() {
    }

    @Override
    public List<Measurement> query(BindedSystem bindedSystem, User user, QueryExpression queryExpression) throws uQasarException {

        LinkedList<Measurement> measurements = new LinkedList<Measurement>();
        
        String measurement = "";
        String url = "";
        
        
        CubesQueryExpresion cubesQueryExpresion = (CubesQueryExpresion) queryExpression;

        try {
        	URI uri = new URI(bindedSystem.getUri());
        	url = uri.toString();
        	
        	// check if the provided URL ends with "/"
            if(!url.endsWith("/")){
            	url += "/";
            }        	
            
            /* START -- Metrics implementation */
            if (cubesQueryExpresion.getQueryType() == uQasarMetric.AGGREGATE){
            	// This only retrieves the value
            	value = getJSONObject(url + cubesQueryExpresion.getQuery(),"summary","count");
            	measurement = getJSONObject(url + cubesQueryExpresion.getQuery());
            }   else if ((cubesQueryExpresion.getQueryType() == uQasarMetric.FACT) ||
            	(cubesQueryExpresion.getQueryType() == uQasarMetric.MEMBERS)){ //JSONObject
            	measurement = getJSONObject(url + cubesQueryExpresion.getQuery());
            }   else if (cubesQueryExpresion.getQueryType() == uQasarMetric.CUBES){   //JSONArray
            	measurement = getJSONArray(url + cubesQueryExpresion.getQuery());
            }   else if ((cubesQueryExpresion.getQueryType() == uQasarMetric.MODEL) ||   //JSONObject
            			(cubesQueryExpresion.getQueryType() == uQasarMetric.CELL)){    //JSONObject
            	measurement = getJSONObject(url + cubesQueryExpresion.getQuery());
            } 	else if (cubesQueryExpresion.getQueryType() == uQasarMetric.FACTS){   //JSONArray
            	measurement = getJSONArray(url + cubesQueryExpresion.getQuery());
            } else {
            	throw new uQasarException(uQasarException.UQasarExceptionType.UQASAR_NOT_EXISTING_METRIC,cubesQueryExpresion.getQuery());
            }

            if(measurement != null){
            	 JSONArray measurementResultJSONArray = new JSONArray();
            	 JSONObject bp = new JSONObject();
            	 bp.put("self",url + cubesQueryExpresion.getQuery() );
            	 bp.put("key",cubesQueryExpresion.getQueryType() );
            	 bp.put("name",cubesQueryExpresion.getCubeName());
            	 bp.put("jsonContent", measurement);
            	 bp.put("value", value);
            	 measurementResultJSONArray.put(bp);

            	 // Retrieved measurement is stored in List 
        		measurements.add(new Measurement(cubesQueryExpresion.getQueryType(), measurementResultJSONArray.toString()));
            }
		
            /* END -- Metrics implementation */

        } catch (URISyntaxException e) {
            throw new uQasarException(uQasarException.UQasarExceptionType.BINDING_SYSTEM_BAD_URI_SYNTAX,bindedSystem,e.getCause());
        }  catch (RuntimeException e){
            throw new uQasarException(uQasarException.UQasarExceptionType.BINDING_SYSTEM_CONNECTION_REFUSED,bindedSystem,e.getCause());
        } catch (org.codehaus.jettison.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return measurements;
    }

    
    @Override
    public List<Measurement> query(String bindedSystemURL, String credentials, String queryExpression) throws uQasarException {
        List<Measurement> measurements = null;

        BindedSystem bindedSystem = new BindedSystem();
        bindedSystem.setUri(bindedSystemURL);
        User user = new User();

        String[] creds = credentials.split(":");

        // TODO: check that user can be split before set
        user.setUsername(creds[0]);
        user.setPassword(creds[1]);

        CubesQueryExpresion cubesQueryExpresion = new CubesQueryExpresion(queryExpression);
        CubesAdapter cubesAdapter = new CubesAdapter();

        measurements = cubesAdapter.query(bindedSystem,user,cubesQueryExpresion);

        return measurements;
    }

    
//	public String formatIssuesResult(Iterable<BasicIssue> issues)
//			throws JSONException {
//		JSONArray measurementResultJSONArray = new JSONArray();
//		for (BasicIssue issue : issues) {
//			JSONObject i = new JSONObject();
//			i.put("self", issue.getSelf());
//			i.put("key", issue.getKey());
//			measurementResultJSONArray.put(i);
//		}
//		return measurementResultJSONArray.toString();
//	}  
    
	/**
	 * @param url
	 * @return
	 */
	public String getJSONObject(String url) {
		return getJSONObject(url, null, null);
	}

	/**
	 * @param url
	 * @param obj
	 * @return
	 */
	public String getJSONObject(String url, String obj) {
		return getJSONObject(url, obj, null);
	}
	
	/**
	 * @param url
	 * @param obj
	 * @param member
	 * @return
	 */
	public String getJSONObject(String url, String obj, String member) {
		Resty resty = new Resty();
		JSONResource res = null;
		String output = null;
		
			try {
				
				url = url.replaceAll(" ", "%20");
				
				// GET petition to the url
				res = resty.json(url);
				
				// Evaluates the provided Obj and member
				if(obj == null){
					// Parse to get ALL the Object
					output = res.toObject().toString();
				} else if(member == null){
					// Parse to get the "obj" Object
					output = res.toObject().getJSONObject(obj).toString();
				} else {
					// Parse to get the value of the "obj" within the "member"
					output = res.toObject().getJSONObject(obj).getString(member);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				return getJSONObject(url, obj,  member);
			} catch (us.monoid.json.JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return output;
	}
	
	/**
	 * @param url
	 * @return
	 */
	public String getJSONArray(String url){
		return getJSONArray(url, null);
	}
    
    /**
     * @param url
     * @param array
     * @return
     */
    public String getJSONArray(String url, String array){
		Resty resty = new Resty();
		JSONResource res = null;
		String output = null;
		
			try {
				// GET petition to the url
				res = resty.json(url);
				
				// Parse to get the value of the "obj" within the "member"
				if (array == null) {
					output = res.array().toString();
				} else {
					output = res.toObject().getJSONArray(array).toString();
				}
				
				value = String.valueOf(res.array().length());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				return getJSONArray(url, array);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return output;
    }
    
    
    public void printMeasurements(List<Measurement> measurements){
        String newLine = System.getProperty("line.separator");
        for (Measurement measurement : measurements) {
            System.out.println(measurement.getMeasurement()+newLine);

        }
    }

    //in order to invoke main from outside jar
    //mvn exec:java -Dexec.mainClass="eu.uqasar.cubes.adapter.CubesAdapter" -Dexec.args="http://uqasar.pythonanywhere.com user:password ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE"

    public static void main(String[] args) {
    	
    	String[] params = new String[3];

    	String[] pruebas = {
//    			"cubes",
				"cube/jira/aggregate?drilldown=Status&cut=Status:To Do",
				"cube/jira/facts",
//				"cube/jira/fact/UQ-1", 
//				"cube/jira/model",
//				"cube/jira/cell",
//				"cube/jira/members/Status"
//    			"ERRONEUS METRIC",
    	};
    	
    	// URL
    	params[0] = "http://uqasar.pythonanywhere.com";

    	// Credentials (cube to query : Authentication token )
    	params[1] = "user:password";

    	
        List<Measurement> measurements;
        BindedSystem bindedSystem = new BindedSystem();
        bindedSystem.setUri(params[0]);

        // User
        User user = new User();
        String[] credentials = params[1].split(":");
        user.setUsername(credentials[0]);
        user.setPassword(credentials[1]);

        
        try {
        CubesAdapter cubeAdapter = new CubesAdapter();

        for (String prueba : pruebas) {
        	CubesQueryExpresion cubesQueryExpresion = new CubesQueryExpresion(prueba);
            measurements = cubeAdapter.query(bindedSystem,user,cubesQueryExpresion);
        }
        } catch (uQasarException e) {
            e.printStackTrace();
        }
        
        System.out.println("Ended execution");
    }

}
