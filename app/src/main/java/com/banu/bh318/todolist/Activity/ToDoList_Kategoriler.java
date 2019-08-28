package com.banu.bh318.todolist.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;


import com.banu.bh318.todolist.Adapter.ToDoList_KategorilerAdapter;
import com.banu.bh318.todolist.Helper.RecyclerItemTouchHelper;
import com.banu.bh318.todolist.Helper.RecyclerItemTouchHelperListener;
import com.banu.bh318.todolist.Modal.ToDoList_Gonderiler;
import com.banu.bh318.todolist.Modal.User;
import com.banu.bh318.todolist.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.wangjie.rapidfloatingactionbutton.util.RFABShape;
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * Created by bh318 on 23.08.2019.
 */

public class ToDoList_Kategoriler extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener, RecyclerItemTouchHelperListener, SearchView.OnQueryTextListener {


    private List<RFACLabelItem> items;
    private RapidFloatingActionHelper rfabHelper;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private EditText baslik;
    public TextView txtDate, empty;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private String userId;
    private RecyclerView lv;
    public CoordinatorLayout rootLayout;
    public ArrayList<ToDoList_Gonderiler> gorevler = new ArrayList<ToDoList_Gonderiler>();
    static ArrayList<User> listkullanici = new ArrayList<>();
    public ToDoList_KategorilerAdapter gorevlerAdapter;
    public android.support.v7.widget.Toolbar toolbar;
    public ToDoList_Warning uyarilar;
    public List<ToDoList_Gonderiler> kontrolList = new ArrayList<ToDoList_Gonderiler>();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list_kategoriler);

        uyarilar = new ToDoList_Warning(this);
        items = new ArrayList<>();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();

        dialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom).build();

        rfaLayout = findViewById(R.id.label_list_sample_rfal);
        rfaButton = findViewById(R.id.label_list_sample_rfab);
        rootLayout = findViewById(R.id.coordinator_layout);
        empty = findViewById(R.id.bilgiAnasayfa);

        toolbar = findViewById(R.id.toolbar_id_mainpage);
        setSupportActionBar(toolbar);

        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(ToDoList_Kategoriler.this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Kategori Ekle")
                .setResId(R.drawable.task)
                .setIconNormalColor(0xffffffff)
                .setIconPressedColor(0xffffffff)
                .setLabelColor(Color.WHITE)
                .setLabelSizeSp(15)
                .setLabelBackgroundDrawable(RFABShape.generateCornerShapeDrawable(0xaaacacac, RFABTextUtil.dip2px(this, 4)))
                .setWrapper(1)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(RFABTextUtil.dip2px(this, 5))
                .setIconShadowColor(0xffacacac)
                .setIconShadowDy(RFABTextUtil.dip2px(this, 5))
        ;
        rfabHelper = new RapidFloatingActionHelper(
                this,
                rfaLayout,
                rfaButton,
                rfaContent
        ).build();

        kategoriGetir();

        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(lv);
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        positionState(position);
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        positionState(position);
        rfabHelper.toggleContent();
    }

    public void positionState(int position) {
        if (position == 0) {
            gorevEkle();
        }

    }

    public void gorevEkle() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_to_do_list_kategori_add_popup);
        dialog.setTitle("Görev Bilgisi Ekle");
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        baslik = dialog.findViewById(R.id.taskNamme);

        Button button = dialog.findViewById(R.id.gorevekle);
        txtDate = (TextView) dialog.findViewById(R.id.dateName);
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                database.getReference().child("kullanicilar").child(user)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataUser) {
                                if (baslik.getText().toString().equals("") && txtDate.getText().toString().equals("")) {
                                    uyarilar.warningNull("Lütfen boş alan bırakmayınız!!");
                                    dialog.dismiss();
                                } else {
                                    veritabaniGorevEkle();
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }

                        });
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "." + (month + 1) + "." + year;
        txtDate.setText(date);

    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    public void veritabaniGorevEkle() {
        mRef = database.getReference().child("kategoriler").push();
        final String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final HashMap<String, Object> gorev = new HashMap<>();
        gorev.put("ad", baslik.getText().toString());
        gorev.put("date", txtDate.getText().toString());
        gorev.put("userId", userId);
        gorev.put("durum", "0");

        database.getReference().child("kullanicilar").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        database.getReference().child("kategoriler").push().setValue(gorev)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        final DatabaseReference dbRef = database.getReference("kategoriler");
                                        gorevler = new ArrayList<>();
                                        lv = findViewById(R.id.gorevlerList);
                                        database.getReference().child("kullanicilar")
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        lv.setVisibility(View.VISIBLE);
                                                        empty.setVisibility(View.GONE);
                                                        if (listkullanici != null) {
                                                            dbRef.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                        if (user.equals(ds.child("userId").getValue().toString())) {
                                                                            final String gorev = ds.child("ad").getValue().toString();
                                                                            String tarih = ds.child("date").getValue().toString();
                                                                            final String gorevId = ds.getKey();
                                                                            gorevler.add(new ToDoList_Gonderiler(gorevId, gorev, tarih));
                                                                        }
                                                                    }
                                                                    if (!gorevler.isEmpty()) {
                                                                        gorevlerAdapter = new ToDoList_KategorilerAdapter(ToDoList_Kategoriler.this, gorevler);
                                                                        lv.setLayoutManager(new GridLayoutManager(ToDoList_Kategoriler.this, 1));
                                                                        lv.setAdapter(gorevlerAdapter);
                                                                        dbRef.removeEventListener(this);
                                                                        gorevlerAdapter.notifyDataSetChanged();
                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        throw databaseError.toException();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public void kategoriGetir() {
        final DatabaseReference dbRef = database.getReference("kategoriler");
        gorevler = new ArrayList<>();
        lv = findViewById(R.id.gorevlerList);

        final String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dialog.show();
        database.getReference().child("kullanicilar")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (listkullanici != null) {
                            dbRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (user.equals(ds.child("userId").getValue().toString())) {

                                            final String gorev = ds.child("ad").getValue().toString();
                                            final String tarih = ds.child("date").getValue().toString();
                                            String gorevId = ds.getKey();
                                            gorevler.add(new ToDoList_Gonderiler(gorevId, gorev, tarih));
                                        }
                                    }
                                    if (!gorevler.isEmpty()) {
                                        gorevlerAdapter = new ToDoList_KategorilerAdapter(ToDoList_Kategoriler.this, gorevler);
                                        lv.setLayoutManager(new GridLayoutManager(ToDoList_Kategoriler.this, 1));
                                        lv.setAdapter(gorevlerAdapter);
                                        dbRef.removeEventListener(this);
                                        gorevlerAdapter.notifyDataSetChanged();
                                    } else {
                                        lv.setVisibility(View.GONE);
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                    dialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }


    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if (viewHolder instanceof ToDoList_KategorilerAdapter.MyViewHolder) {
            String name = gorevler.get(viewHolder.getAdapterPosition()).getBaslik();
            ToDoList_Gonderiler deletedItem = gorevler.get(viewHolder.getAdapterPosition());
            int deleteIntex = viewHolder.getAdapterPosition();
            gorevlerAdapter.removeItem(deleteIntex, gorevler.get(position));
            if (gorevler.isEmpty()) {
                lv.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_main_page, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        database = FirebaseDatabase.getInstance();
        lv = findViewById(R.id.gorevlerList);
        final DatabaseReference dbRef = database.getReference("kategoriler");
        final ArrayList<ToDoList_Gonderiler> newList = new ArrayList<>();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userInput = newText.toLowerCase();

                if (!userInput.isEmpty()) {
                    for (int i = 0; i < gorevler.size(); i++) {
                        if (gorevler.get(i).getBaslik().toLowerCase().contains(userInput.toLowerCase()) || gorevler.get(i).getDate().toLowerCase().contains(userInput.toLowerCase())) {
                            newList.add(new ToDoList_Gonderiler(gorevler.get(i).getGorevId(), gorevler.get(i).getBaslik(), gorevler.get(i).getDate()));
                        }
                    }
                } else {
                    gorevlerAdapter = new ToDoList_KategorilerAdapter(ToDoList_Kategoriler.this, gorevler);
                    lv.setLayoutManager(new GridLayoutManager(ToDoList_Kategoriler.this, 1));
                    lv.setAdapter(gorevlerAdapter);
                    dbRef.removeEventListener(this);
                    gorevlerAdapter.notifyDataSetChanged();

                }

                if (newList.size() != 0) {
                    gorevlerAdapter = new ToDoList_KategorilerAdapter(ToDoList_Kategoriler.this, newList);
                    lv.setLayoutManager(new GridLayoutManager(ToDoList_Kategoriler.this, 1));
                    lv.setAdapter(gorevlerAdapter);
                    dbRef.removeEventListener(this);
                    gorevlerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ToDoList_Kategoriler.this, ToDoList_Login.class));
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
