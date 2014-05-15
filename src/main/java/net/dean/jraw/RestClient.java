package net.dean.jraw;

import org.apache.http.HttpException;

import java.io.IOException;
import java.util.Map;
import static net.dean.jraw.HttpVerb.*;

public class RestClient {
	private final String host;
	private HttpHelper http;

	public RestClient(String host, String userAgent) {
		this.http = new HttpHelper(userAgent);
		this.host = host;
	}

	public RestResponse get(String path) throws IOException, HttpException {
		return get(path, null);
	}

	public RestResponse get(String path, Map<String, String> args) throws IOException, HttpException {
		return new RestResponse(http.execute(GET, host, path, args));
	}

	public RestResponse post(String path) throws IOException, HttpException {
		return post(path, null);
	}

	public RestResponse post(String path, Map<String, String> args) throws IOException, HttpException {
		return new RestResponse(http.execute(POST, host, path, args));
	}

	public RestResponse put(String path) throws IOException, HttpException {
		return put(path, null);
	}

	public RestResponse put(String path, Map<String, String> args) throws IOException, HttpException {
		return new RestResponse(http.execute(PUT, host, path, args));
	}

	public RestResponse patch(String path) throws IOException, HttpException {
		return patch(path, null);
	}

	public RestResponse patch(String path, Map<String, String> args) throws IOException, HttpException {
		return new RestResponse(http.execute(PATCH, host, path, args));
	}
}
