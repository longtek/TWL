package com.longtek.bluetooth_control;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CanSettings extends Activity {

	private Button CANSettings;
	private TextView CanRPM;
	private	TextView CanSpeed;
	private TextView CanThrottle;
	private TextView CcgFile;
	private ToggleButton m_SpyOnOff;
	
	public static final int REQUEST_CODE = 1000;    //选择文件   请求码
	public static final String SEND_FILE_NAME = "sendFileName";

	private View.OnClickListener OnCANSettings = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			Intent intent= new Intent("android.intent.action.GET_CONTENT");
		    intent.setType("*/*");
		    intent.addCategory("android.intent.category.OPENABLE");
		    CanSettings.this.startActivityForResult(Intent.createChooser(intent, "Choose Ccg file"), 1000);
		}
	};
	private String m_CcgName;
	
	public void CcgFlashed(boolean paramBoolean)
	{
		if (paramBoolean)
	    {
			this.CcgFile.setText(this.getCcgFileName());
			Toast.makeText(this, 2131361824, 0).show();
			return;
	    }
	    Toast.makeText(this, 2131361825, 1).show();
	}
	
	public CharSequence getCcgFileName()
	{
		return this.getM_CcgName();
	}

	public String getM_CcgName()
	{
		return this.m_CcgName;
	}
	 
	public void CcgLoaded(boolean paramBoolean)
	{
	}
	
	public void SpyOnOff(boolean paramBoolean)
	{
		if (paramBoolean)
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
	
	public void UpdateRPMSpeedThrottle(int paramInt1, int paramInt2, int paramInt3)
	{
		String str = String.format("%d", new Object[] { Integer.valueOf(paramInt1) });
	    this.CanRPM.setText(str);
	    str = String.format("%d", new Object[] { Integer.valueOf(paramInt2) });
	    this.CanSpeed.setText(str);
	    str = String.format("%d", new Object[] { Integer.valueOf(paramInt3) });
	    this.CanThrottle.setText(str);
	}
	
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
	 * 6月21日 changed，返回选择Ccg文件的结果
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//请求码和结果码同时为REQUEST_CODE时，处理特定的结果
		if(requestCode == 1000 & resultCode == 1000){
			//请求为 "选择文件"
			try {
				//取得选择的文件名
				String CcgFileName = data.getStringExtra(SEND_FILE_NAME);
				CcgFile.setText(CcgFileName);
			} catch (Exception e) {				
			}
		}	
	}
}
