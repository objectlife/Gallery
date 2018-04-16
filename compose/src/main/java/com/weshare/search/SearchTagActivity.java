package com.weshare.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.weshare.compose.R;
import com.weshare.domain.CateTag;
import com.weshare.domain.Category;

/**
 * Created by mrsimple on 16/4/2018.
 */

public class SearchTagActivity extends AppCompatActivity {

    EditText mSearchEdit ;
    RecyclerView mRecyclerView ;
    SearchTagAdapter mAdapter ;
    ViewStub mNoMatchViewStub ;
    private String mKeyword ;
    View mNoMatchLayout;
    ProgressBar mProgressBar ;

    public static void start(Context context) {
        Intent starter = new Intent(context, SearchTagActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_tag_layout);

        findViewById(R.id.search_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchEdit = findViewById(R.id.search_edit) ;
        mSearchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(mSearchEdit.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        mRecyclerView = findViewById(R.id.search_tag_recyclerview) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchTagAdapter() ;
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // todo : post event

                Toast.makeText(SearchTagActivity.this, "select : " + mAdapter.getItem(position), Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        for (int i = 0; i < 5; i++) {
            Category category = new Category() ;
            category.name = "Category - " + i;
            CateTag tag = new CateTag("tag - " + i, "tag - name - " + i) ;
            category.tagsList.add(tag) ;
            mAdapter.addCategory(category);
        }


        findViewById(R.id.search_clear_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEdit.setText("");
            }
        });

        mNoMatchViewStub = findViewById(R.id.search_no_match_viewstub) ;
        mProgressBar = findViewById(R.id.search_progress_bar) ;
    }


    private void performSearch(String keyword) {
        mKeyword = keyword ;
        Toast.makeText(this, "search " + keyword, Toast.LENGTH_SHORT).show();

        if ( mNoMatchLayout != null ) {
            mNoMatchLayout.setVisibility(View.GONE);
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mSearchEdit.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNoMatchLayout();
            }
        }, 1500);
    }



    TextView mNoMatchTipTv ;
    TextView mKeywordTv ;

    private void showNoMatchLayout() {
        if ( mNoMatchLayout == null ) {
            mNoMatchLayout = mNoMatchViewStub.inflate() ;
            mNoMatchTipTv = mNoMatchLayout.findViewById(R.id.create_tag_tips_tv) ;
            mKeywordTv = mNoMatchLayout.findViewById(R.id.create_tag_name_tv) ;
        }

        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        mKeywordTv.setText(mKeyword);
        mNoMatchLayout.setVisibility(View.VISIBLE);
        mNoMatchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo : create tag event
                Toast.makeText(SearchTagActivity.this, "create tag : " + mKeyword, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
