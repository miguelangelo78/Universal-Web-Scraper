package com.org.app;
import org.jsoup.Connection.Method;

import com.org.scraper.Scraper;

public class app {
		
public static void main(String[] args) {
		
		String urlLogin = "https://id.glam.ac.uk/cas/login?service=http%3A%2F%2Funilife.southwales.ac.uk%2Fcas_session";
		String urlHome = "http://unilife.southwales.ac.uk/";
		
		Scraper result = new Scraper(
				urlLogin, 
				urlHome,
				Method.GET,
				"{username: yourstudentid , password: yourpassword, _eventId: submit, submit: LOGIN}",
				"{'html':'html'}",
				"{lt , execution}"
		);
		
		System.out.println(result);
	}
}
