package com.org.app;
import org.jsoup.Connection.Method;

import com.org.scraper.Scraper;

public class app {
		
public static void main(String[] args) {
		
		String urlLogin = "https://www.reddit.com/api/login/yourusername/";
		String urlHome = "http://www.reddit.com/";
		
		Scraper result = new Scraper(
				urlLogin, 
				urlHome,
				Method.GET,
				"{op : login-main, api_type : json, user : yourusername, passwd : yourpassword}",
				"{'html':'html'}"
				
				
		);
		
		System.out.println(result);
	}
}
