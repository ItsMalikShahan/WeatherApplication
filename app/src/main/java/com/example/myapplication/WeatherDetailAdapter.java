package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherDetailAdapter extends RecyclerView.Adapter<WeatherDetailAdapter.WeatherViewHolder> {

    Context context;
    ArrayList<WeatherDetailModel> weatherModelList;

    public WeatherDetailAdapter(Context context, ArrayList<WeatherDetailModel> weatherModelList) {
        this.context = context;
        this.weatherModelList = weatherModelList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_detail_rv_items, parent, false);
        return new WeatherViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherDetailModel model = weatherModelList.get(position);
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.weatherCondition);
        holder.temperature.setText(model.getTemperature()+"°C");
        holder.windSpeed.setText(model.getWindSpeed()+"km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try
        {
            Date t = input.parse(model.getTime());
            holder.time.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherModelList.size();
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder{
        TextView time, temperature, windSpeed;
        ImageView weatherCondition;
        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.tv_time);
            temperature = itemView.findViewById(R.id.tv_tempValue);
            windSpeed = itemView.findViewById(R.id.tv_windSpeed);
            weatherCondition = itemView.findViewById(R.id.iv_tempIcon);
        }
    }
}
