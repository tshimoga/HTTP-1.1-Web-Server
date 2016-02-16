package web_server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class Protocol {
	private Request request = new Request();
	private Response response = new Response();

	public void processInput(String input) {
		String strArray[] = input.split("\n");
		for (String s : strArray) {
			if (s.toLowerCase().contains("http") || s.toLowerCase().contains("get")) {
				processRequest(s);
			} else if (s.toLowerCase().contains("agent")) {
				processUserAgent(s);
			} else if (s.toLowerCase().contains("accept:")) {
				processAccept(s);
			} else if (s.toLowerCase().contains("accept-language:")) {
				processAcceptLanguage(s);
			} else if (s.toLowerCase().contains("accept-encoding:")) {
				processAcceptEncoding(s);
			} else if (s.toLowerCase().contains("connection")) {
				processConnection(s);
			} else if (s.toLowerCase().contains("host")) {
				processHost(s);
			}
		}
	}

	private void processHost(String s) {
		String[] arr = s.split(":");
		request.setHost(arr[1]);
		request.setPort(Integer.parseInt(arr[2]));

	}

	private void processConnection(String s) {
		request.setConnection(s.split(" ")[1]);
	}

	private void processAcceptEncoding(String s) {
		String[] encodingArray = s.split(" ");
		String acceptEncoding = "";
		for (int i = 1; i < encodingArray.length; i++) {
			acceptEncoding += encodingArray[i];
		}

		request.setAcceptEncoding(acceptEncoding);
	}

	private void processAcceptLanguage(String s) {
		request.setAcceptLanguage(s.split(" ")[1]);

	}

	private void processAccept(String s) {
		request.setAccept(s.split(" ")[1]);

	}

	private void processUserAgent(String s) {
		String[] agentArray = s.split(" ");
		String userAgent = "";
		for (int i = 1; i < agentArray.length; i++) {
			userAgent += agentArray[i];
		}
		request.setUserAgent(userAgent);
	}

	public String executeCommand() {

		StringBuffer output = new StringBuffer();
		BufferedReader reader;
		Process process;
		try {
			process = Runtime.getRuntime()
					.exec(new String[] { "/bin/sh", "-c", request.getRequestURI().replace("/exec/", "") });
			process.waitFor();
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			if ((line = reader.readLine()) != null) {
				do {
					output.append(line + "\n");
				} while ((line = reader.readLine()) != null);
			} else {
				reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				while ((line = reader.readLine()) != null) {
					output.append(line + "\n");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();

	}

	public void setResponseBody(String resp) {
		String responseBody = "";
		String contentType = "";
		boolean agent = (request.getUserAgent().toLowerCase().contains("mozilla"))
				|| (request.getUserAgent().toLowerCase().contains("apple"))
				|| (request.getUserAgent().toLowerCase().contains("safari"))
				|| (request.getUserAgent().toLowerCase().contains("chrome"));
		if (agent) {
			responseBody = "<html><body>";
			responseBody += resp;
			responseBody += "</body></html>";
			responseBody = responseBody.replace("\n", "<br/>");
			if (response.getStatus() == 200) {
				contentType = "text/html; charset=UTF-8";
			} else {
				contentType = "text/html; charset=iso-8859-1";
			}

		} else {
			responseBody = resp;

		}
		response.setContentType(contentType);
		response.setResponseBody(responseBody);
	}

	public String buildNotFound() {
		String message = "<b>Not Found</b>\n\nThe requested URL " + request.getRequestURI()
				+ " was not found on this server.\n<hr>";

		return message;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public void processRequest(String input) {
		String[] strArray = input.split(" ");
		request.setMethod(strArray[0]);
		try {
			request.setRequestURI(URLDecoder.decode(strArray[1], "UTF-8"));
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		request.setProtocolVersion(strArray[2]);
	}

	public void processResponse() {

		String[] requestURI = request.getRequestURI().split("/");
		response.setConnection(request.getConnection());
		response.setProtocolVersion(request.getProtocolVersion());

		if (requestURI.length > 1 && "exec".equals(requestURI[1])) {
			if(!request.getUserAgent().toLowerCase().contains("curl")) {
				response.setStatus(200);
				response.setStatusMessage("OK");
				
			}
			
			setResponseBody(executeCommand());

		} else {
			response.setStatus(404);
			response.setStatusMessage("Not Found");
			setResponseBody(buildNotFound());
		}
		
		if(request.getAcceptEncoding().toLowerCase().contains("gzip") || request.getAcceptEncoding().toLowerCase().contains("deflate")) {
			response.setContentEncoding("gzip");
		}

	}

}
