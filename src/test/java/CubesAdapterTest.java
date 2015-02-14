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
    
	String[] tests = {
			"cubes",
			"cube/jira/aggregate?drilldown=Status&cut=Status:Open",
			"cube/jira/facts",
			"cube/jira/fact/UQ-1", 
			"cube/jira/model",
			"cube/jira/cell",
			"cube/jira/members/Status"
	};


    @Test
    public void queryTestAllMetrics(){
        List<Measurement> measurements = null;


            for (String metric  : tests) {

                try{
                measurements = cubesAdapter.query(bindedSystemURL, credentials, metric);
                    cubesAdapter.printMeasurements(measurements);
                }catch (uQasarException e){
                    System.out.println(e.toString());
                }

            }


    }


//    @Test
//    public void queryTestPROJECTS_PER_SYSTEM_INSTANCE(){
//        List<Measurement> measurements = null;
//            try{
//                measurements = cubesAdapter.query(bindedSystemURL, credentials, "PROJECTS_PER_SYSTEM_INSTANCE");
//                cubesAdapter.printMeasurements(measurements);
//            }catch (uQasarException e){
//                System.out.println(e.toString());
//            }
//    }
//
//
//    @Test
//    public void queryTestISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE(){
//        List<Measurement> measurements = null;
//        try{
//            measurements = cubesAdapter.query(bindedSystemURL, credentials, "ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE");
//            cubesAdapter.printMeasurements(measurements);
//        }catch (uQasarException e){
//            System.out.println(e.toString());
//        }
//    }
//
//    @Test
//    public void queryTestFIXED_ISSUES_PER_PROJECT(){
//        List<Measurement> measurements = null;
//        try{
//            measurements = cubesAdapter.query(bindedSystemURL, credentials, "FIXED_ISSUES_PER_PROJECT");
//            cubesAdapter.printMeasurements(measurements);
//        }catch (uQasarException e){
//            System.out.println(e.toString());
//        }
//    }
//
//    @Test
//    public void queryTestUNRESOLVED_ISSUES_PER_PROJECT(){
//        List<Measurement> measurements = null;
//        try{
//            measurements = cubesAdapter.query(bindedSystemURL, credentials, "UNRESOLVED_ISSUES_PER_PROJECT");
//            cubesAdapter.printMeasurements(measurements);
//        }catch (uQasarException e){
//            System.out.println(e.toString());
//        }
//    }
//
//    @Test
//    public void queryTestUNRESOLVED_BUG_ISSUES_PER_PROJECT(){
//        List<Measurement> measurements = null;
//        try{
//            measurements = cubesAdapter.query(bindedSystemURL, credentials, "UNRESOLVED_BUG_ISSUES_PER_PROJECT");
//            cubesAdapter.printMeasurements(measurements);
//        }catch (uQasarException e){
//            System.out.println(e.toString());
//        }
//    }
//
//    @Test
//    public void queryTestUNRESOLVED_TASK_ISSUES_PER_PROJECT(){
//        List<Measurement> measurements = null;
//        try{
//            measurements = cubesAdapter.query(bindedSystemURL, credentials, "UNRESOLVED_TASK_ISSUES_PER_PROJECT");
//            cubesAdapter.printMeasurements(measurements);
//        }catch (uQasarException e){
//            System.out.println(e.toString());
//        }
//    }


    // Try to pass a non existing metric
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
