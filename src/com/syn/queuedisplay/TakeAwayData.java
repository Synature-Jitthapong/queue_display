package com.syn.queuedisplay;

public class TakeAwayData {
	private String szTransName;
	private String szQueueName;
	private int iKdsStatusID;
	private String szKdsStatusName;
	private String szStartDateTime;
	
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
}
