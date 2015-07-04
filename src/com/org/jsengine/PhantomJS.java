package com.org.jsengine;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class PhantomJS {
	private WebDriver client;
	private DesiredCapabilities capabilities;
	
	private final int CLIENT_WIDTH = 1920;
	private final int CLIENT_HEIGHT = 1080;
	
	private final String PHANTOM_EXECUTABLE = "lib/phantomjs/phantomjs.exe";
	
	private void setCapabilities(){
		capabilities = new DesiredCapabilities();
		capabilities.setJavascriptEnabled(true);
		capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, PHANTOM_EXECUTABLE);
		capabilities.setCapability("databaseEnabled", true);
		capabilities.setCapability("locationContextEnabled", true);
		capabilities.setCapability("applicationCacheEnabled", true);
		capabilities.setCapability("browserConnectionEnabled", true);
		capabilities.setCapability("webStorageEnabled", true);
		capabilities.setCapability("acceptSslCerts", true);
		
	}
	
	public Document getDocument(){
		return Jsoup.parse(client.getPageSource());
	}
	
	private String getDomainFromUrl(String url){
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	    String domain = uri.getHost();
	    return domain.startsWith("www.") ? domain.substring(4) : domain;
	}
	
	private void setCookies(String domain, Map<String, String> cookies){
		for (Map.Entry<String, String> entry : cookies.entrySet())
			client.manage().addCookie(new Cookie(entry.getKey(), entry.getValue(), domain, "/", null));
	}
	
	public PhantomJS(){
		setCapabilities();
		client = new PhantomJSDriver(capabilities);
		
		client.manage().window().setSize(new Dimension(CLIENT_WIDTH, CLIENT_HEIGHT));
	}
	
	public void run(String base_url, Map<String, String> cookies){
		setCookies(getDomainFromUrl(base_url), cookies);
		client.get(base_url);
		
		// Do extra stuff down here, like taking screenshots or interacting with the UI:
		
		File scrFile = ((TakesScreenshot)client).getScreenshotAs(OutputType.FILE);
		// Now you can do whatever you need to do with it, for example copy somewhere
		try {
			FileUtils.copyFile(scrFile, new File("C:\\Users\\Miguel\\Desktop\\screenshot.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void quit(){
		client.close();
		client.quit();
	}
	
}
