package com.csc413.sfsu.csc413_parking;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Button go_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
          /*
            The "continue" button from the splash screen
         */
        go_start = (Button)findViewById(R.id.go_start);
        go_start.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        called from "onClick" method starts the Map_Activity
     */
    private void go_Start_Click(){
        startActivity(new Intent("com.google.android.gms.maps.MapFragment"));
    }

    /*
        gets the button response and re-routes to the method for
        the button action method: "go_Start_Click()"
     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.go_start:
                go_Start_Click();
                break;

        }
    }


}
