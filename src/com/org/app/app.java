package com.org.app;

import org.jsoup.Connection.Method;

import com.org.jsengine.EngineCallback;
import com.org.jsengine.PhantomJS;
import com.org.scraper.Scraper;

public class app {

	public static void main(String[] args) {
		// TODO: ADD EVALUATION FROM STRING TO AUTHENTICATION COMAMNDS
		// TODO: ADD CORS BYPASS REQUESTS
		// TODO: ADD FILE HANDLING (IN AND OUT, BOTH WITH ARRAYS OR SINGLE)
		// TODO: ADD A LOGGING CLASS
		
		String urlLogin = "https://store.steampowered.com/login/";
		String urlHome = "https://store.steampowered.com/";
		
		// Initialize:
		Scraper.setEngineAuthentication("{<Email>: {send(\"youremail\"), Keys.ENTER, click()}}");
		
		Scraper result = new Scraper(urlLogin, urlHome, true);
		
		// Set callback:
		result.setProperty(Scraper.Props.ENGINE_GET_CALLBACK, new EngineCallback() {
			public void after_get(PhantomJS ctx) {
				ctx.take_screenshot("C:\\Users\\Miguel\\Desktop\\screenshot.png", true);
			}

			public void before_get(PhantomJS ctx) { }
		});
		
		result.scrape(urlHome, urlLogin, Method.GET, "{'html':'html'}")
			  .export("C:\\Users\\Miguel\\Desktop\\scraped.json", false, true);
		
		System.out.println("Done scraping");
	}
}
