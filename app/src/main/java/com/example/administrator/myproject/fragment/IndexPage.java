package com.example.administrator.myproject.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.myproject.R;
import com.example.administrator.myproject.adapter.MyFragmentViewPagerAdapter;
import com.example.administrator.myproject.view.PagerSlidingTabStripTitleColorChange;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentItem.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndexPage extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public IndexPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentItem.
     */
    // TODO: Rename and change types and number of parameters
    public static IndexPage newInstance(String param1, String param2) {
        IndexPage fragment = new IndexPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private PagerSlidingTabStripTitleColorChange tabs;
    private ViewPager viewPager;
    private MyFragmentViewPagerAdapter adapter;
    private List<String> titles = new ArrayList<>();

    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_blank, null);


        } else {

            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }

        }

        return rootView;
    }

    protected void initViews() {
        tabs = (PagerSlidingTabStripTitleColorChange) rootView.findViewById(R.id.tabs);
        tabs.setIndicatorHeight(4);
        tabs.setIndicatorColor(getResources().getColor(R.color.green_light));
        tabs.setTextColor(getResources().getColor(R.color.text_color_gray));
        tabs.setTabCurrentTextColor(getResources().getColor(R.color.green_light));

        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
    }

    protected void init() {
        super.init();
        titles.add("搞文");
        titles.add("搞图");
        titles.add("视频");

        adapter = new MyFragmentViewPagerAdapter(getChildFragmentManager(),titles);
        viewPager.setAdapter(adapter);
        tabs.setViewPager(viewPager);
        
    }

    @Override
    protected void bindViews() {
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

