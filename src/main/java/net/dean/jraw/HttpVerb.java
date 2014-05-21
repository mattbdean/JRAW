package net.dean.jraw;

import org.apache.http.client.methods.*;

/**
 * A collection of RESTful HTTP verbs used by the Reddit API
 *
 * @author Matthew Dean
 */
public enum HttpVerb {
	/**
	 * Represents a GET request
	 */
	GET {
		@Override
		public HttpRequestBase getRequestObject(String path) {
			return new HttpGet(path);
		}
	},
	/**
	 * Represents a POST request
	 */
	POST {
		@Override
		public HttpRequestBase getRequestObject(String path) {
			return new HttpPost(path);
		}
	},
	/**
	 * Represents a PATCH request
	 */
	PATCH {
		@Override
		public HttpRequestBase getRequestObject(String path) {
			return new HttpPatch(path);
		}
	},
	/**
	 * Represents a PUT request
	 */
	PUT {
		@Override
		public HttpRequestBase getRequestObject(String path) {
			return new HttpPut(path);
		}
	},
	/**
	 * Represents a DELETE request
	 */
	DELETE {
		@Override
		public HttpRequestBase getRequestObject(String path) {
			return new HttpDelete(path);
		}
	};

	/**
	 * Constructs a new object extending HttpRequestBase (such as HttpGet) that can be used by HttpClient to send an
	 * HTTP request
	 *
	 * @param path The path to use to construct the request object with
	 * @return An object representing an HTTP request
	 */
	public abstract HttpRequestBase getRequestObject(String path);
}
