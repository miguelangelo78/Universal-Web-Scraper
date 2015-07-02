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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.org.jsoniterator.JSONIterator;
import com.org.misc.Util;

public class Scraper{
	
	public enum Props { USER_AGENT, TIMEOUT, COOKIE, HEADER , PARAMS, REFERRER, METHOD, IGNRCONTTYPE, PROXY };
	
	private int timeout = 60*1000;
	private String USER_AGENT = "Mozilla/5.0";
	private boolean is_connected = false;
	private boolean is_method_set = false;
	private JSONObject data;
	
	private Connection conn;
	private Response response_conn;
	
	// CONSTRUCTORS:
	public Scraper(){}
	
	public Scraper(String urlLogin, String urlDest, Method method, String params, String targets){
		auth(urlLogin,urlDest, Util.jsonStringToArray(params));
		scrape(urlDest, method, targets);
	}
	
	public Scraper(String urlLogin, String urlDest, Method method, String params){
		auth(urlLogin,urlDest, Util.jsonStringToArray(params));
	}
	
	public Scraper(String url){
		conn = Jsoup.connect(url);
		is_connected = true;
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
	
	public void auth(String urlLogin, String urlHome, String[] params){
		if(!is_connected) connect(urlLogin);
		
		setProperty(Scraper.Props.PARAMS, Util.strArray_to_map(params));
		setProperty(Scraper.Props.METHOD, Method.POST);
		setProperty(Scraper.Props.IGNRCONTTYPE, true);
		
		execute();
		
		is_connected = false;
		connect(urlHome);
		setProperty(Scraper.Props.COOKIE, response_conn.cookies());
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
						
			JSONIterator.update(targetsObj, conn.execute().parse());
			
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
	
	public String toString(){
		return toJSON(true);
	}
}