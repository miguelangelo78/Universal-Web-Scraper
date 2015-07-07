package com.org.scraper;

import java.io.IOException;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.org.file.ScraperFile;
import com.org.jsengine.EngineAuthCallback;
import com.org.jsengine.EngineCallback;
import com.org.jsengine.PhantomJS;
import com.org.jsoniterator.JSONIterator;
import com.org.misc.Util;

public class WebScraper{
	
	public enum Props { 
		USER_AGENT, TIMEOUT, COOKIE, HEADER , PARAMS, REFERRER, METHOD, 
		IGNRCONTTYPE, PROXY, USING_HEADLESS, ENGINE_GET_CALLBACK , ENGINE_AUTH_CALLBACK, MANUAL_AUTH};
	
	private int timeout = 60*1000;
	private String USER_AGENT = "Mozilla/5.0";
	private JSONObject data;
	private Document document;
	private boolean is_connected = false;
	private boolean is_method_set = false;
	public static boolean manual_auth = false;
	public static boolean is_using_headless = false;
	public static PhantomJS engine;
	public static EngineCallback engine_get_callback = null;
	public static EngineAuthCallback engine_auth_callback = null;
	
	private Connection conn;
	private Response response_conn;
	private Map<String, String> cookies_auth;
	
	// CONSTRUCTORS: ************************************************************************************
	public WebScraper(){}
	
	public WebScraper(String urlLogin, String urlDest, Method method, boolean using_headless, String params, String targets){
		is_using_headless = using_headless;
		engine = new PhantomJS();
		auth(urlLogin,urlDest, Util.jsonStringToArray(params));
		scrape(urlDest,urlLogin, method, targets);
	}
	
	public WebScraper(String urlLogin, String urlDest, Method method, String params, String targets){
		auth(urlLogin,urlDest, Util.jsonStringToArray(params));
		scrape(urlDest, urlLogin, method, targets);
	}
	
	public WebScraper(String urlLogin, String urlDest, Method method, boolean using_headless, String targets,String params, String bytokens){
		is_using_headless = using_headless;
		engine = new PhantomJS();
		auth(urlLogin,urlDest, Util.jsonStringToArray(params), Util.jsonStringToArray(bytokens));
		scrape(urlDest, urlLogin, method, targets);
	}
	
	public WebScraper(String urlLogin, String urlDest, Method method, String targets, String params, String bytokens){
		auth(urlLogin,urlDest, Util.jsonStringToArray(params), Util.jsonStringToArray(bytokens));
		scrape(urlDest, urlLogin, method, targets);
	}
	
	public WebScraper(String urlLogin, String urlDest, boolean using_headless, String params, String bytokens){
		is_using_headless = using_headless;
		engine = new PhantomJS();
		auth(urlLogin,urlDest, Util.jsonStringToArray(params), Util.jsonStringToArray(bytokens));
	}
	
	public WebScraper(String urlLogin, String urlDest, Method method, String params, String bytokens, boolean usebytokens){
		auth(urlLogin,urlDest, Util.jsonStringToArray(params), Util.jsonStringToArray(bytokens));
	}
		
	public WebScraper(String urlLogin, String urlDest, Method method, boolean using_headless, String params){
		is_using_headless = using_headless;
		engine = new PhantomJS();
		auth(urlLogin,urlDest, Util.jsonStringToArray(params));
	}
	
	public WebScraper(String urlLogin, String urlDest, Method method, String params){
		auth(urlLogin,urlDest, Util.jsonStringToArray(params));
	}
	
	public WebScraper(String urlLogin, String urlDest, boolean using_headless){
		is_using_headless = using_headless;
		engine = new PhantomJS();
		auth(urlLogin,urlDest, null);
	}
	
	public WebScraper(String url){
		conn = Jsoup.connect(url);
		is_connected = true;
	}
	
	public WebScraper(String url, boolean using_headless){
		engine = new PhantomJS();
		is_using_headless = using_headless;
		conn = Jsoup.connect(url);
		is_connected = true;
	}
	
	public WebScraper(String url, Method method, boolean using_headless, String targets){
		scrape(url, url, method, targets);
		is_using_headless = using_headless;
	}
	
	public WebScraper(String url, Method method, String targets){
		scrape(url, url, method, targets);
	}
	
	public WebScraper(String url, Method method, String targets, boolean run_on_construct){
		if(!run_on_construct){
			conn = Jsoup.connect(url);
			is_connected = true;
		}else scrape(url, url, method, targets);
	}
	// END CONSTRUCTORS ************************************************************************************
	
	// SETTERS AND GETTERS:
	public Document getDocument() {
		return document;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}
	
	public static void setEngineAuthentication(EngineAuthCallback callback){
		engine_auth_callback = callback;
	}
	
	@SuppressWarnings("unchecked")
	public void setProperty(Props property, Object value){
		switch(property){
			case TIMEOUT: timeout = (int) value; break;
			case COOKIE: if(value instanceof String[]) conn.cookie(((String[])(value))[0], ((String[])(value))[1]); else conn.cookies((Map<String, String>) value); break;
			case HEADER: conn.header(((String[])(value))[0], ((String[])(value))[1]); break;
			case PARAMS: conn.data((Map<String, String>)value); break;
			case USER_AGENT: this.USER_AGENT = (String) value; break;
			case REFERRER: conn.referrer((String) value); break;
			case METHOD: is_method_set = true; conn.method((org.jsoup.Connection.Method) value); break;
			case IGNRCONTTYPE: conn.ignoreContentType((boolean) value); break;
			case USING_HEADLESS: is_using_headless = (boolean) value; break;
			case ENGINE_GET_CALLBACK: engine_get_callback = (EngineCallback) value; break;
			case ENGINE_AUTH_CALLBACK: engine_auth_callback = (EngineAuthCallback) value; break;
			case MANUAL_AUTH: manual_auth = (boolean) value; break;
			case PROXY: 
				String [] keyvals = Util.jsonStringToArray((String) value);
				System.setProperty("http.proxyHost", keyvals[0]); // Set Proxy Host
				System.setProperty("http.proxyPort", keyvals[1]); // Set Proxy Port
				break;
			default: break;
		}
	}
	
	public Object getProperty(Props property, String value){
		switch(property){
			case TIMEOUT: return timeout;
			case COOKIE: return conn.response().cookies();
			case HEADER: return conn.response().header(value);
			case USER_AGENT: return this.USER_AGENT;
			case METHOD: return conn.response().method();
			case USING_HEADLESS: return is_using_headless;
			default: return null;
		}
	}
	
	public Response getResponse(){
		return response_conn;
	}
	
	// DOCUMENT ACCESS:
	public String get(String target, int index){
		return ((Element)((Object[])data.get(target))[index]).toString();
	}
	
	public Element elem(String target, int index){
		return ((Element)((Object[])data.get(target))[index]);
	}
	
	public Elements elems(String target){
		Elements el = new Elements();
		
		Object[] obj_el = (Object[])data.get(target);
	
		for(Object obj: obj_el)
			el.add((Element) obj);
		
		return el;
	}
	
	public Elements[] allElems(){
		Elements[] all_elements = new Elements[data.size()];
		
		int i = 0;
		for(Object elems_targets: data.keySet())
			all_elements[i++] = elems((String)elems_targets);
		
		return all_elements;
	}
	
	void updateCookies(Map<String, String> new_cookies){
		cookies_auth = new_cookies;
	}
	
	// OUTPUT FUNCTIONS:
	public void print(){
		JSONIterator.print(data);
	}
	
	public String toJSON(){
		Util.outerJSON = data;
		return Util.toJSON();
	}
	
	public String toJSON(boolean addslashes){
		Util.json_with_slashes = true;
		String slashed_json = toJSON();
		Util.json_with_slashes = false;
		return slashed_json;
	}
	
	// JSOUP CONNECTION FUNCTIONS:
	public void connect(String url){
		if(!is_connected){
			conn = Jsoup.connect(url);
			is_connected = !is_connected;
		}
	}
	
	public Connection execute(){
		try {
			response_conn = conn.execute();
			is_connected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public void auth(String urlLogin, String urlHome, String[] params, String by_tokens[]){
		if(engine_auth_callback==null && !manual_auth){
			boolean bypass_token = by_tokens != null;
				
				Map<String, String> params_map = Util.strArray_to_map(params);
				
				if(bypass_token){
					// Add random param bypass in this function
					connect(urlLogin); is_connected = false;
					
					setProperty(WebScraper.Props.IGNRCONTTYPE, true);
					setProperty(WebScraper.Props.METHOD, Method.GET);
					execute();
					
					// Fetch random generated field:
					try {
						Document curld = response_conn.parse();
						for(String field: by_tokens)
							params_map.put(field, curld.select("input[name="+field+"]").val()); // Put the random generated field
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				// Actually authenticate:
				connect(urlLogin); is_connected = false;
				
				if(bypass_token)
					setProperty(WebScraper.Props.COOKIE, response_conn.cookies()); // Set cookies from 1st GET request for authentication
				
				setProperty(WebScraper.Props.PARAMS, params_map);
				setProperty(WebScraper.Props.METHOD, Method.POST);
				setProperty(WebScraper.Props.IGNRCONTTYPE, true);
				
				execute(); // Authenticate!
				
				connect(urlHome);
				updateCookies(response_conn.cookies());
		}else
			updateCookies(engine.auth(engine_auth_callback, urlLogin, manual_auth));
	}
	
	public void auth(String urlLogin, String urlHome, String[] params){
		auth(urlLogin, urlHome, params, null);
	}
	
	// SCRAPING FUNCTIONS:
	public WebScraper scrape(String urlAuth, String url, Method method, String targets){
		Document final_doc = null; // This document will fill the jsonobject 'targetsObj' object
		
		JSONObject targetsObj = null;
		
		boolean targetless = targets == null; // This means it's not scraping any data
		
		if(!targetless){
			try {
				targetsObj = ((JSONObject) new JSONParser().parse(targets.replaceAll("'","\""))); // JSON to Java Hash/Arrays
			} catch (ParseException e1) { e1.printStackTrace(); } 
		}
		
		if(is_using_headless){
			// Before actually parsing the document, execute the javascript inside the html:
			engine.run(url, urlAuth, cookies_auth, engine_get_callback);
			
			// After the page is finished, parse the result:
			if(!targetless) final_doc = engine.getDocument();
		}else if(!targetless){
				
			Connection conn = null;
			if(!is_connected) conn = Jsoup.connect(url);
			else conn = this.conn;
			
			is_connected = false;
			
			try {
				conn.userAgent(USER_AGENT).timeout(timeout);
				
				if(!is_method_set)
					conn.method(method);
				
				is_method_set = false;
				
				// Try to access homepage (intented url), with a headless browser OR regular Jsoup
				updateCookies(cookies_auth); // Set cookies to keep connection on
				execute();
					
				final_doc = response_conn.parse();
					
			} catch (IOException e) { e.printStackTrace(); }
		}
		
		if(!targetless){
			JSONIterator.update(targetsObj, final_doc); // Update the targets!
			document = final_doc;
			data = targetsObj; // Data updated
		}
		return this;
	}
	
	public WebScraper scrape(Method method, String targets){
		return scrape("","", method, targets);
	}
	
	public WebScraper scrape(String targets){
		return scrape("","", null, targets);
	}
	
	public void end(){
		engine.quit();
	}
	
	public void export(String filepath, boolean to_each_file, boolean end){
		if(to_each_file){
			
		}else
			ScraperFile.write(filepath, toString());
		
		if(end)
			end();
	}
	
	public String toString(){
		return toJSON(true);
	}
}