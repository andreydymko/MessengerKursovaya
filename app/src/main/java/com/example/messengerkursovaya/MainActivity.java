package com.example.messengerkursovaya;

import android.content.Intent;
import android.os.Bundle;

import com.example.messengerkursovaya.DialogList.DialogData;
import com.example.messengerkursovaya.DialogList.DialogListFragment;
import com.example.messengerkursovaya.MessagingActivity.MessagingActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnClickListener,
                                                        NavigationView.OnNavigationItemSelectedListener,
                                                        DialogListFragment.OnListFragmentInteractionListener{

    private static final String TAG = "Main Activity";

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser currentUser;

    private TextView headerUserName;
    private ImageView headerUserPhoto;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_log_out,
                R.id.nav_tools)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //navigation layout and elements
        View navigationLayout = navigationView.getHeaderView(0);
        headerUserName = navigationLayout.findViewById(R.id.UserName);
        headerUserPhoto = navigationLayout.findViewById(R.id.UserPhoto);

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_log_out).getActionView()
                .findViewById(R.id.button_logout)
                .setOnClickListener(this);
        menu.findItem(R.id.nav_new_dialog).getActionView()
                .findViewById(R.id.button_nav_create_chat)
                .setOnClickListener(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        setUserInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_nav_create_chat:
            case R.id.fab:
                startActivity(new Intent(this, ChatCreateActivity.class));
                break;
            case R.id.button_logout:
                logOutUser();
                break;
            default:
                break;
        }
    }

    private void setUserInfo() {
        if (currentUser == null) return;

        String name = UtilsClass.getCurrUserEmail();
        if(name != null) {
            headerUserName.setText(name);
        }

        if (headerUserPhoto.getDrawable() == null) {
            headerUserPhoto.setImageResource(R.drawable.ic_account_circle_24px);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser == null)
            sendUserToLoginActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Initialises left navigation menu
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        return true;
    }

    @Override
    public void onListFragmentInteraction(DialogData data){
        Intent msgActivityIntent = new Intent(this, MessagingActivity.class);
        msgActivityIntent.putExtra("dialogId", data.getId());
        msgActivityIntent.putExtra("dialogTitle", data.getTitle());
        startActivity(msgActivityIntent);
    }

    private void logOutUser() {
        FirebaseAuth.getInstance().signOut();
        sendUserToLoginActivity();
    }

    private void sendUserToLoginActivity(){
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }
}
