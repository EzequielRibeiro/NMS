package com.portaladdress.nms;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.google.firebase.analytics.FirebaseAnalytics;
import com.portaladdress.nms.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffffff"));
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.inters_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Glyphs glyphs = new Glyphs();
                String g = glyphs.getCoordsDetails("0125:007C:08FF:00D9");
               Log.e("portal:", g);

            }
        });
    }

    public void onResume() {
        super.onResume();
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }

    public void onBackPressed(){
        super.onBackPressed();
        if(mInterstitialAd.isLoaded())
            mInterstitialAd.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch(item.getItemId()){
            case R.id.menuAbout:
                Toast.makeText(this, "You clicked about", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuPrivacy:
                intent = new Intent(getBaseContext(), PrivacyPolicyHelp.class);
                startActivity(intent);
                break;

            case R.id.menuHelp:
                intent = new Intent(getBaseContext(), HelpActivity.class);
                startActivity(intent);
                break;

        }
        return true;
    }


}