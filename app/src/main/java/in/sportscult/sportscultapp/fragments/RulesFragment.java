package in.sportscult.sportscultapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.sportscult.sportscultapp.R;

/**
 * Created by Vikas on 25/03/2016.
 * Displays the Rules of the Tournament
 */
public class RulesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rule_layout,null);
    }
}
