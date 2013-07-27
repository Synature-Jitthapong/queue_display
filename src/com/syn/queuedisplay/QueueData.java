package com.syn.queuedisplay;

public class QueueData {
	private int shopId;
	private String serverIp;
	private String serviceName;
	private String videoPath;
	private String logoPath;
	private boolean isEnableQueue;
	
	public boolean isEnableQueue() {
		return isEnableQueue;
	}
	public void setEnableQueue(boolean isEnableQueue) {
		this.isEnableQueue = isEnableQueue;
	}
	public int getShopId() {
		return shopId;
	}
	public void setShopId(int shopId) {
		this.shopId = shopId;
	}
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getVideoPath() {
		return videoPath;
	}
	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}
	public String getLogoPath() {
		return logoPath;
	}
	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}
}
