[![Build Status](https://travis-ci.org/ManuDevelopia/CubesAdapter.svg?branch=master)](https://travis-ci.org/ManuDevelopia/CubesAdapter)

CubesAdapter
===========

CubesAdapter is a project that implements the uQasarAdapter interface overriding the following methods:

	query : invokes a specific query to a specified binded system-instance using the credentials of a specific user while in parallel returns a list of measurements

------------------------------------------------------------------------

Cubes measures are the predefined metrics that are proposed by uQasarAdapter:

For the time being these metrics are:
     
	AGGREGATE
	FACT
    MEMBERS
    CUBES
    MODEL
    CELL
    FACTS

----------------------------------------------------------------------

Furthermore JiraAdapter throws the proposed uQuasarExceptionTypes


    BINDING_SYSTEM_CONNECTION_REFUSED (thrown when a binding system refuses the connection to the third party Adapter)

    BINDING_SYSTEM_BAD_URI_SYNTAX, (thrown when the binding system base url is malformed)

    UQASAR_NOT_EXISTING_METRIC (thrown when the queried metric is not a proper uQasarMetric)
    
    ERROR_PARSING_JSON (throw when the JSON received can not be parsed)

 
 ---------------------------------------------------------------------
 
All CubesAdapter methods are tested via JUnit tests

---------------------------------------------------------------------

CubesAdapter can be invoked as Java Library (JAR) from command line as 


	mvn exec:java -Dexec.mainClass="eu.uqasar.cubes.adapter.CubesAdapter" -Dexec.args="http://uqasar.pythonanywhere.com user:password cube/jira/facts"
	
OR

	java -cp CubesAdapter-1.0.jar eu.uqasar.cubes.adapter.CubesAdapter http://uqasar.pythonanywhere.com user:password cube/jira/facts
		
 
arg0 is the URL binding or the JiraInstallation
arg1 is the string concatenation of username:password
arg2 is the desired METRIC QUERY



