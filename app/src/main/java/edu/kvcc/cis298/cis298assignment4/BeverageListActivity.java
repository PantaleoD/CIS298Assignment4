package edu.kvcc.cis298.cis298assignment4;

        import android.support.v4.app.Fragment;

/**
 * Created by David Barnes on 11/3/2015.
 */
public class BeverageListActivity extends SingleFragmentActivity {

    //Since this activity inherits from singleFragmentActivity, the only work we have to do
    //is override this createFragment method and return a new BeverageListFragment
    @Override
    protected Fragment createFragment() {
        return new BeverageListFragment();
    }
}
