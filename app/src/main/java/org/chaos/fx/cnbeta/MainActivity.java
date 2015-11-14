package org.chaos.fx.cnbeta;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.chaos.fx.cnbeta.net.CnBetaApi;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    private CnBetaApi mCnBetaApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCnBetaApi = new Retrofit.Builder()
                .baseUrl(CnBetaApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CnBetaApi.class);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ArticlesFragment()).commit();
    }

    public CnBetaApi getCnBetaApi() {
        return mCnBetaApi;
    }
}
