package com.longtek.bluetooth_control;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class BoxSettings extends Activity {

	private ArrayAdapter<String> BoxArrayAdapter;
	private Button DeleteAllBox;
	private ListView ListBox;
	private Button LoadBox;
	private Boolean IsBoxLoaded = true;
	
	private View.OnClickListener OnLoadBox = new View.OnClickListener()
	{
		public void onClick(View view)
	    {
			Intent intent = new Intent("android.intent.action.GET_CONTENT");
			intent.setType("*/*");
			intent.addCategory("android.intent.category.OPENABLE");
			BoxSettings.this.startActivityForResult(Intent.createChooser(intent, "Choose Box file"), 4);
	    }
	};
	
	private View.OnClickListener OnDeleteAllBox = new View.OnClickListener()
	{
	    public void onClick(View view)
	    {
//	      new DialogDelete().show(BoxSettings.this.getSupportFragmentManager(), "DialogDelete");
	    }
	};
	
	public void BoxFlashed(boolean IsBoxLoaded)
	{
		if (IsBoxLoaded)
	    {
			Toast.makeText(this, R.string.TransferComplete, 0).show();
			return;
	    }
	    Toast.makeText(this, R.string.TransferFailed, 1).show();
	}
	
	public void BoxLoaded(boolean IsBoxLoaded)
	{
	    if (!IsBoxLoaded)
	      Toast.makeText(this, R.string.TransferFailed, 1).show();
	}
	
	//显示可用的Box文件列表
	public void BoxListAvailable(String[] ArrayOfString)
	{
		CleanBoxArrayAdapter();
	    int i = 0;
	    while (true)
	    {
	    	if (i >= ArrayOfString.length)
	    	{
	    		this.ListBox.setAdapter(this.BoxArrayAdapter);
	    		return;
	    	}
	    	appendBoxArrayAdapter(ArrayOfString[i]);
	    	i += 1;
	    }
	}
	
	private boolean CleanBoxArrayAdapter()
	{
	    this.BoxArrayAdapter = new ArrayAdapter(this, 17367043);
	    this.ListBox.setAdapter(this.BoxArrayAdapter);
	    return true;
	}
	
	public boolean appendBoxArrayAdapter(String string)
	{
		this.BoxArrayAdapter.add(string);
		return true;
	}
	
	public void updateNBox(int Int)
	{
		
	}
	
	public void init()
	{
		this.LoadBox = ((Button)findViewById(R.id.ButtonBrowseBoxBox));
		this.DeleteAllBox = ((Button)findViewById(R.id.ButtonDeleteBoxBox));
		this.BoxArrayAdapter = new ArrayAdapter(this, R.layout.activity_chooser_view_list_item);
		this.ListBox = ((ListView)findViewById(R.id.listBoxBox));
		this.ListBox.setChoiceMode(1);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boxsettings);
		
		init();
		this.LoadBox.setOnClickListener(this.OnLoadBox);
		
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
		case R.id.menu_boxsettings:
			Launch_BoxSettings();
			break;
		case R.id.menu_help:
			Launch_Help();
			break;
		case R.id.menu_logs:
			Launch_Logs();
			break;
		case R.id.menu_about:
			Launch_About();
			break;
		case R.id.memu_demo:
			Launch_Demo();
			break;
		default:
			return super.onOptionsItemSelected(item);
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
