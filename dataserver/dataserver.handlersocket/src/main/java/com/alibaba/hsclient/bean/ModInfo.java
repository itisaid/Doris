package com.alibaba.hsclient.bean;

import com.alibaba.hsclient.util.ModOperator;

public class ModInfo {

	private ModOperator modType;
	private String[] mopValues;

	public ModOperator getModType() {
		return modType;
	}

	public void setModType(ModOperator modType) {
		this.modType = modType;
	}

	public String[] getMopValues() {
		return mopValues;
	}

	public void setMopValues(String[] mopValues) {
		this.mopValues = mopValues;
	}
}
