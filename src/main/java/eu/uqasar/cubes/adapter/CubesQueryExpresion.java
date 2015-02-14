package eu.uqasar.cubes.adapter;

import java.util.Arrays;

import eu.uqasar.adapter.query.QueryExpression;
import eu.uqasar.adapter.model.uQasarMetric;

/**
 * 
 * Manu Garcia <mgarcia@innopole.net>
 * 
 * Cubes Adapter
 * 
 * Based on https://github.com/IntrasoftInternational/JiraAdapter
 * 
 */
public class CubesQueryExpresion extends QueryExpression {

	private String[] fullQuery; // Store the full expression sliced

	private String cubeName; // Cube name: Jira , SonarQube,...
	private uQasarMetric queryType;
	private String expression;

	public CubesQueryExpresion(String query) {
		super(query);

		// Parse the query
		if (query.contains("/")) {
			fullQuery = query.split("/");

			// Check if the Cube name exists and if so it is saved
			if (!fullQuery[1].isEmpty()) {
				this.cubeName = fullQuery[1];

				// Check if the query type exists and if so it is saved
				if (!fullQuery[2].isEmpty()) {

					if (fullQuery[2].contains("?")) {
						fullQuery = fullQuery[2].split("\\?");
						setQueryType(fullQuery[0]);

						// Check if the expression exists and if so it is saved
						if (!fullQuery[1].isEmpty()) {
							this.expression = fullQuery[1];
						}
					} else {
						setQueryType(fullQuery[2]);
						if(fullQuery.length==4){
							setExpression(fullQuery[3]);
						};
					}
				}
			}
		} else {
			setQueryType(query);
		}
	}

	/**
	 * @return the cubeName
	 */
	public String getCubeName() {
		return cubeName;
	}


	/**
	 * @param expression the expression to set
	 */
	private void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * @return queryType
	 */
	public uQasarMetric getQueryType() {
		return queryType;
	}
	
	/**
	 * @param queryType
	 */
	private void setQueryType(String queryType) {
		
		for (uQasarMetric metric : uQasarMetric.values()) {
			if(queryType.equalsIgnoreCase(metric.toString())){
				this.queryType = metric;
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CubesQueryExpresion [fullQuery=" + Arrays.toString(fullQuery)
				+ ", cubeName=" + cubeName + ", queryType=" + queryType
				+ ", expression=" + expression + "]";
	}

}
