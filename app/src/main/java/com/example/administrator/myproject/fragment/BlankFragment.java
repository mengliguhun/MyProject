package com.example.administrator.myproject.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.administrator.myproject.R;
import com.example.administrator.myproject.adapter.ListViewAdapter;
import com.example.administrator.myproject.adapter.RecyclerViewAdapter;
import com.example.administrator.myproject.httputils.HttpUtils;
import com.example.administrator.myproject.httputils.HttpUtils.Contributor;
import com.example.administrator.myproject.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private ListView listView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private List<Contributor> list = new ArrayList<>();
    private ListViewAdapter adapter;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Callback<List<Contributor>> callback = new Callback<List<Contributor>>() {
        @Override
        public void onResponse(Response<List<Contributor>> response, Retrofit retrofit) {
            list.clear();
            list.addAll(response.body());
            adapter.notifyDataSetChanged();
            recyclerViewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(Throwable t) {

        }
    };
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
        rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        initViews();
        initData();
        return rootView;
    }
    private void initViews(){
        listView = (ListView) rootView.findViewById(R.id.listView);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeLayout);
    }
    private void initData(){
        adapter = new ListViewAdapter(getActivity(),list);
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(),list);
        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST,false));
        recyclerView.setAdapter(recyclerViewAdapter);

//        listView.setAdapter(adapter);

        if (mParam2.equals("title1")){
            HttpUtils.getContributors(callback);
        }
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
