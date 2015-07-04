package com.org.app;

import org.jsoup.Connection.Method;

import com.org.jsengine.EngineCallback;
import com.org.jsengine.PhantomJS;
import com.org.scraper.Scraper;

public class app {

	public static void main(String[] args) {
		// TODO: ADD CORS BYPASS REQUESTS
		// TODO: ADD FILE HANDLING (IN AND OUT, BOTH WITH ARRAYS OR SINGLE)
		
		String urlLogin = "https://www.facebook.com/login.php?login_attempt=1";
		String urlHome = "https://www.facebook.com/";
		
		// Initialize:
		Scraper result = new Scraper(
				urlLogin, 
				urlHome,
				true,
				"{email: youremail, pass: yourpassword, persistent: 1, default_persistent: 1, timezone: -60, locale: pt_PT}",
				"{lsd, lgndim, lgnrnd, lgnjs, qsstamp}"
		);
		
		// Set callback:
		result.setProperty(Scraper.Props.ENGINE_GET_CALLBACK, new EngineCallback() {
			public void after_get(PhantomJS ctx) {
				ctx.take_screenshot("C:\\Users\\Me\\Desktop\\screenshot.png", true);
			}

			public void before_get(PhantomJS ctx) { }
		});
		
		result.scrape(urlHome, Method.GET, "{'html':'html'}")
			  .export("C:\\Users\\Me\\Desktop\\scraped.json", false, true);
		
		System.out.println("Done scraping");
	}
}
