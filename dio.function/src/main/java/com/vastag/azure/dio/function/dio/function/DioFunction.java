package com.vastag.azure.dio.function.dio.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class DioFunction {
	/**
	 * This function listens at endpoint "/api/HttpExample". Two ways to invoke it
	 * using "curl" command in bash: 1. curl -d "HTTP Body" {your
	 * host}/api/HttpExample 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
	 */
	@FunctionName("GetPoCValues")
	public HttpResponseMessage run(@HttpTrigger(name = "req", methods = { HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context) {
		// Parse query parameter
		String connectionUrl = System.getenv("ConnectionUrl");
		ResultSet resultSet = null;
		ArrayList<String> values = new ArrayList<>();
		
		if (null == connectionUrl) {
			connectionUrl = System.getenv("SQLCONNSTR_ConnectionUrl");
		}

		try (Connection connection = DriverManager.getConnection(connectionUrl);
				Statement statement = connection.createStatement();) {

			// Create and execute a SELECT SQL statement.
			String selectSql = "SELECT * FROM [dbo].[POC_PARTNER_XP]";
			resultSet = statement.executeQuery(selectSql);
			// Print results from select statement
			while (resultSet.next()) {
				values.add(resultSet.getString("Description"));
			}
			return request.createResponseBuilder(HttpStatus.OK).body(values).build();
		} catch (Exception e) {
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(e.getMessage()).build();
		}
	}
}
