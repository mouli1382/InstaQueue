package in.mobifirst.tagtree.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import in.mobifirst.tagtree.R;

public class BaseDrawerActivity extends BaseActivity {

    protected View mainContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_base_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainContentView = findViewById(R.id.content_base_drawer);
    }

}
