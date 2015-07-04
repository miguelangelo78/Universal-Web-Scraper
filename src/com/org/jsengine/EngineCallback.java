package com.org.jsengine;

public interface EngineCallback {
	void after_get(PhantomJS ctx);
	void before_get(PhantomJS ctx);
}