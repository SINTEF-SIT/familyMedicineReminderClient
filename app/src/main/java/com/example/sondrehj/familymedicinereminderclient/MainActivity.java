package com.example.sondrehj.familymedicinereminderclient;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MedicationCabinetFragment.OnFragmentInteractionListener,
        AccountAdministrationFragment.OnFragmentInteractionListener, NewReminderFragment.OnFragmentInteractionListener,
        ReminderFragment.OnFragmentInteractionListener, MedicationListFragment.OnListFragmentInteractionListener,
        WelcomeFragment.OnFragmentInteractionListener, MedicationStorageFragment.OnFragmentInteractionListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        changeFragment(new MedicationCabinetFragment());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    public void changeFragment(Fragment fragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_reminders) {

            changeFragment(new ReminderFragment());

        } else if (id == R.id.nav_medication) {

            changeFragment(new MedicationCabinetFragment());

        } else if (id == R.id.nav_symptoms) {

        } else if (id == R.id.nav_settings) {

        } /*else if (id == R.id.nav_newReminder) {

            Fragment newFragment = new NewReminderFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack if needed
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(Medication item) {

    }

    @Override
    public void onAccountAdminFragmentInteraction(Uri uri) {

    }

    @Override
    public void onReminderFragmentInteraction(Uri uri) {

    }

    @Override
    public void onNewReminderFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMedicationCabinetFragmentInteraction(Uri uri) {

    }

    @Override
    public void onWelcomeFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMedicationStorageFragmentInteraction(Uri uri) {

    }

    @Override
    public void addMedicationToMedicationList(Medication medication) {

        System.out.println(medication);

    }
}
