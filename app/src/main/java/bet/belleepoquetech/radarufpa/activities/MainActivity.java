package bet.belleepoquetech.radarufpa.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.WindowManager;

import bet.belleepoquetech.radarufpa.fragments.FeedFragment;
import bet.belleepoquetech.radarufpa.fragments.MapaFragment;
import bet.belleepoquetech.radarufpa.fragments.ProfileFragment;
import bet.belleepoquetech.radarufpa.R;

public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Perfil");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.user);
        tabLayout.getTabAt(1).setIcon(android.R.drawable.ic_dialog_map);
        tabLayout.getTabAt(2).setIcon(android.R.drawable.ic_dialog_info);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        toolbar.setTitle("Perfil");
                        break;
                    case 1:
                        toolbar.setTitle("Mapas");
                        break;
                    case 2:
                        toolbar.setTitle("Feed");
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if(id== R.id.action_logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Deseja mesmo sair do RadarUFPa")
                    .setPositiveButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences mSharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.SharedPreferences),MODE_PRIVATE);;
                            SharedPreferences.Editor edt = mSharedPreferences.edit();
                            edt.clear();
                            edt.apply();
                            System.exit(0);
                        }
                    });

            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return ProfileFragment.newInstance();
                case 1:
                    return MapaFragment.newInstance();
                case 2:
                    return FeedFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
                //case 3:
                  //  return "";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deseja mesmo sair do RadarUFPa")
                .setPositiveButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                    }
                });

        builder.create().show();
    }
}
