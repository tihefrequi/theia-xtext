package {service.namespace}.ui.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import {service.namespace}.ui.model.VariablesFactory;

/**
 * Servlet implementation class RestServlet
 */
public class VariablesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "application/json");
		response.getWriter()
				.append(new ObjectMapper().writeValueAsString(VariablesFactory.getVariables(request, response)));
	}

}
