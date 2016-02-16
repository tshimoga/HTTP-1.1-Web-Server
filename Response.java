package web_server;

public class Response {

	private String protocolVersion="";
	private int status=0;
	private String statusMessage="";
	private String responseBody="";
	private String contentType="";
	private String connection="";
	private String contentEncoding="";
	
	

	public String getContentEncoding() {
		return contentEncoding;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	public String getContentType() {
		return contentType;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String response) {
		responseBody = response;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public String getContentType(String contentType) {

		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	@Override
	public String toString() {
		
		String response="";
		
		if(!"".equals("protocolVersion")) {
			response+=protocolVersion;
		}
		if(status!=0) {
			response+=" " + status;
		}
		if(!"".equals(statusMessage)) {
			response+=" " + statusMessage;
		}
		if(!contentType.equals("")) {
			response+= "break" + "Content-Type: " + contentType;
		}
		if(!connection.equals("")) {
			response+="break" + "Connection: " + connection;
		}
		if(!contentType.equals("")) {
			response+="break"+ "Content-Encoding: " + contentEncoding;
		}
		
		response+="break" + "" + "break" + responseBody;
		
		return response;
	}

}
