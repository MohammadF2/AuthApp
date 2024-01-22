package com.bzu.auth.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.auth.R;
import com.bzu.auth.model.AuthInfo;



import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CardAdapter extends RecyclerView.Adapter<CardView> {

    Context context;
    List<AuthInfo> data;
    private CountDownTimer countDownTimer;
    private Map<Integer, Long> updateTimes; // Store last update time for each item

    int x = 1;
    public Handler handler = new Handler();
    private Runnable updateRunnable;

    public CardAdapter(Context context, List<AuthInfo> data) {
        this.context = context;
        this.data = data;
        this.updateTimes = new HashMap<>();

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                // Update the data and notify the adapter of the changes
                for (int i = 0; i < data.size(); i++) {
                    try {
                        updateData(i);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                        throw new RuntimeException(e);
                    }
                }
                notifyDataSetChanged();
                // Schedule the next update after 30 seconds
                handler.postDelayed(this, 300); // 30 seconds in milliseconds
            }
        };

        // Start the initial update
        handler.post(updateRunnable);

    }

    private void updateData(int position) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeyException {

        String hexTime = OTP.timeInHex(System.currentTimeMillis() + 60000, 30);
        String newValue = OTP.create(data.get(position).getSecretKey(), hexTime, 6, Type.TOTP);


        String hexTime2 = OTP.timeInHex(System.currentTimeMillis() + 90000, 30);
        String newValue2 = OTP.create(data.get(position).getSecretKey(), hexTime2, 6, Type.TOTP);

        data.get(position).setNextCode(newValue2);
        data.get(position).setCode(newValue);
        updateTimes.put(position, System.currentTimeMillis());
    }

    @NonNull
    @Override
    public CardView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardView(LayoutInflater.from(context).inflate(R.layout.card_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CardView holder, @SuppressLint("RecyclerView") int position) {
        // Bind data to the ViewHolder

        long totalTimeInMillis = 30000;

        holder.appName.setText(data.get(position).getAppName());
        holder.code.setText(String.valueOf(data.get(position).getCode()));
        holder.nextCode.setText(String.valueOf(data.get(position).getNextCode()));
        holder.curr_timer.setText("Reset in: " + String.valueOf(totalTimeInMillis / 1000));
        holder.next_timer.setText("Reset in: " + String.valueOf(totalTimeInMillis / 1000));

         countDownTimer = new CountDownTimer(totalTimeInMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                holder.curr_timer.setText("Reset in: " + String.valueOf(totalTimeInMillis / 1000));
                holder.next_timer.setText("Reset in: " + String.valueOf(totalTimeInMillis / 1000));
            }

            @Override
            public void onFinish() {
                resetTimer();
            }
        };
    }

    private int getTOTP(String secret) {
        String url = "";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//            }
//        })

        return 0;
    }

    private String formatTime(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return sdf.format(new Date(milliseconds));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void resetTimer() {
        // Reset the timer to 30 seconds and start again
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
