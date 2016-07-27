package com.longtek.bluetooth_control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * CanSettings类，用于选择发送Ccg文件，显示控制盒回传的CAN数据（RPM、Speed、Throttle）
 * @author TWL
 * */
public class CanSettings extends Activity {

	private Button CANSettings;			//设置CAN配置文件按钮
	private TextView CanRPM;			//发动机转速
	private	TextView CanSpeed;			//车速
	private TextView CanThrottle;		//油门开度
	private TextView CcgFile;
	private ToggleButton m_SpyOnOff;		//数据开关
	
	private Fac_Manager m_Manager;
	public static final int REQUEST_CODE = 1000;    //选择文件 请求码
	public static final String SEND_FILE_NAME = "sendFileName";
	private static final String BASEDIR = "SoundCreator";
	
	//创建CAN设置按钮点击响应事件监听器
	private View.OnClickListener OnCANSettings = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			//读取手机存储，选取需要加载的Ccg文件
			Intent intent= new Intent("android.intent.action.GET_CONTENT");
		    intent.setType("*/*");
		    intent.addCategory("android.intent.category.OPENABLE");
		    CanSettings.this.startActivityForResult(Intent.createChooser(intent, "Choose Ccg file"), REQUEST_CODE);
		}
	}; 

	//创建数据显示开关点击响应事件监听器
	  private View.OnClickListener OnSpyOnOff = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(!CanSettings.this.m_Manager.IsConnected())
			{
				((MainActivity) CanSettings.this.m_Manager.getM_Connect()).PleaseDoConnection();
				CanSettings.this.m_SpyOnOff.setChecked(false);
				return ;
			}
			if (CanSettings.this.m_SpyOnOff.isChecked())
			{
				CanSettings.this.m_Manager.SpyOn();
				return ;
			}
			CanSettings.this.m_Manager.SpyOff();
		}
	};
	
	/* public static String read(String name) {
	        File sdcard = Environment.getExternalStorageDirectory();
	        String sdcardPath = sdcard.getPath();
	        File file = new File(sdcardPath + "/"+BASEDIR+"/" + name + ".ccg");
	        StringBuilder text = new StringBuilder();
	        try {
	            BufferedReader br = new BufferedReader(new FileReader(file));
	            String line;
	            while ((line = br.readLine()) != null) {
	                text.append(line);
	                text.append('\n');
	            }
	            br.close();
	            return text.toString();
	        }
	        catch (IOException e) {
	            return e.getMessage();
	        }
	    }*/
	
	public String read()
	{
		try {
			//如果手机插入了SD卡，而且应用程序具有访问SD的权限
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				//获取SD卡对应的存储目录
				File sdCardDir = Environment.getExternalStorageDirectory();
				System.out.println("----------------" + sdCardDir);
				//获取指定文件的对应输入流
				FileInputStream fis = new FileInputStream(
						sdCardDir.getCanonicalPath() + SEND_FILE_NAME);
				//将指定输入流包装成BufferedReader
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				StringBuffer sb = new StringBuffer("");
				String line = null;
				//循环读取文件内容
				while((line = br.readLine()) != null)
				{
					sb.append(line);
				}
				//关闭资源
				br.close();
				return sb.toString();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String m_CcgName;
	
	public void CcgFlashed(boolean isConnected)
	{
		if (isConnected)
	    {
			this.CcgFile.setText(this.getCcgFileName());
			Toast.makeText(this, R.string.TransferComplete, 0).show();
			return;
	    }
	    Toast.makeText(this, R.string.TransferFailed, 1).show();
	}
	
	public CharSequence getCcgFileName()
	{
		return this.getM_CcgName();
	}

	public String getM_CcgName()
	{
		return this.m_CcgName;
	}
	 
	public void CcgLoaded(boolean isConnected)
	{
	}
	
	public void SpyOnOff(boolean isConnected)
	{
		if (isConnected)
	    {
			Toast.makeText(this, "Spy On", 1).show();
			this.m_SpyOnOff.setBackgroundColor(-16711936);
			return;
	    }
	    Toast.makeText(this, "Spy Off", 1).show();
	    this.m_SpyOnOff.setBackgroundResource(17301508);
	    this.CanRPM.setText("");
	    this.CanSpeed.setText("");
	    this.CanThrottle.setText("");
	}
	
	public void UpdateRPMSpeedThrottle(int RPM, int Speed, int Throttle)
	{
		String rpm1 = String.format("%d", new Object[] { Integer.valueOf(RPM) });
	    this.CanRPM.setText(rpm1);
	    String speed1 = String.format("%d", new Object[] { Integer.valueOf(Speed) });
	    this.CanSpeed.setText(speed1);
	    String throttle1 = String.format("%d", new Object[] { Integer.valueOf(Throttle) });
	    this.CanThrottle.setText(throttle1);
	}
	//初始化该Activity的全部UI组件
	public void init()
	{
		this.CANSettings = (Button) findViewById(R.id.ButtonBrowseCcgCan);
		this.CanRPM = (TextView) findViewById(R.id.textViewRPMCan);
		this.CanSpeed = (TextView) findViewById(R.id.textViewSpeedCan);
		this.CanThrottle = (TextView) findViewById(R.id.textViewThrottleCan);
		this.CcgFile = (TextView) findViewById(R.id.TextCcgFileCan);
		this.m_SpyOnOff = (ToggleButton) findViewById(R.id.buttonSpyOnOffCan);
		this.m_SpyOnOff.setTextOff(getResources().getString(R.string.SpyOff));
		this.m_SpyOnOff.setTextOn(getResources().getString(R.string.SpyOn));
		this.m_SpyOnOff.setChecked(false);
	}
	
	public void ActivityFinish()
	{
		finish();
	}
	
	public void GoHome()
	{
		finish();
	}
	/**
	 * 以下是各个子菜单之间相互跳转的函数
	 * */
	public void Launch_Connection()
	{
		startActivity(new Intent(this, Connect.class));
		ActivityFinish();
	}
	
	public void Launch_CanSettings()
	{
		
	}
	public void Launch_BoxSettings()
	{
		startActivity(new Intent(this, BoxSettings.class));
		ActivityFinish();
	}
	
	public void Launch_Demo()
	{
		startActivity(new Intent(this, Demo.class));
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cansettings);
		
		init();
		this.CANSettings.setOnClickListener(this.OnCANSettings);
		this.m_SpyOnOff.setOnClickListener(OnSpyOnOff);
		this.m_Manager = Fac_Manager.getManager();
		
		   
		//读取选择的Ccg文件
		System.out.println(SEND_FILE_NAME);
		Log.i("选取文件的内容", read());
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
	 
		//通过菜单项的ID响应每个菜单
				switch (item.getItemId())
				{
				case R.id.menu_home:
					GoHome();
					break;
				case R.id.menu_connnection:
					Launch_Connection();
					break;
				case R.id.memu_demo:
					Launch_Demo();
					break;
				case R.id.menu_boxsettings:
					Launch_BoxSettings();
					break;
				case R.id.menu_cansettings:
					Launch_CanSettings();
					break;
				case R.id.menu_help:
					Launch_Help();
					break;
				case R.id.menu_about:
					Launch_About();
					break;
				case R.id.menu_logs:
					Launch_Logs();
					break;
				default:
					return super.onOptionsItemSelected(item);		//对没有处理的事件交给父类处理
				}
				return true;		//返回true表示处理完菜单项的点击事件，不需要将事件传播
				
	}
	/**
	 * Activity回调函数，用于返回选择Ccg文件的结果
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//请求码和结果码同时为REQUEST_CODE时，处理特定的结果
//		if(requestCode == 1000 & resultCode == 1000){
//			//请求为 "选择文件"
//			try {
//				//取得选择的文件名
//				String CcgFileName = data.getStringExtra(SEND_FILE_NAME);
//				CcgFile.setText(CcgFileName);
//			} catch (Exception e) {				
//			}
//		}	
		
		if(requestCode != -1)
		{
			switch(requestCode)
			{
			case 1000:
				Uri uri = getIntent().getData();
				if (!uri.getPath().substring(uri.getPath().lastIndexOf(".") + 1).equals("ccg"))     //截取uri中的最后一个“.”后面为“ccg‘的内容
				{
					Toast toast = Toast.makeText(this, "Error: Please select a .ccg file", 1);
					toast.setGravity(17, 0, 0);
					toast.show();
					return;
				}
				this.m_Manager.LoadCcg(uri);
				
				//请求为 "选择文件"
				try {
					//取得选择的文件名
					String CcgFileName = data.getStringExtra(SEND_FILE_NAME);
					CcgFile.setText(CcgFileName);
				} catch (Exception e) {	
					 e.printStackTrace();  
				}
				break;
			}
		}
	}
}
