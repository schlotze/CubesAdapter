package eu.uqasar.cubes.adapter;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.Measurement;
import eu.uqasar.cubes.adapter.CubesAdapter;

/**
 * Manu Garcia <mgarcia@innopole.net>
 * 
 */

public class CubesAdapterTest {

    CubesAdapter cubesAdapter = new CubesAdapter();
    String newLine = System.getProperty("line.separator");
    String bindedSystemURL = "http://uqasar.pythonanywhere.com";
    String credentials = "user:password";
    
    
    @Test
    public void queryTest_CUBES(){
    	List<Measurement> measurements = null;
    	
    	try {
			measurements = cubesAdapter.query(bindedSystemURL, credentials, "cubes");
			cubesAdapter.printMeasurements(measurements);
		} catch (uQasarException e) {
			System.out.println(e.toString());
		}
    	
    }

    @Test
    public void queryTest_AGGREGATE(){
    	List<Measurement> measurements = null;
    	
    	try {
    		measurements = cubesAdapter.query(bindedSystemURL, credentials, "cube/jira/aggregate?drilldown=Status&cut=Status:Open");
    		cubesAdapter.printMeasurements(measurements);
    	} catch (uQasarException e) {
    		System.out.println(e.toString());
    	}
    	
    }

    @Test
    public void queryTest_FACTS(){
    	List<Measurement> measurements = null;
    	
    	try {
    		measurements = cubesAdapter.query(bindedSystemURL, credentials, "cube/jira/facts");
    		cubesAdapter.printMeasurements(measurements);
    	} catch (uQasarException e) {
    		System.out.println(e.toString());
    	}
    	
    }

    @Test
    public void queryTest_FACT(){
    	List<Measurement> measurements = null;
    	
    	try {
    		measurements = cubesAdapter.query(bindedSystemURL, credentials, "cube/jira/fact/UQ-1");
    		cubesAdapter.printMeasurements(measurements);
    	} catch (uQasarException e) {
    		System.out.println(e.toString());
    	}
    	
    }

    @Test
    public void queryTest_MODEL(){
    	List<Measurement> measurements = null;
    	
    	try {
    		measurements = cubesAdapter.query(bindedSystemURL, credentials, "cube/jira/model");
    		cubesAdapter.printMeasurements(measurements);
    	} catch (uQasarException e) {
    		System.out.println(e.toString());
    	}
    	
    }

    @Test
    public void queryTest_CELL(){
    	List<Measurement> measurements = null;
    	
    	try {
    		measurements = cubesAdapter.query(bindedSystemURL, credentials, "cube/jira/cell");
    		cubesAdapter.printMeasurements(measurements);
    	} catch (uQasarException e) {
    		System.out.println(e.toString());
    	}
    	
    }

    @Test
    public void queryTest_MEMBER_STATUS(){
    	List<Measurement> measurements = null;
    	
    	try {
    		measurements = cubesAdapter.query(bindedSystemURL, credentials, "cube/jira/members/Status");
    		cubesAdapter.printMeasurements(measurements);
    	} catch (uQasarException e) {
    		System.out.println(e.toString());
    	}
    	
    }


    @Test
    public void queryTest_erroneus_metric(){

    try{
        List<Measurement> measurements = cubesAdapter.query(bindedSystemURL, credentials, "ERRONEUS METRIC");
    }catch (uQasarException e){
        e.printStackTrace();
        assertTrue(true);
    }

    }

}
