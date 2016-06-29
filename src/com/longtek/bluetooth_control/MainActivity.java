package com.longtek.bluetooth_control;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity 
{
	
	private Button Connect;
	private ImageButton Search;
	private ImageButton Next;
	private ImageButton Previous;
	private TextView NBox;
	private Button Plus;
	private Button Moins;
	private ProgressBar Volume;
	private TextView TextVolume;
	private ImageButton VolumeOn;
	private TextView Debug;
	
	private Fac_Manager m_Manager;
	private ArrayAdapter<String> BTArrayAdapter;
	
	private View.OnClickListener OnRefresh = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MainActivity.this.Launch_Connection();
		}
	};
	
	private View.OnClickListener OnSearch = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			MainActivity.this.m_Manager.Previous();
		}
	};
	
	
	public void ConnectionDone(boolean paramBoolean)
	{
		if (paramBoolean)
	    {
		//	this.Connect.setBackgroundColor(int color);
			this.Connect.setBackgroundColor(Color.parseColor("#ff99cc00"));       //设置连接按钮的背景色变化（由未连接时的灰色变成功连接时的绿色）
			String str = getString(R.string.ConnectedTo) + this.m_Manager.getM_Data().getM_Device().getName();
			this.Connect.setText(str);
			CleanBTArrayAdapter();
			return;
	    }
	    this.Connect.setBackgroundResource(R.color.green);
	    this.Connect.setText(R.string.ConnectToSoundCreator);
//	    Clean_NBox();
//	    updateVolume(0);
	}
	
	public void setBTArrayAdapter(ArrayAdapter<String> bTArrayAdapter) {
		this.BTArrayAdapter = bTArrayAdapter;
	}
	
	public ArrayAdapter<String> getBTArrayAdapter() {
		return BTArrayAdapter;
	}
	
	private boolean CleanBTArrayAdapter()
	{
		this.setBTArrayAdapter(new ArrayAdapter<String>(this, R.layout.activity_chooser_view_list_item));
	    return true;
	}
	
	public void GoHome()
	{
		
	}
	
	public void Launch_Connection()
	{
		startActivity(new Intent(this, Connect.class));
	}
	
	public void Launch_CanSettings()
	{
		startActivity(new Intent(this, CanSettings.class));
	}
	
	public void Launch_BoxSettings()
	{
		startActivity(new Intent(this, BoxSettings.class));
	}
	
	public void Launch_Demo()
	{
		startActivity(new Intent(this, Demo.class));
	}
	
	public void Launch_Help()
	{
		startActivity(new Intent(this, Help.class));
	}
	
	public void Launch_Logs()
	{
		startActivity(new Intent(this, Logs.class));
	}
	
	public void Launch_About()
	{
		startActivity(new Intent(this, About.class));
	}
	
	private void init()
	{
		this.Connect = (Button) findViewById(R.id.ButtonConnect);
		this.Search = ((ImageButton)findViewById(R.id.ButtonSearch));
		this.Next = ((ImageButton)findViewById(R.id.ButtonNext));
		this.Previous = ((ImageButton)findViewById(R.id.ButtonPrevious));
		this.NBox = ((TextView)findViewById(R.id.TextNBox));
		this.Plus = ((Button)findViewById(R.id.ButtonPlus));
		this.Moins = ((Button)findViewById(R.id.ButtonMoins));
		this.Volume = ((ProgressBar)findViewById(R.id.ProgressBarVolume));
		this.TextVolume = ((TextView)findViewById(R.id.TextVolume));
		this.VolumeOn = ((ImageButton)findViewById(R.id.ButtonVolume));
		this.Debug = ((TextView)findViewById(R.id.TextDebug));
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
		
		this.Connect.setOnClickListener(this.OnRefresh);
		this.Search.setOnClickListener(this.OnSearch);
//		this.Previous.setOnClickListener(this.OnPrevious);
//		this.Next.setOnClickListener(this.OnNext);
//		this.Plus.setOnClickListener(this.OnVolPlus);
//		this.Moins.setOnClickListener(this.OnVolMoins);
//		this.VolumeOn.setOnClickListener(this.OnVolumeOn);
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
	
}
