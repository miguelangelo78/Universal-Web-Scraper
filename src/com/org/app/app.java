package com.org.app;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.jsoup.Connection.Method;

import com.org.scraper.Scraper;

public class app {

public static void main(String[] args) {
		
		String urlLogin = "https://www.facebook.com/login.php?login_attempt=1";
		String urlHome = "https://www.facebook.com/";
		
		Scraper result = new Scraper(
				urlLogin, 
				urlHome,
				Method.GET,
				true,
				"{email: youremail, pass: yourpassword, persistent: 1, default_persistent: 1, timezone: -60, locale: pt_PT}",
				"{'html':'html'}",
				"{lsd, lgndim, lgnrnd, lgnjs, qsstamp}"
		);
		
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("facebook_output.json"), "utf-8"));
		    writer.write(result.toString());
		    writer.close();
		} catch (IOException ex) {}
		
		System.out.println("Done scraping");
	}
}
