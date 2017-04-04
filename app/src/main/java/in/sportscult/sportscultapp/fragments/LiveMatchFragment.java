package in.sportscult.sportscultapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.sportscult.sportscultapp.DetailedMatchDescription;
import in.sportscult.sportscultapp.R;
import in.sportscult.sportscultapp.RecyclerItemClickListener;

public class LiveMatchFragment extends Fragment {

    private RecyclerView Live_Matches_List;
    private ArrayList<LiveMatch> liveMatchArrayList;
    private Map<String,String> team_profile_pic_download_urls;
    private LiveMatchAdapter liveMatchAdapter;
    MapView mapView;
    private ProgressDialog progressDialog;
    private static TextView display_on_empty_live_match;
    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ValueEventListener liveMatchListener;

    public LiveMatchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_live_match, container, false);
        Live_Matches_List = (RecyclerView) view.findViewById(R.id.Live_Matches_List);
        team_profile_pic_download_urls = new HashMap<String, String>();
        liveMatchArrayList = new ArrayList<LiveMatch>();
        display_on_empty_live_match = (TextView)view.findViewById(R.id.display_on_empty_live_match);
      //   mapView = (MapView) view.findViewById(R.id.card_image) ;

        liveMatchAdapter = new LiveMatchAdapter(getActivity(),liveMatchArrayList,team_profile_pic_download_urls);
        Live_Matches_List.setAdapter(liveMatchAdapter);
        Live_Matches_List.setLayoutManager(new LinearLayoutManager(getActivity()));
        Live_Matches_List.setNestedScrollingEnabled(false);

        Fetch_Live_Matches_From_Firebase();

        Live_Matches_List.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), Live_Matches_List, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), DetailedMatchDescription.class);
                intent.putExtra("Activity Name","Live");
                intent.putExtra("Match ID",liveMatchArrayList.get(position).Key);
                intent.putExtra("Age Group",liveMatchArrayList.get(position).AgeGroup);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseReference.child("Live Matches").removeEventListener(liveMatchListener);
    }

    public void Fetch_Live_Matches_From_Firebase(){

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Data....");
        progressDialog.setCancelable(false);
        //progressDialog.show();

        databaseReference.keepSynced(true);
        liveMatchListener = databaseReference.child("Live Matches").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                liveMatchArrayList = new ArrayList<LiveMatch>();
                if(dataSnapshot.getValue()==null){
                    progressDialog.dismiss();
                    ArrayListEmpty();
                    return;
                }
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    Map<String,String> map = (Map<String,String>)childSnapshot.getValue();
                    final LiveMatch liveMatch = new LiveMatch(childSnapshot.getKey(),map.get("Team A"),map.get("Team B"),map.get("Team A Goals"),
                            map.get("Team B Goals"),map.get("Venue"),map.get("Start Time"),map.get("Age Group"));
                    liveMatchArrayList.add(liveMatch);

                    databaseReference.child(liveMatch.AgeGroup).child("Team Names").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot ChildSnapshot : dataSnapshot.getChildren()) {
                                Map<String, String> urlmap = (Map<String, String>) ChildSnapshot.getValue();
                                team_profile_pic_download_urls.put(liveMatch.AgeGroup + ChildSnapshot.getKey(), urlmap.get("Team Profile Pic Thumbnail Url"));
                            }
                            progressDialog.dismiss();
                            if(liveMatchArrayList.size()==0)
                                ArrayListEmpty();
                            else{
                                liveMatchAdapter = new LiveMatchAdapter(getActivity(),liveMatchArrayList,team_profile_pic_download_urls);
                                Live_Matches_List.scrollToPosition(2);
                                Live_Matches_List.setAdapter(liveMatchAdapter);
                                ArrayListNotEmpty();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Some Error Occurred",Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Some Error Occurred",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void ArrayListEmpty(){
        Live_Matches_List.setVisibility(View.GONE);
        display_on_empty_live_match.setVisibility(View.VISIBLE);
    }
    private void ArrayListNotEmpty(){
        Live_Matches_List.setVisibility(View.VISIBLE);
        display_on_empty_live_match.setVisibility(View.GONE);
    }
}

class LiveMatch{
    String TeamA,TeamB,TeamAGoals,TeamBGoals,Venue,StartTime,AgeGroup,Key;
    LiveMatch(String Key,String TeamA,String TeamB,String TeamAGoals,String TeamBGoals,String Venue,String StartTime,String AgeGroup){
        this.TeamA = TeamA;
        this.TeamB = TeamB;
        this.TeamAGoals = TeamAGoals;
        this.TeamBGoals = TeamBGoals;
        this.Venue = Venue;
        this.StartTime = StartTime;
        this.AgeGroup = AgeGroup;
        this.Key = Key;
    }
}

class LiveMatchAdapter extends RecyclerView.Adapter<LiveMatchAdapter.Viewholder3>{

    ArrayList<LiveMatch> arrayListForLiveMatch;
    Map<String,String> urlMap;
    LayoutInflater layoutInflater;
    Context context;

    LiveMatchAdapter(Context context,ArrayList<LiveMatch> arrayListForLiveMatch,Map<String,String> urlMap){
        this.arrayListForLiveMatch = arrayListForLiveMatch;
        layoutInflater = LayoutInflater.from(context);
        this.urlMap = urlMap;
        this.context = context;
    }

    @Override
    public Viewholder3 onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.live_match_card,parent,false);
        Viewholder3 viewholder3 = new Viewholder3(view);
        return viewholder3;
    }

    @Override
    public void onBindViewHolder(Viewholder3 viewHolder, int position) {
        LiveMatch data = arrayListForLiveMatch.get(position);
        viewHolder.teamA_name.setText(data.TeamA);
        viewHolder.teamB_name.setText(data.TeamB);
        viewHolder.live_match_score_A.setText(data.TeamAGoals);
        viewHolder.live_match_score_B.setText(data.TeamBGoals);

        //Set Text Color For Scores
        int AColor,BColor;
        if(Integer.parseInt(data.TeamAGoals)>Integer.parseInt(data.TeamBGoals)){
            AColor = context.getResources().getColor(R.color.winning_color);
            BColor = context.getResources().getColor(R.color.loosing_color);
        }
        else if(Integer.parseInt(data.TeamAGoals)<Integer.parseInt(data.TeamBGoals)){
            BColor = context.getResources().getColor(R.color.winning_color);
            AColor = context.getResources().getColor(R.color.loosing_color);
        }
        else{
            BColor = context.getResources().getColor(R.color.draw_color);
            AColor = context.getResources().getColor(R.color.draw_color);
        }
        viewHolder.live_match_score_A.setTextColor(AColor);
        viewHolder.live_match_score_B.setTextColor(BColor);

        viewHolder.live_match_start_time.setText("Start Time : " + data.StartTime);
        viewHolder.live_match_venue.setText("Venue : " + data.Venue);
        switch (data.AgeGroup){
            case "Group - A":
                viewHolder.group.setText("Group - A");
             //   viewHolder.live_match_detail_card.setBackgroundColor(Color.parseColor("#EA80FC"));
                break;
            case "Group - B":
                viewHolder.group.setText("Group - B");
              //  viewHolder.live_match_detail_card.setBackgroundColor(Color.parseColor("#E040FB"));
                break;
            case "Group - C":
                viewHolder.group.setText("Group - C");
            //    viewHolder.live_match_detail_card.setBackgroundColor(Color.parseColor("#D500F9"));
                break;
            case "Group - D":
                viewHolder.group.setText("Group - D");
             //   viewHolder.live_match_detail_card.setBackgroundColor(Color.parseColor("#AA00FF"));
                break;
            default:
                viewHolder.group.setText("Unspecified Group");
             //   viewHolder.live_match_detail_card.setBackgroundColor(Color.WHITE);
                break;
        }

        final ImageView tempImageViewA = viewHolder.teamA_image;
        final ImageView tempImageViewB = viewHolder.teamB_image;
        final String urlA = urlMap.get(data.AgeGroup + data.TeamA);
        final String urlB = urlMap.get(data.AgeGroup + data.TeamB);
        //Load profile pic thumbnails
        Picasso.with(context)
                .load(urlA)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(tempImageViewA, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(context)
                                .load(urlA)
                                //.error(R.drawable.common_full_open_on_phone)
                                .into(tempImageViewA, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                    }
                                });
                    }
                });
        Picasso.with(context)
                .load(urlB)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(tempImageViewB, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(context)
                                .load(urlB)
                                //.error(R.drawable.common_full_open_on_phone)
                                .into(tempImageViewB, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                    }
                                });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return arrayListForLiveMatch.size();
    }

    class Viewholder3 extends RecyclerView.ViewHolder{
        LinearLayout live_match_detail_card;
        TextView teamA_name,teamB_name,live_match_score_A,live_match_score_B,live_match_start_time,live_match_venue,group;
        ImageView teamA_image,teamB_image;
        Viewholder3(View view){
            super(view);
            live_match_detail_card = (LinearLayout)view.findViewById(R.id.live_match_detail_card);
            teamA_name = (TextView)view.findViewById(R.id.teamA_name);
            teamB_name = (TextView)view.findViewById(R.id.teamB_name);
            live_match_score_A = (TextView)view.findViewById(R.id.scoreA);
            live_match_score_B = (TextView)view.findViewById(R.id.scoreB);
            live_match_start_time = (TextView)view.findViewById(R.id.live_match_start_time);
            live_match_venue = (TextView)view.findViewById(R.id.live_match_venue);
            teamA_image = (ImageView)view.findViewById(R.id.teamA_image);
            teamB_image = (ImageView)view.findViewById(R.id.teamB_image);
            group = (TextView)view.findViewById(R.id.group);
        }
    }


}