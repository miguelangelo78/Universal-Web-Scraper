package com.org.jsengine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

public interface EngineAuthCallback {
	void on_auth(WebDriver client);
	
	public static Map<String, String> toCookies(Set<Cookie> cookies_set){
		Map<String, String> new_map = new HashMap<String, String>();
		
		Iterator<Cookie> it = cookies_set.iterator();
		while(it.hasNext()){
			Cookie cook = it.next();
			new_map.put(cook.getName(), cook.getValue());
		}
		
		return new_map;
	}

}
