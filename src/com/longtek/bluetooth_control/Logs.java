package com.longtek.bluetooth_control;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class Logs extends Activity{

	private Button m_ClearLogs;
	private TextView m_Logs;
	/**
	 *6.22加入Log显示 
	 **/
	private String smsg = "";    	//显示用数据缓存
	private String fmsg = "";    	//保存用数据缓存
	private InputStream is;			//输入流，用来接收蓝牙数据
//	private TextView dis;       	//接收数据显示句柄
	private ScrollView sv;     		//翻页句柄
	boolean bRun = true;
	
	//接收数据线程
    Thread ReadThread=new Thread(){
    	
    	public void run(){
    		int num = 0;
    		byte[] buffer = new byte[1024];
    		byte[] buffer_new = new byte[1024];
    		int i = 0;
    		int n = 0;
    		bRun = true;
    		//接收线程
    		while(true){
    			try{
    				while(is.available()==0){
    					while(bRun == false){}
    				}
    				while(true){
    					num = is.read(buffer);         //读入数据
    					n=0;
    					
    					String s0 = new String(buffer,0,num);
    					fmsg+=s0;    //保存收到数据
    					for(i=0;i<num;i++){
    						if((buffer[i] == 0x0d)&&(buffer[i+1]==0x0a)){
    							buffer_new[n] = 0x0a;
    							i++;
    						}else{
    							buffer_new[n] = buffer[i];
    						}
    						n++;
    					}
    					String s = new String(buffer_new,0,n);
    					smsg+=s;   //写入接收缓存
    					if(is.available()==0)break;  //短时间没有数据才跳出进行显示
    				}
    				//发送显示消息，进行显示刷新
    					handler.sendMessage(handler.obtainMessage());       	    		
    	    		}catch(IOException e){
    	    		}
    		}
    	}
    };
    
  //消息处理队列
    Handler handler= new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		m_Logs.setText(smsg);   //显示数据 
    		sv.scrollTo(0,m_Logs.getMeasuredHeight()); //跳至数据最后一页
    	}
    };
    
	public void init()
	{
		m_ClearLogs = (Button) findViewById(R.id.buttonClearLogs);
		m_Logs = (TextView) findViewById(R.id.textViewLogs);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log);
		
		init();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		//运行时，参数Menu其实就是MenuBuilder对象  
        Log.d("MainActivity", "menu--->" + menu);  
          
        /*利用反射机制调用MenuBuilder的setOptionalIconsVisible方法设置mOptionalIconsVisible为true， 
         * 给菜单设置图标时才可见 
         */  
        setIconEnable(menu, true);
          
        return super.onCreateOptionsMenu(menu);  
	}
	
	//enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效 
	private void setIconEnable(Menu menu, boolean enable)  
    {  
        try   
        {  
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");  
            Method method = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);  
            method.setAccessible(true);  
              
            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)  
            method.invoke(menu, enable);  
              
        } catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
    }  


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
//		case R.id.menu_home:
//			GoHome();
//			break;
		case R.id.menu_connnection:
			Launch_Connection();
			break;
//		case R.id.menu_cansettings:
//			Launch_CanSettings();
//			break;
//		case R.id.menu_boxsettings:
//			Launch_BoxSettings();
//			break;
//		case R.id.menu_help:
//			Launch_Help();
//			break;
//		case R.id.menu_logs:
//			Launch_Logs();
//			break;
//		case R.id.menu_about:
//			Launch_About();
//			break;
//		case R.id.menu_demo:
//			Launch_Demo();
//			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void ActivityFinish()
	{
		finish();
	}
	
	public void GoHome()
	{
		finish();
	}
	
	public void Launch_Connection()
	{
		startActivity(new Intent(this, Connect.class));
		ActivityFinish();
	}
	
	public void Launch_CanSettings()
	{
		startActivity(new Intent(this, CanSettings.class));
		ActivityFinish();
	}

	public void Launch_BoxSettings()
	{
		startActivity(new Intent(this, BoxSettings.class));
		ActivityFinish();
	}
	
	public void Launch_Help()
	{
		startActivity(new Intent(this, Help.class));
		ActivityFinish();
	}
	
	public void Launch_Logs()
	{
		startActivity(new Intent(this, Logs.class));
		ActivityFinish();
	}
	
	public void Launch_About()
	{
		startActivity(new Intent(this, About.class));
		ActivityFinish();
	}
	
	public void Launch_Demo()
	{
		startActivity(new Intent(this, Demo.class));
		ActivityFinish();
	}
}
