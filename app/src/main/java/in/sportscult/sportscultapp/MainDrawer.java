package in.sportscult.sportscultapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import in.sportscult.sportscultapp.Utils.ExpandAndCollapseViewUtil;
import in.sportscult.sportscultapp.fragments.AboutSFLFragment;
import in.sportscult.sportscultapp.fragments.AboutUsFragment;
import in.sportscult.sportscultapp.fragments.HelpFragment;
import in.sportscult.sportscultapp.fragments.ResultsFragment;
import in.sportscult.sportscultapp.fragments.RulesFragment;
import in.sportscult.sportscultapp.fragments.SettingsFragment;
import in.sportscult.sportscultapp.fragments.TabFragment;

public class MainDrawer extends AppCompatActivity {
    public static final String SETTINGS = "settings" ;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    ViewGroup linearLayoutDetails;
    ImageView imageViewExpand;
    private static final int DURATION = 250;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);


        FirebaseMessaging.getInstance().subscribeToTopic("news");
       // Toast.makeText(this, "news", Toast.LENGTH_SHORT).show();

        readSettings();
/**
 *Setup the DrawerLayout and NavigationView
 */
             mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
             mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

             mFragmentManager = getSupportFragmentManager();
             mFragmentTransaction = mFragmentManager.beginTransaction();
             mFragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
        /**
         * Setup click events on the Navigation View Items.
         */

             mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                 int id = menuItem.getItemId();

                 if (id == R.id.nav_teams) {

                     Intent teamsIntent = new Intent(getBaseContext(), ListOfTeams.class);
                     startActivity(teamsIntent);

                 }else if (id == R.id.nav_home) {
                     FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                     fragmentTransaction.replace(R.id.containerView,new TabFragment()).addToBackStack( "tag" ).commit();
                 }  else if (id == R.id.nav_registration) {
                     FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                     fragmentTransaction.replace(R.id.containerView,new RegistrationFragment()).addToBackStack( "tag" ).commit();
                 } else if (id == R.id.nav_about) {
                     FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                     fragmentTransaction.replace(R.id.containerView,new AboutUsFragment()).addToBackStack( "tag" ).commit();
                 } else if (id == R.id.nav_rules) {
                     FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                     fragmentTransaction.replace(R.id.containerView,new RulesFragment()).addToBackStack( "tag" ).commit();
                 } else if (id == R.id.nav_about_sfl) {
                     FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                     fragmentTransaction.replace(R.id.containerView,new AboutSFLFragment()).addToBackStack( "tag" ).commit();
                 } else if (id == R.id.nav_help) {
                     FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                     xfragmentTransaction.replace(R.id.containerView,new HelpFragment()).addToBackStack( "tag" ).commit();
                 } else if (id == R.id.nav_match_details) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView,new ResultsFragment()).addToBackStack( "tag" ).commit();
                }else if (id == R.id.nav_settings) {
                     FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                     xfragmentTransaction.replace(R.id.containerView,new SettingsFragment()).addToBackStack( "tag" ).commit();
                 }

                 return false;
            }

        });



                android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
                ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

                mDrawerLayout.setDrawerListener(mDrawerToggle);

                mDrawerToggle.syncState();

    }

    /**
     * reads settings from shared preferences and subscribes to topics for notification
     */
    private void readSettings() {
        SharedPreferences sharedPref = getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        boolean live_match = sharedPref.getBoolean(getString(R.string.live_match),  true);
        boolean live_score = sharedPref.getBoolean(getString(R.string.live_score),  true);

        if(live_match) FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.live_match));
        if(live_score) FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.live_score));


    }

    /**
     * Launches E-mail intent to send email using Installed email client
     * Called from HelpFragment
     * @param v The view that launches the intent
     */
    public void sendEmail(View v){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","sportscultprototype@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SFL Enquiry");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    /**
     * Expands and collapses the Request call card in help  fragment
     * @param view
     */
    public void toggleDetails(View view) {

        linearLayoutDetails = (ViewGroup) findViewById(R.id.linearLayoutDetails);
        imageViewExpand = (ImageView) findViewById(R.id.imageViewExpand);

        if (linearLayoutDetails.getVisibility() == View.GONE) {
            ExpandAndCollapseViewUtil.expand(linearLayoutDetails, DURATION);
            imageViewExpand.setImageResource(R.drawable.ic_expand_more_black_24dp);
            rotate(-180.0f);
        } else {
            ExpandAndCollapseViewUtil.collapse(linearLayoutDetails, DURATION);
            imageViewExpand.setImageResource(R.drawable.ic_expand_less_black_24dp);
            rotate(180.0f);
        }
    }

    /**
     * Animates the arrow button in Request a call card in HelpFragment
     * @param angle
     */
    private void rotate(float angle) {
        Animation animation = new RotateAnimation(0.0f, angle, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(DURATION);
        imageViewExpand.startAnimation(animation);
    }

    /**
     * Launches the google navigation app to show directions to qHub Football field
     * @param view
     */
    public void getDirections(View view){

        Uri gmmIntentUri = Uri.parse("google.navigation:q=qhub+by+Quantum+Sports");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");


        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }else {
            Toast.makeText(this, "No Application to View maps", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Launches google maps to pinpoint qHub football field on a map
     * @param view
     */
    public void viewLocation(View view){
        Uri gmmIntentUri = Uri.parse("geo:28.4219738,77.1348673");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }else {
            Toast.makeText(this, "No Application to View maps", Toast.LENGTH_SHORT).show();
        }
    }

    public void setSettings(){
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putBoolean("live_match", true);
        editor.putBoolean("score", true);
        editor.commit();
    }

}