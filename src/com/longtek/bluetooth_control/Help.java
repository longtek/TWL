package com.longtek.bluetooth_control;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

public class Help extends Activity {

	
	private ViewFlipper m_ViewFlipperHelp;
	private float m_Xpos;
	private Animation slide_in_from_left;
	private Animation slide_in_from_right;
	private Animation slide_out_to_left;
	private Animation slide_out_to_right;
	
	
	public void init()
	{
		this.m_ViewFlipperHelp = (ViewFlipper) findViewById(R.id.viewflipperHelp);
		this.slide_in_from_left = AnimationUtils.loadAnimation(this, R.anim.in_from_left);
		this.slide_in_from_right = AnimationUtils.loadAnimation(this, R.anim.in_from_right);
		this.slide_out_to_left = AnimationUtils.loadAnimation(this, R.anim.out_to_left);
		this.slide_out_to_right = AnimationUtils.loadAnimation(this, R.anim.out_to_right);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
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
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
		case R.id.menu_home:
			GoHome();
			break;
		case R.id.menu_connnection:
			Launch_Connection();
			break;
		case R.id.menu_cansettings:
			Launch_CanSettings();
			break;
		case R.id.menu_help:
			Launch_Help();
			break;
		case R.id.menu_boxsettings:
			Launch_BoxSettings();
			break;
		case R.id.menu_logs:
			Launch_Logs();
			break;
		case R.id.menu_about:
			Launch_About();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
		
	}

//	@Override
//	public boolean onTrackballEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		return super.onTrackballEvent(event);
//	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
	    {
		case MotionEvent.ACTION_DOWN:
			Log.d("MotionEvent", "ACTION_DOWN");
			break;

		case MotionEvent.ACTION_MOVE:
			 
			this.m_Xpos = event.getX();
	    	this.m_ViewFlipperHelp.getDisplayedChild();
	      
	    	if (event.getX() > this.m_Xpos)
	    	{
	    		if (this.m_ViewFlipperHelp.getDisplayedChild() != 0)
	    		{
	    			this.m_ViewFlipperHelp.setInAnimation(this.slide_in_from_left);
	    			this.m_ViewFlipperHelp.setOutAnimation(this.slide_out_to_right);
	    			this.m_ViewFlipperHelp.showPrevious();
	    		}
	    	}
	    	else if (this.m_ViewFlipperHelp.getDisplayedChild() != this.m_ViewFlipperHelp.getChildCount() - 1)
	    	{
	    		this.m_ViewFlipperHelp.setInAnimation(this.slide_in_from_right);
	    		this.m_ViewFlipperHelp.setOutAnimation(this.slide_out_to_left);
	    		this.m_ViewFlipperHelp.showNext();
	    	}
	    	break;
		case MotionEvent.ACTION_UP:
			Log.d("MotionEvent", "ACTION_UP");
			break;
		default:
			break;
	    }
	    	return true;
	}
	 
	public void ActivityFinish()
	{
		finish();
	}
	
	public void GoHome()
	{
		ActivityFinish();
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
	
	public void Launch_Demo()
	{
		startActivity(new Intent(this, Demo.class));
		ActivityFinish();
	}
	
	public void Launch_Help()
	{
		 
	}
	
	public void Launch_Logs()
	{
		startActivity(new Intent(this, Log.class));
		ActivityFinish();
	}
	
	public void Launch_About()
	{
		startActivity(new Intent(this, About.class));
		ActivityFinish();
	}
	
}
