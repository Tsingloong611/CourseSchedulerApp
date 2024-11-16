package xyz.tsingloong.courseschedulerapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import xyz.tsingloong.courseschedulerapp.R;
import xyz.tsingloong.courseschedulerapp.models.LogEntry;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<LogEntry> logs;

    public LogAdapter(List<LogEntry> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogEntry log = logs.get(position);
        holder.logDate.setText("日期: " + log.getTimestamp());
        holder.logInput.setText("输入: " + log.getInputData().toString());
        holder.logResult.setText("结果: " + log.getResultText());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public void updateLogs(List<LogEntry> newLogs) {
        logs = newLogs;
        notifyDataSetChanged();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView logDate, logInput, logResult;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            logDate = itemView.findViewById(R.id.logDate);
            logInput = itemView.findViewById(R.id.logInput);
            logResult = itemView.findViewById(R.id.logResult);
        }
    }
}
