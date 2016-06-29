package com.longtek.bluetooth_control;

import android.os.Handler;

public class Fac_Handler extends Handler{
	
	private Fac_Manager m_manager;
	  
	public Fac_Handler(Fac_Manager fac_Manager)
	{
		this.m_manager = fac_Manager;
	}

}
