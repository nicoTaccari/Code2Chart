package com.example.proyectofinal.code2chart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import fragmentsAboutApp.Code2ChartFragment;
import fragmentsAboutApp.EscalableFragment;
import fragmentsAboutApp.MultiplataformaFragment;
import fragmentsAboutApp.ProductividadFragment;

public class SobreAppActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private int[] layouts;
    private Button next, skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre_app);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(viewListener);

        layouts = new int[]{R.layout.fragment_code2_chart2,
                R.layout.fragment_productividad,
                R.layout.fragment_escalable,
                R.layout.fragment_multiplataforma};

        skip = (Button)findViewById(R.id.btn_skip);
        next = (Button)findViewById(R.id.btn_next);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = mViewPager.getCurrentItem() - 1;
                if(current!=(-1)){
                    mViewPager.setCurrentItem(current);
                }else{
                    finish();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = mViewPager.getCurrentItem() + 1;
                if(current<layouts.length){
                    mViewPager.setCurrentItem(current);
                }else{
                    finish();
                }
            }
        });

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

        public PlaceholderFragment() {
        }

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
            switch(position){
                case 0:
                    Code2ChartFragment code2ChartFragment = new Code2ChartFragment();
                    return code2ChartFragment;
                case 1:
                    ProductividadFragment productividadFragment = new ProductividadFragment();
                    return productividadFragment;
                case 2:
                    EscalableFragment escalableFragment = new EscalableFragment();
                    return escalableFragment;
                case 3:
                    MultiplataformaFragment multiplataformaFragment = new MultiplataformaFragment();
                    return multiplataformaFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position==layouts.length-1){
                next.setText("FINALIZAR");
                skip.setText("ATRÁS");
            }else if(position==0){
                next.setText("SIGUIENTE");
                skip.setText("SALIR");
            }else{
                next.setText("SIGUIENTE");
                skip.setText("ATRÁS");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


}
