package com.syn.queuedisplay;

public class QueueData {
	private int shopId;
	private String serverIp;
	private String serviceName;
	private String videoPath;
	private String logoPath;
	private int port;
	private boolean isEnableQueue;
	private boolean isEnableTake;
	private int updateInterval;
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isEnableTake() {
		return isEnableTake;
	}
	public void setEnableTake(boolean isEnableTake) {
		this.isEnableTake = isEnableTake;
	}
	public int getUpdateInterval() {
		return updateInterval;
	}
	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}
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
	
	public static class MarqueeText{
		private int textId;
		private String textVal;
		private float duration;
		private int ordering;
		public int getTextId() {
			return textId;
		}
		public void setTextId(int textId) {
			this.textId = textId;
		}
		public String getTextVal() {
			return textVal;
		}
		public void setTextVal(String textVal) {
			this.textVal = textVal;
		}
		public float getDuration() {
			return duration;
		}
		public void setDuration(float duration) {
			this.duration = duration;
		}
		public int getOrdering() {
			return ordering;
		}
		public void setOrdering(int ordering) {
			this.ordering = ordering;
		}
	}
}
