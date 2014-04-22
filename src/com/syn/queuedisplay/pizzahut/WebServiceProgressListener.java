package com.syn.queuedisplay.pizzahut;

public interface WebServiceProgressListener {
	void onPre();
	void onPost();
	void onError(String msg);
}
