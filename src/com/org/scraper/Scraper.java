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

import com.org.jsengine.PhantomJS;
import com.org.jsoniterator.JSONIterator;
import com.org.misc.Util;

public class Scraper{
	
	public enum Props { USER_AGENT, TIMEOUT, COOKIE, HEADER , PARAMS, REFERRER, METHOD, IGNRCONTTYPE, PROXY, USING_HEADLESS };
	
	private int timeout = 60*1000;
	private String USER_AGENT = "Mozilla/5.0";
	private boolean is_using_headless = false;
	private boolean is_connected = false;
	private boolean is_method_set = false;
	private JSONObject data;
	private PhantomJS engine;
	
	private Connection conn;
	private Response response_conn;
	
	// CONSTRUCTORS:
	public Scraper(){}
	
	public Scraper(String urlLogin, String urlDest, Method method, boolean using_headless, String params, String targets){
		is_using_headless = using_headless;
		engine = new PhantomJS();
		auth(urlLogin,urlDest, Util.jsonStringToArray(params));
		scrape(urlDest, method, targets);
	}
	
	public Scraper(String urlLogin, String urlDest, Method method, String params, String targets){
		auth(urlLogin,urlDest, Util.jsonStringToArray(params));
		scrape(urlDest, method, targets);
	}
	
	public Scraper(String urlLogin, String urlDest, Method method, boolean using_headless, String targets,String params, String bytokens){
		is_using_headless = using_headless;
		engine = new PhantomJS();
		auth(urlLogin,urlDest, Util.jsonStringToArray(params), Util.jsonStringToArray(bytokens));
		scrape(urlDest, method, targets);
	}
	
	public Scraper(String urlLogin, String urlDest, Method method, String targets, String params, String bytokens){
		auth(urlLogin,urlDest, Util.jsonStringToArray(params), Util.jsonStringToArray(bytokens));
		scrape(urlDest, method, targets);
	}
	
	public Scraper(String urlLogin, String urlDest, Method method, boolean using_headless, String params, String bytokens, boolean usebytokens){
		engine = new PhantomJS();
		auth(urlLogin,urlDest, Util.jsonStringToArray(params), Util.jsonStringToArray(bytokens));
		is_using_headless = using_headless;
	}
	
	public Scraper(String urlLogin, String urlDest, Method method, String params, String bytokens, boolean usebytokens){
		auth(urlLogin,urlDest, Util.jsonStringToArray(params), Util.jsonStringToArray(bytokens));
	}
		
	public Scraper(String urlLogin, String urlDest, Method method, boolean using_headless, String params){
		engine = new PhantomJS();
		auth(urlLogin,urlDest, Util.jsonStringToArray(params));
		is_using_headless = using_headless;
	}
	
	public Scraper(String urlLogin, String urlDest, Method method, String params){
		auth(urlLogin,urlDest, Util.jsonStringToArray(params));
	}
	
	public Scraper(String url){
		conn = Jsoup.connect(url);
		is_connected = true;
	}
	
	public Scraper(String url, boolean using_headless){
		engine = new PhantomJS();
		is_using_headless = using_headless;
		conn = Jsoup.connect(url);
		is_connected = true;
	}
	
	public Scraper(String url, Method method, boolean using_headless, String targets){
		scrape(url, method, targets);
		is_using_headless = using_headless;
	}
	
	public Scraper(String url, Method method, String targets){
		scrape(url, method, targets);
	}
	
	public Scraper(String url, Method method, String targets, boolean run_on_construct){
		if(!run_on_construct){
			conn = Jsoup.connect(url);
			is_connected = true;
		}else scrape(url, method, targets);
	}
	
	// SETTERS AND GETTERS:
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
		boolean bypass_token = by_tokens != null;
		
		Map<String, String> params_map = Util.strArray_to_map(params);
		
		if(bypass_token){
			// Add random param bypass in this function
			connect(urlLogin); is_connected = false;
			setProperty(Scraper.Props.METHOD, Method.GET);
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
			setProperty(Scraper.Props.COOKIE, response_conn.cookies()); // Set cookies from 1st GET request for authentication
		
		setProperty(Scraper.Props.PARAMS, params_map);
		setProperty(Scraper.Props.METHOD, Method.POST);
		setProperty(Scraper.Props.IGNRCONTTYPE, true);
		
		execute();
		
		connect(urlHome);
	}
	
	public void auth(String urlLogin, String urlHome, String[] params){
		auth(urlLogin, urlHome, params, null);
	}
	
	// SCRAPING FUNCTIONS:
	public Scraper scrape(String url, Method method, String targets){
		JSONObject targetsObj = null;
		
		Connection conn = null;
		if(!is_connected) conn = Jsoup.connect(url);
		else conn = this.conn;
		
		is_connected = false;
		
		try {
			targetsObj = ((JSONObject)new JSONParser().parse(targets.replaceAll("'","\""))); // JSON to Java Hash/Arrays
			
			conn.userAgent(USER_AGENT).timeout(timeout);
			
			if(!is_method_set)
				conn.method(method);
			
			is_method_set = false;
			
			Map<String, String> cookies_carry = response_conn.cookies(); // Carry the cookies from previous connection
			
			// Try to access homepage (intented url), with a headless browser OR regular Jsoup
			Document doc = null;
			if(is_using_headless){
				// Before actually parsing the document, execute the javascript inside the html:
				engine.run(url, cookies_carry);
				
				// TODO: Now wait and interact with the 'engine' object
				
				// After the page is finished, parse the result:
				doc = engine.getDocument();
			}
			else{
				setProperty(Scraper.Props.COOKIE, cookies_carry); // Set cookies to keep connection on
				execute();
				
				doc = response_conn.parse();
			}
			
			JSONIterator.update(targetsObj, doc); // Update the targets!
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		data = targetsObj;
		return this;
	}
	
	public Scraper scrape(Method method, String targets){
		return scrape("", method, targets);
	}
	
	public Scraper scrape(String targets){
		return scrape("", null, targets);
	}
	
	public void end(){
		engine.quit();
	}
	
	public String toString(){
		return toJSON(true);
	}
}