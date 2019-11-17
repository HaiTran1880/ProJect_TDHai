package com.example.movie;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.example.movie.inter_face.IOnClickItemCategories;
import com.example.movie.ui.categories.CategoriesFragment;
import com.example.movie.ui.home.HomeFragment;
import com.example.movie.ui.hotmovie.HotMovieFragment;
import com.example.movie.ui.item_category.ItemCategoryFragment;
import com.example.movie.ui.user.FragmentHitstory;
import com.example.movie.ui.user.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity implements IOnClickItemCategories {
    private static final String TAG = "MainActivity";
    ProgressBar bar;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        getFragment(HomeFragment.newInstance());
        actionBar=getSupportActionBar();
        bar = findViewById(R.id.prBar);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home: {
                        bar.setVisibility(View.VISIBLE);
                        actionBar.setTitle("Home");
                        actionBar.show();
                        getFragment(HomeFragment.newInstance());
                        break;
                    }
                    case R.id.nav_categories: {
                        bar.setVisibility(View.VISIBLE);
                        actionBar.setTitle("Category");
                        actionBar.show();
                        getFragment(CategoriesFragment.newInstance());
                        break;
                    }
                    case R.id.nav_hot_movie: {
                        bar.setVisibility(View.VISIBLE);
                        actionBar.setTitle("Hot Video");
                        actionBar.show();
                        getFragment(HotMovieFragment.newInstance());
                        break;
                    }
                    case R.id.nav_user: {
                        actionBar.hide();
                        bar.setVisibility(View.INVISIBLE);
                        getFragment(FragmentHitstory.newInstance());
                        break;
                    }
                }
                return false;
            }
        });

    }

    public void getFragment(Fragment fragment) {
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getFragment: " + e.getMessage());
        }
    }

    @Override
    public void getItemCategory(int a) {
        if (a == 1)
            actionBar.hide();
            getFragment(ItemCategoryFragment.newInstance());
    }

}

