package com.syn.queuedisplay.pizzahut;

public class TakeAwayData {
	private String szTransName;
	private String szQueueName;
	private int iKdsStatusID;
	private String szKdsStatusName;
	private String szStartDateTime;
	private String szFinishDateTime;
	private int iMinuteTimeAfterPickup;
	
	public String getSzTransName() {
		return szTransName;
	}
	public void setSzTransName(String szTransName) {
		this.szTransName = szTransName;
	}
	public String getSzQueueName() {
		return szQueueName;
	}
	public void setSzQueueName(String szQueueName) {
		this.szQueueName = szQueueName;
	}
	public int getiKdsStatusID() {
		return iKdsStatusID;
	}
	public void setiKdsStatusID(int iKdsStatusID) {
		this.iKdsStatusID = iKdsStatusID;
	}
	public String getSzKdsStatusName() {
		return szKdsStatusName;
	}
	public void setSzKdsStatusName(String szKdsStatusName) {
		this.szKdsStatusName = szKdsStatusName;
	}
	public String getSzStartDateTime() {
		return szStartDateTime;
	}
	public void setSzStartDateTime(String szStartDateTime) {
		this.szStartDateTime = szStartDateTime;
	}
	public String getSzFinishDateTime() {
		return szFinishDateTime;
	}
	public void setSzFinishDateTime(String szFinishDateTime) {
		this.szFinishDateTime = szFinishDateTime;
	}
	public int getiMinuteTimeAfterPickup() {
		return iMinuteTimeAfterPickup;
	}
	public void setiMinuteTimeAfterPickup(int iMinuteTimeAfterPickup) {
		this.iMinuteTimeAfterPickup = iMinuteTimeAfterPickup;
	}
}
