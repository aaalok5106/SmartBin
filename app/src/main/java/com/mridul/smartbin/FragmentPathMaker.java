package com.mridul.smartbin;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static com.mridul.smartbin.BackgroundWorkerLoginActivity.START_POSITION_SELECTED;


public class FragmentPathMaker extends Fragment {

    Button btn_open_pathmaker;
    Button btn_flush_filled_bins;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_path_maker, container, false);
        AfterLogin1.toolbar.setTitle("Route Maker");

        btn_open_pathmaker = (Button)view.findViewById(R.id.btn_open_makepath_window);
        btn_flush_filled_bins = (Button)view.findViewById(R.id.btn_flush_bin_data);

        btn_open_pathmaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(START_POSITION_SELECTED.equals("YES")){
                    String type = "forMakingPath";
                    BackgroundWorkerPathMaker bgwork = new BackgroundWorkerPathMaker(getContext());
                    bgwork.execute(type);
                }
                else if(START_POSITION_SELECTED.equals("NO")){

                    android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(getContext());
                    alert.setTitle("Important Notice!!");
                    alert.setMessage("Please, First choose Start Position of the optimized path to be made .");
                    alert.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent10 = new Intent(getContext(), SelectStartPosition.class);
                            startActivity(intent10);
                        }
                    });
                    alert.show();
                }
            }
        });


        btn_flush_filled_bins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(getContext());
                alert.setTitle("Important Notice!!");
                alert.setMessage("You have to FLUSH filled bins data only if Driver do not have GarbageCollect App .\nIf Driver has GarbageCollect App you need not to worry .");
                alert.setPositiveButton("Flush", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Toast.makeText(getContext(),"Do Flushing Here",Toast.LENGTH_LONG).show();
                        // Do flush Job here...
                        String type = "flushFilledBins";
                        BackgroundWorkerFlushBinData bgw = new BackgroundWorkerFlushBinData(getContext());
                        bgw.execute(type);
                    }
                });
                alert.setNegativeButton("NO", null);
                alert.show();
            }
        });

        return  view;
    }




}
