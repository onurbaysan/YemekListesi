package com.stall10n.yemeklistesi.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.PagerTitleStrip;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private static String newline = System.getProperty("line.separator");

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.app.ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffbe0009")));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        PagerTitleStrip titleStrip = (PagerTitleStrip) findViewById(R.id.titleStrip);
        titleStrip.setTextColor(Color.BLACK);
        titleStrip.offsetLeftAndRight(20);
        titleStrip.setBackgroundColor(Color.LTGRAY);
        titleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

        ReadMenus();
    }



    public void ReadMenus()
    {
        Calendar c = new GregorianCalendar();
        int week = c.get(Calendar.WEEK_OF_YEAR);
        File dir = new File(this.getFilesDir() + "/weeks/");
        File file = new File(dir, "week" + week + ".bin");

        if (!file.exists()) {
            if (!isConnected()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Lütfen internet bağlantınızı" + newline + "kontrol ediniz ve tekrar deneyiniz.")
                        .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create();
                builder.show();
                return;
            }
        }

        if(!dir.exists())
            dir.mkdirs();


        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.progressText));
        Parser.initializeList(file,progress);
        SelectWeekOfDayTab();

        /*
        Show progressDialog 1 sec without any locking on UI Thread.
        http://stackoverflow.com/a/3380394/343973
         */
        new Handler().postDelayed(new Runnable() {
            public void run() {
                progress.dismiss();
            }
        }, 1000);
    }


    public boolean isConnected()
    {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        } catch (Exception ex)
        { }
        return false;
    }

    protected void showAbout() {
        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        TextView textView = (TextView) messageView.findViewById(R.id.about_credits);
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setMessage("test leeennn");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();
                    }
                });

        builder.setView(messageView);
        builder.create();
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about)
        {
            showAbout();
        }
        if(id == R.id.action_refresh)
        {
            Utils.DeleteAllFilesInDirectory(new File(this.getFilesDir() + "/weeks/"));
            ReadMenus();
        }
        return super.onOptionsItemSelected(item);
    }

    public void SelectWeekOfDayTab()
    {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        mViewPager.setCurrentItem(dayOfWeek - 1);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 7 total pages.
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position)
            {
                case 0:
                    return getString(R.string.title_section7);
                case 1:
                    return getString(R.string.title_section1);
                case 2:
                    return getString(R.string.title_section2);
                case 3:
                    return getString(R.string.title_section3);
                case 4:
                    return getString(R.string.title_section4);
                case 5:
                    return getString(R.string.title_section5);
                case 6:
                    return getString(R.string.title_section6);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            int selected = getArguments().getInt(ARG_SECTION_NUMBER);

            DailyMenu currentDay;
            String data = "";

            try
            {
                currentDay = Parser.menuList.get(selected-1);

                StringBuilder sb = new StringBuilder();
                sb.append("<big><p><b><font color='red'><u>Standart</u>: </font></b><br>");
                sb.append(currentDay.getStandard_menu().replace(",","<br>"));
                sb.append("<br><br><b><font color='red'><u>Diyet</u>: </font></b><br>");
                sb.append(currentDay.getDiet_menu().replace(",","<br>") + "</p></big>");
                data = sb.toString();

            }
            catch (Exception ex)
            {
                data = ex.getMessage();
            }
            textView.setText(Html.fromHtml(data));
            return rootView;
        }
    }

}
