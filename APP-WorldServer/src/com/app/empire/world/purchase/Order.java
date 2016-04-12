package com.app.empire.world.purchase;

public class Order {
	private int status;
	private String msssage;
	private Receipt receipt;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsssage() {
		return msssage;
	}

	public void setMsssage(String msssage) {
		this.msssage = msssage;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}
}
