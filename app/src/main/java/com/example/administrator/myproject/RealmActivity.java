package com.example.administrator.myproject;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.administrator.myproject.adapter.RealmRecyclerViewAdapter;
import com.example.administrator.myproject.realm.model.Dog;
import com.example.administrator.myproject.realm.model.Person;
import com.example.administrator.myproject.view.HaloToast;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by jack on 17-1-3.
 */

public class RealmActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button select,insert,udapte,delete;
    private RecyclerView recyclerView;
    private RealmRecyclerViewAdapter adapter;
    private String[] pics = new String[]{
            "http://img1.imgtn.bdimg.com/it/u=2820211196,3263953083&fm=21&gp=0.jpg",
            "http://img5.duitang.com/uploads/item/201411/30/20141130190623_UK8jf.thumb.700_0.jpeg",
            "http://pic24.nipic.com/20121025/9448607_212625649000_2.jpg",
            "http://img5.imgtn.bdimg.com/it/u=3039273364,4104006443&fm=21&gp=0.jpg",
            "http://www.photohn.com/bbs/data/attachment/forum/month_1108/1108141045e156f2784e2feb04.jpg",
            "http://p1.pstatp.com/large/1649/6019728643",
            "http://s4.sinaimg.cn/mw690/003NWKKyzy6O27E2bKz83&690",
            "http://img2.imgtn.bdimg.com/it/u=1654431408,1314943580&fm=21&gp=0.jpg"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm);

        initViews();
        bindViews();
        initData();
    }

    @Override
    public void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("RealDB");
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        select = (Button) findViewById(R.id.select);
        insert = (Button) findViewById(R.id.insert);
        udapte = (Button) findViewById(R.id.update);
        delete = (Button) findViewById(R.id.del);

    }

    @Override
    public void bindViews() {
        select.setOnClickListener(this);
        insert.setOnClickListener(this);
        udapte.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    @Override
    public void initData() {
        final RealmResults<Dog> puppies = Realm.getDefaultInstance().where(Dog.class).findAll();
        puppies.addChangeListener(new RealmChangeListener<RealmResults<Dog>>() {
            @Override
            public void onChange(RealmResults<Dog> element) {
                adapter.notifyDataSetChanged();
                HaloToast.show(RealmActivity.this,"发生更新了");
            }
        });
        adapter = new RealmRecyclerViewAdapter(this,puppies);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.select:

                break;
            case R.id.insert:
                insert();
                break;
            case R.id.update:

                break;
            case R.id.del:

                break;
        }
    }

    public void insert(){
        Realm realm = Realm.getDefaultInstance();
        //异步/同步 executeTransactionAsync/executeTransaction
        realm.executeTransactionAsync(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                            for (int i=0;i<3;i++){
                                                Person person = realm.createObject(Person.class,UUID.randomUUID().toString().replaceAll("-",""));
                                                person.setName("Person"+i);

                                                for (int j=0;j<3;j++){
                                                    Dog dog = realm.createObject(Dog.class);
                                                    dog.setName("Dog_"+(j+i*3));
                                                    dog.setPic(pics[(j+i*3)%pics.length]);
                                                    person.getDogs().add(dog);
                                                }
                                            }
                                          }
                                      }, new Realm.Transaction.OnSuccess() {
                                          @Override
                                          public void onSuccess() {
                                              // Transaction was a success.
                                          }
                                      }, new Realm.Transaction.OnError() {
                                          @Override
                                          public void onError(Throwable error) {
                                              // Transaction failed and was automatically canceled.
                                          }
                                      }
        );
    }
    public void update(){

    }
    public void delete(){

    }
}
