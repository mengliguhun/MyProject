package com.example.administrator.myproject.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.myproject.R;
import com.example.administrator.myproject.adapter.RecyclerViewAdapter;
import com.example.administrator.myproject.bean.FunnyListResult;
import com.example.administrator.myproject.httputils.HttpUtils;
import com.example.administrator.myproject.view.DividerItemDecoration;
import com.example.administrator.myproject.view.HaloToast;
import com.example.administrator.myproject.view.RFRecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * create an instance of this fragment.
 */
public class FragmentItem extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentItem() {
        // Required empty public constructor
    }

    /**
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentItem.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentItem newInstance(String param1, String param2) {
        FragmentItem fragment = new FragmentItem();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private boolean fragmentVisible;
    private int pageSize=30, pageNo=1;
    private int rqcount=0;
    private int currentPos=-1;
    private boolean isLoadingMore;
    private boolean isLoadingMoreAll = false;
    private RFRecyclerView recyclerView;
    private DividerItemDecoration dividerItemDecoration;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private List<FunnyListResult.ItemsEntity> list = new ArrayList<>();
    private RecyclerViewAdapter recyclerViewAdapter;
    private Callback<FunnyListResult> callback = new Callback<FunnyListResult>() {
        @Override
        public void onResponse(Response<FunnyListResult> response, Retrofit retrofit) {
            swipeRefreshLayout.setRefreshing(false);

            if (response.code() == 200) {

                if (response.body() !=null && response.body().getItems() !=null){
                    if (!isLoadingMore){
                        if (response.body().getItems().size()>0){
                            list.clear();
                            list.addAll(response.body().getItems());
                        }
                    }
                    else{
                        if (response.body().getItems().size()<30){
                            HaloToast.show(getActivity(), "加载完成");
                            isLoadingMoreAll = true;
                        }
                        list.addAll(response.body().getItems());
                    }
                }

                recyclerView.notifyDataSetChanged();


                if (isLoadingMore){
                    isLoadingMore = false;
                }
            } else {
                HaloToast.show(getActivity(), "服务器错误");
            }
        }


        @Override
        public void onFailure(Throwable t) {
            isLoadingMore = false;
            swipeRefreshLayout.setRefreshing(false);
            HaloToast.show(getActivity(), t.getMessage());
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
//        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_blank, null);
//        } else {
//
//            ViewGroup parent = (ViewGroup) rootView.getParent();
//            if (parent != null) {
//                parent.removeView(rootView);
//            }
//
//        }

        return rootView;
    }

    protected void initViews() {
        recyclerView = (RFRecyclerView) rootView.findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeLayout);
    }

    protected void init() {
        super.init();
        if (dividerItemDecoration == null){
            dividerItemDecoration =new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, false);
        }

        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), list);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.activity_custom_views,null));
        recyclerView.setHasFixedSize(true);
        recyclerView.removeItemDecoration(dividerItemDecoration);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(null);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                if (lastVisibleItem >= totalItemCount - 4 && dy>0 && !isLoadingMoreAll && !isLoadingMore){
                    isLoadingMore = true;
                    onLoadMore();
                }
                //视频
                if (mParam1.equals("2")){

                    int firstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    if (firstVisibleItem >=0 ){
                        if (currentPos != firstVisibleItem){
                            currentPos = firstVisibleItem;
                            updateVideoItem(currentPos,fragmentVisible);
                        }
                    }
                    Log.e("firstVisibleItem",firstVisibleItem+"");
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //视频
                if (mParam1.equals("2")){

                    int firstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    Log.e("firstVisibleItem",firstVisibleItem+" onScrollStateChanged");
                    if (firstVisibleItem >=0 && currentPos != firstVisibleItem && currentPos >=0){
                        if (newState == RecyclerView.SCROLL_STATE_IDLE){
                            currentPos = firstVisibleItem;
                            updateVideoItem(currentPos,fragmentVisible);
                        }
                        else {
                            if (list.get(currentPos).isVisible()){
                                updateVideoItem(currentPos,false);
                            }

                        }

                    }


                }
            }
        });
        if (list.size()==0){

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
            onRefresh();
        }

    }

    @Override
    protected void bindViews() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private void updateVideoItem(int position,boolean isVisible){
        if (list.size()>0){
            list.get(position).setIsVisible(isVisible);
            recyclerViewAdapter.notifyItemChanged(position);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        fragmentVisible = isVisibleToUser;
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
    public void onLoadMore(){
        pageNo++;
        if (mParam1.equals("0")){
            HttpUtils.getFunnyTextListResult(callback,pageNo+"",pageSize+"","",rqcount+"");
        }
        else if(mParam1.equals("1")){
            HttpUtils.getFunnyImgListResult(callback, pageNo + "", pageSize + "", "", rqcount + "");
        }
        else if(mParam1.equals("2")){
            HttpUtils.getFunnyVideoListResult(callback, pageNo + "", pageSize + "", "", rqcount + "");
        }
    }
    @Override
    public void onRefresh() {
        rqcount++;
        pageNo=1;
        currentPos = -1;
        isLoadingMoreAll = false;
        if (mParam1.equals("0")){
            HttpUtils.getFunnyTextListResult(callback,pageNo+"",pageSize+"","",rqcount+"");
        }
        else if(mParam1.equals("1")){
            HttpUtils.getFunnyImgListResult(callback, pageNo + "", pageSize + "", "", rqcount + "");
        }
        else if(mParam1.equals("2")){
            HttpUtils.getFunnyVideoListResult(callback, pageNo + "", pageSize + "", "", rqcount + "");
        }
        else {
            HttpUtils.getFunnyTextListResult(callback,pageNo+"",pageSize+"","",rqcount+"");
        }
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

