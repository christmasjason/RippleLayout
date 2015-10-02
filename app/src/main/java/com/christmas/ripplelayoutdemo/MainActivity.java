package com.christmas.ripplelayoutdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
  @Bind(R.id.tv_demo_one) TextView tvDemoOne;
  @Bind(R.id.tv_demo_two) TextView tvDemoTwo;
  @Bind(R.id.tv_demo_three) TextView tvDemoThree;
  @Bind(R.id.tv_demo_four) TextView tvDemoFour;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_main);
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.tv_demo_one)
  protected void demoOneClick() {
    Toast.makeText(this, "Demo one click.", Toast.LENGTH_SHORT).show();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.tv_demo_two)
  protected void demoTwoClick() {
    Toast.makeText(this, "Demo two click.", Toast.LENGTH_SHORT).show();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.tv_demo_three)
  protected void demoThreeClick() {
    Toast.makeText(this, "Demo three click.", Toast.LENGTH_SHORT).show();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.tv_demo_four)
  protected void demoFourClick() {
    Toast.makeText(this, "Demo four click.", Toast.LENGTH_SHORT).show();
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
}
